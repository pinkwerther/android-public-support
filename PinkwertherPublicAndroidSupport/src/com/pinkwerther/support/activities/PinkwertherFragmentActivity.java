package com.pinkwerther.support.activities;

import com.pinkwerther.support.PinkwertherSupport;
import com.pinkwerther.support.R;
import com.pinkwerther.support.ads.PinkwertherAds;
import com.pinkwerther.support.fragment.PinkwertherSubstantialFragment;
import com.pinkwerther.support.license.PinkwertherLicenseInterface;
import com.pinkwerther.support.resources.PinkwertherResourceManager;
import com.pinkwerther.support.tracking.PinkwertherShellTracking;
import com.pinkwerther.support.tracking.PinkwertherTrackingInterface;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;

public class PinkwertherFragmentActivity extends FragmentActivity implements PinkwertherActivityInterface {

	@Override
	public String TAG() {
		return "PinkwertherFragmentActivity";
	}
	
	PinkwertherSupport pwSupport;

	@Override
	public PinkwertherSupport getPinkwertherSupport() {
		if (pwSupport == null) {
			pwSupport = (PinkwertherSupport)getSupportFragmentManager().findFragmentByTag("PinkwertherSupport");
			if (pwSupport == null)
				pwSupport = new PinkwertherSupport();
		}
		return pwSupport;
	}
	
	@Override
	public void finishPinkwertherActivity() {
		finish();
	}
	
	@Override
	protected void onCreate(Bundle arg0) {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.vertical_adbottom);
		getSupportFragmentManager().beginTransaction().
			add(getPinkwertherSupport(), "PinkwertherSupport").commit();
		super.onCreate(arg0);
	}

	@Override
	public PinkwertherResourceManager getPinkwertherResourceManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PinkwertherAds getPinkwertherAds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PinkwertherLicenseInterface getPinkwertherLicense() {
		return null;
	}

	@Override
	public PinkwertherTrackingInterface getPinkwertherTracking() {
		return new PinkwertherShellTracking();
	}

	@Override
	public void onFinishedInitialization() {
	}

	@Override
	public PinkwertherSubstantialFragment getInitialMainFragment() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSupportProgressBarIndeterminate(boolean loading) {
		setProgressBarIndeterminate(loading);
	}

	@Override
	public void setSupportProgressBarIndeterminateVisibility(boolean loading) {
		setProgressBarIndeterminateVisibility(loading);
	}

}
