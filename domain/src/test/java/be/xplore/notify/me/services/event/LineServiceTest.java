package be.xplore.notify.me.services.event;

import be.xplore.notify.me.domain.event.Line;
import be.xplore.notify.me.domain.exceptions.NotFoundException;
import be.xplore.notify.me.persistence.LineRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest
class LineServiceTest {

    @Autowired
    private LineService lineService;

    @Autowired
    private Line line;

    @MockBean
    private LineRepo lineRepo;

    @Test
    void getAllByVenue() {
        mockGetLinesByVenueId();
        Page<Line> allByVenue = lineService.getAllByVenue(line.getVenue().getId(), 0);
        assertEquals(line.getId(), allByVenue.getContent().get(0).getId());
    }

    @Test
    void getById() {
        mockFindById();
        Line foundLine = lineService.getById(line.getId());
        assertEquals(line.getId(), foundLine.getId());
    }

    @Test
    void getByIdNotFound() {
        mockFindById();
        assertThrows(NotFoundException.class, () -> lineService.getById("qdsf"));
    }

    @Test
    void save() {
        mockSave();
        Line save = lineService.save(line);
        assertEquals(line.getId(), save.getId());
    }

    @Test
    void createLine() {
        mockSave();
        Line save = lineService.createLine(line);
        assertEquals(line.getId(), save.getId());
    }

    @Test
    void updateLine() {
        mockFindById();
        mockSave();
        Line updated = lineService.updateLine(Line.builder().id(line.getId()).name("update").description("update").build());
        assertEquals(line.getId(), updated.getId());
        assertEquals("update", updated.getName());
        assertEquals(line.getVenue().getId(), updated.getVenue().getId());
    }

    @Test
    void updateLineNotFound() {
        given(lineRepo.findById(any())).willReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> lineService.updateLine(line));
    }

    private void mockFindById() {
        given(lineRepo.findById(any())).will(i -> i.getArgument(0).equals(line.getId()) ? Optional.of(line) : Optional.empty());
    }

    private void mockSave() {
        given(lineRepo.save(any())).will(i -> i.getArgument(0));
    }

    private void mockGetLinesByVenueId() {
        given(lineRepo.getAllByVenue(any(), any())).will(i -> {
            List<Line> lines = new ArrayList<>();
            if (i.getArgument(0).equals(line.getVenue().getId())) {
                lines.add(line);
            }
            return new PageImpl<>(lines);
        });
    }
}