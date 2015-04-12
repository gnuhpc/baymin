package com.baymin.ui.main;

import java.util.ArrayList;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

import com.baymin.ui.main.TabBottom.OnTabClickListener;

public class TabViewPager extends ViewPager implements OnTabClickListener{
	private ArrayList<Fragment> fragmentList = new ArrayList<Fragment>();

	public TabViewPager(Context context) {
		super(context);
	}

	public TabViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setFragments(ArrayList<Fragment> fragments, FragmentManager fm) {
		this.fragmentList = fragments;
		setAdapter(new TabFragmentPagerAdapter(fm, fragmentList));
	}
	
	@Override
	public void onTabClick(int index) {
		setCurrentItem(index,false);
	}

	@Override
	public int getFragmentCount() {
		return fragmentList.size();
	}
}
