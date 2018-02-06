package cn.aiworks.note.constant;

import android.content.Context;

import com.youdao.note.sdk.openapi.IYNoteAPI;
import com.youdao.note.sdk.openapi.YNoteAPIFactory;

/**
 *
 * @author KISN
 *
 */
public class SDKConst {

//  应用指纹 "6bf21adf569245fdcd6f9d74a80793d4";
    public static String sAppId = "c6982334fa3b6ce8f6705216a559fde5743f8821";
    
    public static String CREATE_NOTE_REQUEST_ACTION = "com.youdao.note.sdk.action.create_note";
    
    public static String EDIT_NOTE_REQUEST_ACTION = "com.youdao.note.sdk.action.edit_note";
    
    public static IYNoteAPI getYNoteAPI(Context context){
        return YNoteAPIFactory.getYNoteAPI(context, SDKConst.sAppId);
    }
}