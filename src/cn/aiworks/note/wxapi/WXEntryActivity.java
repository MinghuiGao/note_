package cn.aiworks.note.wxapi;

import cn.aiworks.note.constant.Constants;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;

import android.app.Activity;
import android.os.Bundle;

public class WXEntryActivity extends Activity {

	// IWXAPI 是第三方app和微信通信的openapi接口
	private IWXAPI api;
	private String text;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 将该app注册到微信
		api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, true);
		api.registerApp(Constants.APP_ID);

		text = (String) savedInstanceState.getBundle("et_info").get("et_content");
		System.out.println("text:"+text);
		// init WXTextObejct object
		WXTextObject textObj = new WXTextObject();
		textObj.text = text;

		// use wxtextobject init wxmediamessage object
		WXMediaMessage msg = new WXMediaMessage();
		msg.mediaObject = textObj;
		msg.description = text;

		// construct a Req object
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = String.valueOf(System.currentTimeMillis());// time stamp
		req.message = msg;

		// use api to send data to WX
		api.sendReq(req);
		this.finish();
	}
}
