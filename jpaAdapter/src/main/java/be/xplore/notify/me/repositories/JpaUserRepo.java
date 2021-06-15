package be.xplore.notify.me.repositories;

import be.xplore.notify.me.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaUserRepo extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);

    @Query("select o from UserEntity o where o.id in :ids")
    List<UserEntity> findAllByIds(@Param("ids") List<Long> ids);
}
