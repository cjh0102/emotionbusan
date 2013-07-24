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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.hipits.emotionbusan.R;
import com.hipits.emotionbusan.manager.FileManager;
import com.hipits.emotionbusan.manager.LoginManger;
import com.hipits.emotionbusan.model.Cafe;
import com.kth.baasio.Baas;
import com.kth.baasio.callback.BaasioCallback;
import com.kth.baasio.entity.entity.BaasioEntity;
import com.kth.baasio.entity.user.BaasioUser;
import com.kth.baasio.exception.BaasioException;
import com.kth.baasio.utils.ObjectUtils;

public class RegistrationCafeActivity extends Activity {

	private String[] menus;

	private static final int PICK_FROM_CAMERA = 0;
	private static final int PICK_FROM_CROP = 1;
	private static final String ENTITY_TYPE = "cafe";
	
	private Uri imageUri = null;
	private ImageView cafeImageView;
	private Spinner categorySpinner;
	private EditText contentEditText;
	
	@Override
	public void onBackPressed() {
		deleteImageFile();
		super.onBackPressed();
	}
	
	@Override
	protected void onDestroy() {
		deleteImageFile();
		super.onDestroy();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registrationcafe);
		
		initId();

		menus = new String[] { "사진 찰영하기", "앨범에서 선택하기", "취소" };

	}
	
	private void initId() {
		
		cafeImageView = (ImageView) findViewById(R.id.cafeImageView);
		categorySpinner = (Spinner) findViewById(R.id.categorySpinner);
		contentEditText = (EditText) findViewById(R.id.contentEditText);
	}

	public void onClick(View view) {
		int id = view.getId();

		if (id == R.id.imageRegisterButton) {
			showMenu();
		} else if (id == R.id.registerCafeButton) {
			registerCafe();
		}

	}
	
	public void registerCafe() {
		
		BaasioUser user = Baas.io().getSignedInUser();
		
		if (ObjectUtils.isEmpty(user)) {
			Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
			return;
		}
		
		LoginManger.getInstance(this).signIn("junhwan", "123456");
		
		BaasioEntity cafe = new BaasioEntity(ENTITY_TYPE);
		cafe.setProperty("writer_username", user.getUsername());
		cafe.setProperty("cafeName", "카페이름");
		cafe.setProperty("cafeAddress", "카페주소");
		cafe.setProperty("content", "이 카페 맛있음요.");
		cafe.setProperty("cafeURL", "카페 네이버 주소");
		cafe.setProperty("favoritesCount", 1);
		cafe.setProperty("category", "서면");
		
		
		cafe.saveInBackground(new BaasioCallback<BaasioEntity>() {
			@Override
			public void onResponse(BaasioEntity entity) {
				if (entity != null) {
					FileManager.getInstance().upLoadFile(imageUri.getPath(), entity);
				}
			}
			
			@Override
			public void onException(BaasioException exception) {
				Log.e("RegisterException", exception.getMessage());
			}
		});
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode != RESULT_OK) {
			return;
		}

		switch (requestCode) {

		case PICK_FROM_CAMERA:

			Intent intent = new Intent("com.android.camera.action.CROP");
			intent.setDataAndType(imageUri, "image/*");

			intent.putExtra("aspectx", 1);
			intent.putExtra("aspecty", 1);

			intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

			startActivityForResult(intent, PICK_FROM_CROP);

			break;

		case PICK_FROM_CROP:
			
			
			Bitmap selectedBitmap = BitmapFactory.decodeFile(imageUri.getPath());

			cafeImageView.setImageBitmap(selectedBitmap);
			
			break;
		}

	}

	public void pickFromCamera() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		String url = "Cam_" + String.valueOf(System.currentTimeMillis())
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
		
		String fileName = "Album_" + String.valueOf(System.currentTimeMillis())
				+ ".jpg";
		
		File file = new File(Environment.getExternalStorageDirectory(), fileName);

		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				Log.e("fileException", e.getMessage());
			}
		}
		
		imageUri = Uri.fromFile(file);

		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

		startActivityForResult(intent, PICK_FROM_CROP);
	}

	public void showMenu() {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("앨범");
		builder.setItems(menus, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int position) {
				if (position == PICK_FROM_CAMERA) {
					pickFromCamera();
				} else if (position == PICK_FROM_CROP) {
					pickFromAlbum();
				}

			}
		});

		builder.setCancelable(true);
		builder.create().show();
	}
	
	public void deleteImageFile() {
		
		if (imageUri == null) {
			return;
		}
		
		File imageFile = new File(imageUri.getPath());
		if (imageFile.exists()) {
			imageFile.delete();
		}
	}
}
