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

import android.app.Activity;
import android.content.Intent;

public class AppSecurityManager {
	public static final int UNLOCK_ACTIVITY = 9834;
	
	private static boolean sIsUnlocked = false;
	private static int sStatusCount = 0;
	private static boolean sAppHasFocus = false;
	
	public static void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == UNLOCK_ACTIVITY && resultCode == Activity.RESULT_OK) {
			sIsUnlocked = true;
		}
	}

	public static void onWindowFocusChanged(boolean hasFocus) {
		sAppHasFocus = hasFocus;
		
		if(hasFocus) {
			++sStatusCount;
		} else {
			--sStatusCount;
		}
		
		// If the app was completley shut off, force the unlock screen.
		if(sStatusCount == 0) {
			sIsUnlocked = false;
		}
	}
	
	public static void onResume() {
		++sStatusCount;
	}

	public static void onPause() {
		// An app has come over the top of this app. Lock the app.
		if(sStatusCount == 1 && !sAppHasFocus) {
			sIsUnlocked = false;
		}
		
		--sStatusCount;
	}
	
	public static void setIsUnlocked(boolean b) {
		sIsUnlocked = b;
	}
	
	public static boolean isUnlocked() {
		return sIsUnlocked;
	}
	
	public static void startUnlockActivity(Activity a, Intent i) {
		a.startActivityForResult(i, AppSecurityManager.UNLOCK_ACTIVITY);
	}
}
