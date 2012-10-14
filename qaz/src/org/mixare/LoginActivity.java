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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.qaz.client.R;

public class LoginActivity extends Activity {

	EditText login_id;
	EditText login_pw;
	String usrPw;
	String encPw;
	String server_result;
	remoteRequestTask server_login;
	Button btn_login;

	MixView mixView;
	public static String usrId;

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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		btn_login = (Button) findViewById(R.id.btn_login);
		Button btn_join = (Button) findViewById(R.id.btn_join);
		Button btn_findId = (Button) findViewById(R.id.btn_findId);
		login_id = (EditText) findViewById(R.id.login_id);
		login_pw = (EditText) findViewById(R.id.login_pw);

		btn_login.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
				usrId = login_id.getText().toString();
				usrPw = login_pw.getText().toString();
				encPw = getMD5Hash(usrPw);

				if (usrId.length() == 0 || usrPw.length() == 0) {
					Toast.makeText(getApplicationContext(),
							"아이디, 비밀번호 모두 입력해주세요", Toast.LENGTH_SHORT).show();
				} else {

					login_id.setEnabled(false);
					login_pw.setEnabled(false);
					btn_login.setEnabled(false);
					btn_login.setText("로그인 중...");

					server_login = new remoteRequestTask();
					server_login.execute();

				}

			}
		});

		btn_join.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(LoginActivity.this, JoinActivity.class);
				startActivity(i);

			}
		});

		btn_findId.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(Intent.ACTION_VIEW, Uri
						.parse("http://www.manjong.org:8255/qaz/find_id.jsp"));
				startActivity(i);

			}
		});

	}

	class remoteRequestTask extends AsyncTask<Void, Void, Void> {
		
		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			server_result = "";
			try {
				// URL설정, 접속
				URL url = new URL("http://www.manjong.org:8255/qaz/login.jsp");

				HttpURLConnection http = (HttpURLConnection) url
						.openConnection();

				http.setDefaultUseCaches(false);
				http.setDoInput(true);
				http.setDoOutput(true);
				http.setRequestMethod("POST");

				http.setRequestProperty("Content-type",
						"application/x-www-form-urlencoded");

				StringBuffer buffer = new StringBuffer();

				buffer.append("id").append("=").append(usrId).append("&");
				buffer.append("encpw").append("=").append(encPw);

				OutputStreamWriter outStream = new OutputStreamWriter(
						http.getOutputStream(), "EUC-KR");

				PrintWriter writer = new PrintWriter(outStream);
				writer.write(buffer.toString());

				writer.flush();

				InputStreamReader inputStream = new InputStreamReader(
						http.getInputStream(), "EUC-KR");
				BufferedReader bufferReader = new BufferedReader(inputStream);
				StringBuilder builder = new StringBuilder();
				String str;
				while ((str = bufferReader.readLine()) != null) {
					builder.append(str + "\n");
				}

				String result = builder.toString();
				Log.d("Qaz-HttpPost", "전송결과 : " + result);
				server_result = result.trim();
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		protected void onPostExecute(Void params) {
			if (server_result == "") {
				Toast.makeText(getApplicationContext(),
						"인터넷 연결 상태를 점검해주세요", Toast.LENGTH_LONG).show();
				
				login_id.setEnabled(true);
				login_pw.setEnabled(true);
				btn_login.setEnabled(true);
				btn_login.setEnabled(true);
				btn_login.setText("로그인");

			} else
				if (server_result.equals(encPw)) {
					Intent i = new Intent(LoginActivity.this, MixView.class);
					startActivity(i);

					Toast.makeText(getApplicationContext(),
							usrId + "님, 환영합니다!", Toast.LENGTH_LONG).show();

					finish();
				} else {
					Toast.makeText(getApplicationContext(),
							"아이디와 비밀번호를 올바로 입력했는지 확인해주세요", Toast.LENGTH_LONG).show();
					
					login_id.setEnabled(true);
					login_pw.setEnabled(true);
					btn_login.setEnabled(true);
					btn_login.setEnabled(true);
					btn_login.setText("로그인");
				}
		}
	}

}
