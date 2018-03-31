package org.androidtown.new_chatting.SQ_lite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.androidtown.new_chatting.Adapter.ChatMessage;
import org.androidtown.new_chatting.Adapter.Room_DTO;

import java.util.ArrayList;


/**
 * Created by 김승훈 on 2017-07-27.
 */
public class DBHelper extends SQLiteOpenHelper {

    ArrayList<String> List;

    // DBHelper 생성자로 관리할 DB 이름과 버전 정보를 받음

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE ROOMLIST (_id INTEGER PRIMARY KEY AUTOINCREMENT, Room_Id TEXT, Room_User_list TEXT, User_Profile TEXT,Recent_Message TEXT,Message_Time TEXT);");
    }





    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    //메세지 테이블 생성

    public void Create_Chat_MSG_Table(String Room_ID) {


        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("CREATE TABLE"+" "+Room_ID+" "+"(_id INTEGER PRIMARY KEY AUTOINCREMENT, MSG_SENDER TEXT, MSG_USER_PROFILE TEXT, MSG_TIME TEXT,MSG TEXT,MSG_ID TEXT,MSG_USER_NICK TEXT,MSG_READ_NUM TEXT,READ_MSG INTEGER,MSG_PROTOCOL TEXT);");
        System.out.println("메세지테이블 생성완료 방번호:"+Room_ID);
    }

    public void update_chat_msg(String Room_ID, String Msg_ID) {
        SQLiteDatabase db = getWritableDatabase();
        int price=1;
        // 입력한 항목과 일치하는 행의 가격 정보 수정
        db.execSQL("UPDATE"+" "+Room_ID.trim()+" "+"SET READ_MSG=" + price + " WHERE MSG_ID='" + Msg_ID + "';");
        db.close();
    }
    public void update_chat_msg_num(String Room_ID, String Msg_ID,String Msg_num) {
        SQLiteDatabase db = getWritableDatabase();

        // 입력한 항목과 일치하는 행의 가격 정보 수정
        db.execSQL("UPDATE"+" "+Room_ID.trim()+" "+"SET MSG_READ_NUM='" + Msg_num + "' WHERE MSG_ID='" + Msg_ID + "';");
        db.close();
    }


    public boolean Find_Table (String Room_ID) {

        SQLiteDatabase db =getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name ='"+Room_ID.trim()+"'" , null);
        System.out.println("파인드테이블쿼리"+"SELECT name FROM sqlite_master WHERE type='table' AND name ='"+Room_ID.trim()+"'");
        System.out.println("파인드테이블"+cursor);

        cursor.moveToFirst();

        if(cursor.getCount()>0){
            //존재한다
            return true;
        }else{

            //존재하지 않는다.
            return false;
        }

//        db.execSQL("select count(*) from sqlite_master Where Name ="+"'"+Room_ID+"';");
    }

    // 메세지 저장하는 sqlite

    public void insert_Message(String RoomId,String Msg_Sender,String User_Profile ,String Message_Time,String Message,String Msg_id,String Msg_User_Nick,String Msg_Read_Num,int Read_MSG,String Msg_Protocol){

        SQLiteDatabase db = getWritableDatabase();
        // DB에 입력한 값으로 행 추가
        db.execSQL("INSERT INTO"+" "+ RoomId+" "+ "VALUES(null, '" + Msg_Sender + "', '" + User_Profile + "', '" + Message_Time + "', '" + Message + "', '" + Msg_id + "', '" + Msg_User_Nick + "', '" + Msg_Read_Num + "', '" + Read_MSG + "', '" + Msg_Protocol + "');");
        System.out.println("INSERT INTO"+" "+ RoomId+" "+ "VALUES(null, '" + Msg_Sender + "', '" + User_Profile + "', '" + Message_Time + "', '" + Message + "', '" + Msg_id + "', '" + Msg_User_Nick + "', '" + Msg_Read_Num + "', '" + Read_MSG + "', '" + Msg_Protocol + "');");
        db.close();

    }


    //채팅방 리스트 데이터 삽입
    public void insert_ChatRoomList(String Room_Id , String Room_User_list, String User_Profile, String Recent_Message, String Message_Time) {
        // 읽고 쓰기가 가능하게 DB 열기
        SQLiteDatabase db = getWritableDatabase();
        // DB에 입력한 값으로 행 추가
        db.execSQL("INSERT INTO ROOMLIST VALUES(null, '" + Room_Id + "', '" + Room_User_list + "', '" + User_Profile + "', '" + Recent_Message + "', '" + Message_Time + "');");
        db.close();
    }

    public void update_ChatRoomList(String Room_id,String msg,String time) {
        SQLiteDatabase db = getWritableDatabase();
        // 입력한 항목과 일치하는 행의 가격 정보 수정

        db.execSQL("UPDATE ROOMLIST SET Recent_Message='"+msg+"', Message_Time='"+time+"' WHERE Room_Id='" + Room_id + "';");
        db.close();
    }

    public void delete(String item) {
        SQLiteDatabase db = getWritableDatabase();
        // 입력한 항목과 일치하는 행 삭제
        db.execSQL("DELETE FROM ROOMLIST WHERE item='" + item + "';");
        db.close();
    }

    public ArrayList<ChatMessage> getResult_ChatMsg(String Room_ID){



        ArrayList<ChatMessage> chat_dtos = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM"+" "+ Room_ID+"", null);
        while (cursor.moveToNext()){
            ChatMessage chatMessage = new ChatMessage();
            //채팅메세지라는 어레이리스트에 있는 아이디 가져오는 커서값
            chatMessage.setId(cursor.getString(1));
            //채팅메세지라는 어레이리스트에 있는 프로필사진 가져오는 커서값
            chatMessage.setChat_Profile_Photo(cursor.getString(2));
            //채팅메세지라는 어레이리스트에 있는 시간 가져오는 커서값
            chatMessage.setDateTime(cursor.getString(3));
            //채팅메세지라는 어레이리스트에 있는 메세지 가져오는 커서값
            chatMessage.setMessage(cursor.getString(4));
            //채팅메세지라는 어레이리스트에 있는 메세지아이디(어떤 메세지인지 구별하기 위한) 가져오는 커서값
            chatMessage.setMessageID(cursor.getString(5));
            //채팅메세지라는 어레이리스트에 있는 닉네임(이름) 가져오는 커서값
            chatMessage.setUserNick(cursor.getString(6));
            //채팅메세지라는 어레이리스트에 있는 읽은 숫자 가져오는 커서값
            chatMessage.setReadNum(cursor.getString(7));
            //채팅메세지라는 어레이리스트에 있는 읽었는지 안 읽었는지 체크하는 값을 가져오는 커서값
            chatMessage.setReadMessage(cursor.getInt(8));
            //채팅메세지라는 어레이리스트에 있는 프로토콜 가져오는 커서값
            chatMessage.setProtocol(cursor.getString(9));

            chat_dtos.add(chatMessage);
        }

        return chat_dtos;
    }

    public ArrayList<Room_DTO> getResult_ChatRoomList() {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        String result = "";

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        Cursor cursor = db.rawQuery("SELECT * FROM ROOMLIST", null);

        ArrayList<Room_DTO> room_dtos = new ArrayList<>();


        while (cursor.moveToNext()) {

            Room_DTO room_dto = new Room_DTO();

            room_dto.setRoom_Unique_name(cursor.getString(1));

            room_dto.setUser_Room_Name(cursor.getString(2));

            room_dto.setUser_Profile(cursor.getString(3));

            room_dto.setUser_Chat(cursor.getString(4));

            room_dto.setMessage_Time(cursor.getString(5));


            room_dtos.add(room_dto);

        }

        return room_dtos;
    }
}
