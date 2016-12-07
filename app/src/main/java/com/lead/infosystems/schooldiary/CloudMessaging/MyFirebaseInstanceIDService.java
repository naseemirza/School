package com.lead.infosystems.schooldiary.CloudMessaging;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.lead.infosystems.schooldiary.Data.UserDataSP;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        String token = FirebaseInstanceId.getInstance().getToken();
        UserDataSP userDataSP = new UserDataSP(getApplicationContext());
        userDataSP.storeCloudId(token);
    }
}
