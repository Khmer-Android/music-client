package com.sabaysongs.music;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(this, HomeActivity.class));


        ///startActivity(new Intent(this, PlayMusicActivity.class));

        finish();
    }
}
