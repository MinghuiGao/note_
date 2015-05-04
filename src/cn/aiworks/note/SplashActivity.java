package cn.aiworks.note;

import java.util.HashMap;

import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import cn.aiworks.note.application.EverInputApplication;
import cn.aiworks.note.constant.Constant;

public class SplashActivity extends Activity {

	LinearLayout ll_splash;

	SharedPreferences sp;
	private Editor edit;
	private boolean isFirstStart;
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constant.FADEOUT:
				AlphaAnimation fadeOut = new AlphaAnimation(1.0F, 0.0F);
				fadeOut.setDuration(300);
				fadeOut.setFillAfter(true);
				ll_splash.startAnimation(fadeOut);
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		

		setContentView(R.layout.splash);
		ll_splash = (LinearLayout) findViewById(R.id.ll_splash);
		AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
		fadeIn.setFillAfter(true);
		fadeIn.setDuration(400);
		ll_splash.startAnimation(fadeIn);
		//获取屏幕的宽高
		Display display = getWindowManager().getDefaultDisplay();
		int width = display.getWidth();
		int height = display.getHeight();
		Point winSize = new Point(width,height);
		EverInputApplication app  = (EverInputApplication) getApplication();
		app.getAppConstant().put("winSize", winSize);
		new Thread() {
			public void run() {
				try {
					Thread.sleep(1000);
					handler.sendEmptyMessage(Constant.FADEOUT);
					Thread.sleep(300);
					loadMainUi();
				} catch (InterruptedException e) {
				} 
			}
		}.start();
		sp = getSharedPreferences("splash", MODE_PRIVATE);
		edit = sp.edit();
		isFirstStart = sp.getBoolean("isFirstStart", true);
	}
	
	/**
	 *  进入主界面
	 */
	public void  loadMainUi(){
		if (isFirstStart) {
			MainActivity.start(SplashActivity.this);
			edit.putBoolean("isFirstStart", false);
			edit.commit();
			SplashActivity.this.finish();
		} else {
			TapeActivity.start(SplashActivity.this, true);
			SplashActivity.this.finish();
		}
	}
	
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
