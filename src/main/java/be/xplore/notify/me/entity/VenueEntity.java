package be.xplore.notify.me.entity;

import be.xplore.notify.me.entity.user.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VenueEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    private String name;
    @ManyToMany
    private List<UserEntity> venueManagers;
    @ManyToMany
    private List<UserEntity> lineManagers;
}
