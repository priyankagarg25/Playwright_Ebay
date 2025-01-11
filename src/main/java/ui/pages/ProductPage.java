package ui.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class ProductPage extends UI_Base {
    public ProductPage(Page page) {
        this.page = page;
    }

    public String getProductPageTitle() {
        return page.title();
    }

    public Locator getBuyItNowButton() {
        return page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Buy It Now").setExact(true));
    }

    public Locator getAdd2CartButton() {
        return page.getByTestId("x-atc-action").getByTestId("ux-call-to-action");
    }
public Locator getCartProductCount(){
        return page.locator("#//*[@id='gh-cart-n']");
}
    public Locator getYourCartLink() {
        return page.locator("//a[starts-with(@aria-label,'Your shopping cart contains')]");
    }
    public Locator getProductName() {
        return page.getByTestId("x-item-title").getByRole(AriaRole.HEADING);
    }
public String storedProductName(){
        waitForVisibilityOfLocator(getProductName(),5000);
      return getProductName().textContent();
}
    public void verifyProductPage() {
        waitForClickablityOfLocator(getBuyItNowButton());
        assertThat(getBuyItNowButton()).isVisible();
    }

    public void addProductToCart() {
        waitForClickablityOfLocator(getAdd2CartButton());
        getAdd2CartButton().scrollIntoViewIfNeeded();
        getAdd2CartButton().click();
    }
public int addedProductCountInCart(){
  return Integer.parseInt(getCartProductCount().textContent());
}
    public Add2CartPage redirectToCartPage() {
        waitForClickablityOfLocator(getYourCartLink());
        getYourCartLink().click();
        return new Add2CartPage(page);
    }

}
