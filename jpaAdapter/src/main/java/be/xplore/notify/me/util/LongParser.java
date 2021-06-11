package be.xplore.notify.me.util;

public class LongParser {
    public static Long parseLong(String id) {
        if (id == null) {
            return null;
        }

        return Long.parseLong(id);
    }
}
