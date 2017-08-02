package com.example.networktest;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Warden on 2017/8/1.
 */

public class HttpUtil {
    static final OkHttpClient client = new OkHttpClient();

    public static  byte[] get() throws Exception {
        Request request = new Request.Builder()
                .url("http://kmustjwcxk4.kmust.edu.cn/JWWEB/sys/ValidateCode.aspx")
                .build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

        Headers responseHeaders = response.headers();
        for (int i = 0; i < responseHeaders.size(); i++) {
            System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
        }
        System.out.println(response.body().string());
        byte[] byte_image =  response.body().bytes();
        return byte_image;

    }

}
