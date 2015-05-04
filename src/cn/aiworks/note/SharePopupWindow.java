package cn.aiworks.note;

import java.io.File;
import java.io.Serializable;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.ClipboardManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;
import cn.aiworks.note.application.EverInputApplication;
import cn.aiworks.note.constant.Constant;
import cn.aiworks.note.constant.Constants;
import cn.aiworks.note.domain.Attachment;
import cn.aiworks.note.utils.Utils;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;
import com.umeng.analytics.MobclickAgent;

import cn.aiworks.note.view.PopupToast;

@SuppressWarnings("deprecation")
public class SharePopupWindow extends Activity implements OnClickListener {
	RelativeLayout rl_menu_gridview;
	GridView gv_pop_layout;
	TextView bt_cancle;
	// #959595 TODO：加颜色
	private IWXAPI api;
	// 分享菜单内容
	String[] titles = { "邮件", "短信", "微信好友", "朋友圈", "复制", "印象笔记","新浪微博","有道云笔记" };// 菜单图片资源
	int[] iconIds = { R.drawable.s_mail_, R.drawable.s_msg_,
			R.drawable.s_voice_,R.drawable.s_friend_, R.drawable.s_copy_, R.drawable.s_elephent_,R.drawable.s_sina_,R.drawable.s_youdao_
			 };// 菜单文字资源

	private Resources resources;

	private void showMessage(int titleResId, int imageResId) {
		PopupToast pt = new PopupToast(SharePopupWindow.this);
		pt.showMessage(this, titleResId, imageResId,(Point)((EverInputApplication)getApplication()).getAppConstant().get("winSize"));
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case Constant.COPY_TO_CLIPBOARD:
				showMessage(R.string.copy_success, R.drawable.success);
				break;
			case Constant.NETWORK_UNAVAILABLE:
				showMessage(R.string.network_error, R.drawable.bugeili);
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setFinishOnTouchOutside(false);
//		setFinishOnTouchOutside(false);
		View view = View.inflate(this, R.layout.pop, null);
		setContentView(view);
		api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);

		resources = getResources();

		gv_pop_layout = (GridView) findViewById(R.id.gv_pop_layout);
		bt_cancle = (TextView) findViewById(R.id.bt_cancle);
		rl_menu_gridview = (RelativeLayout) findViewById(R.id.rl_menu_gridview);
		// 添加选择窗口范围监听可以优先获取触点，即不再执行onTouchEvent()函数，点击其他地方时执行onTouchEvent()函数销毁Activity
		MyAdapter myAdapter = new MyAdapter(this, titles, iconIds);
		gv_pop_layout.setAdapter(myAdapter);
		gv_pop_layout.setSelector(new ColorDrawable(Color.TRANSPARENT));
		gv_pop_layout.setOnItemClickListener(new MyListener());
		// bt_cancle.setOnClickListener(this);
//		bt_cancle.setOnTouchListener(this);
		// 注册
		api.registerApp(Constants.APP_ID);

		int width = getWindowManager().getDefaultDisplay().getWidth();
		int width_dip = Utils.px2dip(this, width);
	}

	// 实现onTouchEvent触屏函数但点击屏幕时销毁本Activity
	@Override
	public boolean onTouchEvent(MotionEvent event) {
/*		if (event.getAction() == MotionEvent.ACTION_UP) {
			int top = bt_cancle.getTop();
			int cancelHeight = bt_cancle.getHeight();
			float rawy = event.getY();
			System.out.println("rawy: " + rawy);
			System.out.println("cancelHeight: " + cancelHeight);
			System.out.println("cancelTop: " + top);
			if (top < rawy && rawy < (cancelHeight + top) ) {
				finish();
			}
			else {
				bt_cancle.setSelected(false);
				bt_cancle.setPressed(false);
				bt_cancle.setBackgroundColor(Color.WHITE);
				return false;
			}
		}
		return true;*/
		int cacelHeight;
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				cancelHeight = bt_cancle.getHeight();
				// 获取第一次点击的点
				oldy = (int) event.getRawY();
				Log.i("sp", ""+oldy);
				//获取上限
				top = bt_cancle.getTop();
				if(oldy < (top+cancelHeight) && oldy >top)
					bt_cancle.setBackgroundColor(getResources().getColor(R.color.grey));
				break;
			case MotionEvent.ACTION_MOVE:
				newy = (int) event.getRawY();
				Log.i("sp", ""+newy);
				if(/*newy < (top+cancelHeight) && */newy >top)
					bt_cancle.setBackgroundColor(getResources().getColor(R.color.grey));
				else
					bt_cancle.setBackgroundColor(Color.WHITE);
				break;
			case MotionEvent.ACTION_UP:
				newy = (int) event.getRawY();
				Log.i("sp", ""+newy);
				if(newy>top && oldy < (top+cancelHeight) && oldy >top){
					bt_cancle.setBackgroundColor(Color.WHITE);
					this.finish();
				}
				if( newy == oldy){
					bt_cancle.setBackgroundColor(Color.WHITE);
					this.finish();
				}
				System.out.println("oldy:---->"+oldy);
				bt_cancle.setBackgroundColor(Color.WHITE);
	//			event.gety
				break;
			default:
				break;
		}
		return true;
	}

	class MyListener implements android.widget.AdapterView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			switch (iconIds[position]) {
			case R.drawable.s_mail_:
				setResult(Constant.SHARE_EMAIL);
				SharePopupWindow.this.finish();
				System.out.println("mail is clicked");
				break;
			case R.drawable.s_msg_:
				setResult(Constant.SHARE_SMS);
				SharePopupWindow.this.finish();
				System.out.println("message is clicked");
				break;
			case R.drawable.s_voice_:
				setResult(Constant.SHARE_WEIXIN);
				SharePopupWindow.this.finish();
				System.out.println("voice  is clicked");
				break;
			case R.drawable.s_elephent_:
				if(Utils.isNetworkConnected(SharePopupWindow.this)){
					setResult(Constant.SHARE_EVERNOTE);
					System.out.println("elephent  is clicked");
				}else{
					handler.sendEmptyMessage(Constant.NETWORK_UNAVAILABLE);
				}
				SharePopupWindow.this.finish();
				break;
			case R.drawable.s_copy_:
				setResult(Constant.SHARE_COPY);
				SharePopupWindow.this.finish();
				System.out.println("copy  is clicked");
				break;
			case R.drawable.s_friend_:
				// 朋友圈
				setResult(Constant.SHARE_FRIENDS);
				SharePopupWindow.this.finish();
				break;
			case R.drawable.s_sina_:
				if(Utils.isNetworkConnected(SharePopupWindow.this)){
					setResult(Constant.SHARE_SINA_WEIBO);
					System.out.println("elephent  is clicked");
				}else{
					handler.sendEmptyMessage(Constant.NETWORK_UNAVAILABLE);
				}
				SharePopupWindow.this.finish();
				break;
			case R.drawable.s_youdao_:
				setResult(Constant.SHARE_YOUDAO);
				SharePopupWindow.this.finish();
				break;
			default:
				break;
			}
		}
	}

	class MyAdapter extends BaseAdapter {
		Context context;
		String[] titles;
		int[] ids;

		public MyAdapter(Context context, String[] titles, int[] ids) {
			this.context = context;
			this.titles = titles;
			this.ids = ids;
		}

		@Override
		public int getCount() {
			return titles.length;
		}

		@Override
		public Object getItem(int position) {
			return iconIds[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(context, R.layout.item, null);
			}
			ImageView iv_icon = (ImageView) convertView
					.findViewById(R.id.iv_icon);
			TextView tv = (TextView) convertView.findViewById(R.id.tv_tag);
			// 设置图片src TODO:加背景
			iv_icon.setImageResource(ids[position]);
			// iv_icon.setBackgroundResource(ids[position]);
			tv.setText(titles[position]);
			return convertView;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_cancle:
			this.finish();
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case Constant.SHARE_TO_ELEPHANT_REQ:
			if(data != null){
				//处理印象的返回结果。
			}
			break;
		default:
			break;
		}
	}
	
	private int newy;
	private int oldy;
	private int top;
	private int top2;
	private int cancelHeight;
	private float rawy;
//	@Override
//	public boolean onTouch(View v, MotionEvent event) {
//		int cacelHeight;
//		switch (event.getAction()) {
//		case MotionEvent.ACTION_DOWN:
//			cancelHeight = bt_cancle.getHeight();
//			// 获取第一次点击的点
//			oldy = (int) event.getRawY();
//			System.out.println("oldy:---->"+oldy);
//			//获取上限
//			top = bt_cancle.getTop();
//			v.setBackgroundColor(getResources().getColor(R.color.grey));
//			break;
//		case MotionEvent.ACTION_MOVE:
//			v.setBackgroundColor(getResources().getColor(R.color.grey));
//			newy = (int) event.getRawY();
//			break;
//		case MotionEvent.ACTION_UP:
//			newy = (int) event.getRawY();
//			if(newy>top && oldy < (top+cancelHeight) && oldy >top){
//				v.setBackgroundColor(Color.WHITE);
//				this.finish();
//			}
//			System.out.println("oldy:---->"+oldy);
//			v.setBackgroundColor(Color.WHITE);
////			event.gety
//			break;
//		default:
//			break;
//		}
//		return true;
//	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	
}
