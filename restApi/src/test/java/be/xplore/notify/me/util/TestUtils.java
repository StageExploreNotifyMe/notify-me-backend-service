package be.xplore.notify.me.util;

import be.xplore.notify.me.domain.user.Role;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TestUtils {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final List<Role> ROLES = new ArrayList<>(Arrays.asList(Role.values()));
    private static String userId = "1";

    public static void setRoles(Role[] rolesArray) {
        ROLES.clear();
        ROLES.addAll(Arrays.asList(rolesArray));
    }

    public static void setUserId(String id) {
        userId = id;
    }

    public static void reset() {
        setUserId("1");
        setRoles(Role.values());
    }

    public static void failTest(Exception e) {
        e.printStackTrace();
        Assertions.fail("Exception was thrown in test.");
    }

    public static ResultActions performGet(MockMvc mockMvc, String url) {
        try {
            return mockMvc.perform(addHeaderAndContentType(get(url)));
        } catch (Exception e) {
            failTest(e);
            return null;
        }
    }

    public static ResultActions performPost(MockMvc mockMvc, Object body, String url) {
        try {
            MockHttpServletRequestBuilder builder = addBody(body, post(url));
            return mockMvc.perform(addHeaderAndContentType(builder));
        } catch (Exception e) {
            failTest(e);
            return null;
        }
    }

    public static ResultActions performPatch(MockMvc mockMvc, Object body, String url) {
        try {
            MockHttpServletRequestBuilder builder = addBody(body, patch(url));
            return mockMvc.perform(addHeaderAndContentType(builder));
        } catch (Exception e) {
            failTest(e);
            return null;
        }
    }

    private static MockHttpServletRequestBuilder addBody(Object body, MockHttpServletRequestBuilder builder) throws JsonProcessingException {
        if (body != null) {
            builder.content(MAPPER.writeValueAsString(body));
        }
        return builder;
    }

    private static MockHttpServletRequestBuilder addHeaderAndContentType(MockHttpServletRequestBuilder builder) {
        return builder.contentType(MediaType.APPLICATION_JSON).header("Authorization", generateValidJwt());
    }

    public static String getContentAsString(ResultActions resultActions) {
        try {
            return resultActions.andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            failTest(e);
            return "";
        }
    }

    public static void expectStatus(ResultActions resultActions, HttpStatus expectedStatus) {
        try {
            resultActions.andExpect(status().is(expectedStatus.value()));
        } catch (Exception e) {
            failTest(e);
        }
    }

    private static String generateValidJwt() {
        Date exp = new Date(System.currentTimeMillis() + 50000);
        Claims claims = Jwts.claims().setSubject(userId);
        claims.put("ROLES", ROLES.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toString()))
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","))
        );
        return Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS512, "ADummySecretKey").setExpiration(exp).compact();
    }
}
