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
package org.t2health.lib.media;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class ListMediaPlayer implements OnCompletionListener {
	private static final String TAG = ListMediaPlayer.class.getSimpleName();
	
	private List<Uri> uris = new ArrayList<Uri>();
	private List<Integer> durations = new ArrayList<Integer>();
	private Context context;
	private int currentIndex = 0;
	private MediaPlayer mediaPlayer;
	private int duration = 0;
	private OnCompleteListener completeListener;
	private StopMonitor stopMonitor = new StopMonitor();
	private int startOffset = 0;
	private int endOffset = 0;
	
	private Handler stopIfNeededHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			stopIfNeeded();
		}
	};
	
	public ListMediaPlayer(Context c) {
		this.context = c;
		mediaPlayer = new MediaPlayer();
	}
	
	public ListMediaPlayer(Context c, List<Uri> uris) {
		this.context = c;
		mediaPlayer = new MediaPlayer();
		setMedia(uris);
	}
	
	public void setStartOffset(int millisFromStart) {
		this.startOffset = millisFromStart;
	}
	
	public int getStartOffset() {
		return this.startOffset;
	}
	
	public void setEndOffset(int millisFromEnd) {
		this.endOffset = millisFromEnd;
	}
	
	public int getEndOffset() {
		return this.endOffset;
	}
	
	public void setMedia(List<Uri> uris) {
		this.uris.clear();
		
		currentIndex = 0;
		duration = 0;
		durations.clear();
		for(int i = 0; i < uris.size(); ++i) {
			//Log.v(TAG, "prepare: "+ uris.get(i));
			MediaPlayer mp = MediaPlayer.create(context, uris.get(i));
			if(mp != null) {
				this.uris.add(uris.get(i));
				int dur = mp.getDuration();
				//Log.v(TAG, dur +" uri:"+ uris.get(i));
				durations.add(dur);
				duration += dur;
				mp.release();
			}
		}
		
		prepare(currentIndex);
	}
	
	public void setOnCompletionListener(OnCompleteListener l) {
		this.completeListener = l;
	}
	
	public int getCurrentPosition() {
		return _getCurrentPosition() - startOffset;
	}
	
	private int _getCurrentPosition() {
		int duration = 0;
		for(int i = 0; i < durations.size(); ++i) {
			if(i < currentIndex) {
				duration += durations.get(i);
			} else {
				break;
			}
		}
		
		int mpDuration = 0;
		try {
			mpDuration = mediaPlayer.getCurrentPosition();
		} catch (IllegalStateException e) {
			
		}
		
		return duration + mpDuration;
	}

	public int getDuration() {
		Log.v(TAG, "getDuration:"+duration);
		return _getDuration() - startOffset - endOffset;
	}
	
	private int _getDuration() {
		return duration;
	}

	public void seekTo(int msec) {
		if(msec > getDuration()) {
			stop();
			return;
		}
		
		this._seek(msec + startOffset);
	}
	
	private void _seek(int msec) {
		Log.v(TAG, "seekTo:"+ msec);
		
		int runningDuration = 0;
		int targetIndex = 0;
		for(int i = 0; i < durations.size(); ++i) {
			int dur = durations.get(i);
			targetIndex = i;
			
			if(runningDuration + dur > msec) {
				break;
			}
			
			runningDuration += dur;
		}
		
		currentIndex = targetIndex;
		
		int newPos = msec - runningDuration;
		Log.v(TAG, "newPos:"+newPos);
		
		stop();
		prepare(currentIndex);
		mediaPlayer.seekTo(newPos);
	}

	public void start() {
		Log.v(TAG, "start");
		if(!mediaPlayer.isPlaying()) {
			if(this._getCurrentPosition() < startOffset) {
				this._seek(startOffset);
			}
			
			stopMonitor = new StopMonitor();
			stopMonitor.start();
			
			mediaPlayer.start();
		}
	}

	public void stop() {
		Log.v(TAG, "stop");
		if(mediaPlayer.isPlaying()) {
			stopMonitor.setIsRunning(false);
			mediaPlayer.stop();
		}
	}
	
	public void pause() {
		Log.v(TAG, "pause");
		if(mediaPlayer.isPlaying()) {
			stopMonitor.setIsRunning(false);
			mediaPlayer.pause();
		}
	}
	
	public void release() {
		Log.v(TAG, "release");
		mediaPlayer.release();
	}
	
	private void prepare(int index) {
		Log.v(TAG, "prepare");
		if(index < uris.size()) {
			Uri uri = uris.get(index);
			
			try {
				Log.v(TAG, "prepare:"+uri);
				mediaPlayer.reset();
				mediaPlayer.setLooping(false);
				mediaPlayer.setOnCompletionListener(this);
				mediaPlayer.setDataSource(context, uri);
				mediaPlayer.prepare();
				
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}	
		}
	}
	
	private void stopIfNeeded() {
		int pos = getCurrentPosition() + startOffset;
		int endPos = duration - endOffset;
		if(pos >= endPos) {
			stop();
		}
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		Log.v(TAG, "onCompletion");
		currentIndex++;
		if(currentIndex < uris.size()) {
			Log.v(TAG, "goto next");
			prepare(currentIndex);
			start();
		} else {
			if(completeListener != null) {
				completeListener.onCompletion(this);
			}
		}
	}
	
	public interface OnCompleteListener {
		public void onCompletion(ListMediaPlayer player);
	}
	
	private class StopMonitor extends Thread {
		private boolean isRunning = true;

		@Override
		public void run() {
			while(true) {
				stopIfNeededHandler.sendEmptyMessage(0);
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
				}
				
				if(!isRunning) {
					break;
				}
			}
		}
		
		public void setIsRunning(boolean b) {
			this.isRunning  = b;
		}
	}
}