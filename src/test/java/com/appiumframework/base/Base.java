package com.appiumframework.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.URL;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.io.FileHandler;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeTest;

import com.appiumframework.reports.ExtentListeners;
import com.appiumframework.utilities.ExcelReader;
import com.aventstack.extentreports.ExtentTest;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.service.local.AppiumDriverLocalService;

public class Base {
	

	public static ExcelReader excel = new ExcelReader(".\\src\\test\\resources\\testdata\\testdata.xlsx");
	public static AndroidDriver<AndroidElement> driver;

	public static AppiumDriverLocalService service;
	public static String screenshotName;

	// incase of port error
	// taskkill /F /IM node.exe
	public static AppiumDriverLocalService startAppiumServer() {
		boolean flag = checkIfServerIsRunning(4723);
		// not started start it
		if (!flag) {
			service = AppiumDriverLocalService.buildDefaultService();
			service.start();
		}
		return service;
	}

	@BeforeTest
	public static void KillAllNodesAndInitReport() throws IOException {

		Runtime.getRuntime().exec("taskkill /F /IM node.exe");
		wait(5);

		
	}

	public static void startEmulator() throws IOException {
		// run bat file on the fly
		Runtime.getRuntime().exec(
				System.getProperty("user.dir") + "\\src\\test\\resources\\emulator\\emulator1.bat");
		wait(6);
	}

	// to check is appium server running
	public static boolean checkIfServerIsRunning(int port) {

		boolean isServerRunning = false;
		ServerSocket serverSocket;
		try {
			serverSocket = new ServerSocket(port);
			serverSocket.close();
		} catch (IOException e) {
			// port is in use
			isServerRunning = true;

		} finally {
			serverSocket = null;

		}

		return isServerRunning;

	}

	public static AndroidDriver<AndroidElement> desiredcapability(String appName) throws IOException {

		FileInputStream file = new FileInputStream(
				System.getProperty("user.dir") + "\\src\\test\\resources\\properties\\config.properties");
		Properties prop = new Properties();
		prop.load(file);

		// DesiredCapabilities set up with device name ad app location
		DesiredCapabilities cap = new DesiredCapabilities();
		String device=(String) prop.get("emulatorDevice");
		//gets value on runtime from mvn command
		//mvn test -DdeviceName=Emulator
		//String device = System.getProperty("deviceName");
		if (device.contains("Emulator")) {
			
			startEmulator();
		}
		cap.setCapability(MobileCapabilityType.DEVICE_NAME, device);
		/*
		 * if (device.equals("emulator")) { // startEmulator startEmulator();
		 * cap.setCapability(MobileCapabilityType.DEVICE_NAME,
		 * prop.getProperty("emulatorDevice")); } else if (device.equals("real")) {
		 * cap.setCapability(MobileCapabilityType.DEVICE_NAME,
		 * prop.getProperty("realDevice")); }
		 */
		// location of chrome driver
		// cap.setCapability("chromedriverExecutable", System.getProperty("user.dir") +
		// "\\ChromeDriver\\chromedriver.exe");
		cap.setCapability(MobileCapabilityType.AUTOMATION_NAME, "uiautomator2");
		// Timeout set
		cap.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, 10);
		cap.setCapability(MobileCapabilityType.APP,
				System.getProperty("user.dir") + "\\AppToTest\\" + prop.getProperty(appName));

		// get file using absolute path
		// cap.setCapability(MobileCapabilityType.APP,fs.getAbsolutePath());

		// Connect to server
		driver = new AndroidDriver<>(new URL("http://127.0.0.1:4723/wd/hub"), cap);
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		return driver;

	}

	// wait for element
	public static void waitForElement(int time, String id) {

		WebDriverWait wait = new WebDriverWait(driver, time);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(id)));

	}

	// scroll " + containedText + "
	public static void scroll(String TextValue) {
		driver.findElementByAndroidUIAutomator(
				"new UiScrollable(new UiSelector()).scrollIntoView(text(\"" + TextValue + "\"))");
	}

	// scroll select and click
	public static void scrollandClick(String TextValue) {
		driver.findElementByAndroidUIAutomator(
				"new UiScrollable(new UiSelector()).scrollIntoView(text(\"" + TextValue + "\"))");
		driver.findElementByAndroidUIAutomator("text(\"" + TextValue + "\")").click();
	}

	// Application related method
	public static void selectProductAndAddToCart(String id, String product, String TextValue) {

		int count = driver.findElementsById(id).size();

		for (int i = 0; i < count; i++) {

			String productName = driver.findElementsById(id).get(i).getText();

			if (productName.equalsIgnoreCase(product)) {
				driver.findElementsById(id).get(i).click();
				// ADD TO CART
				driver.findElementByAndroidUIAutomator("text(\"" + TextValue + "\")").click();
				break;

			}

		}

	}

	// conversion to double
	public static double getAmount(String value) {

		double amountValue = Double.parseDouble(value);
		return amountValue;

	}

	// wait
	public static void wait(int time) {
		try {
			Thread.sleep(time * 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// take screeshots with testname
	public static void takeScreenShot(String testName) throws IOException {

		// get screenshot
		File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		// copy file to destination folder
		FileUtils.copyFile(srcFile, new File(System.getProperty("user.dir") + "\\Screenshots\\" + testName + ".png"));

	}

	/*
	 * @AfterMethod // runs after every testcases // after test is finished
	 * ItestResults has all the information
	 * 
	 * public void tearDownMethod(ITestResult result) throws IOException {
	 * 
	 * 
	 * // caputure screenshot on failure if (result.getStatus() ==
	 * ITestResult.FAILURE) {
	 * 
	 * 
	 * test.log(Status.FAIL, result.getThrowable());
	 * test.fail("Test Failed",MediaEntityBuilder.createScreenCaptureFromPath(
	 * captureScreenshot()).build());
	 * 
	 * } }
	 */

	/*
	 * @AfterTest public void endreport() {
	 * 
	 * extent.flush();
	 * 
	 * }
	 */

	public static String captureScreenshot1() throws IOException {

		String screenshotpath = System.getProperty("user.dir") + "\\Screenshots\\" + getCurrentDateTime() + ".png";
		File srecFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

		// copy file to destination folder
		// FileUtils.copyFile(srecFile, new File(screenshotpath));

		File DestFile = new File(screenshotpath);
		try {
			FileHandler.copy(srecFile, DestFile);
		} catch (IOException e) {
			System.out.println("Unable to capture screenshot" + e.getMessage());
			e.printStackTrace();
		}

		return screenshotpath;

	}

	public static String getCurrentDateTime() {

		// DateFormat customFormat = new SimpleDateFormat("yyyy_MM_dd_HHmmss");
		Date currentDate = new Date();
		return currentDate.toString().replace(":", "_").replace(" ", "_");

	}
	
	public static void captureScreenshot() {

		// File scrFile = ((TakesScreenshot)
		// DriverManager.getDriver()).getScreenshotAs(OutputType.FILE);
		File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

		Date d = new Date();
		screenshotName = d.toString().replace(":", "_").replace(" ", "_") + ".jpg";

		try {
			FileUtils.copyFile(scrFile, new File(System.getProperty("user.dir") + "\\reports\\" + screenshotName));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	//To get log 
	public static ExtentTest getLog() {
		
		ExtentTest test =ExtentListeners.testReport.get().info("Test Started");
		
		return test;
		
	}

}
