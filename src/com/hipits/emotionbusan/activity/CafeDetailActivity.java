package com.hipits.emotionbusan.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hipits.emotionbusan.KakaoLink;
import com.hipits.emotionbusan.R;
import com.hipits.emotionbusan.baasio.EtcUtils;
import com.kth.baasio.callback.BaasioDownloadAsyncTask;
import com.kth.baasio.callback.BaasioDownloadCallback;
import com.kth.baasio.callback.BaasioQueryCallback;
import com.kth.baasio.entity.BaasioBaseEntity;
import com.kth.baasio.entity.entity.BaasioEntity;
import com.kth.baasio.entity.file.BaasioFile;
import com.kth.baasio.exception.BaasioException;
import com.kth.baasio.query.BaasioQuery;
import com.kth.baasio.utils.JsonUtils;
import com.kth.baasio.utils.ObjectUtils;

public class CafeDetailActivity extends Activity {

	private TextView cafeNameTextView;
	private TextView writeIdTextView;
	private TextView contentTextView;
	private ImageView cafeImageView;
	private File cafeImaFile = null;
	private BaasioEntity cafeEntity = null;

	@Override
	public void onBackPressed() {
		deleteImageFile();
		super.onBackPressed();
	}

	private void deleteImageFile() {
		if (cafeImaFile != null && cafeImaFile.exists()) {
			cafeImaFile.delete();
		}
	}

	@Override
	protected void onDestroy() {
		deleteImageFile();
		super.onDestroy();
	}

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
		cafeImageView = (ImageView) findViewById(R.id.cafeImageView);
		contentTextView = (TextView) findViewById(R.id.contentTextView);
	}

	public void onClick(View view) {
		int id = view.getId();

		if (id == R.id.kakaoButton) {
			try {
				kakaoSend();
			} catch (NameNotFoundException e) {
				Log.e("CafeDetail", e.getMessage());
			}
		}
	}

	public void setData(String cafe) {

		cafeEntity = JsonUtils.parse(cafe, BaasioEntity.class);

		String cafeName = EtcUtils.getStringFromEntity(cafeEntity, "cafeName");
		String writeId = EtcUtils.getStringFromEntity(cafeEntity,
				"writer_username");
		String content = EtcUtils.getStringFromEntity(cafeEntity, "content");

		cafeNameTextView.append(cafeName);
		writeIdTextView.append(writeId);
		contentTextView.setText(content);

		BaasioQuery query = new BaasioQuery();
		query.setType("file");
		query.setRelation(cafeEntity, "imagefile");

		query.queryInBackground(new BaasioQueryCallback() {
			@Override
			public void onResponse(List<BaasioBaseEntity> entities,
					List<Object> arg1, BaasioQuery arg2, long arg3) {

				BaasioFile imageFile = BaasioBaseEntity.toType(entities,
						BaasioFile.class).get(0);
				downLoadImageFile(imageFile);
			}

			@Override
			public void onException(BaasioException arg0) {
				Log.e("query", arg0.getMessage());
			}
		});

	}

	public void downLoadImageFile(BaasioFile imageFile) {

		if (isFinishing()) {
			return;
		}

		final ProgressDialog dialog = ProgressDialog
				.show(this, "알림", "데이터 로딩중");
		dialog.setCancelable(true);

		String filePath = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/";

		BaasioDownloadAsyncTask task = imageFile.fileDownloadInBackground(
				filePath, new BaasioDownloadCallback() {

					@Override
					public void onResponse(String filePath) {

						Bitmap bitmap = BitmapFactory.decodeFile(filePath);
						cafeImageView.setImageBitmap(bitmap);

						cafeImaFile = new File(filePath);

						dialog.dismiss();
					}

					@Override
					public void onProgress(long arg0, long arg1) {

					}

					@Override
					public void onException(BaasioException arg0) {
						Log.e("imageFile", arg0.getMessage());
					}
				});
	}

	public void kakaoSend() throws NameNotFoundException {
		
		ArrayList<Map<String, String>> metaInfoArray = new ArrayList<Map<String, String>>();

		Map<String, String> metaInfoAndroid = new Hashtable<String, String>();
		metaInfoAndroid.put("os", "android");
		metaInfoAndroid.put("devicetype", "phone");
		metaInfoAndroid.put("installurl",
				"market://details?id=com.hipits.information");
		metaInfoAndroid.put("executeurl", "emotionbusan://kyungsung");

		metaInfoArray.add(metaInfoAndroid);

		KakaoLink kakaoLink = KakaoLink.getLink(getApplicationContext());

		if (!kakaoLink.isAvailableIntent()) {
			Toast.makeText(this, "카카오톡이 설치되어있지않습니다.", Toast.LENGTH_SHORT)
					.show();
		}

		if (!ObjectUtils.isEmpty(cafeEntity)) {
			
			String url = EtcUtils.getStringFromEntity(cafeEntity, "cafeURL");
			String message = "카페 추천!!" + "\n" + url;
			
			kakaoLink.openKakaoAppLink(
							this,
							url,
							message,
							getPackageName(),
							getPackageManager().getPackageInfo(
									getPackageName(), 0).versionName, "감성부산",
							"UTF-8", metaInfoArray);
		}
	}
}
