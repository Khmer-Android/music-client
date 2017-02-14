package com.andzilla.smarttune.fragments;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.VolleyError;
import com.andzilla.smarttune.R;
import com.andzilla.smarttune.adapters.SongsRecyclerAdapter;

public class SongsFragment extends Fragment {

    private static SongsFragment INSTANCE;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SongsRecyclerAdapter adapter;
    private TextView msgTextView;

    public static SongsFragment newInstance() {
        return new SongsFragment();
    }

    public static SongsFragment getInstance() {
        if (INSTANCE == null)
            INSTANCE = newInstance();
        return INSTANCE;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_albums, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        msgTextView = (TextView) view.findViewById(R.id.msg_text_view);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SongsRecyclerAdapter() {
            @Override
            public void onLoading(boolean isLoading) {
                swipeRefreshLayout.setRefreshing(isLoading);
                msgTextView.setVisibility(View.GONE);
            }

            @Override
            public boolean isEnableTopSongsList() {
                return true;
            }

            @Override
            public void onError(VolleyError error) {
                if (error instanceof NoConnectionError) {
                    if (getItemCount() < 1) {
                        msgTextView.setText("No Internet connection!");
                        msgTextView.setVisibility(View.VISIBLE);
                    } else {
                        msgTextView.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "No Internet connection!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.reLoadData();
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.recycler_view);
        recyclerView.getAdapter().notifyDataSetChanged();
    }
}
