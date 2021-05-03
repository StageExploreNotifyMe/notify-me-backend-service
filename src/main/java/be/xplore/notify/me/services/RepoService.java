package be.xplore.notify.me.services;

import be.xplore.notify.me.domain.exceptions.DatabaseException;
import be.xplore.notify.me.entity.mappers.EntityMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@Slf4j
public class RepoService<D, E> {
    protected final JpaRepository<E, String> repo;
    protected final EntityMapper<E, D> entityMapper;

    public RepoService(JpaRepository<E, String> repo, EntityMapper<E, D> entityMapper) {
        this.repo = repo;
        this.entityMapper = entityMapper;
    }

    public Optional<D> getById(String id) {
        try {
            Optional<E> optional = repo.findById(id);
            if (optional.isEmpty()) {
                return Optional.empty();
            }
            D d = entityMapper.fromEntity(optional.get());
            return Optional.of(d);
        } catch (Exception e) {
            log.error("Failed to fetch object with id {}: {}: {}", id, e.getClass().getSimpleName(), e.getMessage());
            throw new DatabaseException(e);
        }
    }

    public D save(D d) {
        try {
            E e = repo.save(entityMapper.toEntity(d));
            return entityMapper.fromEntity(e);
        } catch (Exception e) {
            log.error("Failed to save entity: {}: {}", e.getClass().getSimpleName(), e.getMessage());
            throw new DatabaseException(e);
        }
    }

}
