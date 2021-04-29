package be.xplore.notify.me.entity.user;

import be.xplore.notify.me.domain.user.MemberRequestStatus;
import be.xplore.notify.me.domain.user.Role;
import be.xplore.notify.me.entity.OrganizationEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserOrganizationEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private String id;
    @ManyToOne
    private UserEntity userEntity;
    @ManyToOne
    private OrganizationEntity organizationEntity;
    private Role role;
    private MemberRequestStatus status;
}
