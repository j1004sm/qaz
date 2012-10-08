package org.mixare;

import com.qaz.client.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LoginActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);// [영기]
		Button startmain = (Button) findViewById(R.id.logbtn);
		Button startjoinactivity = (Button) findViewById(R.id.joinmember);
		
		startmain.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(LoginActivity.this, MixView.class);
				startActivity(i);
				finish();

			}
		});
		
		startjoinactivity.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(LoginActivity.this, JoinActivity.class);
				startActivity(i);

			}
		});
		
	}
}
