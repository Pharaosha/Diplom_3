package pageobject;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;


public class ForgotPasswordPagePOM {

    public ForgotPasswordPagePOM(WebDriver driver) {
        this.driver = driver;
    }

    private WebDriver driver;


    //                       ЛОКАТОРЫ
    private final By loginLinkButton = By.xpath("//a[normalize-space(text())='Войти']");

    //                       МЕТОДЫ
    public void clickLoginLinkButton() {
        driver.findElement(loginLinkButton).click();
    }
}
