package org.t2health.lib.widget;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

/**
 * A class to create sectioned lists.
 * @see http://jsharkey.org/blog/2008/08/18/separating-lists-with-headers-in-android-09/
 * @author robbiev
 *
 */
public class SeparatedListAdapter extends BaseAdapter {
	public final Map<String,Adapter> sections = new LinkedHashMap<String,Adapter>();
	public final ArrayAdapter<String> headers;
	public final static int TYPE_SECTION_HEADER = 0;
	public final Map<Adapter,Boolean> enabledSection = new HashMap<Adapter,Boolean>();

	/**
	 * Initialize the adapter
	 * @param context
	 * @param textViewLayout	The layout to use as the header. The layout 
	 * 							file should be an instance of 
	 * 							TextView widget. Otherwise you will have 
	 * 							problems.
	 */
	public SeparatedListAdapter(Context context, int textViewLayout) {
		headers = new ArrayAdapter<String>(context, textViewLayout);
	}

	/**
	 * Add a section
	 * @param section	The title of the section (as it would appear on the
	 * 					screen).
	 * @param adapter	The adapter to add
	 */
	public void addSection(String section, Adapter adapter) {
		this.addSection(section, adapter, true);
	}
	
	public void addSection(String section, Adapter adapter, boolean enabled) {
		this.headers.add(section);
		this.sections.put(section, adapter);
		this.enabledSection.put(adapter, enabled);
	}
	
	/**
	 * Returns the adapter of an item's position.
	 * @param position	the position of the item. Keep in mind, the position
	 * 					includes the existance of the header row.
	 * @return	The adapter for the item.
	 */
	public Adapter getAdapterForItem(int position) {
		for(Object section : this.sections.keySet()) {
			Adapter adapter = sections.get(section);
			int size = adapter.getCount() + 1;

			// check if position inside this section
			if(position < size) return adapter;

			// otherwise jump into next section
			position -= size;
		}
		return null;
	}
	
	/**
	 * Get an item from the adapter as a whole. Keep in mind, the position
	 * 	includes the existence of the header row.
	 */
	public Object getItem(int position) {
		for(Object section : this.sections.keySet()) {
			Adapter adapter = sections.get(section);
			int size = adapter.getCount() + 1;

			// check if position inside this section
			if(position == 0) return section;
			if(position < size) return adapter.getItem(position - 1);

			// otherwise jump into next section
			position -= size;
		}
		return null;
	}
	
	/**
	 * Get the total number of items across all sections plus 1 for each header.
	 */
	public int getCount() {
		// total together all sections, plus one for each section header
		int total = 0;
		for(Adapter adapter : this.sections.values())
			total += adapter.getCount() + 1;
		return total;
	}

	public int getViewTypeCount() {
		// assume that headers count as one, then total all sections
		int total = 1;
		for(Adapter adapter : this.sections.values())
			total += adapter.getViewTypeCount();
		return total;
	}

	/**
	 * Get the view type for the given position
	 * @param position	The position of the item
	 * @return	TYPE_SECTION_HEADER if the item is a header, -1 if it couldn't
	 * 			be determined..
	 */
	public int getItemViewType(int position) {
		int type = 1;
		for(Object section : this.sections.keySet()) {
			Adapter adapter = sections.get(section);
			int size = adapter.getCount() + 1;

			// check if position inside this section
			if(position == 0) return TYPE_SECTION_HEADER;
			if(position < size) return type + adapter.getItemViewType(position - 1);

			// otherwise jump into next section
			position -= size;
			type += adapter.getViewTypeCount();
		}
		return -1;
	}

	public boolean areAllItemsSelectable() {
		return false;
	}

	/**
	 * Determine is the specified position is enabled.
	 */
	public boolean isEnabled(int position) {
		if(getItemViewType(position) == TYPE_SECTION_HEADER) {
			return false;
		}
		
		Adapter a = getAdapterForItem(position);
		if(enabledSection.get(a)) {
			return true;
		}
		return false;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int sectionnum = 0;
		for(Object section : this.sections.keySet()) {
			Adapter adapter = sections.get(section);
			int size = adapter.getCount() + 1;

			// check if position inside this section
			if(position == 0) return headers.getView(sectionnum, convertView, parent);
			if(position < size) return adapter.getView(position - 1, convertView, parent);

			// otherwise jump into next section
			position -= size;
			sectionnum++;
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		for(Object section : this.sections.keySet()) {
			Adapter adapter = sections.get(section);
			int size = adapter.getCount() + 1;

			// check if position inside this section
			if(position == 0) return -1;
			if(position < size) return adapter.getItemId(position - 1);

			// otherwise jump into next section
			position -= size;
		}
		return -1;
	}

}
