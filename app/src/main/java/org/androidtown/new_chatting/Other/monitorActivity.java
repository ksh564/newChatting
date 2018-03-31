package org.androidtown.new_chatting.Other;

/**
 * Created by 김승훈 on 2017-08-07.
 */
public class monitorActivity {

    static boolean inChatActivity;


    public static boolean isInChatActivity() {
        return inChatActivity;
    }

    public static void setInChatActivity(boolean inChatActivity) {
        monitorActivity.inChatActivity = inChatActivity;
    }
}



