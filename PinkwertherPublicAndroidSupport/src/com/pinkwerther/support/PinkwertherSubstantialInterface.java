package com.pinkwerther.support;

import android.os.Bundle;

public interface PinkwertherSubstantialInterface {
	public void init(PinkwertherSupport support, Bundle bundle);
	public Bundle getRecreationArguments();
	public void destroy();
}
