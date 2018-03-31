package org.androidtown.new_chatting.webRTC;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import org.androidtown.new_chatting.R;

/**
 * Created by 김승훈 on 2017-08-18.
 */

public class IsCalledReady extends Activity {

    ImageButton connBtn, disconnBtn;
    TextView tvNick;
    ImageView ivProfile;
    String uuid, profile, nickname;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.image_call_activity);
        super.onCreate(savedInstanceState);
        initControls();

        Bundle new_bundle = getIntent().getExtras();


        uuid = new_bundle.getString("UUID");
        System.out.println("인텐트받을때" + uuid);
        profile = new_bundle.getString("PROFILE");
        nickname = new_bundle.getString("SENDER");

        Glide.with(IsCalledReady.this).load(profile)
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new org.androidtown.new_chatting.Other.CircleTransform(IsCalledReady.this))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivProfile);

        tvNick.setText(nickname);

        connBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IsCalledReady.this, ConnectActivity.class);
                intent.putExtra("send_uuid", uuid);
                System.out.println("인텐트보낼때" + uuid);
                uuid = null;
                startActivity(intent);

            }
        });


    }

    private void initControls() {
        connBtn = (ImageButton) findViewById(R.id.isCalledConnectButton);

        YoYo.with(Techniques.RubberBand)
                .duration(500)
                .repeat(999)
                .playOn(findViewById(R.id.isCalledImage));
        disconnBtn = (ImageButton) findViewById(R.id.isCalledDisConnectButton);
        tvNick = (TextView) findViewById(R.id.isCalledNick);
        ivProfile = (ImageView) findViewById(R.id.isCalledImage);

    }

    @Override
    protected void onPause() {
        finish();
        super.onPause();
    }
}
