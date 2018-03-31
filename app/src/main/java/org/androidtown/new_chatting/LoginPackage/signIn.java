package org.androidtown.new_chatting.LoginPackage;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import org.androidtown.new_chatting.MainActivity;
import org.androidtown.new_chatting.R;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by 김승훈 on 2017-07-04.
 */
public class signIn extends AppCompatActivity {

    HttpURLConnection conn;
    URL url = null;
    public static final int CONNECTION_TIMEOUT=10000;
    private static boolean loginisset = false;
    person person;
    //입력되어 들어가는 파라미터들

    private final String signin_UrlPath = "http://ksh564.vps.phps.kr/user_info/Chat_Login.php";
    private String User_Id,User_Pwd,loginconfirm;
    static String LoginEmail,LoginPwd,LoginImg,LoginNick,LoginName;

    //버튼과 입력폼이 들어가는 변수들

    private Boolean loginAlive;
    private Button Sign_Up_btn,Login_Btn;
    private EditText Input_Id,Input_Pwd;

    //로그인 DTO 리스트
    private  ArrayList<org.androidtown.new_chatting.LoginPackage.LoginDTO> results;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin);

        SharedPreferences pref = getSharedPreferences("idbundle", MODE_PRIVATE);
        loginAlive =pref.getBoolean("check",false);

        if(loginAlive==true){
            Intent intent = new Intent(signIn.this,MainActivity.class);

            LoginEmail =pref.getString("id","");
            LoginNick =pref.getString("nick","");
            LoginName =pref.getString("name","");
            LoginImg =pref.getString("loginimg","");
            person = new person(LoginEmail,LoginNick,LoginName,LoginImg,loginisset);
            startActivity(intent);
        }

        // 입력폼과 버튼을 연결해 주는 부분
        Input_Id = (EditText)findViewById(R.id.Input_Id);
        Input_Pwd = (EditText)findViewById(R.id.Input_Pwd);

        Sign_Up_btn = (Button)findViewById(R.id.Sign_Up_Btn);
        Login_Btn =(Button)findViewById(R.id.Login_Btn);

    //회원가입 버튼
        Sign_Up_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(signIn.this, signUp.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivityForResult(intent,1000);
            }
        });

     //로그인 버튼

        Login_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //유저 아이디와 비밀번호를 에디트 텍스트에서 받는 부분
                User_Id=Input_Id.getText().toString();
                User_Pwd=Input_Pwd.getText().toString();
                //로그인이 실행되는 쓰레드 실행
                new login().execute();


            }
        });

    }


    //로그인 AsyncTask
    private class login extends AsyncTask<ArrayList, Void, ArrayList> {

        // 프로그레스 dialog
        ProgressDialog pdLoading = new ProgressDialog(signIn.this);
        HttpURLConnection conn;
        URL url = null;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pdLoading.setMessage("\t로딩중......");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }


        @Override
        protected ArrayList doInBackground(ArrayList... arrayLists) {

            return getJsonText();
        }



        @Override
        protected void onPostExecute(ArrayList result) {


            try{
                results=result;
            }catch (Exception e){
                e.printStackTrace();
            }
            for(org.androidtown.new_chatting.LoginPackage.LoginDTO dto : results){

                LoginImg=dto.getImgurl();
                LoginEmail=dto.get_email();
                LoginNick=dto.get_nickname();
                loginconfirm=dto.get_confirm();


            }

            pdLoading.dismiss();


            if(loginconfirm.equals("true"))
            {
                Intent intent = new Intent(signIn.this,MainActivity.class);
                SharedPreferences pref = getSharedPreferences("idbundle",MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
//                loginisset=true;
                editor.putString("id",LoginEmail);
                editor.putString("nick",LoginNick);
                editor.putString("name",LoginName);
                editor.putString("loginimg",LoginImg);




                person = new person(LoginEmail,LoginNick,LoginName,LoginImg,loginisset);

                editor.commit();
                startActivity(intent);
                signIn.this.finish();


            }else if(loginconfirm.equals("false")){
                System.out.println("로그인컨펌확인"+loginconfirm);
                YoYo.with(Techniques.Wobble)
                        .duration(700)
                        .repeat(3)
                        .playOn(findViewById(R.id.log_id));
                YoYo.with(Techniques.Wobble)
                        .duration(700)
                        .repeat(3)
                        .playOn(findViewById(R.id.log_pd));
                YoYo.with(Techniques.Wobble)
                        .duration(700)
                        .repeat(3)
                        .playOn(findViewById(R.id.Input_Id));
                YoYo.with(Techniques.Wobble)
                        .duration(700)
                        .repeat(3)
                        .playOn(findViewById(R.id.Input_Pwd));
                Toast.makeText(signIn.this,"아이디 또는 패스워드가 맞지않습니다.", Toast.LENGTH_LONG).show();
            }





        }


    }
    public ArrayList<org.androidtown.new_chatting.LoginPackage.LoginDTO> getJsonText() {

        StringBuffer sb = new StringBuffer();
        ArrayList<org.androidtown.new_chatting.LoginPackage.LoginDTO> list = new ArrayList<LoginDTO>();
        try {
            System.out.println("제이슨시작");
            String jsonpage = getStringFromUrl(User_Id,User_Pwd);
            System.out.println("로그인주소"+jsonpage);

            //파싱하는 곳
            JSONParser jsonParser = new JSONParser();
            System.out.println("회원가입0");
            JSONObject jsonObject = (JSONObject)jsonParser.parse(jsonpage);
            System.out.println("회원가입1");
            JSONArray test = (JSONArray) jsonObject.get("dbresult");

            System.out.println("회원가입jsontest"+test);

            String adminName,adminNick,adminEmail,adminConfirm,adminImg,adminIndex;

            JSONObject obj = (JSONObject)test.get(0);


            adminName=(String)obj.get("Name");
            adminNick=(String)obj.get("Nickname");
            adminEmail=(String)obj.get("Email");
            adminImg=(String)obj.get("profile_img");
            if(adminImg!=null){
                adminImg.replaceAll("\\/","/");
            }
            adminConfirm=(String)obj.get("result");


            //로그인 DTO 리스트에 파싱 정보 저장

            org.androidtown.new_chatting.LoginPackage.LoginDTO idset = new LoginDTO();

            idset.set_confirm(adminConfirm);
            idset.setImgurl(adminImg);
            idset.setName(adminName);
            idset.set_nickname(adminNick);
            idset.set_email(adminEmail);



            list.add(idset);

            return list;
        } catch (Exception e) {
        }
        System.out.println("로그인결과물갯수" + list.size());
        return list;
    }

    public String getStringFromUrl(String ...params) {
        try {
            url = new URL(signin_UrlPath);
        }catch (MalformedURLException e){
            e.printStackTrace();
            return "exception";
        }
        try{

            conn = (HttpURLConnection)url.openConnection();
            conn.setReadTimeout(CONNECTION_TIMEOUT);
            conn.setRequestMethod("POST");

            conn.setDoInput(true);
            conn.setDoOutput(true);

            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("user_id",params[0])
                    .appendQueryParameter("user_password",params[1]);

            String query = builder.build().getEncodedQuery();
            Log.e("log", "쿼리는?"+query);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer =new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            Log.e("log", "버퍼쓰기"+writer);
            writer.write(query);

            writer.flush();
            writer.close();
            os.close();
            conn.connect();
            Log.e("log", "버퍼라이터들어오는거");
        }
        catch (IOException e1){
            e1.printStackTrace();
            return "exception";
        }try{
            int response_code = conn.getResponseCode();
            if(response_code ==HttpURLConnection.HTTP_OK){

                InputStream input = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                StringBuilder result = new StringBuilder();
                String line;

                while ((line=reader.readLine())!=null){
                    result.append(line);
                    //line이 하는 일은 php서버단에서 정보가져오는거
                }

                return(result.toString());
            }else{
                return ("unsuccessful");
            }

        }catch (IOException e){
            e.printStackTrace();
            return "exception";

        }finally {
            conn.disconnect();
        }
    }

    @Override
    protected void onPause() {
        finish();
        super.onPause();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

    }
}
