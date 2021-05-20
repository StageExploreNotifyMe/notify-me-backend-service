package be.xplore.notify.me.util;

import org.springframework.stereotype.Component;

@Component
public class Converters {
    public static int getPageNumber(Integer page) {
        int pageNumber = 0;
        if (page != null) {
            pageNumber = page;
        }
        return pageNumber;
    }
}
