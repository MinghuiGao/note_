package cn.aiworks.note.anim;

import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;

public class MyTanimation extends TranslateAnimation {
	static LinearInterpolator interpolator = new LinearInterpolator();

	private MyTanimation(float paramFloat1, float paramFloat2) {
		super(0.0F, 0.0F, paramFloat1, paramFloat2);
	}

	public static MyTanimation getMyTAnimation(float paramFloat1,
			float paramFloat2) {
		MyTanimation localMyTanimation = new MyTanimation(paramFloat1,
				paramFloat2);
		setAttrs(localMyTanimation);
		return localMyTanimation;
	}

	private static void setAttrs(MyTanimation paramMyTanimation) {
		LinearInterpolator localLinearInterpolator = interpolator;
		paramMyTanimation.setInterpolator(localLinearInterpolator);
		paramMyTanimation.setDuration(300L);
		paramMyTanimation.setFillAfter(true);
	}
}