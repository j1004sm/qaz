package org.mixare;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.qaz.client.R;

public class LoginActivity extends Activity {
	private EditText login_id;
	private EditText login_pw;

	// ------------------------------------------------------서버로 보내기
	public void HttpLoginJoinReq(String urlString, String i, String id,
			String encpw, String name, String email) {
		
	}

	// -----------------------------------------------------MD5
	public static String getMD5Hash(String s) {
		MessageDigest m = null;
		String hash = null;

		try {
			m = MessageDigest.getInstance("MD5");
			m.update(s.getBytes(), 0, s.length());
			hash = new BigInteger(1, m.digest()).toString(16);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return hash;
	}

	// -------------------------------------------------------
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);// [영기]
		Button startmain = (Button) findViewById(R.id.logbtn);
		Button startjoinactivity = (Button) findViewById(R.id.joinmember);
		login_id = (EditText) findViewById(R.id.login_id);
		login_pw = (EditText) findViewById(R.id.login_pw);

		startmain.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				String qaz_id = login_id.getText().toString();
				String qaz_pw = login_pw.getText().toString();
				String qaz_pw_code;
				Intent i = new Intent(LoginActivity.this, MixView.class);
				qaz_pw_code = getMD5Hash(qaz_pw);
				// TODO qaz_pw_code 는 암호화된 비번이다 이것을 서버에 있는것과 맞추어야한다
				// ID : qaz_id, PW : qaz_pw_code
				/*
				 * 
				 * if(yes){ startActivity(i); finish();}
				 */
				// HttpLoginJoinReq("http://www.manjong.org:8255/qaz/login.jsp",
				// "logincheck", qaz_id, qaz_pw_code, null, null);
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
