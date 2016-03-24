package importaddresslist;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
public class ImportAddressController {
    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job importAddressListJob;

    @RequestMapping(value = "/importaddress", method = RequestMethod.GET)
    public String index() {
        return "index";
    }

    @RequestMapping(value = "/importaddress", method = RequestMethod.POST)
    public String importAddress(Model model) throws Exception {
        JobParameters parameters = new JobParametersBuilder() //
                .addDate("timestamp", new Date(), true) // to allow repeated executions of the job
                .toJobParameters();
        JobExecution execution = jobLauncher.run(importAddressListJob, parameters);
        System.out.println("job execution: " + execution);
        model.addAttribute("execution", execution);
        return "result";
    }
}
