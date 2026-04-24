package TestCases;

import org.testng.Assert;
import org.testng.annotations.Test;

import PageObject.HomePage;
import PageObject.LoginPage;
import PageObject.MyAccountPage;
import TestBase.BaseClass;
import Utilities.DataProviders;

public class TC003_LoginDDT extends BaseClass {

	@Test(dataProvider = "LoginData", dataProviderClass = DataProviders.class,groups= {"DataDriven"})//getting data dataprovider from another package
	public void verify_LoginDDT(String email, String pwd, String exp) {

		logger.info("**** Starting TC003_LoginDDT ****");

		try {
		    // 1. Navigate to Login
		    HomePage hp = new HomePage(driver);
		    hp.clickMyAccount(); 
		    hp.clickLogin();

		    // 2. Perform Login
		    LoginPage lp = new LoginPage(driver);
		    lp.setEmail(email);
		    lp.setPassword(pwd);
		    lp.clickLogin();

		    // 3. Check Result
		    MyAccountPage macc = new MyAccountPage(driver);
		    boolean targetpage = macc.isMyAccountPageExist();

		    if (exp.equalsIgnoreCase("Valid")) {
		        if (targetpage == true) {
		            macc.clickLogout(); // Cleanup for next iteration
		            Assert.assertTrue(true);
		        } else {
		            Assert.fail("Login failed for valid credentials");
		        }
		    }

		    if (exp.equalsIgnoreCase("Invalid")) {
		        if (targetpage == true) {
		            macc.clickLogout(); // Logged in when shouldn't have
		            Assert.fail("Login succeeded for invalid credentials");
		        } else {
		            // Login failed as expected
		            Assert.assertTrue(true);
		        }
		    }
		} catch (Exception e) {
		    Assert.fail("An unexpected exception occurred: " + e.getMessage());
		}

		logger.info("**** Finished TC003_LoginDDT ****");
	}
}
