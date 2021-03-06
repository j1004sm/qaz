package com.qaz.dor;

import org.mixare.MixView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.qaz.client.R;

public class ProfileActivity extends Activity {
	
	remoteRequestTask server_request;

	private ListView _listview;
	private String[] items = { "비밀번호 변경", "회원탈퇴" };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile);

		_listview = (ListView) findViewById(R.id.profileList);

		_listview.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, items));
		_listview.setOnItemClickListener(onItemClickListener);
	}

	private OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long id) {
			// BaseAdapter 를 상속해서 어댑터를 만든다면 getItemId에서 다른 값을 줘서 more_code를 다른
			// 숫자로 바꿔서 사용가능하다
			switch (position) {
			case 0:
				Intent i = new Intent(ProfileActivity.this,
						PwChangeActivity.class);
				startActivity(i);
				finish();
				break;
			case 1:
				// 얼럿 다이얼로그의 빌더를 생성
				AlertDialog.Builder builder1 = new AlertDialog.Builder(
						ProfileActivity.this);
				// 텍스트를 등록한다
				builder1.setMessage(getString(R.string.askExit));

				//탈퇴
				builder1.setPositiveButton(getString(R.string.ok_button),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								server_request = new remoteRequestTask();
								server_request.execute();
								dialog.dismiss();
							}
						});

				//취소
				builder1.setNegativeButton(getString(R.string.cancel_button),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.dismiss();
							}
						});
				AlertDialog alert1 = builder1.create();
				alert1.setTitle(getString(R.string.exitTitle));
				alert1.show();
				break;
			}
		}
	};
	

	class remoteRequestTask extends AsyncTask<Void, Void, Void> {
		int ret = QazHttpServer.QAZ_SERVER_FAIL;
		
		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			ret = QazHttpServer.RequestSecession(QazHttpServer.QAZ_URL_SECESSION);

			return null;
		}

		protected void onPostExecute(Void params) {
			if (ret == QazHttpServer.QAZ_SERVER_FAIL) {
				Toast.makeText(getApplicationContext(), "회원탈퇴에 실패했습니다. 나중에 다시시도 해주십시요.",
						Toast.LENGTH_LONG).show();

			} else if (ret == QazHttpServer.QAZ_SERVER_SUCCESS) {
				Toast.makeText(getApplicationContext(), "회원탈퇴에 성공했습니다. 어플리케이션을 종료합니다.",
						Toast.LENGTH_LONG).show();
				
				MixView mixview = (MixView)MixView.mixView;
				mixview.finish();
				
				System.exit(0);
			}
		}
	}

}
