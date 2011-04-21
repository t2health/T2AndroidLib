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
