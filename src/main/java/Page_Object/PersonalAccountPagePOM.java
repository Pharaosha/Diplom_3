package Page_Object;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class PersonalAccountPagePOM {

    public PersonalAccountPagePOM(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    private WebDriver driver;
    private WebDriverWait wait;

    //                       ЛОКАТОРЫ
    private By constructorButton = By.xpath("//button[contains(., 'Конструктор')] | //a[contains(., 'Конструктор')]");
    private By logoutButton = By.xpath("//button[text()='Выход']");


    //                       МЕТОДЫ
    public void clickConstructorButton() {
        driver.findElement(constructorButton).click();
    }

    public void clickLogoutButton() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.elementToBeClickable(logoutButton));
        driver.findElement(logoutButton).click();
    }

}

