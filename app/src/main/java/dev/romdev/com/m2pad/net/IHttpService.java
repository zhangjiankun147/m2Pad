package dev.romdev.com.m2pad.net;

import dev.romdev.com.m2pad.UpdateAPPResponse;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by LCL on 2017/11/16.
 */

public interface IHttpService {
    //    @FormUrlEncoded
    // @GET("sysModuleVersion/getLatestModuleVerion")
    String HOST_URL = "http://119.23.113.71:8080/service-soa/";

    @POST("sysModuleVersion/getLatestModuleVerion")
//            @FormUrlEncoded
    Call<UpdateAPPResponse> updateApp(
            @Query("requestJson") String requestJson);
    /*
    * @POST("shopList")
    @FormUrlEncoded
    Call<JsonObject> getShopList(@FieldMap Map<String, Object> map)
    * */

}
