package com.baymin.ui.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.umeng.analytics.MobclickAgent;

public class BaseActivity extends Activity{

    private Context mContext;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mContext = this;
    }
    protected void initView(){
    }
    
    public Context getContext(){
        return mContext;
    }
    
    protected Object getObjectFromIntent(String key){
        if(getIntent() != null && getIntent().getSerializableExtra(key) != null){
            return getIntent().getSerializableExtra(key);
        }
        return null;
    }
    protected void onResume(){
    	super.onResume();
    	MobclickAgent.onResume(this);
    }
    protected void onPause(){
    	super.onPause();
    	MobclickAgent.onPause(this);
    }
    
}
