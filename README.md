# MampImagePicker

[![](https://jitpack.io/v/mampvenom/MampImagePicker.svg)](https://jitpack.io/#mampvenom/MampImagePicker)

![Screenshot](https://github.com/mampvenom/MampImagePicker/blob/master/screenshot.gif?raw=true)


# 설정

## 1. 프로젝트 빌드에 JitPack 저장소를 추가.

```Gradle
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
}
```

## 2. dependencies 섹션에 추가.

```Gradle
dependencies {
	        compile 'com.github.mampvenom:MampImagePicker:1.0.2'
}
```

## 3. Manifest 수정.
```XML
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />

<application
        ...
        <activity
            ...
            android:configChanges="keyboard|keyboardHidden|layoutDirection|orientation|screenLayout|screenSize">
            ...
        </activity>
    </application>

```


# 사용법

## 1. 다중 선택.
```Java
new MampImagePicker.Builder(this)
                .setMultiPickCallback(new MampImagePicker.MultiPickCallback() {
                    @Override
                    public void onMultiPick(List<Image> images) {
                        
                    }
                })
                .create().show(getSupportFragmentManager());
```

## 2. 단일 선택.
```Java
new MampImagePicker.Builder(this)
                .setSinglePickCallback(new MampImagePicker.SinglePickCallback() {
                    @Override
                    public void onSinglePick(Image image) {
                        
                    }
                })
                .create().show(getSupportFragmentManager());
```


## 3. 사용자 정의.

### 기본 높이.
* `setPeekHeight(int height)`
* `setPeekHeightRes(@DimenRes int resId)`
### 색상.
* `setDefaultTextColor(@ColorInt int color)`
* `setDefaultTextColorRes(@ColorRes int resId)`
* `setTitleColor(@ColorInt int color)`
* `setTitleColorRes(@ColorRes int resId)`
* `setDoneColor(@ColorInt int color)`
* `setDoneColorRes(@ColorRes int resId)`
* `setBackgroundColor(@ColorInt int color)`
* `setBackgroundColorRes(@ColorRes int resId)`
* `setHeaderBackgroundColor(@ColorInt int color)`
* `setHeaderBackgroundColorRes(@ColorRes int resId)`
### 텍스트.
* `setTitle(String title)`
* `setTitle(@StringRes int resId)`
* `setDoneText(String text)`
* `setDoneText(@StringRes int resId)`
### 그리드의 열 수.
* `setPortraitSpanCount(int spanCount)`
* `setLandscapeSpanCount(int spanCount)`
### 카메라 사용 여부.
* `setFromCamera(boolean fromCamera)`
### 콜백.
* `setSinglePickCallback(SinglePickCallback callback)`
* `setMultiPickCallback(MultiPickCallback callback)`
* `setOnLongClickListener(LongClickCallback callback)`
### 미리 지정된 이미지들.
* `setCheckedImages(List<Image> checkedImages)`
