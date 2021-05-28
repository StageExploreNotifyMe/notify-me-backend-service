package be.xplore.notify.me.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TestUtils {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static void failTest(Exception e) {
        e.printStackTrace();
        Assertions.fail("Exception was thrown in test.");
    }

    public static ResultActions performGet(MockMvc mockMvc, String url) throws Exception {
        return mockMvc.perform(addHeaderAndContentType(get(url)));
    }

    public static ResultActions performPost(MockMvc mockMvc, Object body, String url) throws Exception {
        MockHttpServletRequestBuilder builder = addBody(body, post(url));
        return mockMvc.perform(addHeaderAndContentType(builder));
    }

    public static ResultActions performPatch(MockMvc mockMvc, Object body, String url) throws Exception {
        MockHttpServletRequestBuilder builder = addBody(body, patch(url));
        return mockMvc.perform(addHeaderAndContentType(builder));
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

    public static String getContentAsString(ResultActions resultActions) throws UnsupportedEncodingException {
        return resultActions.andReturn().getResponse().getContentAsString();
    }

    public static void expectStatus(ResultActions resultActions, HttpStatus expectedStatus) throws Exception {
        resultActions.andExpect(status().is(expectedStatus.value()));
    }

    private static String generateValidJwt() {
        Date exp = new Date(System.currentTimeMillis() + 50000);
        Claims claims = Jwts.claims().setSubject("1");
        return Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS512, "ADummySecretKey").setExpiration(exp).compact();
    }
}
