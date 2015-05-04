package cn.aiworks.note;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

public class TimerModifier extends BroadcastReceiver {
	private Handler handler;
	private SimpleDateFormat dateFormat;
	public TimerModifier(Handler handler,SimpleDateFormat sdf){
		this.handler = handler;
		this.dateFormat = sdf;
	}
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if(action.equals(Intent.ACTION_TIME_TICK)){
			String currentTime = dateFormat.format(new Date());
			Message msg = handler.obtainMessage();
			msg.obj = currentTime;
			handler.sendMessage(msg);
		}
	}

}
