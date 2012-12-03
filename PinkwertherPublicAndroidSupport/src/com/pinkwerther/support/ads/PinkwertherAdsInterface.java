package com.pinkwerther.support.ads;

import android.support.v4.app.Fragment;
import android.view.View;

import com.pinkwerther.support.PinkwertherSubstantialInterface;

public interface PinkwertherAdsInterface extends PinkwertherSubstantialInterface {
	public View getView();
	public Fragment getFragment();
}
