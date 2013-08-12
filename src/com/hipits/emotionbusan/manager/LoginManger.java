package com.hipits.emotionbusan.manager;

import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.hipits.emotionbusan.R;
import com.hipits.emotionbusan.activity.LoginActivity;
import com.kth.baasio.callback.BaasioSignInCallback;
import com.kth.baasio.callback.BaasioSignUpCallback;
import com.kth.baasio.entity.user.BaasioUser;
import com.kth.baasio.exception.BaasioException;
import com.kth.baasio.utils.ObjectUtils;

public class LoginManger {

	private static LoginManger manger;
	private Context context;

	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	public LoginManger(Context context) {
		this.context = context;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public static LoginManger getInstance(Context context) {
		if (manger == null) {
			manger = new LoginManger(context);
		}
		return manger;
	}

	public void signUp(String id, String nickName, String password) {

		Pattern pattern = Pattern.compile(EMAIL_PATTERN);

		if (!pattern.matcher(id).matches()) {
			Toast.makeText(
					context,
					context.getResources().getString(
							R.string.error_invalid_email), Toast.LENGTH_LONG)
					.show();
			return;
		}

		BaasioUser.signUpInBackground(id, nickName, id, password,
				new BaasioSignUpCallback() {
					@Override
					public void onException(BaasioException e) {
						if (e.getErrorCode() == 913) {
							Toast.makeText(context,
									R.string.DUPLICATED_UNIQUE_PROPERTY_ERROR,
									Toast.LENGTH_SHORT).show();
						} else if (e.getErrorCode() == 201) {
							Toast.makeText(context,
									R.string.NVALID_USERNAME_OR_PASSWORD_ERROR,
									Toast.LENGTH_SHORT).show();
						}
						return;
					}

					@Override
					public void onResponse(BaasioUser response) {
						if (response != null) {
							Intent intent = new Intent(context,
									LoginActivity.class);
							intent.putExtra("id", response.getEmail());
							intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

							Activity activity = (Activity) context;

							Toast.makeText(context, "가입 되었습니다.",
									Toast.LENGTH_SHORT).show();

							activity.setResult(Activity.RESULT_OK, intent);
							activity.finish();

						}
					}
				});
	}

	public void signIn(String id, String password) {

		Pattern pattern = Pattern.compile(EMAIL_PATTERN);
		if (!pattern.matcher(id).matches()) {
			Toast.makeText(
					context,
					context.getResources().getString(
							R.string.error_invalid_email), Toast.LENGTH_LONG)
					.show();
			return;
		}

		if (password == null || password.length() < 4) {
			Toast.makeText(
					context,
					context.getResources().getString(
							R.string.error_invalid_password), Toast.LENGTH_LONG)
					.show();
			return;
		}

		BaasioUser.signInInBackground(context, id, password,
				new BaasioSignInCallback() {

					@Override
					public void onException(BaasioException e) {

						if (e.getStatusCode() != null) {
							if (e.getErrorCode() == 201) {
								Toast.makeText(
										context,
										context.getResources().getString(
												R.string.error_invalid_grant),
										Toast.LENGTH_LONG).show();
								return;
							}
						}

						Toast.makeText(context, "아이디 및 비밀번호를 제대로 입력해주세요!",
								Toast.LENGTH_SHORT).show();

						Toast.makeText(context, "아이디를 등록해주세요!",
								Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onResponse(BaasioUser response) {
						if (!ObjectUtils.isEmpty(response)) {
							
							Toast.makeText(context, "로그인되었습니다.",
									Toast.LENGTH_SHORT).show();
							Activity activity = (Activity) context;
							activity.finish();
						}
					}
				});
	}

	public void signOut() {
		BaasioUser.signOut(context);
		Toast.makeText(context, "로그아웃 되었습니다.", Toast.LENGTH_LONG).show();
	}
}
