package org.mixare;

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

	int server_result = 0;
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

				if (!(usrNowPw.equals(LoginActivity.usrPw))) {
					Toast.makeText(getApplicationContext(), "현재 비밀번호를 틀렸습니다",
							Toast.LENGTH_LONG).show();
				} else if (!(usrNewPw.equals(usrNewPwChk))) {
					Toast.makeText(getApplicationContext(), "새 비밀번호 확인이 맞지 않습니다",
							Toast.LENGTH_LONG).show();
				} else {
					btnCheck.setEnabled(false);
					btnCheck.setText("처리 중...");
					encPw = LoginActivity.getMD5Hash(usrNewPw);
					
					server_request = new remoteRequestTask();
					server_request.execute();
				}

			}
		});
	}

	class remoteRequestTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			server_result = 0;
			try {
				// URL설정, 접속
				URL url = new URL(
						"http://www.manjong.org:8255/qaz/pwchange.jsp");

				HttpURLConnection http = (HttpURLConnection) url
						.openConnection();

				http.setDefaultUseCaches(false);
				http.setDoInput(true);
				http.setDoOutput(true);
				http.setRequestMethod("POST");

				http.setRequestProperty("Content-type",
						"application/x-www-form-urlencoded");

				StringBuffer buffer = new StringBuffer();

				buffer.append("id").append("=").append(LoginActivity.usrId)
						.append("&");
				buffer.append("encpw").append("=").append(encPw);

				OutputStreamWriter outStream = new OutputStreamWriter(
						http.getOutputStream(), "UTF-8");

				PrintWriter writer = new PrintWriter(outStream);
				writer.write(buffer.toString());

				writer.flush();

				InputStreamReader inputStream = new InputStreamReader(
						http.getInputStream(), "UTF-8");
				BufferedReader bufferReader = new BufferedReader(inputStream);
				StringBuilder builder = new StringBuilder();
				String str;
				while ((str = bufferReader.readLine()) != null) {
					builder.append(str + "\n");
				}

				String result = builder.toString();
				Log.d("Qaz-HttpPost", "전송결과 : " + result);
				server_result = Integer.parseInt(result.trim());
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		protected void onPostExecute(Void params) {
			if (server_result == 0) {
				Toast.makeText(getApplicationContext(), "비밀번호 변경에 실패했습니다. 나중에 다시시도 해주십시요.",
						Toast.LENGTH_LONG).show();

				btnCheck.setEnabled(true);
				btnCheck.setText("확인");

			} else {
				Toast.makeText(getApplicationContext(), "비밀번호가 성공적으로 변경되었습니다",
						Toast.LENGTH_LONG).show();

				finish();
			}
		}
	}
}
