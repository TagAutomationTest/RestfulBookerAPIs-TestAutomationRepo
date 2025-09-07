package Utils;



import jakarta.mail.PasswordAuthentication;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class EmailUtil {

    private EmailUtil() {


    }

    public static void sendEmilReport(int passed, int failed, int skipped) {
        try {
            LogsUtil.info("Sending test Status by email ...........> Started ");
            String username = "tag.ahmed252@gmail.com";
            String appPassword = "xwxo tgls dwep adwl"; // 🔐 Make sure this is the actual App Password
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");


            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, appPassword);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse("tag.ahmed252@gmail.com,tag49163@gmail.com")
            );
            message.setSubject("Automation Test Execution Summary");

            // String htmlContent = Files.readString(Paths.get("src/main/resources/email-template.html"));
            String emailBody = buildEmailBody(passed, failed, skipped);
            message.setContent(emailBody, "text/html; charset=utf-8");
            Transport.send(message);
            LogsUtil.info("Report email sent successfully.");

        } catch (Exception e) {
            LogsUtil.info("Sending email fail due to  " + e.getMessage());
        }
    }

    public static String buildEmailBody(int passed, int failed, int skipped) throws Exception {
        // Load the HTML template file
        String templatePath = "src/main/resources/email-template.html"; // adjust path if needed
        String htmlContent = new String(Files.readAllBytes(Paths.get(templatePath)));

        // Replace placeholders with actual values
        htmlContent = htmlContent.replace("{{passed}}", String.valueOf(passed));
        htmlContent = htmlContent.replace("{{failed}}", String.valueOf(failed));
        htmlContent = htmlContent.replace("{{skipped}}", String.valueOf(skipped));

        return htmlContent;
    }
}

