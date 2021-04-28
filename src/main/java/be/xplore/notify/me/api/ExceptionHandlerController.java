package be.xplore.notify.me.api;

import be.xplore.notify.me.domain.exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@ControllerAdvice
public class ExceptionHandlerController {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public void illegalArgumentException(HttpServletRequest request, Exception e) {
        log.trace("Request on {} produced an illegal argument exception: {}: {}", request.getRequestURI(), e.getClass().getSimpleName(), e.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public void notFoundException(HttpServletRequest request, Exception e) {
        log.trace("Request on {} produced a not found exception: {}: {}", request.getRequestURI(), e.getClass().getSimpleName(), e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public void uncaughtExceptionHandler(HttpServletRequest request, Exception e) {
        log.warn("Request on {} produced an uncaught exception: {}: {}", request.getRequestURI(), e.getClass().getSimpleName(), e.getMessage());
    }

}
