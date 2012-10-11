package org.mixare;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.mixare.LoginActivity;

import com.qaz.client.R;

public class JoinActivity extends Activity {
	private Button check;
	private Button cancle;
	private EditText join_id_EditText;
	private EditText join_pw_EditText;
	private EditText join_pwc_EditText;
	private EditText join_name_EditText;
	private EditText join_mail_EditText;

	public static LoginActivity logAct;

	// -------------------------------------------------------email형식체크함수
	public boolean checkEmail(String email) {

		String regex = "^[_a-zA-Z0-9-\\.]+@[\\.a-zA-Z0-9-]+\\.[a-zA-Z]+$";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(email);
		boolean isNormal = m.matches();
		return isNormal;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.join);
		check = (Button) findViewById(R.id.join_button_check);
		cancle = (Button) findViewById(R.id.join_button_cancle);
		join_id_EditText = (EditText) findViewById(R.id.join_id);
		join_pw_EditText = (EditText) findViewById(R.id.join_pw);
		join_pwc_EditText = (EditText) findViewById(R.id.join_pwc);
		join_name_EditText = (EditText) findViewById(R.id.join_name);
		join_mail_EditText = (EditText) findViewById(R.id.join_mail);

		// -----------------------------------------------------------확인시
		check.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				String join_pw = join_pw_EditText.getText().toString();
				String join_pwc = join_pwc_EditText.getText().toString();
				String join_email = join_mail_EditText.getText().toString();
				String join_name = join_name_EditText.getText().toString();
				String join_id = join_id_EditText.getText().toString();
				String join_pw_code;

				if (join_pw.length() == 0 && join_id.length() == 0
						&& join_name.length() == 0 && join_email.length() == 0) {
					
					Toast.makeText(getApplicationContext(), "빈칸이 없는지 확인해주십시요",
							1000).show();

				} else {
					if (!(join_pw.equals(join_pwc)))
						Toast.makeText(getApplicationContext(),
								"비밀번호가 서로 다릅니다", Toast.LENGTH_SHORT).show();
					/*
					 * else if (!(checkEmail(join_email)))
					 * Toast.makeText(getApplicationContext(),
					 * "E-Mail형식에 맞지않습니다", Toast.LENGTH_SHORT).show();
					 */

					else {
						join_pw_code = logAct.getMD5Hash(join_pw);
						try {

							// URL설정, 접속
							URL url = new URL(
									"http://www.manjong.org:8255/qaz/join.jsp");

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

							buffer.append("id").append("=").append(join_id)
									.append("&");
							buffer.append("encpw").append("=")
									.append(join_pw_code).append("&");
							buffer.append("name").append("=").append(join_name)
									.append("&");
							buffer.append("email").append("=")
									.append(join_email);

							// 서버로 전송
							OutputStreamWriter outStream = new OutputStreamWriter(
									http.getOutputStream(), "UTF-8");

							PrintWriter writer = new PrintWriter(outStream);
							writer.write(buffer.toString());

							writer.flush();

							// 전송 결과값 받기
							InputStreamReader inputStream = new InputStreamReader(
									http.getInputStream(), "UTF-8");
							BufferedReader bufferReader = new BufferedReader(
									inputStream);
							StringBuilder builder = new StringBuilder();
							String str;
							while ((str = bufferReader.readLine()) != null) {
								builder.append(str + "\n");
							}

							String result = builder.toString();
							Log.d("Qaz-HttpPost", "전송결과 : " + result);
						} catch (MalformedURLException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}

						/*
						 * logAct.HttpLoginJoinReq(
						 * "http://www.manjong.org:8255/qaz/join.jsp", "join",
						 * join_id, join_pw_code, join_name, join_email);
						 */

						finish();
					}
				}
			}
		});
		// -----------------------------------------------------------취소시
		cancle.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				finish();

			}
		});
	}
}
