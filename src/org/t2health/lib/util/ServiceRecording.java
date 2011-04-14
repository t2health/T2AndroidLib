package org.t2health.lib.util;

import java.io.File;

public class ServiceRecording {
	public File file;
	public long startTime;
	public long endTime;

	public long getDuration() {
		return endTime - startTime;
	}
}
