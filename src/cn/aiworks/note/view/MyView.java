package cn.aiworks.note.view;

import android.content.Context;
import android.view.View;

public class MyView extends View {
	
	public MyView(Context context) {
		super(context);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE>>2,MeasureSpec.AT_MOST );
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
}
