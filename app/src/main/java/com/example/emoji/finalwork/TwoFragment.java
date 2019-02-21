package com.example.emoji.finalwork;


import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okio.BufferedSink;

import static android.app.Activity.RESULT_OK;

@SuppressLint("ValidFragment")
class TwoFragment extends Fragment{
    public static final int TAKE_PHOTO = 1;
    private Uri imageUri;
    private ImageView picture;
    private TextView txt;
    private ScrollView scrollview;
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,  Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bootmark, null);
        //获取控件实例
        ImageButton takePhoto = (ImageButton)view.findViewById(R.id.take_photo);
        picture = (ImageView)view.findViewById(R.id.picture);
        txt=(TextView)view.findViewById(R.id.detail);
        scrollview=(ScrollView)view.findViewById(R.id.myscollView);
        scrollview.setVisibility(View.INVISIBLE);//设置按钮点击事件
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //创建File对象，用于存储拍照后的图片；参数一：存放目录；参数二：图片命名
                File outputImage = new File(getActivity().getExternalCacheDir(),"output_image.jpg");
                try{
                    if (outputImage.exists()){
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                }catch (IOException e){
                    e.printStackTrace();
                }
                if (Build.VERSION.SDK_INT>=24){
                    imageUri = FileProvider.getUriForFile(getActivity(),
                            "com.example.emoji.finalwork.fileprovider",outputImage);
                }else{
                    imageUri = Uri.fromFile(outputImage);
                    System.out.println("照片地址+"+imageUri);
                }
                //启动相机程序
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                startActivityForResult(intent,TAKE_PHOTO);

            }
        });
        return view;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK){
                    try{
                        //将拍摄的照片显示出来 先将照片解析成Bitmap对象，再将它设置到ImageView中显示出来
                        Bitmap bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(imageUri));
                        picture.setImageBitmap(bitmap);

                    }catch(FileNotFoundException e){
                        e.printStackTrace();
                    }
                    String salt = String.valueOf(2);
                    ContentResolver resolver = getActivity().getContentResolver();
                    InputStream inStream= null;
                    try {
                        inStream = resolver.openInputStream(imageUri);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    String query=Base.getBase64OfFile(inStream);
                    OcrWordTask task = new OcrWordTask();
                task.execute(query);
                }
                break;
            default:
                break;
        }
    }
    class OcrWordTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... arg0) {
         return HttpUtil.postJSONResult(arg0.toString());
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result == null || "".equals(result)){
                Toast.makeText(getActivity(), "查询出错！", Toast.LENGTH_LONG).show();
            }else {
                try {
                    fillResultForJSON(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
        private void fillResultForJSON(String JSON) throws JSONException {
            JSONObject object = new JSONObject(JSON);
            System.out.print("返回的结果：");
            System.out.println(object.toString());
            scrollview.setVisibility(View.VISIBLE);
            if(object.getString("errorCode").equals("1301"))
              txt.setText("OCR段落识别失败，请重新拍照上传！！！");
            else{
                JSONObject resultObject = object.getJSONObject("result");
                JSONArray regionsObject=resultObject.getJSONArray("regions");
                    txt.append(regionsObject.toString() );


            }
        }
    }


    }



