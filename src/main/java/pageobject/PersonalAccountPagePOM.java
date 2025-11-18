package pageobject;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
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
    private By profileHeader = By.xpath("//p[contains(text(),'Личный Кабинет')]");
    private By loginButton = By.xpath("//button[contains(text(),'Войти')]");
    private By builderHeader = By.xpath("//p[contains(text(),'Конструктор') and contains(@class,'AppHeader_header__linkText')]");




    //                       МЕТОДЫ
    public void clickConstructorButton() {
        driver.findElement(constructorButton).click();
    }

    public void waitForProfileHeader() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(profileHeader));
    }

    public boolean isProfileHeaderVisible() {
        try {
            return driver.findElement(profileHeader).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void clickLogoutButton() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.elementToBeClickable(logoutButton));
        driver.findElement(logoutButton).click();
    }

    public void waitForConstructorButton() {
        By constructorButton = By.xpath("//button[contains(., 'Конструктор')] | //a[contains(., 'Конструктор')]");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.until(ExpectedConditions.visibilityOfElementLocated(constructorButton));
        wait.until(ExpectedConditions.elementToBeClickable(constructorButton));
    }

    public void waitForLogoutButton() {
        By logoutButton = By.xpath("//button[contains(., 'Выход')]");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.until(ExpectedConditions.visibilityOfElementLocated(logoutButton));
        wait.until(ExpectedConditions.elementToBeClickable(logoutButton));
    }

    public void verifyLogout() {
        By loginButton = By.xpath("//button[text()='Войти'] | //a[text()='Войти']");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.until(ExpectedConditions.visibilityOfElementLocated(loginButton));
    }

    public void waitForLoginButton() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(loginButton));
    }

    public boolean isConstructorPageVisible() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.visibilityOf((WebElement) constructorButton));
            return ((WebElement) constructorButton).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean isLoginButtonVisible() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.visibilityOf((WebElement) loginButton));
            return ((WebElement) loginButton).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    public void waitForBuilderHeader() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(builderHeader));
    }

    public boolean isBuilderHeaderVisible() {
        try {
            return driver.findElement(builderHeader).isDisplayed();
        } catch (TimeoutException | org.openqa.selenium.NoSuchElementException e) {
            return false;
        }
    }
}

