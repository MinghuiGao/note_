package cn.aiworks.note;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import android.animation.AnimatorSet.Builder;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.Color;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.text.ClipboardManager;
import android.text.Editable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.view.View.OnTouchListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.Toast;
import cn.aiworks.note.SettingActivity.AuthListener;
import cn.aiworks.note.SharePopupWindow.MyAdapter;
import cn.aiworks.note.application.EverInputApplication;
import cn.aiworks.note.constant.AppKey;
import cn.aiworks.note.constant.Constant;
import cn.aiworks.note.constant.Constants;
import cn.aiworks.note.constant.Constants_sina;
import cn.aiworks.note.constant.SDKConst;
import cn.aiworks.note.domain.WeiboErrorInfo;
import cn.aiworks.note.listener.HomeWatcher;
import cn.aiworks.note.listener.OnHomePressedListener;
import cn.aiworks.note.sinaWeibo.AccessTokenKeeper;
import cn.aiworks.note.utils.MyImageSpan;
import cn.aiworks.note.utils.Utils;
import cn.aiworks.note.view.MyPopupWindow;
import cn.aiworks.note.view.PopupToast;

import com.aispeech.AIError;
import com.aispeech.AIResult;
import com.aispeech.common.AIConstant;
import com.aispeech.common.JSONResultParser;
import com.aispeech.export.engines.AICloudASREngine;
import com.aispeech.export.listeners.AIASRListener;
import com.aispeech.speech.SpeechReadyInfo;
import com.evernote.client.android.EvernoteUtil;
import com.evernote.client.conn.mobile.FileData;
import com.evernote.edam.type.Resource;
import com.evernote.edam.type.ResourceAttributes;
import com.google.gson.Gson;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMessage;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboDownloadListener;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.WeiboParameters;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.exception.WeiboShareException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.legacy.StatusesAPI;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.youdao.note.sdk.openapi.IYNoteAPI;
import com.youdao.note.sdk.openapi.SendNoteRequest;
import com.youdao.note.sdk.openapi.YNoteAPIFactory;
import com.youdao.note.sdk.openapi.YNoteAttachment;
import com.youdao.note.sdk.openapi.YNoteContent;
import com.youdao.note.sdk.openapi.YNoteImageContent;
import com.youdao.note.sdk.openapi.YNotePlainTextContent;

public class TapeActivity extends ParentActivity implements OnClickListener,IWeiboHandler.Response{
	
	// 存放字符串的集合
	ArrayList<String> strs = new ArrayList<String>();
	
	HashMap<String,Uri> pics = new HashMap<String,Uri>();
	
	String tag = "tapActivity";
	public static final String TAG = TapeActivity.class.getCanonicalName();
	// jar 中的类。
	AICloudASREngine engine;
    int engine_status;

	// 声明
	ImageView tv_clear_all;
	ImageView tv_copy;

	EditText editor;
	ImageView bt_keyboard;
	ImageView bt_pic;
	ImageView bt_share;
	ImageView bt_bottom_feature;

	RelativeLayout rl_tape;
	
	ImageView bt_main_tape;
	ImageView iv_taping_circle;// 添加缩放动画
	ImageView iv_loading;//加载动画
	
	int imageSequence;
	
	
	private FragmentManager fragmentManager;
	private FragmentTransaction transaction;


	StringBuffer sb_old = new StringBuffer("");
	private MyPopupWindow myPopupWindow_success;
	private ProgressDialog pd;
	Handler handler = new Handler() {
		//成功的pop

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			PopupToast pt_share_to_sina = new PopupToast(TapeActivity.this);
			switch (msg.what) {
			case Constant.DIALOG_SHOW:
				
				
				break;
			case Constant.WEIBO_SPACE:
				pt_share_to_sina.showMessage(TapeActivity.this,getResources().getString(R.string.weibo_space), R.drawable.fail,null);
				break;
			case Constant.SHARE_TO_SINA_FAIL_140:
				pt_share_to_sina.showMessage(TapeActivity.this,getResources().getString(R.string.weibo_share_fail_140), R.drawable.fail,null);
				break;
			case Constant.SHARE_TO_SINA_SUCCESS:
				pt_share_to_sina.showMessage(TapeActivity.this,(String)msg.obj, R.drawable.success,null);
				break;
			case Constant.SHARE_TO_SINA_FIAL:
				pt_share_to_sina.showMessage(TapeActivity.this, (String)msg.obj, R.drawable.fail, null);
				break;
			case Constant.SHARE_TO_SINA_EXCEPTION:
				pt_share_to_sina.showMessage(TapeActivity.this, (String)msg.obj, R.drawable.fail, null);
				break;
			case Constant.LOG_IN_SUCCESS:
				shareToSina();
				break;
			case Constant.FROM_SHAER2EVERNOTE_START:
				pd.show();
				break;
			case Constant.FROM_SHAER2EVERNOTE_OVER:
				pd.dismiss();
				break;
			case Constant.ERROR_CREATING_NOTESTORE:
				pd.dismiss();
				PopupToast popToast = new PopupToast(TapeActivity.this);
				popToast.showMessage(TapeActivity.this, R.string.error_creating_notestore, R.drawable.fail, null);
				break;
			case Constant.SEND_MSG_FAILED:
				PopupToast pt = new PopupToast(TapeActivity.this);
				pt.showMessage(TapeActivity.this, getResources().getString(R.string.sms_fail), R.drawable.fail, null);
				break;
            case Constant.MSG_SHARE_EMPTY:
                showMessage(R.string.text_empty, R.drawable.bugeili);
                break;
            case Constant.MSG_SHARE_SUCCESS:
                showMessage(R.string.sms_success, R.drawable.success);
                break;
            case Constant.MSG_SHARE_FAILED:
                showMessage(R.string.sms_fail, R.drawable.fail);
                break;
            case Constant.MSG_RECORD_FAILED:
                showMessage(R.string.record_fail, R.drawable.fail);
                break;
            case Constant.MSG_RECORD_SUCCESS:
                showMessage(R.string.record_success, R.drawable.success);
                break;
            case Constant.MSG_RECORD_SUCCESS_SHARE:
                showMessage(R.string.record_success_share, R.drawable.success);
                break;
            case Constant.MSG_NETWORK_ERROR:
                showMessage(R.string.network_error, R.drawable.bugeili);
                break;
			case Constant.CONTENT_IS_EMPTY:
				showMessage(R.string.copy_text_empty, R.drawable.success);
				break;
			case Constant.COPY_TO_CLIPBOARD:
				showMessage(R.string.copy_success, R.drawable.success);
				break;
			case Constant.START_TAPE_FROM_INTRODUCTION:
                String et = Utils.getStringFromEditable((SpannableStringBuilder)editor.getText());
                showShareView();
                break;
            case Constant.DIALOG_DISMISS:
                if(myPopupWindow_success.isShowing()){
                    myPopupWindow_success.update();
                    myPopupWindow_success.dismiss();
                    myPopupWindow_success = null;
                }
		        AlertDialog.Builder builder = new AlertDialog.Builder(TapeActivity.this);
		        AlertDialog success = builder.create();
		        View dialogView = View.inflate(TapeActivity.this, R.layout.tip_success, null);
				success.setView(dialogView, 0, 0, 0, 0);
				success.show();
				break;
			case Constant.FILE_NOT_FOUND:
				//TODO: alert dialog.
				showMessage(R.string.image_add_none, R.drawable.bugeili);
				break;
				
			case Constant.SHARE_DISMISS_DIALOG:
                progressDialog.dismiss();
                editor.setCursorVisible(true);
                break;
			case Constant.YOUDAO_UNINSTALLED:
				PopupToast youdaoUninstalled = new PopupToast(TapeActivity.this);
				youdaoUninstalled.showMessage(TapeActivity.this, R.string.youdao_uninstalled, R.drawable.fail, null);
				break;
			default:
				break;
			}
		}
	};
	// 引导
	private int currentVersion;
	private int lastVersion;
	private SharedPreferences sp;
	private String oldString = "";
	PackageInfo info = null;
	ContentResolver resolver;
	//屏幕宽高，用于缩放
	private float scalX;
	private float scalY;
	private Point winSize;

    private boolean textChanged;
    
    boolean isFromSplash = false;
    
    //sina weibo
    /** 微博微博分享接口实例 */
    private IWeiboShareAPI  mWeiboShareAPI = null;
    /** 微博 Web 授权类，提供登陆等功能  */
    private WeiboAuth mWeiboAuth;
    /** 当前 Token 信息，封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能  */
    private Oauth2AccessToken mAccessToken;
    /** 注意：SsoHandler 仅当 SDK 支持 SSO 时有效 */
    private SsoHandler mSsoHandler;
    
    private File cache;
    
    //youdao Note
    public YNoteContent mContent;
    public IYNoteAPI yNoteApi ;
    @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mHomeWatcher = new HomeWatcher(this); 
		mHomeWatcher.setOnHomePressedListener(new OnHomePressedListener() {  
            @Override  
            public void onHomePressed() {  
                Log.e(TAG, "onHomePressed");
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
                imm.hideSoftInputFromWindow(editor.getWindowToken(),0);
            }  
            @Override  
            public void onHomeLongPressed() {  
                Log.e(TAG, "onHomeLongPressed");  
            }  
        });  
        mHomeWatcher.startWatch();
        //sina weibo
        // 创建微博分享接口实例
        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, Constants_sina.APP_KEY);
        refreshToken();
        yNoteApi = SDKConst.getYNoteAPI(TapeActivity.this);
        //register application
        boolean isYNoteRegistered = yNoteApi.isRegistered();
        if(!isYNoteRegistered){
        	yNoteApi.registerApp();
        }
		//2创建微博授权类对象
		mWeiboAuth = new WeiboAuth(this,Constants_sina.APP_KEY,Constants_sina.REDIRECT_URL,Constants_sina.SCOPE);
        if (savedInstanceState != null) {
            mWeiboShareAPI.handleWeiboResponse(getIntent(), this);
        }
		disableSleep();
		setContentView(R.layout.tape2);
		resolver  = getContentResolver();
		// 出事话控件
		initViews();
		//获取版本信息及版本号
		try {
			info = getPackageManager().getPackageInfo("cn.aiworks.note", 0);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (info != null) {
			currentVersion = info.versionCode;
		}
		sp = getSharedPreferences("version_code", MODE_PRIVATE);
		lastVersion = sp.getInt("version", 0);
		// 检测edittext
		editor.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
				System.out.println("afterTextChange");
                if (0 == engine_status) {
                    int length = editor.getText().toString().length();
                    setHeaderEnabled(length > 0);
                }
            }
        });

		// init engine
        createEngine();
        
        showNormals();
        isFromSplash = sp.getBoolean("fromSplash", false);
        if(!isFromSplash){
        	changeStatus(1);
        	isFromSplash = true;
        	sp.edit().putBoolean("fromSplash", true).commit();
        }else{
    		//add umeng
    		UmengUpdateAgent.update(this);
    		UmengUpdateAgent.setUpdateOnlyWifi(false);
        }
        
        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);
        pd = new ProgressDialog(this);
		pd = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
		pd.setMessage(getResources().getString(R.string.evernote_makingnote));
        application = (EverInputApplication) TapeActivity.this.getApplication();
        application.getAppConstant().put("tapeHandler", handler);
//      application.getAppConstant().put("tapePd", pd);
        
        cache = getCacheDir();
    }

	@Override
	protected void onStart() {
		super.onStart();
		// 隐藏键盘
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
	}
	
	
	/**
	 * 判断是否是新版本进入引导activity
	 */
	private void beginIntroduction(){
		if (currentVersion > lastVersion) {// 第一次启动
			IntroductionActivity.start(TapeActivity.this, handler);
			// 将当前版本写入sp，下次启动据此判断。
			sp.edit().putInt("version", currentVersion).commit();
			currentVersion = lastVersion;
		}
	}
	
    private OnTouchListener editorListener;
	/**
	 * init all the views.
	 */
	@SuppressWarnings("deprecation")
	private void initViews() {
		sp2 = TapeActivity.this.getSharedPreferences("Content", MODE_PRIVATE);
		tv_clear_all = (ImageView) findViewById(R.id.tv_clear_all);
        tv_copy = (ImageView) findViewById(R.id.tv_copy);
        editor = (EditText) findViewById(R.id.et_content);
        editorListener = new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return 0 != engine_status;
            }
        };
        editor.setOnTouchListener(editorListener);
        loadEditorContents();

		bt_keyboard = (ImageView) findViewById(R.id.bt_keyboard);
		bt_pic = (ImageView) findViewById(R.id.bt_pic);
		bt_share = (ImageView) findViewById(R.id.bt_share);
		bt_bottom_feature = (ImageView) findViewById(R.id.bt_bottom_feature);
		// 录音按钮
		rl_tape = (RelativeLayout) findViewById(R.id.rl_tape);

		bt_main_tape = (ImageView) findViewById(R.id.bt_main_tape);
		iv_taping_circle = (ImageView) findViewById(R.id.iv_taping_circle);
		iv_loading = (ImageView) findViewById(R.id.iv_loading);

		tv_clear_all.setOnClickListener(this);
		bt_keyboard.setOnClickListener(this);
		bt_pic.setOnClickListener(this);
		// tap button
		bt_main_tape.setOnClickListener(this);
		// bt_main_tape.setA
		rl_tape.setOnClickListener(this);

		bt_share.setOnClickListener(this);
		tv_copy.setOnClickListener(this);
		bt_bottom_feature.setOnClickListener(this);

		Display display = getWindowManager().getDefaultDisplay();
//		display.getSize(winSize)
		scalX = display.getWidth();
		scalY = display.getHeight();
		
		//设置分享显示按钮
		setHeaderEnabled(false);
//		if(isContentVoid()){
//			bt_share.setClickable(false);
//			bt_share.setImageResource(R.drawable.share_forbidden);
//		}
		
	}

	private void onPost() {
		//stopEngine();
	}
	
	/**
	 * 设置选中的tab
	 * 
	 * @param index
	 */
	private void setMenuSelection(int index) {}

	/**
	 * 清楚tab的选中状态。
	 */
	private void clearSelection() {}

	/**
	 * 开启本活动。
	 * 
	 * @param context
	 */
	public static void start(Context context) {
		Intent intent = new Intent(context, TapeActivity.class);
		context.startActivity(intent);
	}
	/**
	 * 从splash开启
	 * @param context
	 * @param flag
	 */
	public static void start(Context context,boolean flag){
		Intent intent = new Intent(context, TapeActivity.class);
		context.startActivity(intent);
	}
	
	boolean isTape = false;
	private String content;

	@Override
	public void onClick(View v) {
		
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
		switch (v.getId()) {
		
		case R.id.tv_clear_all://重新开始
			// hide the kb
			imm.hideSoftInputFromWindow(editor.getWindowToken(),0);
			editor.setText("");
			saveEditorContents();
			break;
		case R.id.tv_copy:
			// hide the kb
			imm.hideSoftInputFromWindow(editor.getWindowToken(),0);
			System.out.println("tv_copy is clicked....");
            copyToClipBoardEx();
            break;
        case R.id.bt_keyboard:
            // 判断键盘是否弹出，并弹出键盘
            System.out.println("keyboard is clicked.");
            Window window = getWindow();
            LayoutParams attributes = window.getAttributes();
            System.out
                .println(WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED
                        + "----"
                        + WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
                        + "-cangle --" + attributes.softInputMode);
            //if (getWindow().getAttributes().softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            System.out.println("same");
            getWindow()
                .setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED);
            ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                .toggleSoftInput(R.id.et_content, 0);
            //}
            break;
        case R.id.bt_pic:
            // hide the kb
            imm.hideSoftInputFromWindow(editor.getWindowToken(),0);
            // 本地的图片选择，
            Intent intent = new Intent(Intent.ACTION_PICK, null);

			
			/**
			 * 下面这句话，与其它方式写是一样的效果，如果：
			 * intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			 * intent.setType(""image/*");设置数据类型
			 * 如果朋友们要限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型"
			 */
			intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
			Log.i(tag, "bt_pic is clicked...");
			startActivityForResult(intent, Constant.SELECT_PIC_FROM_LOCAL);
			break;
		case R.id.rl_tape:
			System.out.println("R.id.rl_tape is clicked..");
		case R.id.bt_main_tape:
			switch (engine_status) {
			case 0:
				changeStatus(1);
				break;
			case 1:
				changeStatus(2);
				break;
			default:
				changeStatus(0);
                int end = Math.max(editor.getSelectionEnd(), 0);
                editor.setSelection(end);
                break;
            }
            break;
        case R.id.bt_share:
            // hide the kb
            imm.hideSoftInputFromWindow(editor.getWindowToken(),0);
/*            // text included image chars
            String totalText = editor.getText().toString().trim();
            if (totalText.length() <= 0) {
                handler.sendEmptyMessage(Constant.MSG_SHARE_EMPTY);
                return;
            }*/
            
            showShareView();
			break;
		case R.id.bt_bottom_feature:
			// hide the kb
			imm.hideSoftInputFromWindow(editor.getWindowToken(),0);
			Intent setting = new Intent(this,SettingActivity.class);
			TapeActivity.this.startActivity(setting);
			overridePendingTransition(R.anim.slid_from_right_in, R.anim.nochange_out);
			break;
		default:
			break;
		}
	}

	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch (requestCode) {
		// case Constant.SELECT_PIC_FROM_LOCAL:
		case Constant.SELECT_PIC_FROM_LOCAL:
			if (data != null) {
                Uri uri = data.getData();
                
                String filePath = null ;
                String fileName  = null;
                String fileExtName = null;
                File cacheFile = null;
                try {
					filePath= Utils.getAbsoluteImagePathFromUri(uri, this);
					fileName = filePath.substring(filePath.lastIndexOf("/")+1);
					fileExtName = fileName.substring(fileName.lastIndexOf(".")+1);
					Log.i("gaomh","picture absolute path :----->"+ filePath);
					Log.i("gaomh","picture fileName :----->"+ fileName);
					Log.i("gaomh","picture fileExtName :----->"+ fileExtName);
				} catch (Exception e) {
					Toast.makeText(this, "filePath not found!", 0).show();
					e.printStackTrace();
				}
                
                //copy file
                try {
					FileInputStream fis = new FileInputStream(new File(filePath));
					BufferedInputStream bis = new BufferedInputStream(fis);
					cacheFile = new File(cache.getAbsolutePath() +"/"+Utils.getMd5Digest(fileName)+"."+fileExtName);
					if(!cacheFile.exists()){
						cacheFile.createNewFile();
					}
					Log.i("gaomh", "cacheFile.getAbsolutePath----> "+cacheFile.getAbsolutePath());
					FileOutputStream fos = new FileOutputStream(cacheFile);
					BufferedOutputStream bos = new BufferedOutputStream(fos);
					byte[] buffer = new byte[1024];
					int len;
					while((len = bis.read(buffer)) != -1){
						bos.write(buffer,0,len);
					}
					bos.close();
					fos.close();
					bis.close();
					fis.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
                //generate uri
                Uri cacheFileUri = Uri.fromFile(cacheFile);
                Bitmap bitmap = Utils.getThumbFromUri(cacheFileUri, this);
                if (null == bitmap) {
                    handler.sendEmptyMessage(Constant.FILE_NOT_FOUND);
                }
                else {
                    setBitmapToEdittext(bitmap, uri);
                }
            }
            break;

        case Constant.SHARE_TO_ELEPHANT_REQ: // EverNote result!!!
			if(data != null){
				//处理印象的返回结果。
			}
            break;
		default:
			break;
		}

        // deal with SharePopupWindow result
        switch (resultCode) {
            case Constant.SHARE_EMAIL:
                sendEmailOrSMS(true);
                break;
            case Constant.SHARE_SMS:
                sendEmailOrSMS(false);
                break;
            case Constant.SHARE_WEIXIN:
                sendToWeixin(false);
                break;
            case Constant.SHARE_FRIENDS:
                sendToWeixin(true);
                break;
            case Constant.SHARE_COPY:
                copyToClipBoardEx();
                break;
            case Constant.SHARE_EVERNOTE:
                sendToEverNote();
                break;
            case Constant.SHARE_SINA_WEIBO:
            	shareToSina();
                break;
            case Constant.SHARE_YOUDAO:
            	sendToYoudao();
            	break;
            default:
                break;
        }
        // SSO 授权回调
        // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResult
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void setBitmapToEdittext(Bitmap bitmap, Uri uri) {
        SpannableString spannableString = getSpannableString(bitmap, imageSequence, uri);
        imageSequence += 1;
		int start = Math.max(editor.getSelectionStart(), 0);
		int end = Math.max(editor.getSelectionEnd(), 0);
		String seperator = "\n";
		if (start == end) {
			editor.getText().insert(start, seperator);
		}
		else {
			editor.getText().replace(start, end, seperator);
		}
		start += seperator.length();
		editor.getText().insert(start, spannableString);
		start += spannableString.length();
        editor.getText().insert(start,seperator);
		System.out.println("Add Image: " + uri);
	}
	
	/**
	 * 裁剪图片方法实现
	 * 
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {	
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 150);
		intent.putExtra("outputY", 150);
		intent.putExtra("return-data", true);
		Log.i(tag, "start to cut");
		startActivityForResult(intent, 3);
	}

	private class AISpeechListenerImpl implements AIASRListener {

		public void onReadyForSpeech(SpeechReadyInfo params) {
			Log.i(tag, "请说话...");
		}

		public void onBeginningOfSpeech() {
			Log.i(tag, "检测到说话...");
		}

		public void onEndOfSpeech() {
			Log.i(tag, "检测到语音停止...");
		}

		public void onRmsChanged(float rmsdB) {
			Log.i(tag, "RmsDB = " + rmsdB);
		}

		public void onError(AIError error) {
			Log.i(tag, "error:" + error.toString());
			dealWithASRError();
            handler.sendEmptyMessage(Constant.MSG_RECORD_FAILED);
		}

		public void onInit(int status) {
			Log.i(tag, "Init result " + status);
			if (status == AIConstant.OPT_SUCCESS) {
				Log.i(tag, "初始化成功!" + status);
				bt_main_tape.setEnabled(true);
			} else {
				Log.i(tag, "初始化失败!" + status);
				handler.sendEmptyMessage(Constant.MSG_RECORD_FAILED);
			}
		}

		public void onResults(AIResult results) {

            if (0 == engine_status) {
                return;
            }
			Log.i(TAG, results.getResultObject().toString());
			JSONResultParser parser = new JSONResultParser(
					(String) results.getResultObject());
			// 当前正在识别的结果/最终结果
			String rec = parser.getVar();
			// 已经稳定的结果
			String recd = parser.getRec();
            int ended = parser.getEof();
            if (1 == ended) {
                rec = "";
            }

            int start = Math.max(editor.getSelectionStart(), 0);
            int end = Math.max(editor.getSelectionEnd(), 0);
            oldString = recd;
            if (null != recd && recd.length() > 0) {
                editor.getText().replace(start, end, recd, 0, recd.length());
                start += recd.length();
                end = start;
                textChanged = true;
            }
            if (null != rec && rec.length() > 0) {
                editor.getText().replace(start, end, rec, 0, rec.length());
                end = start + rec.length();
                textChanged = true;
            }
            if (start == end) {
            	editor.setSelection(start);
            }
            else {
            	editor.setSelection(start, end);
            }

            if (1 == ended) {
                changeStatus(0);
            }
        }
    }


    private void createEngine() {
        engine = AICloudASREngine.getInstance();
        engine.setRTMode(AIConstant.RT_MODE_VAD);
        engine.setMaxSpeechTimeS(0);
        engine.setVadEnable(false);
        engine.setVolEnable(true);
        engine.setUseTxtPost(true);
        engine.init(this, new AISpeechListenerImpl(), AppKey.APPKEY, AppKey.SECRETKEY);
		//出事话引擎状态
		engine_status = 0;
    }
    
    @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
        imm.hideSoftInputFromWindow(editor.getWindowToken(),0);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
        imm.hideSoftInputFromWindow(editor.getWindowToken(),0);
	}
    
    @Override 
    public void onRestart() {
    	super.onRestart();
    }
    
	@Override
	public void onStop() {
		System.out.println("stop   y....");
		super.onStop();
        changeStatus(0);
        saveEditorContents();
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
        imm.hideSoftInputFromWindow(editor.getWindowToken(),0);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		saveEditorContents();
		if (engine != null) {
			engine.destory();
			engine = null;
		}
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
        imm.hideSoftInputFromWindow(editor.getWindowToken(),0);
		if(mHomeWatcher.isRegister){
			mHomeWatcher.stopWatch();
		}
	}
	
	@Override
	protected void onSaveInstanceState (Bundle outState) {
		System.out.println("save in.");
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
		imm.hideSoftInputFromWindow(editor.getWindowToken(),0);
		super.onSaveInstanceState(outState);
		saveEditorContents();
		if(mHomeWatcher.isRegister){
			mHomeWatcher.stopWatch();
		}
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		System.out.println(keyCode+"-------");
		if(keyCode == KeyEvent.KEYCODE_HOME){
	        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
	        imm.hideSoftInputFromWindow(editor.getWindowToken(),0);
	        return true;  
		}
		return super.onKeyDown(keyCode, event);
	}
	
    // TODO: add changeStatus(Recording) on first open activity
    // TODO: add changeStatus(Idle) when app when bg
	private void changeStatus(int nstatus) {
        if (engine_status == nstatus) {
            return;
        }
		switch (engine_status) {
		case 0: // idle to recording
            // TODO: check the network status, 
			if (!Utils.isNetworkConnected(getApplicationContext())) {
				showMessage(R.string.network_error, R.drawable.bugeili);
				showNormals();
				return;
			}
            // disable screen locking
            disableSleep();
			startASR();
			showRecording();
			break;
		case 1: // recording to loading or idle
            stopASR();
            if (0 == nstatus) { // to idle
                // enable screen locking
                enableSleep();
                showNormals();
            }
            else { // to loading
                showFinishing();
            }
			break;
        default: // loading to idle
            // enable screen locking
            enableSleep();
            showNormals();
            if (textChanged) {
                beginIntroduction();
            }
            break;
        }
        engine_status = nstatus;
    }

    private void startASR () {
        oldString = "";
        textChanged = false;
        engine.start();
    }

    private void stopASR () {
    	iv_loading.setVisibility(View.INVISIBLE);
        if (engine != null) {
            engine.stopRecording();
            Log.i(tag, "engine stop");
        }
    }

    private Animation circleAnimation;
    private Animation loopAnimation;
    private void showRecording () {
        // hide the kb
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
        imm.hideSoftInputFromWindow(editor.getWindowToken(),0);
        setHeaderEnabled(false);
        bt_keyboard.setVisibility(View.INVISIBLE);
        bt_pic.setVisibility(View.INVISIBLE);
        bt_share.setVisibility(View.INVISIBLE);
        bt_bottom_feature.setVisibility(View.INVISIBLE);
        
        rl_tape.setBackgroundResource(Color.TRANSPARENT);
        iv_taping_circle.setVisibility(View.VISIBLE);
        iv_taping_circle.setBackgroundResource(R.drawable.taping_circle);
		bt_main_tape.setVisibility(View.VISIBLE);
        // animation on cirle
        circleAnimation = new AlphaAnimation(1, 0);
        circleAnimation.setRepeatCount(10000);
        circleAnimation.setDuration(1000);
        circleAnimation.setRepeatMode(Animation.REVERSE);
        iv_taping_circle.setAnimation(circleAnimation);
        iv_loading.setVisibility(View.INVISIBLE);
        circleAnimation.startNow();
    }

    private void showFinishing () {
    	setHeaderEnabled(false);
        bt_keyboard.setVisibility(View.INVISIBLE);
        bt_pic.setVisibility(View.INVISIBLE);
        //设置分享按钮状态
        bt_share.setVisibility(View.INVISIBLE);

        bt_bottom_feature.setVisibility(View.INVISIBLE);


        rl_tape.setBackgroundResource(Color.TRANSPARENT);
        //隐藏tapingcricle。
        iv_taping_circle.setVisibility(View.INVISIBLE);
        iv_taping_circle.setBackgroundResource(Color.TRANSPARENT);
        
        bt_main_tape.setVisibility(View.VISIBLE);
        circleAnimation.cancel();
        //        iv_taping_circle.removeAnimation(circleAnimation);
        circleAnimation = null;
        loopAnimation = new RotateAnimation(0f, +360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        loopAnimation.setDuration(3000);
        loopAnimation.setRepeatCount(1000);
        loopAnimation.setRepeatMode(Animation.RESTART);
        loopAnimation.setInterpolator(new LinearInterpolator());
        iv_loading.setBackgroundResource(R.drawable.loading);
        iv_loading.setVisibility(View.VISIBLE);
        iv_loading.setAnimation(loopAnimation);
        loopAnimation.startNow();
    }

    private void showNormals () {
    	int length = editor.getText().toString().length();
    	setHeaderEnabled(length > 0);
        bt_keyboard.setVisibility(View.VISIBLE);
        bt_pic.setVisibility(View.VISIBLE);
        //设置分享按钮状态
        bt_share.setVisibility(View.VISIBLE);
        bt_bottom_feature.setVisibility(View.VISIBLE);

		if (null != loopAnimation) {
	        loopAnimation.cancel();
			}
			loopAnimation = null;
			if (null != circleAnimation) {
	        circleAnimation.cancel();
			}
			circleAnimation = null;
		
		rl_tape.setBackgroundResource(R.drawable.main_tape);
		iv_taping_circle.setBackgroundResource(Color.TRANSPARENT);
		iv_taping_circle.setVisibility(View.INVISIBLE);
		bt_main_tape.setVisibility(View.INVISIBLE);
		iv_loading.setBackgroundResource(Color.TRANSPARENT);
		iv_loading.setVisibility(View.INVISIBLE);
    }

    private void setHeaderEnabled(boolean enabled) {
        tv_clear_all.setClickable(enabled);
        tv_copy.setClickable(enabled);
        if (enabled) {
            tv_clear_all.setBackgroundResource(R.drawable.del_);
            tv_copy.setBackgroundResource(R.drawable.tape_copy_);
            bt_share.setClickable(true);
//            bt_share.setImageResource(R.drawable.share_);
            bt_share.setBackgroundResource(R.drawable.share_);
            bt_share.setVisibility(View.VISIBLE);
        }
        else {
        	tv_clear_all.setBackgroundResource(R.drawable.del_forbidden);
        	tv_copy.setBackgroundResource(R.drawable.copy_forbidden);
            bt_share.setClickable(false);
            bt_share.setBackgroundResource(R.drawable.share_forbidden);
            bt_share.setVisibility(View.VISIBLE);
        }
    }

    private void dealWithASRError() {
    	runOnUiThread(new Runnable() {
    		@Override
    		public void run() {
    	    	int end = Math.max(0, editor.getSelectionEnd());
    	    	editor.setSelection(end);
    	    	changeStatus(0);
    		}
    	});
    }
    
    private PowerManager.WakeLock wake_lock;
    /**
     * 禁止休眠
     */
    private void disableSleep() {
    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);  
    }

    /**
     * 唤醒休眠
     */
    private void enableSleep() {
    	getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

	// 复制到剪贴板
	private boolean copyToClipboard(Context context, String msg) {
		ClipboardManager clip = (ClipboardManager) context
				.getSystemService(Context.CLIPBOARD_SERVICE);
		// clip.getText(); // 粘贴
		if (clip.hasText()) {
			// 清空剪贴板
			clip.setText("");
		}
		clip.setText(msg); // 复制
		if (clip.getText().equals(msg))
			return true;
		return false;
	}

	private void showMessage(int titleResId, int imageResId) {
		PopupToast pt = new PopupToast(TapeActivity.this);
		pt.showMessage(this, titleResId, imageResId,(Point)((EverInputApplication)getApplication()).getAppConstant().get("winSize"));
	}
    
    private void saveEditorContents() {
        Context ctx = TapeActivity.this;
        SharedPreferences sp = ctx.getSharedPreferences("Content", MODE_PRIVATE);

        // save the text
        Editor content = sp.edit();
        content.putInt("Version", 2);
        String str = editor.getText().toString();
        content.putString("Content", str);
        
        SpannableStringBuilder builder = (SpannableStringBuilder)editor.getText();
        StringBuilder imgStr = new StringBuilder();
        String uriSeperator = "" + Constant.IMG_CHAR + Constant.IMG_CHAR;
        if (builder instanceof SpannableStringBuilder) {
        	System.out.println("Builder is right!!");
        	MyImageSpan[] spans = null;
//        	SpannableString[] spans = builder.getSpans(0, builder.length(), Class.forName("android.text.SpannableString");
        	try {
        		Class cl = Class.forName("cn.aiworks.note.utils.MyImageSpan");
        		int length = builder.length();
        		spans = builder.getSpans(0, length, cl);
        	}
        	catch (ClassNotFoundException e) {
        		System.out.println("Nonthing");
        	}
        	finally {
        		System.out.println("Nonthing");
        	}
//        	SpannableString[] spans = builder.getSpans(0, builder.length(), SpannableString.class);
        	
        	if (null != spans) {
	        	System.out.println("spans count: " + spans.length);
	        	for (MyImageSpan span : spans) {
	        		if (span instanceof ImageSpan) {
	        			// configure a method to get the uri!!
	                    String uri = span.uri.toString() + Constant.IMG_CHAR;
	                    uri = uri + span.sequence;
	                    imgStr.append(uri);
	                    imgStr.append(uriSeperator);
	        			System.out.println("Get a Span!!" + span.uri);
	        		}
	        		else {
	        			System.out.println("Not a span");
	        		}
	        	}
        	}
        	System.out.println("End of gettting span");
        }
        System.out.println("End of processing a BUILDER");
        
        if (imgStr.length() > 0) {
            content.putString("ImageSet", imgStr.toString());
        }
        content.putInt("Sequence", imageSequence);
        content.commit();
    }

    /**
     * 内容是否为空
     * @return
     */
    public boolean isContentVoid(){
    	String str = sp2.getString("Content", "");
    	String imgStr = sp2.getString("ImageSet", "");
    	return "".equals(str) && "".equals(imgStr)?true:false;
    }
    
    /**
     * 
     */
    private void loadEditorContents() {
        Context ctx = TapeActivity.this;
        String str = sp2.getString("Content", "");
        if (str.length() <= 0) {
            return;
        } 

        int version = sp2.getInt("Version", 1);
        if (1 == version) {
        	loadEditorContentsV1();
        	return;
        }
        // load the data
        imageSequence = sp2.getInt("Sequence", 1);
        String imgStr = sp2.getString("ImageSet", "");

        String seperator = "" + Constant.IMG_CHAR;
        String[] subStrs = str.split(seperator);
        if (subStrs.length < 2) {
        	editor.append(str);
            return;
        }

        HashMap<String, String> map = new HashMap<String, String>();
        String uriSeperator = "" + Constant.IMG_CHAR + Constant.IMG_CHAR;
        String[] uris = null;
        if (imgStr.length() > 0) {
            uris = imgStr.split(uriSeperator);
        }
        if (null != uris) {
            for (int i = 0; i < uris.length; ++i) {
                String imageStr = uris[i];
                String[] array = imageStr.split(seperator);
                if (array.length != 2) {
                    continue;
                }
                int seq = Integer.parseInt(array[1]);
                map.put("" + seq, array[0]);
            }
        }

        int start = 0;
        while (start < str.length()) {
            int index = str.indexOf(seperator, start);
            if (index < 0) {
            	String nStr = str.substring(start, str.length());
            	editor.getText().append(nStr);
            	break;
            }

            int end = str.indexOf(seperator, index + seperator.length());
            if (end < 0) {
            	// error to deal with
            	start = index + seperator.length();
            	continue;
            }
            if (index < end) {
                String nstr = str.substring(start, index);
                editor.getText().append(nstr);
                String seqStr = str.substring(index + seperator.length(), end);
                int seq = Integer.parseInt(seqStr);
                String uriStr = map.get(seqStr);
                if (null != uriStr && uriStr.length() > 4) {
                    Uri uri = Uri.parse(uriStr);
                    appendUriToEditor(uri, seq);
                }
            }
            start = end + seperator.length();
        }
    }
    
    @SuppressLint("NewApi")
    private void loadEditorContentsV1() {
        Context ctx = TapeActivity.this;
        SharedPreferences sp = ctx.getSharedPreferences("Content", MODE_PRIVATE);
        // load the data
        String str = sp.getString("Content", "");
        imageSequence = sp.getInt("Sequence", 1);
        Set<String> imgSet = sp.getStringSet("ImageSet", null);

        if (str.length() <= 0) {
            return;
        }

        String seperator = "" + Constant.IMG_CHAR;
        String[] subStrs = str.split(seperator);
        if (subStrs.length < 2) {
        	editor.append(str);
            return;
        }

        HashMap<String, String> map = new HashMap<String, String>();
        if (null != imgSet && imgSet.size() > 0) {
            Iterator<String> it = imgSet.iterator();
            while (it.hasNext()) {
                String imageStr = it.next();
                String[] array = imageStr.split(seperator);
                if (array.length != 2) {
                    continue;
                }
                int seq = Integer.parseInt(array[1]);
                map.put("" + seq, array[0]);
            }
        }

        int start = 0;
        while (start < str.length()) {
            int index = str.indexOf(seperator, start);
            if (index < 0) {
            	String nStr = str.substring(start, str.length());
            	editor.getText().append(nStr);
            	break;
            }

            int end = str.indexOf(seperator, index + seperator.length());
            if (end < 0) {
            	// error to deal with
            	start = index + seperator.length();
            	continue;
            }
            if (index < end) {
                String nstr = str.substring(start, index);
                editor.getText().append(nstr);
                String seqStr = str.substring(index + seperator.length(), end);
                int seq = Integer.parseInt(seqStr);
                String uriStr = map.get(seqStr);
                if (null != uriStr && uriStr.length() > 4) {
                    Uri uri = Uri.parse(uriStr);
                    appendUriToEditor(uri, seq);
                }
            }
            start = end + seperator.length();
        }

    }
    
    // TODO: insert image here
    private void appendUriToEditor(Uri uri, int seq) {
        Bitmap bitmap = Utils.getThumbFromUri(uri, this);
        if (null == bitmap) {
            return;
        }
        SpannableString spannableString = getSpannableString(bitmap, seq, uri);
        editor.getText().append(spannableString);
    }

    private SpannableString getSpannableString(Bitmap bitmap, int seq, Uri uri) {
        MyImageSpan imageSpan = new MyImageSpan(TapeActivity.this, bitmap);
        String seqStr = Constant.IMG_CHAR + "";
        seqStr = seqStr + seq;
        seqStr = seqStr + Constant.IMG_CHAR;
        SpannableString spannableString = new SpannableString(seqStr);
        imageSpan.uri = uri;
        imageSpan.sequence = seq;
        spannableString.setSpan(imageSpan, 0,spannableString.length(),SpannableString.SPAN_MARK_MARK);
        return spannableString;
    }

    /**
     * 获取笔记内容中的图片的uri。
     * @return
     */
    private Uri getContentUri() {
        String fileName = null;
        try {
        	fileName = getContentImage();
        	System.out.println("getContentImage():---->"+fileName);
        }
        catch (Exception e) {
            Log.e("Exception", "getContentUri", e);
        }
        if (null != fileName) {
            File file = new File(fileName);
            if (file.exists()) {
                Uri uri = getImageContentUri(getApplicationContext(), file);
                return uri;
            }
        }
        return null;
    }

    /**
     * 将笔记中的内容画为一张图，并返回图片路径。
     * @return
     * @throws Exception
     */
    private String getContentImage()throws Exception{
    	Bitmap savebitmap = null;
    	int width = editor.getLayout().getWidth() + 30;
    	int height = editor.getLayout().getHeight() + 20;
    	Bitmap.Config conf = Bitmap.Config.ARGB_8888;
    	savebitmap = Bitmap.createBitmap(width, height, conf);
    	Canvas canvas = new Canvas(savebitmap);
    	canvas.drawColor(0xFFFFFFFF);
    	canvas.translate(15, 0);
    	int offset = editor.getScrollY();
        editor.setCursorVisible(false);
    	for (int i = 0; i < height;) {
    		editor.scrollTo(0, i);
    		editor.draw(canvas);
    		i += editor.getHeight();
    	}
        editor.setCursorVisible(true);
    	editor.scrollTo(0, offset);
    	
    	if (savebitmap!=null) {
    		String sd = Environment.getExternalStorageDirectory().getAbsolutePath()+"/ScrawlImage/";
    		File file = new File(sd);
    		if (!file.exists()) {
    			file.mkdirs();
    		}
    		file.createNewFile();
    		long time = System.currentTimeMillis();
    		String fileName = file+"/"+time+".png";
    		FileOutputStream is = new FileOutputStream(fileName);
    		savebitmap.compress(CompressFormat.PNG, 100, is);
    		is.flush();
    		is.close();
    		savebitmap=null;
    		editor.setFocusable(true);
    		editor.setFocusableInTouchMode(true);
    		return fileName;
    	}
    	return null;
    }

	public Uri getImageContentUri(Context context, java.io.File imageFile) {  
		String filePath = imageFile.getAbsolutePath();  
		Cursor cursor = context.getContentResolver().query(  
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI,  
				new String[] { MediaStore.Images.Media._ID },  
				MediaStore.Images.Media.DATA + "=? ",  
						new String[] { filePath }, null);  
		if (cursor != null && cursor.moveToFirst()) {  
			int id = cursor.getInt(cursor  
					.getColumnIndex(MediaStore.MediaColumns._ID));  
			Uri baseUri = Uri.parse("content://media/external/images/media");  
			return Uri.withAppendedPath(baseUri, "" + id);  
		} else {  
			if (imageFile.exists()) {  
				ContentValues values = new ContentValues();  
				values.put(MediaStore.Images.Media.DATA, filePath);  
				return context.getContentResolver().insert(  
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);  
			} else {  
				return null;  
			}  
		}  
	}

    
    private void showShareView() {
        Intent share = new Intent(TapeActivity.this, SharePopupWindow.class);
        startActivityForResult(share, 0);
    }

    private void copyToClipBoardEx() {
    	//放到子线程中去--不！
		String text = Utils.getStringFromEditable((SpannableStringBuilder)editor.getText());
		boolean isSuccess = false;
		if (!"".equals(text)) {
			isSuccess = copyToClipboard(TapeActivity.this, text);
		} else {
			handler.sendEmptyMessage(Constant.CONTENT_IS_EMPTY);
		}
		if (isSuccess) {
			handler.sendEmptyMessage(Constant.COPY_TO_CLIPBOARD);
		}
	}
        

    private ProgressDialog progressDialog;
    private void sendEmailOrSMS(boolean isEmail) {
        Uri[] uris = Utils.getUrisFromEditable((SpannableStringBuilder)editor.getText());
        if (null != uris && uris.length >= 0) {
        	editor.setCursorVisible(false);
            progressDialog = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage(getResources().getString(R.string.share_loading));
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            	@Override
                public void onCancel(DialogInterface dialog) {
                    // do nothing!!
                }
            });
            progressDialog.show();
            // deal with image whole!!!
            if (isEmail) {
                sendEmailWithImage();
            }
            else {
                sendSMSWithImage();
            }
        }
        else {
            String text = Utils.getStringFromEditable((SpannableStringBuilder)editor.getText());
            if (isEmail) {
                sendEmailIntent(text, true);
            }
            else {
                sendSMSIntent(text, true);
            }
        }
    }

    private void sendEmailWithImage () {
        try {
            Uri uri = getContentUri();
            if (null != uri) {
                sendEmailIntent(uri.toString(), false);
            }
            else {
                String text = Utils.getStringFromEditable((SpannableStringBuilder)editor.getText());
                sendEmailIntent(text, true);
            }
        }
        catch (Exception e) {
            Log.e("Exception", "sendEmailWithImage", e);
        }
        handler.sendEmptyMessage(Constant.SHARE_DISMISS_DIALOG);
    }

    private void sendSMSWithImage () {
        try {
            Uri uri = getContentUri();
            if (null != uri) {
                sendSMSIntent(uri.toString(), false);
            }
            else {
                String text = Utils.getStringFromEditable((SpannableStringBuilder)editor.getText());
                sendSMSIntent(text, true);
            }
        }
        catch (Exception e) {
            Log.e("Exception", "sendEmailWithImage", e);
            handler.sendEmptyMessage(Constant.SEND_MSG_FAILED);
        }
        handler.sendEmptyMessage(Constant.SHARE_DISMISS_DIALOG);
    }

	// 发邮件
    private void sendEmailIntent(String textOrUri, boolean isCleanText) {
		Intent email = new Intent(android.content.Intent.ACTION_SEND);
		email.setType("plain/text");
		String emailSubject = Utils.getEmailSubject();
		email.putExtra(Intent.EXTRA_SUBJECT, emailSubject);

		if (isCleanText) {
			// 设置要默认发送的内容
			email.putExtra(android.content.Intent.EXTRA_TEXT, textOrUri);
		}
        else {
            email.putExtra(Intent.EXTRA_STREAM, Uri.parse(textOrUri));
        }
        startActivityForResult(Intent.createChooser(email, "请选择邮件发送软件"), 1001);

	}

	// 发短信
	private void sendSMSIntent(String textOrUri, boolean isCleanText) {
		try {
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			
			if (isCleanText) {
			    intent.putExtra("sms_body", textOrUri); // 彩信中文字内容
			}
			else {
			    intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(textOrUri));
			}
			intent.setType("image/*");// 彩信附件类型
			intent.setPackage("com.android.mms");
			startActivity(intent);
		} catch (Exception e) {
			handler.sendEmptyMessage(Constant.SEND_MSG_FAILED);
		}
    }

	private IWXAPI api;

	private EverInputApplication application;

	private HomeWatcher mHomeWatcher;

	private SharedPreferences sp2;
	// 分享到微信
	private void sendToWeixin(boolean isForFriend) {
        String text = Utils.getStringFromEditable((SpannableStringBuilder)editor.getText());
		// 初始化一个WXTextObject对象
		WXTextObject textObj = new WXTextObject();
		textObj.text = text;

		// 用WXTextObject对象初始化一个WXMediaMessage对象
		WXMediaMessage msg = new WXMediaMessage();
		msg.mediaObject = textObj;
		// 发送文本类型的消息时，title字段不起作用
		// msg.title = "Will be ignored";
		msg.description = text;

		// 构造一个Req
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("text"); // transaction字段用于唯一标识一个请求
		req.message = msg;
        if (isForFriend) {
            req.scene = SendMessageToWX.Req.WXSceneTimeline;
        }
        else {
            req.scene = SendMessageToWX.Req.WXSceneSession;
        }

		// 调用api接口发送数据到微信
		api.sendReq(req);
	}

	// 分享到印象笔记
	private void  sendToEverNote() {
		String text = Utils.getStringFromEditable((SpannableStringBuilder)editor.getText());
        Uri[] uris = Utils.getUrisFromEditable((SpannableStringBuilder)editor.getText());
//        if(text.trim().length() == 0 ){//全是空格
//        	handler.sendEmptyMessage(Constant.WEIBO_SPACE);//
//        	return;
//        }
		// 授权内容写在Share2EvernoteActivity中。
		Intent intent = new Intent(TapeActivity.this,Share2EvernoteActivity.class);
		//传递纯文本和图片uris[]
		intent.putExtra("note_body", text);
		ArrayList<Uri> uriList = new ArrayList<Uri>();
		if(uris != null){
			for(Uri uri : uris){
				uriList.add(uri);
			}
		}
		intent.putExtra("uris", (Serializable)uriList);
		startActivityForResult(intent, Constant.SHARE_TO_ELEPHANT_REQ);
	}

	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis())
				: type + System.currentTimeMillis();
	}

    /**
     * 接收微客户端博请求的数据。
     * 当微博客户端唤起当前应用并进行分享时，该方法被调用。
     * 
     * @param baseRequest 微博请求数据对象
     * @see {@link IWeiboShareAPI#handleWeiboRequest}
     */
    @Override
    public void onResponse(BaseResponse baseResp) {
        switch (baseResp.errCode) {
        case WBConstants.ErrorCode.ERR_OK:
//            Toast.makeText(this, R.string.weibosdk_demo_toast_share_success, Toast.LENGTH_LONG).show();
            break;
        case WBConstants.ErrorCode.ERR_CANCEL:
//            Toast.makeText(this, R.string.weibosdk_demo_toast_share_canceled, Toast.LENGTH_LONG).show();
            break;
        case WBConstants.ErrorCode.ERR_FAIL:
//            Toast.makeText(this, 
//                    getString(R.string.weibosdk_demo_toast_share_failed) + "Error Message: " + baseResp.errMsg, 
//                    Toast.LENGTH_LONG).show();
            break;
        }
    }

	/**
	 * 分享到有道
	 */
	private void sendToYoudao() {
//		yNoteApi.openYNoteApp();---ok
		final String stringFromEditable = Utils.getStringFromEditable((SpannableStringBuilder)editor.getText());
//		if(stringFromEditable.trim().length() == 0 ){//全是空格
//			handler.sendEmptyMessage(Constant.WEIBO_SPACE);//
//			return;
//		}
		if(!yNoteApi.isYNoteAppInstalled()){
			handler.sendEmptyMessage(Constant.YOUDAO_UNINSTALLED);
			return;
		}else{
			yNoteApi.registerApp();//注册到有道，可以在有道中生成笔记
		}
		mContent = new YNoteContent();
		String emailSubject = Utils.getEmailSubject();
		//set note title
        mContent.setTitle(emailSubject);
        //add plain text
        String plainText = Utils.getStringFromEditable((SpannableStringBuilder)editor.getText());
        final YNotePlainTextContent textContent = new YNotePlainTextContent(plainText);
        mContent.addObject(textContent);
        //add attachments
        Uri[] uris = Utils.getUrisFromEditable((SpannableStringBuilder)editor.getText());
        if(uris != null){
	        for (Object uri : uris) {
	        	YNoteImageContent imageContent = new YNoteImageContent((Uri)uri);
	        	mContent.addObject(imageContent);
	        }
        }
        mContent.setYNoteEditable(false);
        SendNoteRequest request = new SendNoteRequest();
        request.setYNoteContent(mContent);
        yNoteApi.sendRequest(request);
	}
	/**
	 * 
	 * @param imageCursor
	 * @return
	 */
	private String getFilePathFromCursor(Cursor imageCursor) {
		int actual_image_column_index = imageCursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		imageCursor.moveToFirst();
		String img_path = imageCursor.getString(actual_image_column_index);
		return img_path;
	}
	/**
	 * 分享到sinaweibo
	 */
	private void shareToSina() {
		refreshToken();
		//edittext中的文字，包括空格换行
		final String stringFromEditable = Utils.getStringFromEditable((SpannableStringBuilder)editor.getText());
		System.out.println("stringFromEditable:----->"+stringFromEditable.length());
		System.out.println("stringFromEditable.trim().length():----->"+stringFromEditable.trim().length());
		   final Uri[] uris = Utils.getUrisFromEditable((SpannableStringBuilder)editor.getText());
		if(stringFromEditable.trim().length() == 0 && uris == null){//全是空格
			handler.sendEmptyMessage(Constant.WEIBO_SPACE);//
			return;
		}
		if (mAccessToken != null && mAccessToken.isSessionValid()) {//已经注册则直接分享
			try {
				final StatusesAPI sa = new StatusesAPI(mAccessToken);
//					mWeiboShareAPI.registerApp();
	            progressDialog = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
	            progressDialog.setMessage(getResources().getString(R.string.share_to_sina_loading));
	            progressDialog.show();
	            /**文本总长度 包含空格*/
	            final String wenan = "(详见长微博)...(分享自#语音输入板# http://ime.aiworks.cn )";
	            final int len = stringFromEditable.length();
				String imagePath2 = null;
				if (len > 140 || uris != null) {
					//笔记中的全文截图的路径
					try {
						imagePath2 = getContentImage();
					}
					catch(Exception e) {
					}
				}
				final String imagePath = imagePath2;
	            new Thread(new Runnable() {
	                @Override
	                public void run() {
	                	try {
	                		handler.sendEmptyMessage(Constant.DIALOG_SHOW);
							if (null != imagePath) {
								if(stringFromEditable.trim().length() ==0){//只有图
									sa.upload("分享图片", imagePath, "", "", new MyRequestListener());
									return;
								}
								if( len >0 && len<=140){//正常发送
									sa.upload(stringFromEditable, imagePath, "", "", new MyRequestListener());
								}else {//len大于140字
									String wenben = stringFromEditable.trim().substring(0, 90)+wenan;
									sa.upload(wenben, imagePath, "", "", new MyRequestListener());
								}
							}
							else {//无图
								if (stringFromEditable.trim().length() == 0) {
									throw new Exception();
								}
								String text = stringFromEditable;
								if (stringFromEditable.length() > 140) {
									text = stringFromEditable.trim().substring(0, 140);
								}
								sa.update(text,"","",new MyRequestListener());
							}
	                	} catch (Exception e) {
	                		e.printStackTrace();
	                		Message msg = Message.obtain();
	                		msg.what = Constant.SHARE_TO_SINA_EXCEPTION;
	                		msg.obj = getResources().getString(R.string.weibo_share_fail);
	                		System.out.println("weiboeception:=----->"+msg.obj);
	                		handler.sendMessage(msg);
	                	}
	                }
	            }).start();
	        } catch (WeiboShareException e) {
	            e.printStackTrace();
	//            Toast.makeText(WBShareActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
	        } catch (Exception e) {
				e.printStackTrace();
			}
		}else{//唤起绑定
			mSsoHandler = new SsoHandler(TapeActivity.this, mWeiboAuth);
			mSsoHandler.authorize(new AuthListener());
		}
	}
	
	class MyRequestListener implements RequestListener{
		@Override
		public void onComplete(String arg0) {
			Message msg = Message.obtain();
			msg.what = Constant.SHARE_TO_SINA_SUCCESS;
			msg.obj = getResources().getString(R.string.weibo_share_success);
			Log.i(tag, "onComplete"+(String)arg0);
			handler.sendEmptyMessage(Constant.SHARE_DISMISS_DIALOG);
			handler.sendMessage(msg);
		}
		@Override
		public void onComplete4binary(ByteArrayOutputStream arg0) {
			// TODO Auto-generated method stub
		}
		@Override
		public void onError(WeiboException arg0) {
			Message msg = Message.obtain();
			msg.what = Constant.SHARE_TO_SINA_FIAL;
//			msg.obj = arg0.getMessage();
			Log.i(tag, "onError"+(String)arg0.getMessage());
			Gson gson = new Gson();
			WeiboErrorInfo weiboErrorInfo = gson.fromJson(arg0.getMessage(), WeiboErrorInfo.class);
			if(weiboErrorInfo != null){
				switch (weiboErrorInfo.getError_code()) {
					case 20012:
						msg.obj = getResources().getString(R.string.weibo_share_fail_140);
						break;
					case 21331:
						//新浪微博token过期。重新注册，不谈框
						clearToken();
						shareToSina();
						return;
					case 20019:
					case 20306:
						msg.obj = getResources().getString(R.string.weibo_share_same);
						break;
					default:
						msg.obj = getResources().getString(R.string.weibo_share_fail);
						break;
				}
			}else{
				String message = arg0.getMessage();
				Log.i(tag,"onError:-----> "+ message);
			}
			handler.sendEmptyMessage(Constant.SHARE_DISMISS_DIALOG);
			handler.sendMessage(msg);
		}
		@Override
		public void onIOException(IOException arg0) {
			Message msg = Message.obtain();
			msg.what = Constant.SHARE_TO_SINA_EXCEPTION;
//			msg.obj  = arg0.getMessage();
			msg.obj = "微博分享异常,\n请稍后再试！";
			Log.i(tag, "onIOException"+(String)msg.obj);
			handler.sendEmptyMessage(Constant.SHARE_DISMISS_DIALOG);
			handler.sendMessage(msg);
		}
	}
	/**
	 * 清空过期的token。
	 */
	public void clearToken(){
		AccessTokenKeeper.clear(this);
	}
	
	/**
	* 微博认证授权回调类。
	* 1.SSO 授权时，需要在 {@link #onActivityResult} 中调用 {@link SsoHandler#authorizeCallBack}
	* 后，该回调才会被执行。
	* 2. 非 SSO 授权时，当授权结束后，该回调就会被执行。
	* 当授权成功后，请保存该 access_token、expires_in、uid 等信息到 SharedPreferences 中。
	*/
	class AuthListener implements WeiboAuthListener {
		@Override
		public void onCancel() {
			// TODO Auto-generated method stub
		}

		@Override
		public void onComplete(Bundle arg0) {
			// 从 Bundle 中解析 Token
			mAccessToken = Oauth2AccessToken.parseAccessToken(arg0);
			if(mAccessToken.isSessionValid()){
				//将token保存到sharedpreferences中
				AccessTokenKeeper.writeAccessToken(TapeActivity.this, mAccessToken);
            	Message msg = Message.obtain();
            	msg.obj = "授权成功！";
            	msg.what = Constant.LOG_IN_SUCCESS;
            	Log.i(tag, "AuthListener->onComplete:---->"+mAccessToken.getToken());
            	handler.sendMessage(msg);
			}else{
				String code = arg0.getString("code");
				Log.i(tag, "code:---->"+code);
			}
		}
		@Override
		public void onWeiboException(WeiboException arg0) {
			Log.i(tag, "onWeiboException:---->"+arg0.toString());
			Message msg = Message.obtain();
			msg.what = Constant.SHARE_TO_SINA_EXCEPTION;
			msg.obj = getResources().getString(R.string.oauth_fail);
			handler.sendMessage(msg);
		}
	}
	
	
	
    /**
     * 第三方应用发送请求消息到微博，唤起微博分享界面。
     * @see {@link #sendMultiMessage} 或者 {@link #sendSingleMessage}
     */
    private void sendMessage(boolean hasText, boolean hasImage, 
			boolean hasWebpage, boolean hasMusic, boolean hasVideo, boolean hasVoice) {
        
        if (mWeiboShareAPI.isWeiboAppSupportAPI()) {
            int supportApi = mWeiboShareAPI.getWeiboAppSupportAPI();
                sendMultiMessage(hasText, hasImage, hasWebpage, hasMusic, hasVideo, hasVoice);
        } else {
//            Toast.makeText(this, R.string.weibosdk_demo_not_support_api_hint, Toast.LENGTH_SHORT).show();
            PopupToast pt = new PopupToast(TapeActivity.this);
            
        }
    }

    /**
     * 第三方应用发送请求消息到微博，唤起微博分享界面。
     * 注意：当 {@link IWeiboShareAPI#getWeiboAppSupportAPI()} >= 10351 时，支持同时分享多条消息，
     * 同时可以分享文本、图片以及其它媒体资源（网页、音乐、视频、声音中的一种）。
     * 
     * @param hasText    分享的内容是否有文本
     * @param hasImage   分享的内容是否有图片
     * @param hasWebpage 分享的内容是否有网页
     * @param hasMusic   分享的内容是否有音乐
     * @param hasVideo   分享的内容是否有视频
     * @param hasVoice   分享的内容是否有声音
     */
    private void sendMultiMessage(boolean hasText, boolean hasImage, boolean hasWebpage,
            boolean hasMusic, boolean hasVideo, boolean hasVoice) {
        // 1. 初始化微博的分享消息
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        if (hasText) {
            weiboMessage.textObject = getTextObj();
        }
        if (hasImage) {
            weiboMessage.imageObject = getImageObj();
        }
        // 2. 初始化从第三方到微博的消息请求
        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        // 用transaction唯一标识一个请求
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = weiboMessage;
        
        // 3. 发送请求消息到微博，唤起微博分享界面
        mWeiboShareAPI.sendRequest(request);
    }
    /**
     * 创建文本消息对象。
     * 
     * @return 文本消息对象。
     */
    private TextObject getTextObj() {
        TextObject textObject = new TextObject();
        textObject.text = Utils.getStringFromEditable((SpannableStringBuilder)editor.getText());
        return textObject;
    }

    /**
     * 创建图片消息对象。
     * 
     * @return 图片消息对象。
     */
    private ImageObject getImageObj() {
        ImageObject imageObject = new ImageObject();
        String contentBitmapFilePath = "这里写一个默认的失败图片的路径";
		try {
			contentBitmapFilePath = getContentImage();
			Bitmap content_bitmap = BitmapFactory.decodeFile(contentBitmapFilePath);
			imageObject.setImageObject(content_bitmap);
			return imageObject;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
    }

	/**
	 * 将bitmap加入到edittext中，并在MyImageSpan中记录对应的uri。
	 * @param bitmap
	 * @param uri
	 */
	/*private void setBitmapToEdittext(Bitmap bitmap,Uri uri) {
		// 拿到bitmap，加入到edittext中。
		MyImageSpan imageSpan = new MyImageSpan(TapeActivity.this,bitmap);
		long timeStamp = System.currentTimeMillis();
		String seq = Constant.IMG_CHAR + "";
		seq = seq + imageSequence;
		seq = seq + Constant.IMG_CHAR;
		SpannableString spannableString = new SpannableString(seq);
		pics.put(timeStamp+"", uri);
        imageSpan.uri = uri;
        imageSpan.sequence = imageSequence; */
    
    /**
     * 重新获取token
     */
	public void refreshToken(){
        // 第一次启动本应用，AccessToken 不可用
        mAccessToken = AccessTokenKeeper.readAccessToken(this);
        long expiresTime = mAccessToken.getExpiresTime();
        Date date = new Date(expiresTime);
        String date2 = Utils.getFormatDate("yyyy-MM-dd HH:mm");
        System.out.println("--------share to sina------------"+date2);
	}
	
    
}

