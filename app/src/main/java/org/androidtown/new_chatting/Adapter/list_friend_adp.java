package org.androidtown.new_chatting.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.androidtown.new_chatting.LoginPackage.LoginDTO;
import org.androidtown.new_chatting.Other.CircleTransform;
import org.androidtown.new_chatting.R;

import java.util.ArrayList;

/**
 * Created by 김승훈 on 2017-07-19.
 */
public class list_friend_adp extends BaseAdapter {

    ArrayList<LoginDTO> UserDto = new ArrayList<>();



    public list_friend_adp(ArrayList<LoginDTO> aritem){
        this.UserDto = aritem;

    }


    @Override
    public int getCount() {
        return UserDto.size();
    }

    @Override
    public Object getItem(int position) {
        return UserDto.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View ConvertView, ViewGroup parent) {

        final ViewHolder holder;
        final Context context = parent.getContext();

        if(ConvertView==null){

            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ConvertView = inflater.inflate(R.layout.new_friend_profile_layout,parent,false);

            holder.NickName = (TextView)ConvertView.findViewById(R.id.User_NickName);
            holder.StatusView=(TextView)ConvertView.findViewById(R.id.User_Stat);
            holder.ProfileView=(ImageView) ConvertView.findViewById(R.id.Profile_image);


            ConvertView.setTag(holder);
        }else{
            holder=(ViewHolder)ConvertView.getTag();
        }

        holder.NickName.setText(UserDto.get(position).get_nickname());
        holder.StatusView.setText("");

        // 글라이드를 사용해서 프로필 이미지를 처리하는 부분
        Glide.with(context).load(UserDto.get(position).getImgurl())
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(context))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.ProfileView);


        return ConvertView;
    }

    public class ViewHolder{
        TextView NickName;
        TextView StatusView;
        ImageView ProfileView;

    }


    public void additem(String Email,String NickName,String Profile){

        LoginDTO Item = new LoginDTO();

        Item.set_email(Email);
        Item.set_nickname(NickName);
        Item.setImgurl(Profile);
        // 유저DTO라는 어래이에 ADDITEM메소드를 통해 받은 데이터를 넣어준다.
        UserDto.add(Item);
    }

}
