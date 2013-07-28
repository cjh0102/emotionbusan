package com.hipits.emotionbusan.activity;

import com.hipits.emotionbusan.R;
import com.hipits.emotionbusan.baasio.EtcUtils;
import com.kth.baasio.entity.entity.BaasioEntity;
import com.kth.baasio.utils.JsonUtils;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class CafeDetailActivity extends Activity {
	
	private TextView cafeNameTextView;
	private TextView writeIdTextView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cafedetail);
		
		initId();
		
		String cafe = getIntent().getStringExtra("cafe");
		setData(cafe);
		
	}
	
	public void initId() {
		cafeNameTextView = (TextView) findViewById(R.id.cafeNameTextView);
		writeIdTextView = (TextView) findViewById(R.id.writeIdTextView);
	}
	
	public void setData(String cafe) {
		BaasioEntity cafeEntity = JsonUtils.parse(cafe, BaasioEntity.class);
		
		String cafeName = EtcUtils.getStringFromEntity(cafeEntity, "cafeName");
		String writeId = EtcUtils.getStringFromEntity(cafeEntity, "writer_username");
		
		cafeNameTextView.append(cafeName);
		writeIdTextView.append(writeId);
	}
}
