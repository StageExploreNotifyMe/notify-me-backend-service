package be.xplore.notify.me.adapter;

import be.xplore.notify.me.domain.notification.Notification;
import be.xplore.notify.me.domain.notification.NotificationChannel;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.services.SendgridEmailService;
import be.xplore.notify.me.services.TwilioTextService;
import be.xplore.notify.me.services.notification.NotificationSenderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Currency;

@Slf4j
@Component
public class TwilioNotificationSenderAdapter implements NotificationSenderService {

    private final TwilioTextService twilioTextService;
    private final SendgridEmailService sendgridEmailService;

    public TwilioNotificationSenderAdapter(
            TwilioTextService twilioTextService,
            SendgridEmailService sendgridEmailService
    ) {
        this.twilioTextService = twilioTextService;
        this.sendgridEmailService = sendgridEmailService;
    }

    @Override
    public Notification sendNotification(Notification notification, User user) {
        preSendValidation(notification, user);

        Notification returnNotification;
        switch (notification.getUsedChannel()) {
            case SMS:
                returnNotification = twilioTextService.sendSms(notification, user);
                break;
            case APP:
                returnNotification = sendAppNotification(notification, user);
                break;
            case EMAIL:
                returnNotification = sendgridEmailService.sendEmail(notification, user);
                break;
            case WHATSAPP:
                returnNotification = twilioTextService.sendWhatsApp(notification, user);
                break;
            default:
                throw new IllegalArgumentException(String.format("Channel %s is not supported by twilio", notification.getUsedChannel()));
        }

        return returnNotification;
    }

    private void preSendValidation(Notification notification, User user) {
        if (!notification.getUserId().equals(user.getId())) {
            throw new IllegalArgumentException("User id in user and notification don't match!");
        }
    }

    private Notification sendAppNotification(Notification notification, User user) {
        log.info("In app notification sent: " + notification);
        return Notification.builder()
            .id(notification.getId())
            .userId(user.getId())
            .title(notification.getTitle())
            .body(notification.getBody())
            .urgency(notification.getUrgency())
            .type(notification.getType())
            .creationDate(notification.getCreationDate())
            .sentDate(LocalDateTime.now())
            .usedChannel(NotificationChannel.APP)
            .price(0.0)
            .eventId(notification.getEventId())
            .priceCurrency(Currency.getInstance("EUR"))
            .build();
    }

}
