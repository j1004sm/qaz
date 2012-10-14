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
public class ImageMarker extends Marker{

	public static final int MAX_OBJECTS = 20;
	private Bitmap image = null; 
	private int rectangleBackgroundColor = Color.argb(155, 255, 255, 255);


	public ImageMarker(String title, double latitude, double longitude,
			double altitude, String URL, DATASOURCE datasource, Bitmap image) {
		super(title, latitude, longitude, altitude, URL, datasource);
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
		super.drawTextBlock(dw);
		
		if (isVisible) {
			if (image != null){
				
				dw.setColor(rectangleBackgroundColor);
				dw.paintBitmap(image, signMarker.x - (image.getWidth()/2), signMarker.y - (image.getHeight() / 2));
			}
		}
	}
}