package cn.aiworks.note;


import com.umeng.analytics.MobclickAgent;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener {
	
	Button bt_tape;
	Button bt_bottom_feature;
	SharedPreferences sp ;
	//默认是第一次
	Boolean isFirst;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		sp = getSharedPreferences("isFirst", MODE_PRIVATE);
		isFirst = sp.getBoolean("isFirst", false);
		
		if(!isFirst){
			//yidao
			isFirst = false;
			Editor edit = sp.edit();
			edit.putBoolean("isFirst", isFirst);
		}
		bt_tape = (Button) findViewById(R.id.bt_tape);
		bt_tape.setOnClickListener(this);
		bt_bottom_feature = (Button) findViewById(R.id.bt_bottom_feature);
		bt_bottom_feature.setOnClickListener(this);
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_tape:
			TapeActivity.start(this);
			this.finish();
			break;
		case R.id.bt_bottom_feature:
			Intent setting = new Intent(this,SettingActivity.class);
			MainActivity.this.startActivity(setting);
			overridePendingTransition(R.anim.slid_from_right_in, R.anim.nochange_out);
		default:
			break;
		}
	}

	/**
	 * 开启活动
	 * @param context
	 */
	public static void start(Context context) {
		Intent intent = new Intent(context,MainActivity.class);
		context.startActivity(intent);
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
