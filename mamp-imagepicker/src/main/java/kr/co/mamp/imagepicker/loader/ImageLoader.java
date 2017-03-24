package kr.co.mamp.imagepicker.loader;


import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;

import java.util.ArrayList;
import java.util.List;

import kr.co.mamp.imagepicker.Image;

public class ImageLoader extends AsyncTaskLoader<List<Image>> {


    public ImageLoader(Context context) {
        super(context);
    }


    @Override
    public List<Image> loadInBackground() {
        return fetchAllImages();
    }


    /**
     * 이미지 불러오기.
     */
    List<Image> fetchAllImages() {
        List<Image> images = new ArrayList<>();
        String[] projection = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID, MediaStore.Images.ImageColumns.ORIENTATION};
        String orderBy = MediaStore.Images.Media.DATE_ADDED + " DESC";
        Cursor cursor =
                getContext().getContentResolver()
                        .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                projection,
                                null,
                                null,
                                orderBy);
        if (cursor != null) {
            int dataColumnIndex = cursor.getColumnIndex(projection[0]);
            int idColumnIndex = cursor.getColumnIndex(projection[1]);
            int orientationColumnIndex = cursor.getColumnIndex(projection[2]);
            if (cursor.moveToFirst()) {
                do {
                    String path = cursor.getString(dataColumnIndex);
                    String id = cursor.getString(idColumnIndex);
                    int orientation = cursor.getInt(orientationColumnIndex);

                    Image image = new Image(Uri.parse(path), fetchThumbnail(id), orientation);
                    if (image.getOriginal() != null && image.getThumbnail() != null)
                        images.add(image);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return images;
    }


    /**
     * 썸네일 불러오기.
     */
    Uri fetchThumbnail(@NonNull String imageId) {
        String[] projection = {MediaStore.Images.Thumbnails.DATA};
        ContentResolver resolver = getContext().getContentResolver();
        Cursor cursor =
                resolver
                        .query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                                projection,
                                MediaStore.Images.Thumbnails.IMAGE_ID + "=?",
                                new String[]{imageId},
                                null);
        if (cursor == null) {
            // 오류.
            return null;
        } else if (cursor.moveToFirst()) {
            // 썸네일 발견.
            int columnIndex = cursor.getColumnIndex(projection[0]);
            String path = cursor.getString(columnIndex);
            cursor.close();
            return Uri.parse(path);
        } else {
            // 썸네일 커서가 비었을 경우.
            cursor.close();
            // 썸네일 생성 요청.
            Bitmap thumbnail =
                    MediaStore.Images.Thumbnails
                            .getThumbnail(resolver,
                                    Long.parseLong(imageId),
                                    MediaStore.Images.Thumbnails.MINI_KIND,
                                    null);
            if (thumbnail != null) {
                // 생성된 썸네일 Uri 불러오기.
                return fetchThumbnail(imageId);
            } else return null;
        }
    }
}
