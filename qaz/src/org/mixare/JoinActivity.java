package org.mixare;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.qaz.client.R;

public class JoinActivity extends Activity {
	private Button btn_check;
	private Button btn_cancel;
	private Button btn_terms;
	private EditText join_id_EditText;
	private EditText join_pw_EditText;
	private EditText join_pwc_EditText;
	private EditText join_name_EditText;
	private EditText join_mail_EditText;

	public static LoginActivity logAct;

	public boolean checkEmail(String email) {
		String regex = "^[_a-zA-Z0-9-\\.]+@[\\.a-zA-Z0-9-]+\\.[a-zA-Z]+$";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(email);
		boolean isNormal = m.matches();
		return isNormal;
	}
	
	// 영문만 허용
	protected InputFilter filterAlpha = new InputFilter() {
	        public CharSequence filter(CharSequence source, int start, int end,
	                                Spanned dest, int dstart, int dend) {

	                Pattern ps = Pattern.compile("^[a-zA-Z]+$");
	                if (!ps.matcher(source).matches()) {
	                        return "";
	                } 
	                return null;
	        } 
	}; 
	
	// 영문만 허용 (숫자 포함)
	protected InputFilter filterAlphaNum = new InputFilter() {
	        public CharSequence filter(CharSequence source, int start, int end,
	                                Spanned dest, int dstart, int dend) {

	                Pattern ps = Pattern.compile("^[a-zA-Z0-9]+$");
	                if (!ps.matcher(source).matches()) {
	                        return "";
	                } 
	                return null;
	        } 
	}; 
	
	// 한글만 허용
	public InputFilter filterKor = new InputFilter() {
	        public CharSequence filter(CharSequence source, int start, int end,
	                                Spanned dest, int dstart, int dend) {

	                Pattern ps = Pattern.compile("^[ㄱ-가-힣]+$");
	                if (!ps.matcher(source).matches()) {
	                        return "";
	                } 
	                return null;
	        } 
	}; 

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.join);
		
		btn_check = (Button) findViewById(R.id.join_button_check);
		btn_cancel = (Button) findViewById(R.id.join_button_cancel);
		btn_terms = (Button) findViewById(R.id.join_button_terms);
		
		join_id_EditText = (EditText) findViewById(R.id.join_id);
		join_id_EditText.setFilters(new InputFilter[] {filterAlphaNum});
		join_pw_EditText = (EditText) findViewById(R.id.join_pw);
		join_pw_EditText.setFilters(new InputFilter[] {filterAlphaNum});
		join_pwc_EditText = (EditText) findViewById(R.id.join_pwc);
		join_name_EditText = (EditText) findViewById(R.id.join_name);
		join_name_EditText.setFilters(new InputFilter[] {filterKor});
		join_mail_EditText = (EditText) findViewById(R.id.join_mail);

		
		btn_check.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String server_result = null, join_pw_code;
				String join_pw = join_pw_EditText.getText().toString();
				String join_pwc = join_pwc_EditText.getText().toString();
				String join_email = join_mail_EditText.getText().toString();
				String join_name = join_name_EditText.getText().toString();
				String join_id = join_id_EditText.getText().toString();

				if (join_pw.length() == 0 || join_id.length() == 0
						|| join_name.length() == 0 || join_email.length() == 0) {

					Toast.makeText(getApplicationContext(), "빈칸이 없는지 확인해주세요",
							1000).show();

				} else {
					if (!(join_pw.equals(join_pwc)))
						Toast.makeText(getApplicationContext(),
								"비밀번호를 다시 확인해주세요", Toast.LENGTH_SHORT).show();

					else if (!(checkEmail(join_email)))
						Toast.makeText(getApplicationContext(),
								"입력하신 E-Mail주소가 형식에 맞지 않습니다", Toast.LENGTH_SHORT).show();

					else {
						join_pw_code = logAct.getMD5Hash(join_pw);
						try {

							URL url = new URL(
									"http://www.manjong.org:8255/qaz/join.jsp");

							HttpURLConnection http = (HttpURLConnection) url
									.openConnection();

							http.setDefaultUseCaches(false);
							http.setDoInput(true);
							http.setDoOutput(true);
							http.setRequestMethod("POST");

							http.setRequestProperty("Content-type",
									"application/x-www-form-urlencoded");

							StringBuffer buffer = new StringBuffer();

							buffer.append("id").append("=").append(join_id)
									.append("&");
							buffer.append("encpw").append("=")
									.append(join_pw_code).append("&");
							buffer.append("name").append("=").append(join_name)
									.append("&");
							buffer.append("email").append("=")
									.append(join_email);

							OutputStreamWriter outStream = new OutputStreamWriter(
									http.getOutputStream(), "UTF-8");

							PrintWriter writer = new PrintWriter(outStream);
							writer.write(buffer.toString());

							writer.flush();

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
							server_result = result.trim();
						} catch (MalformedURLException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}

						if (server_result.equals("success")) {
							Toast.makeText(getApplicationContext(),
									"회원가입을 축하드립니다!", 1000).show();
							finish();
						} else {
							Toast.makeText(getApplicationContext(),
									"가입에 실패했습니다. 다른 아이디나 다른 이메일 주소로 다시 시도해보십시요.", 1000)
									.show();
						}
					}
				}
			}
		});

		btn_cancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

		btn_terms.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				AlertDialog.Builder builder1 = new AlertDialog.Builder(
						JoinActivity.this);
				builder1.setMessage(getString(R.string.terms_content));
				builder1.setNegativeButton(getString(R.string.close_button),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.dismiss();
							}
						});

				AlertDialog alert1 = builder1.create();
				alert1.setTitle(getString(R.string.terms_title));
				alert1.show();
			}
		});

	}
}
