package cn.aiworks.note.view;

import cn.aiworks.note.R;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;

public class MyPopupWindow extends PopupWindow {

	PopupWindow popupWindow;
	private Context context;
	private View inflate;
	private LayoutInflater inflator;

	public MyPopupWindow(Context context,int width,int height) {
		this.context = context;
		inflator = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
//		inflate = inflator.inflate(R.layout.pop_dialog, null);
		inflate = inflator.inflate(R.layout.diaolog_about_us, null);
		
		if(width == 0 || height == 0){
			popupWindow = new PopupWindow(inflate, LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		}else{
			popupWindow = new PopupWindow(inflate,width,height);
		}
		popupWindow.setOutsideTouchable(true);
		popupWindow.setAnimationStyle(R.style.AnimationPop);
	}

	public void dismiss() {
		popupWindow.dismiss();
	}

	/**
	 * 显示对话框
	 * 对话框的显示位置，以屏幕的左下角为原点。
	 * @param positionX
	 *            
	 * @param positionY
	 *           
	 */
	public void show(int positionX, int positionY) {
		popupWindow.update();
		// 计算弹框的左上角在屏幕上的显示位置，像素值。
		popupWindow.showAtLocation(inflate, Gravity.BOTTOM
				| Gravity.CENTER_HORIZONTAL, positionX, positionY);
	}

}
