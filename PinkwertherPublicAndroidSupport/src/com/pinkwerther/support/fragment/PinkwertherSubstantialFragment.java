package com.pinkwerther.support.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.pinkwerther.support.R;

public class PinkwertherSubstantialFragment extends Fragment {
	public ImageView pwIcon;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.default_main, container, false);
		pwIcon = (ImageView)view.findViewById(R.id.pwIcon);
		animatePWIcon();
		return view;
	}
	
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		if (bundle != null) {
			x = bundle.getFloat("x");
			y = bundle.getFloat("y");
			rotation = bundle.getFloat("rotation");
		}
	}
	@Override
	public void onSaveInstanceState(Bundle bundle) {
		bundle.putFloat("x", x);
		bundle.putFloat("y", y);
		bundle.putFloat("rotation", rotation);
		super.onSaveInstanceState(bundle);
	}
	public Bundle getRecreationArguments() {
		return getArguments();
	}
	
	public boolean isBackWorthy() {
		return false;
	}
	
	private float x=0,y=0;
	private float rotation=0;
	private float rotateX=.2f,rotateY=.2f;
	byte rotateDirection=1;
	boolean stoppedRotation=true;

	public float getRandomNewPosition() {
		return (float)(Math.random()*.8-.4);
	}
	
	long rotationRate = 1000+(long)(Math.random()*1202);
	
	public void animatePWIcon() {
		AnimationSet anims = new AnimationSet(false);
		long duration = (long)(1502*Math.random()+700);
		float rotTimes = (float)duration/(float)rotationRate;
		float newRotation = rotation+
				((float)rotateDirection*(360f*rotTimes));
		float nextRotation = newRotation-(((int)(newRotation/360))*360);
		if (Math.abs(rotTimes-(int)rotTimes)<.25 && rotTimes>.5) {
			newRotation = newRotation-nextRotation;
			nextRotation=0;
		}
		RotateAnimation rotate = new RotateAnimation(rotation, newRotation, 
				RotateAnimation.RELATIVE_TO_SELF, rotateX, RotateAnimation.RELATIVE_TO_SELF, rotateY);
		if (nextRotation==0) {
			if (Math.random()>.5)
				rotateX=.7f+(float)(Math.random()*.5);
			else
				rotateX=.3f-(float)(Math.random()*.5);
			if (Math.random()>.5)
				rotateY=.7f+(float)(Math.random()*.5);
			else
				rotateY=.3f-(float)(Math.random()*.5);
		} 
		if (stoppedRotation) {
			rotate.setInterpolator(AnimationUtils.loadInterpolator(getActivity(), android.R.anim.accelerate_interpolator));
			stoppedRotation = false;
		} else if (Math.random()>.82) {
			rotateDirection *= -1;
			rotate.setInterpolator(AnimationUtils.loadInterpolator(getActivity(), android.R.anim.decelerate_interpolator));
			stoppedRotation=true;
		} else
			rotate.setInterpolator(AnimationUtils.loadInterpolator(getActivity(), android.R.anim.linear_interpolator));
		anims.addAnimation(rotate);
		rotation = nextRotation;
		
		float newX=getRandomNewPosition();
		float newY=getRandomNewPosition();
		anims.addAnimation(new TranslateAnimation(
				TranslateAnimation.RELATIVE_TO_PARENT, x, TranslateAnimation.RELATIVE_TO_PARENT, newX,
				TranslateAnimation.RELATIVE_TO_PARENT, y, TranslateAnimation.RELATIVE_TO_PARENT, newY));
		x = newX;
		y = newY;
		anims.setFillAfter(true);
		anims.setDuration(duration);
		anims.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation arg0) {
				PinkwertherSubstantialFragment.this.animatePWIcon();
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
