package com.example.zhuk;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CbrApi {

    @GET("HD_base/Metal/Metal_base_new/")
    Call<MetalData> getMetalRates(
            @Query("date_req1") String dateFrom,
            @Query("date_req2") String dateTo
    );
}