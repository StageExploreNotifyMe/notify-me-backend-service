package be.xplore.notify.me.domain.exceptions;

public class DatabaseException extends RuntimeException {
    public DatabaseException(Throwable cause) {
        super(cause);
    }
}
