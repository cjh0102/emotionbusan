package com.hipits.emotionbusan.activity;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.hipits.emotionbusan.R;
import com.hipits.emotionbusan.adapter.TalksRoomListAdapter;
import com.hipits.emotionbusan.manager.LoginManger;
import com.kth.baasio.callback.BaasioQueryCallback;
import com.kth.baasio.entity.BaasioBaseEntity;
import com.kth.baasio.entity.entity.BaasioEntity;
import com.kth.baasio.exception.BaasioException;
import com.kth.baasio.query.BaasioQuery;
import com.kth.baasio.query.BaasioQuery.ORDER_BY;
import com.kth.baasio.utils.ObjectUtils;

public class TalksRoomActivity extends Activity {

	private BaasioQuery query;
	public static final String ENTITY_TYPE = "post";
	private List<BaasioEntity> posts;
	private ListView talksRoomListView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_talksroom);
		
		getEntities();

		talksRoomListView = (ListView) findViewById(R.id.talksroomListView);
		
		TalksRoomListAdapter adapter = new TalksRoomListAdapter(TalksRoomActivity.this, posts);
		talksRoomListView.setAdapter(adapter);

		final LoginManger loginManger = LoginManger.getInstance(this);
		final EditText idEditText = (EditText) findViewById(R.id.idEditText);
		final EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);
		
		findViewById(R.id.loginButton).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				loginManger.signIn(idEditText.getText().toString().trim(),
						passwordEditText.getText().toString().trim());
				
				if (loginManger.getIsLogin()) {
					Toast.makeText(TalksRoomActivity.this, "로긴성공", Toast.LENGTH_SHORT).show();
					getEntities();
					
					
				}
			}
		});

	}

	private void getEntities() {

		if (ObjectUtils.isEmpty(query)) {
			
			query = new BaasioQuery();
			query.setType(ENTITY_TYPE);
			query.setOrderBy(BaasioBaseEntity.PROPERTY_MODIFIED,
					ORDER_BY.DESCENDING);
			
			query.queryInBackground(new BaasioQueryCallback() {
				@Override
				public void onResponse(List<BaasioBaseEntity> entities, List<Object> list,
						BaasioQuery query, long timestamp) {
					posts = BaasioBaseEntity.toType(entities, BaasioEntity.class);
				
				}
				
				@Override
				public void onException(BaasioException e) {
					 Toast.makeText(TalksRoomActivity.this, "queryInBackground =>" + e.toString(), Toast.LENGTH_LONG)
	                    .show();
				}
			});
		}
	}
}
