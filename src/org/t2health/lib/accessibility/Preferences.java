package org.t2health.lib.accessibility;

import org.t2health.lib.R;
import org.t2health.lib.preference.BasePreferenceNavigationActivity;

import android.os.Bundle;

public class Preferences extends BasePreferenceNavigationActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.addPreferencesFromResource(R.xml.accessibility_pref);
	}

}
