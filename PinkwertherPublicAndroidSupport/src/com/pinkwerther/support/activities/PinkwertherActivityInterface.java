package com.pinkwerther.support.activities;

import android.support.v4.app.Fragment;
import android.view.View;

import com.pinkwerther.support.PinkwertherSupport;
import com.pinkwerther.support.ads.PinkwertherAdsInterface;
import com.pinkwerther.support.license.PinkwertherLicenseInterface;
import com.pinkwerther.support.resources.PinkwertherResourceManager;
import com.pinkwerther.support.tracking.PinkwertherTrackingInterface;


public interface PinkwertherActivityInterface {
	public static final boolean DEVEL=true;
	
	public abstract String TAG();
	public void setSupportProgressBarIndeterminate(boolean loading);
	public void setSupportProgressBarIndeterminateVisibility(boolean loading);
	
	public void setAdFragment(Fragment fragment);
	public void setAdView(View view);
	
	public void reportClick(String name);
	
	public PinkwertherResourceManager getPinkwertherResourceManager();
	public PinkwertherAdsInterface getPinkwertherAds();
	public PinkwertherLicenseInterface getPinkwertherLicense();
	public PinkwertherTrackingInterface getPinkwertherTracking();
	
	public PinkwertherSupport getPinkwertherSupport();
	
	public void onFinishedInitialization();
}
