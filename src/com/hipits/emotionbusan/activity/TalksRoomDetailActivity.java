package com.hipits.emotionbusan.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.hipits.emotionbusan.R;
import com.hipits.emotionbusan.baasio.EtcUtils;
import com.kth.baasio.entity.entity.BaasioEntity;
import com.kth.baasio.utils.JsonUtils;
import com.kth.baasio.utils.ObjectUtils;

public class TalksRoomDetailActivity extends Activity {

	private BaasioEntity entity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_talksroomdetail);

		Intent intent = getIntent();

		String post = intent.getStringExtra("post");

		if (!ObjectUtils.isEmpty(post)) {
			entity = JsonUtils.parse(post, BaasioEntity.class);
		}

		TextView titleTextView = (TextView) findViewById(R.id.titleTextView);
		TextView contentTextView = (TextView) findViewById(R.id.contentTextView);
		TextView timeTextView = (TextView) findViewById(R.id.timeTextView);

		setStringToView(entity, titleTextView, "title");
		setStringToView(entity, contentTextView, "body");

		if (entity.getCreated() != null) {
			String createdTime = EtcUtils.getDateString(entity.getCreated());
			timeTextView.setText(createdTime);
		}
		
		ListView commentListView = (ListView) findViewById(R.id.commentListView);
		
		findViewById(R.id.writeButton).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				
			}
		});
	}

	private void setStringToView(BaasioEntity entity, TextView view,
			String value) {
		view.setText(EtcUtils.getStringFromEntity(entity, value));
	}
}
