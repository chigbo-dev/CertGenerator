/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gencert;


import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.mail.Address;
import jakarta.mail.Authenticator;
import jakarta.mail.BodyPart;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.apache.commons.text.WordUtils;

/**
 *
 * @author Zacch
 */
public class GenCert {

    public static void main(String[] args) throws BadElementException, IOException, MessagingException {
        
        //Fetch Name/Email from DB
        try (Connection conn = GetConnection.getSimpleConnection()) {
            final char[] delimiters = {' ', ','};
            String scitSql = "SELECT * FROM list";
            PreparedStatement scitStmt = conn.prepareStatement(scitSql);
            
            ResultSet scit_rs = scitStmt.executeQuery();
            while (scit_rs.next()){
                String track = scit_rs.getString("track");
                String name = WordUtils.capitalizeFully(scit_rs.getString("name"), delimiters);
                String to = scit_rs.getString("email");
                int id = scit_rs.getInt("id");
                //GetandSendCert(id,name,to);
                GetandSendCert(id,track,name,to);
            }

            System.out.println("All Done. Hurray!!!");
        } catch (SQLException ex) {
            Logger.getLogger(GenCert.class.getName()).log(Level.SEVERE, null, ex);
        }
            
        
    }
    
    @SuppressWarnings("CallToPrintStackTrace")
    public static void GetandSendCert(int id, String track, String name, String to) throws BadElementException, IOException{
        try{
            Document document = new Document(PageSize.A4.rotate(), 0f, 0f, 0f, 0f);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(name+".pdf"));
            document.open();
            Image image1 = Image.getInstance("bg_it.png");
            image1.setAbsolutePosition(0f, 0f);
            image1.scaleAbsolute(PageSize.A4.rotate());
            
            PdfContentByte cb = writer.getDirectContentUnder();
            document.add(getWatermarkedImage(cb, image1, track+"_"+name));
            
            document.addAuthor("Chigbo Samuel; CTO Omney Payment Ltd.");
            document.addCreationDate();
            document.addCreator("Chigbo Samuel");
            document.addTitle("Internship Diploma");
            document.addSubject("6 Months Intensive Training");
            document.close();
            writer.close();
            
            /*
            //Emailing functionality
            String from = "chigbo.sn@gmail.com";
            //String from = "chigbo@tvl.ng";
            //String host = "mail.tvl.ng";
            Properties props = System.getProperties();
            
            // Setup mail server
//            properties.setProperty("mail.smtp.host", host);
//            properties.setProperty("mail.smtp.port", "26");
//            properties.put("mail.smtp.auth", "true");
            
            props.put("mail.smtp.host", "smtp.gmail.com");    
            props.put("mail.smtp.socketFactory.port", "465");    
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");    
            props.put("mail.smtp.auth", "true");    
            props.put("mail.smtp.port", "465");   
            
            Session session = Session.getDefaultInstance(props,new Authenticator() {  
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {  
                    return new PasswordAuthentication(from,"Ilmm56;;");  
                }  
            }); 
            try {
                
                MimeMessage message = new MimeMessage(session);
                message.setFrom(new InternetAddress(from, "Chigbo Samuel"));
                message.setReplyTo(new Address[]
                {
                    new InternetAddress("ask@egobeke.ng")
                });
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                message.setSubject("Internship Certificate");
                
                // Create the message part 
                BodyPart messageBodyPart = new MimeBodyPart();
                messageBodyPart.setText("Hello "+name+",\n"
                        + "We're glad you were part of the Omney family for the past few months."
                        + "\n"
                        + "Please, find attached your certicate. Best of luck in all your future endeavours."
                        + "\n\n"
                        + "Regards,"
                        + "\nChigbo.");

                Multipart multipart = new MimeMultipart();
                multipart.addBodyPart(messageBodyPart);
                messageBodyPart = new MimeBodyPart();
                String filename = name+".pdf";
                DataSource source = new FileDataSource(filename);
                messageBodyPart.setDataHandler(new DataHandler(source));
                messageBodyPart.setFileName(filename);
                multipart.addBodyPart(messageBodyPart);

                message.setContent(multipart );

                Transport.send(message);
                
                
            } catch (MessagingException mex) {
               mex.printStackTrace();
            }
            */
            System.out.println(id+". Cert for "+name+" sent successfully.");
        }catch(DocumentException e){
            e.printStackTrace();
        }
    }
    
    public static Image getWatermarkedImage(PdfContentByte cb, Image img, String watermark) throws DocumentException {
        float width = img.getScaledWidth();
        float height = img.getScaledHeight();
        String []p = watermark.split("_");
        Font FONT = FontFactory.getFont(FontFactory.TIMES_ITALIC, 45, Font.BOLD);
        Font FONT_S = FontFactory.getFont(FontFactory.TIMES, 15, Font.BOLD);
        FONT.setColor(BaseColor.BLACK);
        FONT_S.setColor(BaseColor.BLACK);
        PdfTemplate template = cb.createTemplate(width, height);
        template.addImage(img, width, 0, 0, height, 0, 0);
        ColumnText.showTextAligned(template, Element.ALIGN_CENTER,
                new Phrase(p[1], FONT), width / 2, (float) (height / 2.1), 0);
        ColumnText.showTextAligned(template, Element.ALIGN_LEFT,
                new Phrase(p[0], FONT_S), width / 12, (float) (height - height / 1.145), 0);
        return Image.getInstance(template);
    }
    
}
