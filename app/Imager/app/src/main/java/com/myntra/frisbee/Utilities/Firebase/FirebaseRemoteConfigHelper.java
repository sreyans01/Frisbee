package com.myntra.frisbee.Utilities.Firebase;


import com.google.firebase.BuildConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.myntra.frisbee.R;
import com.myntra.frisbee.Utilities.Constants;

import javax.inject.Singleton;

@Singleton
public class FirebaseRemoteConfigHelper {
    private FirebaseRemoteConfig remoteConfig;

    public FirebaseRemoteConfigHelper() {
        remoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(!BuildConfig.DEBUG ? 0 : (Constants.HOUR * 6))
                .build();
        remoteConfig.setConfigSettingsAsync(configSettings);
        remoteConfig.setDefaultsAsync(R.xml.resource_config_defaults);
    }

    public void fetchRemoteConfigValues() {
        remoteConfig.fetchAndActivate();
    }

    public String getLocalHostAddress() {
        return  remoteConfig.getString(Constants.LOCALHOSTADDRESS);
    }
}