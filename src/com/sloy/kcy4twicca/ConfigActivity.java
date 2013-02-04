package com.sloy.kcy4twicca;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

public class ConfigActivity extends Activity {

	private SharedPreferences prefs;
	private EditText mUser, mKey;
	private Button mGuardar;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		setContentView(R.layout.main);

		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		mUser = (EditText)findViewById(R.id.config_edit_usuario);
		mKey = (EditText)findViewById(R.id.config_edit_key);
		mGuardar = (Button)findViewById(R.id.config_bt_guardar);

		/* Intenta coger los valores */
		mUser.setText(prefs.getString("user", ""));
		mKey.setText(prefs.getString("key", ""));

		mGuardar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				prefs.edit().putString("user", mUser.getText().toString().trim()).putString("key", mKey.getText().toString().trim()).commit();
				finish();
			}
		});
	}
}