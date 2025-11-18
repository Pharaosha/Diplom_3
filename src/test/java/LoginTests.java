import pageobject.ForgotPasswordPagePOM;
import pageobject.LoginPagePOM;
import pageobject.MainPagePOM;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import com.github.javafaker.Faker;

public class LoginTests {

    private WebDriver driver;
    private MainPagePOM mainPagePOM;
    private LoginPagePOM loginPagePOM;
    private ForgotPasswordPagePOM forgotPasswordPagePOM;
    private BrowserFactory browserFactory;
    private static String accessToken;
    private static UserData testUser;

    @BeforeEach
    void setUp() {
        String browser = System.getProperty("browser", "chrome"); // chrome по умолчанию
        browserFactory = new BrowserFactory();
        driver = browserFactory.getWebDriver(browser);
        mainPagePOM = new MainPagePOM(driver);
        forgotPasswordPagePOM = new ForgotPasswordPagePOM(driver);
    }

    @BeforeEach
    void createTestUser() {

        Faker faker = new Faker();
        String email = faker.internet().emailAddress();
        String password = faker.internet().password(8, 12);
        String name = faker.name().firstName();

        testUser = new UserData(email, password, name);

        Response createResponse = UserApi.createNewUser(testUser);
        Assertions.assertEquals(200, createResponse.getStatusCode(), "Пользователь не был создан через API");

        Response loginResponse = UserApi.loginUser(testUser);
        accessToken = UserApi.extractAccessToken(loginResponse);
        Assertions.assertNotNull(accessToken, "accessToken не должен быть null");
    }

    @AfterEach
    void closeBrowser() {
        if (driver != null) driver.quit();

        if (accessToken != null) {
            UserApi.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Вход через кнопку 'Войти в аккаунт' на главной")
    void loginFromMainButton() {
        driver.get("https://stellarburgers.education-services.ru");
        mainPagePOM.clickLoginButtonMain();

        loginPagePOM = new LoginPagePOM(driver);
        loginPagePOM.waitForLoginPage();
        loginPagePOM.enterEmail(testUser.getEmail());
        loginPagePOM.enterPassword(testUser.getPassword());
        loginPagePOM.clickLoginButton();
    }

    @Test
    @DisplayName("Вход через кнопку Личный кабинет")
    void loginFromPersonalAccountButton() {
        driver.get("https://stellarburgers.education-services.ru");
        mainPagePOM.clickPersonalAccountButton();

        loginPagePOM = new LoginPagePOM(driver);
        loginPagePOM.waitForLoginPage();
        loginPagePOM.enterEmail(testUser.getEmail());
        loginPagePOM.enterPassword(testUser.getPassword());
        loginPagePOM.clickLoginButton();
    }

    @Test
    @DisplayName("Вход через кнопку Восстановить пароль")
    void loginFromResetPasswordButton() {
        driver.get("https://stellarburgers.education-services.ru/login");

        loginPagePOM = new LoginPagePOM(driver);
        loginPagePOM.waitForLoginPage();
        loginPagePOM.clickResetPasswordButton();
        forgotPasswordPagePOM = new ForgotPasswordPagePOM(driver);

        forgotPasswordPagePOM.clickLoginLinkButton();
        loginPagePOM.enterEmail(testUser.getEmail());
        loginPagePOM.enterPassword(testUser.getPassword());
        loginPagePOM.clickLoginButton();
    }
}