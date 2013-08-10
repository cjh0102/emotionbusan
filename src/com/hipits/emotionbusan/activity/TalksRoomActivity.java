package com.hipits.emotionbusan.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.hipits.emotionbusan.R;
import com.hipits.emotionbusan.adapter.TalksRoomListAdapter;
import com.hipits.emotionbusan.baasio.EtcUtils;
import com.hipits.emotionbusan.manager.LoginManger;
import com.kth.baasio.Baas;
import com.kth.baasio.callback.BaasioCallback;
import com.kth.baasio.callback.BaasioQueryCallback;
import com.kth.baasio.entity.BaasioBaseEntity;
import com.kth.baasio.entity.entity.BaasioEntity;
import com.kth.baasio.entity.user.BaasioUser;
import com.kth.baasio.exception.BaasioException;
import com.kth.baasio.query.BaasioQuery;
import com.kth.baasio.query.BaasioQuery.ORDER_BY;
import com.kth.baasio.utils.ObjectUtils;

public class TalksRoomActivity extends Activity {

	private BaasioQuery query;
	public static final String ENTITY_TYPE = "post";
	private List<BaasioEntity> posts;
	private TalksRoomListAdapter adapter;
	private ListView talksRoomListView;
	private List<BaasioEntity> comments;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_talksroom);

		getEntities();

		final LoginManger loginManger = LoginManger.getInstance(this);

		findViewById(R.id.writeButton).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						showInputDialog();
					}
				});
	}

	private void deletePost(final int position) {

		BaasioUser user = Baas.io().getSignedInUser();

		if (ObjectUtils.isEmpty(user)) {
			Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
			return;
		}

		BaasioEntity post = posts.get(position);

		if (!ObjectUtils.isEmpty(post)) {
			String postUserName = EtcUtils.getStringFromEntity(post,
					"writer_username");
			if (!user.getUsername().equals(postUserName)) {
				Toast.makeText(this, "삭제 권한이 없습니다.", Toast.LENGTH_SHORT).show();
				return;
			}
		}

		deleteComments(post);

		post.deleteInBackground(new BaasioCallback<BaasioEntity>() {

			@Override
			public void onResponse(BaasioEntity entity) {

				if (!ObjectUtils.isEmpty(entity)) {
					Toast.makeText(TalksRoomActivity.this, "삭제성공",
							Toast.LENGTH_SHORT).show();
					posts.remove(position);
					adapter.notifyDataSetChanged();
				}
			}

			@Override
			public void onException(BaasioException arg0) {
				Log.e("삭제실패", arg0.getMessage());
			}
		});
	}

	public void writePost(String title, String body) {

		if (ObjectUtils.isEmpty(Baas.io().getSignedInUser())) {
			Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
			return;
		}

		BaasioUser user = Baas.io().getSignedInUser();
		BaasioEntity entity = new BaasioEntity(ENTITY_TYPE);

		entity.setProperty("writer_username", user.getUsername());
		entity.setProperty("writer_uuid", user.getUuid().toString());
		entity.setProperty("title", title);
		entity.setProperty("commentCount", 0);

		if (!ObjectUtils.isEmpty(body)) {
			entity.setProperty("body", body);
		}

		entity.saveInBackground(new BaasioCallback<BaasioEntity>() {
			@Override
			public void onResponse(BaasioEntity response) {
				if (response != null) {
					posts.add(0, response);
					adapter.notifyDataSetChanged();
				}
			}

			@Override
			public void onException(BaasioException e) {
			}
		});
	}

	private void getEntities() {

		posts = new ArrayList<BaasioEntity>();
		comments = new ArrayList<BaasioEntity>();

		if (ObjectUtils.isEmpty(query)) {

			final ProgressDialog dialog = ProgressDialog.show(this, "알림",
					"데이터 로딩중");

			query = new BaasioQuery();
			query.setType(ENTITY_TYPE);
			query.setOrderBy(BaasioBaseEntity.PROPERTY_MODIFIED,
					ORDER_BY.DESCENDING);

			query.queryInBackground(new BaasioQueryCallback() {
				@Override
				public void onResponse(List<BaasioBaseEntity> entities,
						List<Object> list, BaasioQuery query, long timestamp) {

					posts = BaasioBaseEntity.toType(entities,
							BaasioEntity.class);

					dialog.dismiss();

					displayList();
				}

				@Override
				public void onException(BaasioException e) {
					dialog.dismiss();
					Toast.makeText(TalksRoomActivity.this,
							"queryInBackground =>" + e.toString(),
							Toast.LENGTH_LONG).show();
				}
			});
		}
	}

	private void deleteComments(BaasioEntity post) {

		if (ObjectUtils.isEmpty(Baas.io().getSignedInUser())) {
			Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
			return;
		}

		BaasioQuery query = new BaasioQuery();
		query.setType("comment");
		query.setRelation(post, "write_comment");
		query.queryInBackground(new BaasioQueryCallback() {

			@Override
			public void onResponse(List<BaasioBaseEntity> entities,
					List<Object> arg1, BaasioQuery arg2, long arg3) {

				comments = BaasioEntity.toType(entities, BaasioEntity.class);

				if (comments.isEmpty()) {
					return;
				}

				for (BaasioEntity comment : comments) {

					comment.deleteInBackground(new BaasioCallback<BaasioEntity>() {

						@Override
						public void onResponse(BaasioEntity arg0) {
							comments.remove(arg0);

						}

						@Override
						public void onException(BaasioException arg0) {

						}
					});
				}
			}

			@Override
			public void onException(BaasioException arg0) {

			}
		});

	}

	public void showInputDialog() {

		View view = getLayoutInflater()
				.inflate(R.layout.dialog_talksroom, null);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog;

		builder.setTitle("게시글 쓰기");
		builder.setView(view);

		dialog = builder.create();
		dialog.show();

		final EditText titleEditText = (EditText) view
				.findViewById(R.id.titleEditText);
		final EditText bodyEditText = (EditText) view
				.findViewById(R.id.bodyEditText);

		view.findViewById(R.id.writePostButton).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						String title = titleEditText.getText().toString()
								.trim();
						String body = bodyEditText.getText().toString().trim();

						if (title.equals("")|| title.isEmpty()) {
							Toast.makeText(TalksRoomActivity.this,
									"제목입력 해주세요!", Toast.LENGTH_SHORT).show();

						} else if (body.equals("") || body.isEmpty()) {
							Toast.makeText(TalksRoomActivity.this,
									"내용을 입력해주세요!", Toast.LENGTH_SHORT).show();
						} else {
							writePost(title, body);
							dialog.dismiss();
						}
					}
				});
	}

	public void displayList() {

		talksRoomListView = (ListView) findViewById(R.id.talksroomListView);
		adapter = new TalksRoomListAdapter(TalksRoomActivity.this, posts);
		talksRoomListView.setAdapter(adapter);

		talksRoomListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int index,
					long arg3) {
				Intent intent = new Intent(TalksRoomActivity.this,
						TalksRoomDetailActivity.class);
				intent.putExtra("post", posts.get(index).toString());
				startActivity(intent);
			}
		});

		talksRoomListView
				.setOnItemLongClickListener(new OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> arg0,
							View arg1, final int position, long arg3) {

						AlertDialog.Builder builder = new AlertDialog.Builder(
								TalksRoomActivity.this);

						builder.setTitle("삭제!");
						builder.setMessage("글을 삭제하시겠습니까?");

						builder.setPositiveButton("예",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										deletePost(position);
									}
								});

						builder.setNegativeButton("아니요",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {

									}
								});

						builder.create().show();

						return false;
					}
				});
	}
}
