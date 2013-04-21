package com.pinkwerther.support.tracking;

import android.os.Bundle;
import android.util.Log;

import com.pinkwerther.support.PinkwertherSupport;

public class PinkwertherShellTracking implements PinkwertherTrackingInterface {

	@Override
	public void init(PinkwertherSupport support, Bundle bundle) {
	}

	@Override
	public void destroy() {
	}

	@Override
	public void track(TrackingEvent event) {
		Log.d("PinkwertherShellTracking",event.serialize());
	}

	@Override
	public Bundle getRecreationArguments() {
		return null;
	}

}
