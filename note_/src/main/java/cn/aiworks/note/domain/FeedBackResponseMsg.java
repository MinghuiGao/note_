package cn.aiworks.note.domain;
/**
 * 封装了返回的消息和状态，还有id。
 * @author aispeech
 *
 */
public class FeedBackResponseMsg {
	/**
	 * {"msg": "输入参数有误", "status": 50}
	 * {"msg": "上传成功", "status": 20, "id": "5343b36fc38d7c60eca920c9"}
	 */
	private String msg;
	private String status;
	private String id;
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	
}