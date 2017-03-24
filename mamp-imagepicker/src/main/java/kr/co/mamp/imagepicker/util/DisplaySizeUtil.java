package kr.co.mamp.imagepicker.util;


import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.WindowManager;

public class DisplaySizeUtil {


    private Point size;


    private static class SingleTon {
        private static final DisplaySizeUtil instance = new DisplaySizeUtil();
    }


    private DisplaySizeUtil() {
    }


    public static DisplaySizeUtil getInstance() {
        return SingleTon.instance;
    }


    public Point getSize(Context context) {
        return size == null ? newSize(context) : size;
    }


    public Point newSize(Context context) {
        size = new Point();
        WindowManager windowManager =
                (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            display.getSize(size);
        } else {
            size.set(display.getWidth(), display.getHeight());
        }
        return size;
    }
}
