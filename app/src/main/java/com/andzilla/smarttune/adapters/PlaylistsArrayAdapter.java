package com.andzilla.smarttune.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.andzilla.smarttune.R;
import com.andzilla.smarttune.model.PlayList;

public class PlaylistsArrayAdapter extends ArrayAdapter<PlayList>  implements View.OnClickListener {



	public PlaylistsArrayAdapter(Context context, ArrayList<PlayList> playlist) {
		super(context, 0, playlist);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// Get the data item for this position
		PlayList playListModel = getItem(position);
		// Check if an existing view is being reused, otherwise inflate the view
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.playlist_item, parent, false);
		}
		// Lookup view for data population
		TextView number = (TextView) convertView.findViewById(R.id.number);
		CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.playlist_checkBox);
		checkBox.setText(playListModel.getName());
		number.setText(playListModel.getNumber()+"");
		checkBox.setOnClickListener(this);




		// Return the completed view to render on screen
		return convertView;
	}

	@Override
	public void onClick(View view) {


		switch (view.getId()){
			case R.id.playlist_checkBox:


				boolean checked = ((CheckBox) view).isChecked();

				Log.i("is","value"+checked);


				break;
		}



	}
}
