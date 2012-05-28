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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

public class JokeActivity extends Activity implements OnClickListener {

	private ImageButton btnNext, btnPrev, btnShare, btnAdd;
	private static TextView tvJoke;
	private int totalJoke = 10;
	private String joke;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.joke_view);

		/* AdMob Start */
		AdView adView = new AdView(this, AdSize.BANNER, Extra.MY_AD_UNIT_ID);
		LinearLayout layout = (LinearLayout) findViewById(R.id.llJvAd);
		layout.addView(adView);
		adView.loadAd(new AdRequest());
		/* AdMob End */

		btnNext = (ImageButton) findViewById(R.id.btnJvNext);
		btnPrev = (ImageButton) findViewById(R.id.btnJvPrev);
		btnShare = (ImageButton) findViewById(R.id.btnJvShare);
		btnAdd = (ImageButton) findViewById(R.id.btnJvAdd);

		tvJoke = (TextView) findViewById(R.id.tvJvJoke);

		btnNext.setOnClickListener(this);
		btnPrev.setOnClickListener(this);
		btnShare.setOnClickListener(this);
		btnAdd.setOnClickListener(this);
		tvJoke.setOnClickListener(this);

		btnNext.performClick();

		tvJoke.buildDrawingCache(false);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnJvNext:
			if (Extra.cJoke < totalJoke) {
				Extra.cJoke++;
				tvJoke.setText(getJoke("inc"));
			}
			break;
		case R.id.btnJvPrev:
			if (Extra.cJoke > 1) {
				Extra.cJoke--;
				tvJoke.setText(getJoke("dec"));
			}
			break;
		case R.id.btnJvShare:
			Intent i = new Intent(Intent.ACTION_SEND);
			i.putExtra(Intent.EXTRA_SUBJECT, "Joke");
			i.putExtra(Intent.EXTRA_TEXT, tvJoke.getText().toString());
			i.setType("text/plain");
			startActivity(Intent.createChooser(i, "Send mail"));
			break;
		case R.id.btnJvAdd:
			startActivity(new Intent(JokeActivity.this, UploadJokeActivity.class));
			break;
		case R.id.tvJvJoke:

			tvJoke.setText(joke);
			break;
		}
	}

	public String getJoke(String type) {
		JSONArray array;
		String url = Extra.url + "getJoke.php?do=submit&num=" + Extra.cJoke + "&type=" + type;
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(url);
			HttpResponse response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			InputStream is = entity.getContent();

			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"),
					8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			is.close();
			if (!(sb.toString()).equals("[Form not submitted]")) {
				array = new JSONArray(sb.toString());
				Extra.cJoke = Integer.parseInt(array.getString(0));
				totalJoke = Integer.parseInt(array.getString(2));
				joke = array.getString(1).replace("%20", " ");
			} else
				joke = "No joke to View";

		} catch (Exception e) {
			e.printStackTrace();
		}
		return joke;
	}

}
