package com.hipits.emotionbusan.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hipits.emotionbusan.R;
import com.hipits.emotionbusan.adapter.CommentListAdapter;
import com.hipits.emotionbusan.baasio.EtcUtils;
import com.kth.baasio.Baas;
import com.kth.baasio.callback.BaasioCallback;
import com.kth.baasio.callback.BaasioQueryCallback;
import com.kth.baasio.entity.BaasioBaseEntity;
import com.kth.baasio.entity.entity.BaasioEntity;
import com.kth.baasio.entity.user.BaasioUser;
import com.kth.baasio.exception.BaasioException;
import com.kth.baasio.query.BaasioQuery;
import com.kth.baasio.utils.JsonUtils;
import com.kth.baasio.utils.ObjectUtils;

public class TalksRoomDetailActivity extends Activity {

	private BaasioEntity postEntity;
	private List<BaasioEntity> comments;
	private CommentListAdapter adapter;
	
	@Override
	public void onBackPressed() {
		Intent intent = new Intent(this, TalksRoomActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

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
						showWriteCommentDialog();
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
	
	public void showWriteCommentDialog() {
		
		View view = getLayoutInflater().inflate(R.layout.dialog_comment, null);
		
		final AlertDialog dialog;
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("댓글 달기");
		builder.setView(view);
		
		dialog = builder.create();
		dialog.show();
		
		final EditText commentEditText = (EditText) view.findViewById(R.id.bodyEditText);
		
		view.findViewById(R.id.writeCommentButton).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String commentBody = commentEditText.getText().toString().trim();
				if (commentBody.isEmpty() || commentBody.equals("")) {
					Toast.makeText(TalksRoomDetailActivity.this,
							"댓글을 입력해주세요!", Toast.LENGTH_SHORT).show();
				} else {
					writeComment(commentBody);
					dialog.dismiss();
				}
			}
		});
	}
	

	public void writeComment(String comment) {
		
		if (ObjectUtils.isEmpty(Baas.io().getSignedInUser())) {
			Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
			return;
		}
		
		BaasioUser user = Baas.io().getSignedInUser();

		BaasioEntity commentEntity = new BaasioEntity("comment");

		commentEntity.setProperty("body", comment);
		commentEntity.setProperty("writer_username", user.getUsername());
		commentEntity.saveInBackground(new BaasioCallback<BaasioEntity>() {
			@Override
			public void onException(BaasioException arg0) {
				Log.e("exception", arg0.getMessage());

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
		
		postEntity.setProperty("commentCount", comments.size() + 1);
		postEntity.updateInBackground(new BaasioCallback<BaasioEntity>() {
			
			@Override
			public void onResponse(BaasioEntity entity) {
				Log.d("junhwan", "성공");
			}
			
			@Override
			public void onException(BaasioException arg0) {
				Log.d("junhwan", "실패");
			}
		});
	}
}
