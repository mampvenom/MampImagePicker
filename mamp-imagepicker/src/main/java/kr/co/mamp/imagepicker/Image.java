package kr.co.mamp.imagepicker;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;

public class Image {


    private Uri original;
    private Uri thumbnail;
    private int orientation;
//    private boolean checked;


    public Image(@NonNull Uri original, @Nullable Uri thumbnail, int orientation) {
        this.original = original;
        this.thumbnail = thumbnail;
        this.orientation = orientation;
    }


    public Image(@NonNull File original, @Nullable File thumbnail, int orientation) {
        this(Uri.fromFile(original), thumbnail == null ? null : Uri.fromFile(thumbnail), orientation);
    }


    public Image(@NonNull String original, @Nullable String thumbnail, int orientation) {
        this(new File(original), thumbnail == null ? null : new File(thumbnail), orientation);
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


//    public void setChecked(boolean checked) {
//        this.checked = checked;
//    }
//
//
//    public boolean isChecked() {
//        return checked;
//    }


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

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        String result;
        Cursor cursor = context.getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            result = contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(index);
            cursor.close();
        }
        return result;
    }
}
