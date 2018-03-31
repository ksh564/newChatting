package org.androidtown.new_chatting.Adapter;


import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.androidtown.new_chatting.R;

import java.util.List;

/**
 * Created by 김승훈 on 2017-08-17.
 */

public class Chat_Msg_Adp extends BaseAdapter implements View.OnClickListener {
    private final List<ChatMessage> chatMessages;
    private Activity context;

    private Chat_ItemClickListener chat_itemClickListener;

    public Chat_Msg_Adp(Activity context, List<ChatMessage> chatMessages,Chat_Msg_Adp.Chat_ItemClickListener itemClick) {

        this.chat_itemClickListener = itemClick;
        this.context = context;
        this.chatMessages = chatMessages;
    }


    //메세지의 갯수를 알려주는 메소드
    @Override
    public int getCount() {
        if (chatMessages != null) {
            return chatMessages.size();
        } else {
            return 0;
        }

    }


    //채팅 메세지의 position을 관리해주는 메소드
    @Override
    public ChatMessage getItem(int position) {
        if (chatMessages != null) {
            return chatMessages.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        ChatMessage chatMessage = getItem(position);
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);




        if (convertView == null) {
            convertView = inflater.inflate(R.layout.chatting_message, null);
            holder = createViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        boolean myMsg = chatMessage.getisMe() ;//Just a dummy check
        String protocol = chatMessage.getProtocol();
        String mesg = chatMessage.getMessage();

        System.out.println(getClass().getName()+":에서 온 로그"+" "+"프로토콜은"+protocol+"안에들어가있는메세지"+mesg);
        // 글라이드를 사용해서 프로필 이미지를 처리하는 부분
        Glide.with(context).load(chatMessage.getChat_Profile_Photo())
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new org.androidtown.new_chatting.Other.CircleTransform(context))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.profileImg);
        if(protocol.equals("text")){
            holder.txtMessage.setText(chatMessage.getMessage());
        }else if(protocol.equals("video")){
            String[] st = chatMessage.getMessage().split("\\*\\*");


            String thumnail = st[0];
            System.out.println(getClass().getName()+":에서 온 로그"+" "+"st[0]"+thumnail);
            final String video_url = st[1];
            System.out.println(getClass().getName()+":에서 온 로그"+" "+"st[1]"+video_url);
        //    Request.
            Glide.with(context).load(st[0])
                    .crossFade()
                    .thumbnail(0.5f)
                    .override(700,300)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.videoView);

            // 프로토콜타입이 video일때 버튼 클릭리스너를 달아준다.
            holder.gotovideo.setOnClickListener(this);
            holder.gotovideo.setTag(position);


        }

        holder.txtInfo.setText(chatMessage.getDateTime());
        holder.txtReadNum.setText(chatMessage.getReadNum());
        holder.txtReadnum_ME.setText(chatMessage.getReadNum());
        holder.txtNick.setText(chatMessage.getUserNick());


        //to simulate whether it me or other sender
        setAlignment(holder,myMsg,protocol);


        return convertView;

    }

    //메세지 왼쪽 오른쪽으로 나누는 메소드드
    private void setAlignment(ViewHolder holder, boolean isMe, String protocol){

        if(protocol.equals("text")){

            holder.videoView.setVisibility(View.GONE);
            holder.gotovideo.setVisibility(View.GONE);
            holder.contentWithBG.setVisibility(View.VISIBLE);

            if(isMe){
                holder.contentWithBG.setBackgroundResource(R.drawable.in_message_bg);

                //나에게 보내는 메세지의 배경그림을 layoutparam으로 받고 그다음 gravity를 오른쪽에 줌줌
                LinearLayout.LayoutParams layoutParams =
                        (LinearLayout.LayoutParams) holder.contentWithBG.getLayoutParams();
                layoutParams.gravity = Gravity.RIGHT;
                holder.contentWithBG.setLayoutParams(layoutParams);

                // 나한테 보낸 메세지일때 감춰지는 요소들
                holder.profileImg.setVisibility(View.GONE);
                holder.txtNick.setVisibility(View.GONE);
                holder.txtReadNum.setVisibility(View.GONE);
                holder.txtReadnum_ME.setVisibility(View.VISIBLE);

                //감추기 끝
                RelativeLayout.LayoutParams lp =
                        (RelativeLayout.LayoutParams) holder.fullcontent.getLayoutParams();
                lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
                lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                holder.fullcontent.setLayoutParams(lp);
                layoutParams = (LinearLayout.LayoutParams) holder.txtMessage.getLayoutParams();
                layoutParams.gravity = Gravity.RIGHT;
                holder.txtMessage.setLayoutParams(layoutParams);

                layoutParams = (LinearLayout.LayoutParams) holder.txtInfo.getLayoutParams();
                layoutParams.gravity = Gravity.RIGHT;
                holder.txtInfo.setLayoutParams(layoutParams);
            }else if(!isMe){

                //내가 보내는 메세지가 아닌 남이 보낸 메세지의 layout Param
                holder.contentWithBG.setBackgroundResource(R.drawable.out_message_bg);

                // 대화상자
                LinearLayout.LayoutParams layoutParams =
                        (LinearLayout.LayoutParams) holder.contentWithBG.getLayoutParams();
                layoutParams.gravity = Gravity.LEFT;
                holder.contentWithBG.setLayoutParams(layoutParams);

                holder.txtReadnum_ME.setVisibility(View.GONE);
                holder.profileImg.setVisibility(View.VISIBLE);
                holder.txtNick.setVisibility(View.VISIBLE);
                holder.txtReadNum.setVisibility(View.VISIBLE);

                RelativeLayout.LayoutParams lp =
                        (RelativeLayout.LayoutParams) holder.fullcontent.getLayoutParams();
                lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
                lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                holder.fullcontent.setLayoutParams(lp);

                layoutParams = (LinearLayout.LayoutParams) holder.txtMessage.getLayoutParams();
                layoutParams.gravity = Gravity.LEFT;
                holder.txtMessage.setLayoutParams(layoutParams);

                layoutParams = (LinearLayout.LayoutParams) holder.txtInfo.getLayoutParams();
                layoutParams.gravity = Gravity.LEFT;
                holder.txtInfo.setLayoutParams(layoutParams);

            }

        }
        // 만약 메세지가 video일때 설정
        else if(protocol.equals("video")){

            holder.videoView.setVisibility(View.VISIBLE);
            holder.gotovideo.setVisibility(View.VISIBLE);
            holder.contentWithBG.setVisibility(View.GONE);
            if(isMe){
//                holder.contentWithBG.setVisibility(View.GONE);
//                holder.contentWithBG.setBackgroundResource(R.drawable.in_message_bg);

                //나에게 보내는 메세지의 배경그림을 layoutparam으로 받고 그다음 gravity를 오른쪽에 줌줌
//                LinearLayout.LayoutParams layoutParams =
//                        (LinearLayout.LayoutParams) holder.contentWithBG.getLayoutParams();
//                layoutParams.gravity = Gravity.RIGHT;
//                holder.contentWithBG.setLayoutParams(layoutParams);

                // 나한테 보낸 메세지일때 감춰지는 요소들
                holder.profileImg.setVisibility(View.GONE);
                holder.txtNick.setVisibility(View.GONE);
                holder.txtReadNum.setVisibility(View.GONE);
                holder.txtReadnum_ME.setVisibility(View.VISIBLE);

                //감추기 끝
                RelativeLayout.LayoutParams lp =
                        (RelativeLayout.LayoutParams) holder.fullcontent.getLayoutParams();
                lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
                lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                holder.fullcontent.setLayoutParams(lp);
//                layoutParams = (LinearLayout.LayoutParams) holder.txtMessage.getLayoutParams();
//                layoutParams.gravity = Gravity.RIGHT;
//                holder.txtMessage.setLayoutParams(layoutParams);
//
//                layoutParams = (LinearLayout.LayoutParams) holder.txtInfo.getLayoutParams();
//                layoutParams.gravity = Gravity.RIGHT;
//                holder.txtInfo.setLayoutParams(layoutParams);
            }else if(!isMe){

                //내가 보내는 메세지가 아닌 남이 보낸 메세지의 layout Param
//                holder.contentWithBG.setBackgroundResource(R.drawable.out_message_bg);
//
//                // 대화상자
//                LinearLayout.LayoutParams layoutParams =
//                        (LinearLayout.LayoutParams) holder.contentWithBG.getLayoutParams();
//                layoutParams.gravity = Gravity.LEFT;
//                holder.contentWithBG.setLayoutParams(layoutParams);

                holder.txtReadnum_ME.setVisibility(View.GONE);
                holder.profileImg.setVisibility(View.VISIBLE);
                holder.txtNick.setVisibility(View.VISIBLE);
                holder.txtReadNum.setVisibility(View.VISIBLE);

                RelativeLayout.LayoutParams lp =
                        (RelativeLayout.LayoutParams) holder.fullcontent.getLayoutParams();
                lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
                lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                holder.fullcontent.setLayoutParams(lp);

//                layoutParams = (LinearLayout.LayoutParams) holder.txtMessage.getLayoutParams();
//                layoutParams.gravity = Gravity.LEFT;
//                holder.txtMessage.setLayoutParams(layoutParams);
//
//                layoutParams = (LinearLayout.LayoutParams) holder.txtInfo.getLayoutParams();
//                layoutParams.gravity = Gravity.LEFT;
//                holder.txtInfo.setLayoutParams(layoutParams);

            }

        }



    }

    //메세지 더하는 부분
    public void add(ChatMessage message) {
        chatMessages.add(message);
    }

    public void add(List<ChatMessage> messages) {
        chatMessages.addAll(messages);
    }


    private ViewHolder createViewHolder(View v) {
        ViewHolder holder = new ViewHolder();

        holder.videoView = (ImageView)v.findViewById(R.id.video_view);
        holder.gotovideo = (ImageButton) v.findViewById(R.id.videoProgress);
        holder.fullcontent =(LinearLayout)v.findViewById(R.id.full_content);
        holder.profileImg = (ImageView)v.findViewById(R.id.uesrProfile);
        holder.contentImg = (FrameLayout)v.findViewById(R.id.contentImg);
        holder.txtMessage = (TextView)v.findViewById(R.id.txtMessage);
        holder.content = (LinearLayout)v.findViewById(R.id.content);
        holder.contentWithBG =(LinearLayout)v.findViewById(R.id.contentWithBackground);
        holder.txtNick =(TextView)v.findViewById(R.id.txtNick);
        holder.txtReadNum=(TextView)v.findViewById(R.id.readNum);
        holder.txtReadnum_ME =(TextView)v.findViewById(R.id.readNum_me);
        //메세지 날짜 부분
        holder.txtInfo = (TextView)v.findViewById(R.id.txtInfo);
        return holder;
    }


    // 아이템이 클릭이 되었으면 태그를 찾아서 그 아이템을 선택해준다.
    @Override
    public void onClick(View view) {
        if(this.chat_itemClickListener!=null){
            this.chat_itemClickListener.onItemClick(view,(int)view.getTag());
        }

    }


    //뷰홀더에 들어갈 아이템 클래스
    private static class ViewHolder {

        public FrameLayout contentImg;
        public ImageView profileImg;
        public TextView txtNick;
        public TextView txtReadNum;
        public TextView txtMessage;
        public TextView txtReadnum_ME;
        //메세지의 시간을 담아주는 텍스트뷰
        public TextView txtInfo;
        public LinearLayout fullcontent;
        public LinearLayout content;
        public LinearLayout contentWithBG;
        public ImageView videoView;
        public ImageButton gotovideo;

    }

    public interface Chat_ItemClickListener{

        void onItemClick(View v, int position);
    }


}
