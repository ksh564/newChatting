package org.androidtown.new_chatting;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

/**
 * Created by 김승훈 on 2017-08-25.
 */

public class photo_view extends AppCompatActivity {

    ImageView imageView;
    String intent_url;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_view);




        imageView = (ImageView)findViewById(R.id.photo_view);
        final Bundle Intent = getIntent().getExtras();
        intent_url = Intent.getString("url_image");


        Glide.with(this).load(intent_url)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);



    }
}
