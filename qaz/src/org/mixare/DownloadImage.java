package org.mixare;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class DownloadImage extends Thread {
	private String mUrl = "";
	public static InputStream input = null;

	public DownloadImage(String title) {
		mUrl = title;
	}

	@Override
	public void run() {

		URL myFileUrl = null;

		try {

			mUrl = URLEncoder.encode(new String(mUrl.getBytes("UTF-8"))); // UTF-8로
			mUrl = "http://manjong.org:8255/qaz/upload/" + mUrl + ".png";
			myFileUrl = new URL(mUrl);

			HttpURLConnection connection = (HttpURLConnection) myFileUrl
					.openConnection();
			connection.setDoInput(true);
			connection.connect();

			int status = connection.getResponseCode();
			input = connection.getInputStream();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}