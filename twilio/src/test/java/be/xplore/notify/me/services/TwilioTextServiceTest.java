package be.xplore.notify.me.services;

import be.xplore.notify.me.domain.notification.Notification;
import be.xplore.notify.me.domain.notification.NotificationChannel;
import be.xplore.notify.me.domain.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest
class TwilioTextServiceTest {

    @Autowired
    private TwilioTextService twilioTextService;

    @MockBean
    private TwilioTextService.TwilioTextSender textSender;

    @Autowired
    private Notification notification;
    @Autowired
    private User user;

    private void mockSendText(boolean includeSentDate, boolean includePrice) {
        String json = "{" +
                "\"body\": \"This is a test\", " +
                "\"numSegments\": 1, " +
                "\"direction\": \"outbound-api\", " +
                "\"from\": \"+15005550006\", " +
                "\"to\": \"+32492920000\", " +
                "\"dateUpdated\": \"2021-05-17T14:25:21Z\", " +
                "\"uri\": \"/2010-04-01/Accounts/AC0ff9d72359779614e668f321a757a887/Messages/SM3a96711f946d45058d26242cac33176d.json\", " +
                "\"accountSid\": \"AC0ff9d72359779614e668f321a757a887\", " +
                "\"numMedia\": 0, " +
                "\"status\": \"queued\", " +
                "\"sid\": \"SM3a96711f946d45058d26242cac33176d\", " +
                (includeSentDate ? "\"dateSent\": \"2021-05-17T14:25:21Z\", " : "") +
                "\"dateCreated\": \"2021-05-17T14:25:21Z\", " +
                "\"priceUnit\": \"USD\", " +
                (includePrice ? "\"price\": \"0.5\", " : "") +
                "\"apiVersion\": \"2010-04-01\", " +
                "\"subresourceUris\": {}}";
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        given(textSender.sendText(any(), any(), any())).will(i -> Message.fromJson(json, mapper));
    }

    @Test
    void sendWhatsApp() {
        mockSendText(true, true);
        Notification notification = twilioTextService.sendWhatsApp(this.notification, user);
        assertEquals(NotificationChannel.WHATSAPP, notification.getUsedChannel());
        assertTrue(notification.getSentDate().toString().contains("2021-05-17T14:25:21"));
    }

    @Test
    void sendSms() {
        mockSendText(false, false);
        twilioTextService.sendSms(notification, user);
        assertEquals(NotificationChannel.SMS, notification.getUsedChannel());
    }

    @Test
    void sendText() {
        TwilioTextService.TwilioTextSender twilioTextSender = new TwilioTextService.TwilioTextSender();
        assertThrows(ApiException.class, () -> twilioTextSender.sendText(new PhoneNumber("+15005550006"), "+32492920000", "no Authentication"));
    }
}