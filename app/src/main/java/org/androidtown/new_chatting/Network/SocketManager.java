package org.androidtown.new_chatting.Network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by 김승훈 on 2017-07-22.
 */
public class SocketManager {
    public final static String HOST = "115.71.233.6";
    public final static int PORT = 9999;

    private static Socket socket;

    public static Socket getSocket() throws IOException
    {
        if( socket == null)
            socket = new Socket();

        if( !socket.isConnected() )
            socket.connect(new InetSocketAddress(HOST, PORT));

        return socket;
    }

    public static void closeSocket() throws IOException
    {
        if ( socket != null )
            socket.close();
    }

    public static void sendMsg(String msg) throws IOException
    {
        getSocket().getOutputStream().write((msg + '\n').getBytes());
    }
}
