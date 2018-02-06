package cn.aiworks.note;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;

import java.lang.ref.WeakReference;

import cn.aiworks.note.application.EverInputApplication;
import cn.aiworks.note.constant.Constant;
import cn.aiworks.note.constant.Constants;
import cn.aiworks.note.utils.Utils;
import cn.aiworks.note.view.PopupToast;

@SuppressWarnings("deprecation")
public class SharePopupWindowAcitivity extends Activity implements OnClickListener {
    RelativeLayout rl_menu_gridview;
    GridView gv_pop_layout;
    TextView bt_cancle;
    // 分享菜单内容
    String[] titles = {"邮件", "短信", "微信好友", "朋友圈", "复制", "印象笔记", "新浪微博", "有道云笔记"};// 菜单图片资源
    int[] iconIds = {R.drawable.s_mail_, R.drawable.s_msg_,
            R.drawable.s_voice_, R.drawable.s_friend_, R.drawable.s_copy_, R.drawable.s_elephent_, R.drawable.s_sina_, R.drawable.s_youdao_
    };// 菜单文字资源

    private void showMessage(int titleResId, int imageResId) {
        PopupToast pt = new PopupToast(SharePopupWindowAcitivity.this);
        pt.showMessage(this, titleResId, imageResId, (Point) ((EverInputApplication) getApplication()).getAppConstant().get("winSize"));
    }

    Handler handler = new SharePopWindowHandler(this);

    private static class SharePopWindowHandler extends Handler {
        private WeakReference<SharePopupWindowAcitivity> mAct = null;

        SharePopWindowHandler(SharePopupWindowAcitivity act) {
            this.mAct = new WeakReference<SharePopupWindowAcitivity>(act);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.COPY_TO_CLIPBOARD:
                    mAct.get().showMessage(R.string.copy_success, R.drawable.success);
                    break;
                case Constant.NETWORK_UNAVAILABLE:
                    mAct.get().showMessage(R.string.network_error, R.drawable.bugeili);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = View.inflate(this, R.layout.pop, null);
        setContentView(view);
        IWXAPI api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);

        gv_pop_layout = (GridView) findViewById(R.id.gv_pop_layout);
        bt_cancle = (TextView) findViewById(R.id.bt_cancle);
        rl_menu_gridview = (RelativeLayout) findViewById(R.id.rl_menu_gridview);
        // 添加选择窗口范围监听可以优先获取触点，即不再执行onTouchEvent()函数，点击其他地方时执行onTouchEvent()函数销毁Activity
        MyAdapter myAdapter = new MyAdapter(this, titles, iconIds);
        gv_pop_layout.setAdapter(myAdapter);
        gv_pop_layout.setSelector(new ColorDrawable(Color.TRANSPARENT));
        gv_pop_layout.setOnItemClickListener(new MyListener());
        // 注册
        api.registerApp(Constants.APP_ID);
    }

    // 实现onTouchEvent触屏函数但点击屏幕时销毁本Activity
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                cancelHeight = bt_cancle.getHeight();
                // 获取第一次点击的点
                oldy = (int) event.getRawY();
                Log.i("sp", "" + oldy);
                //获取上限
                top = bt_cancle.getTop();
                if (oldy < (top + cancelHeight) && oldy > top)
                    bt_cancle.setBackgroundColor(getResources().getColor(R.color.grey));
                break;
            case MotionEvent.ACTION_MOVE:
                int newy = (int) event.getRawY();
                Log.i("sp", "" + newy);
                if (/*newy < (top+cancelHeight) && */newy > top)
                    bt_cancle.setBackgroundColor(getResources().getColor(R.color.grey));
                else
                    bt_cancle.setBackgroundColor(Color.WHITE);
                break;
            case MotionEvent.ACTION_UP:
                newy = (int) event.getRawY();
                Log.i("sp", "" + newy);
                if (newy > top && oldy < (top + cancelHeight) && oldy > top) {
                    bt_cancle.setBackgroundColor(Color.WHITE);
                    this.finish();
                }
                if (newy == oldy) {
                    bt_cancle.setBackgroundColor(Color.WHITE);
                    this.finish();
                }
                System.out.println("oldy:---->" + oldy);
                bt_cancle.setBackgroundColor(Color.WHITE);
                break;
            default:
                break;
        }
        return true;
    }

    private class MyListener implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            switch (iconIds[position]) {
                case R.drawable.s_mail_:
                    setResult(Constant.SHARE_EMAIL);
                    SharePopupWindowAcitivity.this.finish();
                    System.out.println("mail is clicked");
                    break;
                case R.drawable.s_msg_:
                    setResult(Constant.SHARE_SMS);
                    SharePopupWindowAcitivity.this.finish();
                    System.out.println("message is clicked");
                    break;
                case R.drawable.s_voice_:
                    setResult(Constant.SHARE_WEIXIN);
                    SharePopupWindowAcitivity.this.finish();
                    System.out.println("voice  is clicked");
                    break;
                case R.drawable.s_elephent_:
                    if (Utils.isNetworkConnected(SharePopupWindowAcitivity.this)) {
                        setResult(Constant.SHARE_EVERNOTE);
                        System.out.println("elephent  is clicked");
                    } else {
                        handler.sendEmptyMessage(Constant.NETWORK_UNAVAILABLE);
                    }
                    SharePopupWindowAcitivity.this.finish();
                    break;
                case R.drawable.s_copy_:
                    setResult(Constant.SHARE_COPY);
                    SharePopupWindowAcitivity.this.finish();
                    System.out.println("copy  is clicked");
                    break;
                case R.drawable.s_friend_:
                    // 朋友圈
                    setResult(Constant.SHARE_FRIENDS);
                    SharePopupWindowAcitivity.this.finish();
                    break;
                case R.drawable.s_sina_:
                    if (Utils.isNetworkConnected(SharePopupWindowAcitivity.this)) {
                        setResult(Constant.SHARE_SINA_WEIBO);
                        System.out.println("elephent  is clicked");
                    } else {
                        handler.sendEmptyMessage(Constant.NETWORK_UNAVAILABLE);
                    }
                    SharePopupWindowAcitivity.this.finish();
                    break;
                case R.drawable.s_youdao_:
                    setResult(Constant.SHARE_YOUDAO);
                    SharePopupWindowAcitivity.this.finish();
                    break;
                default:
                    break;
            }
        }
    }

    private class MyAdapter extends BaseAdapter {
        Context context;
        String[] titles;
        int[] ids;

        MyAdapter(Context context, String[] titles, int[] ids) {
            this.context = context;
            this.titles = titles;
            this.ids = ids;
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public Object getItem(int position) {
            return iconIds[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.item, null);
            }
            ImageView iv_icon = (ImageView) convertView
                    .findViewById(R.id.iv_icon);
            TextView tv = (TextView) convertView.findViewById(R.id.tv_tag);
            iv_icon.setImageResource(ids[position]);
            tv.setText(titles[position]);
            return convertView;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_cancle:
                this.finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constant.SHARE_TO_ELEPHANT_REQ:
                break;
            default:
                break;
        }
    }

    private int oldy;
    private int top;
    private int cancelHeight;

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }


}
