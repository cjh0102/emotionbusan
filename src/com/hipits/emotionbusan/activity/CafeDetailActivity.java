package com.hipits.emotionbusan.activity;

import java.io.File;
import java.util.List;

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

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class CafeDetailActivity extends Activity {
	
	private TextView cafeNameTextView;
	private TextView writeIdTextView;
	private ImageView cafeImageView;
	
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
	}
	
	public void setData(String cafe) {
		
		BaasioEntity cafeEntity = JsonUtils.parse(cafe, BaasioEntity.class);
		
		String cafeName = EtcUtils.getStringFromEntity(cafeEntity, "cafeName");
		String writeId = EtcUtils.getStringFromEntity(cafeEntity, "writer_username");
		
		cafeNameTextView.append(cafeName);
		writeIdTextView.append(writeId);
		
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
				
				File imageFile = new File(filePath);
				
				if (imageFile.exists()) {
					imageFile.delete();
				}
				
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
}
