package com.qaz.dor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.qaz.client.R;

public class JoinActivity extends Activity {

	remoteRequestTask server_join;
	EditText join_id_EditText;
	EditText join_pw_EditText;
	EditText join_pwc_EditText;
	EditText join_name_EditText;
	EditText join_mail_EditText;
	Button btn_check;

	int server_result;
	String join_pw;
	String join_pwc;
	String join_email;
	String join_name;
	String join_id;
	String join_pw_code;

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
		join_id_EditText = (EditText) findViewById(R.id.join_id);
		join_id_EditText.setFilters(new InputFilter[] { filterAlphaNum });
		join_pw_EditText = (EditText) findViewById(R.id.join_pw);
		join_pw_EditText.setFilters(new InputFilter[] { filterAlphaNum });
		join_pwc_EditText = (EditText) findViewById(R.id.join_pwc);
		join_name_EditText = (EditText) findViewById(R.id.join_name);
		join_mail_EditText = (EditText) findViewById(R.id.join_mail);
		
		TextView txtTerms = (TextView) findViewById(R.id.txtTerms);
		TextView txtPriv = (TextView) findViewById(R.id.txtPriv);
		
		CheckBox chkAgree = (CheckBox) findViewById(R.id.chkAgree);
		
		btn_check.setEnabled(false);
		
		chkAgree.setOnCheckedChangeListener(new OnCheckedChangeListener(){
		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
		    {
		        if ( isChecked )
		        {
		        	btn_check.setEnabled(true);
		        } else {
		        	btn_check.setEnabled(false);
		        }

		    }
		});

		
		txtTerms.setOnClickListener(new TextView.OnClickListener(){
        	public void onClick(View v) {
        		Intent i = new Intent(Intent.ACTION_VIEW, Uri
						.parse("http://www.manjong.org:8255/qaz/terms.html"));
				startActivity(i);
        	}
        });
		
		txtPriv.setOnClickListener(new TextView.OnClickListener(){
        	public void onClick(View v) {
        		Intent i = new Intent(Intent.ACTION_VIEW, Uri
						.parse("http://www.manjong.org:8255/qaz/privacy.html"));
				startActivity(i);
        	}
        });

		btn_check.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				join_pw = join_pw_EditText.getText().toString();
				join_pwc = join_pwc_EditText.getText().toString();
				join_email = join_mail_EditText.getText().toString();
				join_name = join_name_EditText.getText().toString();
				join_id = join_id_EditText.getText().toString();

				if (join_pw.length() == 0 || join_id.length() == 0
						|| join_name.length() == 0 || join_email.length() == 0) {

					Toast.makeText(getApplicationContext(), "빈칸이 없는지 확인해주세요",
							Toast.LENGTH_SHORT).show();

				} else {
					if (!(join_pw.equals(join_pwc)))
						Toast.makeText(getApplicationContext(),
								"비밀번호를 다시 확인해주세요", Toast.LENGTH_LONG).show();

					else if (!(checkEmail(join_email)))
						Toast.makeText(getApplicationContext(),
								"올바른 E-Mail주소를 입력해주세요", Toast.LENGTH_LONG)
								.show();

					else {
						btn_check.setEnabled(false);
						btn_check.setText("처리 중...");

						join_pw_code = LoginActivity.getMD5Hash(join_pw);

						server_join = new remoteRequestTask();
						server_join.execute();

					}
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

				URL url = new URL("http://www.manjong.org:8255/qaz/join.jsp");

				HttpURLConnection http = (HttpURLConnection) url
						.openConnection();

				http.setDefaultUseCaches(false);
				http.setDoInput(true);
				http.setDoOutput(true);
				http.setRequestMethod("POST");

				http.setRequestProperty("Content-type",
						"application/x-www-form-urlencoded");

				StringBuffer buffer = new StringBuffer();

				buffer.append("id").append("=").append(join_id).append("&");
				buffer.append("encpw").append("=").append(join_pw_code)
						.append("&");
				buffer.append("name").append("=").append(join_name).append("&");
				buffer.append("email").append("=").append(join_email);

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
//				Log.d("Qaz-HttpPost", "result : " + result);
				server_result = Integer.parseInt(result.trim());
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		protected void onPostExecute(Void params) {
			if (server_result == 0) {
				Toast.makeText(getApplicationContext(),
						"가입에 실패했습니다. 다른 아이디나 다른 이메일 주소로 다시 시도해보세요.", Toast.LENGTH_LONG)
						.show();

				btn_check.setEnabled(true);
				btn_check.setText("약관 동의 및 가입");
			} else {
				Toast.makeText(getApplicationContext(), "회원가입을 축하드립니다!", Toast.LENGTH_LONG)
						.show();

				finish();

			}
		}

	}
}
