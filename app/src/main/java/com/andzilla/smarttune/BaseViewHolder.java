package com.andzilla.smarttune;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.json.JSONObject;

/**
 * Created by Rattanak on 9/1/2016.
 */
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