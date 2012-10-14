package org.mixare;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.location.Location;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qaz.client.R;

public class QazPaintBoardActivity extends Activity {
	
	QazPaintBoard board;
	Button colorBtn;
	Button penBtn;
	Button eraserBtn;
	Button undoBtn;
	Button saveBtn;
	Button cancelBtn;
	
	LinearLayout addedLayout;
	Button colorLegendBtn;
	TextView sizeLegendTxt;

	String usrId = LoginActivity.usrId;
	
	int mColor = 0xff000000;
	int mSize = 2;
	int oldColor;
	int oldSize;
	boolean eraserSelected = false;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.paint);
        
        LinearLayout toolsLayout = (LinearLayout) findViewById(R.id.toolsLayout);
        LinearLayout boardLayout = (LinearLayout) findViewById(R.id.boardLayout);
        colorBtn = (Button) findViewById(R.id.colorBtn);
        penBtn = (Button) findViewById(R.id.penBtn);
        eraserBtn = (Button) findViewById(R.id.eraserBtn);
        undoBtn = (Button) findViewById(R.id.undoBtn);
        saveBtn = (Button) findViewById(R.id.saveBtn);
        cancelBtn = (Button) findViewById(R.id.cancelBtn);
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
        		LinearLayout.LayoutParams.FILL_PARENT,
        		LinearLayout.LayoutParams.FILL_PARENT);
        
        board = new QazPaintBoard(this);
        board.setLayoutParams(params);
        board.setPadding(2, 2, 2, 2);
        
        boardLayout.addView(board);
        
        // add legend buttons
        LinearLayout.LayoutParams addedParams = new LinearLayout.LayoutParams(
        		LinearLayout.LayoutParams.FILL_PARENT,
        		LinearLayout.LayoutParams.FILL_PARENT);
        
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
        		LinearLayout.LayoutParams.FILL_PARENT,
        		LinearLayout.LayoutParams.WRAP_CONTENT);
        addedLayout = new LinearLayout(this);
        addedLayout.setLayoutParams(addedParams);
        addedLayout.setOrientation(LinearLayout.VERTICAL);
        addedLayout.setPadding(8,8,8,8);
        
        LinearLayout outlineLayout = new LinearLayout(this);
        outlineLayout.setLayoutParams(buttonParams);
        outlineLayout.setOrientation(LinearLayout.VERTICAL);
        outlineLayout.setBackgroundColor(Color.LTGRAY);
        outlineLayout.setPadding(1,1,1,1);
        
        colorLegendBtn = new Button(this);
        colorLegendBtn.setLayoutParams(buttonParams);
        colorLegendBtn.setText(" ");
        colorLegendBtn.setBackgroundColor(mColor);
        colorLegendBtn.setHeight(20);
        outlineLayout.addView(colorLegendBtn);
        addedLayout.addView(outlineLayout);
        
        sizeLegendTxt = new TextView(this);
        sizeLegendTxt.setLayoutParams(buttonParams);
        sizeLegendTxt.setText("굵기 : " + mSize);
        sizeLegendTxt.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        sizeLegendTxt.setTextSize(16);
        sizeLegendTxt.setTextColor(Color.BLACK);
        addedLayout.addView(sizeLegendTxt);
        
        toolsLayout.addView(addedLayout);
        
        
        colorBtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		
        		ColorPaletteDialog.listener = new OnColorSelectedListener() {
        			public void onColorSelected(int color) {
        				mColor = color;
        				board.updatePaintProperty(mColor, mSize);
        				displayPaintProperty();
        			}
        		};
        		
        		
        		// show color palette dialog
        		Intent intent = new Intent(getApplicationContext(), ColorPaletteDialog.class);
        		startActivity(intent);
        		
        	}
        });
        
        penBtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		
        		PenPaletteDialog.listener = new OnPenSelectedListener() {
        			public void onPenSelected(int size) {
        				mSize = size;
        				board.updatePaintProperty(mColor, mSize);
        				displayPaintProperty();
        			}
        		};
        		
        		
        		// show pen palette dialog
        		Intent intent = new Intent(getApplicationContext(), PenPaletteDialog.class);
        		startActivity(intent);
        		
        	}
        });
        
        eraserBtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		
        		eraserSelected = !eraserSelected;
        		
        		if (eraserSelected) {
                    colorBtn.setEnabled(false);
                    penBtn.setEnabled(false);
                    undoBtn.setEnabled(false);
        			
                    colorBtn.invalidate();
                    penBtn.invalidate();
                    undoBtn.invalidate();
                    
                    oldColor = mColor;
                    oldSize = mSize;
        			
                    mColor = Color.WHITE;
                    mSize = 2;
                    
                    board.erasePaintProperty();
                    displayPaintProperty();
                    
                } else {
                	colorBtn.setEnabled(true);
                    penBtn.setEnabled(true);
                    undoBtn.setEnabled(true);
        			
                    colorBtn.invalidate();
                    penBtn.invalidate();
                    undoBtn.invalidate();
                    
                    mColor = oldColor;
                    mSize = oldSize;
                    
                    board.updatePaintProperty(mColor, mSize);
                    displayPaintProperty();
                    
                }
        		
        	}
        });
        
        undoBtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		
        		board.undo();
        		
        	}
        });
        
        saveBtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        	
        		
        		AlertDialog.Builder alert = new AlertDialog.Builder(QazPaintBoardActivity.this);

        		alert.setTitle("그림 저장");
        		alert.setMessage("그림 제목을 입력해주세요 :");

        		// Set an EditText view to get user input
        		final EditText input = new EditText(QazPaintBoardActivity.this);
        		alert.setView(input);

        		alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
        			public void onClick(DialogInterface dialog, int whichButton) {
        				
        				String value = input.getText().toString();
        				Location curLoc = MixView.mixContext.getCurrentGPSInfo();
        				double lat = curLoc.getLatitude(), lon = curLoc.getLongitude(), alt = curLoc.getAltitude();
        				
        				if (value.length() == 0){
        					Toast.makeText(getApplicationContext(),"하나 이상의 문자를 입력해주십시요", Toast.LENGTH_LONG).show();
        				} else {
        				
        					String resServer = new String();
        					resServer = board.SaveBitmapToFileUpload(QazPaintBoardActivity.this.getFileStreamPath(value.toString() + ".png"), value.toString(), lat, lon, alt, usrId);
        					
        					if (resServer.equals("success")) {
        						Toast.makeText(getApplicationContext(), value.toString() + "이(가) 저장되었습니다", Toast.LENGTH_SHORT).show();
        						finish();
        					} else {
        						Toast.makeText(getApplicationContext(), "서버와의 연결에 실패했습니다", Toast.LENGTH_LONG).show();
        					}
        					
  
        				}
        			}
        		});
        		alert.setNegativeButton("취소",
        				new DialogInterface.OnClickListener() {
        					public void onClick(DialogInterface dialog, int whichButton) {
        					}
        				});
        		
        		alert.show();
        	}
        });
        
        cancelBtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		finish();
        	}
        });   
    }
    
        
    public int getChosenColor() {
    	return mColor;
    }
    
    public int getPenThickness() {
    	return mSize;
    }
    
    private void displayPaintProperty() {
    	colorLegendBtn.setBackgroundColor(mColor);
    	sizeLegendTxt.setText("굵기 : " + mSize);
    	
    	addedLayout.invalidate();
    }
    
}