package com.nostra13.example.universalimageloader;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */

public class ImageGridActivity extends BaseActivity implements OnClickListener {

	private static final int maxImages = 9;

	private ImageButton btnNext, btnPrev, btnUpload;
	private GridView gridView;

	private String[] showUrls;

	private int pos = 1;

	private DisplayImageOptions options;

	private SharedPreferences settings;
	private Editor editor;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_image_grid);

		/* AdMob Start */
		AdView adView = new AdView(this, AdSize.BANNER, Extra.MY_AD_UNIT_ID);
		LinearLayout layout = (LinearLayout) findViewById(R.id.llIgAd);
		layout.addView(adView);
		adView.loadAd(new AdRequest());
		/* AdMob End */

		btnNext = (ImageButton) findViewById(R.id.btnIgNext);
		btnPrev = (ImageButton) findViewById(R.id.btnIgPrev);
		btnUpload = (ImageButton) findViewById(R.id.btnIgUpload);

		showUrls = new String[maxImages];

		settings = getPreferences(0);
		editor = settings.edit();
		
		pos = settings.getInt("pos", 1);


		for (int i = (pos - 1) * maxImages, j = 0; i < maxImages * pos; i++, j++) {

			try {
				showUrls[j] = Extra.imageUrls[i];
			} catch (Exception e) {
				if ((j == 0) && (i % 9 == 0)) {
					pos = 1;
					
					editor.putInt("pos", 1);
					editor.commit();
					btnPrev.performClick();
				}
				e.printStackTrace();
				showUrls[j] = "";
			}
		}

		options = new DisplayImageOptions.Builder().showStubImage(R.drawable.stub_image)
				.showImageForEmptyUrl(R.drawable.image_for_empty_url).cacheInMemory()
				.cacheOnDisc().build();

		gridView = (GridView) findViewById(R.id.gridview);
		gridView.setAdapter(new ImageAdapter());
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				startImageGalleryActivity(position);
			}
		});

		btnNext.setOnClickListener(this);
		btnPrev.setOnClickListener(this);
		btnUpload.setOnClickListener(this);
	}

	@Override
	protected void onDestroy() {
		imageLoader.stop();
		super.onDestroy();
	}

	private void startImageGalleryActivity(int position) {
		Intent intent = new Intent(this, ImagePagerActivity.class);
		intent.putExtra(Extra.IMAGES, showUrls);
		intent.putExtra(Extra.IMAGE_POSITION, position);
		startActivity(intent);
	}

	public class ImageAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return showUrls.length;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView = (ImageView) convertView;
			if (imageView == null) {
				imageView = (ImageView) getLayoutInflater().inflate(R.layout.item_grid_image,
						parent, false);
			}

			imageLoader.displayImage(showUrls[position], imageView, options);

			return imageView;
		}
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btnIgNext:

			if (Extra.imageUrls.length > (maxImages * pos))
				pos++;

			for (int i = (pos - 1) * maxImages, j = 0; i < maxImages * pos; i++, j++) {
				try {
					showUrls[j] = Extra.imageUrls[i];
				} catch (Exception e) {
					showUrls[j] = "";
				}
				editor.putInt("pos", pos);
				editor.commit();

			}

			gridView.setAdapter(new ImageAdapter());
			break;
		case R.id.btnIgPrev:
			if (pos > 1)
				pos--;

			for (int i = (pos - 1) * maxImages, j = 0; i < maxImages * pos; i++, j++) {
				try {
					showUrls[j] = Extra.imageUrls[i];
				} catch (Exception e) {
					showUrls[j] = "";
				}
			}
			editor.putInt("pos", pos);
			editor.commit();
			gridView.setAdapter(new ImageAdapter());
			break;
		case R.id.btnIgUpload:
			editor.putInt("pos", pos);
			editor.commit();

			Intent intent = new Intent(ImageGridActivity.this, UploadActivity.class);
			startActivity(intent);
			break;
		}

	}

}