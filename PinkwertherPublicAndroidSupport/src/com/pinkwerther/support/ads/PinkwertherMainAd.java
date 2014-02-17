package com.pinkwerther.support.ads;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.pinkwerther.support.R;
import com.pinkwerther.support.fragment.PinkwertherSubstantialFragment;

public class PinkwertherMainAd extends PinkwertherSubstantialFragment implements OnClickListener {

	private final static int REQUEST_CODE=22;
	public String getMarketLink() {
		return "market://search?q=pub:pinkwerther.com";
	}
	public String getWebLink() {
		return "http://play.google.com/store/apps/developer?id=pinkwerther.com";
	}
	@Override
	public void onClick(View arg0) {
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.default_main_ad, container, false);
		pwIcon = (ImageView)view.findViewById(R.id.pwIcon);
		view.setOnClickListener(this);
		animatePWIcon();
		return view;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE)
			getActivity().setProgressBarIndeterminateVisibility(false);
		super.onActivityResult(requestCode, resultCode, data);
	}
}
