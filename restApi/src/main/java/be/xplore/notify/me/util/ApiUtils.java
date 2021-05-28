package be.xplore.notify.me.util;

import be.xplore.notify.me.domain.exceptions.Unauthorized;
import be.xplore.notify.me.domain.user.Role;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ApiUtils {
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
        if (hasRole(authentication, Role.ADMIN)) {
            return;
        }
        if (!hasRole(authentication, role)) {
            throw new Unauthorized("You are not authorized to do that");
        }
    }
}
