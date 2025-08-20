package com.tencent.wxcloudrun.client.email;

import com.tencent.wxcloudrun.domain.constant.Constants;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author zhangyichuan
 * @date 2025/5/20
 */
@Component
@Slf4j
public class EmailService {
  private static final String SMTP_HOST = "smtp.qq.com";
  private static final String SMTP_PORT = "587";
  private final String FROM = System.getenv("EMAIL_FROM");
  private final String PASSWORD = System.getenv("EMAIL_PASSWORD");

  public void sendEmailForIeg(String to, String content) throws Exception {
    Message message = createMessage();
    String nick = javax.mail.internet.MimeUtility.encodeText("IEG Reading");
    message.setFrom(new InternetAddress(nick + " <" + FROM + ">"));
    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
    message.setSubject("IEG读书协会-七夕活动，您有新的留言！");
    message.setText(content + "\n\n\n----------------------------\n邮件为IEG读书协会邮箱统一发送，请勿直接回复该邮箱，请移步公众号留言回复：\n"+ Constants.sendMailHelper());
    Transport.send(message);
  }

  private Message createMessage() {
    Properties props = new Properties();
    props.put("mail.smtp.host", SMTP_HOST);
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.port", SMTP_PORT);
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.connectiontimeout", "5000");
    props.put("mail.smtp.timeout", "5000");
    Session session =
        Session.getInstance(
            props,
            new Authenticator() {
              @Override
              protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM, PASSWORD);
              }
            });
    return new MimeMessage(session);
  }
}
