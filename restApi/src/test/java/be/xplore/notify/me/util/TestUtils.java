package be.xplore.notify.me.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.io.UnsupportedEncodingException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TestUtils {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static void failTest(Exception e) {
        e.printStackTrace();
        Assertions.fail("Exception was thrown in test.");
    }

    public static ResultActions performPost(MockMvc mockMvc, Object body, String url) throws Exception {
        return mockMvc.perform(post(url).content(MAPPER.writeValueAsString(body)).contentType(MediaType.APPLICATION_JSON));
    }

    public static String getContentAsString(ResultActions resultActions) throws UnsupportedEncodingException {
        return resultActions.andReturn().getResponse().getContentAsString();
    }

    public static void expectStatus(ResultActions resultActions, HttpStatus expectedStatus) throws Exception {
        resultActions.andExpect(status().is(expectedStatus.value()));
    }
}
