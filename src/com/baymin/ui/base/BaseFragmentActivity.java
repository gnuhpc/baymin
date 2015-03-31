package com.baymin.ui.base;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class BaseFragmentActivity extends FragmentActivity {
    private Context mContext;
    private FragmentManager fm;

    /**  
     * @param arg0 
     */
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        mContext = this;
        fm = getSupportFragmentManager();
    }

    protected void initView(){
        
    }

    public Context getContext() {
        return mContext;
    }

    public void replaceFragment(int id, BaseFragment fragment) {
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(id, fragment);
        transaction.commit();
    }
    protected Object getObjectFromIntent(String key){
        if(getIntent() != null && getIntent().getSerializableExtra(key) != null){
            return getIntent().getSerializableExtra(key);
        }
        return null;
    }
    
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
