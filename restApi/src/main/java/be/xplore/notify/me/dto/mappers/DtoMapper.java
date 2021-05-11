package be.xplore.notify.me.dto.mappers;

public interface DtoMapper<Dt, Do> {
    Do fromDto(Dt d);

    Dt toDto(Do d);
}
