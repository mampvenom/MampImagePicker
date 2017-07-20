package kr.co.mamp.imagepicker;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import kr.co.mamp.imagepicker.loader.ImageLoader;

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


    public void addToThumbnailView(Context context, ImageView thumbnailView) {
        addToImageView(context, thumbnailView, thumbnail);
    }


    public void addToOriginalView(Context context, ImageView originalView) {
        addToImageView(context, originalView, original);
    }


    private void addToImageView(Context context, ImageView imageView, Uri uri) {
        if (uri != null) {
            if (orientation == 0) {
                imageView.setImageURI(uri);
            } else if (orientation == 180) {
                imageView.setImageURI(uri);
                imageView.setRotation(180);
            } else {
                Matrix matrix = new Matrix();
                matrix.setRotate(orientation);

                String realPath = new ImageLoader.ImageCursor(context).getRealPathFromUri(uri);
                Bitmap bitmap = BitmapFactory.decodeFile(realPath);
                if (bitmap != null) {
                    Bitmap oriented = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                    bitmap.recycle();
                    imageView.setImageBitmap(oriented);
                } else {
                    imageView.setImageURI(uri);
                }
            }
        }
    }

}
