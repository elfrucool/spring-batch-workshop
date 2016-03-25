package importaddresslist;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Controller
public class ImportAddressController {
    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job importAddressListJob;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @RequestMapping(value = "/importaddress", method = RequestMethod.GET)
    public String index(Model model) {
        model.addAttribute("contacts", loadContacts());
        return "index";
    }

    private List<Contact> loadContacts() {
        return jdbcTemplate.query(
                "SELECT name,email,phone FROM contacts ORDER BY name",
                (rs, rowNum) -> {
                    Contact contact = new Contact();
                    contact.setName(rs.getString("name"));
                    contact.setEmail(rs.getString("email"));
                    contact.setPhone(rs.getString("phone"));
                    return contact;
                }
        );
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
