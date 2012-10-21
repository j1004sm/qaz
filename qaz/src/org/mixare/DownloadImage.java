package org.mixare;

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
	private String mUrl;
	public Bitmap downImg = null; // 다운받은 이미지가 저장될 공간
	public boolean doneFlg = false;

	public DownloadImage(String title) {
		mUrl = title;
		setDaemon(true);
	}

	@Override
	public void run() {

		URL myFileUrl = null;
		
		try {
			mUrl = URLEncoder.encode(new String(mUrl.getBytes("UTF-8")));	//UTF-8로 인코딩 
			mUrl = "http://manjong.org:8255/qaz/upload/" + mUrl + ".png";
			
			myFileUrl = new URL(mUrl);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			
			HttpURLConnection connection = (HttpURLConnection) myFileUrl.openConnection();
			connection.setDoInput(true);
			connection.connect();
			
			int status = connection.getResponseCode();
			//Log.e("Image Download ErrorCode", Integer.toString(status));
			
			InputStream input = connection.getInputStream();
			//Bitmap myBitmap = BitmapFactory.decodeStream(input);
			
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 4;				//이미지 사이즈를 줄임 : 1/4로
			downImg = BitmapFactory.decodeStream(input, null, options);
			
			doneFlg = true;
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 핸들러에 완료 알림
		//mHandler.sendEmptyMessage(0);

	}
//
//	// 내부 핸들러
//	Handler mHandler = new Handler(Looper.getMainLooper()) {
//		public Bitmap retImg = null;
//		@Override
//		public void handleMessage(Message msg) {
//			if (msg.what == 0) {
//				retImg = downImg;
//			}
//		}
//	};

}