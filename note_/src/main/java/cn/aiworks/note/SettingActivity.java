package cn.aiworks.note;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.evernote.client.android.InvalidAuthenticationException;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.LogoutAPI;
import com.umeng.analytics.MobclickAgent;
import com.youdao.note.sdk.openapi.IYNoteAPI;
import com.youdao.note.sdk.openapi.YNoteAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

import cn.aiworks.note.application.EverInputApplication;
import cn.aiworks.note.constant.Constant;
import cn.aiworks.note.constant.Constants_sina;
import cn.aiworks.note.constant.SDKConst;
import cn.aiworks.note.sinaWeibo.AccessTokenKeeper;
import cn.aiworks.note.utils.Utils;
import cn.aiworks.note.view.PopupToast;


public class SettingActivity extends ParentActivity implements OnClickListener {
    String tag = "sinawb";
    RelativeLayout rl_about_us;
    RelativeLayout rl_bind_yinxiang;

    RelativeLayout rl_bind_sina;

    RelativeLayout rl_feedback;
    RelativeLayout rl_app_recommandation;
    ImageView iv_setting_back;
    TextView tv_yinxiang_status;

    public Handler handler = new SettingHandler(this);

    private static class SettingHandler extends Handler{
        private WeakReference<SettingActivity> mAct = null;
        SettingHandler(SettingActivity act){
            this.mAct = new WeakReference<SettingActivity>(act);
        }

        @Override
        public void handleMessage(Message msg) {
            mAct.get().refreshToken();
            PopupToast pt_share_to_sina = new PopupToast(mAct.get());
            switch (msg.what) {
                case Constant.LOG_IN_SUCCESS:
                    mAct.get().setBindStatus(mAct.get().mEvernoteSession.isLoggedIn(), R.id.tv_yinxiang_bind);
                    mAct.get().setBindStatus(mAccessToken.isSessionValid(), R.id.tv_bind_sina);
                    mAct.get().setBindStatus(mAct.get().yNoteApi.isRegistered(), R.id.tv_youdao_bind);
                    break;
                case Constant.LOG_IN_FAIL:
                    PopupToast li_pt_failed = new PopupToast(mAct.get());
                    String logout_fail_info = (String) msg.obj;
                    li_pt_failed.showMessage(mAct.get(), logout_fail_info, R.drawable.fail, (Point) ((EverInputApplication) (mAct.get().getApplication())).getAppConstant().get("winSize"));
                    mAct.get().setBindStatus(mAct.get().mEvernoteSession.isLoggedIn(), R.id.tv_yinxiang_bind);
                    mAct.get().setBindStatus(mAccessToken.isSessionValid(), R.id.tv_bind_sina);
                    mAct.get().setBindStatus(mAct.get().yNoteApi.isRegistered(), R.id.tv_youdao_bind);
                    break;
                case Constant.LOG_OUT_SUCCESS:
                    mAct.get().setBindStatus(mAct.get().mEvernoteSession.isLoggedIn(), R.id.tv_yinxiang_bind);
                    mAct.get().setBindStatus(mAccessToken.isSessionValid(), R.id.tv_bind_sina);
                    mAct.get().setBindStatus(mAct.get().yNoteApi.isRegistered(), R.id.tv_youdao_bind);
                    break;
                case Constant.LOG_OUT_FAILED:
                    PopupToast lo_pt_failed = new PopupToast(mAct.get());
                    String fail_info = (String) msg.obj;
                    lo_pt_failed.showMessage(mAct.get(), fail_info, R.drawable.fail, null);
                    mAct.get().setBindStatus(mAct.get().mEvernoteSession.isLoggedIn(), R.id.tv_yinxiang_bind);
                    mAct.get().setBindStatus(mAccessToken.isSessionValid(), R.id.tv_bind_sina);
                    mAct.get().setBindStatus(mAct.get().yNoteApi.isRegistered(), R.id.tv_youdao_bind);
                    break;
                case Constant.SHARE_TO_SINA_SUCCESS:
                    pt_share_to_sina.showMessage(mAct.get(), (String) msg.obj, R.drawable.success, null);
                    break;
                case Constant.SHARE_TO_SINA_FIAL:
                    pt_share_to_sina.showMessage(mAct.get(), (String) msg.obj, R.drawable.fail, null);
                    break;
                case Constant.SHARE_TO_SINA_EXCEPTION:
                    pt_share_to_sina.showMessage(mAct.get(), (String) msg.obj, R.drawable.fail, null);
                case Constant.YOUDAO_UNINSTALLED:
                    pt_share_to_sina.showMessage(mAct.get(), (String) msg.obj, R.drawable.bugeili, null);
                    break;
                case Constant.NETWORK_UNAVAILABLE:
                    pt_share_to_sina.showMessage(mAct.get(), (String) msg.obj, R.drawable.bugeili, null);
                    break;
                case Constant.LOG_OUT_EXCEPTION:
                    pt_share_to_sina.showMessage(mAct.get(), (String) msg.obj, msg.arg1, null);
                    mAct.get().setBindStatus(mAct.get().mEvernoteSession.isLoggedIn(), R.id.tv_yinxiang_bind);
                    mAct.get().setBindStatus(mAccessToken.isSessionValid(), R.id.tv_bind_sina);
                    mAct.get().setBindStatus(mAct.get().yNoteApi.isRegistered(), R.id.tv_youdao_bind);
                    break;
                default:
                    break;
            }
        }
    }
    //新浪微博
    /**
     * 显示认证后的信息，如 AccessToken
     */
    private TextView mTokenText;
    /**
     * 微博 Web 授权类，提供登陆等功能
     */
    private WeiboAuth mWeiboAuth;
    /**
     * 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能
     */
    private static Oauth2AccessToken mAccessToken;
    /**
     * 注意：SsoHandler 仅当 SDK 支持 SSO 时有效
     */
    private SsoHandler mSsoHandler;
    /**
     * 登出操作对应的listener
     */
    private LogOutRequestListener mLogoutListener = new LogOutRequestListener();

    /**
     * 有道笔记的api类
     */
    private IYNoteAPI yNoteApi;

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        //2创建微博授权类对象
        mWeiboAuth = new WeiboAuth(this, Constants_sina.APP_KEY, Constants_sina.REDIRECT_URL, Constants_sina.SCOPE);
        // 3从 SharedPreferences 中读取上次已保存好 AccessToken 等信息，
        refreshToken();

        initViews();

    }


    private void initViews() {

        rl_about_us = (RelativeLayout) findViewById(R.id.rl_about_us);
        rl_bind_yinxiang = (RelativeLayout) findViewById(R.id.rl_bind_yinxiang);
        rl_feedback = (RelativeLayout) findViewById(R.id.rl_feedback);
        rl_app_recommandation = (RelativeLayout) findViewById(R.id.rl_app_recommandation);
        rl_bind_sina = (RelativeLayout) findViewById(R.id.rl_bind_sina);
        RelativeLayout rl_bind_youdao = (RelativeLayout) findViewById(R.id.rl_bind_youdao);

        iv_setting_back = (ImageView) findViewById(R.id.iv_setting_back);
        tv_yinxiang_status = (TextView) findViewById(R.id.tv_yinxiang_status);

        rl_about_us.setOnClickListener(this);
        rl_bind_yinxiang.setOnClickListener(this);
        rl_feedback.setOnClickListener(this);
        rl_app_recommandation.setOnClickListener(this);
        rl_bind_sina.setOnClickListener(this);
        rl_bind_youdao.setOnClickListener(this);
        iv_setting_back.setOnClickListener(this);


    }

    public void refreshToken() {
        // 第一次启动本应用，AccessToken 不可用
        mAccessToken = AccessTokenKeeper.readAccessToken(this);
        //获取有道api类。
        yNoteApi = YNoteAPIFactory.getYNoteAPI(SettingActivity.this, SDKConst.sAppId);
    }


    @Override
    protected void onResume() {
        super.onResume();
        //设置初始状态
        setBindStatus(mEvernoteSession.isLoggedIn(), R.id.tv_yinxiang_bind);
        setBindStatus(mAccessToken.isSessionValid(), R.id.tv_bind_sina);
        setBindStatus(yNoteApi.isRegistered(), R.id.tv_youdao_bind);
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    public void logout_sina() {
        new LogoutAPI(AccessTokenKeeper.readAccessToken(SettingActivity.this)).logout(mLogoutListener);
    }

    /**
     * 印象笔记解除账号绑定。
     */
    public void logout() {
        try {
            mEvernoteSession.logOut(this);
        } catch (InvalidAuthenticationException e) {
            PopupToast pt = new PopupToast(SettingActivity.this);
            pt.showMessage(SettingActivity.this, "啊，授权失败了…", R.drawable.fail, (Point) ((EverInputApplication) getApplication()).getAppConstant().get("winSize"));
        }
        Message msg = Message.obtain();
        msg.obj = "解绑成功！";
        msg.what = Constant.LOG_OUT_SUCCESS;
        handler.sendMessage(msg);
    }

    /**
     * 有道笔记取消注册
     */
    public void unregister_ynote() {
        yNoteApi.unregisterApp();
        Message msg = Message.obtain();
        msg.obj = "已注销";
        msg.what = Constant.LOG_OUT_SUCCESS;
        handler.sendMessage(msg);
    }

    /**
     * 注册笔记
     */
    public void register_ynote() {
        if (yNoteApi.isYNoteAppInstalled()) {//如果已经安装ynote
            yNoteApi.registerApp();
            Message msg = Message.obtain();
            msg.obj = "已绑定";
            msg.what = Constant.LOG_OUT_SUCCESS;
            handler.sendMessage(msg);
        } else {
            Message msg = Message.obtain();
            msg.obj = "请下载安装有道与笔记！";
            msg.what = Constant.LOG_OUT_SUCCESS;
            handler.sendMessage(msg);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_app_recommandation:
                Intent recommand = new Intent(SettingActivity.this, RecommandActivity.class);
                SettingActivity.this.startActivity(recommand);
                this.finish();
                overridePendingTransition(R.anim.slid_from_right_in, R.anim.nochange_out);
                break;
            case R.id.rl_about_us:
                Intent intent = new Intent(SettingActivity.this, AboutUsActivity.class);
                SettingActivity.this.startActivity(intent);
                this.finish();
                overridePendingTransition(R.anim.slid_from_right_in, R.anim.nochange_out);
                break;
            case R.id.iv_setting_back:
                this.finish();
                SettingActivity.this.overridePendingTransition(R.anim.nochange_in, R.anim.slide_from_left_out);
                break;
            case R.id.rl_feedback:
                Intent feedback = new Intent(SettingActivity.this, FeedbackActivity.class);
                startActivity(feedback);
                this.finish();
                overridePendingTransition(R.anim.slid_from_right_in, R.anim.nochange_out);
                break;
            case R.id.rl_bind_yinxiang:
                if (Utils.isNetworkConnected(SettingActivity.this)) {
                    if (mEvernoteSession.isLoggedIn()) {//已经绑定
                        View evernote_confirm = View.inflate(this, R.layout.evernote_confirm_dialog, null);
                        final Dialog evernote_dialog = new Dialog(SettingActivity.this, R.style.MyDialog);
                        evernote_dialog.setContentView(evernote_confirm);
                        evernote_confirm.findViewById(R.id.bt_evernote_confirm).setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                switch (v.getId()) {
                                    case R.id.bt_evernote_confirm:
                                        logout();
                                        evernote_dialog.dismiss();
                                        break;
                                    default:
                                        break;
                                }
                            }
                        });
                        evernote_confirm.findViewById(R.id.bt_evernote_cancle).setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                switch (v.getId()) {
                                    case R.id.bt_evernote_cancle:
                                        evernote_dialog.dismiss();
                                        break;
                                    default:
                                        break;
                                }
                            }
                        });
                        evernote_dialog.show();
                    } else {//未绑定
                        mEvernoteSession.authenticate(this);
                    }
                } else {
                    Message msg = Message.obtain();
                    msg.what = Constant.NETWORK_UNAVAILABLE;
                    msg.obj = getResources().getString(R.string.network_error);
                    handler.sendMessage(msg);
                }
                break;
            case R.id.rl_bind_sina:
                if (Utils.isNetworkConnected(SettingActivity.this)) {
                    if (mAccessToken.isSessionValid()) {//已绑定
                        View evernote_confirm = View.inflate(this, R.layout.evernote_confirm_dialog, null);
                        final Dialog evernote_dialog = new Dialog(SettingActivity.this, R.style.MyDialog);
                        evernote_dialog.setContentView(evernote_confirm);
                        evernote_confirm.findViewById(R.id.bt_evernote_confirm).setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                switch (v.getId()) {
                                    case R.id.bt_evernote_confirm:
                                        logout_sina();
                                        evernote_dialog.dismiss();
                                        break;
                                    default:
                                        break;
                                }
                            }
                        });
                        evernote_confirm.findViewById(R.id.bt_evernote_cancle).setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                switch (v.getId()) {
                                    case R.id.bt_evernote_cancle:
                                        evernote_dialog.dismiss();
                                        break;
                                    default:
                                        break;
                                }
                            }
                        });
                        evernote_dialog.show();
                    } else {//未绑定
                        mSsoHandler = new SsoHandler(SettingActivity.this, mWeiboAuth);
                        mSsoHandler.authorize(new AuthListener());
                    }
                } else {
                    Message msg = Message.obtain();
                    msg.what = Constant.NETWORK_UNAVAILABLE;
                    msg.obj = getResources().getString(R.string.network_error);
                    handler.sendMessage(msg);
                }
                break;
            case R.id.rl_bind_youdao:
                if (yNoteApi.isYNoteAppInstalled()) {
                    if (yNoteApi.isRegistered()) {
                        unregister_ynote();
                    } else {
                        register_ynote();
                    }
                } else {
                    Message msg = Message.obtain();
                    msg.obj = "尚未安装有道云笔记！";
                    msg.what = Constant.YOUDAO_UNINSTALLED;
                    handler.sendMessage(msg);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 设置绑定状态
     *
     * @param isbind 判断是否已经通过验证
     * @param id     要改变的空间id。
     */
    private void setBindStatus(boolean isbind, int id) {
        TextView bindStatus = (TextView) findViewById(id);
        if (isbind) {
            bindStatus.setText(getResources().getString(R.string.unbind));
        } else {
            bindStatus.setText(getResources().getString(R.string.bind));
        }
        bindStatus.invalidate();
    }

    /**
     * 微博认证授权回调类。
     * 1.SSO 授权时，需要在 {@link #onActivityResult} 中调用 {@link SsoHandler#authorizeCallBack}
     * 后，该回调才会被执行。
     * 2. 非 SSO 授权时，当授权结束后，该回调就会被执行。
     * 当授权成功后，请保存该 access_token、expires_in、uid 等信息到 SharedPreferences 中。
     */
    private class AuthListener implements WeiboAuthListener {
        @Override
        public void onCancel() {

        }

        @Override
        public void onComplete(Bundle arg0) {
            // 从 Bundle 中解析 Token
            mAccessToken = Oauth2AccessToken.parseAccessToken(arg0);
            if (mAccessToken.isSessionValid()) {
                //将token保存到sharedpreferences中
                AccessTokenKeeper.writeAccessToken(SettingActivity.this, mAccessToken);
                Message msg = Message.obtain();
                msg.obj = "授权成功！";
                msg.what = Constant.LOG_IN_SUCCESS;
                handler.sendMessage(msg);
            } else {
                String code = arg0.getString("code");
                Log.i(tag, "code:---->" + code);
            }
        }

        @Override
        public void onWeiboException(WeiboException arg0) {
            Log.i(tag, "onWeiboException:---->" + arg0.toString());
            Message msg = Message.obtain();
            msg.what = Constant.SHARE_TO_SINA_EXCEPTION;
            msg.obj = "微博授权异常！";
            handler.sendMessage(msg);
        }

    }

    /**
     * 登出按钮的监听器，接收登出处理结果。（API 请求结果的监听器）
     */
    private class LogOutRequestListener implements RequestListener {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                try {
                    JSONObject obj = new JSONObject(response);
                    String value = obj.getString("result");

                    if ("true".equalsIgnoreCase(value)) {
                        //从sharedpreferences中清除token
                        AccessTokenKeeper.clear(SettingActivity.this);
                        Message msg = Message.obtain();
                        msg.obj = getResources().getString(R.string.unbind);
                        msg.what = Constant.LOG_OUT_SUCCESS;
                        handler.sendMessage(msg);
                    } else {
                        Log.i("sa", "LogOutRequestListener.onComplete:------>" + value);
                        //从sharedpreferences中清除token
                        AccessTokenKeeper.clear(SettingActivity.this);
                        Message msg = Message.obtain();
                        msg.obj = "请卸载或重新安装微博客户端";
                        msg.what = Constant.LOG_OUT_EXCEPTION;
                        msg.arg1 = R.drawable.fail;
                        handler.sendMessage(msg);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.i("sa", "LogOutRequestListener.onComplete:------>" + e.getMessage());
                    //从sharedpreferences中清除token
                    AccessTokenKeeper.clear(SettingActivity.this);
                    Message msg = Message.obtain();
                    msg.obj = getResources().getString(R.string.unbind);
                    msg.what = Constant.LOG_OUT_SUCCESS;
                    handler.sendMessage(msg);
                }
            } else {
                Message msg = Message.obtain();
                msg.obj = "微博无响应…";
                msg.what = Constant.LOG_OUT_FAILED;
                handler.sendMessage(msg);
            }
        }

        @Override
        public void onComplete4binary(ByteArrayOutputStream responseOS) {
            // Do nothing
        }

        @Override
        public void onIOException(IOException e) {
            Message msg = Message.obtain();
            msg.obj = "注销异常！";
            msg.what = Constant.LOG_OUT_FAILED;
            handler.sendMessage(msg);
        }

        @Override
        public void onError(WeiboException e) {
            Message msg = Message.obtain();
            msg.obj = "微博出错了！";
            msg.what = Constant.LOG_OUT_FAILED;
            handler.sendMessage(msg);
        }
    }


    /**
     * 当 SSO 授权 Activity 退出时，该函数被调用。
     *
     * @see Activity onActivityResult
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // SSO 授权回调
        // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResult
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.finish();  //finish当前activity
            overridePendingTransition(R.anim.nochange_in, R.anim.slide_from_left_out);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
