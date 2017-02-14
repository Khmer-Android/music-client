package com.andzilla.smarttune.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.andzilla.smarttune.ListingAlbumsActivity;
import com.andzilla.smarttune.R;
import com.andzilla.smarttune.Server;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rattanak on 8/31/2016.
 */
public class AlbumsRecyclerAdapter extends RecyclerView.Adapter<AlbumsRecyclerAdapter.BaseViewHolder> {

    private Context context;
    private String requestUrl;
    private List<ItemHolder> itemHolderList = new ArrayList<>();
    private boolean isloading = false;

    public AlbumsRecyclerAdapter() {
        requestUrl = onGetFirstUrl();
    }

    public String onGetFirstUrl() {
        return Server.ROOT + Server.CONTROLLER + getType();
    }

    public String getType() {
        return "albums";
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

    public void addAll(List<ItemHolder> list) {
        itemHolderList.addAll(list);
        notifyDataSetChanged();
    }

    public ItemHolder getItem(int position) {
        return itemHolderList.get(position);
    }

    public void clear() {
        itemHolderList.clear();
        notifyDataSetChanged();
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        switch (viewType) {
//            case 0:
//                View view = LayoutInflater.from(getContext()).inflate(R.layout.listing_top_songs_layout, parent, false);
//                StaggeredGridLayoutManager.LayoutParams params = new StaggeredGridLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                params.setFullSpan(true);
//                view.setLayoutParams(params);
//                return new TopSongsViewHolder(view);
////            case 1:
////                view = LayoutInflater.from(getContext()).inflate(R.layout.album_load_error_layout, parent, false);
////                StaggeredGridLayoutManager.LayoutParams params = new StaggeredGridLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
////                params.setFullSpan(true);
////                view.setLayoutParams(params);
////                return new ErrorMessageViewHolder(view);
////            default:
////                view = new ProgressBar(getContext());
////                params = new StaggeredGridLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
////                params.setFullSpan(true);
////                view.setLayoutParams(params);
////                return new LoadingViewHolder(view);
//            default:
//
//        }
        View view = LayoutInflater.from(getContext()).inflate(R.layout.album_item_layout, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder)
            holder.onBindView(position, getItem(position).data);
        if (position > getItemCount() - 5) {
            loadData();
        }
    }

    @Override
    public int getItemCount() {
        return itemHolderList.size();
    }

    public void reLoadData() {
        requestUrl = onGetFirstUrl();
        loadData();
    }

    public void loadData() {
        if (getRequestUrl() != null && !isloading) {
            onLoading(true);
            isloading = true;
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(getRequestUrl(), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (onGetFirstUrl().equals(getRequestUrl()))
                            clear();
                        List<ItemHolder> tmp = new ArrayList<>();
                        JSONArray data = response.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            tmp.add(new ItemHolder(data.getJSONObject(i), 0));
                        }
                        addAll(tmp);
                        if (!response.isNull("next_page_url")) {
                            setRequestUrl(response.getString("next_page_url"));
                        } else {
                            setRequestUrl(null);
                        }
                    } catch (Exception E) {
                    }
                    isloading = false;
                    onLoading(false);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    isloading = false;
                    onLoading(false);
                    onError(error);
                }
            });
            Volley.newRequestQueue(getContext()).add(jsObjRequest);
        } else {
            onLoading(false);
        }
    }

    public void onLoading(boolean isloading) {

    }

    public String getItemName(JSONObject jsonObject) {
        return jsonObject.optString("name", "Unknown");
    }

    public String onGetImageUrl(JSONObject jsonObject) {
        return jsonObject.optString("url");
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        if (recyclerView == null)
            return;
        setContext(recyclerView.getContext());
        if (getItemCount() < 1)
            loadData();
    }

    public void onError(VolleyError error) {
    }

    public abstract class BaseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public BaseViewHolder(View itemView) {
            super(itemView);
            onSetOnClickListener();
        }

        public void onSetOnClickListener() {
            this.itemView.setOnClickListener(this);
        }

        public abstract void onBindView(int position, JSONObject jsonObject);
    }

    private class ItemViewHolder extends BaseViewHolder {
        private ImageView thumbnail;
        private int id;
        private String url;
        private String name;
        private TextView title;
        private TextView numSong;

        public ItemViewHolder(View itemView) {
            super(itemView);
            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            title = (TextView) itemView.findViewById(R.id.title);
            numSong = (TextView) itemView.findViewById(R.id.num_song);
        }

        @Override
        public void onBindView(int position, JSONObject jsonObject) {
            try {
                id = jsonObject.getInt("id");
                url = onGetImageUrl(jsonObject);
                Display display = ((Activity) itemView.getContext()).getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                ViewGroup.LayoutParams params = thumbnail.getLayoutParams();
                params.height = size.x / 2;
                params.width = params.height;
                thumbnail.setLayoutParams(params);
                Glide.with(itemView.getContext()).load(url).centerCrop().placeholder(R.mipmap.app_icon)
                        .dontAnimate()
//                        .transform(new CircleTransformation(itemView.getContext()))
                        .error(R.mipmap.app_icon).into(thumbnail);
                name = getItemName(jsonObject);
                params = title.getLayoutParams();
                params.width = (size.x / 2) - 16;
                title.setLayoutParams(params);
                title.setText(name);
                numSong.setText(jsonObject.optString("total_songs", "0"));
            } catch (Exception e) {
            }
        }

        @Override
        public void onClick(View view) {
            ListingAlbumsActivity.launch(itemView.getContext(), getType(), name, id, url);
        }
    }

    private class ItemHolder {
        JSONObject data;
        int viewTypeId;

        public ItemHolder(int viewTypeId) {
            this.viewTypeId = viewTypeId;
        }

        public ItemHolder(JSONObject data, int viewTypeId) {
            this.data = data;
            this.viewTypeId = viewTypeId;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof ItemHolder) {
                return ((ItemHolder) obj).viewTypeId == viewTypeId;
            }
            return super.equals(obj);
        }
    }
}
