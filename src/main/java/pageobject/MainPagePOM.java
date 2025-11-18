package pageobject;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;



public class MainPagePOM {

    public MainPagePOM(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    private WebDriver driver;
    private WebDriverWait wait;

    //                       ЛОКАТОРЫ

    private final By loginButtonMain = By.xpath("//button[normalize-space(text())='Войти в аккаунт']");

    private final By loginButton = By.cssSelector(
            "button.button_button__33qZ0.button_button_type_primary__1O7Bx.button_button_size_medium__3zxIa");

    private final By personalAccountButton = By.xpath("//p[normalize-space(text())='Личный Кабинет']");

    private final By constructorButton = By.xpath("//p[contains(text(),'Конструктор')]");;

    private By constructorPageHeader = By.xpath("//h1[contains(text(),'Конструктор')]");


    //                      МЕТОДЫ

    public void clickLoginButtonMain() {
        driver.findElement(loginButtonMain).click();
    }

    public void clickPersonalAccountButton() {
        driver.findElement(personalAccountButton).click();
    }

    public void clickConstructorButton() {
        driver.findElement(constructorButton).click();
    }

    public void openMainPage() {
        driver.get("https://stellarburgers.education-services.ru");
    }

    public boolean isConstructorPageVisible() {
        try {
            return driver.findElement(constructorPageHeader).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void waitForLoginButton() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(loginButton));
    }

    // Проверка отображения кнопки "Войти"
    public boolean isLoginButtonDisplayed() {
        try {
            return driver.findElement(loginButton).isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }
}
