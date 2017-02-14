package com.andzilla.smarttune.model;

import android.os.Parcel;
import android.os.Parcelable;

public class PlayList implements Parcelable {
    public static final String TAG = FavoriteSongs.class.getSimpleName();

    public static final String PARCELABLE_KEY = TAG + ":" + "ParcelableKey";
    public static final Parcelable.Creator<PlayList> CREATOR = new Parcelable.Creator<PlayList>() {
        @Override
        public PlayList createFromParcel(Parcel inputParcel) {
            return new PlayList(inputParcel);
        }

        @Override
        public PlayList[] newArray(int size) {
            return new PlayList[size];
        }
    };

    private Long _id;

    private String name;

    private int number;

    private int number_songs;

    private boolean Initialized;

    public PlayList() {
    }

    public PlayList(String name,int number,int number_songs) {
        this.name = name;
        this.number = number;
        this.number_songs = number_songs;
    }



    private PlayList(Parcel inputParcel) {
        _id = inputParcel.readLong();
        if (_id < 0) {
            _id = null;
        }

        name = inputParcel.readString();
        number= inputParcel.readInt();
        number_songs = inputParcel.readInt();


        Initialized = inputParcel.readByte() != 0;
    }

    public Long getId() {
        return _id;
    }


    public Long get_id() {
        return _id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isInitialized() {
        return Initialized;
    }

    public void setInitialized(boolean initialized) {
        Initialized = initialized;
    }


    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getNumber_songs() {
        return number_songs;
    }

    public void setNumber_songs(int number_songs) {
        this.number_songs = number_songs;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel outputParcel, int flags) {
        if (_id != null) {
            outputParcel.writeLong(_id);
        } else {
            outputParcel.writeLong(-1);
        }

        outputParcel.writeString(name);
        outputParcel.writeInt(number);
        outputParcel.writeInt(number_songs);

        outputParcel.writeByte((byte) (Initialized ? 1 : 0));
    }
}
