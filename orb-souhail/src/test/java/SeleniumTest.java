import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class SeleniumTest {

    private WebDriver driver;

    @BeforeAll
    public static void setupClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    public void setupTest() {
        ChromeOptions options = new ChromeOptions();

        // options.addArguments("--headless=new");

        // ✅ FORCE VISIBLE BROWSER
        options.addArguments("--start-maximized");

        driver = new ChromeDriver(options);
    }

    @Test
    public void openHomePage() {
        driver.get("http://localhost:8080/orb-souhail");
        System.out.println("Page title = " + driver.getTitle());
        Assertions.assertTrue(true);
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
