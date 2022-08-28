package ui.rozetka;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import io.qameta.allure.Description;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

public class LoginForm {

    @BeforeClass
    private void start() {
        Selenide.open("https://rozetka.com.ua/");
        Configuration.browserSize = "1920x1080";
//        Configuration.holdBrowserOpen = true;
    }

    @Test
    @Description("* Click the account login button \n * Verify that the login form is opened \n " +
            "* Verify that the all required fields are presented \n * Verify that the 'Login' button is presented")
    public void loginFormTest(){
        $(By.xpath("//rz-user")).shouldBe(visible).click();

        Assert.assertTrue($(By.xpath("//div[contains(@class, 'modal__holder')]"))
                .shouldBe(visible)
                .isDisplayed());

        Assert.assertTrue($(By.xpath("//form[contains(@class, 'auth-modal__form')]"))
                .shouldBe(visible)
                .isDisplayed());

        Assert.assertTrue($("#auth_email")
                .shouldBe(visible)
                .isDisplayed());

        Assert.assertTrue($("#auth_pass")
                .shouldBe(visible)
                .isDisplayed());

        Assert.assertTrue($(By.xpath("//button[contains(@class, 'auth-modal__submit')]"))
                .shouldBe(visible)
                .isDisplayed());
    }
}
