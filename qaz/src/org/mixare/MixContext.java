/*
 * Copyright (C) 2010- Peer internet solutions
 * 
 * This file is part of mixare.
 * 
 * This program is free software: you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version. 
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS 
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License 
 * for more details. 
 * 
 * You should have received a copy of the GNU General Public License along with 
 * this program. If not, see <http://www.gnu.org/licenses/>
 */
package org.mixare;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Random;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

import org.mixare.data.DataSource;
import org.mixare.data.DataSourceList;
import org.mixare.render.Matrix;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.Toast;

// 컨텍스트랩퍼를 확장하는 컨텍스트 클래스
public class MixContext extends ContextWrapper {

	public static final String TAG = "Qaz-Mixare";
	
	// 뷰와 컨텍스트
	public MixView mixView;
	Context ctx;
	
	boolean isURLvalid = true;	// URL이 유효한지 여부
	Random rand;	// 랜덤 수치를 생성하기 위함

	DownloadManager downloadManager;	// 다운로드 관리자

	Location curLoc;	// 현재 위치
	Location locationAtLastDownload;	// 마지막으로 다운로드된 위치
	Matrix rotationM = new Matrix();	// 회전연산에 사용될 행렬

	float declination = 0f;	// 경사, 적위
	
	private LocationManager lm;

	private ArrayList<DataSource> allDataSources=new ArrayList<DataSource>();

	public ArrayList<DataSource> getAllDataSources() {
		return this.allDataSources;
	}

	public void setAllDataSourcesforLauncher(DataSource datasource) {
		this.allDataSources.clear();
		this.allDataSources.add(datasource);
	}

	public void refreshDataSources() {
		this.allDataSources.clear();
		SharedPreferences settings = getSharedPreferences(
				DataSourceList.SHARED_PREFS, 0);
		int size = settings.getAll().size();
		
		if (size == 0){
			SharedPreferences.Editor dataSourceEditor = settings.edit();
			dataSourceEditor.putString("DataSource0", "위키피디아|http://ws.geonames.org/findNearbyWikipediaJSON|0|0|true");
			dataSourceEditor.putString("DataSource1", "Twitter|http://search.twitter.com/search.json|2|0|false");
			dataSourceEditor.putString("DataSource2", "OpenStreetmap|http://open.mapquestapi.com/xapi/api/0.6/node[railway=station]|3|1|false");
			dataSourceEditor.putString("DataSource3", "DrawOnReal|http://www.manjong.org:8255/qaz/check.jsp|4|0|true");
			dataSourceEditor.commit();
			size = settings.getAll().size();
		}
		
		// copy the value from shared preference to adapter
		for (int i = 0; i < size; i++) {
			String fields[] = settings.getString("DataSource" + i, "").split("\\|", -1);
			this.allDataSources.add(new DataSource(fields[0], fields[1], fields[2], fields[3], fields[4]));
		}
	}
	
	// 생성자. 어플리케이션의 컨텍스트를 받는다
	public MixContext(Context appCtx) {
		super(appCtx);
		
		// 메인 뷰와 컨텍스트를 할당
		this.mixView = (MixView) appCtx;
		this.ctx = appCtx.getApplicationContext();

		// 액티비티의 자체 세팅을 공유할 프레퍼런스
		refreshDataSources();
		boolean atLeastOneDatasourceSelected = false;	// 최소 하나 이상의 데이터 소스가 선택되었는지 여부
		
		// 데이터 소스 전체를 돌며 적용
		for(DataSource ds: this.allDataSources) {
			if(ds.getEnabled())
				atLeastOneDatasourceSelected = true;
		}
		// 아무것도 선택된 것이 없을 경우 위키피디아와 Qaz를 기본으로 선택
//		if (!atLeastOneDatasourceSelected) {
//		}
		
		// 회전행렬을 일단 단위행렬로 세팅
		rotationM.toIdentity();
		
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		Criteria c = new Criteria();
		//try to use the coarse provider first to get a rough position
		c.setAccuracy(Criteria.ACCURACY_COARSE);
		String coarseProvider = lm.getBestProvider(c, true);
		try {
			lm.requestLocationUpdates(coarseProvider, 0 , 0, lcoarse);
		} catch (Exception e) {
			Log.d(TAG, "Could not initialize the coarse provider");
		}

		//need to be precise
		c.setAccuracy(Criteria.ACCURACY_FINE);				
		//fineProvider will be used for the initial phase (requesting fast updates)
		//as well as during normal program usage
		//NB: using "true" as second parameters means we get the provider only if it's enabled
		String fineProvider = lm.getBestProvider(c, true);
		try {
			lm.requestLocationUpdates(fineProvider, 0 , 0, lbounce);
		} catch (Exception e) {
			Log.d(TAG, "Could not initialize the bounce provider");
		}

		//fallback for the case where GPS and network providers are disabled
		Location hardFix = new Location("reverseGeocoded");

		//Frangart, Eppan, Bozen, Italy
		hardFix.setLatitude(46.480302);
		hardFix.setLongitude(11.296005);
		hardFix.setAltitude(300);

		/*New York*/
//		hardFix.setLatitude(40.731510);
//		hardFix.setLongitude(-73.991547);

		// TU Wien
//		hardFix.setLatitude(48.196349);
//		hardFix.setLongitude(16.368653);
//		hardFix.setAltitude(180);

		//frequency and minimum distance for update
		//this values will only be used after there's a good GPS fix
		//see back-off pattern discussion 
		//http://stackoverflow.com/questions/3433875/how-to-force-gps-provider-to-get-speed-in-android
		//thanks Reto Meier for his presentation at gddde 2010
		long lFreq = 60000;	//60 seconds
		float lDist = 50;		//20 meters
		try {
			lm.requestLocationUpdates(fineProvider, lFreq , lDist, lnormal);
		} catch (Exception e) {
			Log.d(TAG, "Could not initialize the normal provider");
			Toast.makeText( this, getString(DataView.CONNECTION_GPS_DIALOG_TEXT), Toast.LENGTH_LONG ).show();
		}
		
		try {
			
			Location lastFinePos=lm.getLastKnownLocation(fineProvider);
			Location lastCoarsePos=lm.getLastKnownLocation(coarseProvider);
			if(lastFinePos!=null)
				curLoc = lastFinePos;
			else if (lastCoarsePos!=null)
				curLoc = lastCoarsePos;
			else
				curLoc = hardFix;
			
		} catch (Exception ex2) {
			ex2.printStackTrace();
			curLoc = hardFix;
			
			Toast.makeText( this, getString(DataView.CONNECTION_GPS_DIALOG_TEXT), Toast.LENGTH_LONG ).show();
		}
		
		setLocationAtLastDownload(curLoc);
		
	}

	// 정확한 위치가 맞는지 리턴
	public void unregisterLocationManager() {
	if (lm != null) {
		lm.removeUpdates(lnormal);
		lm.removeUpdates(lcoarse);
		lm.removeUpdates(lbounce);
		lm = null;
	}
}

	// 사용중인 다운로드 관리자 리턴
	public DownloadManager getDownloader() {
		return downloadManager;
	}

	// 시작 Url 경로를 리턴한다
	public String getStartUrl() {
		Intent intent = ((Activity) mixView).getIntent();
		// 웹 브라우져가 켜질 경우
		if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_VIEW)) { 
			return intent.getData().toString(); 
		} 
		else { 
			return ""; 
		}
	}

	// 인자로 받는 dest 에 회전 행렬을 세팅
	public void getRM(Matrix dest) {
		synchronized (rotationM) {
			dest.set(rotationM);
		}
	}

	// 현재의 위치를 리턴
	public Location getCurrentLocation() {
		synchronized (curLoc) {
			return curLoc;
		}
	}

	// GET 형식으로 데이터를 받아 인풋 스트림을 리턴한다
	public InputStream getHttpGETInputStream(String urlStr)
	throws Exception {
		InputStream is = null;	// 내용을 읽어올 인풋 스트림
		URLConnection conn = null;	// URL 과의 통신을 위한 URLConnection 객체  
		
		if (Integer.parseInt(Build.VERSION.SDK) < Build.VERSION_CODES.FROYO) {
          System.setProperty("http.keepAlive", "false");
		}

		// 각 파일, 컨텐트, 네트워크 주소등에 따른 스트림을 읽을 준비
		if (urlStr.startsWith("file://"))			
			return new FileInputStream(urlStr.replace("file://", ""));

		if (urlStr.startsWith("content://"))
			return getContentInputStream(urlStr, null);

		// 네트워크 부분은 절차가 좀 복잡하다(SSL/TLS)
		if (urlStr.startsWith("https://")) {
			HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier(){
    			public boolean verify(String hostname, SSLSession session) {
    				return true;
    			}});
			
			// SSL 통신용 컨텍스트
			SSLContext context = SSLContext.getInstance("TLS");
			
			context.init(null, new X509TrustManager[]{new X509TrustManager(){
				public void checkClientTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {}
				public void checkServerTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {}
				public X509Certificate[] getAcceptedIssuers() {
					return new X509Certificate[0];
				}}}, new SecureRandom());
			
			HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
		}
		
		try {
			URL url = new URL(urlStr);	// 준비된 스트링 값으로 URL 을 생성
			// 커넥션 설정을 한다
			conn =  url.openConnection();
			conn.setReadTimeout(10000);
			conn.setConnectTimeout(10000);

			is = conn.getInputStream();	// 인풋 스트림으로 저장하여 리턴
			
			return is;
		} catch (Exception ex) {	// 예외 처리
			try {
				is.close();	// 인풋 스트림을 닫고
			} catch (Exception ignore) {			
			}
			try {	// 접속을 끊는다
				if(conn instanceof HttpURLConnection)
					((HttpURLConnection)conn).disconnect();
			} catch (Exception ignore) {			
			}
			
			throw ex;				

		}
	}

	// 네트워크의 데이터를 인풋 스트림을 스트링 형태로 리턴
	public String getHttpInputString(InputStream is) {
		// 인풋 스트림으로부터 데이터를 읽을 버퍼와 그에 사용될 스트링 빌더
		BufferedReader reader = new BufferedReader(new InputStreamReader(is), 8 * 1024);
		StringBuilder sb = new StringBuilder();

		try {
			// 행 단위로 읽어 뒤에 개행코드를 추가한다 
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {	// 모든 작업이 끝나면
			try {
				is.close();	// 스트림을 닫는다
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();	// 완성된 스트링을 리턴
	}

	// POST 형식으로 데이터를 받아 인풋 스트림을 리턴한다
	public InputStream getHttpPOSTInputStream(String urlStr,
			String params) throws Exception {
		
		// 사용될 인풋, 아웃풋 스트림과 커넥션 객체
		InputStream is = null;
		OutputStream os = null;
		HttpURLConnection conn = null;

		// 컨텐트의 경우
		if (urlStr.startsWith("content://"))
			return getContentInputStream(urlStr, params);

		try {
			URL url = new URL(urlStr);	// 준비된 스트링으로 URL 생성
			// 커넥션 설정
			conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(10000);
			conn.setConnectTimeout(10000);

			// 파라메터 값이 null 이 아닐 때
			if (params != null) {
				conn.setDoOutput(true);
				os = conn.getOutputStream();	// 커넥션 객체로부터 아웃풋 스트림을 읽고
				OutputStreamWriter wr = new OutputStreamWriter(os);	// 라이터를 생성
				wr.write(params);	// 파라메터에 기록
				wr.close();
			}

			is = conn.getInputStream();	// 커넥션 객체로부터 인풋 스트림을 읽어옴
			
			return is;	// 읽어온 인풋 스트림을 리턴
		} catch (Exception ex) {	
			// 예외 처리
			try {
				is.close();
			} catch (Exception ignore) {			

			}
			try {
				os.close();
			} catch (Exception ignore) {			

			}
			try {
				conn.disconnect();
			} catch (Exception ignore) {
			}

			// 405 에러 시에는 GET형식으로
			if (conn != null && conn.getResponseCode() == 405) {
				return getHttpGETInputStream(urlStr);
			} else {		

				throw ex;
			}
		}
	}

	// 컨텐트 인풋 스트림을 리턴
	public InputStream getContentInputStream(String urlStr, String params)
	throws Exception {
		// 쿼리를 통해 컨텐트 프로바이더(CP)와 통신할 ContentResolver 객체와 커서
		ContentResolver cr = mixView.getContentResolver();
		// ContentResolver 의 쿼리를 통해 urlStr 을 파싱하여 커서를 생성한다. 파라메터 이용
		Cursor cur = cr.query(Uri.parse(urlStr), null, params, null, null);

		cur.moveToFirst();	// 커서를 맨 처음으로 옮기고, 모드를 읽어 저장
		int mode = cur.getInt(cur.getColumnIndex("MODE"));

		// 모드가 1일 경우
		if (mode == 1) {
			// 결과를 읽는다
			String result = cur.getString(cur.getColumnIndex("RESULT"));
			cur.deactivate();

			// 결과를 바이트단위의 인풋스트림으로 변환하여 리턴한다
			return new ByteArrayInputStream(result
					.getBytes());
		} else {
			cur.deactivate();

			// 다른 모드일 경우엔 예외 발생
			throw new Exception("Invalid content:// mode " + mode);
		}
	}

	// 네트워크 인풋 스트림을 닫는다
	public void returnHttpInputStream(InputStream is) throws Exception {
		if (is != null) {
			is.close();
		}
	}

	// 리소스 인풋 스트림을 리턴
	public InputStream getResourceInputStream(String name) throws Exception {
		AssetManager mgr = mixView.getAssets();	// assets 안의 파일을 접근하기 위함
		return mgr.open(name);
	}

	// 리소스 인풋 스트림을 닫는다
	public void returnResourceInputStream(InputStream is) throws Exception {
		if (is != null)
			is.close();
	}

	// 웹페이지를 로드
	public void loadMixViewWebPage(String url) throws Exception {
		// TODO
		WebView webview = new WebView(mixView);	// 웹 뷰
		webview.getSettings().setJavaScriptEnabled(true);	// 자바스크립트 허용

		// URL 을 연결하여 웹 뷰 클라이언트를 세팅
		webview.setWebViewClient(new WebViewClient() {
			public boolean  shouldOverrideUrlLoading  (WebView view, String url) {
			     view.loadUrl(url);
				return true;
			}

		});
		
		// 다이얼로그를 생성
		Dialog d = new Dialog(mixView) {
			public boolean onKeyDown(int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK)
					this.dismiss();
				return true;
			}
		};
		
		// 웹 뷰를 다이얼로그 연결한다
		d.requestWindowFeature(Window.FEATURE_NO_TITLE);
		d.getWindow().setGravity(Gravity.BOTTOM);
		d.addContentView(webview, new FrameLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				Gravity.BOTTOM));

		d.show();	// 다이얼로그 출력
		
		webview.loadUrl(url);	// 웹 뷰에 url 로드
	}
	
	// 웹 페이지 로드. 위 메소드와의 차이는 컨텍스트를 별도로 지정한다는 것이다
	public void loadWebPage(String url, Context context) throws Exception {
		// TODO
		WebView webview = new WebView(context);	// 웹 뷰
		webview.getSettings().setJavaScriptEnabled(true);
		webview.setBackgroundColor(0x99FFFFFF);
		
		webview.setWebViewClient(new WebViewClient() {
			public boolean  shouldOverrideUrlLoading  (WebView view, String url) {
			     view.loadUrl(url);
				return true;
			}

		});
				
		Dialog d = new Dialog(context) {
			public boolean onKeyDown(int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK)
					this.dismiss();
				return true;
			}
		};
		d.requestWindowFeature(Window.FEATURE_NO_TITLE);
		d.getWindow().setGravity(Gravity.BOTTOM);
		d.addContentView(webview, new FrameLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				Gravity.BOTTOM));

		d.show();
		
		webview.loadUrl(url);
	}

	// 마지막으로 다운로드된 위치를 리턴
	public Location getLocationAtLastDownload() {
		return locationAtLastDownload;
	}

	// 마지막으로 다운로드된 위치를 세팅
	public void setLocationAtLastDownload(Location locationAtLastDownload) {
		this.locationAtLastDownload = locationAtLastDownload;
	}
	
	private LocationListener lbounce = new LocationListener() {

		@Override
		public void onLocationChanged(Location location) {
			
			try {
			Log.d(TAG, "bounce Location Changed: "+location.getProvider()+" lat: "+location.getLatitude()+" lon: "+location.getLongitude()+" alt: "+location.getAltitude()+" acc: "+location.getAccuracy());
			//Toast.makeText(ctx, "BOUNCE: Location Changed: "+location.getProvider()+" lat: "+location.getLatitude()+" lon: "+location.getLongitude()+" alt: "+location.getAltitude()+" acc: "+location.getAccuracy(), Toast.LENGTH_LONG).show();

			downloadManager.purgeLists();
			
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			
			if (location.getAccuracy() < 40) {
				lm.removeUpdates(lcoarse);
				lm.removeUpdates(lbounce);			
			}
		}

		@Override
		public void onProviderDisabled(String arg0) {
			Log.d(TAG, "bounce disabled");
		}

		@Override
		public void onProviderEnabled(String arg0) {
			Log.d(TAG, "bounce enabled");

		}

		@Override
		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {}

	};

	private LocationListener lcoarse = new LocationListener() {

		@Override
		public void onLocationChanged(Location location) {
			Log.d(TAG, "coarse Location Changed: "+location.getProvider()+" lat: "+location.getLatitude()+" lon: "+location.getLongitude()+" alt: "+location.getAltitude()+" acc: "+location.getAccuracy());
			//Toast.makeText(ctx, "COARSE: Location Changed: "+location.getProvider()+" lat: "+location.getLatitude()+" lon: "+location.getLongitude()+" alt: "+location.getAltitude()+" acc: "+location.getAccuracy(), Toast.LENGTH_LONG).show();
			lm.removeUpdates(lcoarse);
			
			downloadManager.purgeLists();
		}

		@Override
		public void onProviderDisabled(String arg0) {}

		@Override
		public void onProviderEnabled(String arg0) {}

		@Override
		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {}

	};

	private LocationListener lnormal = new LocationListener() {
		public void onProviderDisabled(String provider) {}

		public void onProviderEnabled(String provider) {}

		public void onStatusChanged(String provider, int status, Bundle extras) {}

		public void onLocationChanged(Location location) {
			Log.d(TAG, "normal Location Changed: "+location.getProvider()+" lat: "+location.getLatitude()+" lon: "+location.getLongitude()+" alt: "+location.getAltitude()+" acc: "+location.getAccuracy());
			//Toast.makeText(ctx, "NORMAL: Location Changed: "+location.getProvider()+" lat: "+location.getLatitude()+" lon: "+location.getLongitude()+" alt: "+location.getAltitude()+" acc: "+location.getAccuracy(), Toast.LENGTH_LONG).show();
			try {
				downloadManager.purgeLists();
				Log.v(TAG,"Location Changed: "+location.getProvider()+" lat: "+location.getLatitude()+" lon: "+location.getLongitude()+" alt: "+location.getAltitude()+" acc: "+location.getAccuracy());
					synchronized (curLoc) {
						curLoc = location;
					}
					mixView.repaint();
					Location lastLoc=getLocationAtLastDownload();
					if(lastLoc==null)
						setLocationAtLastDownload(location);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

	};
	
}
