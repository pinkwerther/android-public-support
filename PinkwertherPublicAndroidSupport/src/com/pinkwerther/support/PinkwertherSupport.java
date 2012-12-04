package com.pinkwerther.support;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.pinkwerther.support.activities.PinkwertherActivityInterface;
import com.pinkwerther.support.ads.PinkwertherAds;
import com.pinkwerther.support.fragment.PinkwertherSubstantialFragment;
import com.pinkwerther.support.license.PinkwertherLicenseInterface;
import com.pinkwerther.support.resources.PinkwertherResourceManager;
import com.pinkwerther.support.tracking.PinkwertherTrackingInterface;
import com.pinkwerther.support.tracking.TrackingEvent;

public class PinkwertherSupport extends Fragment {
	public final static String TAG = "PinkwertherSupportFragment";
	
	public boolean DEVEL() {
		return PinkwertherActivityInterface.DEVEL;
	}
	
	@Override
	public void onAttach(Activity activity) {
		if (activity instanceof PinkwertherActivityInterface)
			mPinkwertherActivity = (PinkwertherActivityInterface)activity;
		super.onAttach(activity);
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			ArrayList<String> subs = savedInstanceState.getStringArrayList("mSubFrags");
			for (String name : subs) {
				try {
					mSubFrags.add((PinkwertherSubstantialFragment)Class.forName(name).newInstance());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		if (mPinkwertherActivity == null) {
			if (getActivity() instanceof PinkwertherActivityInterface)
				mPinkwertherActivity = (PinkwertherActivityInterface)getActivity();
			else
				return;
		}
		super.onCreate(savedInstanceState);
	}	
	
	@Override
	public void onSaveInstanceState(Bundle bundle) {
		ArrayList<String> subs = new ArrayList<String>();
		for (PinkwertherSubstantialFragment pwFrag : mSubFrags) {
			subs.add(pwFrag.getClass().getCanonicalName());
		}
		bundle.putStringArrayList("mSubFrags", subs);
		super.onSaveInstanceState(bundle);
	}
	
	ArrayList<PinkwertherSubstantialFragment> mSubFrags = new ArrayList<PinkwertherSubstantialFragment>();
	public void replaceMainFragment(PinkwertherSubstantialFragment pwFrag, boolean backstacked) {
		FragmentManager fm = mPinkwertherActivity.getSupportFragmentManager();
		if (backstacked)
			mSubFrags.add((PinkwertherSubstantialFragment)fm.findFragmentById(R.id.main));
		fm.beginTransaction().replace(R.id.main, pwFrag).commit();
	}
	private boolean mCancelableByBackPress=true;
	public void setCancelOnFinalBackPress(boolean cancel) {
		mCancelableByBackPress = cancel;
	}
	public void backPressed() {
		if (mSubFrags.isEmpty()) {
			if (mCancelableByBackPress)
				mPinkwertherActivity.finishPinkwertherActivity();
			return;
		}
		PinkwertherSubstantialFragment pwFrag = mSubFrags.remove(mSubFrags.size()-1);
		mPinkwertherActivity.getSupportFragmentManager().beginTransaction().
			replace(R.id.main, pwFrag).commit();
	}
	
	private boolean mInitialized = false;
	public boolean initialized() {
		return mInitialized;
	}
	
	ArrayList<OnFinishedListener> mInitListeners = new ArrayList<OnFinishedListener>();
	public void addOnInitializedListener(OnFinishedListener listener) {
		if (mInitialized)
			listener.finished();
		else 
			mInitListeners.add(listener);
	}
		
	private void setInitialFragments() {
		if (mSubstantialFragment == null) {
			mSubstantialFragment = (PinkwertherSubstantialFragment)mPinkwertherActivity.getSupportFragmentManager().
					findFragmentById(R.id.main);
			if (mSubstantialFragment == null) {
				mSubstantialFragment = mPinkwertherActivity.getInitialMainFragment();
				mPinkwertherActivity.getSupportFragmentManager().
					beginTransaction().add(R.id.advertisement, mSubstantialFragment).commit();
			}
		}
	}
	
	public void onStart() {
		
		mHandler = new Handler();
		mRM = mPinkwertherActivity.getPinkwertherResourceManager();
		mLicense = mPinkwertherActivity.getPinkwertherLicense();
		mTracking = mPinkwertherActivity.getPinkwertherTracking();
		mAds = mPinkwertherActivity.getPinkwertherAds();
		
		new Thread(new Runnable(){
			@Override
			public void run() {
				showLoadingBar(true);
				
				if (mRM != null)
					mRM.init(PinkwertherSupport.this);
				
				if (mAds != null)
					mAds.init(PinkwertherSupport.this);
								
				if (mLicense != null)
					mLicense.init(PinkwertherSupport.this);
				
				if (mTracking != null)
					mTracking.init(PinkwertherSupport.this);
				
				setInitialFragments();
				
				showLoadingBar(false);
				
				mInitialized = true;
				mPinkwertherActivity.onFinishedInitialization();
				for (OnFinishedListener listener : mInitListeners)
					listener.finished();
				mInitListeners.clear();
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
	private PinkwertherAds mAds;
	private PinkwertherLicenseInterface mLicense;
	private PinkwertherTrackingInterface mTracking;
	private PinkwertherSubstantialFragment mSubstantialFragment;
		
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
	@Override
	public void onDestroy() {
		if (mRM != null)
			mRM.destroy();
		if (mAds != null)
			mAds.destroy();
		if (mLicense != null)
			mLicense.destroy();
		if (mTracking != null)
			mTracking.destroy();
		mRM = null;
		mAds = null;
		mLicense = null;
		mTracking = null;
		super.onDestroy();
	}
	
}
