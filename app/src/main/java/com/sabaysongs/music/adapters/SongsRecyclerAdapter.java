package com.sabaysongs.music.adapters;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.sabaysongs.music.PlayerActivity;
import com.sabaysongs.music.R;
import com.sabaysongs.music.Server;
import com.sabaysongs.music.database.dao.PlayListDAO;
import com.sabaysongs.music.model.PlayList;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Rattanak on 8/31/2016.
 */
public class SongsRecyclerAdapter extends RecyclerView.Adapter<SongsRecyclerAdapter.BaseViewHolder> {

    private String TAG = SongsRecyclerAdapter.class.getSimpleName();
    private Context context;
    private String requestUrl;
    private List<ItemHolder> itemHolderList = new ArrayList<>();
    private boolean isloading = false;
    private PlayListDAO playListDAO;


    public SongsRecyclerAdapter() {
        requestUrl = onGetFirstUrl();

    }

    public String onGetFirstUrl() {
        return Server.ROOT + Server.CONTROLLER + getType();
    }

    public String getType() {
        return "songs";
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
        if (isEnableTopSongsList())
            position = position - 1;
        return itemHolderList.get(position);
    }

    public void clear() {
        itemHolderList.clear();
        notifyDataSetChanged();
    }

    public boolean isEnableTopSongsList() {
        return true;
    }

    @Override
    public int getItemViewType(int position) {
        if (isEnableTopSongsList() && position == 0)
            return 0;
        return 1;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0:
                View view = LayoutInflater.from(getContext()).inflate(R.layout.listing_top_songs_layout, parent, false);
                StaggeredGridLayoutManager.LayoutParams params = new StaggeredGridLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setFullSpan(true);
                view.setLayoutParams(params);
                return new TopSongsViewHolder(view);
            default:
                view = LayoutInflater.from(getContext()).inflate(R.layout.song_item_layout, parent, false);
                return new ItemViewHolder(view);
        }
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
        return itemHolderList.size() + (isEnableTopSongsList() ? 1 : 0);
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
        private TextView title, artist, view_number, download_number, duration;
        private JSONObject jsonObject;
        private TextView more_items;
        private TextView add_playlist;
        public ItemViewHolder(View itemView) {
            super(itemView);
            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            title = (TextView) itemView.findViewById(R.id.title);
            artist = (TextView) itemView.findViewById(R.id.artist);
            view_number = (TextView) itemView.findViewById(R.id.view_number);
            download_number = (TextView) itemView.findViewById(R.id.download_number);
            duration = (TextView) itemView.findViewById(R.id.duration);
            add_playlist = (TextView)itemView.findViewById(R.id.add_playlist);
            more_items = (TextView) itemView.findViewById(R.id.more_menu);
            more_items.setOnClickListener(this);
            add_playlist.setOnClickListener(this);
        }

        @Override
        public void onBindView(int position, JSONObject jsonObject) {
            try {
                this.jsonObject = jsonObject;
                id = jsonObject.getInt("id");
                Glide.with(itemView.getContext()).load(jsonObject.getString("thumb")).centerCrop().placeholder(R.mipmap.app_icon)
                        .error(R.mipmap.app_icon).into(thumbnail);
                title.setText(jsonObject.optString("title", "Unknown"));
                artist.setText(jsonObject.optString("author", "Unknown"));
                view_number.setText(jsonObject.optString("views"));
                download_number.setText(jsonObject.optString("downloaded"));
                duration.setText(jsonObject.optString("duration", "0:00"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onClick(View view) {



             switch (view.getId()){
                 case R.id.more_menu:
                     PopupMenu popup = new PopupMenu(context, view);
                     Menu m = popup.getMenu();
                     MenuInflater inflater = popup.getMenuInflater();
                     inflater.inflate(R.menu.more_menus, popup.getMenu());

                     popup.show();
                     break;
                 case R.id.add_playlist:





                     LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);



                     AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                     alertDialog.setIcon(context.getResources().getDrawable(R.drawable.playlist_icon_18dp ));

                     View convertView = (View) li.inflate(R.layout.playlist_layout, null);
                     alertDialog.setView(convertView);
                     alertDialog.setTitle("List");
                     ListView lv = (ListView) convertView.findViewById(R.id.listView1);

                     playListDAO = PlayListDAO.getInstance(context);
                     playListDAO.createTest();
                      ArrayList<PlayList> playListModels = playListDAO.lists();

                     PlaylistsArrayAdapter adapter1 = new PlaylistsArrayAdapter(context,playListModels);

                     lv.setAdapter(adapter1);

                     alertDialog.show();







                     break;

                 default:
                     PlayerActivity.launch(itemView.getContext(), onGetFirstUrl(), this.jsonObject);
             }

        }
    }

    private class TopSongsViewHolder extends BaseViewHolder {

        private RecyclerView top_songs_recycler;

        public TopSongsViewHolder(View itemView) {
            super(itemView);
            top_songs_recycler = (RecyclerView) itemView.findViewById(R.id.top_songs_recycler);
            top_songs_recycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            top_songs_recycler.setAdapter(new TopSongsRecyclerAdapter(getContext()));
        }

        @Override
        public void onBindView(int position, JSONObject jsonObject) {
        }

        @Override
        public void onClick(View view) {

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
