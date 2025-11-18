package pageobject;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class ConstructorPagePOM {

    private WebDriver driver;
    private WebDriverWait wait;

    public ConstructorPagePOM(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // Локаторы
    private By bunsTabLocator = By.xpath("//span[text()='Булки']");
    private By saucesTabLocator = By.xpath("//span[text()='Соусы']");
    private By fillingsTabLocator = By.xpath("//span[text()='Начинки']");

    // МЕТОДЫ
    private void clickElementJS(By locator) {
        WebElement element = driver.findElement(locator);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    public void clickBunsTab() {
        clickElementJS(bunsTabLocator);
    }

    public void clickSaucesTab() {
        clickElementJS(saucesTabLocator);
    }

    public void clickFillingsTab() {
        clickElementJS(fillingsTabLocator);
    }

    public boolean isTabActive(String tabName) {
        String activeClass = "tab_tab_type_current"; // пример класса активной вкладки, уточни в браузере
        WebElement tab = driver.findElement(By.xpath("//span[text()='" + tabName + "']/ancestor::div[contains(@class,'tab')]"));
        return tab.getAttribute("class").contains(activeClass);
    }

}