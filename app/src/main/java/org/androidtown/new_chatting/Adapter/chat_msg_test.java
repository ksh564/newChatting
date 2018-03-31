package org.androidtown.new_chatting.Adapter;


import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.androidtown.new_chatting.Other.CircleTransform;
import org.androidtown.new_chatting.R;

import java.util.List;

/**
 * Created by 김승훈 on 2017-07-06.
 */
public class chat_msg_test extends BaseAdapter {
    private final List<ChatMessage> chatMessages;
    private Activity context;

    public chat_msg_test(Activity context, List<ChatMessage> chatMessages) {

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
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.chatting_message, null);
            holder = createViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        boolean myMsg = chatMessage.getisMe();//Just a dummy check
        //to simulate whether it me or other sender
        setAlignment(holder, myMsg);
        // 글라이드를 사용해서 프로필 이미지를 처리하는 부분
        Glide.with(context).load(chatMessage.getChat_Profile_Photo())
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(context))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.profileImg);
        holder.txtMessage.setText(chatMessage.getMessage());
        holder.txtInfo.setText(chatMessage.getDateTime());

        return convertView;

    }

    //메세지 왼쪽 오른쪽으로 나누는 메소드드
    private void setAlignment(ViewHolder holder, boolean isMe) {

        if (isMe) {
            holder.contentWithBG.setBackgroundResource(R.drawable.in_message_bg);

            holder.profileImg.setVisibility(View.GONE);
            //나에게 보내는 메세지의 배경그림을 layoutparam으로 받고 그다음 gravity를 오른쪽에 줌줌
            LinearLayout.LayoutParams layoutParams =
                    (LinearLayout.LayoutParams) holder.contentWithBG.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            holder.contentWithBG.setLayoutParams(layoutParams);

            RelativeLayout.LayoutParams lp =
                    (RelativeLayout.LayoutParams) holder.content.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            holder.content.setLayoutParams(lp);
            layoutParams = (LinearLayout.LayoutParams) holder.txtMessage.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            holder.txtMessage.setLayoutParams(layoutParams);

            layoutParams = (LinearLayout.LayoutParams) holder.txtInfo.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            holder.txtInfo.setLayoutParams(layoutParams);
        } else if (!isMe) {

            //내가 보내는 메세지가 아닌 남이 보낸 메세지의 layout Param
            holder.contentWithBG.setBackgroundResource(R.drawable.out_message_bg);

            // 대화상자
            LinearLayout.LayoutParams layoutParams =
                    (LinearLayout.LayoutParams) holder.contentWithBG.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            holder.contentWithBG.setLayoutParams(layoutParams);


            RelativeLayout.LayoutParams lp =
                    (RelativeLayout.LayoutParams) holder.content.getLayoutParams();

            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

            holder.content.setLayoutParams(lp);


            layoutParams = (LinearLayout.LayoutParams) holder.txtMessage.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            holder.txtMessage.setLayoutParams(layoutParams);

            layoutParams = (LinearLayout.LayoutParams) holder.txtInfo.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            holder.txtInfo.setLayoutParams(layoutParams);

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

        holder.profileImg = (ImageView) v.findViewById(R.id.uesrProfile);
        holder.contentImg = (FrameLayout) v.findViewById(R.id.contentImg);
        holder.txtMessage = (TextView) v.findViewById(R.id.txtMessage);
        holder.content = (LinearLayout) v.findViewById(R.id.content);
        holder.contentWithBG = (LinearLayout) v.findViewById(R.id.contentWithBackground);
        //메세지 날짜 부분
        holder.txtInfo = (TextView) v.findViewById(R.id.txtInfo);
        return holder;
    }


    //뷰홀더에 들어갈 아이템 클래스
    private static class ViewHolder {

        public FrameLayout contentImg;
        public ImageView profileImg;
        public TextView txtMessage;
        //메세지의 시간을 담아주는 텍스트뷰
        public TextView txtInfo;
        public LinearLayout content;
        public LinearLayout contentWithBG;
    }


}

