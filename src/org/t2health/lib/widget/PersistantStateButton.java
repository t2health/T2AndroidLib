package org.t2health.lib.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.Checkable;

/**
 * Provides a button who's pressed state can persist through user interaction.
 * @author robbiev
 *
 */
public class PersistantStateButton extends Button implements Checkable{
	private boolean mIsChecked = false;

	public PersistantStateButton(Context context) {
		super(context);
	}

	public PersistantStateButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PersistantStateButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void setChecked(boolean isChecked) {
		this.mIsChecked = isChecked;
		this.refreshDrawableState();
	}

	@Override
	public boolean isChecked() {
		return mIsChecked;
	}
	
	@Override
	public void toggle() {
		setChecked(!this.mIsChecked);
	}

	@Override
	protected int[] onCreateDrawableState(int extraSpace) {
		int[] states;

		if(this.isChecked()) {
			states = Button.PRESSED_WINDOW_FOCUSED_STATE_SET;
		} else {
			states = super.onCreateDrawableState(extraSpace);
		}

		return states;
	}
}
