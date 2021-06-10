package be.xplore.notify.me.adapters.event;

import be.xplore.notify.me.domain.event.Line;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.transaction.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
@SpringBootTest
class LineAdapterTest {

    @Autowired
    private LineAdapter lineAdapter;

    @Test
    void getAllByVenue() {
        Page<Line> linePage = lineAdapter.getAllByVenue("1", PageRequest.of(0, 20));
        assertTrue(linePage.hasContent());
    }

    @Test
    void findById() {
        Optional<Line> byId = lineAdapter.findById("1");
        assertTrue(byId.isPresent());
    }

    @Test
    void findByIdNotFound() {
        Optional<Line> byId = lineAdapter.findById("500");
        assertTrue(byId.isEmpty());
    }

    @Test
    void save() {
        Line line = Line.builder().id("501").name("tst").build();
        Line save = lineAdapter.save(line);
        assertEquals(line.getName(), save.getName());
    }
}