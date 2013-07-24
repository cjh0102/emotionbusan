package com.hipits.emotionbusan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.hipits.emotionbusan.activity.RegistrationCafeActivity;
import com.hipits.emotionbusan.activity.TalksRoomActivity;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void onClick(View view) {
		int id = view.getId();
		
		Intent intent = null;
		
		if (id == R.id.talksRoomButton) {
			intent = new Intent(this, TalksRoomActivity.class);
		} else if (id == R.id.registerCafeButton) {
			intent = new Intent(this, RegistrationCafeActivity.class); 
		}
		
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
}
