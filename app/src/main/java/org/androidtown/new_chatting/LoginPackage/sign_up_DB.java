package org.androidtown.new_chatting.LoginPackage;

import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by 김승훈 on 2017-07-05.
 */
public class sign_up_DB {


    // 유저 정보 및 주소의 변수들
    private String urlPath,user_email,user_nickname,user_name,user_password;
    // php파일 저장되는 경로
    private final String  signup_user_information_UrlPath ="http://115.71.233.69/user_info/signup.php";
    private  ArrayList<String> results;


    //유저의 정보를 리스트로 받는 부분
    public ArrayList<String> User_Info_Array(String user_email,String user_nickname,String user_name
            ,String user_password)
    {
        urlPath=signup_user_information_UrlPath;
        this.user_email=user_email;
        this.user_nickname=user_nickname;
        this.user_name=user_name;
        this.user_password=user_password;

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
                results = new SignUpUser().execute().get();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return results;
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    class SignUpUser extends AsyncTask<Void,Void,ArrayList<String>>{



        @Override
        protected ArrayList<String> doInBackground(Void... voids) {
            try{
                URL url = new URL(urlPath);
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setDoInput(true);
                con.setDoOutput(true);
                con.setUseCaches(false);
                con.setRequestMethod("POST");

                String param = "User_Email="+user_email+"&User_NickName="+user_nickname+"&User_Name="+user_name+"&User_Pwd="+user_password;
                Log.d("스트링파람",param);
                OutputStream outputStream = con.getOutputStream();

                outputStream.write(param.getBytes());
                outputStream.flush();
                outputStream.close();

                BufferedReader rd = null;
                rd = new BufferedReader(new InputStreamReader(con.getInputStream(),"UTF-8"));
                String line = null;
                while((line = rd.readLine())!=null){
                    Log.d("BufferdReader",line);
                }

            }catch (UnsupportedEncodingException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }
            return null;
        }
    }


}
