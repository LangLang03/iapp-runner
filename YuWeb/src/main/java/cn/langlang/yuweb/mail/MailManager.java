package cn.langlang.yuweb.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

public class MailManager {
    private static final Logger logger = LoggerFactory.getLogger(MailManager.class);
    
    private static final MailManager INSTANCE = new MailManager();
    
    private String host;
    private int port = 587;
    private String username;
    private String password;
    private boolean ssl = false;
    private boolean configured = false;
    
    private MailManager() {
    }
    
    public static MailManager getInstance() {
        return INSTANCE;
    }
    
    public void configure(String host, int port, String username, String password, boolean ssl) {
        this.host = host;
        this.port = port > 0 ? port : 587;
        this.username = username;
        this.password = password;
        this.ssl = ssl;
        this.configured = true;
        logger.info("Mail configured: {}:{}", host, port);
    }
    
    public boolean sendMail(String to, String subject, String body, boolean html) {
        if (!configured) {
            logger.error("Mail not configured");
            return false;
        }
        
        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", "true");
        
        if (ssl) {
            props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        } else {
            props.put("mail.smtp.starttls.enable", "true");
        }
        
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            
            if (html) {
                message.setContent(body, "text/html; charset=utf-8");
            } else {
                message.setText(body);
            }
            
            Transport.send(message);
            logger.info("Mail sent to: {}", to);
            return true;
        } catch (MessagingException e) {
            logger.error("Failed to send mail: {}", e.getMessage(), e);
            return false;
        }
    }
    
    public boolean isConfigured() {
        return configured;
    }
    
    public void reset() {
        this.host = null;
        this.port = 587;
        this.username = null;
        this.password = null;
        this.ssl = false;
        this.configured = false;
    }
}
