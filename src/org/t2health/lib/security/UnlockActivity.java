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
package org.t2health.lib.security;

import org.t2health.lib.IntentFactory;
import org.t2health.lib.R;
import org.t2health.lib.SharedPref;
import org.t2health.lib.activity.BaseNavigationActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.EditText;

public class UnlockActivity extends BaseNavigationActivity implements OnKeyListener, OnClickListener {
	private static final int FORGOT_PIN_ACTIVITY = 235;
	private EditText mPinEditText;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.security_unlock_activity);
		this.setTitle(R.string.security_unlock_title);
		mPinEditText = (EditText)this.findViewById(R.id.passwordEditText);
		mPinEditText.setOnKeyListener(this);
		
		this.findViewById(R.id.forgotPasswordButton).setOnClickListener(this);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(requestCode == FORGOT_PIN_ACTIVITY && resultCode == RESULT_OK) {
			AppSecurityManager.setIsUnlocked(true);
			this.setResult(RESULT_OK);
			this.finish();
			
			// Start the configure activity so the password can be changed.
			startActivity(IntentFactory.Security.getConfigureIntent(this));
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			this.moveTaskToBack(true);
			return true;
		}
		
		if(keyCode == KeyEvent.KEYCODE_MENU) {
			return true;
		}
		
		return false;
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if(v.getId() == R.id.passwordEditText) {
			String enteredPin = mPinEditText.getText().toString().trim();
			if(SharedPref.Security.doesPasswordMatch(this, enteredPin)) {
				AppSecurityManager.setIsUnlocked(true);
				this.setResult(RESULT_OK);
				this.finish();
			}
		}
		return false;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}
	
	public void onClick(View v) {
		int viewId = v.getId();
		if(viewId == R.id.forgotPasswordButton) {
			this.startActivityForResult(
					IntentFactory.Security.getForgotPasswordIntent(this), 
					FORGOT_PIN_ACTIVITY
			);
		}
	}

	@Override
	protected boolean isSecurityEnabled() {
		return false;
	}
}
