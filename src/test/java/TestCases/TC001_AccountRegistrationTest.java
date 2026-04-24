package TestCases;

import org.testng.Assert;
import org.testng.annotations.Test;

import PageObject.AccountRegistrationPage;
import PageObject.HomePage;
import TestBase.BaseClass;

public class TC001_AccountRegistrationTest extends BaseClass {
	
	@Test(groups= {"sanity","master"})
	public void verify_Account_Registrarion() {
		logger.info("**** Started TC001_AccountRegistrationTest ****");
		try {
		// 1. Initialize Home Page and navigate to Registration
		HomePage hp = new HomePage(driver);
		
		hp.clickMyAccount();
		logger.info("click on my account");
		hp.clickRegister();
		logger.info("click on Register");
		
		// 2. Initialize Registration Page
		AccountRegistrationPage regpage = new AccountRegistrationPage(driver);
		logger.info("Providing customer details");
		// 3. Pass dynamic random data to fields
		regpage.setFirstName(randomString().toUpperCase());
		regpage.setLastName(randomString().toUpperCase());
		
		regpage.setEmail(randomString() + "@gmail.com");
		
		regpage.setTelephone(randomNumber());
		
		String password = randomAlphaNumeric();
		regpage.setPassword(password);
		regpage.setConfirmPassword(password);
		
		// 4. Submit the form
		regpage.setPrivacyPolicy();
		regpage.clickContinue();
		logger.info("validation information");
		// 5. Validation
		String confmsg = regpage.getConfirmationMsg();
		//intentionally fail testcases
		/*
		if(confmsg.contentEquals("Your Account Has Been Created!!")) {
			Assert.assertTrue(true);
		}
		else {
			logger.error("Test failed......");
			logger.debug("Test Debug......");
			Assert.assertTrue(false);
		}
		*/
		Assert.assertEquals(confmsg, "Your Account Has Been Created!");
	}
		catch(Exception e) {
			logger.error("Test failed......");
			//logger.debug("Test Debug......");
			Assert.fail();
		}
		logger.info("**** Finished TC001_AccountRegistrationTest ****");
	}
}
