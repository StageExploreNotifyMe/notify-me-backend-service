package be.xplore.notify.me.api;

import be.xplore.notify.me.domain.exceptions.AlreadyExistsException;
import be.xplore.notify.me.domain.exceptions.BadRequestException;
import be.xplore.notify.me.domain.exceptions.NotFoundException;
import be.xplore.notify.me.domain.exceptions.Unauthorized;
import be.xplore.notify.me.util.ApiUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@ControllerAdvice
public class ExceptionHandlerController {

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(AlreadyExistsException.class)
    public void alreadyExistsException(HttpServletRequest request, Exception e) {
        log.trace("Request on {} produced an already exists exception: {}: {}", request.getRequestURI(), e.getClass().getSimpleName(), e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public void illegalArgumentException(HttpServletRequest request, Exception e) {
        log.trace("Request on {} produced an illegal argument exception: {}: {}", request.getRequestURI(), e.getClass().getSimpleName(), e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    public void badRequestException() {
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public void notFoundException(HttpServletRequest request, Exception e) {
        log.trace("Request on {} produced a not found exception: {}: {}", request.getRequestURI(), e.getClass().getSimpleName(), e.getMessage());
    }

    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public void methodNotAllowed() {
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(Unauthorized.class)
    public void unAuthorized(HttpServletRequest request, Authentication authentication) {
        try {
            log.info("Request on {} by user with id {} produced an unauthorized exception", request.getRequestURI(), ApiUtils.getRequestUserId(authentication));
        } catch (Exception e) {
            log.info("Request on {} produced an unauthorized exception", request.getRequestURI());
        }
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public void uncaughtExceptionHandler(HttpServletRequest request, Exception e) {
        log.error("Request on {} produced an uncaught exception: {}: {}", request.getRequestURI(), e.getClass().getSimpleName(), e.getMessage());
        e.printStackTrace();
    }

}
