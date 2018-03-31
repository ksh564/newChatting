package org.androidtown.new_chatting.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.androidtown.new_chatting.Adapter.ChatMessage;
import org.androidtown.new_chatting.Adapter.Room_DTO;
import org.androidtown.new_chatting.Adapter.room_List_Adp;
import org.androidtown.new_chatting.Chat_Activity;
import org.androidtown.new_chatting.Network.SocketService;
import org.androidtown.new_chatting.R;
import org.androidtown.new_chatting.SQ_lite.DBHelper;
import org.androidtown.new_chatting.invite_friend;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by 김승훈 on 2017-07-06.
 */
public class Chat_Fragment extends Fragment {
    private View Chat_Fragment_View;
    int frament_id;


    private String Chat_MSG,Chat_User_ID,Chat_User_Profile,Chat_Message_Time,Chat_Room_ID,Chat_Room_Name
            ,Chat_Read_Num,Msg_ID,Msg_User_Nick,Chat_UUID,User_Nick,Roomuser;
    String Room_Name,Room_User_ID,User_list;
    StringTokenizer st;
    MyReceiver myReceiver;
    private ListView Room_List_View;

    SocketService socketService;
    DBHelper dbHelper;
    int From_db_Read_Number;
    private String From_db_unique_name,From_db_Room_Name,From_db_User_Profile,From_db_Chatting,From_db_Messag_Time,From_db_Read_Num;
    public ArrayList<Room_DTO> Room_Item;
    room_List_Adp room_list_adp;




    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        if (Chat_Fragment_View == null) {

            Chat_Fragment_View = inflater.inflate(R.layout.chat_fragment, container, false);
            frament_id = getId();
        }



        Room_List_View = (ListView)Chat_Fragment_View.findViewById(R.id.ROOM_LIST_VIEW);


        dbHelper = new DBHelper(getActivity(), "CHAT_MESSAGE.DB", null, 1);
//        dbHelper = new DBHelper(getActivity(),"ROOMLIST.db",null,1);

//        ArrayList<Room_DTO> db = new ArrayList<>();
//        db = dbHelper.getResult_ChatRoomList();
//            //sqlite 로 저장된 채팅방 불러오기
//        for(int i=0; i<db.size(); i++){
//
//            From_db_unique_name = dbHelper.getResult_ChatRoomList().get(i).getRoom_Unique_name();
//            From_db_Room_Name = dbHelper.getResult_ChatRoomList().get(i).getUser_Room_Name();
//            From_db_User_Profile = dbHelper.getResult_ChatRoomList().get(i).getUser_Profile();
//            From_db_Chatting = dbHelper.getResult_ChatRoomList().get(i).getUser_Chat();
//
//            From_db_Messag_Time = dbHelper.getResult_ChatRoomList().get(i).getMessage_Time();
//
//            room_list_adp.addItem(From_db_unique_name,From_db_Room_Name,From_db_User_Profile,From_db_Chatting,From_db_Messag_Time);
//            room_list_adp.notifyDataSetChanged();
//        }

        //친구추가 할 때 들어가는 버튼튼

        FloatingActionButton fab = (FloatingActionButton)Chat_Fragment_View.findViewById(R.id.Chatting_FAB);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), invite_friend.class);
                startActivity(intent);

            }
        });




        return Chat_Fragment_View;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.e(getClass().getName(),"onResume");
        Room_Item = new ArrayList<>();
        room_list_adp = new room_List_Adp(Room_Item);
        Room_List_View.setAdapter(room_list_adp);

        ArrayList<Room_DTO> db = new ArrayList<>();
        ArrayList<ChatMessage> get_db = new ArrayList<>();

        db = dbHelper.getResult_ChatRoomList();


        System.out.println("방목록 갯수:"+db.size());
        //sqlite 로 저장된 채팅방 불러오기
        for(int i=0; i<db.size(); i++){

            From_db_unique_name = dbHelper.getResult_ChatRoomList().get(i).getRoom_Unique_name();
            From_db_Room_Name = dbHelper.getResult_ChatRoomList().get(i).getUser_Room_Name();
            From_db_User_Profile = dbHelper.getResult_ChatRoomList().get(i).getUser_Profile();
//            From_db_Chatting = dbHelper.getResult_ChatRoomList().get(i).getUser_Chat();
//            From_db_Messag_Time = dbHelper.getResult_ChatRoomList().get(i).getMessage_Time();
//            From_db_Read_Num = dbHelper.getResult_ChatRoomList().get(i).getMsg_read_Num();
            // 채팅방에 저장된 메세지와 시간 부름
            get_db = dbHelper.getResult_ChatMsg(From_db_unique_name);
            int readnum = 0;

            System.out.println("채팅방에 메세지 갯수: "+get_db.size());
            for(int j=0; j<get_db.size(); j++){
                System.out.println("채팅방출력해보셈: "+dbHelper.getResult_ChatMsg(From_db_unique_name).get(i).getReadMessage());
                if (dbHelper.getResult_ChatMsg(From_db_unique_name).get(i).getReadMessage() == 0){
                    readnum += 1;

                }
            }


            From_db_Chatting = dbHelper.getResult_ChatMsg(From_db_unique_name).get(get_db.size()-1).getMessage();
            From_db_Messag_Time = dbHelper.getResult_ChatMsg(From_db_unique_name).get(get_db.size()-1).getDateTime();
            From_db_Read_Number = readnum;
            System.out.println("채팅방에 읽은 메세지 숫자: "+From_db_Read_Number);

            room_list_adp.addItem(From_db_unique_name,From_db_Room_Name,From_db_User_Profile,From_db_Chatting,From_db_Messag_Time,From_db_Read_Number);
            room_list_adp.notifyDataSetChanged();
        }
        initControls();

        Room_List_View.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long ID) {

                // 아이템을 클릭하면 룸이 있다는 정보와 룸 이름을 보내주게 된다.
                Room_Name = Room_Item.get(position).getRoom_Unique_name();
                User_list = Room_Item.get(position).getUser_Room_Name();

                Room_Item.get(position).setMsg_Numer(0);
                room_list_adp.notifyDataSetChanged();
                Intent intent = new Intent(getActivity(), Chat_Activity.class);

                intent.putExtra("isRoom",true);
                intent.putExtra("User_List_Json",User_list);
                intent.putExtra("Room_name",Room_Name);
                startActivity(intent);
            }
        });
        super.onResume();
    }

    @Override
    public void onPause() {

        getActivity().unregisterReceiver(myReceiver);
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.e(getClass().getName(),"onStop");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.e(getClass().getName(),"onDestroy");
        super.onDestroy();
    }

    // 프래그먼트 초기 세팅
    private void initControls()
    {

        System.out.println("채팅프래그먼트_초기세팅");


        //브로드캐스트리시버 세팅
        registerReceiver();










    }

    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {


            String Received_Data = intent.getStringExtra("message");

            String[] st = Received_Data.split("\\|\\|");

            String protocol = st[0];
            String Message = st[1];

            System.out.println("프로토콜 : " + protocol);
            System.out.println("내용 : " + Message);


            if (protocol.equals("CreateRoom")) //방을 만들었을때
            {

            } else if (protocol.equals("CreateRoomFail")) //방 만들기 실패 했을 경우
            {
                System.out.println("방 만들기 실패");
            } else if (protocol.equals("New_Room")) //새로운 방을 만들었을때
            {
//                room_List_Adp room_list_adp = new room_List_Adp(Room_Item);
                System.out.println("1번문제");
                Chat_Room_ID=Message;
                System.out.println("2번문제"+Chat_Room_ID);
                Chat_Room_Name =st[2];
                System.out.println("3번문제"+Chat_Room_Name);
                Chat_User_ID=st[3];
                System.out.println("4번문제"+Chat_User_ID);
                Chat_User_Profile=st[4];
                System.out.println("5번문제"+Chat_User_Profile);
                Chat_Message_Time=st[5];
                System.out.println("6번문제"+Chat_Message_Time);
                Chat_MSG=st[6];
                System.out.println("7번문제"+Chat_MSG);
                Msg_ID = st[7];
                Msg_User_Nick = st[8];
                Chat_Read_Num = st[9];



                room_list_adp.addItem(Chat_Room_ID,Chat_Room_ID,Chat_User_Profile,Chat_MSG,Chat_Message_Time,1);
                System.out.println("8번문제");

                room_list_adp.notifyDataSetChanged();

                System.out.println("완료");


            }else if (protocol.equals("Chatting")){

                Chat_Room_ID = Message;
                Chat_User_ID = st[2];
                Chat_User_Profile = st[3];
                Chat_Message_Time = st[4];
                Chat_MSG = st[5];
                Chat_UUID = st[6];
                User_Nick = st[7];
                Roomuser = st[8];
                System.out.println("룸아이템사이즈는"+Room_Item.size());
                for(int i=0; i<Room_Item.size();i++){

                   if(Room_Item.get(i).getRoom_Unique_name().equals(Chat_Room_ID)){
                       Room_Item.get(i).setMessage_Time(Chat_Message_Time);
                       Room_Item.get(i).setUser_Chat(Chat_MSG);
                       int msg =Room_Item.get(i).getMsg_Numer();
                       msg+=1;
                       Room_Item.get(i).setMsg_Numer(msg);

                   }

                }
                room_list_adp.notifyDataSetChanged();

            }

        }
    }

    private void registerReceiver() {

        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SocketService.SENDTOFRAGMENT);
        getActivity().registerReceiver(myReceiver,intentFilter);
    }


}
