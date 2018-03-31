package org.androidtown.new_chatting.Adapter;

/**
 * Created by 김승훈 on 2017-07-06.
 */
public class Room_DTO  {

    private String User;
    private String User_Profile;
    private int User_Number;
    private String User_Chat;
    private String User_Room_Name;
    private String Message_Time;
    private String Room_Unique_name;
    private String Msg_read_Num;
    private int Msg_Numer;

    public int getMsg_Numer() {
        return Msg_Numer;
    }

    public void setMsg_Numer(int msg_Numer) {
        Msg_Numer = msg_Numer;
    }

    public String getMsg_read_Num() {
        return Msg_read_Num;
    }

    public void setMsg_read_Num(String msg_read_Num) {
        Msg_read_Num = msg_read_Num;
    }

    public String getRoom_Unique_name() {
        return Room_Unique_name;
    }

    public void setRoom_Unique_name(String room_Unique_name) {
        Room_Unique_name = room_Unique_name;
    }

    public String getMessage_Time() {
        return Message_Time;
    }

    public void setMessage_Time(String message_Time) {
        Message_Time = message_Time;
    }

    public String getUser() {
        return User;
    }

    public void setUser(String user) {
        User = user;
    }

    public String getUser_Profile() {
        return User_Profile;
    }

    public void setUser_Profile(String user_Profile) {
        User_Profile = user_Profile;
    }

    public int getUser_Number() {
        return User_Number;
    }

    public void setUser_Number(int user_Number) {
        User_Number = user_Number;
    }

    public String getUser_Chat() {
        return User_Chat;
    }

    public void setUser_Chat(String user_Chat) {
        User_Chat = user_Chat;
    }

    public String getUser_Room_Name() {
        return User_Room_Name;
    }

    public void setUser_Room_Name(String user_Room_Name) {
        User_Room_Name = user_Room_Name;
    }
}
