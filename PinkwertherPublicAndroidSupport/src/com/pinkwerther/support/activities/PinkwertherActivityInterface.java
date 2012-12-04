package com.pinkwerther.support.activities;

import android.support.v4.app.FragmentManager;

import com.pinkwerther.support.PinkwertherSupport;
import com.pinkwerther.support.ads.PinkwertherAds;
import com.pinkwerther.support.fragment.PinkwertherSubstantialFragment;
import com.pinkwerther.support.license.PinkwertherLicenseInterface;
import com.pinkwerther.support.resources.PinkwertherResourceManager;
import com.pinkwerther.support.tracking.PinkwertherTrackingInterface;


public interface PinkwertherActivityInterface {
	public static final boolean DEVEL=true;
	
	public String TAG();
	public void setSupportProgressBarIndeterminate(boolean loading);
	public void setSupportProgressBarIndeterminateVisibility(boolean loading);
	
	public void finishPinkwertherActivity();
	
	public FragmentManager getSupportFragmentManager();

	public PinkwertherSubstantialFragment getInitialMainFragment();
	
	public PinkwertherResourceManager getPinkwertherResourceManager();
	public PinkwertherAds getPinkwertherAds();
	public PinkwertherLicenseInterface getPinkwertherLicense();
	public PinkwertherTrackingInterface getPinkwertherTracking();
	
	public PinkwertherSupport getPinkwertherSupport();
	
	public void onFinishedInitialization();
}
