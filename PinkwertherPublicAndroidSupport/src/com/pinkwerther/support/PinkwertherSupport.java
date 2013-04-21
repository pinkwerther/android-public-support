package com.pinkwerther.support;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
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
	
	public FragmentManager getSupportFragmentManager() {
		return mPinkwertherActivity.getSupportFragmentManager();
	}
	
	@Override
	public void onAttach(Activity activity) {
		if (activity instanceof PinkwertherActivityInterface)
			mPinkwertherActivity = (PinkwertherActivityInterface)activity;
		super.onAttach(activity);
	}
		
	private final static String
		RM_BUNDLE="RMBundle",
		TRACKING_BUNDLE="TrackingBundle",
		LICENSE_BUNDLE="LicenseBundle",
		ADS_BUNDLE="AdsBundle";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			int i=0;
			Bundle bundle=savedInstanceState.getBundle(PW_SUB_BUNDLE+i);
			while (bundle != null) {
				mSubBundles.add(bundle);
				i++;
				bundle = savedInstanceState.getBundle(PW_SUB_BUNDLE+i);
			}
			RMBundle = savedInstanceState.getBundle(RM_BUNDLE);
			TrackingBundle = savedInstanceState.getBundle(TRACKING_BUNDLE);
			LicenseBundle = savedInstanceState.getBundle(LICENSE_BUNDLE);
			AdsBundle = savedInstanceState.getBundle(ADS_BUNDLE);
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
		if (mSubBundles.size() > 0) {
			for (int i=0; i<mSubBundles.size(); i++) {
				bundle.putBundle(PW_SUB_BUNDLE+i, mSubBundles.get(i));
			}
		}
		if (mRM != null)
			bundle.putBundle(RM_BUNDLE, mRM.getRecreationArguments());
		if (mTracking != null)
			bundle.putBundle(TRACKING_BUNDLE, mTracking.getRecreationArguments());
		if (mLicense != null)
			bundle.putBundle(LICENSE_BUNDLE, mLicense.getRecreationArguments());
		if (mAds != null)
			bundle.putBundle(ADS_BUNDLE,mAds.getRecreationArguments());
		
		super.onSaveInstanceState(bundle);
	}
	
	private final static String PW_SUB_FRAG_CLASS_NAME="pwSubFragClassName";
	private final static String PW_SUB_BUNDLE="pwSubBundle";
	ArrayList<Bundle> mSubBundles = new ArrayList<Bundle>();
	public void replaceMainFragment(Fragment pwFrag) {
		FragmentManager fm = mPinkwertherActivity.getSupportFragmentManager();
		PinkwertherSubstantialFragment pwSub = (PinkwertherSubstantialFragment)fm.findFragmentById(R.id.main);
		if (pwSub != null && pwSub.isBackWorthy()) {
			Bundle bundle = pwSub.getRecreationArguments();
			bundle.putString(PW_SUB_FRAG_CLASS_NAME, pwSub.getClass().getCanonicalName());
			mSubBundles.add(bundle);
		}
		fm.beginTransaction().replace(R.id.main, pwFrag).commit();
	}
	private boolean mCancelableByBackPress=true;
	public void setCancelOnFinalBackPress(boolean cancel) {
		mCancelableByBackPress = cancel;
	}
	public void backPressed() {
		if (mSubBundles.isEmpty()) {
			if (mCancelableByBackPress)
				mPinkwertherActivity.finishPinkwertherActivity();
			return;
		}
		Bundle bundle = mSubBundles.remove(mSubBundles.size()-1);
		PinkwertherSubstantialFragment pwFrag;
		try {
			pwFrag = (PinkwertherSubstantialFragment) Class.forName(bundle.getString(PW_SUB_FRAG_CLASS_NAME)).newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		pwFrag.setArguments(bundle);
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
	
	private boolean stillRunning = false;
	private Bundle RMBundle,AdsBundle,LicenseBundle,TrackingBundle;
	
	public void onStart() {
		
		super.onStart();
		
		if (stillRunning) {
			mSubstantialFragment = (PinkwertherSubstantialFragment)getFragmentManager().findFragmentById(R.id.main);
			return;
		} else
			stillRunning = true;
		
		mHandler = new Handler();
		mRM = mPinkwertherActivity.getPinkwertherResourceManager();
		mLicense = mPinkwertherActivity.getPinkwertherLicense();
		mTracking = mPinkwertherActivity.getPinkwertherTracking();
		mAds = mPinkwertherActivity.getPinkwertherAds();

		mSubstantialFragment = mPinkwertherActivity.getInitialMainFragment();
		if (mSubstantialFragment != null)
			mPinkwertherActivity.getSupportFragmentManager().
				beginTransaction().replace(R.id.main, mSubstantialFragment).commit();

		showLoadingBar(true);
		new Thread(new Runnable(){
			@Override
			public void run() {
				
				if (mAds != null)
					mAds.init(PinkwertherSupport.this,AdsBundle);
				AdsBundle = null;
				
				if (mRM != null)
					mRM.init(PinkwertherSupport.this,RMBundle);
				RMBundle = null;

				if (mLicense != null)
					mLicense.init(PinkwertherSupport.this,LicenseBundle);
				LicenseBundle = null;
				
				if (mTracking != null)
					mTracking.init(PinkwertherSupport.this,TrackingBundle);
				TrackingBundle = null;
				
				mInitialized = true;
				mPinkwertherActivity.onFinishedInitialization();
				for (OnFinishedListener listener : mInitListeners)
					listener.finished();
				mInitListeners.clear();
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						showLoadingBar(false);
					}
				});
			}
		}).start();
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
	    	//mPinkwertherActivity.setSupportProgressBarIndeterminate(loading);
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
