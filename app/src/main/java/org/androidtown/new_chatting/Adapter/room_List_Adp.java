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

import org.androidtown.new_chatting.Other.CircleTransform;
import org.androidtown.new_chatting.R;

import java.util.ArrayList;

/**
 * Created by 김승훈 on 2017-07-06.
 */
public class room_List_Adp extends BaseAdapter {


    ArrayList<Room_DTO> Room_item = new ArrayList<Room_DTO>();

    public room_List_Adp(ArrayList<Room_DTO>RoomItem){
        this.Room_item=RoomItem;
    }

    //룸 리스트 에 대한 뷰 홀더

    public class ViewHolder{

        TextView RoomName;
        TextView Chat;
        TextView Chat_Time;
        ImageView Profile_Img;
        TextView msg_num;

    }

    @Override
    public int getCount() {
        return Room_item.size();
    }

    @Override
    public Object getItem(int position) {
        return Room_item.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View ConvertView, ViewGroup Parent) {
        final  ViewHolder holder;
        final Context context = Parent.getContext();

        if(ConvertView==null){
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ConvertView = inflater.inflate(R.layout.room_list_item,Parent,false);

            holder.RoomName=(TextView)ConvertView.findViewById(R.id.Room_Name);
            holder.Chat=(TextView)ConvertView.findViewById(R.id.Chat);
            holder.Chat_Time=(TextView)ConvertView.findViewById(R.id.list_message_time) ;
            holder.Profile_Img=(ImageView)ConvertView.findViewById(R.id.Profile_image);
            holder.msg_num =(TextView)ConvertView.findViewById(R.id.list_msg_num);

            ConvertView.setTag(holder);

        }else{
            holder = (ViewHolder)ConvertView.getTag();
        }
        holder.Chat.setText(Room_item.get(position).getUser_Chat());
        holder.RoomName.setText(Room_item.get(position).getUser_Room_Name());
        // 글라이드를 사용해서 프로필 이미지를 처리하는 부분
        Glide.with(context).load(Room_item.get(position).getUser_Profile())
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(context))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.Profile_Img);
        holder.Chat_Time.setText(Room_item.get(position).getMessage_Time());
        holder.msg_num.setText(String.valueOf(Room_item.get(position).getMsg_Numer()));

        return ConvertView;
    }

    public void addItem(String Room_Unique_Name,String Room_Name,String Profile,String message,String message_time,int message_read_num){

        Room_DTO room_item = new Room_DTO();
        room_item.setRoom_Unique_name(Room_Unique_Name);
        room_item.setUser_Room_Name(Room_Name);
        room_item.setUser_Profile(Profile);
        room_item.setMessage_Time(message_time);
        room_item.setUser_Chat(message);
//        room_item.setMsg_read_Num(message_read_num);
        room_item.setMsg_Numer(message_read_num);

        Room_item.add(room_item);
    }


}
