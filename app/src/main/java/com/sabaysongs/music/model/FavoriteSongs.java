package com.sabaysongs.music.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by phuon on 17-Jan-17.
 */
public class FavoriteSongs implements Parcelable {
    public static final String TAG = FavoriteSongs.class.getSimpleName();

    public static final String PARCELABLE_KEY = TAG + ":" + "ParcelableKey";
    public static final Parcelable.Creator<FavoriteSongs> CREATOR = new Parcelable.Creator<FavoriteSongs>() {
        @Override
        public FavoriteSongs createFromParcel(Parcel inputParcel) {
            return new FavoriteSongs(inputParcel);
        }

        @Override
        public FavoriteSongs[] newArray(int size) {
            return new FavoriteSongs[size];
        }
    };

    private Long _id;

    private String title;
    private  long author_id;
    private String author;
    private String album;
    private String src;
    private String thumb;
    private int views;
    private int  downloaded;



    private boolean Initialized;

    public FavoriteSongs() {
    }

    private FavoriteSongs(Parcel inputParcel) {
        _id = inputParcel.readLong();
        if (_id < 0) {
            _id = null;
        }

        title = inputParcel.readString();
        author_id = inputParcel.readLong();
        author = inputParcel.readString();
        album = inputParcel.readString();
        src   = inputParcel.readString();
        thumb = inputParcel.readString();
        views = inputParcel.readInt();
        downloaded = inputParcel.readInt();


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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getAuthor_id() {
        return author_id;
    }

    public void setAuthor_id(long author_id) {
        this.author_id = author_id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public int getDownloaded() {
        return downloaded;
    }

    public void setDownloaded(int downloaded) {
        this.downloaded = downloaded;
    }

    public boolean isInitialized() {
        return Initialized;
    }

    public void setInitialized(boolean initialized) {
        Initialized = initialized;
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



        outputParcel.writeString(title);
        outputParcel.writeLong(author_id);
        outputParcel.writeString(author);
        outputParcel.writeString(album);
        outputParcel.writeString(src);
        outputParcel.writeString(thumb);
        outputParcel.writeInt(views);
        outputParcel.writeInt(downloaded);

        outputParcel.writeByte((byte) (Initialized ? 1 : 0));
    }
}
