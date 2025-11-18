import pageobject.*;
import pageobject.ConstructorPagePOM;
import io.qameta.allure.Step;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import com.github.javafaker.Faker;


public class ConstructorSectionNavigationTests {

    private WebDriver driver;
    private ConstructorPagePOM constructorPagePOM;
    private static String accessToken;
    private MainPagePOM mainPagePOM;
    private LoginPagePOM loginPagePOM;
    private BrowserFactory browserFactory;
    private UserData userData;

    @BeforeEach
    void setUp() {

        Faker faker = new Faker();
        String randomEmail = faker.internet().emailAddress();
        String randomPassword = faker.internet().password(8, 12);
        String randomName = faker.name().firstName();
        userData = new UserData(randomEmail, randomPassword, randomName);

        String browser = System.getProperty("browser", "chrome");
        browserFactory = new BrowserFactory();
        driver = browserFactory.getWebDriver(browser);
        mainPagePOM = new MainPagePOM(driver);
        constructorPagePOM = new ConstructorPagePOM(driver);

        loginUser(userData);
        openConstructor();
    }

    @AfterEach
    void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    @AfterAll
    @DisplayName("Удаление пользователя после тестов")
    public static void tearDown() {
        if (accessToken != null && !accessToken.isEmpty()) {
            UserApi.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Переход в раздел 'Булки'")
    public void navigateToBunsTest() {
        constructorPagePOM.clickBunsTab();
        Assertions.assertTrue(constructorPagePOM.isTabActive("Булки"), "Вкладка 'Булки' должна быть активна");
    }

    @Test
    @DisplayName("Переход в раздел 'Соусы'")
    public void navigateToSaucesTest() {
        constructorPagePOM.clickSaucesTab();
        Assertions.assertTrue(constructorPagePOM.isTabActive("Соусы"), "Вкладка 'Соусы' должна быть активна");
    }

    @Test
    @DisplayName("Переход в раздел 'Начинки'")
    public void navigateToFillingsTest() {
        constructorPagePOM.clickFillingsTab();
        Assertions.assertTrue(constructorPagePOM.isTabActive("Начинки"), "Вкладка 'Начинки' должна быть активна");
    }

    @Step("Логинимся под пользователем")
    private void loginUser(UserData userData) {
        driver.get("https://stellarburgers.education-services.ru/login");
        loginPagePOM = new LoginPagePOM(driver);
        loginPagePOM.enterEmail(userData.getEmail());
        loginPagePOM.enterPassword(userData.getPassword());
        loginPagePOM.clickLoginButton();
    }

    @Step("Открываем конструктор")
    private void openConstructor() {
        mainPagePOM.clickConstructorButton();
    }

}