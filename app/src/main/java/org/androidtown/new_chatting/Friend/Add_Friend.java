package org.androidtown.new_chatting.Friend;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import org.androidtown.new_chatting.Adapter.find_friend_adp;
import org.androidtown.new_chatting.LoginPackage.LoginDTO;
import org.androidtown.new_chatting.R;
import org.androidtown.new_chatting.photo_view;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
 * Created by 김승훈 on 2017-07-19.
 */
public class Add_Friend extends AppCompatActivity implements find_friend_adp.Friend_ItemClickListener {


    //http 통신을 위한 변수들

    private final String UrlPath = "http://115.71.233.69/user_info/find_friend.php";
    String find_email,User_Email,Friend_Email,Friend_NickName,Friend_Profile,photo_url;

    public final ArrayList<LoginDTO> find_item = new ArrayList<>();
    // http 통신에 대한 결과를 받는 변수
    String results,friend_nick,friend_email,friend_profile,friend_confirm;
    int Friend_Button_CHK;


    // 액티비티에 뷰들
    ListView find_freind_list;
    ImageButton find_Btn;
    EditText find_et;
    find_friend_adp find_friend_adp;

    //로그인 변수 받기


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_friend);


        User_Email = org.androidtown.new_chatting.LoginPackage.person.getEmail();


        // 뷰 세팅
        find_Btn=(ImageButton)findViewById(R.id.find_button);
        find_et=(EditText)findViewById(R.id.find_nick_name_et);
        find_freind_list = (ListView)findViewById(R.id.add_friend_listview);

        find_friend_adp = new find_friend_adp(find_item,this);


        find_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    find_email=find_et.getText().toString();
                    new Find_information(find_email).execute().get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    // 리스트뷰에서 친구추가 버튼
    @Override
    public void onItemClick(View v, int position) {

        switch (v.getId()){

            case R.id.add_friend:


                find_item.get(position).setIsChecked(1);
                find_friend_adp.notifyDataSetChanged();
                org.androidtown.new_chatting.Friend.Update_DB_Friend update_db_friend = new org.androidtown.new_chatting.Friend.Update_DB_Friend();
                Friend_Email = find_item.get(position).get_email();
                Friend_NickName = find_item.get(position).get_nickname();
                Friend_Profile =find_item.get(position).getImgurl();
                update_db_friend.update_friend(User_Email,Friend_Email,Friend_NickName,Friend_Profile);
                Toast.makeText(Add_Friend.this,"친구가 추가 되었습니다.",Toast.LENGTH_SHORT).show();
                System.out.println("친구추가 했습니다다");
                setResult(RESULT_OK);
                finish();

                break;
            case R.id.profile_click:
                photo_url=find_item.get(position).getImgurl();
                Intent intent = new Intent(Add_Friend.this, photo_view.class);
                intent.putExtra("url_image",photo_url);
                startActivity(intent);
                break;

        }

    }

    class Find_information extends AsyncTask<Void,Void,String> {
        String email;

        Find_information(String email){
            this.email=email;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {

            try {
                URL url = new URL(UrlPath);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setDoInput(true);
                con.setDoOutput(true);
                con.setUseCaches(false);
                con.setRequestMethod("POST");

                String param = URLEncoder.encode("Email", "UTF-8") + "=" +  URLEncoder.encode(email, "UTF-8");
                System.out.println("아이디바꾸는패럼확인2"+email);

                OutputStream outputStream = con.getOutputStream();
                outputStream.write(param.getBytes());
                outputStream.flush();
                outputStream.close();

                BufferedReader rd = null;
                rd = new BufferedReader(new InputStreamReader
                        (con.getInputStream(), "UTF-8"));
                String line = null;
                while ((line = rd.readLine()) != null) {
                    Log.d("회원정보찾기", line);
                    results=line;
                }



            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return results;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            String jsonPage = result;
            ArrayList<LoginDTO> loginDTOs = new ArrayList<>();

            JSONParser jsonParser = new JSONParser();
            try {

                // 결과값으로 온 스트링을 jsonobject로 파스
                JSONObject jsonObject = (JSONObject)jsonParser.parse(jsonPage);
                // jsonobject를 jsonarray로
                org.json.simple.JSONArray friend_array = (org.json.simple.JSONArray) jsonObject.get("dbresult");
                System.out.println("friend_array_확인"+friend_array);

                String Email,NickName,Profile,Confirm,Checker;
                int Check;


                for(int i =0; i< friend_array.size(); i++){
                    JSONObject obj = (JSONObject)friend_array.get(i);
                    Email = (String)obj.get("Email");
                    NickName=(String)obj.get("Nickname");
                    Profile=(String)obj.get("profile_img");
                    Confirm = (String)obj.get("result");
                    Checker=(String)obj.get("IsCheck");


                    if(Profile!=null){
                        Profile.replaceAll("\\/","/");
                    }
                    System.out.println("형변환후"+Profile);

                    LoginDTO entity = new LoginDTO();
                    entity.set_email(Email);
                    entity.set_nickname(NickName);
                    System.out.println("형변환전"+Profile);
                    entity.setImgurl(Profile);
                    entity.set_confirm(Confirm);
                    entity.setIsChecked(Integer.parseInt(Checker));

                    loginDTOs.add(entity);

                }




            } catch (ParseException e) {
                e.printStackTrace();
            }




            find_freind_list.setAdapter(find_friend_adp);

            // 만약 어댑터가 비워지지 않았으면
            if(find_friend_adp.isEmpty()==false){
                find_item.clear();
                find_friend_adp.notifyDataSetChanged();
            }




            for(LoginDTO entity : loginDTOs){
                friend_nick = entity.get_nickname();
                friend_email = entity.get_email();
                friend_profile= entity.getImgurl();
                friend_confirm=entity.get_confirm();
                Friend_Button_CHK = entity.getIsChecked();
            }

            if(friend_confirm.equals("true")){
                find_friend_adp.additem(friend_nick,friend_email,friend_profile,Friend_Button_CHK);
                find_friend_adp.notifyDataSetChanged();

            }else if(friend_confirm.equals("false")){
                System.out.println("해당 회원이 존재하지 않습니다.");
            }



        }
    }
}
