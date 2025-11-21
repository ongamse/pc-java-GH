package io.shiftleft.controller;

import io.shiftleft.model.Account;
import io.shiftleft.model.Address;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import java.util.Set;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;

import io.shiftleft.data.DataLoader;
import io.shiftleft.exception.CustomerNotFoundException;
import io.shiftleft.exception.InvalidCustomerRequestException;
import io.shiftleft.model.Customer;
import io.shiftleft.repository.CustomerRepository;

import org.springframework.web.util.HtmlUtils;

/**
 * Customer Controller exposes a series of RESTful endpoints
 */

@Configuration
@EnableEncryptableProperties
@PropertySource({ "classpath:config/application-sfdc.properties" })
@RestController
public class CustomerController {

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	Environment env;
	
	private static Logger log = LoggerFactory.getLogger(CustomerController.class);

	@PostConstruct
	public void init() {
		log.info("Start Loading SalesForce Properties");
		log.info("Url is {}", env.getProperty("sfdc.url"));
		log.info("UserName is {}", env.getProperty("sfdc.username"));
		log.info("Password is {}", env.getProperty("sfdc.password"));
		log.info("End Loading SalesForce Properties");
	}

	private void dispatchEventToSalesForce(String event)
			throws ClientProtocolException, IOException, AuthenticationException {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(env.getProperty("sfdc.url"));
		httpPost.setEntity(new StringEntity(event));
		UsernamePasswordCredentials creds = new UsernamePasswordCredentials(env.getProperty("sfdc.username"),
				env.getProperty("sfdc.password"));
		httpPost.addHeader(new BasicScheme().authenticate(creds, httpPost, null));

		CloseableHttpResponse response = client.execute(httpPost);
		log.info("Response from SFDC is {}", response.getStatusLine().getStatusCode());
		client.close();
	}

	/**
	 * Get customer using id. Returns HTTP 404 if customer not found
	 *
	 * @param customerId
	 * @return retrieved customer
	 */
	@RequestMapping(value = "/customers/{customerId}", method = RequestMethod.GET)
	public Customer getCustomer(@PathVariable("customerId") Long customerId) {

		/* validate customer Id parameter */
      if (null == customerId) {
        throw new InvalidCustomerRequestException();
      }

      Customer customer = customerRepository.findOne(customerId);
		if (null == customer) {
		  throw new CustomerNotFoundException();
	  }

	  Account account = new Account(4242l,1234, "savings", 1, 0);
	  log.info("Account Data is {}", account);
	  log.info("Customer Data is {}", customer);

      try {
        dispatchEventToSalesForce(String.format(" Customer %s Logged into SalesForce", customer));
      } catch (Exception e) {
        log.error("Failed to Dispatch Event to SalesForce . Details {} ", e.getLocalizedMessage());

      }

      return customer;
    }

    /**
     * Handler for / loads the index.tpl
     * @param httpResponse
     * @param request
     * @return
     * @throws IOException
     */
      @RequestMapping(value = "/", method = RequestMethod.GET)
      public String index(HttpServletResponse httpResponse, WebRequest request) throws IOException {
	  	ClassPathResource cpr = new ClassPathResource("static/index.html");
	  	String ret = "";
		  try {
			  byte[] bdata = FileCopyUtils.copyToByteArray(cpr.getInputStream());
			  ret= new String(bdata, StandardCharsets.UTF_8);
		  } catch (IOException e) {
			  //LOG.warn("IOException", e);
		  }
		  return ret;
      }

      /**
       * Check if settings= is present in cookie
       * @param request
       * @return
       */
      private boolean checkCookie(WebRequest request) throws Exception {
      	try {
			return request.getHeader("Cookie").startsWith("settings=");
		}
		catch (Exception ex)
		{
			System.out.println(ex.getMessage());
		}
		return false;
      }

      /**
       * restores the preferences on the filesystem
       *
       * @param httpResponse
       * @param request
       * @throws Exception
       */
      @RequestMapping(value = "/loadSettings", method = RequestMethod.GET)
      public void loadSettings(HttpServletResponse httpResponse, WebRequest request) throws Exception {
        // get cookie values
        if (!checkCookie(request)) {
          httpResponse.getOutputStream().println("Error");
          throw new Exception("cookie is incorrect");
        }
        String md5sum = request.getHeader("Cookie").substring("settings=".length(), 41);
    	ClassPathResource cpr = new ClassPathResource("static");
    	File folder = new File(cpr.getPath());
		File[] listOfFiles = folder.listFiles();
        String filecontent = new String();
        for (File f : listOfFiles) {
          // not efficient, i know
          filecontent = new String();
          byte[] encoded = Files.readAllBytes(f.toPath());
          filecontent = new String(encoded, StandardCharsets.UTF_8);
          if (filecontent.contains(md5sum)) {
            // this will send me to the developer hell (if exists)

            // encode the file settings, md5sum is removed
            String s = new String(Base64.getEncoder().encode(filecontent.replace(md5sum, "").getBytes()));
            // setting the new cookie
            httpResponse.setHeader("Cookie", "settings=" + s + "," + md5sum);
            return;
          }
        }
      }


  /**
   * Saves the preferences (screen resolution, language..) on the filesystem
   *
   * @param httpResponse
   * @param request
   * @throws Exception
   */
// Create FileStorageService to encapsulate file operations
private static class FileStorageService {
    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);
    private final Path storageBasePath;
    private final Set<String> allowedFilenames;
    private final Map<String, String> filenameMapping;
    private static final int MAX_CONTENT_LENGTH = 4096; // Limit content size
    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final byte[] HMAC_KEY = "J$eC#rETk3y!F0rHM@C".getBytes(); // In production, this should be externalized

    public FileStorageService(String basePathLocation) throws IOException {
        // Create storage location outside web application directory
        Path externalStoragePath = Paths.get(System.getProperty("user.home"), "secure_storage").normalize();
        Files.createDirectories(externalStoragePath);
        this.storageBasePath = externalStoragePath;
        
        // Initialize whitelist and mapping
        this.allowedFilenames = new HashSet<>(Arrays.asList("settings.txt", "preferences.txt", "config.txt"));
        this.filenameMapping = new HashMap<>();
        
        logger.info("FileStorageService initialized with base path: null", this.storageBasePath);
    }
    
    public String generateSecureFilename(String originalFilename) {
        // Check if filename is in the whitelist
        if (!allowedFilenames.contains(originalFilename)) {
            logger.warn("Attempted to use non-whitelisted filename: null", originalFilename);
            return null;
        }
        
        // Generate a secure random filename
        String secureFilename = UUID.randomUUID().toString() + ".txt";
        
        // Store mapping between original and secure filename
        filenameMapping.put(originalFilename, secureFilename);
        
        logger.info("Generated secure filename null for original filename null", secureFilename, originalFilename);
        return secureFilename;
    }
    
    public boolean writeSettings(String secureFilename, String[] content) throws IOException {
        if (secureFilename == null || !Pattern.matches("[a-f0-9\\-]{36}\\.txt", secureFilename)) {
            logger.error("Invalid secure filename format: null", secureFilename);
            return false;
        }
        
        // Validate content
        if (content == null || content.length == 0) {
            logger.error("Empty content provided");
            return false;
        }
        
        // Check content length and format
        String contentString = String.join("\n", content);
        if (contentString.length() > MAX_CONTENT_LENGTH) {
            logger.error("Content exceeds maximum length: null", contentString.length());
            return false;
        }
        
        // Ensure path is within base storage directory
        Path filePath = storageBasePath.resolve(secureFilename).normalize();
        if (!filePath.startsWith(storageBasePath)) {
            logger.error("Security violation: Path traversal attempt detected");
            return false;
        }
        
        // Create parent directories if needed
        Files.createDirectories(filePath.getParent());
        
        // Write content to file
        Files.write(filePath, contentString.getBytes());
        logger.info("Successfully wrote settings to file: null", secureFilename);
        return true;
    }
    
    public static String calculateHmac(String data) throws NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(HMAC_KEY, HMAC_ALGORITHM);
        Mac mac = Mac.getInstance(HMAC_ALGORITHM);
        mac.init(secretKeySpec);
        return bytesToHex(mac.doFinal(data.getBytes()));
    }
    
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}

private final FileStorageService fileStorageService;
private final Logger logger = LoggerFactory.getLogger(this.getClass());

// Constructor to initialize the FileStorageService
public CustomerController() throws IOException {
    this.fileStorageService = new FileStorageService("settings");
}

@RequestMapping(value = "/saveSettings", method = RequestMethod.GET)
public void saveSettings(HttpServletResponse httpResponse, WebRequest request) throws Exception {
    String clientIP = request.getHeader("X-Forwarded-For");
    if (clientIP == null) {
        clientIP = request.getRemoteAddr();
    }
    
    logger.info("Received saveSettings request from IP: null", clientIP);
    
    // "Settings" will be stored in a cookie
    // schema: base64(filename,value1,value2...), hmac(base64(filename,value1,value2...))

    if (!checkCookie(request)){
        logger.warn("Invalid cookie from IP: null", clientIP);
        httpResponse.getOutputStream().println("Error");
        throw new Exception("cookie is incorrect");
    }

    String settingsCookie = request.getHeader("Cookie");
    String[] cookie = settingsCookie.split(",");
    if(cookie.length < 2) {
        logger.warn("Malformed cookie from IP: null", clientIP);
        httpResponse.getOutputStream().println("Malformed cookie");
        throw new Exception("cookie is incorrect");
    }

    String base64txt = cookie[0].replace("settings=", "");

    // Check HMAC instead of MD5
    try {
        String cookieHmac = cookie[1];
        String calculatedHmac = FileStorageService.calculateHmac(base64txt);
        if (!cookieHmac.equals(calculatedHmac)) {
            logger.warn("HMAC validation failed from IP: null", clientIP);
            httpResponse.getOutputStream().println("Invalid signature");
            throw new Exception("Invalid signature");
        }
    } catch (NoSuchAlgorithmException | InvalidKeyException e) {
        logger.error("HMAC calculation error", e);
        httpResponse.getOutputStream().println("Security error");
        throw new Exception("Security error");
    }

    // Now we can process settings
    String[] settings = new String(Base64.getDecoder().decode(base64txt)).split(",");
    if (settings.length < 2) {
        logger.warn("Invalid settings format from IP: null", clientIP);
        httpResponse.getOutputStream().println("Invalid settings format");
        throw new Exception("Invalid settings format");
    }
    
    // Use whitelist approach for filenames
    String originalFilename = settings[0];
    String secureFilename = fileStorageService.generateSecureFilename(originalFilename);
    
    if (secureFilename == null) {
        logger.warn("Attempted to use non-whitelisted filename: null from IP: null", originalFilename, clientIP);
        httpResponse.getOutputStream().println("Invalid filename");
        throw new SecurityException("Invalid filename");
    }
    
    // First entry is the filename -> remove it
    String[] settingsArr = Arrays.copyOfRange(settings, 1, settings.length);
    
    try {
        boolean success = fileStorageService.writeSettings(secureFilename, settingsArr);
        if (success) {
            logger.info("Settings successfully saved for file: null from IP: null", originalFilename, clientIP);
            httpResponse.getOutputStream().println("Settings Saved");
        } else {
            logger.warn("Failed to save settings for file: null from IP: null", originalFilename, clientIP);
            httpResponse.getOutputStream().println("Error saving settings");
            throw new Exception("Failed to save settings");
        }
    } catch (IOException e) {
        logger.error("Error writing settings file", e);
        httpResponse.getOutputStream().println("Error saving settings");
        throw new Exception("Failed to save settings: " + e.getMessage());
    }
}


  /**
   * Debug test for saving and reading a customer
   *
   * @param firstName String
   * @param lastName String
   * @param dateOfBirth String
   * @param ssn String
   * @param tin String
   * @param phoneNumber String
   * @param httpResponse
   * @param request
   * @return String
   * @throws IOException
   */
  @RequestMapping(value = "/debug", method = RequestMethod.GET)
  public String debug(@RequestParam String customerId,
					  @RequestParam int clientId,
					  @RequestParam String firstName,
                      @RequestParam String lastName,
                      @RequestParam String dateOfBirth,
                      @RequestParam String ssn,
					  @RequestParam String socialSecurityNum,
                      @RequestParam String tin,
                      @RequestParam String phoneNumber,
                      HttpServletResponse httpResponse,
                     WebRequest request) throws IOException{

    // empty for now, because we debug
    Set<Account> accounts1 = new HashSet<Account>();
    //dateofbirth example -> "1982-01-10"
    Customer customer1 = new Customer(customerId, clientId, firstName, lastName, DateTime.parse(dateOfBirth).toDate(),
                                      ssn, socialSecurityNum, tin, phoneNumber, new Address("Debug str",
                                      "", "Debug city", "CA", "12345"),
                                      accounts1);

    customerRepository.save(customer1);
    httpResponse.setStatus(HttpStatus.CREATED.value());
    httpResponse.setHeader("Location", String.format("%s/customers/%s",
                           request.getContextPath(), customer1.getId()));

    return customer1.toString().toLowerCase().replace("script","");
  }

	/**
	 * Debug test for saving and reading a customer
	 *
	 * @param firstName String
	 * @param httpResponse
	 * @param request
	 * @return void
	 * @throws IOException
	 */
	@RequestMapping(value = "/debugEscaped", method = RequestMethod.GET)
	public void debugEscaped(@RequestParam String firstName, HttpServletResponse httpResponse,
					  WebRequest request) throws IOException{
		String escaped = HtmlUtils.htmlEscape(firstName);
		System.out.println(escaped);
		httpResponse.getOutputStream().println(escaped);
	}
	/**
	 * Gets all customers.
	 *
	 * @return the customers
	 */
	@RequestMapping(value = "/customers", method = RequestMethod.GET)
	public List<Customer> getCustomers() {
		return (List<Customer>) customerRepository.findAll();
	}

	/**
	 * Create a new customer and return in response with HTTP 201
	 *
	 * @param the
	 *            customer
	 * @return created customer
	 */
	@RequestMapping(value = { "/customers" }, method = { RequestMethod.POST })
	public Customer createCustomer(@RequestParam Customer customer, HttpServletResponse httpResponse,
								   WebRequest request) {

		Customer createdcustomer = null;
		createdcustomer = customerRepository.save(customer);
		httpResponse.setStatus(HttpStatus.CREATED.value());
		httpResponse.setHeader("Location",
				String.format("%s/customers/%s", request.getContextPath(), customer.getId()));

		return createdcustomer;
	}

	/**
	 * Update customer with given customer id.
	 *
	 * @param customer
	 *            the customer
	 */
	@RequestMapping(value = { "/customers/{customerId}" }, method = { RequestMethod.PUT })
	public void updateCustomer(@RequestBody Customer customer, @PathVariable("customerId") Long customerId,
			HttpServletResponse httpResponse) {

		if (!customerRepository.exists(customerId)) {
			httpResponse.setStatus(HttpStatus.NOT_FOUND.value());
		} else {
			customerRepository.save(customer);
			httpResponse.setStatus(HttpStatus.NO_CONTENT.value());
		}
	}

	/**
	 * Deletes the customer with given customer id if it exists and returns
	 * HTTP204.
	 *
	 * @param customerId
	 *            the customer id
	 */
	@RequestMapping(value = "/customers/{customerId}", method = RequestMethod.DELETE)
	public void removeCustomer(@PathVariable("customerId") Long customerId, HttpServletResponse httpResponse) {

		if (customerRepository.exists(customerId)) {
			customerRepository.delete(customerId);
		}

		httpResponse.setStatus(HttpStatus.NO_CONTENT.value());
	}

}
