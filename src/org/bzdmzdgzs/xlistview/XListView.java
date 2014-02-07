package org.bzdmzdgzs.xlistview;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;
import android.widget.Scroller;

public class XListView extends ListView {
	/** 下拉刷新的移动缩小倍率 */
	private static final float MOVEMENT_RATIO = 1.8f;
	/** 弹回动画的持续时间 */
	private static final int SCROLLBACK_DURATION = 500;
	/** 刷新加载结果的持续时间 */
	private static final int RESULT_DURATION = 3000;
	private Header headerView;
	private float lastTouchY;
	/** headerView的完整高度 */
	private int headerFullHeight;
	/** 用于弹回动画的Scroller */
	private Scroller scroller;
	/** 正在刷新的标志 */
	private boolean refreshing;
	/** 当前的监听器 */
	private IXListViewListener listener;

	/**
	 * 控件构造器。
	 * 
	 * @param context
	 *            控件所在的Context
	 * @param attrs
	 *            控件的构造参数
	 */
	public XListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		headerView = new Header(context);
		headerView.setNormal();
		headerView.setVisibleHeight(0);
		headerFullHeight = headerView.getFullHeight();
		addHeaderView(headerView);

		scroller = new Scroller(context);

		refreshing = false;
		listener = null;
	}

	/**
	 * 开始刷新。
	 */
	public void startRefresh() {
		if (refreshing)
			return;
		refreshing = true;
		scroller.startScroll(0, headerView.getVisibleHeight(), 0,
				headerFullHeight - headerView.getVisibleHeight(),
				SCROLLBACK_DURATION); // 弹出到完全显示状态
		invalidate();
		headerView.setRefreshing();
		if (listener != null)
			listener.onRefresh();
	}

	/**
	 * 结束刷新。
	 */
	public void stopRefresh() {
		if (!refreshing)
			return;
		refreshing = false;
		headerView.setNormal();
		scroller.startScroll(0, headerView.getVisibleHeight(), 0,
				-headerView.getVisibleHeight(), SCROLLBACK_DURATION); // 立即弹回
		invalidate();
	}

	/**
	 * 结束刷新并显示刷新结果。
	 * 
	 * @param result
	 *            刷新的结果
	 */
	public void stopRefresh(String result) {
		if (!refreshing)
			return;
		refreshing = false;
		headerView.setResult(result);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				headerView.setNormal();
				scroller.startScroll(0, headerView.getVisibleHeight(), 0,
						-headerView.getVisibleHeight(), SCROLLBACK_DURATION); // 延迟弹回
				invalidate();
			}
		}, RESULT_DURATION); // 延迟RESULT_DURATION
	}

	/**
	 * 设置最后刷新时间。传入空字符串来取消最后刷新时间显示。
	 * 
	 * @param time
	 *            最后刷新时间
	 */
	public void setRefreshTime(String time) {
		headerView.setRefreshTime(time);
	}

	/**
	 * 设置刷新加载事件监听器。
	 * 
	 * @param listener
	 *            要设置的监听器
	 */
	public void setXListViewListener(IXListViewListener listener) {
		this.listener = listener;
	}

	/**
	 * 更新headerView的显示高度。保证更新后的高度>=0。正在刷新或显示结果状态保证更新后高度>=完整高度
	 * 
	 * @param dy
	 *            高度差。正数表示增加显示高度
	 */
	private void updateHeaderHeight(int dy) {
		int height = headerView.getVisibleHeight() + dy;
		headerView.setVisibleHeight(height);
		if (refreshing || headerView.isResult())
			return;
		if (height > headerFullHeight)
			headerView.setReady(); // 超出完全高度后设为松开刷新
		else
			headerView.setNormal(); // 少于完全高度后设为下拉刷新
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: // 开始触控
			lastTouchY = event.getRawY(); // 初始化上次触控点位置
			break;
		case MotionEvent.ACTION_MOVE: // 触控点移动
			float movement = (event.getRawY() - lastTouchY) / MOVEMENT_RATIO; // 计算手指移动距离
			lastTouchY = event.getRawY(); // 更新上次触控点位置
			if ((getAdapter().getCount() == 0) // XListView当前没内容
					|| (getFirstVisiblePosition() == 0)) { // 或当前已滚动到XListView的开头
				if ((movement > 0) || (headerView.getVisibleHeight() > 0))
					updateHeaderHeight((int) (movement / MOVEMENT_RATIO)); // 下拉移动或headerView正在显示时更新headerView显示高度
			}
			break;
		default: // 触控点超出屏幕或停止触控等
			if (headerView.getVisibleHeight() > headerFullHeight) // 释放时headerView完全显示则开始刷新
				startRefresh();
			else {
				scroller.startScroll(0, headerView.getVisibleHeight(), 0,
						-headerView.getVisibleHeight()); // 其他情况下回滚隐藏headerView
				invalidate();
			}
			break;
		}
		return super.onTouchEvent(event);
	}

	@Override
	public void computeScroll() {
		if (scroller.computeScrollOffset()) { // 计算判断滚动动画是否完成
			if (scroller.getCurrY() > 0) // getCurrY() > 0 即headerView动画
				headerView.setVisibleHeight(scroller.getCurrY()); // 根据滚动动画位置设置headerView显示高度
			postInvalidate();
		}
		super.computeScroll();
	}
}
