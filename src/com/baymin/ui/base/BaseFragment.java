package com.baymin.ui.base;

import java.io.Serializable;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class BaseFragment extends Fragment {
    private FragmentManager fm;
    private String title;
    private String TAG = "BaseFragment";
    /** 特殊用途 **/
    private Object object;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fm = getActivity().getSupportFragmentManager();
        
    }
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }
    
    public void replaceFragment(int layoutId, BaseFragment fragment){
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(layoutId, fragment);
        ft.commit();
    }
    protected void initView(){
        
    }
    
    /**  
     * @return: title <BR>  
     */
    public String getTitle() {
        return title;
    }

    /**  
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }
    
    protected Object getObjectFromArguments(String key){
        if(getArguments() != null && getArguments().getSerializable(key) != null){
            
            return getArguments().getSerializable(key);
        }
        return null;
    } 
    
    public void putObject(String key, Serializable value){
        Bundle bundle = getBundle();
        bundle.putSerializable(key, value);
        setArguments(bundle);
    }
    public void putString(String key, String value){
        Bundle bundle = getBundle();
        bundle.putString(key, value);
//        setArguments(bundle);
    }
    public void putBoolean(String key, boolean value){
        Bundle bundle = getBundle();
        bundle.putBoolean(key, value);
//        setArguments(bundle);
    }
    
    public Bundle getBundle(){
        Bundle bundle = getArguments();
        if(bundle == null){
            bundle = new Bundle();
            setArguments(bundle);
        }
        return bundle;
    }

    public void setFragmentTag(String tag){
        this.TAG = tag;
    }
    
    public String getFragmentTag(){
        return TAG;
    }

    /**  
     * @return: object <BR>  
     */
    public Object getObject() {
        return object;
    }

    /**  
     * @param object
     */
    public void setObject(Object object) {
        this.object = object;
    }
    
}
