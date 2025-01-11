package ui.pages;

import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;

public class HomePage extends UI_Base {

    public HomePage(Page page) {
        this.page = page;
        getURL();


    }

    public Locator getTitle() {
        return page.getByLabel("eBay Home");

    }
public Locator getFirstSearchResult(){
        return page.locator("#item5e661cbd1f > .s-item__wrapper > .s-item__image-section > .s-item__image > a > .s-item__image-wrapper");
}
    public Locator getSearchBox() {
        return page.locator("input[name='_nkw']");

    }

    public ElementHandle getFirstSearchedResult() {
        waitForVisibilityOfLocator(getFirstSearchResult(),10000);
        getFirstSearchResult().scrollIntoViewIfNeeded();
        return  page.waitForSelector("#item5e661cbd1f > .s-item__wrapper > .s-item__image-section > .s-item__image > a > .s-item__image-wrapper",new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE));
    }

    public ProductPage selectFirstProductFromSearchResults(String product) {
        getSearchBox().fill(product);
        getSearchBox().press("Enter");
        waitForVisibilityOfLocator(getFirstSearchResult(),10000);
        getFirstSearchedResult().click();
        return new ProductPage(page);
    }

}
