package org.t2health.lib.preference;

import org.t2health.lib.IntentFactory;
import org.t2health.lib.ManifestMetaData;
import org.t2health.lib.SharedPref;
import org.t2health.lib.security.AppSecurityManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public abstract class BasePreferenceSecurityActivity extends BasePreferenceActivity {
	private boolean isEnabled = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// configure the security manager.
		this.isEnabled = ManifestMetaData.SecurityManager.isEnabled(this) && 
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
		if(!AppSecurityManager.isUnlocked() && this.isEnabled) {
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
