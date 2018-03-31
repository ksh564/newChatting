package org.androidtown.new_chatting.Adapter;

/**
 * Created by 김승훈 on 2017-07-07.
 */
public class ChatMessage {

    private String id;
    private boolean isMe;
    private String message;
    private Long userId;
    private String dateTime;
    private String Chat_Profile_Photo;
    private String messageID;
    private String readNum;
    private String userNick;
    private String protocol;
    private int readMessage;

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public int getReadMessage() {
        return readMessage;
    }

    public void setReadMessage(int readMessage) {
        this.readMessage = readMessage;
    }

    public String getUserNick() {
        return userNick;
    }

    public void setUserNick(String userNick) {
        this.userNick = userNick;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getReadNum() {
        return readNum;
    }

    public void setReadNum(String readNum) {
        this.readNum = readNum;
    }

    public String getChat_Profile_Photo() {
        return Chat_Profile_Photo;
    }

    public void setChat_Profile_Photo(String chat_Profile_Photo) {
        Chat_Profile_Photo = chat_Profile_Photo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean getisMe() {
        return isMe;
    }

    public void setisMe(boolean isMe) {
        this.isMe = isMe;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }


}
