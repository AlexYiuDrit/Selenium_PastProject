package hkma.gov.hk.entity;

public class ResponseData<T> {
	private int statusCode;
	private T responseBody;
	
	public ResponseData(int statusCode, T responseBody) {
		this.statusCode = statusCode;
		this.responseBody = responseBody;
	}
	
	public int getStatusCode() {
		return statusCode;
	}
	
	public T getResponseBody() {
		return responseBody;
	}
}
