package com.pinkwerther.support;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.pinkwerther.support.activities.PinkwertherActivityInterface;
import com.pinkwerther.support.ads.PinkwertherAdsInterface;
import com.pinkwerther.support.license.PinkwertherLicenseInterface;
import com.pinkwerther.support.resources.PinkwertherResourceManager;
import com.pinkwerther.support.tracking.PinkwertherTrackingInterface;
import com.pinkwerther.support.tracking.TrackingEvent;

public class PinkwertherSupport {
	public final static String TAG = "PinkwertherSupportFragment";
	
	public boolean DEVEL() {
		return PinkwertherActivityInterface.DEVEL;
	}
	
	public Activity getActivity() {
		return (Activity) mPinkwertherActivity;
	}
	
	public void init(PinkwertherActivityInterface pw) {
		mPinkwertherActivity = pw;
		mHandler = new Handler();
		mRM = mPinkwertherActivity.getPinkwertherResourceManager();
		mLicense = mPinkwertherActivity.getPinkwertherLicense();
		mAds = mPinkwertherActivity.getPinkwertherAds();
		mTracking = mPinkwertherActivity.getPinkwertherTracking();
		new Thread(new Runnable(){
			@Override
			public void run() {
				showLoadingBar(true);
				
				if (mRM != null)
					mRM.init(PinkwertherSupport.this);
				
				if (mAds != null) {
					mAds.init(PinkwertherSupport.this);
/*
					mHandler.post(new Runnable(){
						@Override
						public void run() {
*/
							Fragment ads = mAds.getFragment();
							if (ads != null)
								mPinkwertherActivity.setAdFragment(ads);
							else
								mPinkwertherActivity.setAdView(mAds.getView());
/*
						}
					});
*/
				}
				
				if (mLicense != null)
					mLicense.init(PinkwertherSupport.this);
				
				if (mTracking != null)
					mTracking.init(PinkwertherSupport.this);
				
				showLoadingBar(false);
				
				mPinkwertherActivity.onFinishedInitialization();
			}
		}).run();
	}
	
	private PinkwertherActivityInterface mPinkwertherActivity;
	
	private Handler mHandler;
	public Handler getHandler() {
		return mHandler;
	}
	
	private ProgressDialog mLoadingWheelDialog;
	private ArrayList<String> mLoadingWheelCallers=new ArrayList<String>();
	/** Be careful, is stopped only if called by the same Class again */
	public void showLoadingWheel(boolean loading) {
		StackTraceElement[] stack = Thread.currentThread().getStackTrace();
		String name = stack[1].getClassName();
		if (loading) {
			mLoadingWheelCallers.add(name);
		} else {
			if ( ! mLoadingWheelCallers.remove(name) && PinkwertherActivityInterface.DEVEL)
				Log.e(TAG,name+" can not be removed from mLoadingWheelCallers");
		}
		if (loading && ((mLoadingWheelDialog==null) || (!mLoadingWheelDialog.isShowing())) ) {
			Activity activity = (Activity)mPinkwertherActivity;
			mLoadingWheelDialog = ProgressDialog.show(activity, "", 
		            activity.getString(R.string.loading), true);
		}
		else if (!loading && mLoadingWheelCallers.isEmpty()) {
			if ((mLoadingWheelDialog != null) && (mLoadingWheelDialog.isShowing())) {
				mLoadingWheelDialog.cancel();
				mLoadingWheelDialog = null;
			}
		}
	}
	
	private ArrayList<String> mLoadingBarCallers = new ArrayList<String>();
	/** Be careful, is stopped only if called by the same Class again */
	public void showLoadingBar(boolean loading) {
		StackTraceElement[] stack = Thread.currentThread().getStackTrace();
		String name = stack[1].getClassName();
		if (loading) {
			mLoadingBarCallers.add(name);
		} else {
			if ( ! mLoadingBarCallers.remove(name) && PinkwertherActivityInterface.DEVEL)
				Log.e(TAG,name+" can not be removed from mLoadingBarCallers");
		}
		if (loading || mLoadingBarCallers.isEmpty()) {
	    	mPinkwertherActivity.setSupportProgressBarIndeterminate(loading);
			mPinkwertherActivity.setSupportProgressBarIndeterminateVisibility(loading);
		}
	}
	
	public void showLoading(boolean loadingWheelInBar, boolean loading) {
		if (loadingWheelInBar) {
			showLoadingBar(loading);
		} else {
			showLoadingWheel(loading);
		}
	}
	
	public void run(Runnable runner) {
		run(runner,true,false,null);
	}
	public void run(Runnable runner, OnFinishedListener listener) {
		run(runner,true,true,listener);
	}
	public void run(final Runnable runner, final boolean loadingWheelInBar, boolean background, final OnFinishedListener listener) {
		if (background) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					showLoading(loadingWheelInBar, true);
					runner.run();
					showLoading(loadingWheelInBar, false);
					if (listener != null)
						listener.finished();
				}
			}).run();
		} else {
			showLoading(loadingWheelInBar, true);
			runner.run();
			showLoading(loadingWheelInBar, false);
			if (listener != null)
				listener.finished();
		}
	}
	
	private PinkwertherResourceManager mRM;
	private PinkwertherAdsInterface mAds;
	private PinkwertherLicenseInterface mLicense;
	private PinkwertherTrackingInterface mTracking;
	
	public boolean addFragment(Object object, String tag) {
		if ( !(object instanceof Fragment) )
			return false;
		Fragment fragment = (Fragment) object;
		FragmentManager fm = null;
		if (mPinkwertherActivity instanceof FragmentActivity) {
			fm = ((FragmentActivity)mPinkwertherActivity).getSupportFragmentManager();
		} else if (mPinkwertherActivity instanceof FragmentActivity) {
			fm = ((FragmentActivity)mPinkwertherActivity).getSupportFragmentManager();
		}
		if (fm != null) {
			fm.beginTransaction().add(fragment, tag).commitAllowingStateLoss();
			return true;
		} else
			return false;
	}
	
	public void trackStuff(boolean doTracking) {
		if (doTracking && mTracking==null)
			mTracking = mPinkwertherActivity.getPinkwertherTracking();
	}
	public void track(TrackingEvent event) {
		if (mTracking != null) {
			mTracking.track(event);
		}
	}
	
	public boolean hasPermission(int rights) {
		if (mLicense != null)
			return mLicense.hasPermission(rights);
		else
			return false;
	}
	public boolean hasPermission(String right) {
		if (mLicense != null)
			return mLicense.hasPermission(right);
		else
			return false;
	}
	
	public void destroy() {
		if (mRM != null)
			mRM.destroy();
		if (mAds != null)
			mAds.destroy();
		if (mLicense != null)
			mLicense.destroy();
		if (mTracking != null)
			mTracking.destroy();
	}
	
}
