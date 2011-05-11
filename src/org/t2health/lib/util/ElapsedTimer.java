package org.t2health.lib.util;

/**
 * A simple class to time events.
 * @author robbiev
 *
 */
public final class ElapsedTimer {
	private long mElapsedTime = 0;
	private long mStartTime = 0;
	private boolean mIsStarted = false;
	
	/**
	 * Start the timer.
	 */
	public void start() {
		if(mIsStarted) {
			return;
		}
		this.mStartTime = System.currentTimeMillis();
		this.mIsStarted = true;
	}
	
	/**
	 * Stop the timer.
	 */
	public void stop() {
		if(!mIsStarted) {
			return;
		}
		this.mElapsedTime += System.currentTimeMillis() - this.mStartTime;
		this.mStartTime = 0;
		this.mIsStarted = false;
	}
	
	/**
	 * Reset the timer.
	 */
	public void reset() {
		this.mElapsedTime = 0;
		this.mStartTime = 0;
		this.mIsStarted = false;
	}
	
	/**
	 * Get the elapsed time.
	 * @return
	 */
	public long getElapsedTime() {
		if(mIsStarted) {
			return this.mElapsedTime + System.currentTimeMillis() - this.mStartTime;
		}
		return this.mElapsedTime;
	}
}
