package com.hipits.emotionbusan.baasio;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Application;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.kth.baasio.Baas;
import com.kth.baasio.callback.BaasioDeviceCallback;
import com.kth.baasio.callback.BaasioQueryCallback;
import com.kth.baasio.entity.BaasioBaseEntity;
import com.kth.baasio.entity.entity.BaasioEntity;
import com.kth.baasio.entity.push.BaasioDevice;
import com.kth.baasio.exception.BaasioException;
import com.kth.baasio.query.BaasioQuery;
import com.kth.baasio.query.BaasioQuery.ORDER_BY;
import com.kth.common.utils.LogUtils;

public class BaasioApplication extends Application {
    
	private static final String TAG = LogUtils.makeLogTag(BaasioApplication.class);
    
	public AsyncTask mGCMRegisterTask;
   
    private List<BaasioEntity> cafes;
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        cafes = new ArrayList<BaasioEntity>();
        
        Baas.io().init(this, BaasioConfig.BAASIO_URL, BaasioConfig.BAASIO_ID,
                BaasioConfig.APPLICATION_ID);

        mGCMRegisterTask = Baas.io().setGcmEnabled(this, null, new BaasioDeviceCallback() {

            @Override
            public void onException(BaasioException e) {
                LogUtils.LOGE(TAG, "init onException:" + e.toString());
            }

            @Override
            public void onResponse(BaasioDevice response) {
                LogUtils.LOGD(TAG, "init onResponse:" + response.toString());
            }
        }, BaasioConfig.GCM_SENDER_ID);
        
    }

	public List<BaasioEntity> getCafes() {
		return cafes;
	}

	public void setCafes(List<BaasioEntity> cafes) {
		this.cafes = cafes;
	}

	@Override
    public void onTerminate() {
        if (mGCMRegisterTask != null) {
            mGCMRegisterTask.cancel(true);
        }

        Baas.io().uninit(this);
        super.onTerminate();
    }
}
