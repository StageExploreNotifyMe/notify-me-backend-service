package be.xplore.notify.me.repositories;

import be.xplore.notify.me.entity.user.AuthenticationCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaAuthenticationCodeRepo extends JpaRepository<AuthenticationCodeEntity, Long> {
}
