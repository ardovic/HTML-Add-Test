package com.ardovic.htmladdtest;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiInterface {

    @FormUrlEncoded
    @Headers({"Content-Type: application/x-www-form-urlencoded",
            "Postman-Token: abd93bb8-2857-2fd0-7679-0b25087e1d35",
            "Cache-Control: no-cache"
    })
    @POST("adviator/index.php")
    Call<AddData> getPageData(@Field("id") Long id);

}
