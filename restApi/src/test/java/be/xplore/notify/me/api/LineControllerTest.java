package be.xplore.notify.me.api;

import be.xplore.notify.me.domain.Venue;
import be.xplore.notify.me.domain.event.Line;
import be.xplore.notify.me.dto.line.LineCreationDto;
import be.xplore.notify.me.dto.line.LineDto;
import be.xplore.notify.me.services.VenueService;
import be.xplore.notify.me.services.event.LineService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LineControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private Line line;
    @Autowired
    private Venue venue;

    @MockBean
    private LineService lineService;
    @MockBean
    private VenueService venueService;

    @Test
    void getLinesOfVenue() {
        try {
            mockEverything();
            ResultActions resultActions = performGet("/line/venue/" + line.getVenue().getId());
            expectResult(resultActions, HttpStatus.OK);
        } catch (Exception e) {
            failTest(e);
        }
    }

    @Test
    void getLinesOfVenueWithPage() {
        try {
            mockEverything();
            ResultActions resultActions = performGet("/line/venue/" + line.getVenue().getId() + "?page=0");
            expectResult(resultActions, HttpStatus.OK);
        } catch (Exception e) {
            failTest(e);
        }
    }

    @Test
    void getLinesOfVenueNotFound() {
        try {
            mockEverything();
            ResultActions resultActions = performGet("/line/venue/mqldfkj");
            expectResult(resultActions, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            failTest(e);
        }
    }

    @Test
    void createLine() {
        try {
            mockEverything();
            ResultActions resultActions = performPost("/line/create",
                    new LineCreationDto(line.getName(), line.getDescription(), line.getVenue().getId(), line.getNumberOfRequiredPeople()));
            expectResult(resultActions, HttpStatus.CREATED);
            LineDto lineDto = mapper.readValue(getResult(resultActions), LineDto.class);
            assertEquals(line.getName(), lineDto.getName());
        } catch (Exception e) {
            failTest(e);
        }
    }

    @Test
    void createLineBadRequest() {
        try {
            mockEverything();
            ResultActions resultActions = performPost("/line/create",
                    new LineCreationDto("", line.getDescription(), line.getVenue().getId(), line.getNumberOfRequiredPeople()));
            expectResult(resultActions, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            failTest(e);
        }
    }

    @Test
    void createLineBadRequest2() {
        try {
            mockEverything();
            ResultActions resultActions = performPost("/line/create",
                    new LineCreationDto(line.getName(), line.getDescription(), line.getVenue().getId(), -1));
            expectResult(resultActions, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            failTest(e);
        }
    }

    private void mockEverything() {
        mockGetLinesByVenue();
        mockGetLineById();
        mockGetVenueById();
        given(lineService.createLine(any())).will(i -> i.getArgument(0));
    }

    private void mockGetLinesByVenue() {
        given(lineService.getAllByVenue(any(), any(int.class))).will(i -> {
            List<Line> lineList = new ArrayList<>();
            if (i.getArgument(0).equals(line.getVenue().getId())) {
                lineList.add(line);
            }
            return new PageImpl<>(lineList);
        });
    }

    private void mockGetLineById() {
        given(lineService.getById(any())).will(i -> i.getArgument(0).equals(line.getId()) ? Optional.of(line) : Optional.empty());
    }

    private void mockGetVenueById() {
        given(venueService.getById(any())).will(i -> i.getArgument(0).equals(venue.getId()) ? Optional.of(venue) : Optional.empty());
    }

    private ResultActions performGet(String url) throws Exception {
        return mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions performPost(String url, Object dto) throws Exception {
        return mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)));
    }

    private String getResult(ResultActions resultActions) throws UnsupportedEncodingException {
        return resultActions.andReturn().getResponse().getContentAsString();
    }

    private void expectResult(ResultActions resultActions, HttpStatus ok) throws Exception {
        resultActions.andExpect(status().is(ok.value()));
    }

    private void failTest(Exception e) {
        e.printStackTrace();
        Assertions.fail("Exception was thrown in test.");
    }
}