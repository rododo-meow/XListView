package org.bzdmzdgzs.xlistview;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;
import android.widget.Scroller;

public class XListView extends ListView {
	private static final float MOVEMENT_RATIO = 1.8f;
	private static final int SCROLLBACK_DURATION = 500;
	private static final int RESULT_DURATION = 3000;
	private Header headerView;
	private float lastTouchY;
	private int headerFullHeight;
	private Scroller scroller;
	private boolean refreshing;
	private IXListViewListener listener;

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

	public void startRefresh() {
		if (refreshing)
			return;
		refreshing = true;
		scroller.startScroll(0, headerView.getVisibleHeight(), 0,
				headerFullHeight - headerView.getVisibleHeight(),
				SCROLLBACK_DURATION);
		invalidate();
		headerView.setRefreshing();
		if (listener != null)
			listener.onRefresh();
	}

	public void stopRefresh() {
		if (!refreshing)
			return;
		refreshing = false;
		headerView.setNormal();
		scroller.startScroll(0, headerView.getVisibleHeight(), 0,
				-headerView.getVisibleHeight(), SCROLLBACK_DURATION);
		invalidate();
	}
	
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
						-headerView.getVisibleHeight(), SCROLLBACK_DURATION);
				invalidate();
			}
		}, RESULT_DURATION);
	}
	
	public void setRefreshTime(String time) {
		headerView.setRefreshTime(time);
	}

	public void setXListViewListener(IXListViewListener listener) {
		this.listener = listener;
	}

	private void updateHeaderHeight(int dy) {
		int height = headerView.getVisibleHeight() + dy;
		headerView.setVisibleHeight(height);
		if (height > headerFullHeight)
			headerView.setReady();
		else
			headerView.setNormal();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			lastTouchY = event.getRawY();
			break;
		case MotionEvent.ACTION_MOVE:
			float movement = event.getRawY() - lastTouchY;
			lastTouchY = event.getRawY();
			if ((getAdapter().getCount() == 0)
					|| (getFirstVisiblePosition() == 0)) {
				if ((movement > 0) || (headerView.getVisibleHeight() > 0))
					if ((!refreshing)
							|| (movement > 0)
							|| (headerView.getVisibleHeight() > headerFullHeight))
						updateHeaderHeight((int) (movement / MOVEMENT_RATIO));
			}
			break;
		default:
			if (headerView.getVisibleHeight() > headerFullHeight)
				startRefresh();
			else {
				scroller.startScroll(0, headerView.getVisibleHeight(), 0,
						-headerView.getVisibleHeight());
				invalidate();
			}
			break;
		}
		return super.onTouchEvent(event);
	}

	@Override
	public void computeScroll() {
		if (scroller.computeScrollOffset()) {
			if (scroller.getCurrY() > 0)
				headerView.setVisibleHeight(scroller.getCurrY());
			postInvalidate();
		}
		super.computeScroll();
	}
}
