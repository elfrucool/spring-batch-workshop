package importaddresslist;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Date;

@Component
public class ScheduledService {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job importAddressListJob;

    @Scheduled(fixedRate =  10000) // every 10 seconds
    public void importAddressList() throws Exception {
        Date now = new Date();
        System.out.println("simulating i'm importing the address list, date/time is:" + now);

        if (new File("work/inbound/contacts.csv").exists()) {
            JobParameters parameters = //
                    new JobParametersBuilder() //
                    .addDate("timestamp", now, true) // to allow repeated executions of the job
                    .toJobParameters();

            JobExecution execution = jobLauncher.run(importAddressListJob, parameters);
            System.out.println("job execution: " + execution);
        } else {
            System.out.println("there was no file to be processed, so nothing done.");
        }
    }

}
