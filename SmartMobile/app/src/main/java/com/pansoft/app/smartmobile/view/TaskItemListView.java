package com.pansoft.app.smartmobile.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Scroller;

import com.pansoft.app.smartmobile.R;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;


public class TaskItemListView extends ListView implements PtrHandler,AbsListView.OnScrollListener {
	private UltraRefreshListener mUltraRefreshListener;
	/**
	 * 根布局
	 */
	private View footView;
	/**
	 * 是否正在加载数据
	 */
	private boolean isLoadData = false;
	/**
	 * 是否是下拉刷新，主要在处理结果时使用
	 */
	private boolean isRefresh = false;
	/*禁止侧滑模式*/
	public static int MOD_FORBID = 0;
	/*从左向右滑出菜单模式*/
	public static int MOD_LEFT = 1;
	/*从右向左滑出菜单模式*/
	public static int MOD_RIGHT = 2;
	/*左右均可以滑出菜单模式*/
	public static int MOD_BOTH = 3;
	/*当前的模式*/
	private int mode = MOD_RIGHT;
	/*左侧菜单的长度*/
	private int leftLength = 0;
	/*右侧菜单的长度*/
	private int rightLength = 0;
	/*当前滑动的ListView　position*/
	private int slidePosition;
	/* 手指按下X的坐标*/
	private int downY;
	/* 手指按下Y的坐标*/
	private int downX;
	/* ListView的item*/
	private View itemView;
	/* 滑动类*/
	private Scroller scroller;
	/* 认为是用户滑动的最小距离*/
	private int mTouchSlop;
	/* 判断是否可以侧向滑动*/
	private boolean canMove = false;
	/* 标示是否完成侧滑*/
	private boolean isSlided = false;
	private int leftPadding;


	/**
	 * 当前是否启动加载更多
	 */
	private boolean isAddMore = false;
	public TaskItemListView(Context context) {
		this(context, null);
	}

	public TaskItemListView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public TaskItemListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		scroller = new Scroller(context);
		mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
		initView();
	}
	private void initView() {
		footView = LayoutInflater.from(getContext()).inflate(R.layout.foot_refresh_listview, null);
		leftPadding = this.getPaddingLeft();
		setOnScrollListener(this);
	}
	/**
	 * 初始化菜单的滑出模式
	 * @param mode
	 */
	public void initSlideMode(int mode){
		this.mode = mode;
	}
	public void initAddMore(boolean isAddMore){
		this.isAddMore = isAddMore;
	}
	public int getLeftPadding(){
		return leftPadding;
	}
	/**
	 * 处理我们拖动ListView item的逻辑
	 */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {

		final int action = ev.getAction();
		int lastX = (int) ev.getX();
		switch (action) {
			case MotionEvent.ACTION_DOWN:
				System.out.println("touch-->" + "down");

			/*当前模式不允许滑动，则直接返回，交给ListView自身去处理*/
				if(this.mode == MOD_FORBID){
					return super.onTouchEvent(ev);
				}

				// 如果处于侧滑完成状态，侧滑回去，并直接返回
				if (isSlided) {
					MotionEvent cancelEvent = MotionEvent.obtain(ev);
					cancelEvent
							.setAction(MotionEvent.ACTION_CANCEL
									| (ev.getActionIndex() << MotionEvent.ACTION_POINTER_INDEX_SHIFT));
					onTouchEvent(cancelEvent);
					scrollBack();
					return true;
				}
				// 假如scroller滚动还没有结束，我们直接返回
				if (!scroller.isFinished()) {
					return false;
				}
				downX = (int) ev.getX();
				downY = (int) ev.getY();

				slidePosition = pointToPosition(downX, downY);

				// 无效的position, 不做任何处理
				if (slidePosition == AdapterView.INVALID_POSITION) {
					return super.onTouchEvent(ev);
				}

				// 获取我们点击的item view
				itemView = getChildAt(slidePosition - getFirstVisiblePosition());

			/*此处根据设置的滑动模式，自动获取左侧或右侧菜单的长度*/
				if(this.mode == MOD_BOTH){
					this.leftLength = -itemView.getPaddingLeft();
					this.rightLength = -itemView.getPaddingRight();
				}else if(this.mode == MOD_LEFT){
					this.leftLength = -itemView.getPaddingLeft();
				}else if(this.mode == MOD_RIGHT){
					if (itemView != footView) {
						this.rightLength = itemView.findViewById(R.id.ll_right).getWidth();
					}
				}

				break;
			case MotionEvent.ACTION_MOVE:

				if (!canMove
						&& slidePosition != AdapterView.INVALID_POSITION
						&& (Math.abs(ev.getX() - downX) > Math.abs(ev
						.getY() - downY) && Math.abs(ev
						.getY() - downY) < mTouchSlop)) {
					int offsetX = downX - lastX;
					if(offsetX > 0 && (this.mode == MOD_BOTH || this.mode == MOD_RIGHT)){
					/*从右向左滑*/
						canMove = true;
					}else if(offsetX < 0 && (this.mode == MOD_BOTH || this.mode == MOD_LEFT)){
					/*从左向右滑*/
						canMove = true;
					}else{
						canMove = false;
					}
					System.out.println("touch-->" + "move"+offsetX);
				/*此段代码是为了避免我们在侧向滑动时同时出发ListView的OnItemClickListener时间*/
					MotionEvent cancelEvent = MotionEvent.obtain(ev);
					cancelEvent
							.setAction(MotionEvent.ACTION_CANCEL
									| (ev.getActionIndex() << MotionEvent.ACTION_POINTER_INDEX_SHIFT));
					onTouchEvent(cancelEvent);
				}
				if (canMove) {
				/*设置此属性，可以在侧向滑动时，保持ListView不会上下滚动*/
					requestDisallowInterceptTouchEvent(true);

					// 手指拖动itemView滚动, deltaX大于0向左滚动，小于0向右滚
					int deltaX = downX - lastX;
					System.out.println(deltaX);
					if(deltaX < 0 && (this.mode == MOD_BOTH || this.mode == MOD_LEFT)){
					/*向左滑*/
						itemView.scrollTo(deltaX, 0);
					}else if(rightLength > deltaX && deltaX > 0 && (this.mode == MOD_BOTH || this.mode == MOD_RIGHT)){
					/*向右滑*/
						itemView.scrollTo(deltaX, 0);
					}
					return true; // 拖动的时候ListView不滚动
				}
			case MotionEvent.ACTION_UP:
				System.out.println("touch-->" + "up");

				if (canMove){
					canMove = false;
					scrollByDistanceX();
				}
				break;
		}

		// 否则直接交给ListView来处理onTouchEvent事件
		return super.onTouchEvent(ev);
	}

	/**
	 * 根据手指滚动itemView的距离来判断是滚动到开始位置还是向左或者向右滚动
	 */
	private void scrollByDistanceX() {
		/*当前模式不允许滑动，则直接返回*/
		if(this.mode == MOD_FORBID){
			return;
		}
		if(itemView.getScrollX() > 0 && (this.mode == MOD_BOTH || this.mode == MOD_RIGHT)){
			/*从右向左滑*/
			if (itemView.getScrollX() >= rightLength / 3) {
				scrollLeft();
			}  else {
				// 滚回到原始位置
				scrollBack();
			}
		}else if(itemView.getScrollX() < 0 && (this.mode == MOD_BOTH || this.mode == MOD_LEFT)){
			/*从左向右滑*/
			if (itemView.getScrollX() <= -leftLength / 3) {
				scrollRight();
			} else {
				// 滚回到原始位置
				scrollBack();
			}
		}else{
			// 滚回到原始位置
			scrollBack();
		}

	}

	/**
	 * 往右滑动，getScrollX()返回的是左边缘的距离，就是以View左边缘为原点到开始滑动的距离，所以向右边滑动为负值
	 */
	private void scrollRight() {
		isSlided = true;
		final int delta = (leftLength + itemView.getScrollX());
		// 调用startScroll方法来设置一些滚动的参数，我们在computeScroll()方法中调用scrollTo来滚动item
		scroller.startScroll(itemView.getScrollX(), 0, -delta, 0,
				Math.abs(delta));
		postInvalidate(); // 刷新itemView
	}

	/**
	 * 向左滑动，根据上面我们知道向左滑动为正值
	 */
	private void scrollLeft() {
		isSlided = true;
		final int delta = (rightLength - itemView.getScrollX());
		// 调用startScroll方法来设置一些滚动的参数，我们在computeScroll()方法中调用scrollTo来滚动item
		scroller.startScroll(itemView.getScrollX(), 0, delta, 0,
				Math.abs(delta));
		postInvalidate(); // 刷新itemView
	}

	/**
	 * 滑动会原来的位置
	 */
	private void scrollBack() {
		isSlided = false;
		scroller.startScroll(itemView.getScrollX(), 0, -itemView.getScrollX(),
				0, 500);
		postInvalidate(); // 刷新itemView
	}

	@Override
	public void computeScroll() {
		// 调用startScroll的时候scroller.computeScrollOffset()返回true，
		if (scroller.computeScrollOffset()) {
			// 让ListView item根据当前的滚动偏移量进行滚动
			itemView.scrollTo(scroller.getCurrX(), scroller.getCurrY());
			postInvalidate();
		}
	}
	/**
	 * 提供给外部调用，用以将侧滑出来的滑回去
	 */
	public void slideBack() {
		this.scrollBack();
	}
	public boolean isSlide(){
		return this.isSlided;
	}
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		//加载更多的判断
		if (isAddMore){
			if(totalItemCount>1&&!isLoadData&&totalItemCount==firstVisibleItem+visibleItemCount){
				isRefresh =false;
				isLoadData = true;
				addFooterView(footView);
				mUltraRefreshListener.addMore();
			}
		}
	}
	/**
	 * 设置ListView的监听回调
	 */
	public void setUltraRefreshListener(UltraRefreshListener mUltraRefreshListener) {
		this.mUltraRefreshListener = mUltraRefreshListener;
	}
	//刷新完成的后调用此方法还原布局
	public void refreshComplete() {
		isLoadData = false;
		if (isRefresh) {
			//获取其父控件，刷新
			ViewParent parent = getParent();
			if (parent instanceof PtrClassicFrameLayout) {
				((PtrClassicFrameLayout) parent).refreshComplete();
			}
		} else {
			removeFooterView(footView);
		}
	}
	@Override
	public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
		//  PtrHandler 的接口回调，是否能够加载数据的判断
		if (canMove){
			return  false;
		}else{
			return !isLoadData&&checkContentCanBePulledDown(frame, content, header);
		}
	}
	// 从PtrHandler的默认实现类 PtrDefaultHandler中找到的，用以判断是否可以下拉刷新
	public static boolean checkContentCanBePulledDown(PtrFrameLayout frame, View content, View header) {
		return !canChildScrollUp(content);

	}

	// 从PtrHandler的默认实现类 PtrDefaultHandler中找到的，用以判断是否可以下拉刷新
	public static boolean canChildScrollUp(View view) {
		if (android.os.Build.VERSION.SDK_INT < 14) {
			if (view instanceof AbsListView) {
				final AbsListView absListView = (AbsListView) view;
				return absListView.getChildCount() > 0
						&& (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
						.getTop() < absListView.getPaddingTop());
			} else {
				return view.getScrollY() > 0;
			}
		} else {
			return view.canScrollVertically(-1);
		}
	}
	@Override
	public void onRefreshBegin(PtrFrameLayout frame) {
		isLoadData  =true;
		isRefresh =true;
		//下拉刷新的回调
		if(mUltraRefreshListener!=null){

			mUltraRefreshListener.onRefresh();
		}
	}
}