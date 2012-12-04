package com.pinkwerther.support.ads;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.pinkwerther.support.R;

public class PinkwertherAdActivity extends FragmentActivity {
	public final static String MAIN_FRAGMENT="main_fragment";
	public final static String AD_FRAGMENT="ad_fragment";
	@Override
	protected void onCreate(Bundle arg0) {
		setContentView(R.layout.vertical_adbottom);
		Bundle bundle = getIntent().getExtras();
		String main = bundle.getString(MAIN_FRAGMENT);
		String ad = bundle.getString(AD_FRAGMENT);
		FragmentManager fm = getSupportFragmentManager();
		if (main != null)
			try {
				Fragment frag = (Fragment)Class.forName(main).newInstance();
				fm.beginTransaction().add(R.id.main, frag).commit();
			} catch (Exception e) {
				e.printStackTrace();
			}
		if (ad != null)
			try {
				Fragment frag = (Fragment)Class.forName(ad).newInstance();
				fm.beginTransaction().add(R.id.advertisement, frag).commit();
			} catch (Exception e) {
				e.printStackTrace();
			}
		super.onCreate(arg0);
	}
	@SuppressWarnings("rawtypes")
	public static void start(Context context, Class main, Class ad) {
		Intent intent = new Intent(context, PinkwertherAdActivity.class);
		Bundle bundle = new Bundle();
		if (main != null)
			bundle.putString(MAIN_FRAGMENT,main.getCanonicalName());
		if (ad != null)
			bundle.putString(AD_FRAGMENT, ad.getCanonicalName());
		intent.putExtras(bundle);
		context.startActivity(intent);
	}
}
