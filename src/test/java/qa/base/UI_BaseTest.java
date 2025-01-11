package qa.base;

import ui.factory.PlaywrightFactory;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import ui.pages.Add2CartPage;
import ui.pages.HomePage;
import ui.pages.ProductPage;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import java.nio.file.Path;
import java.util.Properties;

public abstract class UI_BaseTest {

	public Page page;
	public BrowserContext browserContext;
	protected Properties prop;
	protected HomePage homePage;
	protected ProductPage productPage;
	protected Add2CartPage add2CartPage;
	public ThreadLocal<PlaywrightFactory> tlplaywrightFactory = new ThreadLocal<PlaywrightFactory>();

	public PlaywrightFactory getplaywrightFactory() {
		return tlplaywrightFactory.get();
	}

	@Parameters({ "browser" })
	@BeforeMethod(alwaysRun = true)
	public void setup(@Optional("chrome") String browserName, ITestResult result) {
		tlplaywrightFactory.set(new PlaywrightFactory());
		prop = getplaywrightFactory().initProp();
		if (browserName != null) {
			prop.setProperty("browser", browserName);
		}
		page = getplaywrightFactory().initBrowser(prop);
		page.setViewportSize(1920, 1080);
		result.setAttribute("playwrightFactory", getplaywrightFactory());
	}

	@AfterMethod
	public void tearDown() {
		Path path = Path.of(System.getProperty("user.dir") + "/screenshots/" + System.currentTimeMillis() + ".png");
		page.screenshot(new Page.ScreenshotOptions().setPath(path));
		if (getplaywrightFactory().getPlaywright() != null) {
			getplaywrightFactory().getPlaywright().close();
		}


	}


}
