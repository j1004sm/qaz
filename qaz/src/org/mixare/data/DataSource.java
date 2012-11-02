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

package org.mixare.data;

import org.mixare.MixListView;
import com.qaz.client.R;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

/**
 * @author hannes
 *
 */

// 데이터 소스를 실질적으로 다루는 클래스
public class DataSource {
	
	// 데이터 소스와 데이터 포맷은 비슷해 보이지만 전혀 다르다.
	// 데이터 소스는 데이터가 어디서 왔는지, 데이터 포맷은 어떤 형식으로 포맷되었는지를 가르킨다.
	// 이에 대한 이해는 똑같은 데이터 포맷으로 여러가지의 데이터 소스를 실험하는데에 필수적이다
	
	
	// 데이터 소스와 데이터 포맷의 열거형 변수
	public enum DATASOURCE {Wikipedia, Twitter, OSM, Qaz};
	public enum DATAFORMAT {Wikipedia, Twitter, OSM, DOR};	

	/** 기본 URL */
	// 위키피디아
	private static final String WIKI_BASE_URL = "http://ws.geonames.org/findNearbyWikipediaJSON";
	//private static final String WIKI_BASE_URL =	"file:///sdcard/wiki.json";
	
	// 트위터
	private static final String TWITTER_BASE_URL = "http://search.twitter.com/search.json";
	
	// OSM(OpenStreetMap)
	// OpenStreetMap API는 http://wiki.openstreetmap.org/wiki/Xapi 를 참고
	// 예를 들어, 철도만 사용할 경우:
	//private static final String OSM_BASE_URL =	"http://www.informationfreeway.org/api/0.6/node[railway=station]";
	//private static final String OSM_BASE_URL =	"http://xapi.openstreetmap.org/api/0.6/node[railway=station]";
	//private static final String OSM_BASE_URL =	"http://open.mapquestapi.com/xapi/api/0.6/node[railway=station]";
	
	// 주의할것! 방대한 양의 데이터(MB단위 이상)을 산출할 때에는, 작은 반경이나 특정한 쿼리만을 사용해야한다
	/** URL 부분 끝 */
	
	
	// 아이콘들. 트위터
	public static Bitmap twitterIcon;
	
	// 기본 생성자
	public DataSource() {
		
	}
	
	// 리소스로부터 각 아이콘 생성
	public static void createIcons(Resources res) {
		twitterIcon=BitmapFactory.decodeResource(res, R.drawable.twitter);
		
	}
	
	// 아이콘 비트맵의 게터
	public static Bitmap getBitmap(DATASOURCE ds) {
		Bitmap bitmap=null;
		switch (ds) {
			case Twitter: bitmap=twitterIcon; break;
		}
		return bitmap;
	}
	
	// 데이터 소스로부터 데이터 포맷을 추출
	public static DATAFORMAT dataFormatFromDataSource(DATASOURCE ds) {
		DATAFORMAT ret;
		// 소스 형식에 따라 포맷을 할당한다
		switch (ds) {
			case Wikipedia: ret=DATAFORMAT.Wikipedia; break;
			case Twitter: ret=DATAFORMAT.Twitter; break;
			case OSM: ret=DATAFORMAT.OSM; break;
			case Qaz: ret=DATAFORMAT.DOR; break;
			default: ret=DATAFORMAT.DOR; break;
		}
		return ret;	// 포맷 리턴
	}
	
	// 각 정보들로 완성된 URL 리퀘스트를 생성
	public static String createRequestURL(DATASOURCE source, double lat, double lon, double alt, float radius,String locale) {
		String ret="";	// 결과 스트링
		
		// 소스에 따라 주소 할당. 우선 상수로 설정된 값들을 할당한다
		switch(source) {
		
			case Wikipedia: 
				ret = WIKI_BASE_URL;
			break;
			
			case Twitter: 
				ret = TWITTER_BASE_URL;			
			break;
			/*
			case OSM: 
				ret = OSM_BASE_URL;
			break;
			*/
			
			case Qaz:
				ret = MixListView.customizedURL;
			break;
			
		}
		
		// 파일로부터 읽는 것이 아니라면
		if (!ret.startsWith("file://")) {
			
			// 각 소스에 따른 URL 리퀘스트를 완성한다
			switch(source) {
			// 위키피디아
			case Wikipedia:
				float geoNamesRadius = radius > 20 ? 20 : radius; // Free service limited to 20km
				ret += 
				"?lat=" + lat + 
				"&lng=" + lon +
				"&radius=" + geoNamesRadius + 
				"&maxRows=50" + 
				"&lang=" + locale + 
				"&username=mixare";
				break;
			
			// 트위터
			case Twitter: 
				ret+=
				"?geocode=" + lat + "%2C" + lon + "%2C" + 
				Math.max(radius, 1.0) + "km" ;				
			break;
			
			/*
			// OSM(OpenStreetMap)
			case OSM: 
				ret+= XMLHandler.getOSMBoundingBox(lat, lon, radius);
			break;
			*/
			
			// 자체 URL
			case Qaz:
				ret+=
				"?latitude=" + Double.toString(lat) + 
				"&longitude=" + Double.toString(lon) + 
				"&altitude=" + Double.toString(alt) +
				"&radius=" + Double.toString(radius);
			break;
			
			}
			
		}
		
		return ret;
	}
	
	public static String createRequestOSMURL(String sourceURL, double lat,
			double lon, double alt, float radius, String locale) {

		String ret = sourceURL;

		if (!ret.startsWith("file://")) {
			ret += XMLHandler.getOSMBoundingBox(lat, lon, radius);
		}

		return ret;
	}
	
	// 각 소스에 따른 색을 리턴
	public static int getColor(DATASOURCE datasource) {
		int ret;
		switch(datasource) {
			case Twitter:	ret = Color.rgb(50, 204, 255); break;
			//case OSM:		ret = Color.rgb(255, 168, 0); break;
			case Wikipedia:	ret = Color.RED; break;
			default:		ret = Color.WHITE; break;
		}
		return ret;
	}

}
