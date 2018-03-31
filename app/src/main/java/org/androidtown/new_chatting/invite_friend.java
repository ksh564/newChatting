package org.androidtown.new_chatting;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import org.androidtown.new_chatting.Adapter.NameBean;
import org.androidtown.new_chatting.LoginPackage.LoginDTO;
import org.androidtown.new_chatting.Other.static_adp;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by 김승훈 on 2017-08-23.
 */

public class invite_friend extends AppCompatActivity {

    private ListView listview;
    String JSON_User_list,user_nickName,user_email;
    ArrayList<LoginDTO> loginDTOs;
    ArrayList<NameBean> nameBean;
    ArrayList<String> sortArray,emailArray;
    String[] nick_arr;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.confirm, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id==R.id.confirm){

            Intent intent = new Intent(invite_friend.this,Chat_Activity.class);




            // 방이름을 닉네임으로 받을때 통일을 시켜주기 위해 정렬을 시켜주는 메소드

            loginDTOs= static_adp.getMultiple_choice_adp().UserDto;

            nick_arr = new String[loginDTOs.size()];
            emailArray = new ArrayList<String >();

            emailArray.add(user_email);
            sortArray = new ArrayList<String>();
//            NameBean nameBean;


            for(LoginDTO loginDTO : loginDTOs){
                if(loginDTO.isSelected()==true){
                    emailArray.add(loginDTO.get_email());
                  sortArray.add(loginDTO.get_nickname());
                }
            }

            //
//            for(LoginDTO loginDTO : loginDTOs){
//                if(loginDTO.isSelected()==true){
//
//                }
//            }

            Collections.sort(sortArray);
            StringBuilder stringBuilder = new StringBuilder(user_nickName);
            for(int i=0; i<sortArray.size(); i++){
                stringBuilder.append("_"+sortArray.get(i));
            }
            //정렬 메소드 끝

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("result",emailArray);
            JSON_User_list = jsonObject.toString();


            intent.putExtra("User_List_Json",JSON_User_list);
            intent.putExtra("Room_name",stringBuilder.toString().trim());
            intent.putExtra("isRoom",false);
            startActivity(intent);
            finish();
//            Toast.makeText(getApplicationContext(),JSON_User_list,Toast.LENGTH_LONG).show();
//            intent.putExtra("User_List_Json",JSON_User_list);
//            intent.putExtra("Room_name",stringBuilder.toString().trim());
//            intent.putExtra("isRoom",false);
//            startActivity(intent);


        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.invite_friend);
        super.onCreate(savedInstanceState);

        user_nickName=org.androidtown.new_chatting.LoginPackage.person.getNick();
        user_email = org.androidtown.new_chatting.LoginPackage.person.getEmail();

        listview = (ListView)findViewById(R.id.ROOM_LIST_VIEW);
        listview.setAdapter(org.androidtown.new_chatting.Other.static_adp.getMultiple_choice_adp());

        Toolbar mtoolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(mtoolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.mipmap.new_backbutton);
        actionBar.setTitle("대화초대");

    }
}
