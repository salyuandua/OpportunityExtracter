import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;

import static org.quartz.CronScheduleBuilder.*;


import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;


public class ReminderSchedler {
public static void main(String[] args) {
	try {
		Scheduler scheduler=StdSchedulerFactory.getDefaultScheduler();
		JobDetail jobDetail=newJob(GrantReminderJob.class).
				withIdentity("GrantReminderJob", "reminderJobs").build();
		
		Trigger trigger=newTrigger().withIdentity("GrantReminderTrigger", "reminderTrigger").
				startNow().withSchedule(dailyAtHourAndMinute(22, 02)).build();
		scheduler.scheduleJob(jobDetail, trigger);
		scheduler.start();
	} catch (SchedulerException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}
}
