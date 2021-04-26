package be.xplore.notify.me.entity.mappers;

public interface EntityMapper<E, D> {
    D fromEntity(E e);

    E toEntity(D d);
}
