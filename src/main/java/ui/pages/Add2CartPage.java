package ui.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import java.util.stream.IntStream;

public class Add2CartPage extends UI_Base {
    public Add2CartPage(Page page) {
        this.page = page;
    }
    public Locator getAddedProduct(){
        return page.locator("//a[@data-test-id='cart-item-link']");
    }
    public boolean isProductAddedToCart(String productName){
        int count = getAddedProduct().count();


        boolean isProductFound = IntStream.range(0, count)
                .mapToObj(getAddedProduct()::nth)
                .anyMatch(element -> {
                    String textContent = element.textContent();
                    return textContent != null && textContent.contains(productName);
                });

       return isProductFound;
    }
}
