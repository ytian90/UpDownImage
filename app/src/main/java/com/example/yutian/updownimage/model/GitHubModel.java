package com.example.yutian.updownimage.model;

import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;

import com.example.yutian.updownimage.API.GitHubService;
import com.example.yutian.updownimage.bean.FromServer;
import com.example.yutian.updownimage.bean.Image;
import com.example.yutian.updownimage.net.ServiceGenerator;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by yutian on 5/13/16.
 */
public class GitHubModel {
    private GitHubService git;
    private final static String server_dir = "http://www.techep.csi.cuny.edu/~tianyu/service/Documents/Downloads/";
    private String image_dir;
    private String responseJSON;
    private List<String> images_list;

    public GitHubModel() {
        this.git =  ServiceGenerator.createService(GitHubService.class);
    }

    public void upload(Bitmap image, String name) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

        Call<List<FromServer>> call = git.upload(name, encodedImage);
        call.enqueue(new Callback<List<FromServer>>() {
            @Override
            public void onResponse(Call<List<FromServer>> call, Response<List<FromServer>> response) {
//                Toast.makeText(this, response.body().get(0).result, Toast.LENGTH_SHORT).show();
//                Log.v("Upload", "success");
                Log.v("List<FromServer>", response.body().get(0).result);
//                Log.v("List<FromServer>", String.valueOf(response.body().size()));
            }

            @Override
            public void onFailure(Call<List<FromServer>> call, Throwable t) {
                Log.e("Upload error:", t.getMessage());
            }
        });
    }

    public void upload2(File file, final String new_name) {
//        // use the FileUtils to get the actual file by uri
//        File file = FileUtils.getFile(Environment.getExternalStorageDirectory()+"/1.jpg");

        // create RequestBody instance from file
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("image/jpg"), file);

        Call<List<FromServer>> call = git.upload2(new_name, requestFile);
        call.enqueue(new Callback<List<FromServer>>() {
            @Override
            public void onResponse(Call<List<FromServer>> call, Response<List<FromServer>> response) {
//                Toast.makeText(this, response.body().get(0).result, Toast.LENGTH_SHORT).show();
//                Log.v("Upload", "success");
                Log.v("List<FromServer>", response.body().get(0).result);
//                Log.v("List<FromServer>", String.valueOf(response.body().size()));
                image_dir = server_dir + new_name + ".jpg";
                System.out.println("image_dir: " + image_dir);
            }
            @Override
            public void onFailure(Call<List<FromServer>> call, Throwable t) {
                Log.e("Upload error:", t.getMessage());
            }
        });

    }

    public void ProcImage(String url, String algo, final TextView resTextView) {
        //binding.username.getText().toString()
        Call call = git.ProcImage(image_dir, algo);
        call.enqueue(new Callback<List<Image>>() {
            @Override
            public void onResponse(Call<List<Image>> call, Response<List<Image>> response) {
                List<Image> model = response.body();
                //resTextView.setText(model.toString());

                if (model == null) {
                    //404 or the response cannot be converted to User.
                    ResponseBody responseBody = response.errorBody();
                    if (responseBody != null) {
                        try {
                            responseJSON = responseBody.string();
                            resTextView.setText(responseJSON);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        resTextView.setText("responseBody  = null");
                    }
                } else {
                    //200
//                    viewModel.setText("Github Name :" + model.getName() + "\nWebsite :" + model.getBlog() + "\nCompany Name :" + model.getCompany());

                    resTextView.setText(model.toString());


                }

            }

            @Override
            public void onFailure(Call<List<Image>> call, Throwable t) {

            }
        });
    }




}
