package com.hipits.emotionbusan.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
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
import android.widget.Toast;

import com.hipits.emotionbusan.InternalStorageContentProvider;
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

import eu.janmuller.android.simplecropimage.CropImage;

public class RegistrationCafeActivity extends Activity {
	
	public static final String TAG = "RegistrationCafeActivity";

	public static String TEMP_PHOTO_FILE_NAME = null;
	private static final String ENTITY_TYPE = "cafe";

	public static final int REQUEST_CODE_GALLERY = 1;
	public static final int REQUEST_CODE_TAKE_PICTURE = 0;
	public static final int REQUEST_CODE_CROP_IMAGE = 2;

	private ImageView cafeImageView;
	private File imageFile;
	private Button categoryButton;
	private EditText contentEditText;
	private EditText cafeNameEditText;
	private EditText cafeAddressEditText;
	private List<File> imageFiles;

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

		init();

	}

	private void init() {

		cafeImageView = (ImageView) findViewById(R.id.cafeImageView);
		categoryButton = (Button) findViewById(R.id.categoryButton);
		contentEditText = (EditText) findViewById(R.id.contentEditText);
		cafeNameEditText = (EditText) findViewById(R.id.cafeNameEditText);
		cafeAddressEditText = (EditText) findViewById(R.id.cafeAddressEditText);

		cafe = new Cafe();
		
		imageFiles = new ArrayList<File>();

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

		if (!imageFile.exists()) {
			Toast.makeText(RegistrationCafeActivity.this, "이미지를 등록해주세요!",
					Toast.LENGTH_SHORT).show();
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
//					FileManager.getInstance().upLoadFile(imageUri.getPath(),
//							entity);
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
			Log.e("Result_Error", "ResultCodeError");
			return;
		}
		
		Bitmap bitmap;
		
		switch (requestCode) {
		
		case REQUEST_CODE_TAKE_PICTURE:
			
			Intent intent = new Intent(this, CropImage.class);
	        intent.putExtra(CropImage.IMAGE_PATH, imageFile.getPath());
	        intent.putExtra(CropImage.SCALE, true);
	        intent.putExtra(CropImage.ASPECT_X, 3);
	        intent.putExtra(CropImage.ASPECT_Y, 2);
	        
	        startActivityForResult(intent, REQUEST_CODE_CROP_IMAGE);

			break;

        case REQUEST_CODE_GALLERY:

            try {
            	
            	TEMP_PHOTO_FILE_NAME = "Album_" + String.valueOf(System.currentTimeMillis())
        				+ ".jpg";

        		imageFile = new File(Environment.getExternalStorageDirectory(), TEMP_PHOTO_FILE_NAME);
            	
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
                copyStream(inputStream, fileOutputStream);
                fileOutputStream.close();
                inputStream.close();

                startCropImage();

            } catch (Exception e) {

                Log.e(TAG, "Error while creating temp file", e);
            }

            break;
		
		case REQUEST_CODE_CROP_IMAGE:
			
			  String path = data.getStringExtra(CropImage.IMAGE_PATH);
              if (path == null) {

                  return;
              }

              bitmap = BitmapFactory.decodeFile(imageFile.getPath());
              cafeImageView.setImageBitmap(bitmap);
              
              imageFiles.add(imageFile);
              
              break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void showMenu() {

		String[] menus = new String[] { "사진 찰영하기", "앨범에서 선택하기", "취소" };

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("앨범");
		builder.setItems(menus, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int position) {
				if (position == REQUEST_CODE_TAKE_PICTURE) {
					takePicture();
					
				} else if (position == REQUEST_CODE_GALLERY) {
					openGallery();
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

	private void takePicture() {

		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		try {
			Uri mImageCaptureUri = null;
			String state = Environment.getExternalStorageState();
			if (Environment.MEDIA_MOUNTED.equals(state)) {
				
				TEMP_PHOTO_FILE_NAME = "Cam_" + String.valueOf(System.currentTimeMillis())
						+ ".jpg";
				
				imageFile = new File(Environment.getExternalStorageDirectory(), TEMP_PHOTO_FILE_NAME);
				
				mImageCaptureUri = Uri.fromFile(imageFile);
			} else {
				/*
				 * The solution is taken from here:
				 * http://stackoverflow.com/questions
				 * /10042695/how-to-get-camera-result-as-a-uri-in-data-folder
				 */
				mImageCaptureUri = InternalStorageContentProvider.CONTENT_URI;
			}
			intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
					mImageCaptureUri);
			intent.putExtra("return-data", true);
			startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
		} catch (ActivityNotFoundException e) {

			Log.d(TAG, "cannot take picture", e);
		}
	}
	
	 private void openGallery() {
	        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
	        photoPickerIntent.setType("image/*");
	        startActivityForResult(photoPickerIntent, REQUEST_CODE_GALLERY);
	    }
	 
	 public static void copyStream(InputStream input, OutputStream output)
	            throws IOException {

	        byte[] buffer = new byte[1024];
	        int bytesRead;
	        while ((bytesRead = input.read(buffer)) != -1) {
	            output.write(buffer, 0, bytesRead);
	        }
	    }
	 private void startCropImage() {

	        Intent intent = new Intent(this, CropImage.class);
	        intent.putExtra(CropImage.IMAGE_PATH, imageFile.getPath());
	        intent.putExtra(CropImage.SCALE, true);

	        intent.putExtra(CropImage.ASPECT_X, 3);
	        intent.putExtra(CropImage.ASPECT_Y, 2);

	        startActivityForResult(intent, REQUEST_CODE_CROP_IMAGE);
	    }

	public void deleteImageFile() {
		for (File file : imageFiles) {
			file.delete();
		}
		imageFiles.clear();
	}
}
