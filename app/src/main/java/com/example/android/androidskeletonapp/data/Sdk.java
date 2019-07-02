package com.example.android.androidskeletonapp.data;

import android.content.Context;

import org.hisp.dhis.android.core.d2manager.D2Configuration;

public class Sdk {

    public static D2Configuration getD2Configuration(Context context) {
        // TODO Add configuration
        D2Configuration configuration = D2Configuration.builder()
                .appName("cdjedje").appVersion("1.0.0")
                .context(context)
                .readTimeoutInSeconds(30).connectTimeoutInSeconds(30).writeTimeoutInSeconds(30)
//.networkInterceptors(Collections.singletonList(new StethoInterceptor()))
                .build();

        return configuration;
    }
}