package be.xplore.notify.me.services;

import be.xplore.notify.me.domain.exceptions.NotificationSenderException;
import be.xplore.notify.me.domain.notification.Notification;
import be.xplore.notify.me.domain.notification.NotificationChannel;
import be.xplore.notify.me.domain.user.User;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.MailSettings;
import com.sendgrid.helpers.mail.objects.Setting;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Currency;

@Service
public class SendgridEmailService {
    private final Email fromEmail;
    private final SendGrid sendGrid;
    private final boolean sandbox;

    public SendgridEmailService(
            @Value("${notify.me.sendgrid.fromEmail}") String fromEmail,
            SendGridConfig sendGrid,
            @Value("${notify.me.sandbox:false}") boolean sandbox
    ) {
        this.fromEmail = new Email(fromEmail);
        this.sendGrid = sendGrid.getSendGrid();
        this.sandbox = sandbox;
    }

    public Notification sendEmail(Notification notification, User user) {
        try {
            Request request = generateEmailRequest(notification, user);
            Response response = sendGrid.api(request);
            return Notification.builder()
                .id(notification.getId())
                .userId(user.getId())
                .title(notification.getTitle())
                .body(notification.getBody())
                .urgency(notification.getUrgency())
                .type(notification.getType())
                .creationDate(notification.getCreationDate())
                .sentDate(getEmailSentDate(response.getHeaders().get("Date")))
                .usedChannel(NotificationChannel.EMAIL)
                .price(0.0)
                .priceCurrency(Currency.getInstance("EUR"))
                .build();
        } catch (IOException ex) {
            throw new NotificationSenderException(ex);
        }
    }

    private Request generateEmailRequest(Notification notification, User user) throws IOException {
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(generateMail(notification, user).build());

        return request;
    }

    private Mail generateMail(Notification notification, User user) {
        Mail mail = new Mail(
                fromEmail,
                notification.getTitle(),
                new Email(user.getEmail()),
                new Content("text/plain", notification.getBody())
        );
        mail.mailSettings = generateMailSettings();
        return mail;
    }

    private MailSettings generateMailSettings() {
        MailSettings mailSettings = new MailSettings();
        Setting sandBoxMode = new Setting();
        sandBoxMode.setEnable(sandbox);
        mailSettings.setSandboxMode(sandBoxMode);
        return mailSettings;
    }

    private LocalDateTime getEmailSentDate(String date) {
        LocalDateTime sentDate = LocalDateTime.now();
        if (date != null) {
            sentDate = LocalDateTime.parse(date, DateTimeFormatter.RFC_1123_DATE_TIME);
        }
        return sentDate;
    }

    @Component
    public static class SendGridConfig {
        @Value("${notify.me.sendgrid.apiKey}")
        private String sendGridApiKey;

        public SendGrid getSendGrid() {
            return new SendGrid(sendGridApiKey);
        }
    }
}
