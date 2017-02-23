package com.sabaysongs.music.adapters;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.sabaysongs.music.BaseViewHolder;
import com.sabaysongs.music.PlayerActivity;
import com.sabaysongs.music.R;
import com.sabaysongs.music.Server;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rattanak on 8/31/2016.
 */
public class ListingSongRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> implements Response.Listener<JSONObject>, Response.ErrorListener {

    private Context context;
    private String requestUrl;
    private int id;
    private String type;

    private int onPlayingId = -1;
    private List<ItemHolder> itemHolderList = new ArrayList<>();
    private boolean isloading = false;
    private boolean isPlaying = false;

    public ListingSongRecyclerAdapter(String type, int id) {
        this.type = type;
        this.id = id;
        requestUrl = onGetFirstUrl();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String onGetFirstUrl() {
        return Server.ROOT + Server.CONTROLLER + type + "/" + getId();
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public void addItem(ItemHolder item) {
        itemHolderList.add(item);
        try {
            notifyItemChanged(itemHolderList.indexOf(item));
        } catch (Exception e) {
        }
    }

    public ItemHolder getItem(int position) {
        try {
            return itemHolderList.get(position);
        } catch (Exception e) {
        }
        return null;
    }

    public void clear() {
        itemHolderList.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<ItemHolder> list) {
        itemHolderList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).viewTypeId;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case -1:
                View view = new ProgressBar(getContext());
                StaggeredGridLayoutManager.LayoutParams params = new StaggeredGridLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setFullSpan(true);
                view.setLayoutParams(params);
                return new LoadingViewHolder(view);
            case -2:
                view = LayoutInflater.from(getContext()).inflate(R.layout.album_load_error_layout, parent, false);
                params = new StaggeredGridLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setFullSpan(true);
                view.setLayoutParams(params);
                return new ErrorMessageViewHolder(view);
            default:
                view = LayoutInflater.from(getContext()).inflate(R.layout.song_item_layout, parent, false);
                return new ItemViewHolder(view);
        }
    }

    public ItemHolder getMediaByid(int mediaId) {
        return getMedia(mediaId, 0);
    }

    private ItemHolder getMedia(int mediaId, int offset) {
        ItemHolder result = null;
        try {
            result = itemHolderList.get(itemHolderList.indexOf(new ItemHolder(mediaId)) + offset);
        } catch (Exception e) {
        }
        return result;
    }

    public ItemHolder getNextOf(int mediaId) {
        return getMedia(mediaId, 1);
    }

    public ItemHolder getPreviewOf(int mediaId) {
        return getMedia(mediaId, -1);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.onBindView(position, getItem(position).data);
    }

    @Override
    public int getItemCount() {
        return itemHolderList.size();
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
        notifyDataSetChanged();
    }

    public void loadData() {
        if (getRequestUrl() != null && !isloading) {
            isloading = true;
            itemHolderList.remove(new ItemHolder(-2));
            itemHolderList.remove(new ItemHolder(-1));
            itemHolderList.add(new ItemHolder(-1));
            try {
                notifyDataSetChanged();
            } catch (Exception e) {
            }
            Volley.newRequestQueue(getContext()).add(new JsonObjectRequest(getRequestUrl(), this, this));
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public void onResponse(JSONObject response) {
        try {
            List<ItemHolder> tmp = new ArrayList<>();
            JSONArray data = response.getJSONArray("data");
            ItemHolder tmpItem;
            for (int i = 0; i < data.length(); i++) {
                tmpItem = new ItemHolder(data.getJSONObject(i), 0);
                if (!itemHolderList.contains(tmpItem))
                    tmp.add(tmpItem);
            }
            addAll(tmp);
            itemHolderList.remove(new ItemHolder(-1));
            if (!response.isNull("next_page_url")) {
                setRequestUrl(response.getString("next_page_url"));
                itemHolderList.add(new ItemHolder(-1));
            } else {
                setRequestUrl(null);
            }
        } catch (Exception E) {
        }
        isloading = false;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        JSONObject msg = new JSONObject();
        try {
            msg.put("message", "Error while trying to loading albums");
        } catch (Exception e) {
        }
        itemHolderList.remove(new ItemHolder(-1));
        addItem(new ItemHolder(msg, -2));
        isloading = false;
    }

    public int getOnPlayingId() {
        return onPlayingId;
    }

    public void setOnPlayingId(int onPlayingId) {
        this.onPlayingId = onPlayingId;
        notifyDataSetChanged();
    }

    public void onMediaClick(Context context, String type, int typeId, JSONObject mediaOb) {
        PlayerActivity.launch(context, type, typeId, mediaOb);
    }

    public String getItemName(JSONObject jsonObject) {
        return jsonObject.optString("title", "Unknown");
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        if (recyclerView == null)
            return;
        setContext(recyclerView.getContext());
        loadData();

    }

    public static class ItemHolder {
        public   static  JSONObject data;
        int viewTypeId = -2;

        public ItemHolder(int viewTypeId) {
            this.viewTypeId = viewTypeId;
        }

        public ItemHolder(JSONObject data, int viewTypeId) {
            this.data = data;
            if (data != null && viewTypeId == 0) {
                this.viewTypeId = data.optInt("sid", data.optInt("id"));
            }
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof ItemHolder) {
                return ((ItemHolder) obj).viewTypeId == viewTypeId;
            }
            return super.equals(obj);
        }

    }

    private class ItemViewHolder extends BaseViewHolder {
        private ImageView thumbnail;
        private int id;
        private String url;
        private String name;
        private TextView title, artist, view_number, download_number, duration;
        private ImageView play_eq;

        private JSONObject jsonObject;

        public ItemViewHolder(View itemView) {
            super(itemView);
            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            title = (TextView) itemView.findViewById(R.id.title);
            artist = (TextView) itemView.findViewById(R.id.artist);
            play_eq = (ImageView) itemView.findViewById(R.id.play_eq);
            view_number = (TextView) itemView.findViewById(R.id.view_number);
            download_number = (TextView) itemView.findViewById(R.id.download_number);
            duration = (TextView) itemView.findViewById(R.id.duration);
        }

        @Override
        public void onBindView(int position, JSONObject jsonObject) {
            try {
                this.jsonObject = jsonObject;
                id = jsonObject.optInt("sid", jsonObject.optInt("id"));
                url = jsonObject.optString("thumb");
                artist.setText(jsonObject.optString("author"));
                Glide.with(itemView.getContext()).load(url).centerCrop().placeholder(R.mipmap.app_icon)
                        .dontAnimate()
                        .error(R.mipmap.app_icon).into(thumbnail);
                name = getItemName(jsonObject);
                title.setText(name);
                view_number.setText(jsonObject.optString("views"));
                download_number.setText(jsonObject.optString("downloaded"));
                duration.setText(jsonObject.optString("duration", "0:00"));
                if (id == onPlayingId) {
                    AnimationDrawable animation = (AnimationDrawable) ContextCompat.getDrawable(getContext(), R.drawable.ic_equalizer_white_36dp);
                    play_eq.setImageDrawable(animation);
                    play_eq.setVisibility(View.VISIBLE);
                    if (isPlaying())
                        animation.start();
                    else
                        animation.stop();
                } else {
                    play_eq.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onClick(View view) {
            onMediaClick(view.getContext(), getType(), getId(), jsonObject);
        }
    }

    private class LoadingViewHolder extends BaseViewHolder {
        public LoadingViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void onBindView(int position, JSONObject jsonObject) {
            loadData();
        }

        @Override
        public void onClick(View view) {

        }
    }

    private class ErrorMessageViewHolder extends BaseViewHolder {
        private TextView msgTextView;

        public ErrorMessageViewHolder(View itemView) {
            super(itemView);
            msgTextView = (TextView) itemView.findViewById(R.id.msg_text_view);
        }

        @Override
        public void onBindView(int position, JSONObject jsonObject) {
            try {
                msgTextView.setText(jsonObject.getString("message"));
            } catch (Exception e) {
            }
        }

        @Override
        public void onClick(View view) {
            loadData();
        }
    }
}
