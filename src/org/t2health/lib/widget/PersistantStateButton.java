/*
 * 
 * T2AndroidLib
 * 
 * Copyright © 2009-2012 United States Government as represented by 
 * the Chief Information Officer of the National Center for Telehealth 
 * and Technology. All Rights Reserved.
 * 
 * Copyright © 2009-2012 Contributors. All Rights Reserved. 
 * 
 * THIS OPEN SOURCE AGREEMENT ("AGREEMENT") DEFINES THE RIGHTS OF USE, 
 * REPRODUCTION, DISTRIBUTION, MODIFICATION AND REDISTRIBUTION OF CERTAIN 
 * COMPUTER SOFTWARE ORIGINALLY RELEASED BY THE UNITED STATES GOVERNMENT 
 * AS REPRESENTED BY THE GOVERNMENT AGENCY LISTED BELOW ("GOVERNMENT AGENCY"). 
 * THE UNITED STATES GOVERNMENT, AS REPRESENTED BY GOVERNMENT AGENCY, IS AN 
 * INTENDED THIRD-PARTY BENEFICIARY OF ALL SUBSEQUENT DISTRIBUTIONS OR 
 * REDISTRIBUTIONS OF THE SUBJECT SOFTWARE. ANYONE WHO USES, REPRODUCES, 
 * DISTRIBUTES, MODIFIES OR REDISTRIBUTES THE SUBJECT SOFTWARE, AS DEFINED 
 * HEREIN, OR ANY PART THEREOF, IS, BY THAT ACTION, ACCEPTING IN FULL THE 
 * RESPONSIBILITIES AND OBLIGATIONS CONTAINED IN THIS AGREEMENT.
 * 
 * Government Agency: The National Center for Telehealth and Technology
 * Government Agency Original Software Designation: T2AndroidLib001
 * Government Agency Original Software Title: T2AndroidLib
 * User Registration Requested. Please send email 
 * with your contact information to: robert.kayl2@us.army.mil
 * Government Agency Point of Contact for Original Software: robert.kayl2@us.army.mil
 * 
 */
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
