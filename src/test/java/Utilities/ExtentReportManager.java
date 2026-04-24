package Utilities;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List; // Added import for List

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import TestBase.BaseClass; 

public class ExtentReportManager implements ITestListener {
    public ExtentSparkReporter sparkReporter;
    public ExtentReports extent;
    public ExtentTest test;

    String repName;

    public void onStart(ITestContext testContext) {
        
        /* 
        SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
        Date dt = new Date();
        String currentdatetimestamp = df.format(dt); 
        */

        // Generate timestamp for the report name
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        repName = "Test-Report-" + timeStamp + ".html";

        // Specify location of the report
        sparkReporter = new ExtentSparkReporter(".\\reports\\" + repName);

        sparkReporter.config().setDocumentTitle("opencart Automation Report"); 
        sparkReporter.config().setReportName("opencart Functional Testing"); 
        sparkReporter.config().setTheme(Theme.DARK);

        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
        
        // --- System Information ---
        extent.setSystemInfo("Application", "opencart");
        extent.setSystemInfo("Module", "Admin");
        extent.setSystemInfo("User Name", System.getProperty("user.name"));
        extent.setSystemInfo("Environment", "QA");

        // Fetching OS and Browser from testng.xml (Correct Placement)
        String os = testContext.getCurrentXmlTest().getParameter("os");
        extent.setSystemInfo("Operating System", os);

        String browser = testContext.getCurrentXmlTest().getParameter("browser");
        extent.setSystemInfo("Browser", browser);

        List<String> includedGroups = testContext.getCurrentXmlTest().getIncludedGroups();
        if(!includedGroups.isEmpty()) {
            extent.setSystemInfo("Groups", includedGroups.toString());
        }
    }

    public void onTestSuccess(ITestResult result) {
        test = extent.createTest(result.getTestClass().getName());
        test.assignCategory(result.getMethod().getGroups()); 
        test.log(Status.PASS, result.getName() + " got successfully executed");
    }

    public void onTestFailure(ITestResult result) {
        test = extent.createTest(result.getTestClass().getName());
        test.assignCategory(result.getMethod().getGroups());
        
        test.log(Status.FAIL, result.getName() + " got failed");
        test.log(Status.INFO, result.getThrowable().getMessage());

        try {
            String imgPath = new BaseClass().captureScreen(result.getName());
            test.addScreenCaptureFromPath(imgPath);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public void onTestSkipped(ITestResult result) {
        test = extent.createTest(result.getTestClass().getName());
        test.assignCategory(result.getMethod().getGroups());
        test.log(Status.SKIP, result.getName() + " got skipped");
        test.log(Status.INFO, result.getThrowable().getMessage());
    }

   public void onFinish(ITestContext testContext) {
    
    extent.flush();

 //open report automatically  not compulsory
    String pathOfExtentReport = System.getProperty("user.dir")+"\\reports\\"+repName;
    File extentReport = new File(pathOfExtentReport);

    try {
        Desktop.getDesktop().browse(extentReport.toURI());
    } catch (IOException e) {
        e.printStackTrace();
    }

// send the report automatically  -don't use many times 1 or 2 times
/*
try {
    URL url = new URL("file:///"+System.getProperty("user.dir")+"\\reports\\"+repName);

    // Create the email message
    ImageHtmlEmail email = new ImageHtmlEmail();
    email.setDataSourceResolver(new DataSourceUrlResolver(url));
    email.setHostName("smtp.googlemail.com");
    email.setSmtpPort(465);
    email.setAuthenticator(new DefaultAuthenticator("pavanoltraining@gmail.com", "password"));
    email.setSSLOnConnect(true);
    email.setFrom("pavanoltraining@gmail.com"); //Sender
    email.setSubject("Test Results");
    email.setMsg("Please find Attached Report....");
    email.addTo("pavankumar.busyqa@gmail.com"); //Receiver
    email.attach(url, "extent report", "please check report...");
    email.send(); // send the email
} catch (Exception e) {
    e.printStackTrace();
}*/


    }
}
