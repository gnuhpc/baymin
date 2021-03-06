package com.baymin.ui.main;

import java.util.ArrayList;

import org.apache.commons.lang3.NotImplementedException;

import com.baymin.R;
import com.baymin.log.AppLogger;

import android.R.integer;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class TabBottom extends LinearLayout implements OnClickListener,ViewPager.OnPageChangeListener {
	public static interface OnTabClickListener {
		public void onTabClick(int index);
		public int getFragmentCount();
	}

	private ArrayList<TabItem> tabItems;
	private OnTabClickListener changeListener;
	private int[] tabIconUnClickPics;
	private int[] tabIconClickPics;
	private int textColorClick;
	private int textColorUnclick;
	private int R1;// 未选中的Red值
	private int G1;// 未选中的Green值
	private int B1;// 未选中的Blue值
	private int R2;// 选中的Red值
	private int G2;// 选中的Green值
	private int B2;// 选中的Blue值
	private int Rm = R2 - R1;// Red的差值
	private int Gm = G2 - G1;// Green的差值
	private int Bm = B2 - B1;// Blue的差值

	private int mIndex;// 记录当前tab的index
	private int mTabCounts;


	private Context context;
	private CharSequence[] tabTexts;

	public TabBottom(Context context) {
		super(context);
		this.context = context;
	}

	public TabBottom(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init(attrs);
	}

	public void setTextClickColor(int unclickColor, int clickColor) {
		textColorUnclick = getResources().getColor(unclickColor);
		textColorClick = getResources().getColor(clickColor);
	}
	
	/**
	 * 初始化参数
	 *
	 * @param attrs the attrs
	 */
	private void init(AttributeSet attrs) {
		initTabIconText(attrs);
		
		setOrientation(LinearLayout.HORIZONTAL);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
		layoutParams.weight = 1;

		for (int i = 0; i < mTabCounts; i++) {
			TabItem tabItem = new TabItem(context);
			tabItem.setTabText(String.valueOf(tabTexts[i]));
			tabItem.setTabTextColor(textColorUnclick);
			tabItem.setTabIcon(tabIconClickPics[i], tabIconUnClickPics[i]);
			tabItem.setTag(i);
			tabItem.setTabOnClickListener(this);
			addView(tabItem.getParent(), layoutParams);
			tabItems.add(i, tabItem);
		}

		R1 = (textColorClick & 0xff0000) >> 16;
		G1 = (textColorClick & 0xff00) >> 8;
		B1 = (textColorClick & 0xff);
		R2 = (textColorUnclick & 0xff0000) >> 16;
		G2 = (textColorUnclick & 0xff00) >> 8;
		B2 = (textColorUnclick & 0xff);
		Rm = R1 - R2;
		Gm = G1 - G2;
		Bm = B1 - B2;

		mIndex = 0;
	}

	private void initTabIconText(AttributeSet attrs) {
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BottomUI, 0, 0);
		try {
			textColorUnclick= ta.getColor(R.styleable.BottomUI_textunclickcolor, R.color.coldgray);
			textColorClick= ta.getColor(R.styleable.BottomUI_textclickcolor, R.color.white);
			tabTexts = ta.getTextArray(R.styleable.BottomUI_text);		
			if (tabTexts==null) {
				throw  new IllegalStateException("Please add \"text\" attr in your UITabBottom widget");
			}
			mTabCounts = tabTexts.length;
			int unclickPicResID = ta.getResourceId(R.styleable.BottomUI_iconunclick, 0);
			int clickPicResID = ta.getResourceId(R.styleable.BottomUI_iconclick, 0);
			if (unclickPicResID!=0 && clickPicResID!=0) {
				TypedArray unClickIconArray = this.getResources().obtainTypedArray(unclickPicResID);
				if (mTabCounts!=unClickIconArray.length()) {
					throw new NotImplementedException("The array of iconunclick and text must be equal");
				}
				tabIconUnClickPics = new int[mTabCounts];
				for (int i = 0; i < mTabCounts; i++) {
					tabIconUnClickPics[i] = unClickIconArray.getResourceId(i, -1);
				}
				unClickIconArray.recycle();

				TypedArray clickIconArray = this.getResources().obtainTypedArray(clickPicResID);
				if (mTabCounts!=clickIconArray.length()) {
					throw new NotImplementedException("The array of iconclick and text must be equal");
				}
				tabIconClickPics = new int[mTabCounts];
				for (int i = 0; i < mTabCounts; i++) {
					tabIconClickPics[i] = clickIconArray.getResourceId(i, -1);
				}
				clickIconArray.recycle();
				tabItems = new ArrayList<>(mTabCounts);
				
			}
			else {
				throw new NotImplementedException("Please add \"iconunclick\" and \"iconunclick\" attrs in your UITabBottom widget");
			}
			
			
		} finally {
			ta.recycle();
		}

	}

	public void setTabClickListener(OnTabClickListener changeListener) {
		if (changeListener!=null&&changeListener.getFragmentCount()!=mTabCounts) {
			throw new IllegalArgumentException("The count of fragments must be equal to number of tabs at bottom");
		}
		
		this.changeListener = changeListener;
	}

	public void setTabRedDot(int index, int state) {
		tabItems.get(index).setDotVisibility(state);
	}

	
	/**
	 * 点击Tab触发的事件
	 *
	 * @param index the index
	 */
	public void selectTab(int index) {
		if (mIndex == index) {// 重复点击同一个tab，则直接返回
			return;
		}

		mIndex = index;
		//切换Fragment
		if (changeListener != null) {
			changeListener.onTabClick(mIndex);
		}
		
		//调整对应Tab的图标和文字透明度与颜色
		tabItems.get(mIndex).setTabIconAlpha(255);
		tabItems.get(mIndex).setTabTextColor(textColorClick);
		for (int i = 0; i < mTabCounts; i++) {
			if (i != mIndex) {
				tabItems.get(i).setTabIconAlpha(0);
				tabItems.get(i).setTabTextColor(textColorUnclick);
			}
		}
	}

	/**
	 * 拼成颜色值
	 * 
	 * @param f
	 * @return
	 */
	private int getColorInt(float f) {
		int R = (int) (R2 + f * Rm);
		int G = (int) (G2 + f * Gm);
		int B = (int) (B2 + f * Bm);
		return 0xff << 24 | R << 16 | G << 8 | B;

	}

	/**
	 * index为最左边页面的index,e.g.,fragment0到fragment1,传入location=0 f为滑动距离的百分比
	 * 
	 * @param index
	 * @param f
	 */
	public void scroll(int index, float f) {
		int leftAlpha = (int) (255 * (1 - f));
		int rightAlpha = (int) (255 * f);
		int leftColor = getColorInt(1 - f);
		int rightColor = getColorInt(f);
		if (index < mTabCounts-1) {
			tabItems.get(index).setTabIconAlpha(leftAlpha);
			tabItems.get(index).setTabTextColor(leftColor);
			tabItems.get(index + 1).setTabIconAlpha(rightAlpha);
			tabItems.get(index + 1).setTabTextColor(rightColor);
		}
	}

	@Override
	public void onClick(View v) {
		int i = ((Integer) v.getTag()).intValue();
		selectTab(i);
	}
	
	public int getmTabCounts() {
		return mTabCounts;
	}
	
	/**
	 * Sets the tip icon.
	 *
	 * @param tabPos TAB位置，从0开始
	 * @param drawableRes TIP的图片，比如一个小红点
	 */
	public void setTipIcon(int tabPos, int drawableRes){
		tabItems.get(tabPos).setTipDrawable(drawableRes);
		tabItems.get(tabPos).setDotVisibility(VISIBLE);
	}
	
	public void setTipVisibility(int tabPos,int visibility) {
		tabItems.get(tabPos).setDotVisibility(visibility);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageScrolled(int pageIndex, float f, int arg2) {
		scroll(pageIndex, f);
	}

	@Override
	public void onPageSelected(int index) {
		selectTab(index);
	}

}
