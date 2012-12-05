package com.pinkwerther.support.ads;

import android.os.Handler;
import android.support.v4.app.Fragment;

import com.pinkwerther.support.OnFinishedListener;
import com.pinkwerther.support.PinkwertherSubstantialInterface;
import com.pinkwerther.support.PinkwertherSupport;
import com.pinkwerther.support.R;


public class PinkwertherAds implements PinkwertherSubstantialInterface {
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
	public void init(final PinkwertherSupport pwSupport) {
		final Fragment ads = getDefaultBanner();
		if (ads != null)
			pwSupport.getFragmentManager().beginTransaction().add(R.id.advertisement,ads).commit();
		final PinkwertherCommercialBanner com = getCommercialBanner();
		com.setOnInitializedListener(new OnFinishedListener() {
			@Override
			public void finished() {
				pwSupport.getFragmentManager().beginTransaction().remove(ads).commit();
			}
		});
		if (com != null) {
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					pwSupport.getFragmentManager().beginTransaction().add(R.id.commercial, com).commit();
				}
			}, getCommercialWaitTime());
		}
	}
	@Override
	public void destroy() {
	}
}
