package com.hipits.emotionbusan.activity;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.hipits.emotionbusan.R;

public class RegistrationCafeActivity extends Activity {

	private String[] menus;

	private static final int PICK_FROM_CAMERA = 0;
	private static final int PICK_FROM_ALBUM = 1;
	private static final int CROP_FROM_CAMERA = 2;

	private Uri imageUri;
	private ImageView cafeImageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registrationcafe);

		cafeImageView = (ImageView) findViewById(R.id.cafeImageView);

		menus = new String[] { "사진 찰영하기", "앨범에서 선택하기", "취소" };

	}

	public void onClick(View view) {
		int id = view.getId();

		if (id == R.id.cafeImageView) {
			showMenu();
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode != RESULT_OK) {
			Log.e("Result Fail", "no");
			return;
		}

		switch (requestCode) {

		case PICK_FROM_CAMERA:

			Intent intent = new Intent("com.android.camera.action.CROP");
			intent.setDataAndType(imageUri, "image/*");

			intent.putExtra("aspectx", 1);
			intent.putExtra("aspecty", 1);

			intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

			startActivityForResult(intent, CROP_FROM_CAMERA);

			break;

		case CROP_FROM_CAMERA:

			Bitmap bitmap = BitmapFactory.decodeFile(imageUri.getPath());

			cafeImageView.setImageBitmap(bitmap);

			File file = new File(imageUri.getPath());

			if (file.exists()) {
				file.delete();
			}

			break;

		case PICK_FROM_ALBUM:

			String filePath = Environment.getExternalStorageDirectory()
					+ "/temp.jpg";
			Bitmap selectedBitmap = BitmapFactory.decodeFile(filePath);

			cafeImageView.setImageBitmap(selectedBitmap);
			
			File file2 = new File(filePath);

			if (file2.exists()) {
				file2.delete();
			}


			break;
		}

	}

	public void pickFromCamera() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		String url = "tmp_" + String.valueOf(System.currentTimeMillis())
				+ ".jpg";

		imageUri = Uri.fromFile(new File(Environment
				.getExternalStorageDirectory(), url));
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		startActivityForResult(intent, PICK_FROM_CAMERA);
	}

	public void pickFromAlbum() {
		
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT,
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		intent.setType("image/*");
		intent.putExtra("crop", "true");

		File file = new File(Environment.getExternalStorageDirectory(),
				"temp.jpg");

		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				Log.e("fileException", e.getMessage());
			}
		}

		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

		startActivityForResult(intent, PICK_FROM_ALBUM);
	}

	public void showMenu() {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("앨범");
		builder.setItems(menus, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int position) {
				if (position == PICK_FROM_CAMERA) {
					pickFromCamera();
				} else if (position == PICK_FROM_ALBUM) {
					pickFromAlbum();
				}

			}
		});

		builder.setCancelable(true);
		builder.create().show();
	}

}
