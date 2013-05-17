package com.hipits.emotionbusan.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hipits.emotionbusan.R;
import com.hipits.emotionbusan.adapter.CommentListAdapter;
import com.hipits.emotionbusan.baasio.EtcUtils;
import com.hipits.emotionbusan.manager.LoginManger;
import com.kth.baasio.callback.BaasioCallback;
import com.kth.baasio.callback.BaasioQueryCallback;
import com.kth.baasio.entity.BaasioBaseEntity;
import com.kth.baasio.entity.entity.BaasioEntity;
import com.kth.baasio.exception.BaasioError;
import com.kth.baasio.exception.BaasioException;
import com.kth.baasio.query.BaasioQuery;
import com.kth.baasio.utils.JsonUtils;
import com.kth.baasio.utils.ObjectUtils;

public class TalksRoomDetailActivity extends Activity {

	private BaasioEntity postEntity;
	private List<BaasioEntity> comments;
	private CommentListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_talksroomdetail);

		//이전 게시글의 entity를 받음
		Intent intent = getIntent();
		String post = intent.getStringExtra("post");

		if (!ObjectUtils.isEmpty(post)) {
			postEntity = JsonUtils.parse(post, BaasioEntity.class);
		}
		

		TextView titleTextView = (TextView) findViewById(R.id.titleTextView);
		TextView contentTextView = (TextView) findViewById(R.id.contentTextView);
		TextView timeTextView = (TextView) findViewById(R.id.timeTextView);

		comments = new ArrayList<BaasioEntity>();
		
		getCommentEntities();
		
		setStringToView(postEntity, titleTextView, "title");
		setStringToView(postEntity, contentTextView, "body");

		if (postEntity.getCreated() != null) {
			String createdTime = EtcUtils
					.getDateString(postEntity.getCreated());
			timeTextView.setText(createdTime);
		}

		findViewById(R.id.writeCommentButton).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						String body = "comment";
						writeComment(body);
					}
				});
	}

	private void getCommentEntities() {
		BaasioQuery query = new BaasioQuery();
		query.setType("comment");
		query.setRelation(postEntity, "write_comment");
		query.queryInBackground(new BaasioQueryCallback() {

			@Override
			public void onResponse(List<BaasioBaseEntity> entities,
					List<Object> arg1, BaasioQuery arg2, long arg3) {

				comments = BaasioBaseEntity.toType(entities, BaasioEntity.class);
				displayCommentList();
			}

			@Override
			public void onException(BaasioException exception) {
				Log.e("queryException", exception.getMessage());
			}
		});

	}
	
	public void displayCommentList() {
		ListView commentListView = (ListView) findViewById(R.id.commentListView);
		adapter = new CommentListAdapter(TalksRoomDetailActivity.this, comments);
		commentListView.setAdapter(adapter);
	}

	private void setStringToView(BaasioEntity entity, TextView view,
			String value) {
		view.setText(EtcUtils.getStringFromEntity(entity, value));
	}

	public void writeComment(String comment) {

		LoginManger.getInstance(this).signIn("oprt12@gmail.com", "1234");

		BaasioEntity commentEntity = new BaasioEntity("comment");
		commentEntity.setProperty("body", comment);
		commentEntity.saveInBackground(new BaasioCallback<BaasioEntity>() {
			@Override
			public void onException(BaasioException arg0) {

			}

			@Override
			public void onResponse(BaasioEntity response) {
				postEntity.connectInBackground("write_comment", response,
						new BaasioCallback<BaasioEntity>() {

					@Override
					public void onException(BaasioException exception) {
						Log.e("exception", exception.getMessage());
					}

					@Override
					public void onResponse(BaasioEntity response) {
						if (!ObjectUtils.isEmpty(response)) {
							comments.add(0, response);
							adapter.notifyDataSetChanged();
						}
					}
				});
			}
		});
	}
}
