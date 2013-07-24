package com.hipits.emotionbusan.manager;

import java.io.File;

import com.kth.baasio.callback.BaasioCallback;
import com.kth.baasio.callback.BaasioUploadAsyncTask;
import com.kth.baasio.callback.BaasioUploadCallback;
import com.kth.baasio.entity.entity.BaasioEntity;
import com.kth.baasio.entity.file.BaasioFile;
import com.kth.baasio.exception.BaasioException;

import android.os.Handler;
import android.util.Log;

public class FileManager {
	
	private static FileManager fileManager;
	private BaasioUploadAsyncTask uploadAsyncTask;
	
	public static FileManager getInstance() {
		if (fileManager == null) {
			fileManager = new FileManager();
		}
		return fileManager;
	}
	
	public void upLoadFile(final String filePath, final BaasioEntity cafe) {
		
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				
				final BaasioFile uploadFile = new BaasioFile();
				
				uploadAsyncTask = uploadFile.fileUploadInBackground(filePath, null, new BaasioUploadCallback() {
					
					@Override
					public void onResponse(BaasioFile baasioFile) {
						
						cafe.connectInBackground("imageFile", baasioFile, new BaasioCallback<BaasioEntity>() {
							
							@Override
							public void onException(BaasioException exception) {
								Log.e("imageFileConnect", "" + exception.getMessage());
							}

							@Override
							public void onResponse(BaasioEntity entity) {
								Log.e("imageConnect", "success");
							}
						});
						
						File imageFile = new File(filePath);

						if (imageFile.exists()) {
							imageFile.delete();
						}
					}
					
					@Override
					public void onProgress(long arg0, long arg1) {
						
					}
					
					@Override
					public void onException(BaasioException exception) {
						Log.e("FileUploadException", exception.getMessage());
					}
				});
			}
		}, 300);
	}
}
