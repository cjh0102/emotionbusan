package com.hipits.emotionbusan;

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.hipits.emotionbusan.activity.CafeDetailActivity;
import com.hipits.emotionbusan.activity.LoginActivity;
import com.hipits.emotionbusan.activity.RegistrationCafeActivity;
import com.hipits.emotionbusan.activity.TalksRoomActivity;
import com.hipits.emotionbusan.baasio.BaasioApplication;
import com.hipits.emotionbusan.manager.LoginManger;
import com.kth.baasio.Baas;
import com.kth.baasio.callback.BaasioQueryCallback;
import com.kth.baasio.entity.BaasioBaseEntity;
import com.kth.baasio.entity.entity.BaasioEntity;
import com.kth.baasio.entity.user.BaasioUser;
import com.kth.baasio.exception.BaasioException;
import com.kth.baasio.query.BaasioQuery;
import com.kth.baasio.query.BaasioQuery.ORDER_BY;
import com.kth.baasio.utils.ObjectUtils;

public class MainActivity extends Activity {
	private BaasioApplication application;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		application = (BaasioApplication) this.getApplicationContext();
		// queryCafes();

		startActivity(new Intent(this, TalksRoomActivity.class));

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void onClick(View view) {
		int id = view.getId();

		Intent intent = null;

		if (id == R.id.registerCafeButton) {
			intent = new Intent(this, RegistrationCafeActivity.class);
		} else if (id == R.id.cafeButton) {

			intent = new Intent(this, CafeDetailActivity.class);

			BaasioEntity cafe = application.getCafes().get(0);

			intent.putExtra("cafe", cafe.toString());

		} else if (id == R.id.loginButton) {
			intent = new Intent(this, LoginActivity.class);
		} else if (id == R.id.logOutButton) {
			LoginManger.getInstance(this).signOut();
		}

		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	public void queryCafes() {

		BaasioUser user = Baas.io().getSignedInUser();

		if (ObjectUtils.isEmpty(user)) {
			Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
		}

		final ProgressDialog dialog = ProgressDialog
				.show(this, "알림", "데이터 로딩중");
		dialog.setCancelable(true);

		BaasioQuery query = new BaasioQuery();
		query.setType("cafe");
		query.setOrderBy(BaasioBaseEntity.PROPERTY_MODIFIED,
				ORDER_BY.DESCENDING);

		query.queryInBackground(new BaasioQueryCallback() {
			@Override
			public void onResponse(List<BaasioBaseEntity> entities,
					List<Object> arg1, BaasioQuery arg2, long arg3) {
				application.setCafes(BaasioBaseEntity.toType(entities,
						BaasioEntity.class));
				dialog.dismiss();
			}

			@Override
			public void onException(BaasioException arg0) {
				Log.e("MainActivity", arg0.getMessage());
				Toast.makeText(MainActivity.this, "인터넷이 지연중입니다.",
						Toast.LENGTH_SHORT).show();
				dialog.dismiss();
			}
		});
	}
}
