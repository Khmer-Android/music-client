package com.andzilla.smarttune.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.andzilla.smarttune.PlayerActivity;
import com.andzilla.smarttune.R;
import com.andzilla.smarttune.Server;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by RINRattanak on 12/20/2016.
 */

public class TopSongsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private String requestUrl = Server.ROOT + Server.CONTROLLER + "topten";
    private boolean isloading;

    private JSONArray list;

    public TopSongsRecyclerAdapter(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.top_song_item_layout, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        SongViewHolder h = (SongViewHolder) holder;
        JSONObject object = list.optJSONObject(position);
        String url = object.optString("thumb");
        String title = object.optString("title");
        h.title.setText(title);
        Glide.with(getContext()).load(url).centerCrop().placeholder(R.mipmap.app_icon)
                .dontAnimate()
//                        .transform(new CircleTransformation(itemView.getContext()))
                .error(R.mipmap.app_icon).into(h.thumbnail);

    }

    @Override
    public int getItemCount() {
        return (list != null) ? list.length() : 0;
    }

    public void loadData() {
        if (getRequestUrl() == null || isloading)
            return;
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(getRequestUrl(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                isloading = false;
                list = response.optJSONArray("data");
                if (!response.isNull("next_page_url")) {
                    requestUrl = response.optString("next_page_url");
                } else {
                    requestUrl = null;
                }
                notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                isloading = false;
            }
        });
        Volley.newRequestQueue(getContext()).add(jsObjRequest);
    }

    public void onNotify() {
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        if (getItemCount() < 1)
            loadData();
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    private class SongViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView title;
        private ImageView thumbnail;

        public SongViewHolder(View itemView) {
            super(itemView);
            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            title = (TextView) itemView.findViewById(R.id.title);
            this.itemView.setOnClickListener(this);


        }

        @Override
        public void onClick(View view) {
           // Toast.makeText(context,"work",Toast.LENGTH_SHORT).show();
           PlayerActivity.launch(getContext(), Server.ROOT + Server.CONTROLLER + "topten", list.optJSONObject(getAdapterPosition()));
        }
    }
}
