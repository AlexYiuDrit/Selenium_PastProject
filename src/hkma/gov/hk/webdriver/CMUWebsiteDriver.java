package hkma.gov.hk.webdriver;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.v112.network.Network;
import hkma.gov.hk.entity.ResponseData;

public class CMUWebsiteDriver extends WebDriverUtils implements LoginPageInterface {
	public CMUWebsiteDriver(String driverPath) {
		super(driverPath);
		devTools = ((ChromeDriver) driver).getDevTools();
		devTools.createSession();
		devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));

	}
	
	@Override
	public void navigatePage(String url) throws TimeoutException {}
	
	@Override
	public String loginGetSessionID(String verificationCode) throws InterruptedException { return null; }
	
	@Override
	public String loginGetSessionID(String email, String password) throws InterruptedException {
		login(email, password);
		return extractPhpSessionID();
	}
	
	public ArrayList<ResponseData<String>> containsError(Set<String> urls, String url) {
		ArrayList<ResponseData<String>> errorResponses = new ArrayList<>();
		devTools.addListener(Network.requestWillBeSent(), req -> {
			if (req.getRedirectHasExtraInfo() && req.getRequest().getUrl().endsWith("pagenotfound.html")) {
				errorResponses.add(new ResponseData<String>(404, req.getRedirectResponse().get().getUrl()));
//				String redirectedURL = req.getRequest().getUrl();
//				System.out.println(redirectedURL + ": " + req.getRedirectResponse().get().getUrl());
			}
		});
		devTools.addListener(Network.responseReceived(), res -> {
			String responseURL = res.getResponse().getUrl();
			if (!urls.contains(responseURL)) {
				int statusCode = res.getResponse().getStatus();
				if (statusCode < 200 || statusCode > 299) {
					errorResponses.add(new ResponseData<String>(statusCode, responseURL));
				}
				urls.add(responseURL);
			}
		});
		try {
			driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(10));
			driver.get(url);
		} catch (TimeoutException ex) {
			return null;
		} finally {
			devTools.clearListeners();
		}
		return errorResponses;
	}
	
	public String getPageSource(String url) throws TimeoutException {
		driver.get(url);
		return driver.getPageSource().split("\\r?\\n")[0];
	}
	
	private void login(String email, String password) throws InterruptedException {
		driver.get("https://website.com");
	    driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));
	    WebElement emailBar = driver.findElement(By.id("email"));
	    WebElement pwdBar = driver.findElement(By.id("pass"));
	    emailBar.sendKeys(email);
	    pwdBar.sendKeys(password);
	    while (driver.getCurrentUrl().equals("https://website.com")) {
	        solveCaptcha();
	    }
	}
	
	@SuppressWarnings("resource")
	private void solveCaptcha() {
		WebElement captchaField = driver.findElement(By.id("captchaCode"));
		WebElement captchaRefreshButton = driver.findElement(By.xpath("//img[contains(@class, 'captcha_refresh_image')]"));
		WebElement loginButton = driver.findElement(By.cssSelector("#form > div.button-row > a.btn.btn-orange.form_submit"));
		captchaField.clear();
        captchaRefreshButton.click();
		Scanner scanner = new Scanner(System.in);
        System.out.print("Press enter to the captcha: ");
        String answer = scanner.nextLine();
        captchaField.sendKeys(answer);     
        loginButton.click();
	}
	
	private String extractPhpSessionID() {
		String phpSessionID = driver.manage().getCookieNamed("PHPSESSID").toString();
		phpSessionID = phpSessionID.substring(phpSessionID.indexOf("PHPSESSID"), phpSessionID.indexOf(";"));
		return phpSessionID;
	}
}
