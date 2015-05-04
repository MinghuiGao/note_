package cn.aiworks.note.domain;

/**
 * EDAMUserException(errorCode:QUOTA_REACHED, parameter:Accounting.uploadLimit)
 * @author aispeech
 *
 */
public class EvernoteEDAMUserExceptionBean {
	String errorCode;
	String parameter;
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getParameter() {
		return parameter;
	}
	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

	
}
