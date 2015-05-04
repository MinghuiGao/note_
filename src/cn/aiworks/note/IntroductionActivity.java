package cn.aiworks.note;

import cn.aiworks.note.constant.Constant;
import cn.aiworks.note.utils.Utils;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class IntroductionActivity extends Activity implements OnClickListener {
	
	ImageView iv_background;
	static Handler handler;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View view = View.inflate(IntroductionActivity.this, R.layout.introdue, null);
		iv_background = (ImageView) view.findViewById(R.id.iv_background);
		setContentView(R.layout.introdue);
		iv_background = (ImageView) findViewById(R.id.iv_background);
		iv_background.setOnClickListener(this);
	}
	
	public static void start(Context context,Handler handler){
		IntroductionActivity.handler = handler;
		Intent intent = new Intent(context,IntroductionActivity.class);
		context.startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_background:
			this.finish();
			//开始录音。
			handler.sendEmptyMessage(Constant.START_TAPE_FROM_INTRODUCTION);
			break;
		default:
			break;
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (KeyEvent.KEYCODE_BACK == keyCode) {
			return true;
		}
		return false;
	}
}
