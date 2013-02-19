package com.qaz.dor;

import java.io.File;

public class UploadMarkerImage extends Thread {

	public static String ret;

	private String createdMarkerImageUrl;
	private File createdMarkerImagePath;
	private String createdMarkerImageName;
	private double createdMarkerImageLat;
	private double createdMarkerImageLon;
	private double createdMarkerImageAlt;
	private String createdMarkerImageUser;

	public UploadMarkerImage(String createdMarkerImageUrl,
			File createdMarkerImagePath, String createdMarkerImageName,
			double createdMarkerImageLat, double createdMarkerImageLon,
			double createdMarkerImageAlt, String createdMarkerImageUser) {

		this.createdMarkerImageUrl = createdMarkerImageUrl;
		this.createdMarkerImagePath = createdMarkerImagePath;
		this.createdMarkerImageName = createdMarkerImageName;
		this.createdMarkerImageLat = createdMarkerImageLat;
		this.createdMarkerImageLon = createdMarkerImageLon;
		this.createdMarkerImageAlt = createdMarkerImageAlt;
		this.createdMarkerImageUser = createdMarkerImageUser;

	}

	@Override
	public void run() {
		try {
			ret = QazHttpServer.UploadImageText(
					createdMarkerImageUrl, createdMarkerImagePath,
					createdMarkerImageName, createdMarkerImageLat,
					createdMarkerImageLon, createdMarkerImageAlt,
					createdMarkerImageUser);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
