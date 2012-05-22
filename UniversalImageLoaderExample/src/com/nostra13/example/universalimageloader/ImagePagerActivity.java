package com.nostra13.example.universalimageloader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.nostra13.universalimageloader.core.DecodingType;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoadingListener;

/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class ImagePagerActivity extends BaseActivity implements OnClickListener {

	private ViewPager pager;
	private Button btnBack, btnPublish, btnSave, btnNext, btnPrev;
	private Bitmap bmImg;
	private int pagerPosition;
	private String[] imageUrls;
	private DisplayImageOptions options;
	private ProgressDialog dialog;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.ac_image_pager);
		
		/* AdMob Start */
		AdView adView = new AdView(this, AdSize.BANNER, Extra.MY_AD_UNIT_ID);
		LinearLayout layout = (LinearLayout) findViewById(R.id.llIpAd);
		layout.addView(adView);
		adView.loadAd(new AdRequest());
		/* AdMob End */

		btnBack = (Button) findViewById(R.id.btnIpBack);
		btnPublish = (Button) findViewById(R.id.btnIpPublish);
		btnSave = (Button) findViewById(R.id.btnIpSave);
		btnNext = (Button) findViewById(R.id.btnIpNext);
		btnPrev = (Button) findViewById(R.id.btnIpPrev);

		btnBack.setOnClickListener(this);
		btnPublish.setOnClickListener(this);
		btnSave.setOnClickListener(this);
		btnNext.setOnClickListener(this);
		btnPrev.setOnClickListener(this);

		Bundle bundle = getIntent().getExtras();
		imageUrls = bundle.getStringArray(Extra.IMAGES);
		pagerPosition = bundle.getInt(Extra.IMAGE_POSITION, 0);

		options = new DisplayImageOptions.Builder()
				.showImageForEmptyUrl(R.drawable.image_for_empty_url).cacheOnDisc()
				.decodingType(DecodingType.MEMORY_SAVING).build();

		pager = (ViewPager) findViewById(R.id.pager);
		pager.setAdapter(new ImagePagerAdapter(imageUrls));
		pager.setCurrentItem(pagerPosition);
	}

	private class ImagePagerAdapter extends PagerAdapter {

		private String[] images;
		private LayoutInflater inflater;

		ImagePagerAdapter(String[] images) {
			this.images = images;
			inflater = getLayoutInflater();
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewPager) container).removeView((View) object);
		}

		@Override
		public void finishUpdate(View container) {
		}

		@Override
		public int getCount() {
			return images.length;
		}

		@Override
		public Object instantiateItem(View view, int position) {
			final FrameLayout imageLayout = (FrameLayout) inflater.inflate(
					R.layout.item_pager_image, null);
			final ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image);
			final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.loading);

			imageLoader.displayImage(images[position], imageView, options,
					new ImageLoadingListener() {
						@Override
						public void onLoadingStarted() {
							spinner.setVisibility(View.VISIBLE);
						}

						@Override
						public void onLoadingFailed() {
							spinner.setVisibility(View.GONE);
							imageView.setImageResource(android.R.drawable.ic_delete);
						}

						@Override
						public void onLoadingComplete() {
							spinner.setVisibility(View.GONE);
						}
					});

			((ViewPager) view).addView(imageLayout, 0);
			return imageLayout;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view.equals(object);
		}

		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View container) {
		}
	}

	void downloadFile(String fileUrl) {
		URL myFileUrl = null;
		try {
			myFileUrl = new URL(fileUrl);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			Log.i("im connected", "Download");
			bmImg = BitmapFactory.decodeStream(is);
			saveImage();
			// imView.setImageBitmap(bmImg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void saveImage() {
		File filename;
		try {
			String path = Environment.getExternalStorageDirectory().toString();
			Log.i("in save()", "after mkdir");
			new File(path + "/UniversalImages").mkdir();
			filename = new File(path + "/UniversalImages/" + pagerPosition + ".jpg");
			Log.i("in save()", "after file");
			FileOutputStream out = new FileOutputStream(filename);
			Log.i("in save()", "after outputstream");
			bmImg.compress(Bitmap.CompressFormat.JPEG, 90, out);
			out.flush();
			out.close();
			Log.i("in save()", "after outputstream closed");
			MediaStore.Images.Media.insertImage(getContentResolver(),
					filename.getAbsolutePath(), filename.getName(), filename.getName());
			Toast.makeText(getApplicationContext(), "File is Saved in  " + filename, 1000)
					.show();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onClick(View v) {
		dialog = ProgressDialog.show(this, "Loading Images From Server....", "Please wait",
				true, false);
		switch (v.getId()) {

		case R.id.btnIpBack:
			finish();
			break;

		case R.id.btnIpSave:

			downloadFile(imageUrls[pagerPosition]);
			break;
		case R.id.btnIpPublish:
			downloadFile(imageUrls[pagerPosition]);
			Uri uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory()
					.toString() + "/UniversalImages/" + pagerPosition + ".jpg"));
			Intent i = new Intent(Intent.ACTION_SEND);
			i.putExtra(Intent.EXTRA_SUBJECT, "Image");
			i.putExtra(Intent.EXTRA_TEXT, "Content");
			i.putExtra(Intent.EXTRA_STREAM, uri);
			i.setType("text/plain");
			startActivity(Intent.createChooser(i, "Send mail"));

			break;
		case R.id.btnIpNext:
			pager.setAdapter(new ImagePagerAdapter(imageUrls));
			System.out.println(pagerPosition + ":+:"
					+ (pagerPosition >= imageUrls.length - 1 ? pagerPosition : pagerPosition + 1));
			
			pager.setCurrentItem(pagerPosition >= imageUrls.length - 1 ? pagerPosition
					: ++pagerPosition);
			break;

		case R.id.btnIpPrev:
			pager.setAdapter(new ImagePagerAdapter(imageUrls));
			System.out.println(pagerPosition + ":+:"
					+ (pagerPosition >= imageUrls.length - 1 ? pagerPosition : pagerPosition + 1));
			pager.setCurrentItem(pagerPosition < 1 ? pagerPosition : --pagerPosition);
			break;

		}

		dialog.dismiss();

	}
}