package be.xplore.notify.me.entity.user;

import be.xplore.notify.me.entity.notification.NotificationEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    @OneToOne
    private UserPreferencesEntity userPreferences;
    private String firstname;
    private String lastname;
    private String mobileNumber;
    @Column(unique = true)
    private String email;
    private String passwordHash;
    @OneToMany
    private List<NotificationEntity> inbox = new ArrayList<>();
    @OneToMany
    private List<NotificationEntity> notificationQueue = new ArrayList<>();
}
