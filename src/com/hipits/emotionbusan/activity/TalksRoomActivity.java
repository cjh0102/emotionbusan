package com.hipits.emotionbusan.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.hipits.emotionbusan.R;
import com.hipits.emotionbusan.adapter.TalksRoomListAdapter;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_talksroom);

		getEntities();

		final LoginManger loginManger = LoginManger.getInstance(this);
		final EditText idEditText = (EditText) findViewById(R.id.idEditText);
        final EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);

		findViewById(R.id.loginButton).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loginManger.signIn(idEditText.getText().toString()
                                .trim(), passwordEditText.getText().toString()
                                .trim());

                        if (loginManger.getIsLogin()) {
                            Toast.makeText(TalksRoomActivity.this, "로긴성공",
                                    Toast.LENGTH_SHORT).show();
                            getEntities();

                        }
                    }
                });

		findViewById(R.id.writeButton).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						writePost("title", "내용입니다");

					}
				});
	}

	public void writePost(String title, String body) {

		LoginManger.getInstance(this).signIn("oprt12@gmail.com", "1234");

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

		if (ObjectUtils.isEmpty(query)) {

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

					displayList();
				}

				@Override
				public void onException(BaasioException e) {
					Toast.makeText(TalksRoomActivity.this,
							"queryInBackground =>" + e.toString(),
							Toast.LENGTH_LONG).show();
				}
			});
		}
	}

	public void displayList() {
		talksRoomListView = (ListView) findViewById(R.id.talksroomListView);
		adapter = new TalksRoomListAdapter(TalksRoomActivity.this, posts);
		talksRoomListView.setAdapter(adapter);
		
		talksRoomListView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int index,
					long arg3) {
				Intent intent = new Intent(TalksRoomActivity.this, TalksRoomDetailActivity.class);
				intent.putExtra("post", posts.get(index).toString());
				startActivity(intent);
			}
		});
	}
}
