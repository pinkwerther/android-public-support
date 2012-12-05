package com.pinkwerther.support.ads;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public abstract class PinkwertherImageBanner extends PinkwertherBanner {
	public abstract int getImageResId();
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ImageView iv = new ImageView(getActivity());
		iv.setImageResource(getImageResId());
		iv.setOnClickListener(this);
		return iv;
	}
}
