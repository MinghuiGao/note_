package cn.aiworks.note;

import java.io.File;
import java.util.ArrayList;

import cn.aiworks.note.constant.Constant;
import cn.aiworks.note.domain.RecommandAppBean;
import cn.aiworks.note.utils.Utils;
import cn.aiworks.note.view.MyListView;
import cn.aiworks.note.view.PopupToast;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class RecommandActivity extends Activity implements OnClickListener,
		OnItemClickListener {

	MyListView lv_recommand;
	ImageView iv_setting_back_recommand;
	ProgressDialog pd;
	ArrayList<RecommandAppBean> apps = new ArrayList<RecommandAppBean>();

	Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constant.DOWNLOAD_FAILED:
				PopupToast pt = new PopupToast(RecommandActivity.this);
				pt.showMessage(RecommandActivity.this, (String)msg.obj, msg.arg1, null);
				break;
			default:
				break;
			}
			
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recommand);
		// ====just for test fake data======
		RecommandAppBean rab = new RecommandAppBean();
		rab.imageId = R.drawable.yingyongtuijian_icon_1;
		rab.appName = "瘦瘦-健康减肥顾问";
		rab.appDesc = "中国第一健康瘦身减肥应用";
		rab.url = "http://42.121.120.19/download/Slimcoach_232_Umeng.apk";
		RecommandAppBean rab2 = new RecommandAppBean();
		rab2.imageId = R.drawable.yingyongtuijian_icon_2;
		rab2.appName = "大姨吗";
		rab2.appDesc = "亚洲最受欢迎的女性健康助手";
		rab2.url = "http://cdn.yoloho.com/upload/filedownload/2014/03/18/dayima_v5.20_official_signed_build99_201403181039.apk?ver=1";
		RecommandAppBean rab3 = new RecommandAppBean();
		rab3.imageId = R.drawable.yingyongtuijian_icon_3;
		rab3.appName = "外卖库";
		rab3.appDesc = "喂人民服务";
		rab3.url = "http://static.waimaiku.com/download/apps/Waimaiku.apk";
		RecommandAppBean rab4 = new RecommandAppBean();
		rab4.imageId = R.drawable.yingyongtuijian_icon_4;
		rab4.appName = "哔哩哔哩动画";
		rab4.appDesc = "国内知名的弹幕视频分享网站";
		rab4.url = "http://app.bilibili.cn/BiliPlayer.apk";
		RecommandAppBean rab5 = new RecommandAppBean();
		rab5.imageId = R.drawable.anyview;
		rab5.appName = "Anyview";
		rab5.appDesc = "享受阅读美好时光";
		rab5.url = "http://www.anyview.net/released/android/2.26/Anyview_2.26.apk";
		RecommandAppBean rab6 = new RecommandAppBean();
		rab6.imageId = R.drawable.yingyongtuijian_icon_6;
		rab6.appName = "语音输入板222";
		rab6.appDesc = "史上最工具";
		RecommandAppBean rab7 = new RecommandAppBean();
		rab7.imageId = R.drawable.yingyongtuijian_icon_7;
		rab7.appName = "天气衣报";
		rab7.appDesc = "蜜糖你的生活";
		rab7.url = "http://app.sugarlady.com/m/app_bin/DailyAttire_sugarlady_1_7_6.apk";
		RecommandAppBean rab8 = new RecommandAppBean();
		rab8.imageId = R.drawable.e_jiajie;
		rab8.appName = "E家洁";
		rab8.appDesc = "全京城都在用的预约保洁应用";
		rab8.url = "http://cdn.market.hiapk.com/data/upload/2014/03_20/0/com.e.jiajie.user_005756.apk";
		RecommandAppBean rab9 = new RecommandAppBean();
		rab9.imageId = R.drawable.jiecao;
		rab9.appName = "节操精选";
		rab9.appDesc = "失足青年的灵魂导师";
		rab9.url = "http://news.jiecao.fm/apk/jiecao-yuyinshuru-v2.4.2-release.apk";
		
//		apps.add(rab);
		apps.add(rab2);
		apps.add(rab3);
		apps.add(rab4);
		apps.add(rab5);
//		apps.add(rab6);
		apps.add(rab7);
		apps.add(rab8);
		apps.add(rab9);
		// =======end of fake data---====
		lv_recommand = (MyListView) findViewById(R.id.lv_recommand);
		setListViewHeightBasedOnChildren(lv_recommand);
		iv_setting_back_recommand = (ImageView) findViewById(R.id.iv_setting_back_recommand);

		iv_setting_back_recommand.setOnClickListener(this);
		MyAdapter ma = new MyAdapter();
		lv_recommand.setAdapter(ma);
		lv_recommand.setOnItemClickListener(this);

	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return apps.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(RecommandActivity.this,
						R.layout.recommand_item, null);
			}

			ImageView iv_logo_recommand = (ImageView) convertView
					.findViewById(R.id.iv_logo_recommand);
			TextView tv_app_name_recommand = (TextView) convertView
					.findViewById(R.id.tv_app_name_recommand);
			TextView tv_app_desc_recommand = (TextView) convertView
					.findViewById(R.id.tv_app_desc_recommand);
			// 也许不用处理点击事件，顶个imageview用。
			ImageView bt_app_recommand_status = (ImageView) convertView
					.findViewById(R.id.bt_app_recommand_status);
			int imageId = apps.get(position).imageId;
			System.out.println("------->imagedi:" + imageId);
			iv_logo_recommand.setBackgroundResource(imageId);
			tv_app_name_recommand.setText(apps.get(position).appName);
			tv_app_desc_recommand.setText(apps.get(position).appDesc);

			// 判断应用的安装情况。用来显示状态

			return convertView;
		}

	}

	public void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		((MarginLayoutParams) params).setMargins(10, 10, 10, 10);
		listView.setLayoutParams(params);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_setting_back_recommand:
			RecommandActivity.this.finish();
			startActivity(new Intent(this, SettingActivity.class));
			if ("ja3gchnduos".equals(android.os.Build.DEVICE)
					&& "ja3gchnduoszn".equals(android.os.Build.PRODUCT))
				break;
			overridePendingTransition(R.anim.nochange_in,
					R.anim.slide_from_left_out);
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			this.finish(); // finish当前activity
			startActivity(new Intent(this, SettingActivity.class));
			if ("ja3gchnduos".equals(android.os.Build.DEVICE)
					&& "ja3gchnduoszn".equals(android.os.Build.PRODUCT))
				return true;
			overridePendingTransition(R.anim.nochange_in,
					R.anim.slide_from_left_out);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		System.out.println("position:------->" + position);
		RecommandAppBean appBean = apps.get(position);
		final String url = appBean.url;
		pd = new ProgressDialog(RecommandActivity.this);
		pd.setTitle("正在下载");
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setCanceledOnTouchOutside(false);
		String filename = appBean.appName+".apk";
		final File file;
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			file = new File(Environment.getExternalStorageDirectory(),filename);
		} else {
			file = new File(getCacheDir(), filename);
		}
		pd.show();
		new Thread() {
			public void run() {
				File newfile = Utils.download(url, file.getAbsolutePath(),pd);
				if (newfile == null) {
					// 下载失败 
					Message msg = Message.obtain();
					msg.what = Constant.DOWNLOAD_FAILED;
					msg.obj = "下载失败！";
					msg.arg1 = R.drawable.fail;
					handler.sendMessage(msg);
				} else {
					// 下载成功,安装应用程序
					//显示一个notication去安装
					intstallApk(newfile);
				}
				pd.dismiss();
			}
			private void intstallApk(File newfile) {
				Intent intent = new Intent();
				intent.setAction("android.intent.action.VIEW");
				intent.addCategory("android.intent.category.DEFAULT");
				intent.setDataAndType(Uri.fromFile(newfile),
						"application/vnd.android.package-archive");
				startActivity(intent);
			};
		}.start();
	}
}
