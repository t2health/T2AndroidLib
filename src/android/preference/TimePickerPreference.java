package android.preference;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;
 
/**
 * A preference type that allows a user to choose a time
 * Taken from: http://www.ebessette.com/d/TimePickerPreference
 * Modified to add summary text and properly handle the dialog's ok and cancel
 * buttons.
 */
public class TimePickerPreference extends DialogPreference {
 
	/**
	 * The format to use for the summary content
	 */
	private static final SimpleDateFormat SUMMARY_FORMAT = new SimpleDateFormat("K:mm a");
	
	/**
	 * The validation expression for this preference
	 */
	private static final String VALIDATION_EXPRESSION = "[0-2]*[0-9]:[0-5]*[0-9]";
 
	/**
	 * The default value for this preference
	 */
	private String defaultValue;

	private TimePicker timePicker;
 
	/**
	 * @param context
	 * @param attrs
	 */
	public TimePickerPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize();
	}
 
	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public TimePickerPreference(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		initialize();
	}
 
	/**
	 * Initialize this preference
	 */
	private void initialize() {
		setPersistent(true);
	}
 
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.preference.DialogPreference#onCreateDialogView()
	 */
	@Override
	protected View onCreateDialogView() {
		timePicker = new TimePicker(getContext());
 
		int h = getHour();
		int m = getMinute();
		if (h >= 0 && m >= 0) {
			timePicker.setCurrentHour(h);
			timePicker.setCurrentMinute(m);
		}
 
		return timePicker;
	}
	
	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch(which) {
			case DialogInterface.BUTTON_POSITIVE:
				persistString(timePicker.getCurrentHour() + ":" + timePicker.getCurrentMinute());
				notifyChanged();
			break;
		}
		super.onClick(dialog, which);
	}
 
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.preference.Preference#setDefaultValue(java.lang.Object)
	 */
	@Override
	public void setDefaultValue(Object defaultValue) {
		// BUG this method is never called if you use the 'android:defaultValue' attribute in your XML preference file, not sure why it isn't		
 
		super.setDefaultValue(defaultValue);
 
		if (!(defaultValue instanceof String)) {
			return;
		}
 
		if (!((String) defaultValue).matches(VALIDATION_EXPRESSION)) {
			return;
		}
 
		this.defaultValue = (String) defaultValue;
	}
 
	/**
	 * Get the hour value (in 24 hour time)
	 * 
	 * @return The hour value, will be 0 to 23 (inclusive)
	 */
	private int getHour() {
		String time = getPersistedString(this.defaultValue);
		if (time == null || !time.matches(VALIDATION_EXPRESSION)) {
			return -1;
		}
 
		return Integer.valueOf(time.split(":")[0]);
	}
 
	/**
	 * Get the minute value
	 * 
	 * @return the minute value, will be 0 to 59 (inclusive)
	 */
	private int getMinute() {
		String time = getPersistedString(this.defaultValue);
		if (time == null || !time.matches(VALIDATION_EXPRESSION)) {
			return -1;
		}
 
		return Integer.valueOf(time.split(":")[1]);
	}

	@Override
	public CharSequence getSummary() {
		int hour = getHour();
		int minute = getMinute();
		
		if(hour < 0 || minute < 0) {
			return "";
		}
		
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR, hour);
		cal.set(Calendar.MINUTE, minute);
		return SUMMARY_FORMAT.format(cal.getTime());
	}
}