package com.sloy.kcy4twicca;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TransformActivity extends Activity {

	private MyApplication mApp;
	private Intent mIntent;
	private ImageButton btCancel;
	private String mTweet;
	private Shorter mShorter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, WindowManager.LayoutParams.FLAG_BLUR_BEHIND);

		mIntent = getIntent();
		mApp = (MyApplication)getApplication();
		mTweet = mIntent.getStringExtra(Intent.EXTRA_TEXT);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mApp.reload();
		if(mApp.getUser() == null || mApp.getKey() == null || mApp.getUser().equals("") || mApp.getKey().equals("")){
			// No está configurado
			Toast.makeText(this, "Debes configurar un usuario y contraseña", Toast.LENGTH_LONG).show();
			setScreenContentConfigurate();
		}else{
			// Sí está, convierte
			setScreenContentTransform();
			new Shorter().execute();
		}
	}

	private void setScreenContentTransform() {
		setContentView(R.layout.transform);

		btCancel = (ImageButton)findViewById(R.id.trans_bt_cancel);

		btCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mShorter != null){
					mShorter.cancel(true);
				}
				finish();
			}
		});
	}

	private void setScreenContentConfigurate() {
		setContentView(R.layout.transform_config);
		Button config = (Button)findViewById(R.id.bt_configurar);
		config.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(TransformActivity.this, ConfigActivity.class));
			}
		});
	}

	private static String acortar(String enlace, String user, String key) throws IOException {
		URL url = new URL("http://kcy.me/api/?u=" + user + "&key=" + key + "&url=" + enlace);
		HttpURLConnection c = (HttpURLConnection)url.openConnection();
		c.setRequestMethod("GET");
		c.setReadTimeout(15 * 1000);
		c.setUseCaches(false);
		c.connect();
		// read the output from the server
		BufferedReader reader = new BufferedReader(new InputStreamReader(c.getInputStream()));
		StringBuilder stringBuilder = new StringBuilder();
		String line = null;
		while((line = reader.readLine()) != null){
			stringBuilder.append(line);
		}
		String fuck = stringBuilder.toString();
		return fuck;
	}

	private enum Result{
		OK, ERROR, NOLINKS;
	}
	
	private class Shorter extends AsyncTask<Void, Void, Result> {

		@Override
		protected Result doInBackground(Void... params) {
			/* Saca la lista de enlaces */
			List<String> enlaces = new ArrayList<String>();
			int start = -1;
			int end = -1;
			while(true){
				start = mTweet.indexOf("http", end);
				if(start < 0){
					break;
				}
				end = mTweet.indexOf(" ", start);
				if(end < 0){
					end = mTweet.length();
				}
				String url = (end > 0) ? mTweet.substring(start, end) : mTweet.substring(start);
				enlaces.add(url);
			}
			/* Los acorta todos */
			if(enlaces.size()==0){
				//No hay enlaces
				return Result.NOLINKS;
			}
			List<String> kcy = new ArrayList<String>();
			for(String url : enlaces){
				try{
					kcy.add(acortar(url, mApp.getUser(), mApp.getKey()));
				}catch(Exception e){
					Log.e("kcy.me", "Error al acortar", e);
				}
			}

			/* Una vez acortados todos los sustituye si no ha habido errores */
			if(kcy.size() != enlaces.size()){
				//error
				return Result.ERROR;
			}else{
				//ok
				for(int i = 0; i < enlaces.size(); i++){
					mTweet = mTweet.replace(enlaces.get(i), kcy.get(i));
				}
				return Result.OK;
			}
		}

		@Override
		protected void onPostExecute(Result result) {
			switch (result){
				case OK:
					// Devuelve el tweet acortado
					mIntent.putExtra(Intent.EXTRA_TEXT, mTweet);
					setResult(RESULT_OK, mIntent);
					finish();
					break;
				case ERROR:
					// Ha habido algún error :(
					Toast.makeText(TransformActivity.this, "Error al acortar :(\nComprueba la configuraci�n", Toast.LENGTH_LONG).show();
					setResult(RESULT_CANCELED);
					finish();
					break;
				case NOLINKS:
					// No había enlaces
					Toast.makeText(TransformActivity.this, "No se detectaron enlaces", Toast.LENGTH_LONG).show();
					setResult(RESULT_CANCELED);
					finish();
					break;
			}
		}

		@Override
		protected void onCancelled() {
			setResult(RESULT_CANCELED);
		}

	}

}
