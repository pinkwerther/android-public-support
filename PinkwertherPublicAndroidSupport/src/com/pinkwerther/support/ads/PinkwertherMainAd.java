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
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;

import com.pinkwerther.support.R;
import com.pinkwerther.support.activities.PinkwertherActivityInterface;
import com.pinkwerther.support.tracking.TrackingEvent;

public class PinkwertherMainAd extends Fragment implements OnClickListener {
	View pwIcon=null;
	private final static int REQUEST_CODE=22;
	public String getMarketLink() {
		return "market://search?q=pub:pinkwerther.com";
	}
	public String getWebLink() {
		return "http://play.google.com/store/apps/developer?id=pinkwerther.com";
	}
	@Override
	public void onClick(View arg0) {
		clicked = true;
		getActivity().setProgressBarIndeterminateVisibility(true);
		if (getActivity() instanceof PinkwertherActivityInterface) {
			((PinkwertherActivityInterface)getActivity()).getPinkwertherSupport().track(
					new TrackingEvent("click","main ad",this.getClass().getSimpleName(),1));
		}
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.default_main_ad, container, false);
		pwIcon = view.findViewById(R.id.pwIcon);
		animate();
		clicked = false;
		view.setOnClickListener(this);
		return view;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE)
			getActivity().setProgressBarIndeterminateVisibility(false);
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	boolean clicked=false;
	
	private void animate() {
		if (clicked) {
			pwIcon.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));
			return;
		}
		AnimationSet anims = new AnimationSet(true);
		anims.addAnimation(new RotateAnimation(0, 360, 
				RotateAnimation.RELATIVE_TO_SELF, .5f, RotateAnimation.RELATIVE_TO_SELF, .5f));
		anims.addAnimation(new TranslateAnimation(TranslateAnimation.RELATIVE_TO_SELF, 0f, TranslateAnimation.RELATIVE_TO_PARENT, (float)Math.random(),
				TranslateAnimation.RELATIVE_TO_SELF, 0f, TranslateAnimation.RELATIVE_TO_PARENT, (float)Math.random()));
		anims.setFillAfter(true);
		anims.setDuration((long)(2000*Math.random()+202));
		anims.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation arg0) {
				PinkwertherMainAd.this.animate();
			}
			@Override
			public void onAnimationRepeat(Animation arg0) {
			}
			@Override
			public void onAnimationStart(Animation arg0) {
			}
		});
		pwIcon.startAnimation(anims);
	}
}
