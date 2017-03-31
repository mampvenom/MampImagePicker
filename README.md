# MampImagePicker

[![](https://jitpack.io/v/mampvenom/MampImagePicker.svg)](https://jitpack.io/#mampvenom/MampImagePicker)

![Screenshot](https://github.com/mampvenom/MampImagePicker/blob/master/screenshot.gif?raw=true)

설정
====================

1. 프로젝트 빌드에 JitPack 저장소를 추가.

```Groovy
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
}
```

2. dependencies 섹션에 추가.

```Groovy
dependencies {
	        compile 'com.github.mampvenom:MampImagePicker:1.0.1'
}
```

3. Manifest 수정.
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


사용법
====================

1. 다중 선택.
```Java
new MampImagePicker.Builder(this)
                .setMultiPickCallback(new MampImagePicker.MultiPickCallback() {
                    @Override
                    public void onMultiPick(List<Image> images) {
                        
                    }
                })
                .create().show(getSupportFragmentManager());
```

2. 단일 선택.
```Java
new MampImagePicker.Builder(this)
                .setSinglePickCallback(new MampImagePicker.SinglePickCallback() {
                    @Override
                    public void onSinglePick(Image image) {
                        
                    }
                })
                .create().show(getSupportFragmentManager());
```
