package org.androidtown.new_chatting.Other;

import org.androidtown.new_chatting.Adapter.list_friend_adp;
import org.androidtown.new_chatting.Adapter.multiple_choice_adp;
import org.androidtown.new_chatting.Adapter.room_List_Adp;

/**
 * Created by 김승훈 on 2017-08-23.
 */

public class static_adp {

    static list_friend_adp list_friend_adp;
    static multiple_choice_adp multiple_choice_adp;
    static room_List_Adp room_list_adp;

    public static room_List_Adp getRoom_list_adp() {
        return room_list_adp;
    }

    public static void setRoom_list_adp(room_List_Adp room_list_adp) {
        static_adp.room_list_adp = room_list_adp;
    }

    public static org.androidtown.new_chatting.Adapter.multiple_choice_adp getMultiple_choice_adp() {
        return multiple_choice_adp;
    }

    public static void setMultiple_choice_adp(org.androidtown.new_chatting.Adapter.multiple_choice_adp multiple_choice_adp) {
        static_adp.multiple_choice_adp = multiple_choice_adp;
    }

    public static_adp(){

    }

    public static org.androidtown.new_chatting.Adapter.list_friend_adp getList_friend_adp() {
        return list_friend_adp;
    }

    public static void setList_friend_adp(org.androidtown.new_chatting.Adapter.list_friend_adp list_friend_adp) {
        static_adp.list_friend_adp = list_friend_adp;
    }
}
