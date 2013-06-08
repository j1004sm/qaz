package org.mixare;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import android.util.Log;

import com.qaz.dor.QazHttpServer;

public class DownloadMarkerImage extends Thread {
	private String mUrl = null;
//	public InputStream input = null;
	public BufferedInputStream input = null;

	public DownloadMarkerImage(String title) {
		mUrl = title;
	}

	@Override
	public void run() {

		String eUrl = null;
		URL myFileUrl = null;

		try {

			eUrl = URLEncoder.encode(new String(mUrl.getBytes("UTF-8"))); // UTF-8로
			eUrl = eUrl.replace("+", "%20");
			eUrl = QazHttpServer.QAZ_URL_IMAGEDIR + eUrl + ".png";
			myFileUrl = new URL(eUrl);

			HttpURLConnection connection = (HttpURLConnection) myFileUrl
					.openConnection();
			connection.setDoInput(true);
			connection.connect();

//			int status = connection.getResponseCode();
//			input = connection.getInputStream();
			input = new BufferedInputStream(connection.getInputStream());

		} catch (FileNotFoundException e) {
			Log.e("Qaz", "Can't download : " + eUrl);
			
			try {

//				mUrl = new String(mUrl.getBytes("UTF-8"));// UTF-8로
				mUrl = QazHttpServer.QAZ_URL_IMAGEDIR + mUrl + ".png";
				myFileUrl = new URL(mUrl);

				HttpURLConnection connection = (HttpURLConnection) myFileUrl
						.openConnection();
				connection.setDoInput(true);
				connection.connect();

				// int status = connection.getResponseCode();
//				input = connection.getInputStream();
				input = new BufferedInputStream(connection.getInputStream());

			} catch (Exception e1) {
//				e1.printStackTrace();
				Log.e("Qaz", "Can't download anymore : " + mUrl);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			CloseInputStream();
			
		}

	}
	
	public void CloseInputStream() {
		if (input != null)
			try {
				input.close();
			} catch (IOException ignore) {}
	}
}