package be.xplore.notify.me.services;

import be.xplore.notify.me.domain.notification.Notification;
import be.xplore.notify.me.domain.notification.NotificationChannel;
import be.xplore.notify.me.domain.user.User;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TwilioTextService {
    private final PhoneNumber fromNumber;
    private final PhoneNumber whatsAppFromNumber;
    private final TwilioTextSender twilioTextSender;

    public TwilioTextService(
            @Value("${notify.me.twilio.accountSid}") String accountSid,
            @Value("${notify.me.twilio.authToken}") String authToken,
            @Value("${notify.me.twilio.fromNumber}") String fromNumber,
            @Value("${notify.me.twilio.whatsAppFromNumber}") String whatsAppFromNumber,
            TwilioTextSender twilioTextSender
    ) {
        this.fromNumber = new PhoneNumber(fromNumber);
        this.whatsAppFromNumber = new PhoneNumber(whatsAppFromNumber);
        this.twilioTextSender = twilioTextSender;
        Twilio.init(accountSid, authToken);
    }

    public Notification sendWhatsApp(Notification notification, User user) {
        Message message = twilioTextSender.sendText(whatsAppFromNumber, String.format("whatsapp:%s", user.getMobileNumber()), notification.getBody());
        return Notification.builder()
            .id(notification.getId())
            .userId(user.getId())
            .title(notification.getTitle())
            .body(notification.getBody())
            .urgency(notification.getUrgency())
            .type(notification.getType())
            .creationDate(notification.getCreationDate())
            .sentDate(getSmsSentDate(message))
            .usedChannel(NotificationChannel.WHATSAPP)
            .price(getPrice(message))
            .priceCurrency(message.getPriceUnit())
            .build();
    }

    public Notification sendSms(Notification notification, User user) {
        Message message = twilioTextSender.sendText(fromNumber, user.getMobileNumber(), notification.getBody());
        return Notification.builder()
            .id(notification.getId())
            .userId(user.getId())
            .title(notification.getTitle())
            .body(notification.getBody())
            .urgency(notification.getUrgency())
            .type(notification.getType())
            .creationDate(notification.getCreationDate())
            .sentDate(getSmsSentDate(message))
            .usedChannel(NotificationChannel.SMS)
            .price(getPrice(message))
            .priceCurrency(message.getPriceUnit())
            .build();
    }

    private Double getPrice(Message message) {
        return message.getPrice() == null ? null : Double.parseDouble(message.getPrice());
    }

    private LocalDateTime getSmsSentDate(Message message) {
        LocalDateTime sentDate = LocalDateTime.now();
        if (message.getDateSent() != null) {
            sentDate = message.getDateSent().toLocalDateTime();
        }
        return sentDate;
    }

    @Component
    public static class TwilioTextSender {
        public Message sendText(PhoneNumber from, String to, String body) {
            return Message.creator(new PhoneNumber(to), from, body).create();
        }
    }
}
