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
package org.t2health.lib.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.t2health.lib.R;
import org.t2health.lib.WorkflowStep;
import org.t2health.lib.activity.BaseNavigationActivity;
import org.t2health.lib.activity.WebViewActivity;
import org.t2health.lib.util.WebViewUtil;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Provides a structure of content based on an XML file.
 * located in res/xml.
 * <?xml version="1.0" encoding="utf-8"?>
 * <workflow title="Tile of this workflow item">
 *     <step id="1" positiveStepId="3" negativeStepId="2" neutralStepId="">
 * 			<title>Step 1 title</title>
 * 			<content><![CDATA[
 * 		   		This is HTML content shown in the workflow bubble.
 * 	    	]]></content>
 * 			<detailedContent></detailedContent>
 * 		</step>
 *     
 *     <step id="2" positiveStepId="" negativeStepId="" neutralStepId="">
 * 			<title>Step 2 title</title>
 * 			<content><![CDATA[
 * 		    	This is HTML content shown in the workflow bubble.
 * 	    	]]></content>
 * 			<detailedContent><![CDATA[
 * 		    	Optional HTML content shown in a separate activity.
 * 		    	If this content is set, a Details button will appear
 * 		    	next to the title.
 * 	    	]]></detailedContent>
 * 		</step>
 * 	
 * 		<step id="3" positiveStepId="" negativeStepId="" neutralStepId="">
 * 			<title>Step 3 title</title>
 * 			<content><![CDATA[
 * 		    	This is HTML content shown in the workflow bubble.
 * 	    	]]></content>
 * 			<detailedContent></detailedContent>
 * 		</step>
 * </workflow>
 * 
 * 
 * Note: In order for the details HTML content of an item to load, 
 * org.t2health.lib.activity.util.WebViewActivity
 * needs be be listed in your manifest file as an activity.
 * @author robbiev
 *
 * @TODO Make this activity take a URL too. Then use a conditional GET to
 * get the updated content. Cache the new content some place.
 */
public class XMLWorkflowActivity extends BaseNavigationActivity {
	/**
	 * The resource id of the xml file to use for this workflow.
	 */
	public static final String EXTRA_XML_RESOURCE = "xmlResourceId";
	
	/**
	 * Allows all items to be shown by default.
	 */
	public static final String EXTRA_EXPAND_ALL = "expandAll";
	
	private int xmlResourceId;
	private LinkedHashMap<String, WorkflowStep> workflowSteps = new LinkedHashMap<String, WorkflowStep>();
	private WorkflowStepViewFactory workflowStepViewFactory;
	private ArrayList<WorkflowStep> currentSteps;
	private Activity thisActivity = this;
	private boolean expandAll;
	private LinearLayout contentArea;
	private ScrollView scrollView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		this.expandAll = intent.getBooleanExtra(EXTRA_EXPAND_ALL, false);

		this.xmlResourceId = intent.getIntExtra(EXTRA_XML_RESOURCE, -1);
		if(this.xmlResourceId == -1) {
			this.finish();
			return;
		}

		contentArea = new LinearLayout(this);
		contentArea.setOrientation(LinearLayout.VERTICAL);
		scrollView = new ScrollView(this);
		scrollView.addView(contentArea, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		this.setContentView(scrollView);
		
		this.workflowSteps = this.getSteps(xmlResourceId);
		this.currentSteps = new ArrayList<WorkflowStep>();
		this.workflowStepViewFactory = new WorkflowStepViewFactory(this, this.currentSteps);

		// add the steps from the previous orientation shift.
		if(savedInstanceState != null) {
			this.expandAll = savedInstanceState.getBoolean(EXTRA_EXPAND_ALL);
			this.workflowStepViewFactory.setButtonsHidden(!expandAll);

			ArrayList<WorkflowStep> tmpSteps = savedInstanceState.getParcelableArrayList("currentSteps");
			String lastId = "";
			for(int i = 0; i < tmpSteps.size(); ++i) {
				WorkflowStep step = tmpSteps.get(i);
				workflowSteps.put(step.id, step);
				addStepToList(lastId, step.id);

				lastId = step.id;
			}
		}

		// add the first step.
		if(this.currentSteps.size() == 0) {
			WorkflowStep[] steps = this.workflowSteps.values().toArray(new WorkflowStep[this.workflowSteps.size()]);
			if(steps != null && steps.length > 0) {
				// if we are suppose to show all the steps, then add them all now.
				if(expandAll) {
					workflowStepViewFactory.setButtonsHidden(true);
					for(int i = 0; i < steps.length; ++i) {
						steps[i].neutralButtonActive = i < steps.length - 1;
						this.currentSteps.add(steps[i]);
					}
					
					for(int i = 0; i < currentSteps.size(); ++i) {
						contentArea.addView(workflowStepViewFactory.getView(i), new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
					}
				// just show the first step.
				} else {
					addStepToList("", steps[0].id);
				}
			}
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(EXTRA_EXPAND_ALL, this.expandAll);
		outState.putParcelableArrayList("currentSteps", this.currentSteps);
	}

	private LinkedHashMap<String,WorkflowStep> getSteps(int xmlResourceId) {
		LinkedHashMap<String,WorkflowStep> steps = new LinkedHashMap<String,WorkflowStep>();

		boolean inWorkflow = false;
		boolean inStep = false;
		WorkflowStep currentStep = null;
		try {
			XmlResourceParser parser = this.getResources().getXml(xmlResourceId);
			int eventType = parser.getEventType();
			while(eventType != XmlPullParser.END_DOCUMENT) {
				String tag = parser.getName();

				if(eventType == XmlPullParser.START_TAG) {
					if(tag.equals("workflow") && !inWorkflow) {
						inWorkflow = true;
						String title = parser.getAttributeValue(null, "title");
						if(title != null && title.length() > 0) {
							this.setTitle(title);
						}

					} else if(tag.equals("step") && !inStep) {
						currentStep = new WorkflowStep(
								parser.getAttributeValue(null, "id"),
								parser.getAttributeValue(null, "title"),
								parser.getAttributeValue(null, "positiveStepId"),
								parser.getAttributeValue(null, "negativeStepId"),
								parser.getAttributeValue(null, "neutralStepId")
						);
						inStep = true;

					} else if(tag.equals("title") && inStep) {
						if(parser.next() == XmlPullParser.TEXT) {
							currentStep.title = parser.getText();
						}

					} else if(tag.equals("content") && inStep) {
						if(parser.next() == XmlPullParser.TEXT) {
							currentStep.content = parser.getText();
						}

					} else if(tag.equals("detailedContent") && inStep) {
						if(parser.next() == XmlPullParser.TEXT) {
							currentStep.detailedContent = parser.getText();
						}
					}
				} else if(eventType == XmlPullParser.END_TAG) {
					if(tag.equals("step")) {
						inStep = false;
						steps.put(currentStep.id, currentStep);
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

		return steps;
	}

	private void onDetailsButtonPressed(String id) {
		WorkflowStep step = this.workflowSteps.get(id);
		Intent intent = new Intent(this, WebViewActivity.class);
		intent.putExtra(WebViewActivity.EXTRA_CONTENT, step.detailedContent);
		this.startActivity(intent);
	}

	/**
	 * 
	 * @param fromId remove all steps until this id
	 * @param id show this id.
	 */
	private void addStepToList(String fromId, String id) {
		final int scrollTop = this.scrollView.getScrollY();
		rollbackToStep(fromId, false);

		WorkflowStep newStep = this.workflowSteps.get(id);
		newStep.negativeButtonActive = false;
		newStep.positiveButtonActive = false;
		newStep.neutralButtonActive = false;
		this.currentSteps.add(newStep);
		View stepView = workflowStepViewFactory.getView(this.currentSteps.size()-1);
		this.contentArea.addView(
				stepView,
				new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT)
		);
		
		contentArea.requestFocus();
		
		scrollView.postDelayed(new Runnable() {
			@Override
			public void run() {
				scrollView.scrollTo(0, scrollTop);
				contentArea.requestFocus();
			}
		}, 50);
	}

	private void rollbackToStep(String id, boolean inclusive) {
		for(int i = this.currentSteps.size()-1; i >= 0; --i) {
			boolean idsMatch = this.currentSteps.get(i).id.equals(id);
			if(idsMatch && !inclusive) {
				break;
			}

			this.currentSteps.remove(i);
			this.contentArea.removeViewAt(i);

			if(idsMatch && inclusive) {
				break;
			}
		}
	}

	public static View findParentById(View v, int id) {
		ViewParent parent = null;
		View parentView = v;
		
		while(true) {
			parent = parentView.getParent();
			if(parent == null) {
				break;
			}
			
			if(! (parent instanceof View)) {
				break;
			}
			
			parentView = (View)parent;
			if(parentView.getId() == id) {
				return parentView;
			}
		}
		
		return null;
	}

	private class WorkflowStepViewFactory {
		private List<WorkflowStep> items;
		private LayoutInflater layoutInflater;
		private boolean hideButtons;

		public WorkflowStepViewFactory(Context context, List<WorkflowStep> objects) {
			items = objects;
			this.layoutInflater = (LayoutInflater)context.getSystemService(LAYOUT_INFLATER_SERVICE);
		}

		public void setButtonsHidden(boolean b) {
			this.hideButtons = b;
		}
		
		public View getView(final int position) {
			WorkflowStep step = this.getItem(position);
			
			View view = layoutInflater.inflate(R.layout.workflow_step, null);

			if(step.title == null || step.title.length() == 0) {
				((ViewGroup)view.findViewById(R.id.text1).getParent()).removeView(view.findViewById(R.id.text1));
			} else {
				((TextView)view.findViewById(R.id.text1)).setText(step.title);
			}

			if(step.content == null || step.content.length() == 0) {
				((ViewGroup)view.findViewById(R.id.text2).getParent()).removeView(view.findViewById(R.id.text2));
			} else {
				WebView wv = (WebView)view.findViewById(R.id.text2);
				WebViewUtil.formatWebViewText(thisActivity, wv, step.content, Color.BLACK);
			}

			view.findViewById(R.id.detailsButton).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					onDetailsButtonPressed(getItem(position).id);
				}
			});
			view.findViewById(R.id.negativeButton).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					WorkflowStep s = getItem(position);
					s.negativeButtonActive = true;
					s.positiveButtonActive = false;
					s.neutralButtonActive = false;
					
					View workflowView = XMLWorkflowActivity.findParentById(v, R.id.workflowStep);
					workflowView.findViewById(R.id.negativeArrow).setVisibility(View.VISIBLE);
					workflowView.findViewById(R.id.positiveArrow).setVisibility(View.INVISIBLE);
					workflowView.findViewById(R.id.neutralArrow).setVisibility(View.INVISIBLE);
					
					addStepToList(s.id, s.negativeId);
				}
			});
			view.findViewById(R.id.positiveButton).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					WorkflowStep s = getItem(position);
					s.negativeButtonActive = false;
					s.positiveButtonActive = true;
					s.neutralButtonActive = false;
					
					View workflowView = XMLWorkflowActivity.findParentById(v, R.id.workflowStep);
					workflowView.findViewById(R.id.negativeArrow).setVisibility(View.INVISIBLE);
					workflowView.findViewById(R.id.positiveArrow).setVisibility(View.VISIBLE);
					workflowView.findViewById(R.id.neutralArrow).setVisibility(View.INVISIBLE);
					
					addStepToList(s.id, s.positiveId);
				}
			});
			view.findViewById(R.id.neutralButton).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					WorkflowStep s = getItem(position);
					s.negativeButtonActive = false;
					s.positiveButtonActive = false;
					s.neutralButtonActive = true;
					
					View workflowView = XMLWorkflowActivity.findParentById(v, R.id.workflowStep);
					workflowView.findViewById(R.id.negativeArrow).setVisibility(View.INVISIBLE);
					workflowView.findViewById(R.id.positiveArrow).setVisibility(View.INVISIBLE);
					workflowView.findViewById(R.id.neutralArrow).setVisibility(View.VISIBLE);
					
					addStepToList(s.id, s.neutralId);
				}
			});

			view.findViewById(R.id.detailsButton).setVisibility(View.INVISIBLE);
			view.findViewById(R.id.negativeButton).setVisibility(View.INVISIBLE);
			view.findViewById(R.id.positiveButton).setVisibility(View.INVISIBLE);
			view.findViewById(R.id.neutralButton).setVisibility(View.INVISIBLE);
			if(step.detailedContent != null && step.detailedContent.length() > 0) {
				view.findViewById(R.id.detailsButton).setVisibility(View.VISIBLE);
			} else {
				((ViewGroup)view.findViewById(R.id.detailsButton).getParent()).removeView(view.findViewById(R.id.detailsButton));
			}
			if(step.negativeId != null && step.negativeId.length() > 0) {
				view.findViewById(R.id.negativeButton).setVisibility(View.VISIBLE);
			}
			if(step.positiveId != null && step.positiveId.length() > 0) {
				view.findViewById(R.id.positiveButton).setVisibility(View.VISIBLE);
			}
			if(step.neutralId != null && step.neutralId.length() > 0) {
				view.findViewById(R.id.neutralButton).setVisibility(View.VISIBLE);
			}

			if(hideButtons) {
				view.findViewById(R.id.negativeButton).setVisibility(View.GONE);
				view.findViewById(R.id.positiveButton).setVisibility(View.GONE);
				view.findViewById(R.id.neutralButton).setVisibility(View.GONE);
			}

			view.findViewById(R.id.negativeArrow).setVisibility(View.INVISIBLE);
			view.findViewById(R.id.positiveArrow).setVisibility(View.INVISIBLE);
			view.findViewById(R.id.neutralArrow).setVisibility(View.INVISIBLE);
			if(step.negativeButtonActive) {
				view.findViewById(R.id.negativeArrow).setVisibility(View.VISIBLE);
			}
			if(step.positiveButtonActive) {
				view.findViewById(R.id.positiveArrow).setVisibility(View.VISIBLE);
			}
			if(step.neutralButtonActive) {
				view.findViewById(R.id.neutralArrow).setVisibility(View.VISIBLE);
			}
			
			return view;
		}

		public WorkflowStep getItem(int pos) {
			return this.items.get(pos);
		}
	}
}
