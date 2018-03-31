package org.androidtown.new_chatting.LoginPackage;

/**
 * Created by 김승훈 on 2017-08-02.
 */
public class Service_is_alive {
    static boolean service;

    Service_is_alive(){

    }

    public static boolean isService() {
        return service;
    }

    public static void setService(boolean service) {
        Service_is_alive.service = service;
    }
}
