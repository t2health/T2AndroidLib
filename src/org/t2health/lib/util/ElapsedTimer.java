package org.t2health.lib.util;

public class ElapsedTimer {
	private long elapsedTime = 0;
	private long startTime = 0;
	private boolean isStarted = false;
	
	public void start() {
		if(isStarted) {
			return;
		}
		this.startTime = System.currentTimeMillis();
		this.isStarted = true;
	}
	
	public void stop() {
		if(!isStarted) {
			return;
		}
		this.elapsedTime += System.currentTimeMillis() - this.startTime;
		this.startTime = 0;
		this.isStarted = false;
	}
	
	public void reset() {
		this.elapsedTime = 0;
		this.startTime = 0;
		this.isStarted = false;
	}
	
	public long getElapsedTime() {
		if(isStarted) {
			return this.elapsedTime + System.currentTimeMillis() - this.startTime;
		}
		return this.elapsedTime;
	}
}
