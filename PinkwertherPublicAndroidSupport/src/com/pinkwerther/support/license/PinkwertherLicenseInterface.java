package com.pinkwerther.support.license;

import com.pinkwerther.support.PinkwertherSubstantialInterface;

public interface PinkwertherLicenseInterface extends PinkwertherSubstantialInterface {

	public boolean hasPermission(int rights);
	public boolean hasPermission(String permission);
	public int licenseCheck();
	public void licenseCheckInBackground();
	public String getMessage();
}
