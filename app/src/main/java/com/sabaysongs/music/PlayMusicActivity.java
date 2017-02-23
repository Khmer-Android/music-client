package com.sabaysongs.music;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;




public class PlayMusicActivity extends AppCompatActivity implements
        View.OnClickListener,
        ServiceConnection
{


    private Button pause,stop,play;

    // indicates whether the activity is linked to service player.
    private boolean mIsBound = false;

    // Saves the binding instance with the service.
    private MusicService mServ;

    private static final String TAG = PlayMusicActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_play_music);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        stop = (Button) findViewById(R.id.stop);
        play = (Button) findViewById(R.id.play);
        pause = (Button) findViewById(R.id.pause);

        stop.setOnClickListener(this);
        play.setOnClickListener(this);
        pause.setOnClickListener(this);


        // Starting the service of the player, if not already started.
        Intent music = new Intent(this, MusicService.class);
        startService(music);

        doBindService();




      /*  FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }





    // when closing the current activity, the service will automatically shut down(disconnected).
    @Override
    public void onDestroy()
    {
        super.onDestroy();

        doUnbindService();
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.stop:

                Log.i(TAG,"Stop music");
                mServ.stop();
                break;
            case R.id.play:
                 Log.i(TAG,"play music");
                  mServ.start();
                break;


            case R.id.pause:
                mServ.pause();
                Log.i(TAG,"pause music");

                break;
        }
    }

    @Override
    // interface connection with the service activity
    public void onServiceConnected(ComponentName name, IBinder binder)
    {
        mServ = ((MusicService.ServiceBinder) binder).getService();
    }
    @Override
    public void onServiceDisconnected(ComponentName name)
    {
        mServ = null;
    }









    public void doBindService()
    {
        // activity connects to the service.
        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, this, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    public void doUnbindService()
    {
        // disconnects the service activity.
        if(mIsBound)
        {
            unbindService(this);
            mIsBound = false;
        }
    }

}
