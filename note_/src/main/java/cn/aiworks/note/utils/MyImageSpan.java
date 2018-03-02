package cn.aiworks.note.utils;

import android.graphics.Bitmap;
import android.net.Uri;
import android.text.style.ImageSpan;
import android.content.Context;

public class MyImageSpan extends ImageSpan {
	public  MyImageSpan(Context ctx, Bitmap b) {
		super(ctx, b);
		System.out.println("MyImageSpan constructor");
	}
	public Uri uri;
	public int sequence;
}
