package org.androidtown.new_chatting.LoginPackage;

/**
 * Created by 김승훈 on 2017-07-05.
 */
public class LoginDTO {

    private String email;
    private String nickname;
    private String name;
    private String imgurl;
    private String confirm;
    private String index;
    private boolean selected;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    private int IsChecked;


    public int getIsChecked() {
        return IsChecked;
    }

    public void setIsChecked(int isChecked) {
        IsChecked = isChecked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public String getImgurl() {

        return imgurl;
    }

    public String get_confirm(){
        return  confirm;
    }
    public void set_confirm(String confirm){
        this.confirm=confirm;
    }
    public String get_nickname(){
        return  nickname;
    }
    public void set_nickname(String nickname){
        this.nickname=nickname;
    }
    public String get_email(){
        return  email;
    }
    public void set_email(String email){
        this.email=email;
    }
}
