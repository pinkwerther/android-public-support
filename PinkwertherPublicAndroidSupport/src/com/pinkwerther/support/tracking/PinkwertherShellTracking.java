package com.pinkwerther.support.tracking;

import android.util.Log;

import com.pinkwerther.support.PinkwertherSupport;

public class PinkwertherShellTracking implements PinkwertherTrackingInterface {

	@Override
	public void init(PinkwertherSupport support) {
	}

	@Override
	public void destroy() {
	}

	@Override
	public void track(TrackingEvent event) {
		Log.d("PinkwertherShellTracking",event.serialize());
	}

}
