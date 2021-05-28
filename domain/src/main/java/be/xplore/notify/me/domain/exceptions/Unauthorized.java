package be.xplore.notify.me.domain.exceptions;

public class Unauthorized extends RuntimeException {
    public Unauthorized(String message) {
        super(message);
    }
}
