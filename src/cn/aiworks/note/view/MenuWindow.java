package cn.aiworks.note.view;


import cn.aiworks.note.R;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.view.ViewGroup.LayoutParams;
import android.widget.SimpleAdapter;

public class MenuWindow extends PopupWindow {
	
	private GridView gv_menu;
	private LinearLayout pop_layout;
	private View myMenu;
	
	private SimpleAdapter adapter;
	private OnItemClickListener listener;

	/**
	 * 
	 * @param context
	 * @param itemOnClickListener
	 * @param adapter 实现的gridview菜单适配器。
	 * @param listener gridview的点击处理监听器。
	 */
	public MenuWindow(Activity context,OnClickListener itemOnClickListener,SimpleAdapter adapter,OnItemClickListener listener){
		super(context);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		myMenu = inflater .inflate(R.layout.share_menu, null);
		
		pop_layout = (LinearLayout) myMenu.findViewById(R.id.ll_pop_main);
		gv_menu = (GridView) myMenu.findViewById(R.id.gv_menu);
		//
		gv_menu.setAdapter(adapter);
		//
		gv_menu.setOnItemClickListener(listener);
		
		this.setContentView(myMenu);
		this.setWidth(LayoutParams.MATCH_PARENT);
		this.setHeight(LayoutParams.WRAP_CONTENT);
		this.setFocusable(true);
		//-1 默认动画。
		this.setAnimationStyle(-1);
		//实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0xb0000000);
		//设置SelectPicPopupWindow弹出窗体的背景
		this.setBackgroundDrawable(dw);
		//添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
		myMenu.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				int height = myMenu.findViewById(R.id.ll_pop_main).getTop();
				int y=(int) event.getY();
				if(event.getAction()==MotionEvent.ACTION_UP){
					if(y<height){
						dismiss();
					}
				}
				return true;
			}
		});
	}
}
