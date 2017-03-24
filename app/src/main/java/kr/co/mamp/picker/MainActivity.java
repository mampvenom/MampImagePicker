package kr.co.mamp.picker;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

import kr.co.mamp.imagepicker.Image;
import kr.co.mamp.imagepicker.MampImagePicker;
import kr.co.mamp.imagepicker.adapter.ImageAdapter;

public class MainActivity extends AppCompatActivity {


    private ImageAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = (RecyclerView) findViewById(kr.co.mamp.imagepicker.R.id.recycler_view);
        GridLayoutManager manager = new GridLayoutManager(this, 4);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        adapter = new ImageAdapter(null);
        recyclerView.setAdapter(adapter);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                        && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                    } else {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                    }
                    return;
                }
                createImagePicker();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        createImagePicker();
    }


    void createImagePicker() {
        new MampImagePicker.Builder(getApplicationContext())
//                .setSinglePickCallback(new MampImagePicker.SinglePickCallback() {
//                    @Override
//                    public void onSinglePick(Image image) {
//                        List<Image> images = new ArrayList<Image>();
//                        images.add(image);
//                        adapter.setImages(images);
//                        adapter.notifyDataSetChanged();
//                    }
//                })
                .setMultiPickCallback(new MampImagePicker.MultiPickCallback() {
                    @Override
                    public void onMultiPick(List<Image> images) {
                        adapter.setImages(images);
                        adapter.notifyDataSetChanged();
                    }
                })
                .setCheckedImages(adapter.getImages())
                .create().show(getSupportFragmentManager());
    }
}
