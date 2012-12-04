package com.pinkwerther.support.ads;

import android.os.Handler;
import android.support.v4.app.Fragment;

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
	public Fragment getCommercialBanner() {
		return null;
	}
	@Override
	public void init(final PinkwertherSupport pwSupport) {
		Fragment ads = getDefaultBanner();
		if (ads != null)
			pwSupport.getFragmentManager().beginTransaction().replace(R.id.advertisement,ads).commit();
		final Fragment com = getCommercialBanner();
		if (com != null) {
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					pwSupport.getFragmentManager().beginTransaction().replace(R.id.advertisement, com).commit();
				}
			}, getCommercialWaitTime());
		}
	}
	@Override
	public void destroy() {
	}
}
