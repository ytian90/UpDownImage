package com.example.yutian.updownimage;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yutian.updownimage.model.GitHubModel;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int RESULT_LOAD_IMAGE = 1;
    ImageView imageToUpload, downloadedImage;
    Button bUploadImage, bDownloadImage;
    EditText downloadImageName;
    Uri selectedImage;
    TextView resTextView;
    String finalFileName;
    List<String> images_list;

    private final static String server_dir = "http://www.techep.csi.cuny.edu/~tianyu/service/Documents/Downloads/";
    private ArrayList<String> cmds = new ArrayList<>(
            Arrays.asList("edgedetect", "facedetect", "filters", "histequalize", "histogram"));
    private Integer algo_op = 0;
    // spinner for select algorithm
    Spinner spinner, spinner2;
    ArrayAdapter<CharSequence> adapter;
    ArrayAdapter<String> adapter2;

    private GitHubModel model;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        model = new GitHubModel();

        imageToUpload = (ImageView) findViewById(R.id.imageToUpload);
        downloadedImage = (ImageView) findViewById(R.id.downloadedImage);

        bUploadImage = (Button) findViewById(R.id.bUploadImage);
        bDownloadImage = (Button) findViewById(R.id.bDownloadImage);

        downloadImageName = (EditText) findViewById(R.id.etDownloadName);

        resTextView = (TextView) findViewById(R.id.resTextView);
        images_list = new ArrayList<>();

        imageToUpload.setOnClickListener(this);
        bUploadImage.setOnClickListener(this);
        bDownloadImage.setOnClickListener(this);

        spinner = (Spinner) findViewById(R.id.spinner);
        adapter = ArrayAdapter.createFromResource(this, R.array.algorithm_names, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                algo_op = (int) parent.getItemIdAtPosition(position);
                Toast.makeText(getBaseContext(), parent.getItemIdAtPosition(position) + " selected", Toast.LENGTH_LONG).show();
                model.ProcImage(server_dir + finalFileName, cmds.get(algo_op), resTextView);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        resTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String jsonStr = resTextView.getText().toString();
                    try {
                        images_list = fetchImages(jsonStr);
                        System.out.println("Now images_list : " + images_list);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        String[] arr_res_names = new String[images_list.size()];
        for (int i = 0; i < images_list.size(); i++) {
            arr_res_names[i] = images_list.get(i);
            System.out.println("arr_res_names[" + i + "] = " + arr_res_names[i]);
        }

        spinner2 = (Spinner) findViewById(R.id.spinner2);
        adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arr_res_names);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                algo_op = (int) parent.getItemIdAtPosition(position);
//                Toast.makeText(getBaseContext(), parent.getItemIdAtPosition(position) + " selected", Toast.LENGTH_LONG).show();
//                model.ProcImage(server_dir + finalFileName, cmds.get(algo_op), resTextView);



            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        verifyStoragePermissions(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageToUpload:
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE); // get back the image user selected

                break;

            case R.id.bUploadImage:
//                Bitmap image = ((BitmapDrawable) imageToUpload.getDrawable()).getBitmap();
                File finalFile = new File(getRealPathFromURI(selectedImage));
//                System.out.println("finalFile: " + finalFile);
                finalFileName = finalFile.getName();
                model.upload2(finalFile, finalFileName);
                System.out.println(finalFileName);
                break;

            case R.id.bDownloadImage:
                Picasso.with(this)
                        .load("http://www.techep.csi.cuny.edu/~tianyu/service/Documents/Results/"
                                + downloadImageName.getText().toString() + ".jpg")
                        .into(downloadedImage);
                break;
        }
    }

    /*
    Called when user selected picture from the gallery
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            selectedImage = data.getData(); // get image URI
            imageToUpload.setImageURI(selectedImage); // display the image

        }
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public List<String> fetchImages(String responseJSON) throws IOException, JSONException {

        List<String> allImages = new ArrayList<>();

        // Parse the string into JSON
        JSONObject root = new JSONObject(responseJSON);
        // get the array of plants from JSON
        JSONArray images = root.getJSONArray("images");

        for (int i = 0; i < images.length(); i++) {
            // parse the JSON object into its fields and values
            JSONObject jsonImage = images.getJSONObject(i);
            String src = jsonImage.getString("src");
            allImages.add(src);
        }

        return allImages;


    }

}
