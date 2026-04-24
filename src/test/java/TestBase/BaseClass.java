package TestBase;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Platform;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;

public class BaseClass {
    public static WebDriver driver; // Static to ensure same instance across listeners
    public Logger logger;
    public Properties p;

    @BeforeClass(groups= {"sanity","regression","master"})
    @Parameters({"os", "browser"})
    public void setUp(String os, String br) throws IOException {
        
        try {
            // 1. Initialize Logger
            logger = LogManager.getLogger(this.getClass()); 

            // 2. Loading config file
            FileReader file = new FileReader("./src/test/resources/config.properties");
            p = new Properties();
            p.load(file);
            
            String executionEnv = p.getProperty("execution_env");

            // 3. Environment Setup (Remote vs Local)
            if(executionEnv.equalsIgnoreCase("remote")) {
                DesiredCapabilities capabilities = new DesiredCapabilities();
                
                // Set Platform (OS)
                switch(os.toLowerCase()) {
                    case "windows": capabilities.setPlatform(Platform.WINDOWS); break;
                    case "linux":   capabilities.setPlatform(Platform.LINUX); break;
                    case "mac":     capabilities.setPlatform(Platform.MAC); break;
                    default:        logger.error("No matching OS"); return;
                }
                
                // Set Browser
                switch(br.toLowerCase()) {
                    case "chrome": capabilities.setBrowserName("chrome"); break;
                    case "edge":   capabilities.setBrowserName("MicrosoftEdge"); break;
                    case "firefox": capabilities.setBrowserName("firefox"); break;
                    default:       logger.error("No matching browser"); return;
                }
                
                driver = new RemoteWebDriver(new URL("http://localhost:4444"), capabilities);
                
            } else if(executionEnv.equalsIgnoreCase("local")) {
                // Initialize local drivers
                switch(br.toLowerCase()) {
                    case "chrome" : driver = new ChromeDriver(); break;
                    case "edge"   : driver = new EdgeDriver(); break;
                    case "firefox": driver = new FirefoxDriver(); break;
                    default       : logger.error("Invalid browser name.."); return;
                }
            }

            // 4. Common Browser Configurations
            if (driver != null) {
                driver.manage().deleteAllCookies();
                driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
                driver.manage().window().maximize();
                
                String url = p.getProperty("appURL2");
                if(url != null) {
                    driver.get(url);
                } else {
                    logger.error("appURL2 not found in config.properties");
                }
            }
        } catch (Exception e) {
            // Capture and print any setup errors to the console
            System.err.println("Setup failed! Printing stack trace:");
            e.printStackTrace();
            throw e; // Rethrow to ensure TestNG marks it as a Configuration Failure
        }
    }

    @AfterClass(groups= {"sanity","regression","master"})
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
    
    // --- Utility Methods ---
    
    public String randomString() {
        return RandomStringUtils.randomAlphabetic(5);
    }

    public String randomNumber() {
        return RandomStringUtils.randomNumeric(10);
    }

    public String randomAlphaNumeric() {
        return (RandomStringUtils.randomAlphabetic(3) + "@" + RandomStringUtils.randomNumeric(3));
    }

    // --- Screenshot Capture for Extent Reports ---
    public String captureScreen(String tname) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
        TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
        File sourceFile = takesScreenshot.getScreenshotAs(OutputType.FILE);

        String targetFilePath = System.getProperty("user.dir") + File.separator + "screenshots" + File.separator + tname + "_" + timeStamp + ".png";
        File targetFile = new File(targetFilePath);

        // Ensure directories exist
        targetFile.getParentFile().mkdirs();
        
        // Use copy for reliability over renameTo
        java.nio.file.Files.copy(sourceFile.toPath(), targetFile.toPath());

        return targetFilePath;
    }
}
