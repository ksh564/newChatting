package org.androidtown.new_chatting.Network;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import org.androidtown.new_chatting.Other.monitorActivity;
import org.androidtown.new_chatting.SQ_lite.DBHelper;
import org.androidtown.new_chatting.webRTC.IsCalledReady;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;


/**
 * Created by 김승훈 on 2017-07-11.
 */
public class SocketService extends Service {

    // 좀비서비스 변수

    private final String LOG_NAME = SocketService.class.getSimpleName();

    public static Thread mThread;
    public static boolean create_ok = false;

    Thread th;

    private ComponentName recentComponentName;
    private ActivityManager mActivityManager;

    private boolean serviceRunning = false;

    ////
    final static String MY_ACTION = "MY_ACTION";
    public static String SENDMESAGGE = "passMessage";
    public static String SENDTOFRAGMENT = "passToRoomList";


    // 네트워크를 위한 자원 변수
    private Handler mHandler;
    private String ip = "115.71.233.69";
    private int port = 9999;
    private String join_room = "test";
    private String User_Email;
    private Socket socket;
    private InputStream is;
    private OutputStream os;
    private DataInputStream dis;
    private DataOutputStream dos;

    //파일 송수신을 위한 변수들
    private FileOutputStream fos;
    private BufferedOutputStream bos;
    private FileInputStream fis;
    private BufferedInputStream bis;
    int serverResponseCode = 0;
    String upLoadServerUri = "http://115.71.233.69/user_info/uploadfromchat.php";
    private String My_Room = "test"; //내가 있는 방 이름 test라고 임의로 설정합니다.
    private String OtherUser, Send_Message,httpresult,Uri_img,thumbnailuri;
//    private byte[] recvMsgbyte;
//    private int readmsgLength;

    // 메세지 관리변수
    private int readMsg;
    private String MSG_Room_Id, Msg_Sender, Msg_User_Profile, Msg_Time, Msg_Content, Msg_ID, Msg_User_Nick, Msg_Read_Num,Msg_protocol;
    //채팅룸 관리 변수
    private String Chat_MSG, Chat_User_ID, Chat_User_Profile, Chat_Message_Time, Chat_Room_ID, Chat_Room_Name;
    // 영상통화 관리 변수
    private String ImageCallSender,ImageCallUUID,ImageCallSenderProfile;

//    String User_Email = org.androidtown.chatting.LoginPackage.person.getEmail();

    // 채팅 메세지가 들어올때 관리 하는 부분

    Boolean loginAlive;

    //그외 변수들입니다.

    StringTokenizer st;

    //Sqlite 부분
    DBHelper dbhelper, Chat_DB;
    BackThread backThread;

    //메세지 핸들러 부분 서비스->액티비티

//    test_handle handler;

    private final IBinder mBinder = new LocalBinder();


    public class LocalBinder extends Binder {

        public SocketService getService() {
            return SocketService.this;
        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // 액티비티에서 bindService() 를 실행하면 호출됨
        // 리턴한 IBinder 객체는 서비스와 클라이언트 사이의 인터페이스 정의한다


        return mBinder;
    }



    @Override
    public void onCreate() {
        if (socket != null) {
            try {
                dis.close();
                dos.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        SharedPreferences pref = getSharedPreferences("idbundle", MODE_PRIVATE);
//        SharedPreferences.Editor editor = pref.edit();
//        loginisset=true;
        User_Email = pref.getString("id", "");
//        editor.putString("id",User_Email);
        System.out.println("서비스는 죽었었다.." + User_Email);
//        User_Email = org.androidtown.chatting.LoginPackage.person.getEmail();
//        if(Service_is_alive.isService()==true){
        if (User_Email != null) {
            System.out.println("서버로 수신받는 쓰레드 시작");
            backThread = new BackThread();
            backThread.start();
        }

//            Service_is_alive.setService(false);
//        }
        Intent NEW = new Intent();
        NEW.setAction(MY_ACTION);
        mHandler = new Handler();
        // sqlite 세팅
        //방목록 SQLITE
        dbhelper = new DBHelper(getApplicationContext(), "ROOMLIST.db", null, 1);
        // 채팅 SQLITE
        Chat_DB = new DBHelper(getApplicationContext(), "CHAT_MESSAGE.DB", null, 1);
        //좀비서비스 액티비티 매니저
        mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        serviceRunning = true;


        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        SharedPreferences pref = getSharedPreferences("idbundle", MODE_PRIVATE);
        loginAlive = pref.getBoolean("check", false);

        Log.i("서비스 호출", "onStartCommand()실행됨,로그인상태:" + loginAlive);

        if (loginAlive == true) {
//            SharedPreferences pref = getSharedPreferences("idbundle", MODE_PRIVATE);

            User_Email = pref.getString("id", "");
            System.out.println("바인드는 죽었었다.." + User_Email);
            if (User_Email != null) {
                System.out.println("서버로 수신받는 쓰레드 시작");
//                if (!backThread.isAlive()) {
//                    backThread = new BackThread();
//                    backThread.start();
//                }

            }

            if (mThread == null) {
                mThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (serviceRunning) {
                            List<ActivityManager.RecentTaskInfo> info = mActivityManager.getRecentTasks(1, Intent.FLAG_ACTIVITY_NEW_TASK);
                            try {
                                if (info != null) {
                                    ActivityManager.RecentTaskInfo recent = info.get(0);
                                    Intent mIntent = recent.baseIntent;
                                    ComponentName name = mIntent.getComponent();


                                    if (name.equals(recentComponentName)) {
//                                        Log.d(LOG_NAME, "== pre App, recent App is same App");
                                    } else {
                                        recentComponentName = name;
//                                        Log.d(LOG_NAME, "== Application is catched: " + name);
                                    }
                                }

                            } catch (IndexOutOfBoundsException e) {
                                System.out.println("어플이 죽었는데 태스크 버튼이 눌렸습니다.");
                            }

                            SystemClock.sleep(2000);
                        }
                    }
                });

                mThread.start();
            } else if (mThread.isAlive() == false) {
                mThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (serviceRunning) {
                            List<ActivityManager.RecentTaskInfo> info = mActivityManager.getRecentTasks(1, Intent.FLAG_ACTIVITY_NEW_TASK);
                            try {
                                if (info != null) {
                                    ActivityManager.RecentTaskInfo recent = info.get(0);
                                    Intent mIntent = recent.baseIntent;
                                    ComponentName name = mIntent.getComponent();


                                    if (name.equals(recentComponentName)) {
//                                        Log.d(LOG_NAME, "== pre App, recent App is same App");
                                    } else {
                                        recentComponentName = name;
//                                        Log.d(LOG_NAME, "== Application is catched: " + name);
                                    }
                                }

                            } catch (IndexOutOfBoundsException e) {
                                System.out.println("어플이 죽었는데 태스크 버튼이 눌렸습니다.");
                            }

                            SystemClock.sleep(2000);
                        }
                    }
                });
                mThread.start();
            }

            return START_STICKY;
        } else if (loginAlive == false) {

            System.out.println("좀비서비스 종료 되었음");
        }

        return START_NOT_STICKY;
//        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        System.out.println("서비스 죽는다");
        //서비스가 돌아가고 있는지 체크
        serviceRunning = false;
        super.onDestroy();
    }

    private void NetWork() {


        try {

            socket = new Socket(ip, port);
            System.out.println("소켓확인" + socket);

            if (socket != null) {
                System.out.println("소켓연결해서이메일붙임");
                Connection(User_Email);
            }

        } catch (UnknownHostException e) {
            System.out.println("소켓 연결 실패");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("소켓 연결 실패");
            e.printStackTrace();
        }
    }

    private void Connection(String Email) //실제적인 메소드 연결 부분
    {
        this.User_Email = Email;

        try {

            //파일 전송을 위한 스트림설정


            //채팅송수신을 위한 스트림설정
            is = socket.getInputStream();
            dis = new DataInputStream(is);
            os = socket.getOutputStream();
            dos = new DataOutputStream(os);
        } catch (IOException e) //에러처리 부분
        {
            System.out.println("소켓 붙이는 부분 IOException:"+e);
            System.out.println("소켓 연결 실패");
        }    //stream 설정 끝

        // 처음 접속시에 ID 전송(이메일)
        System.out.println("서버로부터 수신하는 쓰레드");
        send_message2(Email);

        //서버로부터 계속 수신하는 쓰레드
        Boolean listen_alive = true;

         th= new Thread(new Runnable() {

            @Override
            public void run() {

                while (true) {

                    try {
                        int  readmsgLength = dis.readInt();
                        System.out.println("수신되는 메세지의 크기:"+readmsgLength);
                         byte[] recvMsgbyte = new byte[readmsgLength];
                        dis.readFully(recvMsgbyte);
                        String server_message = new String(recvMsgbyte);
//                        server_message = dis.readUTF(); // 메세지 수신


                        System.out.println("서버에서받는내용" + server_message);
                        String[] st = server_message.split("\\|\\|");

//                        st = new StringTokenizer(server_message, "||");
                        String protocol = st[0];
                        String Message = st[1];

                        System.out.println("서비스에서 받는 프로토콜 : " + protocol);
                        System.out.println("서비스에서 받는  내용 : " + Message);
                        //채팅이라는 메세지를 받을 때
                        if (protocol.equals("Chatting")) {


                            System.out.println("채팅이라는프로토콜" + server_message);
                            //sqlite에 채팅 메세지 저장하는 부분
                            MSG_Room_Id = Message;
                            Msg_Sender = st[2];
                            Msg_User_Profile = st[3];
                            Msg_Time = st[4];
                            Msg_Content = st[5];
                            Msg_ID = st[6];
                            Msg_User_Nick = st[7];
                            Msg_Read_Num = st[8];
                            Msg_protocol = st[9];

                            Boolean Is_Room;

                            Is_Room = Chat_DB.Find_Table(MSG_Room_Id);
                            System.out.println("방이있습니까?_Fromservice" + Is_Room);


                            //내가 보낸 메세지일 경우
                            if (User_Email.equals(Msg_Sender)) {
                                // 방이 있으면 true
                                if (Is_Room == true) {

                                    // 사용자가 보고 있다.
                                    System.out.println("방이 있을때 사용자가 보고 있나?" + monitorActivity.isInChatActivity());
                                    if (monitorActivity.isInChatActivity() == true) {
                                        readMsg = 1;
                                        // 채팅방 룸 리스트 업데이트
                                        Chat_DB.update_ChatRoomList(MSG_Room_Id,Msg_Content,Msg_Time);
                                        Chat_DB.insert_Message(MSG_Room_Id, Msg_Sender, Msg_User_Profile, Msg_Time, Msg_Content, Msg_ID, Msg_User_Nick, Msg_Read_Num, readMsg,Msg_protocol);

                                        PassMessageToActivity(server_message);
//
                                        // 사용자가 채팅 액티비티를 보고 있지않다.
                                    } else if (monitorActivity.isInChatActivity() == false) {
                                        readMsg = 1;
                                        Chat_DB.update_ChatRoomList(MSG_Room_Id,Msg_Content,Msg_Time);
                                        Chat_DB.insert_Message(MSG_Room_Id, Msg_Sender, Msg_User_Profile, Msg_Time, Msg_Content, Msg_ID, Msg_User_Nick, Msg_Read_Num, readMsg,Msg_protocol);
                                        PassMessageToRoomListFragment(server_message);
                                    }


                                    //방이 존재하지 않을때
                                } else if (Is_Room == false) {
                                    System.out.println("방이 없을때 사용자가 보고 있나?" + monitorActivity.isInChatActivity());
                                    if (monitorActivity.isInChatActivity() == true) {
                                        readMsg = 1;
                                        Chat_DB.Create_Chat_MSG_Table(MSG_Room_Id);
                                        Chat_DB.insert_Message(MSG_Room_Id, Msg_Sender, Msg_User_Profile, Msg_Time, Msg_Content, Msg_ID, Msg_User_Nick, Msg_Read_Num, readMsg,Msg_protocol);
                                        PassMessageToActivity(server_message);
                                        // 사용자가 채팅 액티비티를 보고 있지않다.


                                    } else if (monitorActivity.isInChatActivity() == false) {
                                        readMsg = 1;
                                        Chat_DB.Create_Chat_MSG_Table(MSG_Room_Id);
                                        Chat_DB.insert_Message(MSG_Room_Id, Msg_Sender, Msg_User_Profile, Msg_Time, Msg_Content, Msg_ID, Msg_User_Nick, Msg_Read_Num, readMsg,Msg_protocol);
                                    }


                                }

                                //내가 보낸 메세지가 아닌 경우

                            } else {
                                // 방이 있으면 true
                                if (Is_Room == true) {

                                    // 사용자가 보고 있다.
                                    System.out.println("방이 있을때 사용자가 보고 있나?" + monitorActivity.isInChatActivity());
                                    if (monitorActivity.isInChatActivity() == true) {
                                        readMsg = 1;
                                        Chat_DB.update_ChatRoomList(MSG_Room_Id,Msg_Content,Msg_Time);
                                        Chat_DB.insert_Message(MSG_Room_Id, Msg_Sender, Msg_User_Profile, Msg_Time, Msg_Content, Msg_ID, Msg_User_Nick, Msg_Read_Num, readMsg,Msg_protocol);
                                        PassMessageToActivity(server_message);
//                                    int msg_read_num = Integer.parseInt(Msg_Read_Num);
//                                    int cal = msg_read_num - 1;
//                                    String endReadNum = String.valueOf(cal);
//                                    Msg_Read_Num = endReadNum;

                                        // 사용자가 채팅 액티비티를 보고 있지않다.
                                    } else if (monitorActivity.isInChatActivity() == false) {
                                        readMsg = 0;
                                        PassMessageToRoomListFragment(server_message);
                                        Chat_DB.update_ChatRoomList(MSG_Room_Id,Msg_Content,Msg_Time);
                                        Chat_DB.insert_Message(MSG_Room_Id, Msg_Sender, Msg_User_Profile, Msg_Time, Msg_Content, Msg_ID, Msg_User_Nick, Msg_Read_Num, readMsg,Msg_protocol);
                                    }


                                } else if (Is_Room == false) {
                                    System.out.println("방이 없을때 사용자가 보고 있나?" + monitorActivity.isInChatActivity());
                                    if (monitorActivity.isInChatActivity() == true) {
                                        readMsg = 1;
                                        // 사용자가 채팅 액티비티를 보고 있지않다.
                                        Chat_DB.Create_Chat_MSG_Table(MSG_Room_Id);
                                        Chat_DB.insert_Message(MSG_Room_Id, Msg_Sender, Msg_User_Profile, Msg_Time, Msg_Content, Msg_ID, Msg_User_Nick, Msg_Read_Num, readMsg,Msg_protocol);
                                        PassMessageToActivity(server_message);
                                    } else if (monitorActivity.isInChatActivity() == false) {
                                        readMsg = 0;
                                        Chat_DB.Create_Chat_MSG_Table(MSG_Room_Id);
                                        //방이 생성되면 메세지를 업데이트 시켜주기 위해서 값을 넣어줌
                                        Chat_DB.insert_Message(MSG_Room_Id, Msg_Sender, Msg_User_Profile, Msg_Time, Msg_Content, Msg_ID, Msg_User_Nick, Msg_Read_Num, readMsg,Msg_protocol);
                                    }


                                }

                            }


                        } else if (protocol.equals("New_Room")) {

                            System.out.println("New_Room이라는 프로토콜을 받음");
                            PassMessageToRoomListFragment(server_message);
                            Chat_Room_ID = Message;
                            Chat_Room_Name = st[2];
                            Chat_User_ID = st[3];
                            Chat_User_Profile = st[4];
                            Chat_Message_Time = st[5];
                            Chat_MSG = st[6];
                            Msg_ID = st[7];
                            Msg_User_Nick = st[8];
                            Msg_Read_Num = st[9];
                            Msg_protocol = st[10];
                            System.out.println("방이 없어서 새로 만들때 사용자가 보고 있나?" + monitorActivity.isInChatActivity());
                            if (monitorActivity.isInChatActivity() == true) {
//                                int msg_read_num = Integer.parseInt(Msg_Read_Num);
//                                int cal = msg_read_num - 1;
//                                String endReadNum = String.valueOf(cal);
//                                Msg_Read_Num = endReadNum;

                                Chat_DB.insert_ChatRoomList(Chat_Room_ID, Chat_Room_ID, Chat_User_Profile, Chat_MSG, Chat_Message_Time);
                                // 사용자가 채팅 액티비티를 보고 있지않다.
                            } else if (monitorActivity.isInChatActivity() == false) {
                                Chat_DB.insert_ChatRoomList(Chat_Room_ID, Chat_Room_ID, Chat_User_Profile, Chat_MSG, Chat_Message_Time);
                            }

//                            Chat_Room_Name = st.nextToken();
//                            Chat_User_ID = st.nextToken();
//                            Chat_User_Profile = st.nextToken();
//                            Chat_Message_Time = st.nextToken();
//                            Chat_MSG = st.nextToken();
//   잠시 주석처리
//                            String result="";
//                            JSONParser jsonparser = new JSONParser();
//                            try {
//                                JSONObject jsonObject = (JSONObject)jsonparser.parse(Chat_Room_Name);
//
//                                JSONArray jsonArray = (JSONArray)jsonObject.get("result");
//                                String[] str = new String[jsonArray.size()];
//                                for(int i=0; i<jsonArray.size(); i++){
//                                    // 스트링으로 캐스팅해서 사용
//                                    result += (String)jsonArray.get(i);
//                                }
//
//                            } catch (ParseException e) {
//                                e.printStackTrace();
//                            }


                        }
                        // 메세지를 누군가 읽었을때 들어오는 프로토콜
                        else if (protocol.equals("updateMsgNum")) {
                            Chat_Room_ID = Message;
                            Msg_ID = st[2];
                            Msg_Read_Num = st[3];
                            //메세지 액티비티 를 보고 있을 때
                            if (monitorActivity.isInChatActivity() == true) {
                                System.out.println("메세지보고있을때문제점생겨버리기");
                                Chat_DB.update_chat_msg_num(Chat_Room_ID, Msg_ID, Msg_Read_Num);
                                PassMessageToActivity(server_message);
                                //메세지 액티비티 를 보고 있지 않을때
                            } else if (monitorActivity.isInChatActivity() == false) {

                                System.out.println("메세지보고있지않고쓰레드들어기전");

                                Chat_DB.update_chat_msg_num(Chat_Room_ID, Msg_ID, Msg_Read_Num);
                            }
                        }

                        // 영상채팅 프로토콜이 올때 작동하는 메서드
                        else if(protocol.equals("imageCall")){
                            ImageCallUUID =st[1];
                            System.out.println("서비스에서서버로받을때"+ImageCallUUID);
                            ImageCallSender=st[2];

                            ImageCallSenderProfile=st[3];
                            Context context = getApplicationContext();
                            Intent intent = new Intent(context, IsCalledReady.class);
                            intent.putExtra("UUID",ImageCallUUID.toString());
                            System.out.println("서비스에서보낼때"+ImageCallUUID.toString());
                            intent.putExtra("SENDER",ImageCallSender.toString());
                            intent.putExtra("PROFILE",ImageCallSenderProfile.toString());

                            // 인텐트를 한번만 사용할 수 있게 한다.
                            PendingIntent pendingIntent = PendingIntent.getActivity(context,0 ,intent,PendingIntent.FLAG_ONE_SHOT);
                            try {
                                pendingIntent.send();
                            } catch (PendingIntent.CanceledException e) {
                                e.printStackTrace();
                            }

                        }


//                        mHandler.post(new test_handle(server_message));



                    } catch (IOException e) {
                        System.out.println("IOException" + e);

                        // 서버가 닫히는 경우 리소스 정리하는 부분
                        try {
                            os.close();
                            is.close();
                            dos.close();
                            dis.close();
                            socket.close();
                            mHandler.post(new test_handle("서버와 접속 끊어짐"));

                        } catch (IOException e1) {
                            System.out.println("IOException" + e1);
                            e1.printStackTrace();
                        }

                        break;

                    }catch (Exception e3){
                        System.out.println("Exception:"+e3);
                    }

                }


            }

        });

        th.start();


    }

    class test_handle implements Runnable {

        String mText;

        public test_handle(String text) {
            this.mText = text;
        }

        @Override

        public void run() {

            Toast.makeText(getApplicationContext(), mText, Toast.LENGTH_SHORT).show();
        }


    }

    public void send_file(String path) {
        FileThread fileThread = new FileThread(path);
        fileThread.start();
    }

    class FileThread extends Thread {
        String str;

        FileThread(String str) {
            this.str = str;
        }

        @Override
        public void run() {
            File myFile = new File(str);
            byte[] mybytearray = new byte[(int) myFile.length()];
            try {
                fis = new FileInputStream(myFile);
                bis = new BufferedInputStream(fis);
                bis.read(mybytearray, 0, mybytearray.length);
                System.out.println("Sending " + str + "(" + mybytearray.length + " bytes)");
                os.write(mybytearray, 0, mybytearray.length);
                os.flush();
                System.out.println("Done.");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public void send_message2(String str) {

        BackThread2 backThread2 = new BackThread2(str);
        backThread2.start();


    }

    public void DisConnectService(){
        System.out.println("서비스디스커넥"+th.isAlive());

        if(th.isAlive()){
            th.interrupt();
        }



    }

    public void DisConnectSocket() {
        try {
            os.close();
            is.close();
            dos.close();
            dis.close();
            socket.close();
            mHandler.post(new test_handle("소켓접속 해제"));
        } catch (IOException e) {
            System.out.println("IOException" + e);
            e.printStackTrace();
        }


    }


//    public void inmessage(String str) {//서버로부터 들어오는 모든 메세지를 관리하는 부분
//
//        st = new StringTokenizer(str, "||");
//
//
//        String protocol = st.nextToken();
//        String Message = st.nextToken();
//
////        chatHistory = new ArrayList<ChatMessage>();
//
//        System.out.println("프로토콜 : " + protocol);
//        System.out.println("내용 : " + Message);
//
//        if (protocol.equals("NewUser")) // 새로운 접속자
//        {
//            user_list.add(Message);
//
//        } else if (protocol.equals("CreateRoom")) //방을 만들었을때
//        {
//            My_Room = Message;
//        } else if (protocol.equals("CreateRoomFail")) //방 만들기 실패 했을 경우
//        {
//            System.out.println("방 만들기 실패");
//        } else if (protocol.equals("New_Room")) //새로운 방을 만들었을때
//        {
//            room_list.add(Message);
//
//        } else if (protocol.equals("Chatting")) {
////            String msg = st.nextToken();
//            Chat_MSG = st.nextToken();
//            ChatMessage msg1 = new ChatMessage();
//            msg1.setId(OtherUser);
//            msg1.setisMe(false);
//            msg1.setMessage(Chat_MSG);
//            msg1.setDateTime(DateFormat.getDateTimeInstance().format(new Date()));
////            chatHistory.add(msg1);
//
//
////            displayMessage(msg1);
////            Chat_area.append(Message+":"+msg+"\n");
//        }
//
//    }

    class BackThread extends Thread {
        @Override
        public void run() {
            NetWork();
        }
    }

//    class InPutFromServer extends Thread {
//        @Override
//        public void run() {
//            inmessage(server_message);
//        }
//    }


    class BackThread2 extends Thread {
        String str;

        BackThread2(String str) {
            this.str = str;
        }

        @Override
        public void run() {
            try {
                System.out.println("트림전"+str);
                byte[] sendMsgByte = str.trim().getBytes();
                System.out.println("트림후"+str);

                int length = sendMsgByte.length;
                dos.writeInt(length);
                System.out.println("서버로 보내는 메세지 크기:"+length);
                dos.write(sendMsgByte);
//                dos.writeUTF(str);
                dos.flush();
            } catch (IOException e) {

                e.printStackTrace();
            }

        }
    }


    //메세지를 받으면 대화방 액티비티로 뿌려주는 센드브로드캐스트
    private void PassMessageToActivity(String message) {
        Intent intent = new Intent();
        intent.setAction(SENDMESAGGE);
        intent.putExtra("message", message);
        sendBroadcast(intent);
    }

    //메세지를 받으면 룸 프래그먼트로 뿌려주는 센드브로드캐스트
    private void PassMessageToRoomListFragment(String message) {
        Intent intent = new Intent();
        intent.setAction(SENDTOFRAGMENT);
        intent.putExtra("message", message);
        sendBroadcast(intent);

    }

    public void callupload(String str) {

        try {
            new uploadVideoFile(str).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


    }

    public void callupdownload(String filename,String url) {

        try {
            new Download(filename,url).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


    }


    public class uploadVideoFile extends AsyncTask<Void,Void,String> {
        String uri;
        uploadVideoFile(String Uri){
            this.uri = Uri;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {

            HttpURLConnection conn = null;
            DataOutputStream dos = null;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";

            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;
            System.out.println("파일업로드전파일경로" + uri);
            Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(uri, MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
            thumbnailuri=saveBitmaptoJpeg(bitmap);




//            Thread th = new Thread(new Runnable() {
//                @Override
//                public void run() {
//
//
//                }
//            });
//            th.start();



            File thumnailFile = new File(thumbnailuri);


            try {

                // open a URL connection to the Servlet

                URL url = new URL(upLoadServerUri);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);


//                conn.setRequestProperty("uploaded_file", uri);

                FileInputStream thminpustream = new FileInputStream(thumnailFile);
                dos = new DataOutputStream(conn.getOutputStream());
                // 썸네일 업로드

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition:form-data; name=\"uploaded_file_0"  + "\";filename=\"" + thumbnailuri+ "\"" + lineEnd);

                dos.writeBytes(lineEnd);
                bytesAvailable = thminpustream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                bytesRead = thminpustream.read(buffer, 0, bufferSize);
                while ((bytesRead > 0)) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = thminpustream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = thminpustream.read(buffer, 0, bufferSize);
                }

                dos.writeBytes(lineEnd);

                //썸네일 업로드 끝
                File sourceFile = new File(uri);
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                System.out.println("파일생성되었나" + sourceFile);


                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file_1\";filename=\""
                        + uri + "\"" + lineEnd);

                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);

                String encoded_email = URLEncoder.encode(User_Email, "UTF-8");

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition:form-data; name=\"id\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(encoded_email + lineEnd);

                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

                StringBuilder sb = new StringBuilder();
                String line = null;
                try {
                    while ((line = br.readLine()) != null) {
                        if (sb.length() > 0) {
                            sb.append("\n");
                        }
                        sb.append(line);

                        System.out.println("파일 전송 결과 : " + sb.toString());
                        httpresult = sb.toString();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(serverResponseCode == 200){

                    Log.i("FileUpload", "File Upload Complete");

//                            Toast.makeText(getActivity(), "File Upload Complete.",
//                                    Toast.LENGTH_SHORT).show();

                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();
                conn.disconnect();

            } catch (MalformedURLException ex) {


                ex.printStackTrace();


                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {


                e.printStackTrace();
                Log.e("Exception e", "Got Exception : see logcat ");

            }

            return httpresult;
        }

        @Override
        protected void onPostExecute(String s) {


            System.out.println("업로드result란"+s);
            PassMessageToActivity("upload_result||"+s);

        }
    }

    public class Download extends AsyncTask<Void,Void,Void>{

        String uri,Filename;
        Download(String Filename,String Uri){
            this.Filename=Filename;
            this.uri = Uri;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            StringBuilder html = new StringBuilder();

            try {
                URL url_ = new URL(uri);
                HttpURLConnection conn = (HttpURLConnection)url_.openConnection();

                conn.setConnectTimeout(10000);
                conn.setUseCaches(false);
                if(conn.getResponseCode()==HttpURLConnection.HTTP_OK){


                    String download = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
                    String file_name = "/"+Filename+".mp4";
                    File file = new File(download+file_name);
                    System.out.println("비비오뷰"+file);
                    FileOutputStream fileOutPut = new FileOutputStream(file);
                    InputStream inputStream = conn.getInputStream();

                    int downloadedSize = 0;
                    byte[] buffer = new byte[1024];
                    int bufferLength = 0;

                    while ((bufferLength=inputStream.read(buffer))>0){
                        fileOutPut.write(buffer,0,bufferLength);
                        downloadedSize += bufferLength;
                    }
                    fileOutPut.close();
                }
                conn.disconnect();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

         return null;
        }
    }

    public void DownloadData(String Filename,String url){
        StringBuilder html = new StringBuilder();

        try {
            URL url_ = new URL(url);
            HttpURLConnection conn = (HttpURLConnection)url_.openConnection();

            conn.setConnectTimeout(10000);
            conn.setUseCaches(false);
            if(conn.getResponseCode()==HttpURLConnection.HTTP_OK){


                String download = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
                String file_name = "/"+Filename+".mp4";
                File file = new File(download+file_name);
                System.out.println("비비오뷰"+file);
                FileOutputStream fileOutPut = new FileOutputStream(file);
                InputStream inputStream = conn.getInputStream();

                int downloadedSize = 0;
                byte[] buffer = new byte[1024];
                int bufferLength = 0;

                while ((bufferLength=inputStream.read(buffer))>0){
                    fileOutPut.write(buffer,0,bufferLength);
                    downloadedSize += bufferLength;
                }
                fileOutPut.close();
            }
            conn.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public String saveBitmaptoJpeg(Bitmap bitmap){
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyyMMddHH:mm:ss");
        String formatDate = sdfNow.format(date);
        String download = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();

        String file_name = "/"+formatDate+".png";
        String string_path = download;

        System.out.println("임시파일경로"+string_path+file_name);
        Uri_img=string_path+file_name;

        File file_path;
        try{
            file_path = new File(string_path);
            System.out.println("임시파일경로2"+file_path);
            if(!file_path.isDirectory()){
                file_path.mkdirs();
            }

            int height = bitmap.getHeight();
            int width = bitmap.getWidth();


            FileOutputStream out = new FileOutputStream(string_path+file_name);
            System.out.println("임시파일경로3"+out);
            bitmap = bitmap.createScaledBitmap(bitmap, 250, height/(width/250), true);
            if((bitmap.compress(Bitmap.CompressFormat.PNG, 100, out))){

                System.out.println("임시파일경로4완료");
            }
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();


        }catch(FileNotFoundException exception){
            Log.e("FileNotFoundException", exception.getMessage());
        }catch(IOException exception){
            Log.e("IOException", exception.getMessage());
        }

        return string_path+file_name;
    }






}
