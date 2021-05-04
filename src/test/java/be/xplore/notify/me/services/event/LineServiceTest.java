package be.xplore.notify.me.services.event;

import be.xplore.notify.me.domain.event.Line;
import be.xplore.notify.me.entity.event.LineEntity;
import be.xplore.notify.me.entity.mappers.event.LineEntityMapper;
import be.xplore.notify.me.repositories.LineRepo;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest
class LineServiceTest {

    @Autowired
    private LineService lineService;
    @Autowired
    private Line line;
    @Autowired
    private LineEntityMapper lineEntityMapper;

    @MockBean
    private LineRepo lineRepo;

    @Test
    void getAllByVenue() {
        mockGetLinesByVenueId();
        Page<Line> allByVenue = lineService.getAllByVenue(line.getVenue().getId(), 0);
        assertEquals(line.getId(), allByVenue.getContent().get(0).getId());
    }

    private void mockGetLinesByVenueId() {
        given(lineRepo.getAllByVenueEntity_IdOrderByName(any(), any())).will(i -> {
            List<LineEntity> lineEntityList = new ArrayList<>();
            if (i.getArgument(0).equals(line.getVenue().getId())) {
                lineEntityList.add(lineEntityMapper.toEntity(line));
            }
            return new PageImpl<>(lineEntityList);
        });
    }

    @Test
    void getById() {
        mockFindById();
        Optional<Line> lineOptional = lineService.getById(line.getId());
        assertTrue(lineOptional.isPresent());
        assertEquals(line.getId(), lineOptional.get().getId());
    }

    @Test
    void getByIdNotFound() {
        mockFindById();
        Optional<Line> lineOptional = lineService.getById("qdsf");
        assertTrue(lineOptional.isEmpty());
    }

    private void mockFindById() {
        given(lineRepo.findById(any())).will(i -> i.getArgument(0).equals(line.getId()) ? Optional.of(lineEntityMapper.toEntity(line)) : Optional.empty());
    }

    @Test
    void save() {
        given(lineRepo.save(any())).will(i -> i.getArgument(0));
        Line save = lineService.save(line);
        assertEquals(line.getId(), save.getId());
    }
}