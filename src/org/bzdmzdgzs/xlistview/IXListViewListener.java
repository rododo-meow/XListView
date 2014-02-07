package org.bzdmzdgzs.xlistview;

/**
 * XListView刷新与加载事件监听器接口。
 * 
 * @author lzn
 */
public interface IXListViewListener {
	
	/**
	 * 当XListView开始刷新时调用。在UI线程中调用。
	 */
	public void onRefresh();
	
	/**
	 * 当XListView开始加载更多内容时调用。在UI线程中调用。
	 */
	public void onLoad();
	
}
