package kr.co.mamp.imagepicker;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.ImageView;

import kr.co.mamp.imagepicker.loader.ImageLoader;


public class ImageViewer extends DialogFragment {


    private Image image;
    private ImageView imageView;


    public static ImageViewer newInstance(Image image) {
        ImageViewer imageViewer = new ImageViewer();
        imageViewer.image = image;
        return imageViewer;
    }


    @SuppressWarnings("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);

        View contentView = View.inflate(getContext(), R.layout.dialog_fragment_image_viewer, null);
        dialog.setContentView(contentView);

        imageView = (ImageView) contentView.findViewById(R.id.image_view);
        setImage();
    }


    private void setImage() {
        if (image.getOrientation() == 0) {
            imageView.setImageURI(image.getOriginal());
        } else if (image.getOrientation() == 180) {
            imageView.setImageURI(image.getOriginal());
            imageView.setRotation(180);
        } else {
            Matrix matrix = new Matrix();
            matrix.setRotate(image.getOrientation());

            String realPath = new ImageLoader.ImageCursor(getContext()).getRealPathFromUri(image.getOriginal());
            Bitmap original = BitmapFactory.decodeFile(realPath);
            if (original != null) {
                Bitmap oriented = Bitmap.createBitmap(original, 0, 0, original.getWidth(), original.getHeight(), matrix, true);
                original.recycle();
                imageView.setImageBitmap(oriented);
            } else {
                imageView.setImageURI(image.getOriginal());
            }
        }
    }


    public void show(FragmentManager fragmentManager) {
        fragmentManager.beginTransaction()
                .add(this, getTag())
                .commitAllowingStateLoss();
    }


}
