package com.pinkwerther.support.ads;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.pinkwerther.support.R;

public class PinkwertherBanner extends Fragment implements OnClickListener{
	public int number = 0;
	private final static int REQUEST_CODE=23;
	public String getMarketLink() {
		return "market://search?q=pub:pinkwerther.com";
	}
	public String getWebLink() {
		return "http://play.google.com/store/apps/developer?id=pinkwerther.com";
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		if (savedInstanceState != null)
			number = savedInstanceState.getInt("number",1);
		super.onCreate(savedInstanceState);
	}
	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt("number", number);
		super.onSaveInstanceState(outState);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (number == 0)
			number = currentnum;
		LinearLayout line = new LinearLayout(getActivity());
		line.setOrientation(LinearLayout.HORIZONTAL);
		for (int i=0; i<number; i++) {
			ImageView iv = new ImageView(getActivity());
			iv.setImageResource(R.drawable.pw_icon);
			AnimationSet animation = new AnimationSet(false);
			RotateAnimation rotate;
			if (Math.random()>.5)
				rotate = new RotateAnimation(0f,360f,RotateAnimation.RELATIVE_TO_SELF,.5f,RotateAnimation.RELATIVE_TO_SELF,.5f);
			else
				rotate = new RotateAnimation(360f,0f,RotateAnimation.RELATIVE_TO_SELF,.5f,RotateAnimation.RELATIVE_TO_SELF,.5f);
			rotate.setDuration((long)(Math.random()*2000+202));
			rotate.setRepeatCount(RotateAnimation.INFINITE);
			rotate.setRepeatMode(RotateAnimation.RESTART);
			try {
				rotate.setInterpolator(AnimationUtils.loadInterpolator(getActivity(), android.R.interpolator.linear));
			} catch (Exception e) {
				//not very elegant
			}
			animation.addAnimation(rotate);
			Animation fadein = AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in);
			if (i-1==number)
				animation.addAnimation(fadein);
			line.addView(iv);
			iv.startAnimation(animation);
			iv.setOnClickListener(this);
		}
		return line;
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE)
			getActivity().setProgressBarIndeterminateVisibility(false);
		super.onActivityResult(requestCode, resultCode, data);
	}
	static int currentnum=1;
	@SuppressWarnings("rawtypes")
	public Class getFollowUpMainAd() {
		return PinkwertherMainAd.class;
	}
	@SuppressWarnings("rawtypes")
	public Class getFollowUpBanner() {
		return this.getClass();
	}
	@SuppressWarnings("rawtypes")
	private void clickForFullScreen() {
		currentnum = number;
		Class main = getFollowUpMainAd();
		Class banner = getFollowUpBanner();
		if (main != null || banner != null)
			PinkwertherAdActivity.start(getActivity(), main, banner);
		else
			clickForLinkRedirection();
	}
	private void clickForLinkRedirection() {
		getActivity().setProgressBarIndeterminateVisibility(true);
		try {
			startActivityForResult(
					new Intent(Intent.ACTION_VIEW, 
						Uri.parse(getMarketLink())), 
					REQUEST_CODE);
		} catch (android.content.ActivityNotFoundException anfe) {
		    startActivityForResult(
		    		new Intent(Intent.ACTION_VIEW, 
		    			Uri.parse(getWebLink())),
		    		REQUEST_CODE);
		}
	}
	@Override
	public void onClick(View v) {
		if ( ! (getActivity() instanceof PinkwertherAdActivity)) {
			clickForFullScreen();
		} else {
			clickForLinkRedirection();
		}
	}
}
