package org.t2health.lib.util;

public class ElapsedTimer {
	private long mElapsedTime = 0;
	private long mStartTime = 0;
	private boolean mIsStarted = false;
	
	public void start() {
		if(mIsStarted) {
			return;
		}
		this.mStartTime = System.currentTimeMillis();
		this.mIsStarted = true;
	}
	
	public void stop() {
		if(!mIsStarted) {
			return;
		}
		this.mElapsedTime += System.currentTimeMillis() - this.mStartTime;
		this.mStartTime = 0;
		this.mIsStarted = false;
	}
	
	public void reset() {
		this.mElapsedTime = 0;
		this.mStartTime = 0;
		this.mIsStarted = false;
	}
	
	public long getElapsedTime() {
		if(mIsStarted) {
			return this.mElapsedTime + System.currentTimeMillis() - this.mStartTime;
		}
		return this.mElapsedTime;
	}
}
