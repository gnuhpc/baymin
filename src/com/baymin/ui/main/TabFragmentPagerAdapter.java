package com.baymin.ui.main;

import java.util.ArrayList;

import com.baymin.log.AppLogger;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class TabFragmentPagerAdapter extends FragmentPagerAdapter{
	private FragmentManager fm;
	private ArrayList<Fragment> fragmentList;
	
	public TabFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragmentList) {
		super(fm);
		this.fm = fm;
		this.fragmentList = fragmentList;
	}

	@Override
	public Fragment getItem(int arg0) {
		return fragmentList.get(arg0);
	}

	@Override
	public int getCount() {
		return fragmentList.size();
	}
	
	@Override
	public Object instantiateItem (ViewGroup container, int position){
		AppLogger.d("instantiateItem() called!");
		Fragment fragment = (Fragment) super.instantiateItem(container, position);
		fm.beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).show(fragment).commit();
		return fragment;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		Fragment fragment = fragmentList.get(position);
		fm.beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).hide(fragment).commit();
	}

}
