package cn.aiworks.note.ynoteapi;

import com.youdao.note.sdk.openapi.IYNoteAPI;

import cn.aiworks.note.constant.SDKConst;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class YNoteOpenRegister extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        final IYNoteAPI api = SDKConst.getYNoteAPI(context);
        if(!api.isRegistered()){
            api.registerApp();
        }
    }
}
