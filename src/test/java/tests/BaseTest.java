package tests;

import com.google.inject.Guice;
import com.google.inject.Injector;
import extentreports.ExtentTestManager;
import gherkin.lexer.De;
import keywords.Browser;
import keywords.Element;
import keywords.Verification;
import modules.DriverModule;
import modules.TestParameters;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.ITestResult;
import org.testng.annotations.*;
import pages.HomePage;
import pages.SignInPage;
import reportportal.Launch;
import reportportal.LaunchHandler;
import reportportal.SessionContext;
import webdriver.DriverFactory;
import webdriver.DriverManager;

import java.text.DecimalFormat;

public class BaseTest {

	protected WebDriver driver;
	protected Browser browserKeywords;
	protected Element elementKeywords;
	protected Verification verificationKeywords;
	protected HomePage homePage;
	protected SignInPage signInPage;
	protected Launch launch;
	protected static String browsers = "";

	@BeforeTest
	@Parameters({"browserName"})
	public void beforeTest(String browserName) {
		Injector injector = Guice.createInjector(new DriverModule());
		driver = DriverFactory.createInstance(browserName, new DesiredCapabilities());
		DriverManager.setDriver(driver);
		homePage = injector.getInstance(HomePage.class);
		signInPage = injector.getInstance(SignInPage.class);
		browserKeywords = injector.getInstance(Browser.class);
		elementKeywords = injector.getInstance(Element.class);
		verificationKeywords = injector.getInstance(Verification.class);
		launch = injector.getInstance(Launch.class);
	}

	@AfterMethod()
	public void afterMethod(ITestResult iTestResult) {
		browsers = browsers + DriverManager.getBrowserName() + "_";
		Class<?> clazz = iTestResult.getTestClass().getRealClass();
		if (SessionContext.getRpEnable())
		{
			launch.setAttributes("browser", browsers);
			launch.setAttributes("module", TestParameters.getModule(clazz));
			launch.setAttributes("priority", TestParameters.getPriority(clazz).name());
			launch.setAttributes("createdBy", TestParameters.getCreatedBy(clazz));
			LaunchHandler.updateLaunch(launch.getAttributes(), iTestResult.getMethod().getDescription());
		}
		ExtentTestManager.getTest().assignCategory(DriverManager.getBrowserName());
	}

	@AfterTest
	public void afterTest() {
		DriverManager.quit();
	}
}
