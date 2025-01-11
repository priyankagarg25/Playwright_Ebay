package ui.listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import ui.factory.PlaywrightFactory;
import com.microsoft.playwright.Page;
import lombok.extern.slf4j.Slf4j;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.Reporter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Date;
@Slf4j
public class ExtentReportListener implements ITestListener {
	private static final String REPORT_OUTPUT_FOLDER = "./reports/";
	private static final String SCREENSHOT_OUTPUT_FOLDER = "./screenshots/";
	private static final String FILE_NAME = "TestExecutionReport.html";
	private static ExtentReports extent = init();

	public static ThreadLocal<ExtentTest> test = new ThreadLocal<ExtentTest>();

	private static ExtentReports init() {
		Path path = Paths.get(REPORT_OUTPUT_FOLDER);
		Path pathScreenShot = Paths.get(SCREENSHOT_OUTPUT_FOLDER);
		if (!Files.exists(path)) {
			try {
				Files.createDirectories(path);
			} catch (IOException e) {
				log.info(e.getMessage());
			}
		}
		if (!Files.exists(pathScreenShot)) {
			try {
				Files.createDirectories(pathScreenShot);
			} catch (IOException e) {
				log.info(e.getMessage(),true);
			}
		}
	/*	try {
			FileUtils.cleanDirectory(new File(System.getProperty("user.dir")+"/screenshots"));
		} catch (IOException e) {
			log.info(e.getMessage());
		}*/
		ExtentReports extentReports = new ExtentReports();
		ExtentSparkReporter reporter = new ExtentSparkReporter(
				REPORT_OUTPUT_FOLDER + FILE_NAME);
		reporter.config().setReportName("Test Automation Results Report");
		extentReports.attachReporter(reporter);
		extentReports.setSystemInfo("System", System.getProperty("os.name"));
		extentReports.setSystemInfo("Author", "Priyanka Bansal");
		extentReports.setSystemInfo("Build#", "1.1");
		extentReports.setSystemInfo("Team", "QA");
		extentReports.setSystemInfo("Project", "Playwrite Test");

		return extentReports;
	}

	@Override
	public synchronized void onStart(ITestContext context) {
		log.info("Test Suite Started",true);
	}

	@Override
	public synchronized void onFinish(ITestContext context) {
		log.info("Test Suite Finsihed",true);
		extent.flush();
		test.remove();
	}

	@Override
	public synchronized void onTestStart(ITestResult result) {
		String methodName = result.getMethod().getMethodName();
		String qualifiedName = result.getMethod().getQualifiedName();
		int last = qualifiedName.lastIndexOf(".");
		int mid = qualifiedName.substring(0, last).lastIndexOf(".");
		String className = qualifiedName.substring(mid + 1, last);

		log.info(methodName + " Started",true);

		ExtentTest extentTest = extent.createTest(
				result.getMethod().getMethodName(),
				result.getMethod().getDescription());
		extentTest.assignCategory(result.getTestContext().getSuite().getName());
		extentTest.assignCategory(className);
		test.set(extentTest);
		test.get().getModel().setStartTime(getTime(result.getStartMillis()));
	}

	@Override
	public synchronized void onTestSuccess(ITestResult result) {
		log.info(result.getMethod().getMethodName() + " passed",true);
		PlaywrightFactory playwrightFactory = (PlaywrightFactory) result.getAttribute("playwrightFactory");
		test.get().pass("Test Passed");
		for (String s : Reporter.getOutput()) {
			test.get().info(s);
		}
		Reporter.clear();
		test.get().getModel().setEndTime(getTime(result.getEndMillis()));
		playwrightFactory.getPlaywright().close();
		playwrightFactory.resetPage();
	}

	@Override
	public synchronized void onTestFailure(ITestResult result) {
		log.info(result.getMethod().getMethodName() + " failed",true);
		PlaywrightFactory playwrightFactory = (PlaywrightFactory) result.getAttribute("playwrightFactory");
		Page page = playwrightFactory.getPage();
		Page currentPage = playwrightFactory.getCurrentPage();
		if (currentPage != null && !currentPage.isClosed()) {
			page=currentPage;
		}
		test.get().fail(result.getThrowable(), MediaEntityBuilder
				.createScreenCaptureFromPath(takeScreenshot(page))
				.build());
		test.get().getModel().setEndTime(getTime(result.getEndMillis()));
		for (String s : Reporter.getOutput()) {
			test.get().info(s);
		}
		Reporter.clear();
		playwrightFactory.getPlaywright().close();
		playwrightFactory.resetPage();
	}

	@Override
	public synchronized void onTestSkipped(ITestResult result) {
		log.info(result.getMethod().getMethodName() + " skipped",true);
		PlaywrightFactory playwrightFactory = (PlaywrightFactory) result.getAttribute("playwrightFactory");
		Page page = null;
		if (playwrightFactory != null) {
			page = playwrightFactory.getPage();
			Page currentPage = playwrightFactory.getCurrentPage();
			if (currentPage != null && !currentPage.isClosed()) {
				page = currentPage;
			}
			test.get().skip(result.getThrowable(),
					MediaEntityBuilder.createScreenCaptureFromPath(takeScreenshot(page)).build());
			playwrightFactory.getPlaywright().close();
			playwrightFactory.resetPage();
		} else {
			test.get().skip(result.getThrowable());
		}
		test.get().getModel().setEndTime(getTime(result.getEndMillis()));
		for (String s : Reporter.getOutput()) {
			test.get().info(s);
		}
		Reporter.clear();
	}

	@Override
	public synchronized void onTestFailedButWithinSuccessPercentage(
			ITestResult result) {
		log.info("onTestFailedButWithinSuccessPercentage"
				+ result.getMethod().getMethodName(),true);
	}

	private Date getTime(long millis) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(millis);
		return calendar.getTime();
	}

	/**
	 * Method to take screenshot
	 */
	public String takeScreenshot(Page page) {
		String path = "";
		if (page != null) {
			path = System.getProperty("user.dir") + "/screenshots/" + System.currentTimeMillis() + ".png";
			page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(path)).setFullPage(true));
		}else {
			log.error("Page is null, cannot take screenshot",true);
		}
		return path;
	}

}
