package cn.aiworks.note.view;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.annotation.TargetApi;
import android.app.Activity;
import cn.aiworks.note.R;
import cn.aiworks.note.utils.Utils;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class PopupToast extends Toast {
	Context context;
	/**
	 * winsize
	 */
	Point point;
	static float textSize = 10.0f;
	static int textSizePexl  = 16;
	private WindowManager wm;
	private boolean isSpecial = false;
	private Display display;
	private Toast toast;
    public PopupToast(Context context) {
		super(context);
		point = new Point();
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		display = wm.getDefaultDisplay();
		if("hwu9200".equals(android.os.Build.DEVICE)){
			textSize = 5.8f;
			textSizePexl = 10;
		}
		
		if("mx3".equals(android.os.Build.DEVICE)&&"meizu_mx3".equals(android.os.Build.PRODUCT) /*|| "mako".equals(android.os.Build.DEVICE )&& "occam".equals(android.os.Build.PRODUCT)*/){//魅族或是老四
			//this.point 设置 
//			wm.getDefaultDisplay().getSize(this.point);
			this.point.y = wm.getDefaultDisplay().getHeight();
			isSpecial = true;
		}
		
	}
    public PopupToast(Context context ,Point pos){
    	super(context);
    	this.point = pos;
		if("hwu9200".equals(android.os.Build.DEVICE)){
			textSize = 5.8f;
			textSizePexl = 10;
		}
    	
    }
    
    /**
     * * 通过字符串id设置文本--
     * @param act
     * @param textResId
     * @param imageResId
     * @param point 土司显示的位置，如果是居中，则设置为null。
     */
	public void showMessage(Activity act, int textResId, int imageResId,Point point) {
        LayoutInflater inflater = act.getLayoutInflater();
        View layout = inflater.inflate(R.layout.pop_dialog, null);

        ImageView imageView = (ImageView)layout.findViewById(R.id.iv_face);
        imageView.setImageResource(imageResId);

        TextView textView = (TextView)layout.findViewById(R.id.tv_message);
        textView.setText(textResId);
        //设置土司文字大小
        System.out.println(Utils.sp2px(act, 10.0f)+"---------------------");
        textView.setTextSize(Utils.sp2px(act, textSize)>15?textSizePexl:Utils.sp2px(act, textSize));
        textView.setGravity(Gravity.CENTER);

        final Toast toast = new Toast(act.getApplicationContext());
        
        if(isSpecial){
        	toast.setGravity(Gravity.TOP,0, this.point.y/2-225);
        }else{
//    		toast.setGravity(Gravity.CENTER, point.x/2, point.y/2);
        	toast.setGravity(Gravity.CENTER,0,0);
        }
        toast.setDuration(LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
		showHandler = new Handler();
		showHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				toast.cancel();
			}
		}, 500);
    }
	
	static private Handler showHandler;
    /**
     *  通过字符串id设置文本--默认居中
     * @param act
     * @param textResId
     * @param imageResId
     * @param point 土司显示的位置，如果是居中，则设置为null。
     */
	 public void showMessage(Activity act, String textResId, int imageResId,Point point) {
		LayoutInflater inflater = act.getLayoutInflater();
		View layout = inflater.inflate(R.layout.pop_dialog, null);
		
		ImageView imageView = (ImageView)layout.findViewById(R.id.iv_face);
		imageView.setImageResource(imageResId);
		
		TextView textView = (TextView)layout.findViewById(R.id.tv_message);
		textView.setText(textResId);
		//设置土司文字大小
		System.out.println(Utils.sp2px(act, 10.0f)+"---------------------");
		textView.setTextSize(Utils.sp2px(act, textSize)>15?textSizePexl:Utils.sp2px(act, textSize));
		
		textView.setGravity(Gravity.CENTER);
		
		final Toast toast = new Toast(act.getApplicationContext());
        if(isSpecial){
        	toast.setGravity(Gravity.TOP,0, this.point.y/2-225);
        }else{
//        	System.out.println("widht:----- "+ point.x + "height :---"+ point.y + " wulianjian :"+hasPermanentMenukey);
        	toast.setGravity(Gravity.CENTER,0,0);
        }
		toast.setDuration(LENGTH_SHORT);
		toast.setView(layout);
		toast.show();
		showHandler = new Handler();
		showHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				toast.cancel();
			}
		}, 500);
	}
	 
	 public void canclePT(){
		 if(toast != null)toast.cancel();
	 }
	 
	 
}
