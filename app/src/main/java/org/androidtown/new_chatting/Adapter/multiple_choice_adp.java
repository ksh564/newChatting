package org.androidtown.new_chatting.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.androidtown.new_chatting.LoginPackage.LoginDTO;
import org.androidtown.new_chatting.Other.CircleTransform;
import org.androidtown.new_chatting.R;

import java.util.ArrayList;

/**
 * Created by 김승훈 on 2017-08-23.
 */

public class multiple_choice_adp extends BaseAdapter {

    public ArrayList<LoginDTO> UserDto = new ArrayList<>();
   public ArrayList<NameBean> nameBean = new ArrayList<>();


    public multiple_choice_adp(ArrayList<LoginDTO> aritem){
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
    public View getView(final int position, View ConvertView, ViewGroup parent) {
        final ViewHolder holder;
        final Context context = parent.getContext();

        if(ConvertView==null){

            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ConvertView = inflater.inflate(R.layout.multi_choice_friend,parent,false);

            holder.NickName = (TextView)ConvertView.findViewById(R.id.User_NickName);
            holder.EmailView=(TextView)ConvertView.findViewById(R.id.User_Email);
            holder.StatusView=(TextView)ConvertView.findViewById(R.id.User_Stat);
            holder.ProfileView=(ImageView) ConvertView.findViewById(R.id.Profile_image);
            holder.checkFreind = (CheckBox) ConvertView.findViewById(R.id.check_friend);
            holder.checkFreind.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton view, boolean isChecked) {

//                    int getPosition = (Integer)view.getTag();
//                    UserDto.get(getPosition).setSelected(view.isChecked());
                    UserDto.get(position).setSelected(isChecked==true);

                }
            });


            ConvertView.setTag(holder);
        }else{
            holder=(ViewHolder)ConvertView.getTag();

        }

        holder.EmailView.setText(UserDto.get(position).get_email());
        holder.NickName.setText(UserDto.get(position).get_nickname());
        holder.StatusView.setText("준비중입니다.");
        holder.checkFreind.setTag(position);
//        if(nameBean.size()!=0){
//            holder.checkFreind.setChecked(nameBean.get(position).isSelected());
//        }





        // 글라이드를 사용해서 프로필 이미지를 처리하는 부분
        Glide.with(context).load(UserDto.get(position).getImgurl())
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(context))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.ProfileView);

        return ConvertView;
    }


    //뷰홀더
    public class ViewHolder{

        TextView NickName;
        TextView EmailView;
        TextView StatusView;
        ImageView ProfileView;
        CheckBox checkFreind;

    }


    public void additem(String Nick,String Email,String Profile){

        LoginDTO Item = new LoginDTO();

        Item.set_nickname(Nick);
        Item.set_email(Email);
        Item.setImgurl(Profile);


        // 유저DTO라는 어래이에 ADDITEM메소드를 통해 받은 데이터를 넣어준다.
        UserDto.add(Item);
    }

}
