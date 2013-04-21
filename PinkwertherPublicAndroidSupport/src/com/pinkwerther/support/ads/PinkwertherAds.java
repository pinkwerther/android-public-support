package com.pinkwerther.support.ads;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.pinkwerther.support.OnFinishedListener;
import com.pinkwerther.support.PinkwertherSubstantialInterface;
import com.pinkwerther.support.PinkwertherSupport;
import com.pinkwerther.support.R;
import com.pinkwerther.support.license.OnLicenseChangeListener;
import com.pinkwerther.support.license.PinkwertherLicense;


public class PinkwertherAds implements PinkwertherSubstantialInterface, OnLicenseChangeListener {
	public Fragment getDefaultBanner() {
		return new PinkwertherBannerSet();
	}
	public long getCommercialWaitTime() {
		return 30000;
	}
	public PinkwertherCommercialBanner getCommercialBanner() {
		return null;
	}
	@Override
	public Bundle getRecreationArguments() {
		return null;
	}
	PinkwertherSupport pwSupport;
	Fragment mAds;
	PinkwertherCommercialBanner mCommercials;
	@Override
	public void init(final PinkwertherSupport pwSupport, Bundle bundle) {
		this.pwSupport = pwSupport;
		mAds = getDefaultBanner();
		if (mAds != null)
			pwSupport.getSupportFragmentManager().beginTransaction().replace(R.id.advertisement,mAds).commit();
		mCommercials = getCommercialBanner();
		if (mCommercials != null) {
			mCommercials.setOnInitializedListener(new OnFinishedListener() {
				@Override
				public void finished() {
					pwSupport.getSupportFragmentManager().beginTransaction().remove(mAds).commit();
				}
			});
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					pwSupport.getSupportFragmentManager().beginTransaction().replace(R.id.commercial, mCommercials).commit();
				}
			}, getCommercialWaitTime());
		}
	}
	@Override
	public void destroy() {
	}
	@Override
	public void licenseChange(int license) {
		if (pwSupport.hasPermission(PinkwertherLicense.PERMISSION_NO_ADS)) {
			FragmentManager fm = pwSupport.getSupportFragmentManager();
			if (mAds != null)
			try {
				fm.beginTransaction().remove(mAds).commit();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (mCommercials != null)
			try {
				fm.beginTransaction().remove(mCommercials).commit();
			} catch (Exception e) {
				e.printStackTrace();
			}
			destroy();
		} else
			init(pwSupport,null);
	}
}
