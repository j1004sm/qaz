package com.qaz.dor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.qaz.client.R;

public class PwChangeActivity extends Activity {

	EditText txtNowPw;
	EditText txtNewPw;
	EditText txtNewPwChk;

	Button btnCheck;
	String encPw;

	remoteRequestTask server_request;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.password);

		txtNowPw = (EditText) findViewById(R.id.txtNowPw);
		txtNewPw = (EditText) findViewById(R.id.txtNewPw);
		txtNewPwChk = (EditText) findViewById(R.id.txtNewPwChk);
		btnCheck = (Button) findViewById(R.id.btnCheck);

		btnCheck.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				String usrNowPw = txtNowPw.getText().toString();
				String usrNewPw = txtNewPw.getText().toString();
				String usrNewPwChk = txtNewPwChk.getText().toString();

				if (usrNowPw.length() == 0 || usrNewPw.length() == 0
						|| usrNewPwChk.length() == 0) {
					Toast.makeText(getApplicationContext(), "빈칸을 모두 채워주세요",
							Toast.LENGTH_LONG).show();
				} else {

					if (!(usrNowPw.equals(LoginActivity.usrPw))) {
						Toast.makeText(getApplicationContext(),
								"현재 비밀번호를 틀렸습니다", Toast.LENGTH_LONG).show();
					} else if (!(usrNewPw.equals(usrNewPwChk))) {
						Toast.makeText(getApplicationContext(),
								"새 비밀번호 확인이 맞지 않습니다", Toast.LENGTH_LONG).show();
					} else {
						btnCheck.setEnabled(false);
						btnCheck.setText("처리 중...");
						encPw = LoginActivity.getMD5Hash(usrNewPw);

						server_request = new remoteRequestTask();
						server_request.execute();
					}
				}
			}
		});
	}

	class remoteRequestTask extends AsyncTask<Void, Void, Void> {
		int ret = QazHttpServer.QAZ_SERVER_FAIL;

		@Override
		protected Void doInBackground(Void... arg0) {
			ret = QazHttpServer.RequestPasswordChanging(QazHttpServer.QAZ_URL_PWCHANGE, encPw);

			return null;
		}

		protected void onPostExecute(Void params) {
			if (ret == QazHttpServer.QAZ_SERVER_FAIL) {
				Toast.makeText(getApplicationContext(),
						"비밀번호 변경에 실패했습니다. 나중에 다시 시도해주십시요.", Toast.LENGTH_LONG)
						.show();

				btnCheck.setEnabled(true);
				btnCheck.setText("확인");

			} else if (ret == QazHttpServer.QAZ_SERVER_SUCCESS) {
				Toast.makeText(getApplicationContext(), "비밀번호가 성공적으로 변경되었습니다",
						Toast.LENGTH_LONG).show();

				finish();
			}
		}
	}
}
