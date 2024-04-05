package hkma.gov.hk.webdriver;

/**
 * All properties/variable are `public static final`, so need init the value
 * All methods are `public abstract`, so the same method in subclass MUST be public
 */
public interface LoginPageInterface {	
	String loginGetSessionID(String username, String password) throws Exception;	
	String loginGetSessionID(String verificationCode) throws Exception;	
}
