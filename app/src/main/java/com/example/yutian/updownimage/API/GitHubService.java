package com.example.yutian.updownimage.API;

import android.media.Image;

import com.example.yutian.updownimage.bean.FromServer;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * Created by yutian on 5/12/16.
 */
public interface GitHubService {
    @GET("index.php?")
    Call<List<Image>> ProcImage(@Query("url") String url, @Query("algo") String algo);

//    @Multipart
//    @POST("Downloads/")
//    Call<ResponseBody> update(@Part MultipartBody.Part file,
//                              @Part("description") RequestBody description);


//    @POST("Downloads/")
//    Call<Void> update(@Query("name") String name, @Query("image") String image);

    @Multipart
    @POST("SavePicture.php")
    Call<List<FromServer>> upload(
            @Part("name") String name,
            @Part("file") String file
    );

    @Multipart
    @POST("SavePicture.php")
    Call<List<FromServer>> upload2(
            @Part("name") String name,
            @Part("file") RequestBody file);


}
