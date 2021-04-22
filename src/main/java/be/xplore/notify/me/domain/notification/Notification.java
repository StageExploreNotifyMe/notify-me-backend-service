package be.xplore.notify.me.domain.notification;

import be.xplore.notify.me.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    @ManyToOne
    private User user;
    private String title;
    private String body;
    private NotificationChannel usedChannel;
    private NotificationType type;
    private NotificationUrgency urgency;

    @Override
    public String toString() {
        return "Notification: " + title + ": " + body + "(" + usedChannel + ", " + type + ", " + urgency + ")";
    }
}
