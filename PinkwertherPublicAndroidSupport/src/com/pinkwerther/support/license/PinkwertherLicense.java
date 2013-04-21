package com.pinkwerther.support.license;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.UUID;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import com.pinkwerther.support.PinkwertherSupport;
import com.pinkwerther.support.R;
import com.pinkwerther.support.resources.Helper;


public abstract class PinkwertherLicense implements PinkwertherLicenseInterface {
    public final static int
    	LICENSE_NOTINITIALIZED=0,
    	LICENSE_APPROVED=1,
    	LICENSE_DENIED=2,
    	LICENSE_DELAYED=3,
    	LICENSE_DEVELVERSION=4;
    protected int mLicenseCheck=LICENSE_NOTINITIALIZED;
    
    public final static String
    	PERMISSION_NO_ADS="permission no ads";
            
    PinkwertherSupport mSupport;
    public Bundle getRecreationArguments() {
    	return null;
    }
	public void init(PinkwertherSupport support, Bundle bundle) {
		this.mSupport = support;
		if (this instanceof OnLicenseChangeListener)
			addOnLicenseChangedListener((OnLicenseChangeListener)this);
		if (support instanceof OnLicenseChangeListener)
			addOnLicenseChangedListener((OnLicenseChangeListener)support);
		if (support.getActivity() instanceof OnLicenseChangeListener)
			addOnLicenseChangedListener((OnLicenseChangeListener)support.getActivity());
		licenseCheckInBackground();
	}
	
	ArrayList<String> checkList = new ArrayList<String>();
	public String getMessage() {
		StringBuffer ret = new StringBuffer();
		ret.append("api: "+Build.VERSION.SDK_INT+"\n");
		for (String string : checkList) {
			ret.append("\n"+string);
		}
		return ret.toString();
	}
	@Override
	public void licenseCheckInBackground() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				int check = licenseCheck();
				checkList.add("check = "+check);
				checkList.add("time: "+System.currentTimeMillis());
				if (check == LICENSE_DENIED)
					licenseRetryDialog();
			}
		}).run();
	}


	@Override
	public boolean hasPermission(int rights) {
		if (licenseCheck() == LICENSE_DENIED)
			return false;
		else
			return true;
	}

	@Override
	public boolean hasPermission(String permission) {
		if (licenseCheck() == LICENSE_DENIED)
			return false;
		else
			return true;
	}
	
	/**
	 * the corresponding PinkwertherActivity, PinkwertherSupport and PinkwertherLicense are
	 * added automatically if they implement OnLicenseChangeListener
	 * @param listener
	 */
    public void addOnLicenseChangedListener(OnLicenseChangeListener listener) {
    	changeListeners.add(listener);
    }
    private ArrayList<OnLicenseChangeListener> changeListeners = new ArrayList<OnLicenseChangeListener>();
    protected void changeLicense(int license) {
    	this.mLicenseCheck = license;
		Handler handler = mSupport.getHandler();
    	for (OnLicenseChangeListener listener : changeListeners) {
    		final OnLicenseChangeListener caller = listener;
    		handler.post(new Runnable() {
    			@Override
    			public void run() {
    				caller.licenseChange(mLicenseCheck);
    			}
    		});
    	}
    }
    
    int count=0;
    protected void licenseRetryDialog() {
    	if (count>4) {
    		changeLicense(LICENSE_DENIED);
    		contactOnLicenseFailDialog();
    		return;
    	}
    	count++;
    	try {
    		mSupport.getHandler().post(new Runnable() {
    			@Override
    			public void run() {
    				DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
    					@Override
    					public void onClick(DialogInterface dialog, int which) {
    						switch (which){
    						case DialogInterface.BUTTON_POSITIVE:
    							changeLicense(LICENSE_NOTINITIALIZED);
    							licenseCheckInBackground();
    							break;

    						case DialogInterface.BUTTON_NEGATIVE:
    							changeLicense(LICENSE_DENIED);
    							if (count>0)
        			    			contactOnLicenseFailDialog();
    							break;
    						}
    					}
    				};

    				AlertDialog.Builder builder = new AlertDialog.Builder(mSupport.getActivity());
    				builder
    					.setMessage(R.string.license_retry)
    					.setPositiveButton(android.R.string.yes, dialogClickListener)
    					.setNegativeButton(android.R.string.no, dialogClickListener)
    					.setCancelable(false)
    					.show();

    			}
    		});
    	} catch (Exception e0) {
    		try {
    			checkList.add(e0.toString());
    			changeLicense(LICENSE_DENIED);
    			contactOnLicenseFailDialog();
    		} catch (Exception e1) {
    			if (mSupport.DEVEL())
    				e1.printStackTrace();
    		}
    	}
    }
    boolean noMoreContact = false;
	private void contactOnLicenseFailDialog() {
		if (noMoreContact)
			return;
		else
			noMoreContact = true;
    	try {
    		mSupport.getHandler().post(new Runnable() {
    			@Override
    			public void run() {
    				DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
    					@Override
    					public void onClick(DialogInterface dialog, int which) {
    						switch (which){
    						case DialogInterface.BUTTON_POSITIVE:
    							Context context = mSupport.getActivity();
    							
    							Helper.sendMail(context, R.string.support_mail, 
    									context.getApplicationInfo().packageName+": "+context.getString(R.string.license_failed_subject),
    									getMessage());
    							dialog.cancel();
    							break;

    						case DialogInterface.BUTTON_NEGATIVE:
    							dialog.cancel();
    							break;
    						}
    					}
    				};

    				AlertDialog.Builder builder = new AlertDialog.Builder(mSupport.getActivity());
    				builder
    					.setMessage(R.string.license_contact)
    					.setPositiveButton(android.R.string.yes, dialogClickListener)
    					.setNegativeButton(android.R.string.no, dialogClickListener)
    					.show();

    			}
    		});
    	} catch (NullPointerException e) {
    		if (mSupport.DEVEL())
    			e.printStackTrace();
    	}
	}
	
	
    private static String sID = null;
    private static final String INSTALLATION = "INSTALLATION";
    synchronized static String id(Context context) {
        if (sID == null) {  
            File installation = new File(context.getFilesDir(), INSTALLATION);
            try {
                if (!installation.exists())
                    writeInstallationFile(installation);
                sID = readInstallationFile(installation);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return sID;
    }
    private static String readInstallationFile(File installation) throws IOException {
        RandomAccessFile f = new RandomAccessFile(installation, "r");
        byte[] bytes = new byte[(int) f.length()];
        f.readFully(bytes);
        f.close();
        return new String(bytes);
    }

    private static void writeInstallationFile(File installation) throws IOException {
        FileOutputStream out = new FileOutputStream(installation);
        String id = UUID.randomUUID().toString();
        out.write(id.getBytes());
        out.close();
    }
}
