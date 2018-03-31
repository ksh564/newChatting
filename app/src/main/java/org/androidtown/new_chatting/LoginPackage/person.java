package org.androidtown.new_chatting.LoginPackage;

/**
 * Created by 김승훈 on 2017-07-05.
 */
public class person {
    static String name,email,index_id,id_img,nick;
    static boolean loginstate;
    static int user_sex;

    public person(){

    }
    public person(String email,String nick,String name,String id_img,boolean loginstate){
        person.nick = nick;
        person.name =name;
        person.email =email;
        person.id_img =id_img;
        person.loginstate =loginstate;


    }

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        person.name = name;
    }

    public static String getNick() {
        return nick;
    }

    public static void setNick(String nick) {
        person.nick = nick;
    }

    public static boolean isLoginstate() {
        return loginstate;
    }

    public static void setLoginstate(boolean loginstate) {
        person.loginstate = loginstate;
    }



    public static String getEmail() {
        return email;
    }

    public static String getIndex_id() {
        return index_id;
    }



    public static void setEmail(String email) {
        person.email = email;
    }

    public static void setIndex_id(String index_id) {
        person.index_id = index_id;
    }

    public static String getId_img() {
        return id_img;
    }

    public static void setId_img(String id_img) {
        person.id_img = id_img;
    }
}
