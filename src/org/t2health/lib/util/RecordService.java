package org.t2health.lib.util;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class RecordService extends Service {
	private static final String TAG = RecordService.class.getSimpleName();
	public static final int NOTIFICATION_ID = 230952309;
	private static final int ACTION_ON_RECORD = 34983424;
	private static final int ACTION_ON_STOPPED = 23244248;

	private static Intent notificationIntent;
	private static String notificationTitle;
	private static String notificationDesc;

	private static boolean isRunning = false;
	private static RecordService thisService;
	private static int notificationIconRes;

	private boolean isMarking = false;
	private boolean isRecording = false;
	private long markStartTime;
	private MediaRecorder recorder;
	private ServiceRecording currentRecording;
	private OnActionListener onActionListener;
	private ElapsedTimer timer = new ElapsedTimer();

	private ArrayList<Long> startMarkerTimes = new ArrayList<Long>();
	private ArrayList<Long> stopMarkerTimes = new ArrayList<Long>();

	private Handler actionHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case ACTION_ON_RECORD:
				if(onActionListener != null) {
					onActionListener.onRecordStarted();
				}
				break;

			case ACTION_ON_STOPPED:
				if(onActionListener != null) {
					onActionListener.onStopped();
				}
				break;
			}
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
//		Log.v(TAG, "onCreate");
		isRunning = true;
		recorder = new MediaRecorder();
		thisService = this;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		hideNotification();

		if(recorder != null) {
			try {
				recorder.stop();
				recorder.release();
			} catch (Exception e) {

			}
		}
		isRunning = false;
	}

	public void setOnActionListener(OnActionListener l) {
		this.onActionListener = l;
	}

	public long getDuration() {
		if(currentRecording == null) {
			return 0;
		}
		long startTime = currentRecording.startTime;
		long endTime = 0;
		if(isRecording()) {
			endTime = System.currentTimeMillis();
		} else {
			endTime = currentRecording.endTime;
		}

		return endTime - startTime;
	}

	public ServiceRecording getRecording() {
		if(isRecording()) {
			return null;
		}
		return currentRecording;
	}
	
	public void reset() {
		if(!isRecording()) {
			this.currentRecording = null;
		}
	}

	public void stopRecording() {
		hideNotification();

		recorder.stop();
		recorder.release();
		recorder = null;
		timer.stop();

		currentRecording.endTime = System.currentTimeMillis();

		isRecording = false;

		actionHandler.sendEmptyMessage(ACTION_ON_STOPPED);
	}

	public void startRecording(final File outputFile) {
		if(isRecording()) {
			return;
		}

		new Thread(new Runnable(){
			@Override
			public void run() {
				recorder = new MediaRecorder();
				recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
				recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
				recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
				recorder.setOutputFile(outputFile.getAbsolutePath());
				//recorder.setMaxDuration(MAX_DURATION);

				try {
					recorder.prepare();
					recorder.start();
//					Log.v(TAG, "Recording started");

					timer.start();

					currentRecording = new ServiceRecording();
					currentRecording.file = outputFile;
					currentRecording.startTime = System.currentTimeMillis();

					isRecording = true;

					actionHandler.sendEmptyMessage(ACTION_ON_RECORD);

					showNotification();

				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (RuntimeException e) {
					Log.e(TAG, e.getMessage());
					e.printStackTrace();
				}
			}
		}).start();

		return;
	}

	public boolean isRecording() {
		return isRecording;
	}

	public boolean isMarking() {
		return isMarking;
	}

	public void startMarking() {
		startMarkerTimes.add(timer.getElapsedTime());
		isMarking = true;
	}

	public void stopMarking() {
		if(stopMarkerTimes.size() < startMarkerTimes.size()) {
			isMarking = false;
			stopMarkerTimes.add(
					startMarkerTimes.size() - 1,
					timer.getElapsedTime()
			);

			long start = startMarkerTimes.get(startMarkerTimes.size() - 1);
			long stop = stopMarkerTimes.get(stopMarkerTimes.size() - 1);
			Log.v(TAG, "mark:"+ start +","+stop);
		}
	}

	public ArrayList<Long> getStartMarkerTimes() {
		return startMarkerTimes;
	}

	public ArrayList<Long> getStopMarkerTimes() {
		return stopMarkerTimes;
	}

	public static RecordService getService() {
		return thisService;
	}

	public void showNotification() {
		if(notificationIntent == null) {
			return;
		}

		// Build the notification and the status bar text.
		Notification notification = new Notification(
				notificationIconRes,
				notificationTitle,
				System.currentTimeMillis()
		);

		notification.flags = Notification.FLAG_NO_CLEAR | Notification.FLAG_SHOW_LIGHTS;

		// Prepare the intent to load.
		PendingIntent contentIntent = PendingIntent.getActivity(
				this,
				0,
				notificationIntent,
				0
		);

		// Set the title and details of the notification.
		notification.setLatestEventInfo(
				this,
				notificationTitle,
				notificationDesc,
				contentIntent
		);

		// Show the notification.
		((NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE)).notify(
				NOTIFICATION_ID,
				notification
		);
	}

	public void hideNotification() {
		((NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(
				NOTIFICATION_ID
		);
	}

	public static void startService(Context c, Intent notifyIntent, int notifyIconRes, String notifyTitle, String notifyDesc) {
		if(!isRunning) {
			isRunning = true;
			notificationIconRes = notifyIconRes;
			notificationIntent = notifyIntent;
			notificationTitle = notifyTitle;
			notificationDesc = notifyDesc;
			c.startService(new Intent(c, RecordService.class));
		}
	}

	public static void stopService(Context c) {
		c.stopService(new Intent(c, RecordService.class));
		isRunning = false;
	}

	public interface OnActionListener {
		public void onRecordStarted();
		public void onStopped();
	}
}
