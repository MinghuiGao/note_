package cn.aiworks.note.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import cn.aiworks.note.R;
import cn.aiworks.note.TapeActivity;
import cn.aiworks.note.R.drawable;
import cn.aiworks.note.constant.Constant;
import cn.aiworks.note.domain.Attachment;

import android.R.integer;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore.MediaColumns;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;

public class Utils {
	static String tag = "utils";
	private static Bitmap bitmap;
	/**
	 * 获取某种规则格式的时间字符串
	 * @param pattern 时间格式如："yyyy-MM-dd HH:mm"
	 * @return
	 */
	public static String getFormatDate(String pattern){
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.CHINA);
		return dateFormat.format(new Date());
	}
	
	/**
	 * 根据id获取bitmap
	 * @param id
	 * @param res
	 * @return
	 */
	public static Bitmap getDrawable(int id, Resources res) {
		return BitmapFactory.decodeResource(res, id);
	}

	 /** 
     * 将px值转换为sp值，保证文字大小不变 
     *  
     * @param pxValue 
     * @param fontScale 
     *            （DisplayMetrics类中属性scaledDensity） 
     * @return 
     */  
    public static int px2sp(Context context, float pxValue) {  
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;  
        return (int) (pxValue / fontScale + 0.5f);  
    }  
  
    /** 
     * 将sp值转换为px值，保证文字大小不变 
     *  
     * @param spValue 
     * @param fontScale 
     *            （DisplayMetrics类中属性scaledDensity） 
     * @return 
     */  
    public static int sp2px(Context context, float spValue) {  
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity; 
        float dens ;
        if(fontScale>250){
        	dens = 250.0f;
        }else
        	dens = fontScale;
        return (int) (spValue * dens + 0.5f);  
    }  
	
	/** 
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素) 
     */  
    public static int dip2px(Context context, float dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    }  
  
    /** 
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp 
     */  
    public static int px2dip(Context context, float pxValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (pxValue / scale + 0.5f);  
    }  
	
	/**
	 * 缩放bitmap
	 * 
	 * @param filePath
	 * @param targetWidth
	 * @param targetHeight
	 * @return
	 */
	public static Bitmap resizeBitMapImage1(String filePath, int targetWidth,
			int targetHeight) {
		Bitmap bitMapImage = null;
		// First, get the dimensions of the image
		Options options = new Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);
		double sampleSize = 0;
		// Only scale if we need to
		// (16384 buffer for img processing)
		Boolean scaleByHeight = Math.abs(options.outHeight - targetHeight) >= Math
				.abs(options.outWidth - targetWidth);
		if (options.outHeight * options.outWidth * 2 >= 1638) {
			// Load, scaling to smallest power of 2 that'll get it <= desired
			// dimensions
			sampleSize = scaleByHeight ? options.outHeight / targetHeight
					: options.outWidth / targetWidth;
			sampleSize = (int) Math.pow(2d,
					Math.floor(Math.log(sampleSize) / Math.log(2d)));
		}
		// Do the actual decoding
		options.inJustDecodeBounds = false;
		options.inTempStorage = new byte[128];
		while (true) {
			try {
				options.inSampleSize = (int) sampleSize;
				bitMapImage = BitmapFactory.decodeFile(filePath, options);
				break;
			} catch (Exception ex) {
				try {
					sampleSize = sampleSize * 2;
				} catch (Exception ex1) {
				}
			}
		}
		return bitMapImage;
	}

	/**
	 * 缩放图片。
	 * @param orginal_bitmap
	 * @param screen_width
	 * @param screen_height
	 * @param bitmap_width
	 * @param bitmap_height
	 * @return
	 */
	public static Bitmap resizeBitMap(InputStream orginal_bitmap,int screen_width,int screen_height){
		Bitmap bitmap = null;
		int scale;
		Options options = new Options();
		options.inJustDecodeBounds = true;
		System.out.println("bitmap ---------------orginal_bitmap-------- : "+ orginal_bitmap);
		BitmapFactory.decodeStream(orginal_bitmap, null, options);
		System.out.println("bitmap ------------------------ : "+ bitmap);
		int bitmap_width = options.outWidth;
		int bitmap_height = options.outHeight;
		float bitmap_value = options.outHeight/options.outWidth;
		float screen_value = screen_height/screen_width;
		if(bitmap_value> screen_value){
			scale = (int)bitmap_height/screen_height+1;
			options.inSampleSize = scale;
		}else{
			scale = (int)bitmap_width / screen_width +1;
			options.inSampleSize = scale;
		}
		System.out.println("options.insamplesize:"+options.inSampleSize);
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeStream(orginal_bitmap, null, options);
	}
	
	
	/**
	 * 
	 * @param bytes
	 * @param opts
	 * @return
	 */
	public static Bitmap getPicFromBytes(byte[] bytes,
			BitmapFactory.Options opts) {
		if (bytes != null)
			if (opts != null)
				return BitmapFactory.decodeByteArray(bytes, 0, bytes.length,
						opts);
			else
				return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
		return null;
	}

	public static byte[] readStream(InputStream inStream) throws Exception {
		byte[] buffer = new byte[1024];
		int len = -1;
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		byte[] data = outStream.toByteArray();
		outStream.close();
		inStream.close();
		return data;
	}

	/**
	 * 等比例缩放
	 * @param bitmap
	 * @param scalX 屏幕宽度
	 * @param scalY 屏幕高度
	 * @return
	 * TODO:可以继续优化
	 */
	public static Bitmap adaptive(Bitmap bitmap ,float screen_width,float screen_height) {
		Matrix matrix = new Matrix();
		Bitmap newbmp = null;
		float width = bitmap.getWidth();// 获取资源位图的宽
		float height = bitmap.getHeight();// 获取资源位图的高
		float scale = 1.0f;
		if(width > screen_width || height > screen_height){
			float screenValue = screen_height/screen_width;
			float bitmapValue = height/width;
			if(bitmapValue < screenValue){
				scale = width/screen_width;
				newbmp = Bitmap.createScaledBitmap(bitmap, (int)screen_width, (int)(height/scale), true);
				System.out.println("<width : "+newbmp.getWidth()+"height : "+newbmp.getHeight());
			}else{
				scale = height/screen_height;
				newbmp = Bitmap.createScaledBitmap(bitmap, (int)(width/scale), (int)screen_height, true);
				System.out.println(">width : "+newbmp.getWidth()+"height : "+newbmp.getHeight());
			}
		}else {
			return  bitmap;
		}
		System.out.println("scale : "+ scale);
		matrix.postScale(scale, scale);// 获取缩放比例
		return newbmp;
	}
	
	public static Bitmap adaptive2(Bitmap bitmap ){
		Bitmap newbmp = null;
		float width = bitmap.getWidth();// 获取资源位图的宽
		float height = bitmap.getHeight();// 获取资源位图的高
		float scale = 1.0f;
		if(height > 200.0f && height<=500.0f){
			scale = 200.f/height;
			newbmp = Bitmap.createScaledBitmap(bitmap, (int)(width*scale),200, true);
			return newbmp;
		}else if(height > 400.0f  ){
			scale = 300.f/height;
			newbmp = Bitmap.createScaledBitmap(bitmap, (int)(width*scale),300, true);
			return newbmp;
		}else if(height > 600.0f && height <= 800.0f){
			scale = 300.f/height;
			newbmp = Bitmap.createScaledBitmap(bitmap, (int)(width*scale),300, true);
			return newbmp;
		}else{
			scale = 350.f/height;
			newbmp = Bitmap.createScaledBitmap(bitmap, (int)(width*scale),350, true);
			return newbmp;
		}
	}
	

	/**
	 * get clean string from SpannableStringBuilder
	 * @param builder
	 * @return
	 */
	public static String getStringFromEditable(SpannableStringBuilder builder) {
		String str = builder.toString();
		StringBuilder result = new StringBuilder();
		String seperator = "" + Constant.IMG_CHAR;
		int start = 0;
		while (start < str.length()) {
			int index = str.indexOf(seperator, start);
			if (index < 0) {
				String nStr = str.substring(start, str.length());
				result.append(nStr);
				break;
			}
			else {
				String nStr = str.substring(start, index);
				result.append(nStr);
			}
			int end = str.indexOf(seperator, index + 1);
			if (end < 0) {
				// error to deal with
				start = index + seperator.length();
				continue;
			}
			start = end + seperator.length();
		}
		return result.toString();
	}
	
	/**
	 * get images Uri array from SpannableStringBuilder
	 * @param builder
	 * @return null or the array Uri[]
	 */
	public static Uri[] getUrisFromEditable(SpannableStringBuilder builder) {
		String str = builder.toString();
		String seperator = "" + Constant.IMG_CHAR;
		int start = 0;
		ArrayList<String> seqList = new ArrayList<String>();
		while (start < str.length()) {
			int index = str.indexOf(seperator, start);
			if (index < 0) {
				break;
			}
			int end = str.indexOf(seperator, index + seperator.length());
			if (end < 0) {
				// error to deal with
				break;
			}
			if (index < end) {
				String seqStr = str.substring(index + seperator.length(), end);
				seqList.add(seqStr);
			}
			start = end + seperator.length();
		}
		if (seqList.size() == 0) {
			return null;
		}

		HashMap<String, Uri> map = new HashMap<String, Uri>();
		if (builder instanceof SpannableStringBuilder) {
			System.out.println("Builder is right!!");
			MyImageSpan[] spans = null;
			//    	SpannableString[] spans = builder.getSpans(0, builder.length(), Class.forName("android.text.SpannableString");
			try {
				Class cl = Class.forName("cn.aiworks.note.utils.MyImageSpan");
				int length = builder.length();
				spans = (MyImageSpan[]) builder.getSpans(0, length, cl);
			}
			catch (ClassNotFoundException e) {
				System.out.println("Nonthing");
			}
			finally {
				System.out.println("Nonthing");
			}
			//    	SpannableString[] spans = builder.getSpans(0, builder.length(), SpannableString.class);


			if (null != spans) {
				System.out.println("spans count: " + spans.length);
				for (MyImageSpan span : spans) {
					if (span instanceof ImageSpan) {
						map.put("" + span.sequence, span.uri);
						System.out.println("Get a Span!!" + span.uri);
					}
					else {
						System.out.println("Not a span");
					}
				}
			}
			System.out.println("End of gettting span");
		}
		
		int count = 0;
		Iterator it = seqList.iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			if (map.get(key) != null) {
				++count;
			}
		}
		if (0 == count) {
			return null;
		}
		Uri[] result = new Uri[count];
		it = seqList.iterator();
		int index = 0;
		while (it.hasNext()) {
			String key = (String) it.next();
			if (map.get(key) != null) {
				result[index] = map.get(key);
				++index;
			}
		}
		
		return result;
	}
	
	public static String stringFromUris(Uri[] uris) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < uris.length;++i) {
			if (null != uris[i]) {
			builder.append(uris[i].toString());
			builder.append(Constant.IMG_CHAR);
			}
		}
		return builder.toString();
	}
	
	public static Uri[] urisFromString(String str) {
		if (null == str || str.length() == 0) {
			return null;
		}
		String seperator = "" + Constant.IMG_CHAR;
		String[] uriList = str.split(seperator);
		if (uriList.length == 0) {
			return null;
		}
		Uri[] result = new Uri[uriList.length];
		for (int i = 0; i < uriList.length; ++i) {
			result[i] = Uri.parse(uriList[i]);
		}
		return result;
	}
	
    static public Bitmap getThumbFromUri(Uri imageUri, Activity act) {
    	return getThumbFromUri(imageUri, act, 500, 200);
    }
    
    static public Bitmap getThumbFromUri(Uri imageUri, Activity act, int aMaxWidth, int aMaxHeight) {
    	if (null == imageUri) {
            return null;
        }
        InputStream input = null;
        try {
            input = act.getContentResolver().openInputStream(imageUri);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (null == input) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(input, null, options);
        float origWidth = options.outWidth;
        float origHeight = options.outHeight;
        float scaledWidth = origWidth;
        float scaledHeight = origHeight;
        float scale = 1;
        float maxWidth = aMaxWidth;
        float maxHeight = aMaxHeight;

        int rotate = getImageRotation(imageUri, act.getApplicationContext());
        if (rotate % 180 != 0) {
            maxWidth = aMaxHeight;
            maxHeight = aMaxWidth;
        }
        if (origWidth > maxWidth || origHeight > maxHeight) {
            scale = origWidth / maxWidth;
            scaledHeight = origHeight / scale;
            if (scaledHeight > maxHeight) {
                scale = origHeight / maxHeight;
                scaledHeight = maxHeight;
                scaledWidth = scaledHeight * origWidth / origHeight;
            }
            else {
                scaledWidth = maxWidth;
                scaledHeight = scaledWidth * origHeight / origWidth;
            }
        }
        else {
        	scaledHeight = maxHeight;
        	scaledWidth = scaledHeight * origWidth / origHeight;
        }
        if (scale < 1) {
            scale = 1;
        }

        // get the thumbnail bitmap
        options.inJustDecodeBounds = false;
        options.inSampleSize = (int)scale;

        Bitmap thumb = null;
        try {
            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            bitmapOptions.inSampleSize = (int)scale;
            bitmapOptions.inDither=true;//optional
            bitmapOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//optional
            input = act.getContentResolver().openInputStream(imageUri);
            thumb = BitmapFactory.decodeStream(input, null, bitmapOptions);

            thumb = getFormalBitmap(rotate, thumb);
            if (rotate % 180 != 0) {
                float tmp = scaledWidth;
                scaledWidth = scaledHeight;
                scaledHeight = tmp;
            }
            thumb = Bitmap.createScaledBitmap(thumb, (int)scaledWidth, (int)scaledHeight, true);
        }catch(Exception e){
            // do nothing
        }
        return thumb;

    }
    
	static public String getAbsoluteImagePathFromUri(Uri imageUri, Context ctx) throws Exception {
		String[] proj = { MediaColumns.DATA, MediaColumns.DISPLAY_NAME };

		if (imageUri.toString().startsWith(
				"content://com.android.gallery3d.provider")) {
			imageUri = Uri.parse(imageUri.toString().replace(
					"com.android.gallery3d", "com.google.android.gallery3d"));
		}

		String filePath = "";
		Cursor cursor = null;
		String imageUriString = imageUri.toString();
		if (imageUriString.startsWith("content://com.google.android.gallery3d")
				|| imageUriString
						.startsWith("content://com.google.android.apps.photos.content")
				|| imageUriString
						.startsWith("content://com.android.providers.media.documents")
				|| imageUriString
						.startsWith("content://com.google.android.apps.docs.storage")) {
			filePath = imageUri.toString();
		} else {
			try {
				cursor = ctx.getContentResolver().query(imageUri, proj,
						null, null, null);
				cursor.moveToFirst();
				filePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaColumns.DATA));
				cursor.close();
			} catch (Exception e) {
				Log.i(tag, e.getMessage());
				System.out.println("getAbsoluteImagePathFromUri:----> "+ filePath);
				throw new Exception();
			}finally{
				if(cursor != null)
					cursor.close();
			}
		}
		return filePath;
	}

    static private int getImageRotation(Uri uri, Context ctx) {
    	String filePath = "";
		try {
			filePath = getAbsoluteImagePathFromUri(uri, ctx);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
        int rotate = 0;
        try {
            // now the filePath is absolute Image Path
            //String filePath = getAbsoluteImagePathFromUri(Uri.parse(fileImage));
            ExifInterface exif = new ExifInterface(filePath);
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = -90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }
	
	static private Bitmap getFormalBitmap(int rotate, Bitmap bitmap) {
        if (rotate != 0) {
            Matrix matrix = new Matrix();
            matrix.setRotate(rotate);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                    bitmap.getHeight(), matrix, false);
        }

		return bitmap;
	}
	
	public static File download(String serverpath, String savedpath,ProgressDialog pd) {
		try {
			File file = new File(savedpath);
			HttpClient client = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(serverpath);
			HttpResponse response = client.execute(httpGet);
			if (response.getStatusLine().getStatusCode() == 200) {
				int max  = (int)response.getEntity().getContentLength();
				pd.setMax(max);
				InputStream is = response.getEntity().getContent();
				FileOutputStream fos = new FileOutputStream(file);
				byte[] buffer = new byte[1024];
				int len = 0; 
				int total = 0;
				while ((len = is.read(buffer))!=-1){
					fos.write(buffer, 0, len);
					total+=len;
					pd.setProgress(total);
				}
				fos.flush();
				fos.close();
				return file;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
    /**
     * 返回网络状态
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) { 
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context 
                .getSystemService(Context.CONNECTIVITY_SERVICE); 
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo(); 
            if (mNetworkInfo != null) { 
                return mNetworkInfo.isAvailable(); 
            } 
        } 
        return false; 
    }
	

	static public String getEmailSubject() {
		String emailSubject = "笔记 ";
		long time = System.currentTimeMillis();
		emailSubject = emailSubject + getDate(time);
		return emailSubject;
	}
	
	static private String getDate(long timeStamp){
	    try{
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm",Locale.CHINA);
	        Date netDate = (new Date(timeStamp));
	        return sdf.format(netDate);
	    }
	    catch(Exception ex){
	        return "";
	    }
    }
	static MessageDigest digest = null;
	
	
	/**
	 * 返回字符串的md5值。
	 * @param srcStr
	 * @return
	 */
	public static String getMd5Digest(String srcStr){
		 	try {
		 		digest = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		 	digest.update(srcStr.getBytes());
		return bufferToHex(digest.digest());
	}
  
    private static String bufferToHex(byte bytes[]) {  
        return bufferToHex(bytes, 0, bytes.length);  
    }  
  
    private static String bufferToHex(byte bytes[], int m, int n) {  
        StringBuffer stringbuffer = new StringBuffer(2 * n);  
        int k = m + n;  
        for (int l = m; l < k; l++) {  
            appendHexPair(bytes[l], stringbuffer);  
        }  
        return stringbuffer.toString();  
    } 
	
    private static void appendHexPair(byte bt, StringBuffer stringbuffer) {  
        char c0 = hexDigits[(bt & 0xf0) >> 4];// 取字节中高 4 位的数字转换, >>> 为逻辑右移，将符号位一起右移,此处未发现两种符号有何不同   
        char c1 = hexDigits[bt & 0xf];// 取字节中低 4 位的数字转换   
        stringbuffer.append(c0);  
        stringbuffer.append(c1);  
    }
    protected static char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6',  
        '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
}
