package be.xplore.notify.me.persistence;

import be.xplore.notify.me.domain.user.AuthenticationCode;

import java.util.List;

public interface AuthenticationCodeRepo {

    List<AuthenticationCode> saveAll(List<AuthenticationCode> authenticationCodes);
}
