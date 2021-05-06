package be.xplore.notify.me.services.scheduled;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SchedulerService {

    private final ThreadPoolTaskScheduler scheduler;
    private final EmailScheduledService emailScheduledService;
    private final String cronExpression;

    public SchedulerService(
            @Qualifier("threadPoolTaskScheduler") ThreadPoolTaskScheduler scheduler,
            EmailScheduledService emailScheduledService,
            @Value("${notify.me.scheduled.email.cron:0 12 * * * 0}") String cronExpression
    ) {
        this.cronExpression = cronExpression;
        this.scheduler = scheduler;
        this.emailScheduledService = emailScheduledService;
        scheduleEmails();
    }

    private void scheduleEmails() {
        scheduler.schedule(emailScheduledService, new CronTrigger(cronExpression));
    }

}
