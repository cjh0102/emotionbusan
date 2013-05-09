package com.hipits.emotionbusan.manager;

import android.content.Context;
import android.util.Log;

import com.kth.baasio.callback.BaasioSignInCallback;
import com.kth.baasio.callback.BaasioSignUpCallback;
import com.kth.baasio.entity.user.BaasioUser;
import com.kth.baasio.exception.BaasioException;

public class LoginManger {
	
	private static LoginManger manger;
	private Boolean isLogin = false;
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
		
		BaasioUser.signUpInBackground(id, id, "null", password, new BaasioSignUpCallback() {
			@Override
			public void onException(BaasioException e) {
				Log.e("test", "" + e.getErrorCode());
				if (e.getErrorCode() == 913) {
					setIsLogin(false);
					return;
				}
			}
			
			@Override
			public void onResponse(BaasioUser response) {
				if (response != null) {
					Log.e("test", "성공");
					setIsLogin(true);
				}
			}
		});
		
	}
	
	public void signIn(String id, String password) {

		BaasioUser.signInInBackground(context, id, password, new BaasioSignInCallback() {
			
			@Override
			public void onException(BaasioException e) {
				
			}
			
			@Override
			public void onResponse(BaasioUser response) {
				
			}
		});
	}

	public Boolean getIsLogin() {
		return isLogin;
	}

	public void setIsLogin(Boolean isLogin) {
		this.isLogin = isLogin;
	}
}
