package com.andzilla.smarttune;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.TimedText;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.andzilla.smarttune.adapters.ListingSongRecyclerAdapter;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class PlayerActivity extends AppCompatActivity implements View.OnClickListener {

    private FloatingActionButton playPause;
    private AppCompatSeekBar seekBar;
    private boolean isRun = true;
    private boolean isSeeking = false;
    private MediaPlayer mediaPlayer;
    private final Runnable mUpdateProgressTask = new Runnable() {

        @Override
        public void run() {
            try {
                if (isFinishing())
                    return;
                if (!isSeeking && seekBar != null)
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                if (isRun)
                    playPause.postDelayed(this, 1000);
            } catch (Exception e) {
            }

        }
    };
    private ListingSongRecyclerAdapter adapter;
    private JSONObject currentMedia;
    private Timer timer;

    public static void launch(Context context, String type, int typeId, JSONObject mediaOb) {
        Intent intent = new Intent(context, PlayerActivity.class);
        intent.putExtra("type", type)
                .putExtra("typeId", typeId)
                .putExtra("media_object", mediaOb.toString());
        context.startActivity(intent);
        ((Activity) context).finish();
    }

    public static void launch(Context context, String Url, JSONObject mediaOb) {
        Intent intent = new Intent(context, PlayerActivity.class)
                .putExtra("Url", Url)
                .putExtra("media_object", mediaOb.toString());
        context.startActivity(intent);
    }

    public String getRequestUrl() {
        return getIntent().getStringExtra("Url");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ListingSongRecyclerAdapter(getIntent().getStringExtra("type"), getIntent().getIntExtra("typeId", 0)) {
            @Override
            public String onGetFirstUrl() {
                String url = PlayerActivity.this.getRequestUrl();
                if (url != null)
                    return url;
                return super.onGetFirstUrl();
            }

            @Override
            public void onMediaClick(Context context, String type, int typeId, JSONObject media) {
                int mediaId = media.optInt("sid", media.optInt("id"));
                setCurrentMedia(getMediaByid(mediaId).data);
            }
        };

        recyclerView.setAdapter(adapter);

        playPause = (FloatingActionButton) findViewById(R.id.play_and_pause);
        playPause.setOnClickListener(this);

        seekBar = (AppCompatSeekBar) findViewById(R.id.seek_bar);
        seekBar.setEnabled(false);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                TextView tv = (TextView) findViewById(R.id.current_duration);
                tv.setText(DateUtils.formatElapsedTime(seekBar.getProgress() / 1000));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isSeeking = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isSeeking = false;
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mediaPlayer, int what, int i1) {
                Log.d("MediaPlayer", "what " + what);
                return true;
            }
        });
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                Log.d("MediaPlayer", "onPrepared");
                seekBar.setMax(mediaPlayer.getDuration());
                TextView tv = (TextView) findViewById(R.id.total_duration);
                tv.setText(DateUtils.formatElapsedTime(mediaPlayer.getDuration() / 1000));
                seekBar.setProgress(0);
                seekBar.setEnabled(true);
                playPause.setImageResource(R.drawable.ic_pause_white_24dp);
                isRun = true;
                playPause.postDelayed(mUpdateProgressTask, 1000);
                adapter.setPlaying(true);
            }
        });
        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
                Log.d("MediaPlayer", "onBufferingUpdate " + i);
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Log.d("MediaPlayer", "onCompletion");
                isRun = false;
                seekBar.setEnabled(false);
                playPause.setImageResource(R.drawable.ic_play_arrow_white_24dp);
                next(false);
                adapter.setPlaying(false);
            }
        });
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                Log.d("MediaPlayer", "onError " + i);
                return true;
            }
        });
        mediaPlayer.setOnTimedTextListener(new MediaPlayer.OnTimedTextListener() {
            @Override
            public void onTimedText(MediaPlayer mediaPlayer, TimedText timedText) {
                Log.d("MediaPlayer", "onTimedText " + timedText.getText());
            }
        });
        JSONObject jdata = null;
        try {
            jdata = new JSONObject(getIntent().getStringExtra("media_object"));
            setCurrentMedia(jdata);
        } catch (JSONException e) {
        }
    }

    private void playPause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            playPause.setImageResource(R.drawable.ic_play_arrow_white_24dp);
        } else {
            playPause.setImageResource(R.drawable.ic_pause_white_24dp);
            mediaPlayer.start();
        }
        adapter.setPlaying(mediaPlayer.isPlaying());
    }

    private int getCurrentMediaId() {
        JSONObject obj = getCurrentMedia();
        return obj.optInt("sid", obj.optInt("id"));
    }

    private void next(boolean isReplay) {
        try {
            ListingSongRecyclerAdapter.ItemHolder tmp = adapter.getNextOf(getCurrentMediaId());
            if (tmp == null && isReplay) {
                tmp = adapter.getItem(0);
            }
            setCurrentMedia(tmp.data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void preview(boolean isReplay) {
        try {
            ListingSongRecyclerAdapter.ItemHolder tmp = adapter.getPreviewOf(getCurrentMediaId());
            if (tmp == null && isReplay) {
                int num = adapter.getItemCount() - 1;
                for (; true; num--) {
                    tmp = adapter.getItem(num);
                    if (tmp.data != null)
                        break;
                }
            }
            setCurrentMedia(tmp.data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    public JSONObject getCurrentMedia() {
        return currentMedia;
    }

    public void setCurrentMedia(JSONObject currentMedia) {
        this.currentMedia = currentMedia;
        if (this.currentMedia != null && !isFinishing()) {
            adapter.setPlaying(false);
            adapter.setOnPlayingId(getCurrentMediaId());
            seekBar.setMax(1);
            seekBar.setProgress(0);
            seekBar.setEnabled(false);
            try {
                setTitle(this.currentMedia.getString("title"));
                ImageView poster = (ImageView) findViewById(R.id.poster_image);
                Glide.with(this).load(this.currentMedia.optString("thumb"))
                        .centerCrop()
                        .transform(new CircleTransformation(this))
                        .placeholder(R.mipmap.app_icon)
                        .dontAnimate()
                        .error(R.mipmap.app_icon).into(poster);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (timer != null)
                timer.cancel();
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    new AsyncTask<Object, Object, JSONObject>() {
                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
                                }
                            });
                        }

                        @Override
                        protected JSONObject doInBackground(Object... voids) {
                            JSONObject result = null;
                            try {
                                mediaPlayer.reset();
//                                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                result = getCurrentMedia();
                                mediaPlayer.setDataSource(PlayerActivity.this, Uri.parse(result.optString("src")));
                                mediaPlayer.prepare();
                                mediaPlayer.start();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return result;
                        }

                        @Override
                        protected void onPostExecute(JSONObject result) {
                            super.onPostExecute(result);
                            findViewById(R.id.progress_bar).setVisibility(View.GONE);
                            if (result != null) {
                                markSongView(result.optInt("sid", result.optInt("id")));
                            }
                        }
                    }.execute();
                }
            }, 500);
        } else {
            adapter.setOnPlayingId(-1);
            adapter.setPlaying(false);
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.play_and_pause:
                playPause();
                break;
            case R.id.skip_to_next:
                next(true);
                break;
            case R.id.skip_to_preview:
                preview(true);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
        mediaPlayer = null;
    }

    private void markSongView(int songId) {
        String url = Server.ROOT + Server.CONTROLLER + "track";
        JSONObject body = new JSONObject();
        try {
            body.put("song_id", songId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Volley.newRequestQueue(this).add(new JsonObjectRequest(url, body, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("markSongView", "success");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("markSongView", "fail");
            }
        }));
    }

}

