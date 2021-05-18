package be.xplore.notify.me;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class NotifymeApplication {
    public static void main(String[] args) {
        SpringApplication.run(NotifymeApplication.class, args);
    }
}
