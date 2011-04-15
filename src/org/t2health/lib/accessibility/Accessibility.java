package org.t2health.lib.accessibility;

import android.content.Context;
import android.provider.Settings;

/**
 * Class for managing accessibility.
 * @author robbiev
 *
 */
public class Accessibility {
	/**
	 * Returns true if the system Accessibility setting is enabled.
	 * @param c
	 * @return
	 */
	public static boolean isSystemEnabled(Context c) {
		return Settings.Secure.getInt(c.getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED, 0) > 0;
	}
}
