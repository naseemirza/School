package com.lead.infosystems.schooldiary.ShareButton;

import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebookConfiguration;

/**
 * Created by Naseem on 06-01-2017.
 */

public class MyConfig {

    static final String APP_ID="950181445083601";
    Permission[] permissions= new Permission[]{Permission.EMAIL,Permission.USER_PHOTOS};



    public SimpleFacebookConfiguration getMyConfigs() {
        SimpleFacebookConfiguration configuration = new SimpleFacebookConfiguration.Builder()
                .setAppId(APP_ID)
                .setNamespace("FacebookSDK")
                .setPermissions(permissions)
                .build();
        return configuration;
    }
}
