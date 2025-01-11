package ui.factory;

import com.microsoft.playwright.*;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
@Slf4j
public class PlaywrightFactory {

	Playwright playwright;
	Browser browser;
	BrowserContext browserContext;
	Properties prop;
	List<String> startMax = new ArrayList<>();
	private Page currentPage;

	/**
	 * ThreadLocal Implementation
	 */
	private ThreadLocal<Playwright> tlPlaywright = new ThreadLocal<>();
	private ThreadLocal<Browser> tlBrowser = new ThreadLocal<>();
	private ThreadLocal<BrowserContext> tlBrowserContext = new ThreadLocal<>();
	private ThreadLocal<Page> tlPage = new ThreadLocal<>();

	public Playwright getPlaywright() {
		return tlPlaywright.get();
	}

	public Browser getBrowser() {
		return tlBrowser.get();
	}

	public BrowserContext getBrowserContext() {
		return tlBrowserContext.get();
	}

	public Page getPage() {
		return tlPage.get();
	}

	public Page getCurrentPage() {
		return currentPage;
	}

	public Page getNewTabPage() {
		currentPage = getPage().context().newPage();
		return currentPage;
	}

	public void setCurrentPage(Page page) {
		this.currentPage = page;
	}

	public void closeCurrentPage() {
		currentPage.close();
	}

	public void resetPage() {
		tlPlaywright.remove();
		tlBrowser.remove();
		tlBrowserContext.remove();
		tlPage.remove();
	}

	/**
	 * Initialize Browser of your input choice
	 */
	public Page initBrowser(Properties prop) {

		String browserName = prop.getProperty("browser").trim();
		log.info("Browser : " + browserName);
		startMax.add("--start-maximized");
		tlPlaywright.set(Playwright.create());
		boolean headless = prop.getProperty("headless").equals("true");
		log.info("Headless Mode : " + headless);
		switch (browserName.toLowerCase()) {

		case "chromium":
			tlBrowser.set(getPlaywright().chromium()
					.launch(new BrowserType.LaunchOptions().setHeadless(headless).setArgs(startMax)));
			break;

		case "firefox":
			tlBrowser.set(getPlaywright().firefox()
					.launch(new BrowserType.LaunchOptions().setHeadless(headless).setArgs(startMax)));
			break;

		case "safari":
			tlBrowser.set(getPlaywright().webkit().launch(new BrowserType.LaunchOptions().setHeadless(headless)));
			break;

		case "chrome":
			tlBrowser.set(getPlaywright().chromium().launch(
					new BrowserType.LaunchOptions().setChannel("chrome").setHeadless(headless).setArgs(startMax)));
			break;

		case "edge":
			tlBrowser.set(getPlaywright().chromium()
					.launch(new BrowserType.LaunchOptions().setChannel("msedge").setHeadless(headless)));
			break;

		default:
			log.info("Enter correct browser type");
			break;
		}
		tlBrowserContext.set(getBrowser().newContext(new Browser.NewContextOptions().setViewportSize(1280, 720)));
		tlPage.set(getBrowserContext().newPage());
		getPage().setDefaultNavigationTimeout(90000);
		if (prop.getProperty("consolelog").equals("true")) {
			getPage().onConsoleMessage(msg -> {
				if (msg.type().equals("error")) {
					log.info("Console message: " + msg.type() + "  " + msg.text());
				}
			});
		}
		getPage().onDialog(dialog -> {
			log.info("Dialog message: " + dialog.message());
			dialog.accept(); // or dialog.dismiss();
		});
		return getPage();
	}

	/**
	 * Method to initialize properties from config file
	 */
	public Properties initProp() {
		try {
			FileInputStream ip = new FileInputStream("./src/test/resources/config/config.properties");
			prop = new Properties();
			try {
				prop.load(ip);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return prop;

	}

}
