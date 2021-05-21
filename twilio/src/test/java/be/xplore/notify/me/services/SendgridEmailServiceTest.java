package be.xplore.notify.me.services;

import be.xplore.notify.me.domain.exceptions.NotificationSenderException;
import be.xplore.notify.me.domain.notification.Notification;
import be.xplore.notify.me.domain.notification.NotificationChannel;
import be.xplore.notify.me.domain.user.User;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest
class SendgridEmailServiceTest {

    private SendgridEmailService sendgridEmailService;

    @MockBean
    private SendGrid sendGrid;
    @MockBean
    private SendgridEmailService.SendGridConfig sendGridConfig;
    @Autowired
    private Notification notification;
    @Autowired
    private User user;
    private String date;

    @BeforeEach
    void setUp() {
        try {
            MockitoAnnotations.openMocks(this);
            given(sendGridConfig.getSendGrid()).willReturn(sendGrid);
            mockSendGridApi();
        } catch (IOException e) {
            fail(e);
        }
        sendgridEmailService = new SendgridEmailService("test@email.com", sendGridConfig, true);
    }

    private void mockSendGridApi() throws IOException {
        given(sendGrid.api(any())).will(i -> {
            Map<String, String> headers = new HashMap<>();
            headers.put("Date", date);
            return new Response(200, "", headers);
        });
    }

    @Test
    void sendEmail() {
        Notification sentEmail = sendgridEmailService.sendEmail(this.notification, user);
        assertEquals(NotificationChannel.EMAIL, sentEmail.getUsedChannel());
        assertNotNull(sentEmail.getSentDate());
        date = ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME);
        sendgridEmailService.sendEmail(this.notification, user);
    }

    @Test
    void sendEmailException() {
        try {
            given(sendGrid.api(any())).willThrow(new IOException("test"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertThrows(NotificationSenderException.class, () -> sendgridEmailService.sendEmail(this.notification, user));

    }
}