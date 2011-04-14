package org.t2health.qa;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * A simple adapter to display an array of answers.
 * @author robbiev
 *
 */
public class AnswerAdapter extends ArrayAdapter<Answer> {
	public AnswerAdapter(Context context, int textViewResourceId,
			Answer[] objects) {
		super(context, textViewResourceId, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Answer ans = getItem(position);
		if(convertView == null) {
			convertView = super.getView(position, convertView, parent);
		}

		View titleView = convertView.findViewById(android.R.id.text1);
		View descView = convertView.findViewById(android.R.id.text2);

		if(titleView != null) {
			((TextView)titleView).setText(ans.title);
		}
		if(descView != null) {
			((TextView)descView).setText(ans.desc);
		}

		return convertView;
	}
}