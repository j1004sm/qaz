package org.mixare;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.qaz.client.R;

public class JoinActivity extends Activity {
	private Button check;
	private Button cancle;
	private EditText join_id;
	private EditText join_pw;
	private EditText join_pwc;
	private EditText join_name;
	private EditText join_mail;
	//-------------------------------------------------------email형식체크함수
	public boolean checkEmail(String email){

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
		join_id = (EditText) findViewById(R.id.join_id);
		join_pw = (EditText) findViewById(R.id.join_pw);
		join_pwc = (EditText) findViewById(R.id.join_pwc);
		join_name = (EditText) findViewById(R.id.join_name);
		join_mail = (EditText) findViewById(R.id.join_mail);
		
		//-----------------------------------------------------------확인시
		check.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				String pw = join_pw.getText().toString();
				String pwc = join_pwc.getText().toString();
				String email = join_mail.getText().toString();
				if(!(pw.equals(pwc)))
					Toast.makeText(getApplicationContext(), "비밀번호가 서로 다릅니다", Toast.LENGTH_SHORT).show();
				else if(!(checkEmail(email)))
						Toast.makeText(getApplicationContext(), "E-Mail형식에 맞지않습니다", Toast.LENGTH_SHORT).show();
				else
					finish();

			}
		});
		//-----------------------------------------------------------취소시
		cancle.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				finish();

			}
		});
		
	}
}
