package org.bzdmzdgzs.xlistview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

class Header extends LinearLayout {
	private static final int ROTATE_DURATION = 300;
	
	private static final int STATE_NORMAL = 0;
	private static final int STATE_READY = 1;
	private static final int STATE_REFRESHING = 2;
	private static final int STATE_RESULT = 3;
	private int state;
	
	private LinearLayout layoutContainer;
	private LinearLayout layoutContent;
	private ImageView imageView;
	private ProgressBar progressBar;
	private TextView textHint;
	private TextView textRefreshTime;
	private LinearLayout layoutRefreshTime;
	
	private RotateAnimation animRollUp;
	private RotateAnimation animRollDown;
	public Header(Context context) {
		super(context, null);
		LayoutInflater.from(context).inflate(R.layout.xlistview_header, this, true);
		
		layoutContainer = (LinearLayout) findViewById(R.id.header_container);
		layoutContent = (LinearLayout) findViewById(R.id.header_content);
		imageView = (ImageView) findViewById(R.id.header_image);
		progressBar = (ProgressBar) findViewById(R.id.header_progressBar);
		textHint = (TextView) findViewById(R.id.header_hint);
		textRefreshTime = (TextView) findViewById(R.id.header_refreshtime);
		layoutRefreshTime = (LinearLayout) findViewById(R.id.header_refreshtime_content);
		
		animRollUp = new RotateAnimation(0, 180, imageView.getLayoutParams().height / 2, imageView.getLayoutParams().width / 2);
		animRollUp.setDuration(ROTATE_DURATION);
		animRollUp.setFillAfter(true);
		animRollDown = new RotateAnimation(180, 360, imageView.getLayoutParams().height / 2, imageView.getLayoutParams().width / 2);
		animRollDown.setDuration(ROTATE_DURATION);
		animRollDown.setFillAfter(true);
		
		setNormal();
		setRefreshTime("");
	}
	public void setNormal() {
		if (state == STATE_NORMAL)
			return;
		imageView.setVisibility(VISIBLE);
		imageView.clearAnimation();
		progressBar.setVisibility(INVISIBLE);
		if (state == STATE_READY)
			imageView.startAnimation(animRollDown);
		textHint.setText(R.string.header_hint);
		state = STATE_NORMAL;
	}
	public void setReady() {
		if (state == STATE_READY)
			return;
		imageView.setVisibility(VISIBLE);
		imageView.clearAnimation();
		progressBar.setVisibility(INVISIBLE);
		if (state == STATE_NORMAL)
			imageView.startAnimation(animRollUp);
		textHint.setText(R.string.header_ready);
		state = STATE_READY;
	}
	public void setRefreshing() {
		if (state == STATE_REFRESHING)
			return;
		imageView.setVisibility(INVISIBLE);
		imageView.clearAnimation();
		progressBar.setVisibility(VISIBLE);
		textHint.setText(R.string.header_refreshing);
		state = STATE_REFRESHING;
	}
	public void setResult(String result) {
		imageView.setVisibility(GONE);
		imageView.clearAnimation();
		progressBar.setVisibility(GONE);
		textHint.setText(result);
		state = STATE_RESULT;
	}
	public void setRefreshTime(String time) {
		if (time.isEmpty())
			layoutRefreshTime.setVisibility(GONE);
		else {
			layoutRefreshTime.setVisibility(VISIBLE);
			textRefreshTime.setText(time);
		}
	}
	public void setVisibleHeight(int height) {
		if (height < 0)
			height = 0;
		android.view.ViewGroup.LayoutParams params = layoutContainer.getLayoutParams();
		params.height = height;
		layoutContainer.setLayoutParams(params);
	}
	public int getVisibleHeight() {
		return layoutContainer.getLayoutParams().height;
	}
	public int getFullHeight() {
		return layoutContent.getLayoutParams().height;
	}
}
