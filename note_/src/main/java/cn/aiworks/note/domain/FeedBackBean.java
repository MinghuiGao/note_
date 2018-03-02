package cn.aiworks.note.domain;

/**
 * 用来封装反馈的内容参数。
 * @author aispeech
 *
 */
public class FeedBackBean {
	/**
	 * {"title”:"t","content":”con”,"platform"："ios"}
	 */

	private String title;
	private String content;
	private String platform;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getPlatform() {
		return platform;
	}
	public void setPlatform(String platform) {
		this.platform = platform;
	}

	
	
}
