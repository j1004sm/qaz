package org.mixare;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class DownloadImage extends Thread {
	final Context mContext;
	private String mUrl;
	private Bitmap downImg = null; // 다운받은 이미지가 저장될 공간
	public Bitmap retImg = null;

	/**
	 * 웹에서 이미지를 다운로드
	 * 
	 * @param context 어플의 context
	 * @param url 다운 받을 이미지 주소
	 */
	public DownloadImage(Context context, String title) {
		
		mContext = context;
		mUrl = "http://manjong.org:8255/qaz/upload/" + title + ".png";
		
		/*
		try {
			String utf_title;
			utf_title = new String(title.getBytes("utf-8"), "euc-kr");
			mUrl = "http://manjong.org:8255/qaz/upload/" + utf_title + ".png";
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mUrl = "http://manjong.org:8255/qaz/upload/" + title + ".png";
		}
		*/

		setDaemon(true);
	}

	@Override
	public void run() {

		URL myFileUrl = null;
		
		try {
			myFileUrl = new URL(mUrl);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		try {
			HttpURLConnection conn = (HttpURLConnection) myFileUrl
					.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			downImg = BitmapFactory.decodeStream(is);
			is.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			
		}
		// 핸들러에 완료 알림
		mHandler.sendEmptyMessage(0);
	}

	// 내부 핸들러
	Handler mHandler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				retImg = downImg;
			}
		}
	};

}