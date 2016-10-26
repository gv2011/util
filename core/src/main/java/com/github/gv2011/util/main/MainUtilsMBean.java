package com.github.gv2011.util.main;

import java.time.Instant;

public interface MainUtilsMBean {

	long getUncaughtExceptionCount();

	Instant getStartTime();

	void shutdown();

}
