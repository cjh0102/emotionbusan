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
import android.widget.Button;
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

	private static final int PICK_FROM_CAMERA = 0;
	private static final int PICK_FROM_CROP = 1;
	private static final String ENTITY_TYPE = "cafe";

	private Uri imageUri = null;
	private ImageView cafeImageView;

	private Button categoryButton;
	private EditText contentEditText;
	private EditText cafeNameEditText;
	private EditText cafeAddressEditText;

	private Cafe cafe;

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

	}

	private void initId() {

		cafeImageView = (ImageView) findViewById(R.id.cafeImageView);
		categoryButton = (Button) findViewById(R.id.categoryButton);
		contentEditText = (EditText) findViewById(R.id.contentEditText);
		cafeNameEditText = (EditText) findViewById(R.id.cafeNameEditText);
		cafeAddressEditText = (EditText) findViewById(R.id.cafeAddressEditText);

		cafe = new Cafe();
	}

	public void onClick(View view) {
		int id = view.getId();

		if (id == R.id.imageRegisterButton) {
			showMenu();
		} else if (id == R.id.registerCafeButton) {
			cafe.setCafeName(cafeNameEditText.getText().toString().trim());
			cafe.setCafeAddress(cafeAddressEditText.getText().toString().trim());
			cafe.setContent(contentEditText.getText().toString());
			registerCafe();

		} else if (id == R.id.urlRegisterButton) {
			cafe.setCafeURL("http://ii222.blog.me/150107454004");
		} else if (id == R.id.categoryButton) {
			showCategory();
		}
	}

	public void registerCafe() {

		BaasioUser user = Baas.io().getSignedInUser();
		
		if (imageUri == null) {
			Toast.makeText(RegistrationCafeActivity.this,
					"이미지를 등록해주세요!", Toast.LENGTH_SHORT).show();
			return;
		}

		if (cafe.getCafeName().isEmpty()) {
			Toast.makeText(this, "카페이름을 입력하세요!", Toast.LENGTH_SHORT).show();
			return;
		} else if (cafe.getCafeAddress().isEmpty()) {
			Toast.makeText(this, "주소를 입력하세요.!", Toast.LENGTH_SHORT).show();
			return;
		} else if (cafe.getContent().isEmpty()) {
			Toast.makeText(this, "카페에대한 설명을 적어주세요!", Toast.LENGTH_SHORT).show();
			return;
		} else if (cafe.getCafeURL().isEmpty()) {
			cafe.setCafeURL("null");
		}

		if (ObjectUtils.isEmpty(user)) {
			Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
			return;
		}

		LoginManger.getInstance(this).signIn("junhwan", "123456");

		BaasioEntity cafeEntity = new BaasioEntity(ENTITY_TYPE);
		cafeEntity.setProperty("cafeName", cafe.getCafeName());
		cafeEntity.setProperty("cafeAddress", cafe.getCafeAddress());
		cafeEntity.setProperty("content", cafe.getContent());

		cafeEntity.setProperty("cafeURL", cafe.getCafeURL());
		cafeEntity.setProperty("category", cafe.getCategory());
		cafeEntity.setProperty("favoritesCount", 1);
		cafeEntity.setProperty("writer_username", user.getUsername());

		cafeEntity.saveInBackground(new BaasioCallback<BaasioEntity>() {
			@Override
			public void onResponse(BaasioEntity entity) {
				if (entity != null) {
					FileManager.getInstance().upLoadFile(imageUri.getPath(),
							entity);
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

			Log.e("crop", imageUri.getPath());
			
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

		File file = new File(Environment.getExternalStorageDirectory(),
				fileName);

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

		String[] menus = new String[] { "사진 찰영하기", "앨범에서 선택하기", "취소" };

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

	public void showCategory() {

		final String[] categoStrings = new String[] { "서면 카페거리",
				"경성대학교, 부경대학교", "부산 대학교", "연산동", "기타" };

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("카테고리");
		builder.setItems(categoStrings, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int position) {
				cafe.setCategory(categoStrings[position]);
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
