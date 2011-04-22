package org.t2health.lib.accessibility;

import java.util.ArrayList;
import java.util.List;

import org.t2health.lib.R;
import org.t2health.lib.view.T2GestureDetector;
import org.t2health.lib.view.T2GestureDetector.TwoPointScrollListener;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.AbsSeekBar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.AbsSpinner;

public class ActivityTouchFrame extends FrameLayout implements OnGestureListener, OnDoubleTapListener {
	private static final String TAG = ActivityTouchFrame.class.getSimpleName();
	private boolean mIsBaseViewInitilized = false;
	private boolean mIsEnabled = false;
	private boolean mOnFirstDrawRan = false;
	private boolean mIsScrolling = false;
	private View mBaseView = null;
	private GestureDetector mGestureDetector;
	private ArrayList<ViewCache> viewCache = new ArrayList<ViewCache>();
	private Rect mHilightRect;
	private boolean mIsHilightRectVisible = false;
	private int mDrawYOffset;
	private AccessibilityManager mAccessibilityManager;
	private View mSelectedView;
	
	public ActivityTouchFrame(Context context) {
		super(context);
		init();
	}
	
	public ActivityTouchFrame(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ActivityTouchFrame(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private void init() {
		mGestureDetector = new GestureDetector(this.getContext(), this);
		mGestureDetector.setIsLongpressEnabled(true);
		mGestureDetector.setOnDoubleTapListener(this);
		
		mAccessibilityManager = (AccessibilityManager)this.getContext().getSystemService(Context.ACCESSIBILITY_SERVICE);
	}
	
	@Override
	public void draw(Canvas canvas) {
		//Log.v(TAG, "draw");
		super.draw(canvas);
		
		if(!mOnFirstDrawRan) {
			int[] pos = new int[2];
			this.getLocationOnScreen(pos);
			mDrawYOffset = pos[1]*-1;
			
			mOnFirstDrawRan = true;
			//initializeBaseView();
			rebuildViewCache();
		}
		//Log.v(TAG, "onDraw");
		if(mIsHilightRectVisible && mHilightRect != null) {
			//Log.v(TAG, "draw hilight "+mHilightRect);
			Paint paint = new Paint();
			paint.setStyle(Style.STROKE);
			paint.setStrokeWidth(5);
			paint.setColor(Color.YELLOW);
			canvas.drawRect(mHilightRect, paint);
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if(mIsScrolling && ev.getAction() == MotionEvent.ACTION_UP) {
			mIsScrolling = false;
			super.dispatchTouchEvent(ev);
			rebuildViewCache();
			return true;
		}
		
		if(ev.getAction() == MotionEvent.ACTION_UP) {
			hideHilightRect();
		}
		
		mGestureDetector.onTouchEvent(ev);
		
		// select the item and speak if necessary.
		if(ev.getAction() != MotionEvent.ACTION_UP) {
			ArrayList<View> views = getVisibleViewsAt((int)ev.getX(), (int)ev.getY());
			//Log.v(TAG, "!handled "+views.size());
			if(views != null && views.size() > 0) {
				View v = views.get(views.size()-1);
				if(v != mSelectedView) {
					mSelectedView = v;

					v.requestFocus();
					v.setSelected(true);
					
					setHilightRect(getScreenRect(v));
					showHilightRect();
					this.invalidate();
					
					setViewSelected(mSelectedView);
				}
			}
		}
		return true;
	}
	
	private void setViewSelected(View v) {
		//Log.v(TAG, "ENABLED:"+mAccessibilityManager.isEnabled());
		AccessibilityEvent ae = AccessibilityEvent.obtain();
		ae.setEventType(AccessibilityEvent.TYPE_VIEW_CLICKED);
		ae.setBeforeText("before");
		ae.setClassName(v.getClass().getSimpleName());
		ae.setEnabled(v.isEnabled());
		ae.setContentDescription(getContentDescription(v));
		
		mAccessibilityManager.sendAccessibilityEvent(ae);
		
		//v.sendAccessibilityEventUnchecked(ae);
		//Log.v(TAG, "show hilight");
	}
	
	private CharSequence getContentDescription(View v) {
		CharSequence desc = v.getContentDescription();
		if(desc != null && desc.length() > 0) {
			return desc;
		}
		
		if(v instanceof AbsSeekBar) {
			return "Progress is "+ ((AbsSeekBar)v).getProgress();
		}
		
		return null;
	}
	
	private void setHilightRect(Rect r) {
		mHilightRect = r;
		//mHilightRect.top += mDrawYOffset;
		//mHilightRect.bottom += mDrawYOffset;
	}
	
	private void showHilightRect() {
		mIsHilightRectVisible  = true;
	}
	
	private void hideHilightRect() {
		mIsHilightRectVisible = false;
	}

	private ArrayList<View> getVisibleViewsAt(int x, int y) {
		ArrayList<View> output = new ArrayList<View>();
		
		for(ViewCache c: viewCache) {
			if(c.rect.contains(x, y) && c.visibility == View.VISIBLE) {
				output.add(c.view);
			}
		}
		
		return output;
	}
	
	private Rect getScreenRect(View v) {
		int[] currentXY = new int[2];
		v.getLocationOnScreen(currentXY);
		
		return new Rect(
				currentXY[0], 
				currentXY[1] + mDrawYOffset,
				currentXY[0]+v.getWidth(),
				currentXY[1]+v.getHeight() + mDrawYOffset
		);
	}
	
	private void rebuildViewCache() {
		ArrayList<ViewCache> newCache = new ArrayList<ViewCache>();
		ArrayList<View> children = new ArrayList<View>();
		int childrenIndex = 0;
		View currentChild = null;
		children.addAll(getChildViews(this));
		
		ViewCache tmpCache;
		while(childrenIndex < children.size()) {
			tmpCache = new ViewCache();
			
			currentChild = children.get(childrenIndex);
			
			tmpCache.view = currentChild;
			tmpCache.visibility = currentChild.getVisibility();
			tmpCache.rect = getScreenRect(currentChild);
			//Log.v(TAG, "CACHE:"+currentChild +"\t"+tmpCache.rect.left+","+tmpCache.rect.top+","+tmpCache.rect.right+","+tmpCache.rect.bottom);
			newCache.add(tmpCache);
			
			// Add this child's children so they can be indexed.
			if(currentChild instanceof ViewGroup) {
				children.addAll(getChildViews((ViewGroup)currentChild));
			}
			
			++childrenIndex;
		}

		viewCache.clear();
		viewCache.addAll(newCache);
	}
	
	private List<View> getChildViews(ViewGroup v) {
		ArrayList<View> childViews = new ArrayList<View>();
		for(int i = 0; i < v.getChildCount(); ++i) {
			childViews.add(v.getChildAt(i));
		}
		return childViews;
	}
	
	/*private View getFirstInstanceOf(ArrayList<View> views, Class<? extends View>[] classes) {
		View currentView = null;
		Class<?> currentClass = null;
		for(int i = views.size()-1; i >= 0; --i) {
			currentView = views.get(i);
			currentClass = currentView.getClass();
			//Log.v(TAG, "  CLS:"+currentView);
			
			while(true) {
				if(currentClass == null) {
					break;
				}
				for(Class c: classes) {
					if(currentClass.equals(c)) {
						return currentView;
					}	
				}
				
				currentClass = currentClass.getSuperclass();
			}
		}
		return null;
	}
	
	*/
	
	
	private class ViewCache {
		public Rect rect;
		public int visibility;
		public View view;
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		rebuildViewCache();
		return false;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {
		return super.dispatchTouchEvent(e);
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		if(e2.getPointerCount() > 1) {
			if(!mIsScrolling) {
				MotionEvent down = MotionEvent.obtain(e1);
				down.setAction(MotionEvent.ACTION_DOWN);
				super.dispatchTouchEvent(down);
			}
			mIsScrolling = true;
			
			return super.dispatchTouchEvent(e2);
		}
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}
}
