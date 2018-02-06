package cn.aiworks.note.application;

import java.util.HashMap;

import android.app.Application;

public class EverInputApplication extends Application {

	public static  HashMap<String, Object> appConstant;
	public EverInputApplication(){
		super();
		appConstant = new HashMap<String, Object>();
	}
	/**
	 * 获取应用常量
	 * @return
	 */
	public  HashMap<String, Object> getAppConstant(){
		if(appConstant != null)
			return appConstant;
		else {
			appConstant = new HashMap<String, Object>();
			return appConstant;
		}
	}
	
}
