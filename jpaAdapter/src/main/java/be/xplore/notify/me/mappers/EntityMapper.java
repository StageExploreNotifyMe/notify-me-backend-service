package be.xplore.notify.me.mappers;

public interface EntityMapper<E, D> {
    D fromEntity(E e);

    E toEntity(D d);
}
