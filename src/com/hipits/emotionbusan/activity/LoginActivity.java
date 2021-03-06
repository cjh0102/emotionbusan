package com.hipits.emotionbusan.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.hipits.emotionbusan.R;
import com.hipits.emotionbusan.manager.LoginManger;

public class LoginActivity extends Activity {
	
	private EditText idEditText;
	private EditText passwordEditText;
	private LoginManger loginManger;
	
	private static final int REQUEST_LOGIN_OK = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		init();
		
	}
	
	public void init() {
		idEditText = (EditText) findViewById(R.id.idEditText);
		passwordEditText = (EditText) findViewById(R.id.passwordEditText);
		loginManger = LoginManger.getInstance(this);
	}
	
	public void onClick(View view) {
		int viewId = view.getId();
		
		if (viewId == R.id.loginButton) {
			String id = idEditText.getText().toString().trim();
			String password = passwordEditText.getText().toString().trim();
			
			if (id.isEmpty() || id.equals("")) {
				Toast.makeText(this, "아이디를 입력 해주세요!", Toast.LENGTH_SHORT).show();
				return;
			} else if (password.isEmpty() || password.equals("")) {
				Toast.makeText(this, "비밀번호를 입력 해주세요!", Toast.LENGTH_SHORT).show();
				return;
			} else {
				loginManger.setContext(this);
				loginManger.signIn(id, password);
			}
		} else if (viewId == R.id.signupButton) {
			Intent intent = new Intent(this, SignUpActivity.class);
			startActivityForResult(intent, REQUEST_LOGIN_OK);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == REQUEST_LOGIN_OK) {
				String id = data.getStringExtra("id");
				idEditText.setText(id);
			}
		}
	}
}
