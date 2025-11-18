import com.github.javafaker.Faker;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import pageobject.LoginPagePOM;
import pageobject.MainPagePOM;
import pageobject.PersonalAccountPagePOM;

import static org.hamcrest.Matchers.equalTo;

public class PersonalAccountTests {

    private WebDriver driver;
    private MainPagePOM mainPagePOM;
    private LoginPagePOM loginPagePOM;
    private PersonalAccountPagePOM personalAccountPagePOM;
    private static String accessToken;
    private BrowserFactory browserFactory;
    private UserData testUser;

    @BeforeEach
    public void setUp() {
        String browser = System.getProperty("browser", "chrome");
        browserFactory = new BrowserFactory();
        driver = browserFactory.getWebDriver(browser);
        mainPagePOM = new MainPagePOM(driver);
        loginPagePOM = new LoginPagePOM(driver);
        personalAccountPagePOM = new PersonalAccountPagePOM(driver);

        testUser = generateRandomUser();
        Response registerResponse = UserApi.createNewUser(testUser);
        registerResponse.then().statusCode(200).body("success", equalTo(true));
        Response loginResponse = UserApi.loginUser(testUser);
        accessToken = UserApi.extractAccessToken(loginResponse);
    }

    @AfterEach
    void closeBrowser() {
        if (driver != null) driver.quit();
    }

    @AfterAll
    @DisplayName("Удаление пользователя после тестов")
    public static void tearDown() {
        if (accessToken != null && !accessToken.isEmpty()) {
            UserApi.deleteUser(accessToken);
            accessToken = null;
        }
    }

    private UserData generateRandomUser() {
        Faker faker = new Faker();
        return new UserData(
                faker.internet().emailAddress(),
                faker.internet().password(8, 12),
                faker.name().firstName()
        );
    }

    @Test
    @DisplayName("Переход в личный кабинет с динамическим пользователем и проверка успешного входа")
    public void createUserAndNavigateToPersonalAccount() {
        mainPagePOM.openMainPage();
        mainPagePOM.clickPersonalAccountButton();
        loginPagePOM.enterEmail(testUser.getEmail());
        loginPagePOM.enterPassword(testUser.getPassword());
        loginPagePOM.submitLogin();


        personalAccountPagePOM.waitForProfileHeader();
        Assertions.assertTrue(
                personalAccountPagePOM.isProfileHeaderVisible(),
                "Личный кабинет не открылся после логина"
        );
    }

    @Test
    @DisplayName("Переход из личного кабинета в конструктор с динамическим пользователем и проверка перехода")
    public void navigateToBuilder() {
        mainPagePOM.openMainPage();
        loginPagePOM.openLoginPage();
        loginPagePOM.enterEmail(testUser.getEmail());
        loginPagePOM.enterPassword(testUser.getPassword());
        loginPagePOM.submitLogin();

        mainPagePOM.clickPersonalAccountButton();
        personalAccountPagePOM.waitForConstructorButton();
        personalAccountPagePOM.clickConstructorButton();

        personalAccountPagePOM.waitForBuilderHeader();
        Assertions.assertTrue(personalAccountPagePOM.isBuilderHeaderVisible(),
                "Переход в конструктор не произошел");

    }

    @Test
    @DisplayName("Выход из личного кабинета через кнопку 'Выйти' с динамическим пользователем и проверка выхода")
    public void logoutFromPersonalAccount() {
        mainPagePOM.openMainPage();
        loginPagePOM.openLoginPage();
        loginPagePOM.enterEmail(testUser.getEmail());
        loginPagePOM.enterPassword(testUser.getPassword());
        loginPagePOM.submitLogin();

        mainPagePOM.clickPersonalAccountButton();
        personalAccountPagePOM.waitForLogoutButton();
        personalAccountPagePOM.clickLogoutButton();

        mainPagePOM.waitForLoginButton(); // Ждем появления кнопки "Войти"
        Assertions.assertTrue(mainPagePOM.isLoginButtonDisplayed(), "Пользователь не вышел из личного кабинета!");
    }
}