package cn.aiworks.note;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.Header;
import org.apache.http.HttpConnection;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpProtocolParams;

import com.google.gson.Gson;
import com.tencent.mm.sdk.openapi.GetMessageFromWX;

import cn.aiworks.note.application.EverInputApplication;
import cn.aiworks.note.constant.Constant;
import cn.aiworks.note.domain.FeedBackBean;
import cn.aiworks.note.domain.FeedBackResponseMsg;
import cn.aiworks.note.utils.Utils;
import cn.aiworks.note.view.PopupToast;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView.Validator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class FeedbackActivity extends Activity implements OnClickListener, OnFocusChangeListener{

	ImageView iv_feedback ;
	Button bt_feed_send;
	EditText et_feedback_mail;
	EditText et_feedback_content;
	ProgressDialog pd ;
	/**
	 * {"msg": "输入参数有误", "status": 50}
	 * {"msg": "上传成功", "status": 20, "id": "5343b36fc38d7c60eca920c9"}
	 */
	String feedbackUrl = "http://115.182.0.101/littleoh/v1/user/feedbackgen";
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case Constant.MSG_FEEDBACK:
				PopupToast li_pt_success = new PopupToast(FeedbackActivity.this);
				String login_success_info = (String) msg.obj;
				li_pt_success.showMessage(FeedbackActivity.this, login_success_info, msg.arg1,(Point)((EverInputApplication)getApplication()).getAppConstant().get("winSize"));
//				InputMethodManager imm = (InputMethodManager) FeedbackActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);  
//				imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
				break;
			case Constant.FEEDBACK_MAIL_REQUEST_FOCUS:
				et_feedback_mail.setEnabled(true);
				et_feedback_content.setFocusable(false);
				et_feedback_content.setEnabled(false);
				break;
			case Constant.FEEDBACK_MSG:
				PopupToast feedbackPt = new PopupToast(FeedbackActivity.this);
				String responseInfo = (String) msg.obj;
				int imageId = msg.arg1;
				feedbackPt.showMessage(FeedbackActivity.this, responseInfo, imageId,(Point)((EverInputApplication)getApplication()).getAppConstant().get("winSize"));
				break;
			case Constant.SHARE_DISMISS_DIALOG:
				if(pd != null&&pd.isShowing()){
					pd.dismiss();
				}
				break;
			case Constant.NETWORK_UNAVAILABLE:
				PopupToast pt = new PopupToast(FeedbackActivity.this);
				pt.showMessage(FeedbackActivity.this, R.string.network_error, R.drawable.bugeili, null);
				break;
			default:
				break;
			}
		};
	};

	private Intent intent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feedback);
		iv_feedback = (ImageView) findViewById(R.id.iv_feedback);
		bt_feed_send = (Button) findViewById(R.id.bt_feed_send);
		et_feedback_mail = (EditText) findViewById(R.id.et_feedback_mail);
		et_feedback_content = (EditText) findViewById(R.id.et_feedback_content);
		
		iv_feedback.setOnClickListener(this);
		bt_feed_send.setOnClickListener(this);
		et_feedback_mail.setOnFocusChangeListener(this);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.iv_feedback:
        	this.finish();  //finish当前activity  
            startActivity(new Intent(this,SettingActivity.class));
            if("ja3gchnduos".equals(android.os.Build.DEVICE) && "ja3gchnduoszn".equals(android.os.Build.PRODUCT))
            break;
            overridePendingTransition(R.anim.nochange_in, R.anim.slide_from_left_out);
			break;
		case R.id.bt_feed_send:
			String mailAddress = et_feedback_mail.getText().toString();
			String content = et_feedback_content.getText().toString();
			if(!Utils.isNetworkConnected(FeedbackActivity.this)){
				handler.sendEmptyMessage(Constant.NETWORK_UNAVAILABLE);
				return;
			}
			if(TextUtils.isEmpty(mailAddress)){
				sendMsg(Constant.MSG_FEEDBACK,getResources().getString(R.string.feedback_empty_email),R.drawable.fail,handler);
//				InputMethodManager imm = (InputMethodManager) FeedbackActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);  
//				imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
				return;
			}
			//校验邮箱格式
			if(checkMailAddress(mailAddress)){
				if(!TextUtils.isEmpty(content)){
					
					FeedBackBean fbb = new FeedBackBean();
					fbb.setTitle(mailAddress);
					fbb.setContent(content);
					fbb.setPlatform("android");
					
					Gson gson = new Gson();
					String feedbackJson = "";
					try {
						feedbackJson = gson.toJson(fbb).replace(" ", "%20").replace("{",URLEncoder.encode("{", "utf-8")).replace("}", URLEncoder.encode("}", "utf-8")).replace(",", URLEncoder.encode(",", "utf-8")).replace(":", URLEncoder.encode(":","utf-8")).replace("\"", URLEncoder.encode("\"","utf-8"));
					} catch (UnsupportedEncodingException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					final String url_encode = feedbackUrl+"?app=test&feedback="+feedbackJson;
					System.out.println("gson.toJson(fbb):------->"+feedbackJson);
					
//					{"content":"同居同乐突兀兔子太好了托","platform":"android","title":"fgcgh@fyv.com"}
					
					//发送请求
//					http://115.182.0.101/littleoh/v1/user/feedbackgen/?app=test&feedback=%7B%22title%22:%22t%22,%22content%22:%22con%22%7D
					//init httpparams
					BasicHttpParams params = new BasicHttpParams();
					HttpConnectionParams.setConnectionTimeout(params, 5*1000);
					HttpConnectionParams.setSoTimeout(params, 5*1000);
					HttpConnectionParams.setSocketBufferSize(params, 8192);
					HttpClientParams.setRedirecting(params, true);
					String userAgent = "android/gmh";
					HttpProtocolParams.setUserAgent(params, userAgent);
//					final HttpClient client = new DefaultHttpClient(params);
					final HttpClient client = new DefaultHttpClient(params);  
					//create nameValuePair
//					ArrayList<NameValuePair> paramPairs = new ArrayList<NameValuePair>();
//					paramPairs.add(new BasicNameValuePair("app","EverInput"));
//					paramPairs.add(new BasicNameValuePair("feedback",feedbackJson));
//					String url = feedbackUrl + "?app=EverInput&feedback="+feedbackJson;
//					URLEncoder.encode("[", "utf-8")
					
					
/*					HttpPost post = new HttpPost(feedbackUrl);
					params.setParameters(new String[]{"app","feedback"},paramPairs);
					post.setParams(params);*/
//					pd = new ProgressDialog(this);
//					pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//					pd.setMessage("发送中...");
//					pd.show();
					new Thread(){
						public void run(){
							try {
								System.out.println("url:------->"+url_encode);
								HttpGet get  = new HttpGet(url_encode);
//								get.setURI(new URI(url_encode));
								HttpResponse response = client.execute(get);
								int statusCode = response.getStatusLine().getStatusCode();
								InputStream resInputStream = response.getEntity().getContent();
								Header[] allHeaders = response.getAllHeaders();
								
								//stream2Strig
								String resJason = stream2String(resInputStream);
								System.out.println("stream2String(resInputStream):resJason:==------->"+resJason);
								Gson gson= new Gson();
								FeedBackResponseMsg responseMsg = gson.fromJson(resJason, FeedBackResponseMsg.class);
								if("20".equals(responseMsg.getStatus())){
									//上传成功
									Message msg = Message.obtain();
									msg.what = Constant.FEEDBACK_MSG;
//									msg.obj = responseMsg.getMsg();
									msg.obj = FeedbackActivity.this.getResources().getString(R.string.feedback_success);
									msg.arg1 = R.drawable.success;
//									handler.sendMessage(msg);
								}else{
									//失败
									Message msg = Message.obtain();
									msg.what = Constant.FEEDBACK_MSG;
									msg.obj = FeedbackActivity.this.getResources().getString(R.string.feedback_fail);
									msg.arg1 = R.drawable.fail;
//									handler.sendMessage(msg);
								}
							} catch (ClientProtocolException e) {
								e.printStackTrace();
								System.out.println("ClientProtocolException:---->"+e.getMessage());
							} catch(ConnectException e){
								e.printStackTrace();
								Message msg = Message.obtain();
								msg.what = Constant.FEEDBACK_MSG;
								msg.obj = getResources().getString(R.string.network_error);
								msg.arg1 = R.drawable.bugeili;
//								handler.sendMessage(msg);
							}catch (IOException e) {
								e.printStackTrace();
								Message msg = Message.obtain();
								msg.what = Constant.FEEDBACK_MSG;
								msg.obj = FeedbackActivity.this.getResources().getString(R.string.feedback_fail);
								msg.arg1 = R.drawable.fail;
//								handler.sendMessage(msg);
							}catch (Exception e){
								e.printStackTrace();
							}
							finally{
								handler.sendEmptyMessage(Constant.SHARE_DISMISS_DIALOG);
								Message msg = Message.obtain();
								msg.what = Constant.FEEDBACK_MSG;
//								msg.obj = responseMsg.getMsg();
								msg.obj = FeedbackActivity.this.getResources().getString(R.string.feedback_success);
								msg.arg1 = R.drawable.success;
								handler.sendMessage(msg);
							}
						}
					}.start();
					
					
//					sendMsg(Constant.MSG_FEEDBACK,"反馈已经发送！",R.drawable.success,handler);
					this.finish();
		            if("ja3gchnduos".equals(android.os.Build.DEVICE) && "ja3gchnduoszn".equals(android.os.Build.PRODUCT)){
			            startActivity(new Intent(this,SettingActivity.class));
		            }else{
		            	startActivity(new Intent(this,SettingActivity.class));
		            	overridePendingTransition(R.anim.nochange_in, R.anim.slide_from_left_out);
		            }
				}else{
					sendMsg(Constant.MSG_FEEDBACK,getResources().getString(R.string.feedback_empty_content),R.drawable.fail,handler);
				}
			}
			break;
		default:
			break;
		}
//		InputMethodManager imm = (InputMethodManager) FeedbackActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);  
//		imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
	}
	
	public String stream2String(InputStream ips) throws UnsupportedEncodingException{
		int b;
		StringBuffer sb = new StringBuffer();
		try {
			while((b = ips.read()) != -1){
				sb.append((char)b);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new String(sb.toString().getBytes("iso8859-1"),"utf-8");
	}
	
	 @Override  
	    public boolean onKeyDown(int keyCode, KeyEvent event) {  
	        if(keyCode==KeyEvent.KEYCODE_BACK){  
	        	this.finish();  //finish当前activity  
	            startActivity(new Intent(this,SettingActivity.class));
	            if("ja3gchnduos".equals(android.os.Build.DEVICE) && "ja3gchnduoszn".equals(android.os.Build.PRODUCT))
	                return true;
	            overridePendingTransition(R.anim.nochange_in, R.anim.slide_from_left_out);
	            return true;  
	        }  
	        return super.onKeyDown(keyCode, event);  
	    }

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if(!hasFocus){
			EditText et = (EditText) v;
			String mailAddress = et.getText().toString();
			if(TextUtils.isEmpty(mailAddress)){
				sendMsg(Constant.MSG_FEEDBACK,getResources().getString(R.string.feedback_empty_email),R.drawable.fail,handler);
				InputMethodManager imm = (InputMethodManager) FeedbackActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);  
				imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
			}else{
				checkMailAddress(mailAddress);
			}
		}
	}

	private boolean checkMailAddress(String mailAddress) {
		boolean isOk = validateEmail(mailAddress);
		if(!isOk){
			//提示用户输入正确的邮箱格式
			sendMsg(Constant.MSG_FEEDBACK,"请输入正确的邮箱！",R.drawable.bugeili,handler);
		}
		return isOk;
	}
	/**
     * 验证email规则
     * @param email
     * @return
     */
    public boolean validateEmail(String email)
    {
    	return TextUtils.isEmpty(email)? false
                :email.matches("^(\\w|\\.|-|\\+)+@(\\w|-)+(\\.(\\w|-)+)+$");
    }
    
    /**
     * 想handler发送消息
     * @param code
     * @param info
     * @param handler
     */
    private void sendMsg(int code,String info,int imageId,Handler handler){
    	Message msg = Message.obtain();
    	msg.what = code;
    	msg.arg1 = imageId;
    	msg.obj = info;
    	handler.sendMessage(msg);
    }
	
}


