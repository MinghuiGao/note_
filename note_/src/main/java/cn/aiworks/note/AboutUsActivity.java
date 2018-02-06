package cn.aiworks.note;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class AboutUsActivity extends Activity {

	ImageView iv_about_us_back;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_us);
		iv_about_us_back = (ImageView) findViewById(R.id.iv_about_us_back);
		iv_about_us_back.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				AboutUsActivity.this.finish();
				startActivity(new Intent(AboutUsActivity.this,SettingActivity.class));
				startActivity(new Intent(AboutUsActivity.this,SettingActivity.class));
	            if("ja3gchnduos".equals(android.os.Build.DEVICE) && "ja3gchnduoszn".equals(android.os.Build.PRODUCT))
	                return;
				overridePendingTransition(R.anim.nochange_in, R.anim.slide_from_left_out);
			}
		});
	}
	
	 @Override  
	    public boolean onKeyDown(int keyCode, KeyEvent event) {  
	        if(keyCode==KeyEvent.KEYCODE_BACK){  
	            this.finish();  //finish当前activity  
	            startActivity(new Intent(AboutUsActivity.this,SettingActivity.class));
	            if("ja3gchnduos".equals(android.os.Build.DEVICE) && "ja3gchnduoszn".equals(android.os.Build.PRODUCT))
	                return true;
	            overridePendingTransition(R.anim.nochange_in, R.anim.slide_from_left_out);
	            return true;  
	        }  
	        return super.onKeyDown(keyCode, event);  
	    } 
	
}
