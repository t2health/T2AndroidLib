package org.t2health.lib.view;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

public class T2GestureDetector extends GestureDetector {
	private static final String TAG = T2GestureDetector.class.getSimpleName();
	
	private TwoPointScrollListener mTwoPointScrollListener;
	private MotionEvent mCurrentDownEvent;
	private int mTouchSlopSquare;
	private Context mContext;

	public T2GestureDetector(Context context, OnGestureListener listener) {
		super(context, listener);
		this.mContext = context;
		init();
	}
	
	public T2GestureDetector(Context context, OnGestureListener listener,
			Handler handler) {
		super(context, listener, handler);
		this.mContext = context;
		init();
	}
	
	private void init() {
		int touchSlop = ViewConfiguration.get(this.mContext).getScaledTouchSlop();
		this.mTouchSlopSquare = touchSlop * touchSlop;
	}
	
	public void setOnTwoPointScrollListener(TwoPointScrollListener l) {
		this.mTwoPointScrollListener = l;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		final int action = ev.getAction();
		
		if(ev.getPointerCount() > 1) {
			boolean handled = false;
			final float x0 = ev.getX(0);
			final float y0 = ev.getY(0);
			
			final float x1 = ev.getX(1);
			final float y1 = ev.getY(1);
			
			switch(action) {
				case MotionEvent.ACTION_DOWN:
					mCurrentDownEvent = MotionEvent.obtain(ev);
					break;
					
				case MotionEvent.ACTION_UP:
					mCurrentDownEvent = null;
					break;
					
				case MotionEvent.ACTION_MOVE:
					if(mCurrentDownEvent == null) {
						mCurrentDownEvent = MotionEvent.obtain(ev);
					}
					
					final float scrollX0 = mCurrentDownEvent.getX(0) - x0;
					final float scrollY0 = mCurrentDownEvent.getY(0) - y0;
					
					final float scrollX1 = mCurrentDownEvent.getX(1) - x1;
					final float scrollY1 = mCurrentDownEvent.getY(1) - y1;
					
					final int deltaX0 = (int) (x0 - mCurrentDownEvent.getX(0));
					final int deltaY0 = (int) (y0 - mCurrentDownEvent.getY(0));
					
					final int deltaX1 = (int) (x1 - mCurrentDownEvent.getX(1));
					final int deltaY1 = (int) (y1 - mCurrentDownEvent.getY(1));
					
					int distance0 = (deltaX0 * deltaX0) + (deltaY0 * deltaY0);
					int distance1 = (deltaX1 * deltaX1) + (deltaY1 * deltaY1);
					
					if(distance0 > mTouchSlopSquare && distance1 > mTouchSlopSquare) {
						handled = this.mTwoPointScrollListener.onTwoPointScroll(mCurrentDownEvent, ev, scrollX0, scrollY0, scrollX1, scrollY1);
					}
					
					break;
			}
			
			return handled;
		}
		
		return super.onTouchEvent(ev);
	}
	
	public static interface TwoPointScrollListener {
		/**
		 * 
		 * @param e1 The Down MotionEvent
		 * @param e2 The most recent MotionEvent
		 * @param distanceX0 The distance changed on pointer 0.
		 * @param distanceY0 The distance changed on pointer 0.
		 * @param distanceX1 The distance changed on pointer 1.
		 * @param distanceY1 The distance changed on pointer 1.
		 * @return
		 */
		public boolean onTwoPointScroll(MotionEvent e1, MotionEvent e2, float distanceX0, float distanceY0, float distanceX1, float distanceY1);
	}
}