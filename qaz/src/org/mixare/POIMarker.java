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

import java.text.DecimalFormat;

import org.mixare.data.DataSource;
import org.mixare.data.DataSource.DATASOURCE;
import org.mixare.gui.PaintScreen;
import org.mixare.gui.TextObj;

import android.graphics.Color;
import android.graphics.Path;
import android.location.Location;

/**
 * @author hannes
 *
 */
// POI(Point of Interest)마커 클래스. 마커로부터 확장
public class POIMarker extends Marker {
	
	public static final int MAX_OBJECTS=20;	// 최대 객체 수
	public static final int OSM_URL_MAX_OBJECTS = 5;

	// 생성자. 타이틀과 위도, 경도, 고도, 그리고 URL과 데이터 소스를 인자로 받는다
	public POIMarker(String title, double latitude, double longitude,
			double altitude, String URL, DATASOURCE datasource, String iOSMurl,
			int iOSMUrlID) {
		super(title, latitude, longitude, altitude, URL, datasource, iOSMurl,
				iOSMUrlID);
		// TODO Auto-generated constructor stub
	}

	// 마커 위치 갱신
	@Override
	public void update(Location curGPSFix) {
		super.update(curGPSFix);
	}

	// 최대 객체 수 리턴
	@Override
	public int getMaxObjects() {
		return MAX_OBJECTS;
	}
	
	@Override
	public int getOsmUrlMaxObject() {
		return MixView.osmMaxObject;
	}

	@Override
	public void drawCircle(PaintScreen dw) {
		if (isVisible) {

			// float maxHeight = Math.round(dw.getHeight() / 10f) + 1;
			float maxHeight = dw.getHeight();
			dw.setStrokeWidth(maxHeight / 100f);
			dw.setFill(false);

			if (super.getDatasource().equals(DataSource.DATASOURCE.OSM)) {
				dw.setColor(getColour());
			} else {
				dw.setColor(DataSource.getColor(datasource));
			}

			// draw circle with radius depending on distance
			// 0.44 is approx. vertical fov in radians
			double angle = 2.0 * Math.atan2(10, distance);
			double radius = Math.max(
					Math.min(angle / 0.44 * maxHeight, maxHeight),
					maxHeight / 25f);
			// double radius = angle/0.44d * (double)maxHeight;

			/*
			 * distance 100 is the threshold to convert from circle to another
			 * shape
			 */
			if (distance < 100.0)
				otherShape(dw);
			else
				dw.paintCircle(cMarker.x, cMarker.y, (float) radius);

		}
	}

	@Override
	public void drawTextBlock(PaintScreen dw) {
		float maxHeight = Math.round(dw.getHeight() / 10f) + 1;
		// TODO: change textblock only when distance changes

		String textStr = "";

		double d = distance;
		DecimalFormat df = new DecimalFormat("@#");
		if (d < 1000.0) {
			textStr = title + " (" + df.format(d) + "m)";
		} else {
			d = d / 1000.0;
			textStr = title + " (" + df.format(d) + "km)";
		}

		textBlock = new TextObj(textStr, Math.round(maxHeight / 2f) + 1, 250,
				dw, underline);

		if (isVisible) {
			// based on the distance set the colour
			if (distance < 100.0) {
				// 6.104
				textBlock.setBgColor(Color.argb(128, 52, 52, 52));
				textBlock.setBorderColor(Color.rgb(255, 104, 91));

			} else {
				textBlock.setBgColor(Color.argb(128, 0, 0, 0));
				textBlock.setBorderColor(Color.rgb(255, 255, 255));
			}
			dw.setColor(DataSource.getColor(datasource));

			float currentAngle = MixUtils.getAngle(cMarker.x, cMarker.y,
					signMarker.x, signMarker.y);
			txtLab.prepare(textBlock);
			dw.setStrokeWidth(1f);
			dw.setFill(true);
			dw.paintObj(txtLab, signMarker.x - txtLab.getWidth() / 2,
					signMarker.y + maxHeight, currentAngle + 90, 1);

		}
	}

	public void otherShape(PaintScreen dw) {
		// this is to draw triangle shape
		float currentAngle = MixUtils.getAngle(cMarker.x, cMarker.y,
				signMarker.x, signMarker.y);
		float maxHeight = Math.round(dw.getHeight() / 10f) + 1;

		float radius = maxHeight / 1.5f;
		if (super.getDatasource().equals(DataSource.DATASOURCE.OSM)) {
			dw.setColor(getColour());
		} else {
			dw.setColor(DataSource.getColor(datasource));
		}
		dw.setStrokeWidth(dw.getHeight() / 100f);
		dw.setFill(false);

		Path tri = new Path();
		float x = 0;
		float y = 0;
		tri.moveTo(x, y);
		tri.lineTo(x - radius, y - radius);
		tri.lineTo(x + radius, y - radius);

		tri.close();
		dw.paintPath(tri, cMarker.x, cMarker.y, radius * 2, radius * 2,
				currentAngle + 90, 1);
	}

}
