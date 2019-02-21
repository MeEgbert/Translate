package com.example.emoji.finalwork;



import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpUtil {
    /**
     * 获取访问网络后传回的数据
     *
     * @param urlString URL
     * @return String
     */
    public static String getJSONResult(String urlString) {
        try {

            System.out.println("连接地址是"+urlString);
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.84 Safari/537.36");

            InputStream is = conn.getInputStream();
            byte[] buff = new byte[88888];
            int hasRead;
            StringBuilder result = new StringBuilder("");
            while ((hasRead = is.read(buff)) > 0) {
                result.append(new String(buff, 0, hasRead));
            }
            return result.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String postJSONResult(String str) {
        String salt = String.valueOf(2);
        String sign = md5("7af0d590e3d5c5c6"+ str + salt+ "ArSmk7K7hAKZ1eDkxLQMwOSS6grFvgJK");
        Map<String, String> params = new HashMap<String, String>();
        OkHttpClient client = new OkHttpClient();
        String responseData=null;
        try {
            RequestBody requestBody = new FormBody.Builder()
                    .add("imageType", "1")
                    .add("langType", "zh-en")
                    .add("detectType", "10011")
                    .add("img", str)
                    .add("appKey", "7af0d590e3d5c5c6")
                    .add("salt", salt)
                    .add("sign", sign)
                    .add("docType","json")
                    .build();
            Request request=new Request.Builder()
                    .url("http://openapi.youdao.com/ocrapi")
                    .post(requestBody)
                    .build();
            System.out.println("参数是："+request.body().toString());
            Response response=client.newCall(request).execute();
             responseData=response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseData;
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
}
