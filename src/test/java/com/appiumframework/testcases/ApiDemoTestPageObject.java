package com.appiumframework.testcases;

import java.io.IOException;

import org.testng.annotations.Test;

import com.appiumframework.base.Base;
import com.appiumframework.pageobjects.LandingPage;
import com.appiumframework.pageobjects.Preference;
import com.appiumframework.pageobjects.PreferencesDepedencies;
import com.appiumframework.testdata.TestData;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;

public class ApiDemoTestPageObject extends Base {
	
	
	@Test(dataProvider = "InputData",dataProviderClass = TestData.class)
	public void ApiDemoTest(String value) throws IOException {
		
		startAppiumServer();
		
		//get driver object from base class .return driver
		AndroidDriver<AndroidElement> driver = desiredcapability("ApiDemoApp");
		ExtentTest test = Base.getLog();
		
		 // creates a toggle for the given test, adds all log events under it
		 // ExtentTest test = extent.createTest("API Demo",
		//  "Demo API testing");
		  
		  // log(Status, details) 
		 // test.log(Status.INFO, "Selecting Country");
		
		LandingPage homepage = new LandingPage(driver);
		homepage.clickonpreference();
		//homepage.preference.click();
		
		Preference pref = new Preference(driver);
		pref.clickonpreferencedependencies();
		//pref.dependencies.click();
		
		
		PreferencesDepedencies pd = new PreferencesDepedencies(driver);
		test.log(Status.INFO, "Clicking on checkox");
		pd.checkbox.click();
		test.log(Status.INFO, "Clicking on wifi setting");
		pd.wifisetting.click();
		test.log(Status.INFO, "Typing on the textbox");
		pd.editbox.sendKeys(value);
		test.log(Status.INFO, "Clicking on button");
		pd.button.get(1).click();
		
		service.stop();
		
		
			


	}

}
