package com.sloy.kcy4twicca;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class MyApplication extends Application {

	private SharedPreferences mPrefs;
	private String mUser;
	private String mKey;

	public SharedPreferences getPrefs() {
		return mPrefs;
	}

	public String getUser() {
		return mUser;
	}

	public void setUser(String user) {
		mUser = user;
	}

	public String getKey() {
		return mKey;

	}

	public void setKey(String key) {
		mKey = key;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		reload();
	}
	
	public void reload(){
		mUser=mPrefs.getString("user", "");
		mKey=mPrefs.getString("key", "");
	}

}
