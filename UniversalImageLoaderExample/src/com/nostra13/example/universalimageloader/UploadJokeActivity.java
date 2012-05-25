package com.nostra13.example.universalimageloader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

public class UploadJokeActivity extends Activity {

	Button Upload;
	EditText etJoke;
	private ProgressDialog dialog;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.upload_joke);

		/* AdMob Start */
		AdView adView = new AdView(this, AdSize.BANNER, Extra.MY_AD_UNIT_ID);
		LinearLayout layout = (LinearLayout) findViewById(R.id.llUjAd);
		layout.addView(adView);
		adView.loadAd(new AdRequest());
		/* AdMob End */

		etJoke = (EditText) findViewById(R.id.etUjJoke);

		Upload = (Button) findViewById(R.id.btnUjUpload);

		Upload.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog = ProgressDialog.show(UploadJokeActivity.this, "Please wait",
						"Uploading data...");
				new GetData().execute("");
			}
		});

	}

	public class GetData extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			String url = Extra.url + "addJoke.php";
			System.out.println("Splash screen URL::" + url);
			try {
				HttpPost httppost = new HttpPost(url);
				HttpClient httpclient = new DefaultHttpClient();

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
				// Adding parameters to send to the HTTP server.
				nameValuePairs.add(new BasicNameValuePair("do", "submit"));
				nameValuePairs.add(new BasicNameValuePair("joke", etJoke.getText().toString()));

				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse response = httpclient.execute(httppost);

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
			Toast.makeText(UploadJokeActivity.this, "Joke Added Successfully",
					Toast.LENGTH_SHORT).show();
			dialog.dismiss();

			finish();

		}

	}
}
