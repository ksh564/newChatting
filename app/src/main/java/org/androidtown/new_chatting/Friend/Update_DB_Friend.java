package org.androidtown.new_chatting.Friend;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by 김승훈 on 2017-07-20.
 */
public class Update_DB_Friend {

    private String urlPath;
    private final String signup_user_information_UrlPath = "http://115.71.233.69/friend/update_friend.php";


    static String User_Email,Friend_Email,Friend_NickName,Friend_Profile;


    private ArrayList<String> results;

    public ArrayList<String> update_friend(String User_Email,String Friend_Email,String Friend_NickName,String Friend_Profile) {


        urlPath = signup_user_information_UrlPath;
        Update_DB_Friend.User_Email = User_Email;
        Update_DB_Friend.Friend_Email = Friend_Email;
        Update_DB_Friend.Friend_NickName = Friend_NickName;
        Update_DB_Friend.Friend_Profile = Friend_Profile;


        try {
            results = new update_friend().execute().get();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return results;
    }

    class update_friend extends AsyncTask<Void, Void, ArrayList<String>> {


        @Override
        protected ArrayList<String> doInBackground(Void... voids) {
            StringBuffer page2 = new StringBuffer();

            try {
                URL url = new URL(urlPath);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setDoInput(true);
                con.setDoOutput(true);
                con.setUseCaches(false);
                con.setRequestMethod("POST");
                System.out.println("");


                //utf-8로 인코딩 한 후 param  만들기
                String param = URLEncoder.encode("User_Email", "UTF-8") + "=" + User_Email;
                param += "&" + URLEncoder.encode("Friend_Email", "UTF-8") + "=" + Friend_Email;
                param += "&" + URLEncoder.encode("friend_nickname", "UTF-8") + "=" + Friend_NickName;
                param += "&" + URLEncoder.encode("friend_profile", "UTF-8") + "=" + Friend_Profile;


                OutputStream outputStream = con.getOutputStream();
                outputStream.write(param.getBytes());
                outputStream.flush();
                outputStream.close();


                BufferedReader rd = null;
                rd = new BufferedReader(new InputStreamReader
                        (con.getInputStream(), "UTF-8"));
                String line = null;
                while ((line = rd.readLine()) != null) {
                    Log.d("업데이트프렌드", line);
                }


            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(ArrayList<String> qResults) {
            super.onPostExecute(qResults);

        }
    }

}
