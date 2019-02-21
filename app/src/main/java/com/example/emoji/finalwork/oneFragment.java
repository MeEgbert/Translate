package com.example.emoji.finalwork;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
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
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@SuppressLint("ValidFragment")
class OneFragment extends Fragment{
    private ImageButton ImgButton;
    private ImageButton searchButton;
    private EditText txt;
    private Button from;
    private Button to;
    private Button UKButton = null;
    private WebView webView = null;
    private MediaPlayer UKMediaPlayer = null;
    private MediaPlayer USMediaPlayer = null;
    private TextView us_pronunciation = null;
    private TextView uk_pronunciation = null;
    private TextView paraphrase = null;
    public static final int TAKE_PHOTO = 1;
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,  Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home, null);
        ImgButton=view.findViewById(R.id.reverseButton);
        searchButton=view.findViewById(R.id.searchButton);
        from=view.findViewById(R.id.from);
        to=view.findViewById(R.id.to);
        txt=view.findViewById(R.id.txt);
        UKButton = (Button) view.findViewById(R.id.uk_btn);
        us_pronunciation=(TextView) view.findViewById(R.id.us_pronunciation);
        uk_pronunciation=(TextView) view.findViewById(R.id.uk_pronunciation);
        webView = (WebView) view.findViewById(R.id.detail_web);
        paraphrase=(TextView) view.findViewById(R.id.paraphrase);

        webView.getSettings().setJavaScriptEnabled(true);
        //设置详细结果的WebView不可见
        webView.setVisibility(View.INVISIBLE);
        System.out.println("额愤愤愤愤额愤愤愤愤额愤愤愤愤额愤愤愤愤额愤愤愤愤");
        System.out.println(from.getTag().toString());
        final MainActivity mainActivity = (MainActivity) getActivity();
        ImgButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String tt="";
                String value="";
                tt= (String) from.getText();
                value= (String) from.getTag();
                System.out.println(tt.toString());
                from.setText(to.getText());
                from.setTag(to.getTag());
                to.setTag(value);
                to.setText(tt);
                System.out.println("from的值"+from.getTag());
                System.out.println("to的值"+to.getTag());
            }
        });
        searchButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String query = txt.getText().toString().trim();
                SearchWordTask task = new SearchWordTask();
                String m=from.getTag().toString().trim();
                String s=to.getTag().toString().trim();
                String salt = String.valueOf(2);
                String sign = md5("7af0d590e3d5c5c6"+ query + salt+ "ArSmk7K7hAKZ1eDkxLQMwOSS6grFvgJK");
                task.execute("http://openapi.youdao.com/api?q="+query+"&from="+m+"&to="+s+"&appKey=7af0d590e3d5c5c6&salt="+salt+"&sign="+sign);
            }
        });
        return view;
    }

    class SearchWordTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... arg0) {
            return HttpUtil.getJSONResult(arg0[0]);
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result == null || "".equals(result)){
                Toast.makeText(getActivity(), "查询出错！", Toast.LENGTH_LONG).show();
            }else
                fillResultForJSON(result);

        }

        private void fillResultForJSON(String JSON){
            try {
                JSONObject object = new JSONObject(JSON);
                System.out.print("返回的结果：");
                System.out.println(object.toString());
                if (txt.getText().toString().equals(object.getString("query"))){
                    System.out.println("符合！！！");
                    final JSONObject baseObject = object.getJSONObject("basic");
                    System.out.println(baseObject.toString());
                    System.out.println(object.getString("speakUrl"));
                    //paraphrase.setText(baseObject.getString("basic"));
                    final String uk_audio = object.getString("speakUrl");
                    if(baseObject.has("uk-phonetic")){
                        uk_pronunciation.setText("["+baseObject.getString("uk-phonetic")+"]");
                    }else{
                        uk_pronunciation.setText("[null]");
                    }
                    if(baseObject.has("us-phonetic")){
                        us_pronunciation.setText("["+baseObject.getString("us-phonetic")+"]");
                    }else{
                        us_pronunciation.setText("[null]");
                    }
                    paraphrase.setText("");
                    String parameters=baseObject.getString("explains");
                    System.out.println(parameters);
                    String parameter[] = parameters.split(",");
                    for (String string : parameter) {
                        string =string.replace("["," ");
                        string = string.replace("\"","");
                        paraphrase.append(string.replace("]"," ") + "\n");
                    }
                    webView.setVisibility(View.VISIBLE);
                    webView.loadUrl("https://www.shanbay.com/bdc/mobile/preview/word?word="+txt.getText().toString().trim());
                    webView.setWebViewClient(new WebViewClient());
                    UKButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                UKMediaPlayer = new MediaPlayer();
                                UKMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                UKMediaPlayer.setDataSource(uk_audio);
                                UKMediaPlayer.prepare(); // 这个过程可能需要一段时间，例如网上流的读取
                                UKMediaPlayer.start();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    class OcrWordTask extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... arg0) {
            return HttpUtil.getJSONResult(arg0[0]);
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
        }
    }
    public static String md5(String string) {
        if(string == null){
            return null;
        }
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F'};

        try{
            byte[] btInput = string.getBytes("utf-8");
            /** 获得MD5摘要算法的 MessageDigest 对象 */
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            /** 使用指定的字节更新摘要 */
            mdInst.update(btInput);
            /** 获得密文 */
            byte[] md = mdInst.digest();
            /** 把密文转换成十六进制的字符串形式 */
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (byte byte0 : md) {
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        }catch(NoSuchAlgorithmException | UnsupportedEncodingException e){
            return null;
        }
    }
    public String getRealFilePath(final Uri uri ) {
        if ( null == uri ) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if ( scheme == null )
            data = uri.getPath();
        else if ( ContentResolver.SCHEME_FILE.equals( scheme ) ) {
            data = uri.getPath();
        } else if ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {
            Cursor cursor = getActivity().getContentResolver().query( uri, new String[] { MediaStore.Images.ImageColumns.DATA }, null, null, null );
            if ( null != cursor ) {
                if ( cursor.moveToFirst() ) {
                    int index = cursor.getColumnIndex( MediaStore.Images.ImageColumns.DATA );
                    if ( index > -1 ) {
                        data = cursor.getString( index );
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

}
