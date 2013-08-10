package com.hipits.emotionbusan.manager;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.hipits.emotionbusan.R;
import com.kth.baasio.callback.BaasioSignInCallback;
import com.kth.baasio.callback.BaasioSignUpCallback;
import com.kth.baasio.entity.user.BaasioUser;
import com.kth.baasio.exception.BaasioException;

public class LoginManger {

	private static LoginManger manger;
	private Context context;

	public LoginManger(Context context) {
		this.context = context;
	}

	public static LoginManger getInstance(Context context) {
		if (manger == null) {
			manger = new LoginManger(context);
		}
		return manger;
	}

	public void signUp(String id, String password) {

		BaasioUser.signUpInBackground(id, id, "null", password,
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
						}
					}
				});

	}

	public void signIn(String id, String password) {

		BaasioUser.signInInBackground(context, id, password,
				new BaasioSignInCallback() {

					@Override
					public void onException(BaasioException e) {
						Log.e("signException", e.getMessage());
					}

					@Override
					public void onResponse(BaasioUser response) {
					}
				});
	}

}
