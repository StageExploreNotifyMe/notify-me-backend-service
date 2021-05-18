package be.xplore.notify.me.entity.event;

import be.xplore.notify.me.domain.event.EventLineStatus;
import be.xplore.notify.me.entity.OrganizationEntity;
import be.xplore.notify.me.entity.user.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventLineEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    @ManyToOne
    private LineEntity line;
    @ManyToOne
    private EventEntity event;
    private EventLineStatus eventLineStatus;
    @ManyToOne
    private OrganizationEntity organization;
    @ManyToMany
    private List<UserEntity> assignedUsers;
    @ManyToOne
    private UserEntity lineManager;
}
