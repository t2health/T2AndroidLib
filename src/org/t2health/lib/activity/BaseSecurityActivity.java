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
package org.t2health.lib.activity;

import org.t2health.lib.IntentFactory;
import org.t2health.lib.ManifestMetaData;
import org.t2health.lib.SharedPref;
import org.t2health.lib.security.AppSecurityManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class BaseSecurityActivity extends BaseActivity {
	private boolean mIsEnabled = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// configure the security manager.
		this.mIsEnabled = ManifestMetaData.SecurityManager.isEnabled(this) && 
							this.isSecurityEnabled() &&
							SharedPref.Security.isEnabled(this);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(requestCode == AppSecurityManager.UNLOCK_ACTIVITY) {
			if(resultCode == Activity.RESULT_OK) {
				AppSecurityManager.setIsUnlocked(true);
			} else {
				AppSecurityManager.setIsUnlocked(false);
				this.finish();
			}
		}
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		AppSecurityManager.onWindowFocusChanged(hasFocus);
		super.onWindowFocusChanged(hasFocus);
	}

	@Override
	protected void onResume() {
		if(!AppSecurityManager.isUnlocked() && this.mIsEnabled) {
			AppSecurityManager.startUnlockActivity(this, getSecurityUnlockIntent());
		}
		
		AppSecurityManager.onResume();
		super.onResume();
	}

	@Override
	protected void onPause() {
		AppSecurityManager.onPause();
		super.onPause();
	}
	
	protected Intent getSecurityUnlockIntent() {
		return IntentFactory.Security.getUnlockIntent(this);
	}
	
	protected boolean isSecurityEnabled() {
		return true;
	}
}
