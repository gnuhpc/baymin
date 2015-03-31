package com.baymin.ui.main;

import java.util.ArrayList;

import com.umeng.analytics.f;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;

public class TabViewPager extends ViewPager {
	private ArrayList<Fragment> fragmentList = new ArrayList<Fragment>();
	private TabBottom mUITabBottom;
	private String TAG="ViewPager";

	public TabViewPager(Context context) {
		super(context);
	}

	public TabViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setFragments(ArrayList<Fragment> fragments, FragmentManager fm) {
		if (mUITabBottom!=null){
			if(fragments==null||fragments.size()!=mUITabBottom.getmTabCounts()) {
				throw new IllegalArgumentException("The count of fragments must be equal to number of tabs at bottom");
			}
		}
		this.fragmentList = fragments;
		setAdapter(new TabFragmentPagerAdapter(fm,fragmentList));
	}

	public void setUITabBottom(TabBottom tabBottom) {
		if (fragmentList!=null){
			if(tabBottom==null||fragmentList.size()!=tabBottom.getmTabCounts()) {
				throw new IllegalArgumentException("The count of fragments must be equal to number of tabs at bottom");
			}
		}
		this.mUITabBottom = tabBottom;
		this.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int pageIndex, float v, int i2) {
				mUITabBottom.scroll(pageIndex, v);
			}

			@Override
			public void onPageSelected(int i) {
				mUITabBottom.selectTab(i);// 刷新底部栏
			}

			@Override
			public void onPageScrollStateChanged(int i) {
			}
		});
	}
}
