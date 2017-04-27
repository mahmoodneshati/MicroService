package job;

import task.CoinAnalyticsServiceJob;
import task.CoinServiceJob;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by neshati on 2/7/2017.
 * Behpardaz
 */
public class JobScheduler {
    private static Properties prop = new Properties();

    private org.quartz.Scheduler scheduler;
    private String coinServiceJobPattern;
    private String CoinAnalyticsServiceJobPattern;

    public JobScheduler() {
        try {
            this.scheduler = new StdSchedulerFactory().getScheduler();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    public void start(){
        try {
            scheduler.start();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    public void addJobs(){
        try {
            registerJob(CoinServiceJob.class, "CoinServiceJob", "group1", coinServiceJobPattern);
            registerJob(CoinAnalyticsServiceJob.class, "CoinAnalyticsServiceJob", "group1", CoinAnalyticsServiceJobPattern);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    private String setConfigs() {
        try {

            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            InputStream input = loader.getResourceAsStream("config.properties");
            prop.load(input);
            coinServiceJobPattern = prop.getProperty("coinServiceJobPattern");
            CoinAnalyticsServiceJobPattern = prop.getProperty("CoinAnalyticsServiceJobPattern");

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



    public static void main(String[] args) throws SchedulerException {
        JobScheduler scheduler = new JobScheduler();
        scheduler.start();
        scheduler.setConfigs();
        scheduler.addJobs();
    }

    private void registerJob(Class job_class, String job_name, String job_group, String schedule_pattern) throws SchedulerException {
        JobDetail job = JobBuilder.newJob(job_class).withIdentity(job_name, job_group).build();
        Trigger trigger = TriggerBuilder
                .newTrigger()
                .withIdentity(job_name, job_group)
                .withSchedule(
                        CronScheduleBuilder.cronSchedule(schedule_pattern))
                .build();
        scheduler.scheduleJob(job, trigger);
    }
}
