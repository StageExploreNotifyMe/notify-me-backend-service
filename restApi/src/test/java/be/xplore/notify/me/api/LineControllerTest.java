package be.xplore.notify.me.api;

import be.xplore.notify.me.domain.Venue;
import be.xplore.notify.me.domain.event.Line;
import be.xplore.notify.me.dto.line.LineCreationDto;
import be.xplore.notify.me.dto.line.LineDto;
import be.xplore.notify.me.mappers.event.LineDtoMapper;
import be.xplore.notify.me.services.VenueService;
import be.xplore.notify.me.services.event.LineService;
import be.xplore.notify.me.util.TestUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@AutoConfigureMockMvc
class LineControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private LineDtoMapper lineDtoMapper;
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
            ResultActions resultActions = TestUtils.performGet(mockMvc, "/line/venue/" + line.getVenue().getId());
            TestUtils.expectStatus(resultActions, HttpStatus.OK);
        } catch (Exception e) {
            TestUtils.failTest(e);
        }
    }

    @Test
    void getLinesOfVenueWithPage() {
        try {
            mockEverything();
            ResultActions resultActions = TestUtils.performGet(mockMvc, "/line/venue/" + line.getVenue().getId() + "?page=0");
            TestUtils.expectStatus(resultActions, HttpStatus.OK);
        } catch (Exception e) {
            TestUtils.failTest(e);
        }
    }

    @Test
    void getLinesOfVenueNotFound() {
        try {
            mockEverything();
            ResultActions resultActions = TestUtils.performGet(mockMvc, "/line/venue/mqldfkj");
            TestUtils.expectStatus(resultActions, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            TestUtils.failTest(e);
        }
    }

    @Test
    void createLine() {
        try {
            mockEverything();
            ResultActions resultActions = TestUtils.performPost(mockMvc,
                new LineCreationDto(line.getName(), line.getDescription(), line.getVenue().getId(), line.getNumberOfRequiredPeople()), "/line/create"
            );
            TestUtils.expectStatus(resultActions, HttpStatus.CREATED);
            LineDto lineDto = mapper.readValue(TestUtils.getContentAsString(resultActions), LineDto.class);
            assertEquals(line.getName(), lineDto.getName());
        } catch (Exception e) {
            TestUtils.failTest(e);
        }
    }

    @Test
    void createLineBadRequest() {
        try {
            mockEverything();
            ResultActions resultActions = TestUtils.performPost(mockMvc,
                new LineCreationDto("", line.getDescription(), line.getVenue().getId(), line.getNumberOfRequiredPeople()), "/line/create");
            TestUtils.expectStatus(resultActions, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            TestUtils.failTest(e);
        }
    }

    @Test
    void createLineBadRequest2() {
        try {
            mockEverything();
            ResultActions resultActions = TestUtils.performPost(mockMvc,
                new LineCreationDto(line.getName(), line.getDescription(), line.getVenue().getId(), -1), "/line/create");
            TestUtils.expectStatus(resultActions, HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            TestUtils.failTest(e);
        }
    }

    @Test
    void editLine() {
        try {
            mockEverything();
            ResultActions resultActions = TestUtils.performPatch(mockMvc, lineDtoMapper.toDto(line), "/line/edit");
            TestUtils.expectStatus(resultActions, HttpStatus.OK);
            LineDto lineDto = mapper.readValue(TestUtils.getContentAsString(resultActions), LineDto.class);
            assertEquals(line.getId(), lineDto.getId());
        } catch (Exception e) {
            TestUtils.failTest(e);
        }
    }

    private void mockEverything() {
        mockGetLinesByVenue();
        mockGetLineById();
        mockGetVenueById();
        given(lineService.createLine(any())).will(i -> i.getArgument(0));
        given(lineService.updateLine(any())).will(i -> i.getArgument(0));
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
}