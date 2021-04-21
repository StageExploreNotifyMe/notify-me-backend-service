package be.xplore.notify.me.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import static javax.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class UserOrganisation {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private String id;
    @ManyToOne
    private User user;
    @ManyToOne
    private Organization organization;
    private Role role;
    private MemberRequestStatus status;

}
