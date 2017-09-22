package kr.co.mamp.imagepicker;


import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import kr.co.mamp.imagepicker.adapter.ImageAdapter;
import kr.co.mamp.imagepicker.loader.ImageLoader;
import kr.co.mamp.imagepicker.util.DisplaySizeUtil;

public class MampImagePicker extends BottomSheetDialogFragment
        implements LoaderManager.LoaderCallbacks<List<Image>> {


    /**
     * 다이얼로그 커스텀 뷰.
     */
    private View contentView;
    /**
     * 하단 시트 동작.
     */
    private BottomSheetBehavior bottomSheetBehavior;
    /**
     * 프로그레스.
     */
    private View progress;
    /**
     * 리사이클러뷰.
     */
    private RecyclerView recyclerView;
    /**
     * 이미지 어댑터.
     */
    private ImageAdapter adapter;
    /**
     * 다이얼로그 상태 콜백.
     */
    private BottomSheetBehavior.BottomSheetCallback bottomSheetCallback
            = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            // 하단으로 숨겨지면 다이얼그 닫기.
            if (newState == BottomSheetBehavior.STATE_HIDDEN) dismissAllowingStateLoss();

        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };
    /**
     * 다이얼로그 빌더.
     */
    private Builder builder;
    private static final int REQ_CAMERA = 1734;
    /**
     * 카메라 저장 파일 경로.
     */
    private String cameraFilePath;


    /**
     * 다이얼로그 커스텀 뷰 꾸미기.
     */
    @SuppressWarnings("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        // 커스텀 뷰.
        contentView =
                View.inflate(getContext(), R.layout.dialog_fragment_image_picker, null);
        dialog.setContentView(contentView);

        // 하단 콜백 설정.
        CoordinatorLayout.LayoutParams layoutParams =
                (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        layoutParams.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        CoordinatorLayout.Behavior behavior = layoutParams.getBehavior();
        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            bottomSheetBehavior = (BottomSheetBehavior) behavior;
            bottomSheetBehavior.setBottomSheetCallback(bottomSheetCallback);
            if (builder.peekHeight > 0) { // 기본 높이 설정.
                bottomSheetBehavior.setPeekHeight(builder.peekHeight);
            } else {
                bottomSheetBehavior
                        .setPeekHeight(
                                (int) (DisplaySizeUtil.getInstance().newSize(getContext()).y * 0.7f));
            }
        }

        // 뷰 초기화.
        initView();
        // 이미지 불러오기.
        loadImages();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // 기본 높이 재설정.
        if (builder.peekHeight <= 0) {
            bottomSheetBehavior
                    .setPeekHeight(
                            (int) (DisplaySizeUtil.getInstance().newSize(getContext()).y * 0.7f));
        }
        // 화면 방향에 맞게 스팬 수 설정.
        GridLayoutManager manager = (GridLayoutManager) recyclerView.getLayoutManager();
        int spanCount =
                newConfig.orientation == Configuration.ORIENTATION_PORTRAIT ?
                        builder.portraitSpanCount :
                        builder.landscapeSpanCount;
        manager.setSpanCount(spanCount);

    }


    /**
     * 뷰 초기화.
     */
    private void initView() {
        // 배경 색상.
        contentView.setBackgroundColor(builder.backgroundColor);
        Button doneBtn = (Button) contentView.findViewById(R.id.btn_done);
        TextView titleView = (TextView) contentView.findViewById(R.id.text_title);
        progress = contentView.findViewById(R.id.progress);
        // 헤더 색상.
        contentView.findViewById(R.id.top).setBackgroundColor(builder.headerBackgroundColor);
        // 완료 버튼 색상.
        doneBtn.setTextColor(builder.doneColor);
        // 타이틀 색상.
        titleView.setTextColor(builder.titleColor);
        // 완료 버튼 텍스트.
        if (!TextUtils.isEmpty(builder.doneText)) doneBtn.setText(builder.doneText);
        // 타이틀 텍스트.
        if (!TextUtils.isEmpty(builder.title)) titleView.setText(builder.title);

        // 완료 버튼 동작 설정.
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (builder.singlePickCallback != null) {
                    List<Image> images = adapter.getCheckedList();
                    if (images.size() > 0)
                        builder.singlePickCallback.onSinglePick(images.get(0));
                } else {
                    builder.multiPickCallback.onMultiPick(adapter.getCheckedList());
                }
                dismissAllowingStateLoss();
            }
        });
        // 리사이클뷰.
        recyclerView = (RecyclerView) contentView.findViewById(R.id.recycler_view);
        int orientation = getResources().getConfiguration().orientation;
        // 화면 방향에 따라 스팬 수 설정.
        int spanCount =
                orientation == Configuration.ORIENTATION_PORTRAIT ?
                        builder.portraitSpanCount :
                        builder.landscapeSpanCount;
        GridLayoutManager manager = new GridLayoutManager(getContext(), spanCount);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        adapter = new ImageAdapter(builder);
        if (builder.fromCamera) adapter.setOnClickCamera(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCameraIntent();
            }
        });
        recyclerView.setAdapter(adapter);
    }


    /**
     * 다이얼로그 표시하기.
     */
    public void show(FragmentManager fragmentManager) {
        fragmentManager.beginTransaction()
                .add(this, getTag())
                .commit();
    }


    /**
     * 다이얼로그 표시하기.
     */
    public void showAllowingStateLoss(FragmentManager fragmentManager) {
        fragmentManager.beginTransaction()
                .add(this, getTag())
                .commitAllowingStateLoss();
    }


    /**
     * LoaderCallbacks.
     * 이미지 로더 생성.
     */
    @Override
    public Loader<List<Image>> onCreateLoader(int id, Bundle args) {
        Log.e("Loader", "onCreateLoader");
        progress.setVisibility(View.VISIBLE);
        ImageLoader loader = new ImageLoader(getContext());
        loader.forceLoad();
        return loader;
    }


    /**
     * LoaderCallbacks.
     * 이미지 로더 결과 처리.
     */
    @Override
    public void onLoadFinished(Loader<List<Image>> loader, List<Image> data) {
        Log.e("Loader", "onLoadFinished");
        adapter.setImages(data);
        progress.setVisibility(View.INVISIBLE);
    }

    /**
     * LoaderCallbacks.
     * 이미지 로더 리셋 중.
     */
    @Override
    public void onLoaderReset(Loader<List<Image>> loader) {
        Log.e("Loader", "onLoaderReset");
    }


    /**
     * 이미지 불러오기.
     */
    public void loadImages() {
        getActivity().getSupportLoaderManager().destroyLoader(0);
        getActivity().getSupportLoaderManager().initLoader(0, null, this);
    }


    /**
     * 카메라 저장 파일 생성.
     */
    private File createCameraFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File file = File.createTempFile(timeStamp, ".jpg", dir);
        cameraFilePath = file.getAbsolutePath();
        return file;
    }


    /**
     * 카메라 호출.
     */
    private void startCameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            try {
                File file = createCameraFile();
                Uri cameraUri;
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                    cameraUri = Uri.fromFile(file);
                } else {
                    cameraUri =
                            FileProvider
                                    .getUriForFile(getContext(),
                                            getContext().getPackageName() + ".provider",
                                            file);
                }
                intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
                startActivityForResult(intent, REQ_CAMERA);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQ_CAMERA: // 카메라 결과.
                if (resultCode == Activity.RESULT_OK) {
                    MediaScannerConnection
                            .scanFile(getContext(),
                                    new String[]{cameraFilePath},
                                    new String[]{"image/jpeg"},
                                    new MediaScannerConnection.MediaScannerConnectionClient() {
                                        @Override
                                        public void onMediaScannerConnected() {
                                        }

                                        @Override
                                        public void onScanCompleted(String path, Uri uri) {
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    loadImages();
                                                }
                                            });
                                        }
                                    });
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }


    /**
     * 단일 선택 콜백.
     */
    @SuppressWarnings({"unused", "WeakerAccess"})
    public interface SinglePickCallback {
        void onSinglePick(Image image);
    }


    /**
     * 다중 선택 콜백.
     */
    @SuppressWarnings({"unused", "WeakerAccess"})
    public interface MultiPickCallback {
        void onMultiPick(List<Image> images);
    }


    /**
     * 롱 클릭 콜백.
     */
    @SuppressWarnings({"unused", "WeakerAccess"})
    public interface LongClickCallback {
        void onLongClick(Image image);
    }


    /**
     * 다이얼로그 빌더.
     */
    @SuppressWarnings({"unused", "WeakerAccess"})
    public static class Builder {


        private Context context;
        /**
         * 기본 높이.
         */
        int peekHeight = -1;
        /**
         * 완료 버튼 색상.
         */
        @ColorInt
        int doneColor = Color.BLACK;
        /**
         * 타이틀 색상.
         */
        @ColorInt
        int titleColor = Color.BLACK;
        /**
         * 배경 색상.
         */
        @ColorInt
        int backgroundColor = Color.LTGRAY;
        /**
         * 헤더 배경 색상.
         */
        @ColorInt
        int headerBackgroundColor = Color.WHITE;
        /**
         * 타이틀.
         */
        String title;
        /**
         * 완료 버튼 텍스트.
         */
        String doneText;
        /**
         * 세로 화면 스팬 수.
         */
        int portraitSpanCount = 4;
        /**
         * 가로 화면 스팬 수.
         */
        int landscapeSpanCount = 7;
        /**
         * 카메라로 찍기.
         */
        public boolean fromCamera = true;
        /**
         * 단일 선택 콜백.
         */
        public SinglePickCallback singlePickCallback;
        /**
         * 다중 선택 콜백.
         */
        public MultiPickCallback multiPickCallback;
        /**
         * 롱 클릭 콜백.
         */
        public LongClickCallback longClickCallback;
        /**
         * 이미 체크된 이미지들.
         */
        public List<Image> checkedImages = new ArrayList<>();


        public Builder(Context context) {
            this.context = context.getApplicationContext();
        }


        /**
         * 기본 높이 설정.
         */
        public Builder setPeekHeight(int height) {
            peekHeight = height;
            return this;
        }


        /**
         * 기본 높이 설정.
         */
        public Builder setPeekHeightRes(@DimenRes int resId) {
            peekHeight = context.getResources().getDimensionPixelSize(resId);
            return this;
        }


        /**
         * 기본 텍스트 색상 지정.
         */
        public Builder setDefaultTextColor(@ColorInt int color) {
            titleColor = color;
            doneColor = color;
            return this;
        }


        /**
         * 기본 텍스트 색상 지정.
         */
        public Builder setDefaultTextColorRes(@ColorRes int resId) {
            return setDefaultTextColor(ContextCompat.getColor(context, resId));
        }


        /**
         * 타이틀 색상 지정.
         */
        public Builder setTitleColor(@ColorInt int color) {
            titleColor = color;
            return this;
        }


        /**
         * 타이틀 색상 지정.
         */
        public Builder setTitleColorRes(@ColorRes int resId) {
            titleColor = ContextCompat.getColor(context, resId);
            return this;
        }


        /**
         * 배경 색상 지정.
         */
        public Builder setBackgroundColor(@ColorInt int color) {
            backgroundColor = color;
            return this;
        }


        /**
         * 배경 색상 지정.
         */
        public Builder setBackgroundColorRes(@ColorRes int resId) {
            backgroundColor = ContextCompat.getColor(context, resId);
            return this;
        }


        /**
         * 배경 색상 지정.
         */
        public Builder setHeaderBackgroundColor(@ColorInt int color) {
            headerBackgroundColor = color;
            return this;
        }


        /**
         * 배경 색상 지정.
         */
        public Builder setHeaderBackgroundColorRes(@ColorRes int resId) {
            headerBackgroundColor = ContextCompat.getColor(context, resId);
            return this;
        }


        /**
         * 완료 버튼 색상 지정.
         */
        public Builder setDoneColor(@ColorInt int color) {
            doneColor = color;
            return this;
        }


        /**
         * 완료 버튼 색상 지정.
         */
        public Builder setDoneColorRes(@ColorRes int resId) {
            doneColor = ContextCompat.getColor(context, resId);
            return this;
        }


        /**
         * 타이틀 변경.
         */
        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }


        /**
         * 타이틀 변경.
         */
        public Builder setTitle(@StringRes int resId) {
            title = context.getString(resId);
            return this;
        }


        /**
         * 완료 버튼 텍스트 변경.
         */
        public Builder setDoneText(String text) {
            doneText = text;
            return this;
        }


        /**
         * 완료 버튼 텍스트 변경.
         */
        public Builder setDoneText(@StringRes int resId) {
            doneText = context.getString(resId);
            return this;
        }


        /**
         * 세로 화면 스팬 수 변경.
         */
        public Builder setPortraitSpanCount(int spanCount) {
            portraitSpanCount = spanCount;
            return this;
        }


        /**
         * 가로 화면 스팬 수 변경.
         */
        public Builder setLandscapeSpanCount(int spanCount) {
            landscapeSpanCount = spanCount;
            return this;
        }


        /**
         * 카메라로 찍기 설정.
         */
        public Builder setFromCamera(boolean fromCamera) {
            this.fromCamera = fromCamera;
            return this;
        }


        /**
         * 단일 선택 콜백 설정.
         */
        public Builder setSinglePickCallback(SinglePickCallback callback) {
            singlePickCallback = callback;
            multiPickCallback = null;
            return this;
        }


        /**
         * 다중 선택 콜백 설정.
         */
        public Builder setMultiPickCallback(MultiPickCallback callback) {
            multiPickCallback = callback;
            singlePickCallback = null;
            return this;
        }


        /**
         * 롱 클릭 리스너 설정.
         */
        public Builder setOnLongClickListener(LongClickCallback callback) {
            longClickCallback = callback;
            return this;
        }

        /**
         * 이미 체크된 이미지들 설정.
         */
        public Builder setCheckedImages(List<Image> checkedImages) {
            this.checkedImages = checkedImages;
            return this;
        }


        /**
         * 다이얼로그 생성.
         */
        public MampImagePicker create() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                    && ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                throw new RuntimeException("Missing required WRITE_EXTERNAL_STORAGE permission.");
            }
            if (singlePickCallback == null && multiPickCallback == null) {
                throw new RuntimeException("Missing required single or multi pick callback.");
            }
            MampImagePicker picker = new MampImagePicker();
            picker.builder = this;
            return picker;
        }
    }
}
