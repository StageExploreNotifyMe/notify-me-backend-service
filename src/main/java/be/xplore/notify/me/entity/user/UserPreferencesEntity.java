package be.xplore.notify.me.entity.user;

import be.xplore.notify.me.domain.notification.NotificationChannel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPreferencesEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private String id;
    private NotificationChannel normalChannel;
    private NotificationChannel urgentChannel;
}
