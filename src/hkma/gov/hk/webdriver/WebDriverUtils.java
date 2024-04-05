package hkma.gov.hk.webdriver;

import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.DevTools;

public abstract class WebDriverUtils {
	
	protected WebDriver driver;
	protected DevTools devTools;
	
	public WebDriverUtils(String driverPath) {
		System.setProperty("webdriver.chrome.driver", driverPath);
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--ignore-ssl-errors=yes");
        options.addArguments("--ignore-certificate-errors");
        this.driver = new ChromeDriver(options);
	}
	
	public abstract void navigatePage(String url) throws TimeoutException;
	
	public void terminate() {
		if (driver != null) {
			driver.quit();
		}
	};
}
