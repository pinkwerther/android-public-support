package com.pinkwerther.support.ads;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PinkwertherBannerSet extends Fragment {
	
	public int getAdCount() {
		return 5;
	}
	public Fragment getAd(int index) {
		PinkwertherBanner ret = new PinkwertherBanner();
		ret.number = index+1;
		return ret;
	}
	public long getChangeTime() {
		return 30000;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final ViewPager vp = new ViewPager(getActivity());
		vp.setAdapter(new FragmentPagerAdapter(getFragmentManager()) {
			@Override
			public Fragment getItem(int arg0) {
				return getAd(arg0);
			}
			@Override
			public int getCount() {
				return getAdCount();
			}
		});
		
		final Handler handler = new Handler();
		if (getAdCount() > 1) {
			Runnable run = new Runnable() {
				int index=0;
				@Override
				public void run() {
					index++;
					if (index>=getAdCount())
						index = 0;
					vp.setCurrentItem(index,true);
					handler.postDelayed(this, getChangeTime());
				}
			};
			handler.postDelayed(run, getChangeTime());
		}
		
		return vp;
	}
}
