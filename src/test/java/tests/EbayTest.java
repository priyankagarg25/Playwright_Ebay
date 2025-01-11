package tests;

import ui.pages.HomePage;
import qa.base.UI_BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.testng.Assert.assertTrue;

@Slf4j
public class EbayTest extends UI_BaseTest {

    @Test(/*retryAnalyzer = reRun.Retry.class*/)
    public void amazontest() {
        homePage = new HomePage(page);
        assertThat(homePage.getTitle()).containsText("eBay Home");
        productPage = homePage.selectFirstProductFromSearchResults("book");
        productPage.verifyProductPage();
        String productName = productPage.storedProductName();
        productPage.addProductToCart();
        assertTrue(productPage.addedProductCountInCart()==1);
        add2CartPage = productPage.redirectToCartPage();
        assertTrue(add2CartPage.isProductAddedToCart(productName),"product is not added to the cart");

    }
}

