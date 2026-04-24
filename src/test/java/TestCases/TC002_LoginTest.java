package TestCases;

import org.testng.Assert;
import org.testng.annotations.Test; // Added Import

import PageObject.HomePage;
import PageObject.LoginPage;
import PageObject.MyAccountPage;
import TestBase.BaseClass;

public class TC002_LoginTest extends BaseClass {

	@Test (groups= {"regression","master"})
	public void verify_Login() {
		logger.info("**** Starting TC002_LoginTest ****");
		try {
		
			// HomePage
			HomePage hp = new HomePage(driver);
			hp.clickMyAccount(); // Adjusted name to match your TC001 usage
			hp.clickLogin();
			
			// LoginPage
			LoginPage lp = new LoginPage(driver);
			lp.setEmail(p.getProperty("email")); // Ensure key matches config.properties
			lp.setPassword(p.getProperty("password")); // Ensure key matches config.properties
			lp.clickLogin();
			
			// MyAccountPage
			MyAccountPage macc = new MyAccountPage(driver);
			boolean targetpage = macc.isMyAccountPageExist();
			
			Assert.assertTrue(targetpage); 
		}
		catch(Exception e) {
			logger.error("Login Test Failed: " + e.getMessage());
			Assert.fail();
		}
		logger.info("**** Finished TC002_LoginTest ****");
	}
}
