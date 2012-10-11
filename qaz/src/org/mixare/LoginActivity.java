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
import android.widget.Toast;

import com.qaz.client.R;

public class LoginActivity extends Activity {
	
	private EditText login_id;
	private EditText login_pw;
	
	public static String usrId;

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

				String server_result = null;
				usrId = login_id.getText().toString();
				String usrPw = login_pw.getText().toString();
				String encPw = getMD5Hash(usrPw);

				if (usrId.length() == 0 || usrPw.length() == 0) {
					Toast.makeText(getApplicationContext(),
							"아이디, 비밀번호 모두 입력해주세요", Toast.LENGTH_SHORT)
							.show();
				} else {

					// TODO qaz_pw_code 는 암호화된 비번이다 이것을 서버에 있는것과 맞추어야한다
					// ID : qaz_id, PW : qaz_pw_code

					try {
						// URL설정, 접속
						URL url = new URL(
								"http://www.manjong.org:8255/qaz/login.jsp");

						HttpURLConnection http = (HttpURLConnection) url
								.openConnection();

						// 전송모드 설정(일반적인 POST방식)
						http.setDefaultUseCaches(false);
						http.setDoInput(true);
						http.setDoOutput(true);
						http.setRequestMethod("POST");

						// content-type 설정
						http.setRequestProperty("Content-type",
								"application/x-www-form-urlencoded");

						// 전송값 설정
						StringBuffer buffer = new StringBuffer();

						buffer.append("id").append("=").append(usrId)
								.append("&");
						buffer.append("encpw").append("=").append(encPw);

						// 서버로 전송
						OutputStreamWriter outStream = new OutputStreamWriter(
								http.getOutputStream(), "EUC-KR");

						PrintWriter writer = new PrintWriter(outStream);
						writer.write(buffer.toString());

						writer.flush();

						// 전송 결과값 받기
						InputStreamReader inputStream = new InputStreamReader(
								http.getInputStream(), "EUC-KR");
						BufferedReader bufferReader = new BufferedReader(
								inputStream);
						StringBuilder builder = new StringBuilder();
						String str;
						while ((str = bufferReader.readLine()) != null) {
							builder.append(str + "\n");
						}

						String result = builder.toString();
						Log.d("Qaz-HttpPost", "전송결과 : " + result);
						server_result = result.trim();
					} catch (MalformedURLException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}

					if (server_result.equals(encPw)) {
						Intent i = new Intent(LoginActivity.this, MixView.class);
						startActivity(i);
						finish();
					} else {
						Toast.makeText(getApplicationContext(),
								"아이디와 비밀번호를 올바로 입력했는지 확인해주세요", 1000).show();
					}
				}

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
