package com.ventas.havr.havrventas.Correo;

/**
 * Created by end user on 06/04/2018.
 */

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Security;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class HAVRsender extends javax.mail.Authenticator {
    private static final String TAG = "HAVR SENDER";
    private String mailhost = "smtp.gmail.com"; // HAVR
    private String user;
    private String password;
    private Session session;
    Multipart multipart = new MimeMultipart();

    static {
        Security.addProvider(new JSSEProvider());
    }

    public HAVRsender(String user, String password) {
        this.user = user;
        this.password = password;
        Properties props = new Properties();
        //props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.smtp.host", mailhost);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.ssl.enable", true);
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        //props.put("mail.smtp.socketFactory.fallback", "false");
        //props.setProperty("mail.smtp.quitwait", "false");
        session = Session.getInstance(props, this);
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(user, password);
    }

    public synchronized void HAVRSendMail(String subject, String body, String sender, String recipients) {
        try{
            MimeMessage message = new MimeMessage(session);
            DataHandler handler = new DataHandler(new ByteArrayDataSource(body.getBytes(), "text/plain"));
            message.setSender(new InternetAddress(sender));
            message.setHeader("HAVR","HAVR");
            message.setDescription("Cotizacion");
            Address address = new InternetAddress(user);
            message.setFrom(address);
            message.setSubject(subject);
            message.setDataHandler(handler);
            message.setContent(multipart);
            message.addHeaderLine("HAVR");
            message.addHeader("HAVR ","Cotizaciones");
            if (recipients.indexOf(',') > 0)
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
            else
                message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipients));
            Log.i("check", "transport");
            Transport transport = session.getTransport("smtp");
            Log.i("check", "connecting");
            transport.connect(mailhost, 465, user, password);
            Log.i("check", "Enviando:"+transport.isConnected());
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
            //Transport.send(message);
        }catch(Exception e){

        }
    }

    //Agregando archivo adjunto
    public void HAVRAddAttachment(String filename, String subject) {
        Log.d("SendEmail", "Agregando adjunto");
        try {
            String filename1 = "/data/data/com.ventas.havr.havrventas/PDFA" + "/CotizacionHAVR.pdf";
            BodyPart messageBodyPart = new MimeBodyPart();
            DataSource source = new FileDataSource(filename1);

            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName("CotizacionHAVR.pdf");
            messageBodyPart.setHeader("Cot","HAVR");
            multipart.addBodyPart(messageBodyPart);

            BodyPart messageBodyPart2 = new MimeBodyPart();
            messageBodyPart2.setText(subject);
            multipart.addBodyPart(messageBodyPart2);


        }catch(Exception e){
            Log.e(TAG,"No se agrego el adjunto");
        }
    }

    public class ByteArrayDataSource implements DataSource {
        private byte[] data;
        private String type;


        public ByteArrayDataSource(byte[] data, String type) {
            super();
            this.data = data;
            this.type = type;
        }

        public ByteArrayDataSource(byte[] data) {
            super();
            this.data = data;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getContentType() {
            if (type == null)
                return "application/octet-stream";
            else
                return type;
        }

        public InputStream getInputStream() {
            return new ByteArrayInputStream(data);
        }

        public String getName() {
            return "ByteArrayDataSource";
        }

        public OutputStream getOutputStream() throws IOException {
            throw new IOException("Not Supported");
        }
    }
}
