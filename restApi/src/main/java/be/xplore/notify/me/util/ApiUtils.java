package be.xplore.notify.me.util;

import be.xplore.notify.me.domain.exceptions.BadRequestException;
import be.xplore.notify.me.domain.exceptions.Unauthorized;
import be.xplore.notify.me.domain.user.Role;
import be.xplore.notify.me.domain.user.User;
import be.xplore.notify.me.services.user.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class ApiUtils {
    private final UserService userService;

    public ApiUtils(UserService userService) {
        this.userService = userService;
    }

    public User requireUserFromAuthentication(Authentication authentication) {
        return userService.getById(getRequestUserId(authentication));
    }

    public static int getPageNumber(Integer page) {
        int pageNumber = 0;
        if (page != null) {
            pageNumber = page;
        }
        return pageNumber;
    }

    public static String getRequestUserId(Authentication authentication) {
        Map<String, String> principal = (Map<String, String>) authentication.getPrincipal();
        return principal.getOrDefault("sub", null);
    }

    public static boolean hasRole(Authentication authentication, Role role) {
        return authentication.getAuthorities().stream().anyMatch(grantedAuthority -> grantedAuthority.toString().equals("ROLE_" + role));
    }

    public static void requireRole(Authentication authentication, Role role) {
        if (isAdmin(authentication)) {
            return;
        }
        if (!hasRole(authentication, role)) {
            throw new Unauthorized("You are not authorized to do that");
        }
    }

    public static void requireRole(Authentication authentication, List<Role> roles) {
        if (isAdmin(authentication)) {
            return;
        }
        boolean hasRole = false;
        for (Role role : roles) {
            if (hasRole(authentication, role)) {
                hasRole = true;
                break;
            }
        }
        if (!hasRole) {
            throw new Unauthorized("You are not authorized to do that");
        }
    }

    public static boolean isAdmin(Authentication authentication) {
        return hasRole(authentication, Role.ADMIN);
    }

    public static void requirePathVarAndBodyMatch(String pathVar, String bodyVar) {
        if (!pathVar.equals(bodyVar)) {
            throw new BadRequestException("Path variable and corresponding body variable do not match.");
        }
    }
}
