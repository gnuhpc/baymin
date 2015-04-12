package com.baymin.ui.main;

import com.baymin.R;

import android.R.integer;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public final class TabItem {  
    private View parent;  
    private TabIconView iconView;//图片  
    private TextView labelView;//标签,如首页,我  
    private View tipView;//红点提示之类的  
    
    public TabItem(Context ctx) {
    	this.parent = LayoutInflater.from(ctx).inflate(R.layout.item_main, null);
		this.iconView = (TabIconView) this.parent.findViewById(R.id.mTabIcon);
		this.labelView = (TextView) this.parent.findViewById(R.id.mTabText);
		this.tipView = this.parent.findViewById(R.id.mTabTip);
	}
    
    public void setTabOnClickListener(OnClickListener listener) {
		this.parent.setOnClickListener(listener);
	}
    
    public void setTabText(String text) {
		this.labelView.setText(text);
	}
    
    public void setTabTextColor(int color)  {
		this.labelView.setTextColor(color);
	}
    
    public void setTabIconAlpha(int alpha)  {
		this.iconView.setmAlpha(alpha);
	}

	public void setTabIcon(int clickPic, int unclickPic) {
		this.iconView.init(clickPic, unclickPic);
	}
	
	public View getParent() {
		return this.parent;
	}
	
	public void setDotVisibility(int state) {
		this.tipView.setVisibility(state);
	}

	public void setTag(int i) {
		this.parent.setTag(i);
	}
	
	public void setTipDrawable(int drawableRes) {
		this.tipView.setBackgroundResource(drawableRes);
	}
}  