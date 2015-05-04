package cn.aiworks.note.domain;

/**
 * onError{
 * "error":" Text too long, please input text less than 140 characters!",
 * "error_code":20012, 
 * "request":"/2/statuses/upload.json" }
 * 
 * @author aispeech
 * 
 */
public class WeiboErrorInfo {
	
	private String error;
	private int error_code;
	private String	 requset;
	
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public int getError_code() {
		return error_code;
	}
	public void setError_code(int error_code) {
		this.error_code = error_code;
	}
	public String getRequsetUrl() {
		return requset;
	}
	public void setRequsetUrl(String requset) {
		this.requset = requset;
	}
	
	
}
