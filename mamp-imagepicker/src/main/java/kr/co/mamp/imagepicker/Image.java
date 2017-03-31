package kr.co.mamp.imagepicker;


import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class Image {


    private Uri original;
    private Uri thumbnail;
    private int orientation;


    public Image(@NonNull Uri original, @Nullable Uri thumbnail, int orientation) {
        this.original = original;
        this.thumbnail = thumbnail;
        this.orientation = orientation;
    }


    public Uri getOriginal() {
        return original;
    }


    public Uri getThumbnail() {
        return thumbnail;
    }


    public int getOrientation() {
        return orientation;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof Image)) return false;
        Image image = (Image) obj;
        return image.getOriginal().getPath().equals(original.getPath());
    }


    @Override
    public String toString() {
        return "original: " + original.toString() +
                "\nthumbnail: " + thumbnail.toString() +
                "\norientation: " + orientation;
    }

}
