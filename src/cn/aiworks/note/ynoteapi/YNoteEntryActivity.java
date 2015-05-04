package cn.aiworks.note.ynoteapi;

import cn.aiworks.note.TapeActivity;
import cn.aiworks.note.constant.SDKConst;

import com.youdao.note.sdk.openapi.IYNoteAPI;
import com.youdao.note.sdk.openapi.IYNoteEventHandler;
import com.youdao.note.sdk.openapi.YNoteAPIConstants;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 *
 * @author KISN
 *
 */
public class YNoteEntryActivity extends Activity implements IYNoteEventHandler{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final IYNoteAPI api = SDKConst.getYNoteAPI(this);
        api.handleIntent(getIntent(), this);
    }

    /**
     * handle ynote request
     */
    public void handleRequest(Intent requestIntent) {
        int requestType = requestIntent.getExtras().getInt(YNoteAPIConstants.MESSAGE_TYPE);
        switch (requestType) {
            
            //handle ynote create note request
            case YNoteAPIConstants.CREATE_NOTE_REQUEST_TYPE:
                Intent intent = new Intent(this, TapeActivity.class);
                intent.setAction(SDKConst.CREATE_NOTE_REQUEST_ACTION);
                startActivity(intent);
                break;
                
            //handle ynote edit note request    
            case YNoteAPIConstants.EDIT_NOTE_REQUEST_TYPE:
//                requestIntent.setClass(getApplicationContext(), EditNoteActivity.class);
//                requestIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                requestIntent.setAction(SDKConst.EDIT_NOTE_REQUEST_ACTION);
//                startActivity(requestIntent);
                break;
                
            default:
                break;
        }
        finish();
    }

    /**
     * handle ynote response
     */
    public void handleResponse(Intent reponseIntent) {
        //not used yet
        return;
    }
    
}
