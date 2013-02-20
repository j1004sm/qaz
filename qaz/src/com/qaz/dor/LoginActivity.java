package com.qaz.dor;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.mixare.MixView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.qaz.client.R;

public class LoginActivity extends Activity {

	EditText login_id;
	EditText login_pw;

	String encPw;
	remoteRequestTask server_login;

	Button btn_login;
	Button btn_join;
	Button btn_findId;

	MixView mixView;

	public static String usrId;
	public static String usrPw;

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
		dalvik.system.VMRuntime.getRuntime().setTargetHeapUtilization(0.7f);
		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.login);

		btn_login = (Button) findViewById(R.id.btn_login);
		btn_join = (Button) findViewById(R.id.btn_join);
		btn_findId = (Button) findViewById(R.id.btn_findId);
		login_id = (EditText) findViewById(R.id.login_id);
		login_pw = (EditText) findViewById(R.id.login_pw);

		String strId, strPw;

		SharedPreferences settings = getSharedPreferences(
				"MyPrefsFileForMenuItems", 0);
		strId = settings.getString("id", "");
		strPw = settings.getString("pw", "");

		btn_login.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				usrId = login_id.getText().toString();
				usrPw = login_pw.getText().toString();

				if (usrId.length() == 0 || usrPw.length() == 0) {
					Toast.makeText(getApplicationContext(),
							"아이디, 비밀번호 모두 입력해주세요", Toast.LENGTH_SHORT).show();
				} else {

					btn_findId.setEnabled(false);
					btn_join.setEnabled(false);
					login_id.setEnabled(false);
					login_pw.setEnabled(false);
					btn_login.setEnabled(false);
					btn_login.setText("로그인 중...");

					encPw = getMD5Hash(usrPw);

					server_login = new remoteRequestTask();
					server_login.execute();

				}

			}
		});

		if (!(strId.equals(""))) {
			login_id.setText(strId);
			login_pw.setText(strPw);

			btn_login.performClick();
		}

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
						.parse(QazHttpServer.QAZ_URL_FINDID));
				startActivity(i);

			}
		});

	}

	class remoteRequestTask extends AsyncTask<Void, Void, Void> {
		String ret = "Qaz_Server_Not_Connected";

		@Override
		protected Void doInBackground(Void... arg0) {
			ret = QazHttpServer.RequestLogin(QazHttpServer.QAZ_URL_LOGIN, usrId, encPw);

			return null;
		}

		protected void onPostExecute(Void params) {
			if (ret.equals("Qaz_Server_Not_Connected")) {
				Toast.makeText(getApplicationContext(), "인터넷 연결 상태를 점검해주세요",
						Toast.LENGTH_LONG).show();

				btn_findId.setEnabled(true);
				btn_join.setEnabled(true);
				login_id.setEnabled(true);
				login_pw.setEnabled(true);
				btn_login.setEnabled(true);
				btn_login.setEnabled(true);
				btn_login.setText("로그인");

			} else if (ret.equals(encPw)) {
				Intent i = new Intent(LoginActivity.this, MixView.class);
				startActivity(i);

				Toast.makeText(getApplicationContext(), usrId + "님, 환영합니다!",
						Toast.LENGTH_LONG).show();

				finish();
			} else {
				Toast.makeText(getApplicationContext(),
						"아이디와 비밀번호를 올바로 입력했는지 확인해주세요", Toast.LENGTH_LONG)
						.show();

				btn_findId.setEnabled(true);
				btn_join.setEnabled(true);
				login_id.setEnabled(true);
				login_pw.setEnabled(true);
				btn_login.setEnabled(true);
				btn_login.setEnabled(true);
				btn_login.setText("로그인");
			}
		}
	}

}
