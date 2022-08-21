package ui.rozetka;

import com.codeborne.selenide.*;
import io.qameta.allure.Description;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


import static com.codeborne.selenide.CollectionCondition.*;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class AddToCartButtonTest {

    private int checkResultsTile(String keyWord, ElementsCollection searchResults) {
        int i = 0;
        for (SelenideElement result : searchResults) {
            String searchResultTitle = result.getText();
            if (searchResultTitle.toLowerCase().contains(keyWord.toLowerCase())) {
                i++;
            }
        }
        return i;
    }


    @BeforeClass
    private void startTest() {
        Configuration.holdBrowserOpen = true;
        Configuration.browserSize = "1920x1080";
        Selenide.open("https://rozetka.com.ua/");
    }

    @Test
    @Description("* Search for a product \n * Verify that the search result matches the search keyword \n * Open the PDP " + "\n * Verify that the 'Add to cart' button is active")
    public void addToCartButtonIsActive() {
        SelenideElement searchField = $(By.xpath("//input[@name='search']"));
        SelenideElement searchButton = $(By.xpath("//button[contains(@class, 'search-form__submit')]"));
        searchField.shouldBe(visible).setValue("GORENJE WP");
        searchButton.shouldBe(visible).click();

        ElementsCollection searchResults = $$(By.xpath("//span[contains(translate(., 'GORENJE', 'Gorenje'),'Gorenje')]"));
        searchResults.shouldHave(sizeGreaterThanOrEqual(7));
        int matchingResultsQty = checkResultsTile("GORENJE", searchResults);
        Assert.assertEquals(matchingResultsQty, 18);

        $(By.xpath("//*[contains(@class, 'catalog-grid__cell')][1]")).shouldBe(visible).click();

        Assert.assertTrue($(By.xpath("//button[contains(@class, 'buy-button')]"))
                .shouldBe(visible)
                .isDisplayed());
    }
}
