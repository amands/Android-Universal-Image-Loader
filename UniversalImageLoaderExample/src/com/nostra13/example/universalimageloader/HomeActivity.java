package com.nostra13.example.universalimageloader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class HomeActivity extends BaseActivity {

	private String[] imageUrls;
	private ProgressDialog dialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_home);

		/* AdMob Start */
		AdView adView = new AdView(this, AdSize.BANNER, Extra.MY_AD_UNIT_ID);
		LinearLayout layout = (LinearLayout) findViewById(R.id.llHomeAd);
		layout.addView(adView);
		adView.loadAd(new AdRequest());
		/* AdMob End */

		dialog = ProgressDialog.show(this, "Loading Images From Server....", "Please wait",
				true, false);

		new GetData().execute(""); // GetUserStatus();

		((Button) findViewById(R.id.btnAhJokes)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeActivity.this, JokeActivity.class);
				intent.putExtra(Extra.IMAGES, imageUrls);
				startActivity(intent);
			}
		});

	}

	public void onImageGridClick(View view) {
		Intent intent = new Intent(this, ImageGridActivity.class);
		intent.putExtra(Extra.IMAGES, imageUrls);
		startActivity(intent);
	}

	public class GetData extends AsyncTask<String, Integer, String> {

		String result;
		JSONArray array;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			System.out.println("HomeActivity.GetData.doInBackground()");

			String url = Extra.url + "getImages.php?do=submit";
			System.out.println("Splash screen URL::" + url);
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpGet httpget = new HttpGet(url);
				HttpResponse response = httpclient.execute(httpget);
				HttpEntity entity = response.getEntity();
				InputStream is = entity.getContent();

				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is, "iso-8859-1"), 8);
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line);
				}
				is.close();
				System.out.println("Output::" + sb.toString());
				if (!(sb.toString()).equals("[Form not submitted]")) {
					array = new JSONArray(sb.toString());
					imageUrls = new String[array.length()];
					for (int i = 0; i < array.length(); i++) {
						imageUrls[i] = array.getString(i);
					}
				} else {
					System.out.println("Phone is not registered");
				}

			} catch (Exception e) {
				System.out.println("In Catch ");
				e.printStackTrace();
			}
			return "";
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			dialog.dismiss();

		}
	}

}