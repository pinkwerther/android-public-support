package com.pinkwerther.support.ads;

import com.pinkwerther.support.activities.PinkwertherActivityInterface;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public abstract class UniverseOfAds extends Fragment {
	public abstract int getViewResId();
	public abstract String TAG();
	public abstract UniverseOfAds getFollowUpFragment();
	public abstract int getPositionResId();
	public abstract int getLinkResId();

	public static class BannerCollection extends UniverseOfAds {
		@Override
		public int getViewResId() {
			// TODO Auto-generated method stub
			return 0;
		}
		@Override
		public String TAG() {
			return "BannerCollection";
		}

		@Override
		public UniverseOfAds getFollowUpFragment() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int getPositionResId() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getLinkResId() {
			// TODO Auto-generated method stub
			return 0;
		}
		
	}
	public static class MeinTarotProBanner extends UniverseOfAds {
		@Override
		public int getViewResId() {
			// TODO Auto-generated method stub
			return 0;
		}
		@Override
		public String TAG() {
			return "MeinTarotProBanner";
		}
		@Override
		public UniverseOfAds getFollowUpFragment() {
			return null;
		}
		@Override
		public int getPositionResId() {
			// TODO Auto-generated method stub
			return 0;
		}
		@Override
		public int getLinkResId() {
			// TODO Auto-generated method stub
			return 0;
		}
	}
	public static class TrumpeteerBanner extends UniverseOfAds {
		@Override
		public int getViewResId() {
			// TODO Auto-generated method stub
			return 0;
		}
		@Override
		public String TAG() {
			return "TrumpeteerBanner";
		}
		@Override
		public UniverseOfAds getFollowUpFragment() {
			return null;
		}
		@Override
		public int getPositionResId() {
			// TODO Auto-generated method stub
			return 0;
		}
		@Override
		public int getLinkResId() {
			// TODO Auto-generated method stub
			return 0;
		}
	}
	
	public void performClick() {
		UniverseOfAds follow = getFollowUpFragment();
		if (follow != null) {
			getActivity().getSupportFragmentManager()
				.beginTransaction().replace(follow.getPositionResId(), follow).commit();
		} else {
			int link = getLinkResId();
			if (link > 0) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(getString(link)));
				//TODO show wheel?
				startActivity(intent);
			}
		}
		if (getActivity() instanceof PinkwertherActivityInterface)
			((PinkwertherActivityInterface)getActivity()).reportClick(TAG());
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View ret = inflater.inflate(getViewResId(), container, false);
		if (ret != null)
			ret.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					performClick();
				}
			});
		return ret;
	}
}
