package ui.pages;

import com.machinezoo.noexception.Exceptions;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Request;
import com.microsoft.playwright.Response;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.microsoft.playwright.options.WaitUntilState;
import ui.utility.ConfigReader;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;

import java.util.function.Predicate;

@Slf4j
public class UI_Base {
	protected Page page;
	public void getURL() {
		page.navigate(ConfigReader.url, new Page.NavigateOptions().setTimeout(90000));
	}
	public String getPageTitle() {
		String title = page.title();
		System.out.println("page title is : " + title);
		return title;
	}
	public void selectDropDownWithPartialText(Locator locator, String partialText) {
		String value = locator.locator("option:has-text('" + partialText + "')").textContent();
		locator.selectOption(value);
	}

	public void selectDropDownValue(Locator locator, String value) {
		Assert.assertTrue(locator.locator("option").allTextContents().contains(value),
				value + " - Value not present in the drop down.");
		locator.selectOption(value);
	}

	public void selectDropDownValueWithWait(Locator locator, String value) {
		page.waitForTimeout(3000);
		selectDropDownValue(locator, value);
	}

	public Page switchToNewTab(Locator locator) {
		return page.waitForPopup(locator::click);
	}

	public void closeCurrentTab() {
		page.close();
	}

	public void reloadPage() {
		page.reload(new Page.ReloadOptions()
				.setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
	}

	public void switchToParentWindow() {
		page.bringToFront();
	}

	public void acceptAlert() {
		/*
		 * page.onceDialog(dialog -> {
		 * log.info(String.format("Dialog message: %s", dialog.message()));
		 * dialog.accept(); });
		 */
	}

	public void dismissAlert() {
		page.onceDialog(dialog -> {
			log.info(String.format("Dialog message: %s", dialog.message()));
			dialog.dismiss();
		});
	}

	public void waitForInVisibilityOfLocator(Locator locator) {
		locator.waitFor(new Locator.WaitForOptions()
				.setState(WaitForSelectorState.HIDDEN));
	}

	public void waitForVisibilityOfLocator(Locator locator) {
		Exceptions.silence()
				.run(() -> locator.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE)));

	}

	public void waitForVisibilityOfLocator(Locator locator, int timeout) {
		Exceptions.silence().run(() -> locator
				.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(timeout)));

	}

	public void waitForClickablityOfLocator(Locator locator) {
		locator.waitFor(new Locator.WaitForOptions()
				.setState(WaitForSelectorState.VISIBLE));
		locator.waitFor(new Locator.WaitForOptions()
				.setState(WaitForSelectorState.ATTACHED));
	}

	public void waitForDropDownToLoad(Locator locator) {
		page.waitForCondition(() -> {
			int optionsCount = locator.locator("option").count();
			return optionsCount > 1;
		});
	}

	public void clickConsentToCookies() {
		Exceptions.silence().run(() -> page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Accept"))
				.click(new Locator.ClickOptions().setTimeout(5000)));
	}

	public void waitForRequestToFinish(String requestEndPoint) {
		page.waitForRequestFinished(new Page.WaitForRequestFinishedOptions().setPredicate(new Predicate<Request>() {

			@Override
			public boolean test(Request t) {
				return t.url().contains(requestEndPoint);
			}
		}), new Runnable() {
			@Override
			public void run() {
				log.info(requestEndPoint.toUpperCase() + "-API Load Completed");
			}
		});

	}

	public void waitForResponse(String requestEndPoint) {
		Exceptions.silence().run(() -> page.waitForResponse(new Predicate<Response>() {

			@Override
			public boolean test(Response response) {
				return response.url().contains(requestEndPoint) && response.status() == 200;
			}
		}, new Page.WaitForResponseOptions().setTimeout(10000), new Runnable() {

			@Override
			public void run() {
				log.info(requestEndPoint.toUpperCase() + "-API Load Completed");
			}
		}));
	}
	
	public void navigateTOURL(String URL) {
		page.navigate(URL);
		page.waitForLoadState();
	}
}
