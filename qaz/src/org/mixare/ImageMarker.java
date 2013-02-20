/**
 * 
 */
package org.mixare;

import org.mixare.data.DataSource;
import org.mixare.gui.PaintScreen;

import android.graphics.Bitmap;
import android.location.Location;

/**
 * @author A.Egal
 * 
 */
public class ImageMarker extends Marker {

	public static final int MAX_OBJECTS = 15;
//	public static ArrayList<Bitmap> bitmapMarkerImage = new ArrayList<Bitmap>(); 
	
	private Bitmap image = null;

	public ImageMarker(String title, double latitude, double longitude,
			double altitude, String URL, Bitmap image, DataSource datasource) {
		
		super(title, latitude, longitude, altitude, URL, datasource);
		this.image = Bitmap.createScaledBitmap(image, image.getWidth()/2,
				image.getHeight()/2, true);
		image.recycle();

		// bitmapMarkerImage.add(this.image);
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
		drawTextBlock(dw);
		
		if (isVisible) {
			float left = signMarker.x - (image.getWidth()) / 2;
			float top = signMarker.y - (image.getHeight()) / 2;

			// int rectangleBackgroundColor = Color.argb(255, 255, 255, 255);
			// dw.setColor(rectangleBackgroundColor);

			dw.paintBitmap(image, left, top);

		}
		
//		System.gc();
	}
}