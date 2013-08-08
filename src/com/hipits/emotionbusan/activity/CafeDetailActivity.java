package com.hipits.emotionbusan.activity;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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

public class CafeDetailActivity extends Activity {
	
	private TextView cafeNameTextView;
	private TextView writeIdTextView;
	private TextView contentTextView;
	private ImageView cafeImageView;
	private File cafeImaFile = null;
	
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
			kakaoSend();
		}
	}
	
	public void setData(String cafe) {
		
		BaasioEntity cafeEntity = JsonUtils.parse(cafe, BaasioEntity.class);
		
		String cafeName = EtcUtils.getStringFromEntity(cafeEntity, "cafeName");
		String writeId = EtcUtils.getStringFromEntity(cafeEntity, "writer_username");
		String content = EtcUtils.getStringFromEntity(cafeEntity, "content");
		
		cafeNameTextView.append(cafeName);
		writeIdTextView.append(writeId);
		contentTextView.setText(content);
		
		BaasioQuery query = new BaasioQuery();
		query.setType("file");
		query.setRelation(cafeEntity, "imagefile");
		
		query.queryInBackground(new BaasioQueryCallback() {
			@Override
			public void onResponse(List<BaasioBaseEntity> entities, List<Object> arg1,
					BaasioQuery arg2, long arg3) {
				
				BaasioFile imageFile = BaasioBaseEntity.toType(entities, BaasioFile.class).get(0);
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
		
		String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
		
		BaasioDownloadAsyncTask task = imageFile.fileDownloadInBackground(filePath, new BaasioDownloadCallback() {
			
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
	
	public void kakaoSend() {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, "카페추천!!!");
		
		intent.setType("image/png");
		intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(cafeImaFile.getPath()));
		
		intent.setPackage("com.kakao.talk");
		
		startActivity(intent);
	}
	
}
