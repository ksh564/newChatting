package org.androidtown.new_chatting.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
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
public class find_friend_adp extends BaseAdapter implements View.OnClickListener{

    ArrayList<LoginDTO> UserDto = new ArrayList<>();


    private Friend_ItemClickListener friend_itemClickListener;


    public find_friend_adp(ArrayList<LoginDTO> aritem,find_friend_adp.Friend_ItemClickListener itemClick){
        this.UserDto = aritem;
        this.friend_itemClickListener = itemClick;
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
            ConvertView = inflater.inflate(R.layout.add_profile_layout,parent,false);

            holder.NickName = (TextView)ConvertView.findViewById(R.id.User_NickName);
            holder.EmailView=(TextView)ConvertView.findViewById(R.id.User_Email);
            holder.StatusView=(TextView)ConvertView.findViewById(R.id.User_Stat);
            holder.ProfileView=(ImageView) ConvertView.findViewById(R.id.Profile_image);
            holder.Profile_Click =(FrameLayout)ConvertView.findViewById(R.id.profile_click);
            holder.AddButton = (Button)ConvertView.findViewById(R.id.add_friend);

            ConvertView.setTag(holder);
        }else{
            holder=(ViewHolder)ConvertView.getTag();

        }

        holder.EmailView.setText(UserDto.get(position).get_email());
        holder.NickName.setText(UserDto.get(position).get_nickname());
        holder.StatusView.setText("준비중입니다.");

        holder.Profile_Click.setOnClickListener(this);
        holder.Profile_Click.setTag(position);
        holder.AddButton.setOnClickListener(this);
        holder.AddButton.setTag(position);



        //만약 사용자가 검색해서 나온 다른 사용자를 친구 추가

        System.out.println("체크번호"+UserDto.get(position).getIsChecked());
        if(UserDto.get(position).getIsChecked()==0){
            holder.AddButton.setVisibility(View.VISIBLE);
        }else if(UserDto.get(position).getIsChecked()==1){
            holder.AddButton.setVisibility(View.INVISIBLE);
        }

//        holder.ProfileView.setTag(position);
        // 글라이드를 사용해서 프로필 이미지를 처리하는 부분
        Glide.with(context).load(UserDto.get(position).getImgurl())
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(context))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.ProfileView);

        return ConvertView;
    }

    @Override
    public void onClick(View view) {
        if(this.friend_itemClickListener!=null){
            this.friend_itemClickListener.onItemClick(view,(int)view.getTag());
        }

    }

    //뷰홀더
    public class ViewHolder{

        TextView NickName;
        TextView EmailView;
        TextView StatusView;
        ImageView ProfileView;
        Button AddButton;
        FrameLayout Profile_Click;

    }
    public interface Friend_ItemClickListener{

        void onItemClick(View v, int position);
    }

    public void additem(String Nick,String Email,String Profile,int IsChecked){

        LoginDTO Item = new LoginDTO();

        Item.set_nickname(Nick);
        Item.set_email(Email);
        Item.setImgurl(Profile);
        Item.setIsChecked(IsChecked);

        // 유저DTO라는 어래이에 ADDITEM메소드를 통해 받은 데이터를 넣어준다.
        UserDto.add(Item);
    }

}
