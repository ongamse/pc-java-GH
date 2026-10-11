package io.shiftleft.data;

import io.shiftleft.model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import io.shiftleft.model.Customer;


import io.shiftleft.repository.CustomerRepository;
import io.shiftleft.repository.PatientRepository;

@Component
@Configuration
@PropertySource({ "classpath:config/application-aws.properties", "classpath:config/application-mysql.properties",
    "classpath:config/application-sfdc.properties" })
public class DataLoader implements CommandLineRunner {

  private static Logger log = LoggerFactory.getLogger(DataLoader.class);

  @Autowired
  private DataBuilder dataBuilder;

  @Autowired
  Environment env;

  @Autowired
  private CustomerRepository customerRepository;

  @Autowired
  private PatientRepository patientRepository;

  private boolean connectToAws() {

    log.info("Start Loading AWS Properties");
    log.info("AWS credentials configured: {}",
        env.getProperty("aws.accesskey", "").length() > 0 && env.getProperty("aws.secretkey", "").length() > 0);
    log.info("AWS Bucket is {}", env.getProperty("aws.bucket"));
    log.info("End Loading AWS Properties");

    // Connect to AWS resources and do something

    return true;
  }

  private boolean connectToMySQL() {

    log.info("Start Loading MySQL Properties");
    log.info("Url is {}", env.getProperty("db.url"));
    log.info("UserName is {}", env.getProperty("db.username"));
    log.info("Database password configured: {}", env.getProperty("db.password", "").length() > 0);
    log.info("End Loading MySQL Properties");

    // Connect to DB MySQL resources and do something

    return true;
  }

  @Override
  public void run(String... arg0) throws Exception {

    connectToMySQL();

    connectToAws();

    log.debug("Loading test data...");
    for (Customer customer : dataBuilder.createCustomers()) {
      customerRepository.save(customer);
    }
    for (Patient patient : dataBuilder.createPatients()) {
      patientRepository.save(patient);
    }
    log.debug("Test data loaded...");
  }
}
