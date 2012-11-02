/**
 * 
 */
package org.mixare;

import org.mixare.data.DataSource.DATASOURCE;
import org.mixare.gui.PaintScreen;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;

/**
 * @author A.Egal
 * 
 */
public class ImageMarker extends Marker {

	public static final int MAX_OBJECTS = 15;
	private Bitmap image = null;

	public ImageMarker(String title, double latitude, double longitude,
			double altitude, String URL, DATASOURCE datasource, Bitmap image, String iOSMurl, int iOSMUrlID) {
		super(title, latitude, longitude, altitude, URL, datasource,iOSMurl,iOSMUrlID);
		this.image = image;

	}

	@Override
	public void update(Location curGPSFix) {
		super.update(curGPSFix);
	}

	@Override
	public int getMaxObjects() {
		return MAX_OBJECTS;
	}

	@Override
	public void draw(PaintScreen dw) {
		this.drawImage(dw);
		super.drawTextBlock(dw);

	}

	public void drawImage(PaintScreen dw) {
		if (isVisible) {
			if (image != null) {
				int rectangleBackgroundColor = Color.argb(255, 255, 255, 255);
				dw.setColor(rectangleBackgroundColor);
				dw.paintBitmap(image, signMarker.x - (image.getWidth() / 2),
						signMarker.y - (image.getHeight() / 2));
			} else {
				drawCircle(dw);
			}
		}
	}

	@Override
	public int getOsmUrlMaxObject() {
		// TODO Auto-generated method stub
		return 0;
	}
}