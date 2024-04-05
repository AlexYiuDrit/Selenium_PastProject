package hkma.gov.hk.test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import hkma.gov.hk.entity.ResponseData;
import hkma.gov.hk.utils.ConfigProperties;
import hkma.gov.hk.utils.HttpsAPIGateway;
import hkma.gov.hk.utils.JSONUtils;
import hkma.gov.hk.webdriver.CMUWebsiteDriver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CMUWebsiteTest {
	private static CMUWebsiteDriver webPage;
	private static String phpSessionID, chromeDriverLocation, email, password;
	
	private static Set<String> urls = new HashSet<>();
	
	private void testGetURL(String url, boolean isAPI) throws Exception {
		Map<String, String> param = new HashMap<>();
	    ResponseData<String> result = HttpsAPIGateway.getResult(url, param, HttpsAPIGateway.method_get, phpSessionID);
	    String body = result.getResponseBody();
	    assertEquals(result.getStatusCode(), 200);
	    if (isAPI) {
	        assertTrue(JSONUtils.isValidJSON(body));
	    } else {
	    	boolean validHTML = body.toLowerCase().indexOf("<!doctype html") == 0 || body.toLowerCase().indexOf("<!doctype html") == 1;
	    	if (!validHTML) {
	    		System.out.println("PHPErr: " + url);
	    	}
	    	assertTrue(validHTML);
	    }
	}
	
	private static void init() {
		chromeDriverLocation = ConfigProperties.getInstance().getConfig("chromeDriver.location");
		email = ConfigProperties.getInstance().getConfig("account.email");
		password = ConfigProperties.getInstance().getConfig("account.password");
	}
	
	@BeforeAll
    public static void setup() throws InterruptedException, IOException {
		init();
		webPage = new CMUWebsiteDriver(chromeDriverLocation);
		phpSessionID = webPage.loginGetSessionID(email, password);
    }
	
	@ParameterizedTest
	@CsvFileSource(resources = {
			"/data/pageURL_tc.csv",
			"/data/pageURL_sc.csv",
			"/data/pageURL_en.csv",
			"/data/loginPageURL_tc.csv",
			"/data/loginPageURL_sc.csv",
			"/data/loginPageURL_en.csv",
	}, numLinesToSkip = 1)

	public void testAllPage(String url) throws Exception {
		testGetURL(url, false);
		ArrayList<ResponseData<String>> errorURLs = webPage.containsError(urls, url);
		try {
			if (!errorURLs.isEmpty()) {
				for (int i = 0; i < errorURLs.size(); i++) {
					System.out.println(url + " : " + errorURLs.get(i).getResponseBody() + " : " + errorURLs.get(i).getStatusCode());
				}
				System.out.println();
			}
		} catch (NullPointerException ex) {
			System.out.println("Timeout: " + url);
		} finally {			
			assertTrue(errorURLs != null && errorURLs.isEmpty());
		}
	}
	
	@AfterAll
	public static void tearDown() {
		webPage.terminate();
	}
}
