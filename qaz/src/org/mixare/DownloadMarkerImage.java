package org.mixare;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import com.qaz.dor.QazHttpServer;

public class DownloadMarkerImage extends Thread {
	private String mUrl = "";
	public InputStream input = null;

	public DownloadMarkerImage(String title) {
		mUrl = title;
	}

	@Override
	public void run() {

		URL myFileUrl = null;

		try {

			mUrl = URLEncoder.encode(new String(mUrl.getBytes("UTF-8"))); // UTF-8로
			mUrl = QazHttpServer.QAZ_URL_IMAGEDIR + mUrl + ".png";
			myFileUrl = new URL(mUrl);

			HttpURLConnection connection = (HttpURLConnection) myFileUrl
					.openConnection();
			connection.setDoInput(true);
			connection.connect();

//			int status = connection.getResponseCode();
			input = connection.getInputStream();

		} catch (Exception e) {
			e.printStackTrace();
			
		}

	}
}