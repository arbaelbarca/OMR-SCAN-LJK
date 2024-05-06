package com.apps.arbaelbarca.omrscanner.data.source

import com.apps.arbaelbarca.omrscanner.data.model.request.RequestPostLjk
import com.apps.arbaelbarca.omrscanner.data.model.response.ResponseGetLjk
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("api/get")
    fun callGetListLjk(
    ): Call<ResponseGetLjk>

    @POST("api/insert")
    fun callPostLjk(
        @Body requestPostLjk: RequestPostLjk
    ): Call<JsonObject>


}