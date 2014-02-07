package org.bzdmzdgzs.xlistview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

class Header extends LinearLayout {
	/** 指针图标旋转动画的持续时间 */
	private static final int ROTATE_DURATION = 300;

	private static final int STATE_NORMAL = 0;
	private static final int STATE_READY = 1;
	private static final int STATE_REFRESHING = 2;
	private static final int STATE_RESULT = 3;
	private int state;
	private int fullHeight;

	private LinearLayout layoutContainer;
	private ImageView imageView;
	private ProgressBar progressBar;
	private TextView textHint;
	private TextView textRefreshTime;
	private LinearLayout layoutRefreshTime;

	private RotateAnimation animRollUp;
	private RotateAnimation animRollDown;

	/**
	 * 构造器。
	 * 
	 * @param context
	 *            Header所在的Context
	 */
	public Header(Context context) {
		super(context, null);
		LayoutInflater.from(context).inflate(R.layout.xlistview_header, this,
				true);

		layoutContainer = (LinearLayout) findViewById(R.id.header_container);
		imageView = (ImageView) findViewById(R.id.header_image);
		progressBar = (ProgressBar) findViewById(R.id.header_progressBar);
		textHint = (TextView) findViewById(R.id.header_hint);
		textRefreshTime = (TextView) findViewById(R.id.header_refreshtime);
		layoutRefreshTime = (LinearLayout) findViewById(R.id.header_refreshtime_content);

		animRollUp = new RotateAnimation(0, 180,
				imageView.getLayoutParams().height / 2,
				imageView.getLayoutParams().width / 2);
		animRollUp.setDuration(ROTATE_DURATION);
		animRollUp.setFillAfter(true);
		animRollDown = new RotateAnimation(180, 360,
				imageView.getLayoutParams().height / 2,
				imageView.getLayoutParams().width / 2);
		animRollDown.setDuration(ROTATE_DURATION);
		animRollDown.setFillAfter(true);

		fullHeight = ((LinearLayout) findViewById(R.id.header_content))
				.getLayoutParams().height;
		state = STATE_NORMAL + 1;
		setNormal();
		setRefreshTime("");
	}

	/**
	 * 将Header外观设置为正常（下拉刷新）状态。显示向下的指针图标， 隐藏刷新图标，提示文字为下拉刷新。
	 */
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

	/**
	 * 判断Header是否在正常（下拉刷新）状态
	 * 
	 * @return
	 */
	public boolean isNormal() {
		return state == STATE_NORMAL;
	}

	/**
	 * 将Header外观设置为就绪（释放立即刷新）状态。显示向上的指针图标，隐藏刷新图标，提示文字为释放立即刷新。
	 */
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

	/**
	 * 判断Header是否在就绪（释放立即刷新）状态
	 * 
	 * @return
	 */
	public boolean isReady() {
		return state == STATE_READY;
	}

	/**
	 * 将Header外观设置为正在刷新状态。隐藏指针图标，显示刷新图标，提示文字为正在刷新。
	 */
	public void setRefreshing() {
		if (state == STATE_REFRESHING)
			return;
		imageView.setVisibility(INVISIBLE);
		imageView.clearAnimation();
		progressBar.setVisibility(VISIBLE);
		textHint.setText(R.string.header_refreshing);
		state = STATE_REFRESHING;
	}
	
	/**
	 * 判断Header是否在刷新状态
	 * 
	 * @return
	 */
	public boolean isRefreshing() {
		return state == STATE_REFRESHING;
	}

	/**
	 * 将Header外观设置为显示结果状态。必须在正在刷新或显示结果状态调用。隐藏指针图标，隐藏刷新图标，提示文字为刷新结果。
	 * 
	 * @param result
	 *            刷新结果
	 */
	public void setResult(String result) {
		if ((state != STATE_REFRESHING) && (state != STATE_RESULT))
			return;
		imageView.setVisibility(GONE);
		imageView.clearAnimation();
		progressBar.setVisibility(GONE);
		textHint.setText(result);
		state = STATE_RESULT;
	}

	/**
	 * 判断Header是否在显示结果状态
	 * 
	 * @return
	 */
	public boolean isResult() {
		return state == STATE_RESULT;
	}

	/**
	 * 设置最后刷新时间。传入空字符串来关闭最后刷新时间显示。
	 * 
	 * @param time
	 *            最后刷新时间
	 */
	public void setRefreshTime(String time) {
		if (time.isEmpty())
			layoutRefreshTime.setVisibility(GONE);
		else {
			layoutRefreshTime.setVisibility(VISIBLE);
			textRefreshTime.setText(time);
		}
	}

	/**
	 * 设置Header的显示高度。height < 0时将高度设置为0
	 * 
	 * @param height
	 *            新的显示高度
	 */
	public void setVisibleHeight(int height) {
		if (height < 0)
			height = 0;
		if (((state == STATE_REFRESHING) || (state == STATE_RESULT))
				&& (height < fullHeight))
			height = fullHeight;
		android.view.ViewGroup.LayoutParams params = layoutContainer
				.getLayoutParams();
		params.height = height;
		layoutContainer.setLayoutParams(params);
	}

	/**
	 * 获取当前的显示高度
	 * 
	 * @return 当前的显示高度
	 */
	public int getVisibleHeight() {
		return layoutContainer.getLayoutParams().height;
	}

	/**
	 * 获取Header的完整高度
	 * 
	 * @return Header的完整高度
	 */
	public int getFullHeight() {
		return fullHeight;
	}

}
