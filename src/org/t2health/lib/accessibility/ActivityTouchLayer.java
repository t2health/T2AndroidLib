package org.t2health.lib.accessibility;

import java.util.ArrayList;
import java.util.List;

import org.t2health.lib.R;
import org.t2health.lib.view.T2GestureDetector;
import org.t2health.lib.view.T2GestureDetector.TwoPointScrollListener;

import android.content.Context;
import android.graphics.Canvas;
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
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.AbsSeekBar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.AbsSpinner;

class ActivityTouchLayer extends View implements OnGestureListener, TwoPointScrollListener, OnDoubleTapListener {
	private static final String TAG = ActivityTouchLayer.class.getSimpleName();
	private boolean mIsBaseViewInitilized = false;
	private boolean mIsEnabled = false;
	private boolean mOnFirstDrawRan = false;
	private View mBaseView = null;
	private T2GestureDetector mGestureDetector;
	private ArrayList<ViewCache> viewCache = new ArrayList<ViewCache>();
	
	public ActivityTouchLayer(Context context) {
		super(context);
		init();
	}
	
	public ActivityTouchLayer(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ActivityTouchLayer(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private void init() {
		mGestureDetector = new T2GestureDetector(this.getContext(), this);
		mGestureDetector.setIsLongpressEnabled(true);
		mGestureDetector.setOnTwoPointScrollListener(this);
		mGestureDetector.setOnDoubleTapListener(this);
	}
	
	private void initializeBaseView() {
		if(mIsBaseViewInitilized) {
			return;
		}
		mIsBaseViewInitilized = true;
		
		// Attempt to find the base view for the activity.
		ViewParent baseParent = this.getParent();
		ViewParent tmpParent = null;
		while(true) {
			tmpParent = baseParent.getParent();
			if(tmpParent == null) {
				break;
			} else if(tmpParent instanceof View){
				baseParent = tmpParent;
			} else {
				break;
			}
		}
		
		// Find the content area for the base view.
		try {
			mBaseView = ((View)baseParent).findViewById(R.id.accessibilityActivityContent);
			this.mIsEnabled = true;
		} catch (ClassCastException e) {
			throw new RuntimeException("Could not cast the activity's base parent to a view."+ baseParent.toString());
		} catch (NullPointerException e) {
			throw new RuntimeException("Could not find element with id of accessibilityActivityContent defined in accessibility_layout.");
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		if(!mOnFirstDrawRan) {
			mOnFirstDrawRan = true;
			initializeBaseView();
			rebuildViewCache(mBaseView);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// Accessibility could not be enabled, don't try to do any work.
		if(!this.mIsEnabled) {
			return super.onTouchEvent(event);
		}
		
		//Log.v(TAG, "touch:"+event.getX()+","+event.getY()); return true;
		// get the views that contain the current coords.
		//ArrayList<View> views = getVisibleViewsAt(mBaseView, (int)event.getX(), (int)event.getY());
		//Log.v(TAG, "ViewCount:"+views.size());
		
		/* find children at x,y that have the following charateristics
		 * 	has a contentDescription
		 * 	has text (TextView,Button)
		 *  is an interactive widget (ListView,ScrollView,Button,EditText)
		 *  has an onListener set.
		 */
		
		/*
		 * Notes:
		 * Allow scroll via two fingers.
		 * Hover or Single click will select item. (item text, context desc should be spoken)
		 * Double click will activate the item (click the item).
		 * 
		 * For listviews, speak the number of rows visible on scroll. (eg. rows 1 to 9 of 17)
		 * 
		 */
		
		return mGestureDetector.onTouchEvent(event);
	}
	
	private ArrayList<View> getVisibleViewsAt(View startView, int x, int y) {
		ArrayList<View> output = new ArrayList<View>();
		
		for(ViewCache c: viewCache) {
			if(c.rect.contains(x, y) && c.visibility == View.VISIBLE) {
				output.add(c.view);
			}
		}
		
		return output;
	}
	
	private void rebuildViewCache(View baseView) {
		ArrayList<ViewCache> newCache = new ArrayList<ViewCache>();
		ArrayList<View> children = new ArrayList<View>();
		int childrenIndex = 0;
		View currentChild = null;
		int[] currentXY = new int[2];
		children.add(baseView);
		
		ViewCache tmpCache;
		while(childrenIndex < children.size()) {
			tmpCache = new ViewCache();
			
			currentChild = children.get(childrenIndex);
			currentChild.getLocationInWindow(currentXY);
			
			tmpCache.view = currentChild;
			tmpCache.visibility = currentChild.getVisibility();
			tmpCache.rect = new Rect(
					currentXY[0], 
					currentXY[1],
					currentXY[0]+currentChild.getWidth(),
					currentXY[1]+currentChild.getHeight()
			);
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
	
	private View getFirstInstanceOf(ArrayList<View> views, Class<? extends View>[] classes) {
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
	
	@Override
	public boolean onDown(MotionEvent e) {
//		Log.v(TAG, "onDown");
		return true;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
//		Log.v(TAG, "onFling");
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		ArrayList<View> views = getVisibleViewsAt(mBaseView, (int)e.getX(), (int)e.getY());
		View firstInstance = getFirstInstanceOf(
				views, 
				new Class[]{Button.class}
		);
		
		if(firstInstance != null) {
			MotionEvent up = MotionEvent.obtain(e);
			up.setAction(MotionEvent.ACTION_UP);
			
			firstInstance.onTouchEvent(e);
			//firstInstance.onTouchEvent(up);
		}
		
		Log.v(TAG, "onLongPress");
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
//		Log.v(TAG, "onScroll");
		return true;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		Log.v(TAG, "onShowPress");
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		Log.v(TAG, "onSingleTapUp");
		return true;
	}

	@Override
	public boolean onTwoPointScroll(MotionEvent e1, MotionEvent e2,
			float distanceX0, float distanceY0, float distanceX1,
			float distanceY1) {
		Log.v(TAG, "onTwoPointScroll");
		
		ArrayList<View> views = getVisibleViewsAt(mBaseView, (int)e2.getX(), (int)e2.getY());
		View firstInstance = getFirstInstanceOf(
				views, 
				new Class[]{AbsListView.class, ScrollView.class, WebView.class}
		);
		
		if(firstInstance != null) {
			firstInstance.scrollBy((int)distanceX0, (int)distanceY0);
		}
		
		if(firstInstance instanceof AbsListView) {
			// rebuild the cache as new items may have appeared.
			rebuildViewCache(mBaseView);
		}
		
		return true;
	}

	@Override
	public boolean onDoubleTap(MotionEvent arg0) {
		Log.v(TAG, "onDoubleTap0");
		
		// Get the item.
		ArrayList<View> views = getVisibleViewsAt(mBaseView, (int)arg0.getX(), (int)arg0.getY());
		View firstInstance = getFirstInstanceOf(
				views, 
				new Class[] {ImageView.class, TextView.class, AbsSeekBar.class, AbsSpinner.class}
		);
		
		// press the item
		if(firstInstance != null) {
			MotionEvent down = MotionEvent.obtain(arg0);
			down.setAction(MotionEvent.ACTION_DOWN);
			
			MotionEvent up = MotionEvent.obtain(arg0);
			up.setAction(MotionEvent.ACTION_UP);
			
			firstInstance.onTouchEvent(down);
			firstInstance.onTouchEvent(up);
		}
		
		// rebuild the cache as new items may have appeared.
		rebuildViewCache(mBaseView);
		return true;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent arg0) {
		Log.v(TAG, "onDoubleTap1");
		return true;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent arg0) {
		Log.v(TAG, "onSingleTapConfirmed");
		return true;
	}
	
	
	private class ViewCache {
		public Rect rect;
		public int visibility;
		public View view;
	}
}
