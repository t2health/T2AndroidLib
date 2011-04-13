package org.t2health.lib;

import android.app.Activity;
import android.content.Intent;

public class AppSecurityManager {
	public static final int UNLOCK_ACTIVITY = 9834;
	
	private static boolean isUnlocked = false;
	private static int statusCount = 0;
	private static boolean appHasFocus = false;
	
	public static void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == UNLOCK_ACTIVITY && resultCode == Activity.RESULT_OK) {
			isUnlocked = true;
		}
	}

	public static void onWindowFocusChanged(boolean hasFocus) {
		appHasFocus = hasFocus;
		
		if(hasFocus) {
			++statusCount;
		} else {
			--statusCount;
		}
		
		// If the app was completley shut off, force the unlock screen.
		if(statusCount == 0) {
			isUnlocked = false;
		}
	}
	
	public static void onResume() {
		++statusCount;
	}

	public static void onPause() {
		// An app has come over the top of this app. Lock the app.
		if(statusCount == 1 && !appHasFocus) {
			isUnlocked = false;
		}
		
		--statusCount;
	}
	
	public static void setIsUnlocked(boolean b) {
		isUnlocked = b;
	}
	
	public static boolean isUnlocked() {
		return isUnlocked;
	}
	
	public static void startUnlockActivity(Activity a, Intent i) {
		a.startActivityForResult(i, AppSecurityManager.UNLOCK_ACTIVITY);
	}
}
