package com.andzilla.smarttune.adapters;

import org.json.JSONObject;

/**
 * Created by Rattanak on 8/31/2016.
 */
public class ArtistsRecyclerAdapter extends AlbumsRecyclerAdapter {

    @Override
    public String getType() {
        return "artists";
    }

    @Override
    public String onGetImageUrl(JSONObject jsonObject) {
        return jsonObject.optString("thumb");
    }

    public String getItemName(JSONObject jsonObject) {
        return jsonObject.optString("author");
    }
}
