package org.androidtown.new_chatting;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import org.androidtown.new_chatting.Adapter.ChatMessage;
import org.androidtown.new_chatting.Adapter.Chat_Msg_Adp;
import org.androidtown.new_chatting.Network.SocketService;
import org.androidtown.new_chatting.Other.monitorActivity;
import org.androidtown.new_chatting.SQ_lite.DBHelper;
import org.json.simple.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.Vector;

/**
 * Created by 김승훈 on 2017-08-17.
 */

public class Chat_Activity extends AppCompatActivity implements Chat_Msg_Adp.Chat_ItemClickListener{

    // 네트워크를 위한 자원 변수
    private Handler mHandler;
    private String ip = "115.71.233.69";
    private int port = 9999;
    private String join_room = "test";
    private String User_Email,httpresult;
    private Socket socket;
    private InputStream is;
    private OutputStream os;
    private DataInputStream dis;
    private DataOutputStream dos;
    private JSONObject JsoNResult, JsoN, Read_Result, Json_Result;


    //그외 변수들입니다.
    Vector user_list = new Vector();
    Vector room_list = new Vector();
    StringTokenizer st;
    final int MY_PERMISSION_REQUEST_STORAGE = 0;
    private static final int SELECT_VIDEO = 3;
    private String selectedPath;

    private boolean noReadMsg = false;
    private static ArrayList<String> message_list, Read_list;
    private String UUID_Intent, User_Profile_Photo, Message_Time, inRoomUserNum, Roomuser,New_User_Nick,New_User_Profile,New_User_Email;
    private String My_Room = "test"; //내가 있는 방 이름 test라고 임의로 설정합니다.
    private String Chat_MSG, Chat_Room_ID, Chat_User_ID, Chat_User_Profile, Chat_Message_Time, OtherUser, Send_Message, server_message,
            User_List_Json, Chat_UUID, User_Nick, Msg_Read_Num,msgProtocol;
    // 채팅메시지 SQLITE 변수
    private String From_db_sender, From_db_User_Profile, From_db_MSG_Time, From_db_MSG;

    //안드로이드 클라이언트의 채팅을 하기위한 ui변수
    private EditText messageET;
    private ListView messagesContainer;
    private Button sendBtn, addOnBtn;
    private Chat_Msg_Adp adapter;
    private ArrayList<ChatMessage> chatHistory, chatsqlite;
    private int FromActivity;
    private int readChat;


    //파일업로드 변수


    ProgressDialog dialog = null;
    String Uri_img;



    SocketService socketService;
    boolean isService = false;
    boolean isRoom;
    // db헬퍼
    DBHelper dbHelper;

    MyReceiver myReceiver;


    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            SocketService.LocalBinder myBinder = (SocketService.LocalBinder) service;

            socketService = myBinder.getService();
            System.out.println("서비스커넥트확인" + socketService);
            isService = true;



            // 온크리에이트에서 왔을때
            if(FromActivity==200){

                ////////////////////////// 메세지들 불러올때 arraylist 리셋하기
                Boolean Is_Room = false;
                Is_Room = dbHelper.Find_Table(My_Room);
                System.out.println("방정보가 있습니까?" + Is_Room);

                if (Is_Room == true) {
                    chatHistory = new ArrayList<ChatMessage>();
                    //만약 방 정보가 있으면
//            My_Room=UUID_Intent;

                    // 방정보를 통해 sqlite에 저장된 메세지 불러오기

                    // ChatMessage라는 아이템에 어레이리스트를 초기화
                    ArrayList<ChatMessage> db = new ArrayList<>();
                    // ChatMessage라는 아이템에 방에 고유 번호아이디를 넣어 결과물을 받아오게 세팅
                    db = dbHelper.getResult_ChatMsg(My_Room);

                    JsoNResult = new JSONObject();

                    JsoN = new JSONObject();
                    message_list = new ArrayList<String>();

                    // 방에 갯수만큼 포문을 돌린다.
                    for (int i = 0; i < db.size(); i++) {
                        ChatMessage chatMessage = new ChatMessage();
                        String msg_id_convert;


                        chatMessage.setId(dbHelper.getResult_ChatMsg(My_Room).get(i).getId());
                        if (New_User_Email.equals(dbHelper.getResult_ChatMsg(My_Room).get(i).getId())) {
                            chatMessage.setisMe(true);
                            System.out.println("비교했을때 트루");
                        } else {
                            chatMessage.setisMe(false);
                            System.out.println("비교했을때 뻘스");
                        }
                        chatMessage.setChat_Profile_Photo(dbHelper.getResult_ChatMsg(My_Room).get(i).getChat_Profile_Photo());
                        chatMessage.setDateTime(dbHelper.getResult_ChatMsg(My_Room).get(i).getDateTime());
                        chatMessage.setMessage(dbHelper.getResult_ChatMsg(My_Room).get(i).getMessage());
                        chatMessage.setMessageID(dbHelper.getResult_ChatMsg(My_Room).get(i).getMessageID());
                        chatMessage.setUserNick(dbHelper.getResult_ChatMsg(My_Room).get(i).getUserNick());
                        chatMessage.setReadNum(dbHelper.getResult_ChatMsg(My_Room).get(i).getReadNum());
                        chatMessage.setProtocol(dbHelper.getResult_ChatMsg(My_Room).get(i).getProtocol());

                        // 만약 메세지가 읽음 상태가 아니면 메세지를 보내게 셋팅해준다다
                        if (dbHelper.getResult_ChatMsg(My_Room).get(i).getReadMessage() == 0) {
                            msg_id_convert = dbHelper.getResult_ChatMsg(My_Room).get(i).getMessageID().toString();
                            int read_num = Integer.parseInt(dbHelper.getResult_ChatMsg(My_Room).get(i).getReadNum());
                            String calReadNum = String.valueOf(read_num);
                            JsoN.put("message_id", msg_id_convert);
                            JsoN.put("message_readnum", calReadNum);
                            JsoN.put("room_name", My_Room);
                            JsoN.put("user", New_User_Email);
                            message_list.add(JsoN.toString());
                            dbHelper.update_chat_msg(My_Room, dbHelper.getResult_ChatMsg(My_Room).get(i).getMessageID());

                        }


//                    chatHistory.add(chatMessage);

                        adapter.add(chatMessage);


                    }

                    JsoNResult.put("result", message_list);
                    System.out.println("방읽음갯수업데이트" + JsoNResult.toString());


                    adapter.notifyDataSetChanged();
                    scroll();


                } else if (Is_Room == false) {
//            String uuid =UUID.randomUUID().toString().replaceAll("-","");
//            My_Room = "Table"+uuid;

                }

                if(message_list!=null){
                    socketService.send_message2("ReadUpdate||" + JsoNResult.toString());
                }
            }






        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isService = false;
        }
    };

    @Override
    protected void onPause() {
        FromActivity = 300;
        super.onPause();
    }

    @Override
    protected void onStop() {

        Log.e(getClass().getName(), "onStop");
        unbindService(conn);
        unregisterReceiver(myReceiver);
        monitorActivity.setInChatActivity(false);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.e(getClass().getName(), "onDestroy");
        super.onDestroy();
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);


        FromActivity = 200;
        //유저의 정보를 받는 부분
        New_User_Email = org.androidtown.new_chatting.LoginPackage.person.getEmail();
        New_User_Profile = org.androidtown.new_chatting.LoginPackage.person.getId_img();
        New_User_Nick = org.androidtown.new_chatting.LoginPackage.person.getNick();


        //프렌드 프래그먼트에서 인텐트(이메일 정보)를 받음
        final Bundle Intent = getIntent().getExtras();
        User_List_Json = Intent.getString("User_List_Json");
//        isRoom = Intent.getBoolean("isRoom");
        My_Room = Intent.getString("Room_name");
        UUID_Intent = Intent.getString("UUID");
        System.out.println("인텐트" + User_List_Json + UUID_Intent + isRoom);
        initControls();



    }

    @Override
    protected void onResume() {


        //브로드캐스트리시버
        registerReceiver();
        //서비스 바인딩 하는 부분
        Intent intent = new Intent(this, SocketService.class);
        startService(intent);
        monitorActivity.setInChatActivity(true);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
        // 요소들 초기화

        super.onResume();
    }


    // 처음에 세팅하는 부분
    private void initControls() {


        // sqlite 세팅하는 부분
        dbHelper = new DBHelper(getApplicationContext(), "CHAT_MESSAGE.DB", null, 1);

        messagesContainer = (ListView) findViewById(R.id.messagesContainer);
        messageET = (EditText) findViewById(R.id.etMessage);
        sendBtn = (Button) findViewById(R.id.sendBtn);
        addOnBtn = (Button) findViewById(R.id.addonBtn);
//        socketBtn = (Button)findViewById(R.id.socket);
//        roomBtn = (Button)findViewById(R.id.socket2);

        chatHistory = new ArrayList<ChatMessage>();
        RelativeLayout container = (RelativeLayout) findViewById(R.id.container);
        adapter = new Chat_Msg_Adp(Chat_Activity.this, new ArrayList<ChatMessage>(),this);
        messagesContainer.setAdapter(adapter);
        System.out.println("initcontrol바인드" + socketService);

        addOnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MaterialDialog dialog = new MaterialDialog.Builder(Chat_Activity.this)
                        .title(R.string.file_transfer)
                        .items(R.array.addon)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {

                                // 파일전송이라는 버튼을 클릭했을때
                                if (position == 0) {
//                                    checkPermission();
                                    chooseVideo();
                                } else if (position == 1) {
                                    Toast.makeText(getApplicationContext(),"클릭이됩니다"+position,Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .show();
            }
        });
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageET.getText().toString();
                //TextUtils
                if (TextUtils.isEmpty(messageText)) {
                    return;
                }


                Send_Message = messageText;

                String uuid = UUID.randomUUID().toString();
                Chat_UUID = uuid.replaceAll("-", "_");
                Message_Time = DateFormat.getDateTimeInstance().format(new Date());
                ChatMessage chatMessage = new ChatMessage();

                //채팅메세지에 아이디를 넣어주는 부분

                chatMessage.setId(New_User_Email.toString());
                chatMessage.setMessage(messageText);
                chatMessage.setDateTime(DateFormat.getDateTimeInstance().format(new Date()));
                chatMessage.setisMe(true);
                chatMessage.setMessageID(Chat_UUID);
                chatMessage.setUserNick(New_User_Nick);


                messageET.setText("");
//                displayMessage(chatMessage);


                // 메세지 보내는 부분
                socketService.send_message2("multi_chat||" + My_Room + "||" + New_User_Email + "||" + User_List_Json + "||" + New_User_Profile + "||" + Message_Time + "||" + Send_Message.trim() + "||" + Chat_UUID.trim() + "||" + New_User_Nick.trim()+"||"+"text");

                // Chatting + 방이름 + 내용
                System.out.println("채팅 전송 버튼 클릭");


            }
        });
    }
    // 카메라 인텐트 결과값으로 처리하는 메서드
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RESULT_OK){
            if(requestCode==SELECT_VIDEO){

                Uri selectedImageUri = data.getData();
                selectedPath = getPath(selectedImageUri);
                socketService.callupload(selectedPath);

            }
        }
    }

    //동영상 경로 받는 메서드
    public String getPath(Uri uri){
        Cursor cursor = getContentResolver().query(uri,null,null,null,null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":")+1);
        cursor.close();

        cursor = getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
        cursor.close();

        return path;
    }

    private void chooseVideo(){
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select a Video"),SELECT_VIDEO);
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermission() {



        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "Read/Write external Storage", Toast.LENGTH_SHORT).show();
            }
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST_STORAGE);
        }
        else{
            chooseVideo();
        }
    }
    // 권한설정 결과값 받기
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    //권한 허용시
                    chooseVideo();

                } else {
                    Toast.makeText(Chat_Activity.this, "권한사용을 동의해주셔야 이용이 가능합니다.", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
        }
    }

    public void displayMessage(ChatMessage message) {
        adapter.add(message);
        adapter.notifyDataSetChanged();
        scroll();
    }

    private void scroll() {
        messagesContainer.setSelection(messagesContainer.getCount() - 1);
    }



    // 동영상 클릭할떄 넘어가는 메서드
    @Override
    public void onItemClick(View v, int position) {

        switch (v.getId()){
            case R.id.videoProgress:

                System.out.println("비디오버튼 버튼 클릭");
                String url =adapter.getItem(position).getMessage();
                String[] st = url.split("\\*\\*");


                String thumnail = st[0];
                System.out.println(getClass().getName()+":에서 온 로그"+" "+"st[0]"+thumnail);
                final String video_url = st[1];
                String messageid;
                messageid = adapter.getItem(position).getMessageID();
                System.out.println(getClass().getName()+":에서 온 로그"+" "+"st[1]"+video_url);
                Intent intent = new Intent(Chat_Activity.this,video_view.class);
                intent.putExtra("url",video_url);
                intent.putExtra("message_id",messageid);
                startActivity(intent);


                break;
        }

    }












    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String Received_Data = intent.getStringExtra("message");


            //메세지 읽음 업데이트를 위한 변수
            Json_Result = new JSONObject();
            Read_Result = new JSONObject();
            Read_list = new ArrayList<String>();


            String[] st = Received_Data.split("\\|\\|");


            String protocol = st[0];
            String Message = st[1];


            System.out.println("프로토콜 : " + protocol);
            System.out.println("내용 : " + Message);

            if (protocol.equals("NewUser")) // 새로운 접속자
            {
                user_list.add(Message);

            } else if (protocol.equals("CreateRoom")) //방을 만들었을때
            {
                My_Room = Message;
            } else if (protocol.equals("CreateRoomFail")) //방 만들기 실패 했을 경우
            {
                System.out.println("방 만들기 실패");
            } else if (protocol.equals("New_Room")) //새로운 방을 만들었을때
            {
                room_list.add(Message);

            } else if (protocol.equals("Chatting")) {

                Chat_Room_ID = Message;
                Chat_User_ID = st[2];
                Chat_User_Profile = st[3];
                Chat_Message_Time = st[4];
                Chat_MSG = st[5];
                Chat_UUID = st[6];
                User_Nick = st[7];
                Roomuser = st[8];
                msgProtocol = st[9];

                //채팅을 읽었을 때
                if (monitorActivity.isInChatActivity() == true) {

                    if(Chat_User_ID.equals(New_User_Email)){

                    }else{
                        readChat = 1;

                        Read_Result.put("message_id", Chat_UUID);
                        Read_Result.put("message_readnum", Roomuser);
                        Read_Result.put("room_name", Chat_Room_ID);
                        Read_Result.put("user", New_User_Email);
                        Read_list.add(Read_Result.toString());

                        Json_Result.put("result", Read_list);
                        System.out.println("메세지받자마자상태 업데이트" + Read_list);
                        socketService.send_message2("ReadUpdate||" + Json_Result.toString());
                    }


                } else if (monitorActivity.isInChatActivity() == false) {

                    readChat = 0;
                }


                ChatMessage msg1 = new ChatMessage();


                System.out.println("너" + Chat_User_ID);

                msg1.setId(Chat_User_ID);
                if (New_User_Email.equals(Chat_User_ID.trim())) {
                    msg1.setisMe(true);
                    readChat=1;
                    System.out.println("비교했을때 트루");
                } else {
                    msg1.setisMe(false);
                    System.out.println("비교했을때 뻘스");
                }
                msg1.setMessage(Chat_MSG);
                msg1.setDateTime(Chat_Message_Time);
                msg1.setChat_Profile_Photo(Chat_User_Profile);
                msg1.setMessageID(Chat_UUID);
                msg1.setUserNick(User_Nick);
                msg1.setReadNum(Roomuser);
                msg1.setReadMessage(readChat);
                msg1.setProtocol(msgProtocol);


                chatHistory.add(msg1);

                displayMessage(msg1);
//            Chat_area.append(Message+":"+msg+"\n");
            } else if (protocol.equals("updateMsgNum")) {

                System.out.println("업데이트가 일어난다");

                Chat_Room_ID = Message;
                System.out.println("업데이트가 일어난다1" + Chat_Room_ID);
                Chat_UUID = st[2];
                System.out.println("업데이트가 일어난다2" + Chat_UUID);
                Roomuser = st[3];
                System.out.println("업데이트가 일어난다3" + Roomuser);


                for (int i = 0; i < adapter.getCount(); i++) {

//                    if (chatHistory.get(i).getMessageID().equals(Chat_UUID)) {
//                        chatHistory.get(i).setReadNum(Roomuser);
//                    }

                    if(adapter.getItem(i).getMessageID().equals(Chat_UUID)){
                        adapter.getItem(i).setReadNum(Roomuser);
                    }
                    adapter.notifyDataSetChanged();
                }



            }else if(protocol.equals("upload_result")){

                String uuid = UUID.randomUUID().toString();
                Chat_UUID = uuid.replaceAll("-", "_");
                Message_Time = DateFormat.getDateTimeInstance().format(new Date());

                Send_Message =Message;

                socketService.send_message2("multi_chat||" + My_Room + "||" + New_User_Email + "||" + User_List_Json + "||" + New_User_Profile + "||" + Message_Time + "||" +
                        Send_Message.trim() + "||" + Chat_UUID.trim() + "||" + New_User_Nick.trim()+"||"+"video");

            }


        }
    }

    // 브로드캐스트 리시버를 받는 부분
    private void registerReceiver() {

        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SocketService.SENDMESAGGE);
        registerReceiver(myReceiver, intentFilter);
    }




}
