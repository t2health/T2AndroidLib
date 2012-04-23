package org.t2health.lib.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

/**
 * Provides a list-based hierarchical structure of content based on an XML file.
 * located in res/xml.
 * <items title="Title of activity" style="separated|normal">
 * 		<item id="item1" title="section1">
 * 			<item title="Section 1 item 1"><![CDATA[
 * 				HTML Content goes here
 * 			]]></item>
 * 			<item title="Section 1 item 2">
 *				<item title="another level"><![CDATA[
 * 					HTML Content goes here
 * 				]]></item>
 * 				<item title="another level again"><![CDATA[
 *					HTML Content goes here
 * 				]]></item>
 *			</item>
 * 		</item>
 * </items>
 * 
 * Any item can have multiple children. Each item can have the following tags.
 * <ul>
 * 	<li>title - The title of the item as it will be displayed in the list. (necessary)</li>
 * 	<li>id - a unique id used for quick linking to the specific item. (optional)</li>
 * 	<li>uri - load this uri when the item is clicked (optional)</li>
 * </ul>
 * 
 * Note: In order for the HTML content of an item to load, 
 * org.t2health.lib.activity.util.WebViewActivity
 * needs be be listed in your manifest file as an activity.
 * @author robbiev
 *
 * @TODO Make this activity take a URL too. Then use a conditional GET to
 * get the updated content. Cache the new content some place.
 */
public class XMLItemsBrowserActivity extends BaseNavigationActivity implements OnItemClickListener {
	/**
	 * The resource id of the XML file to parse (required).
	 */
	public static final String EXTRA_XML_RESOURCE = "xmlResId";
	
	/**
	 * Collect all the items start from the element with this id.
	 */
	public static final String EXTRA_START_ID = "startId";
	
	/**
	 * The layout resource id to use as the separator header in the list.
	 * This only visible in the separated style.
	 */
	public static final String EXTRA_LIST_SEPARATOR_RES_ID = "separatorResId";
	
	/**
	 * The layout resource id to use as an item in the list.
	 */
	public static final String EXTRA_LIST_ITEM_RES_ID = "listItemResId";

	/**
	 * Specifies whether or not the separator of a list is clickable. If true,
	 * clicking the separator will load a list of items for that separator.
	 */
	public static final String EXTRA_LIST_SEPARATOR_CLICKABLE = "separatorClickable";
	
	private static final String STYLE_NORMAL = "normal";
	private static final String STYLE_SEPARATED = "separated";
	private static final String BASE_ITEM_ID = "baseItemId";

	private static final String XML_ITEMS_TAG = "items";
	private static final String XML_ITEM_TAG = "item";
	private static final String XML_ID_ATTRIBUTE = "id";
	private static final String XML_TITLE_ATTRIBUTE = "title";
	private static final String XML_STYLE_ATTRIBUTE = "style";
	private static final String XML_URI_ATTRIBUTE = "uri";

	protected static final String LIST_ITEM_TITLE = "title";
	protected static final String LIST_ITEM_ID = "id";

	private boolean isClickableSeperatorEnabled = false;
	private int mXmlResource = -1;

	private int mSeperatorResId = -1;
	private String[] mSeperatorFrom;
	private int[] mSeperatorTo;

	private int mItemResId = -1;
	private String[] mItemFrom;
	private int[] mItemTo;
	protected LinkedHashMap<String, Item> mItemsMap;

	private SimpleSeperatorAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = this.getIntent();
		
		this.mSeperatorResId = intent.getIntExtra(EXTRA_LIST_SEPARATOR_RES_ID, this.getHeaderLayoutResId());
		this.mItemResId = intent.getIntExtra(EXTRA_LIST_ITEM_RES_ID, this.getItemLayoutResId());
		this.isClickableSeperatorEnabled = intent.getBooleanExtra(EXTRA_LIST_SEPARATOR_CLICKABLE, false);
		
		this.setSeparatorResource(
				this.mSeperatorResId,
				new String[]{
						LIST_ITEM_TITLE,
				},
				new int[] {
						android.R.id.text1,
				}
		);
		this.setItemResource(
				this.mItemResId,
				new String[]{
						LIST_ITEM_TITLE,
				},
				new int[] {
						android.R.id.text1,
				}
		);

		// if this hasn't been set by overriding class
		if(!this.isXMLResourceSet()) {
			// try to pull it from the intent.
			this.setXMLResource(intent.getIntExtra(EXTRA_XML_RESOURCE, -1));
			// still isn't set, close the activity.
			if(!this.isXMLResourceSet()) {
				this.finish();
				return;
			}
		}

		String baseXMLItemId = intent.getStringExtra(EXTRA_START_ID);
		if(baseXMLItemId == null || baseXMLItemId.length() == 0) {
			baseXMLItemId = BASE_ITEM_ID;
		}

		this.mItemsMap = this.loadItemsFromXML(this.mXmlResource, baseXMLItemId);
		Item baseItem = this.mItemsMap.get(baseXMLItemId);

		if(baseItem == null) {
			return;
		}
		
		if(baseItem.hasItems(this.mItemsMap)) {
			ListView listView = new ListView(this);
			listView.setScrollingCacheEnabled(false);
			listView.setCacheColorHint(Color.TRANSPARENT);
			if(baseItem.style.equals(STYLE_SEPARATED)) {
				this.adapter = getSeparatedAdapter(baseItem);
			} else {
				this.adapter = getSimpleAdapter(baseItem);
			}
			listView.setAdapter(this.adapter);
			listView.setOnItemClickListener(this);
			
			this.setContentView(
					listView,
					new LayoutParams(
						LayoutParams.FILL_PARENT,
						LayoutParams.FILL_PARENT
					)
			);
			this.setTitle(baseItem.title);
		} else {
			this.onItemClick(baseItem, false);
			this.finish();
			return;
		}
	}

	/**
	 * Get the layout id to be used for header items. Used only for separted list styles.
	 * @return
	 */
	protected int getHeaderLayoutResId() {
		return android.R.layout.simple_list_item_1;
	}

	/**
	 * The layout id to be used for list items.
	 * @return
	 */
	protected int getItemLayoutResId() {
		return android.R.layout.simple_list_item_1;
	}

	/**
	 * Specifies is the xmlResource file was set.
	 * @return
	 */
	public boolean isXMLResourceSet() {
		return this.mXmlResource != -1;
	}
	
	/**
	 * Set the xmlResource file to this file. This file should be in res/xml.
	 * @param xmlRes
	 */
	public void setXMLResource(int xmlRes) {
		this.mXmlResource = xmlRes;
	}
	
	/**
	 * Get the resource id of the current file.
	 * @return
	 */
	public int getXMLResource() {
		return this.mXmlResource;
	}

	/**
	 * Specifies if the separator resource is all set.
	 * @return
	 */
	public boolean isSeparatorResourceSet() {
		return this.mSeperatorResId != -1 && this.mSeperatorFrom != null && this.mSeperatorTo != null;
	}

	/**
	 * Provides the field mappings for the separator. This is the same syntax 
	 * used when initializing a SimpleAdapter.
	 * @param layoutId
	 * @param from
	 * @param to
	 */
	public void setSeparatorResource(int layoutId, String[] from, int[] to) {
		this.mSeperatorResId = layoutId;
		this.mSeperatorFrom = from;
		this.mSeperatorTo = to;
	}

	/**
	 * Specifies if the item resource is all set.
	 * @return
	 */
	public boolean isItemResourceSet() {
		return this.mItemResId != -1 && this.mItemFrom != null && this.mItemTo != null;
	}

	/**
	 * Provides the field mappings for the items. This is the same syntax 
	 * used when initializing a SimpleAdapter.
	 * @param layoutId
	 * @param from
	 * @param to
	 */
	public void setItemResource(int layoutId, String[] from, int[] to) {
		this.mItemResId = layoutId;
		this.mItemFrom = from;
		this.mItemTo = to;
	}

	/**
	 * Iterates through the xml file and builds the data structure.
	 * @param xmlResourceId
	 * @param startElementId
	 * @return
	 */
	private LinkedHashMap<String,Item> loadItemsFromXML(int xmlResourceId, String startElementId) {
		LinkedHashMap<String,Item> items = new LinkedHashMap<String,Item>();
		Stack<Item> itemStack = new Stack<Item>();
		
		// build the list of sections
		try {
			int tagNum = -1;
			String currentTag = null;
			XmlResourceParser parser = this.getResources().getXml(xmlResourceId);
			int eventType = parser.getEventType();
			int startElementDepth = -1;
			int currentDepth = -1;
			boolean isItemsTag = false;
			boolean isItemTag = false;
			boolean inStartElement = (startElementId == null || startElementId.length() == 0)?true:false;
			Item parentItem = null;
			
			// Iterate through each tag in the XML.
			while(eventType != XmlPullParser.END_DOCUMENT) {
				parentItem = null;
				if(itemStack.size() > 0) {
					parentItem = itemStack.peek();
				}
				currentTag = parser.getName();
				currentDepth = parser.getDepth();
				
				isItemsTag = false;
				isItemTag = false;
				if(eventType == XmlPullParser.START_TAG || eventType == XmlPullParser.END_TAG) {
					isItemsTag = currentTag.equals(XML_ITEMS_TAG);
					isItemTag = currentTag.equals(XML_ITEM_TAG);
				}
				++tagNum;

				// If a start tag was found.
				if(eventType == XmlPullParser.START_TAG) {
					
					// if the tag is an "item" tag or 
					if(isItemTag || isItemsTag) {
						Item currentItem = new Item(
							parser.getAttributeValue(null, XML_ID_ATTRIBUTE),
							parser.getAttributeValue(null, XML_TITLE_ATTRIBUTE),
							(parentItem == null)?BASE_ITEM_ID:parentItem.id
						);
						
						// Set item attributes
						String destUriStr = parser.getAttributeValue(null, XML_URI_ATTRIBUTE);
						if(destUriStr != null) {
							currentItem.destUri = Uri.parse(destUriStr);
						}
	
						if(isItemsTag) {
							currentItem.id = BASE_ITEM_ID;
							currentItem.parentId = null;
						} else {
							// generate an id for the item if it doens't exist.
							if(currentItem.id == null || currentItem.id.length() == 0) {
								currentItem.id = "genid-"+tagNum;
							}
						}
						
						// set the style for the item.
						String style = parser.getAttributeValue(null, XML_STYLE_ATTRIBUTE);
						if(style == null || style.length() == 0) {
							style = STYLE_NORMAL;
						}
						if(!style.equals(STYLE_SEPARATED)) {
							style = STYLE_NORMAL;
						}
						currentItem.style = style;
						currentItem.attributes = getAttributes(parser);

						/** 
						 * Check if this item is the startElementId we'd 
						 * like to parse from.
						 */
						if(currentItem.id.equals(startElementId)) {
							startElementDepth = parser.getDepth();
							inStartElement = true;
						}
						
						/** 
						 * Add this item to the list if we have found the start
						 * element and we are within 4 levels of the start element.
						 */
						if(inStartElement && startElementDepth > currentDepth - 4) {
							items.put(currentItem.id, currentItem);
						}
						
						/**
						 *  add this item to the stack, it may be the parent of
						 *  other items.
						 */
						if(!parser.isEmptyElementTag()) {
							itemStack.push(currentItem);
						}
					}
						
				} else if(eventType == XmlPullParser.TEXT) {
					if(parentItem != null) {
						parentItem.content = parser.getText();
					}
					
				} else if(eventType == XmlPullParser.END_TAG) {
					if(currentTag.equals(XML_ITEM_TAG) || isItemsTag) {
						Item poppedItem = itemStack.pop();
						
						// if the popped item is the baseId, then stop parsing.
						if(poppedItem.id.equals(startElementId)) {
							break;
						}
					}
				}

				eventType = parser.next();
			}
			
			parser.close();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return items;
	}

	/**
	 * Get extra attributes from the xml element.
	 * @param parser
	 * @return
	 */
	private HashMap<String,String> getAttributes(XmlPullParser parser) {
		HashMap<String,String> attributes = new HashMap<String,String>();
		for(int i = 0; i < parser.getAttributeCount(); ++i) {
			attributes.put(
					parser.getAttributeName(i),
					parser.getAttributeValue(i)
			);
		}
		return attributes;
	}

	private SimpleSeperatorAdapter getSimpleAdapter(Item baseItem) {
		// build the adapter.
		return new SimpleSeperatorAdapter(
				this,
				buildItemsHashList(baseItem.getItems(mItemsMap)),
				this.mItemResId,
				this.mItemFrom,
				this.mItemTo,
				this.mSeperatorResId,
				this.mSeperatorFrom,
				this.mSeperatorTo
		);
	}

	private SimpleSeperatorAdapter getSeparatedAdapter(Item baseItem) {
		// build the adapter.
		ArrayList<HashMap<String,Object>> items = new ArrayList<HashMap<String,Object>>();

		ArrayList<Item> children = baseItem.getItems(mItemsMap);
		for(int i = 0; i < children.size(); ++i) {
			Item item = children.get(i);

			// add the header item.
			if(item.hasItems(this.mItemsMap)) {
				HashMap<String,Object> hashItem = item.buildHashItem();
				hashItem.put(SimpleSeperatorAdapter.IS_SEPERATOR_ITEM_KEY, true);
				hashItem.put(SimpleSeperatorAdapter.IS_ENABLED_ITEM_KEY, false);
				items.add(hashItem);
				items.addAll(buildItemsHashList(item.getItems(this.mItemsMap)));
			} else {
				items.add(item.buildHashItem());
			}
		}

		return new SimpleSeperatorAdapter(
				this,
				items,
				this.mItemResId,
				this.mItemFrom,
				this.mItemTo,
				this.mSeperatorResId,
				this.mSeperatorFrom,
				this.mSeperatorTo
		);
	}

	private ArrayList<HashMap<String,Object>> buildItemsHashList(ArrayList<Item> itemsIn) {
		ArrayList<HashMap<String,Object>> items = new ArrayList<HashMap<String,Object>>();
		for(int i = 0; i < itemsIn.size(); ++i) {
			Item childItem = itemsIn.get(i);
			items.add(childItem.buildHashItem());
		}
		return items;
	}

	@Override
	public final void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		BaseAdapter adapter = (BaseAdapter)arg0.getAdapter();
		@SuppressWarnings("unchecked")
		HashMap<String,Object> itemMap = (HashMap<String, Object>) adapter.getItem(arg2);
		String id = (String) itemMap.get(LIST_ITEM_ID);
		onItemClick(mItemsMap.get(id), this.adapter.isSeperator(arg2));
	}

	private void onItemClick(Item item, boolean isSeperator) {
		// this item has a url, load the url.
		if(item.destUri != null) {
			Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
			intent.setData(item.destUri);
			startActivity(intent);

		// this item is a list of more items, show them.
		} else if((!isSeperator && item.hasItems(this.mItemsMap)) || (isSeperator && isClickableSeperatorEnabled && item.hasItems(this.mItemsMap))) {
			Intent intent = new Intent(this, this.getClass());
			intent.putExtra(XMLItemsBrowserActivity.EXTRA_XML_RESOURCE, this.mXmlResource);
			intent.putExtra(XMLItemsBrowserActivity.EXTRA_START_ID, item.id);
			intent.putExtra(XMLItemsBrowserActivity.EXTRA_LIST_SEPARATOR_RES_ID, this.mSeperatorResId);
			intent.putExtra(XMLItemsBrowserActivity.EXTRA_LIST_ITEM_RES_ID, this.mItemResId);
			intent.putExtra(XMLItemsBrowserActivity.EXTRA_GESTURES_ENABLED, this.getIsGesturesEnabled());
			intent.putExtra(XMLItemsBrowserActivity.EXTRA_LEFT_BUTTON_VISIBILITY, View.VISIBLE);
			this.startActivity(intent);
			
		// this is content, show the content in a webview.
		} else if(item.hasContent()){
			Intent intent = new Intent(this, WebViewActivity.class);
			intent.putExtra(WebViewActivity.EXTRA_CONTENT, item.content);
			intent.putExtra(WebViewActivity.EXTRA_TITLE_TEXT, item.title);
			intent.putExtra(WebViewActivity.EXTRA_GESTURES_ENABLED, this.getIsGesturesEnabled());
			this.startActivity(intent);
		}
	}

	public class Item {
		public String id;
		public String title;
		public String content;
		public String parentId;
		public String style;
		public Uri destUri;
		private HashMap<String, String> attributes = new HashMap<String,String>();

		public Item(String id, String title, String parentId) {
			this.id = id;
			this.title = title;
			this.parentId = parentId;
		}

		public Item(String id, String title, HashMap<String,String> atts) {
			this.id = id;
			this.title = title;
			this.attributes  = atts;
		}
		
		public boolean hasContent() {
			return content != null && content.length() > 0;
		}

		public boolean hasItems(LinkedHashMap<String,Item> items) {
			for(Item item: items.values()) {
				if(item.parentId != null && item.parentId.equals(id)) {
					return true;
				}
			}
			return false;
		}

		public ArrayList<Item> getItems(LinkedHashMap<String,Item> items) {
			ArrayList<Item> itemsOut = new ArrayList<Item>();

			for(Item item: items.values()) {
				if(item.parentId != null && item.parentId.equals(this.id)) {
					itemsOut.add(item);
				}
			}

			return itemsOut;
		}

		public HashMap<String,Object> buildHashItem() {
			HashMap<String,Object> item = new HashMap<String,Object>();
			for(String key: attributes.keySet()) {
				item.put(key, attributes.get(key));
			}
			item.put(LIST_ITEM_ID, id);
			item.put(LIST_ITEM_TITLE, title);
			return item;
		}
	}

	private class SimpleSeperatorAdapter extends SimpleAdapter {
		public static final String IS_SEPERATOR_ITEM_KEY = "isHeaderItem";
		public static final String IS_ENABLED_ITEM_KEY = "isEnabled";
		private ArrayList<HashMap<String,Object>> seperators = new ArrayList<HashMap<String,Object>>();
		private SimpleAdapter seperatorAdapter;

		public SimpleSeperatorAdapter(Context context,
				List<? extends Map<String, Object>> data, int resource,
				String[] from, int[] to) {
			super(context, data, resource, from, to);

			this.seperatorAdapter = new SimpleAdapter(
					context,
					seperators,
					resource,
					from,
					to
			);
		}

		public SimpleSeperatorAdapter(Context context, List<? extends Map<String, Object>> data,
				int itemResource, String[] itemFrom, int[] itemTo,
				int sepResource, String[] sepFrom, int[] sepTo) {

			super(context, data, itemResource, itemFrom, itemTo);

			this.seperatorAdapter = new SimpleAdapter(
					context,
					seperators,
					sepResource,
					sepFrom,
					sepTo
			);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			@SuppressWarnings("unchecked")
			HashMap<String,Object> item = (HashMap<String, Object>) this.getItem(position);

			if(isSeperator(item)) {
				this.seperators.clear();
				this.seperators.add(item);
				this.seperatorAdapter.notifyDataSetChanged();
				return this.seperatorAdapter.getView(0, null, parent);
			}

			return super.getView(position, null, parent);
		}

		public boolean isSeperator(int pos) {
			return isSeperator((HashMap<String,Object>)getItem(pos));
		}
		
		private boolean isSeperator(HashMap<String,Object> item) {
			Boolean isSep = (Boolean) item.get(IS_SEPERATOR_ITEM_KEY);

			if(isSep == null || !isSep) {
				return false;
			}

			return true;
		}

		private boolean isEnabled(HashMap<String,Object> item) {
			Boolean isEn = (Boolean) item.get(IS_ENABLED_ITEM_KEY);

			if(isEn == null) {
				return true;
			}

			return isEn;
		}

		@Override
		public boolean areAllItemsEnabled() {
			return false;
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean isEnabled(int position) {
			return true;
			//return isEnabled((HashMap<String, Object>) this.getItem(position));
		}
	}
}
