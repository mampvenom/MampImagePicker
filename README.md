# MampImagePicker

[![](https://jitpack.io/v/mampvenom/MampImagePicker.svg)](https://jitpack.io/#mampvenom/MampImagePicker)

![Screenshot](https://github.com/mampvenom/MampImagePicker/blob/master/screenshot.gif?raw=true)

Setup
====================

Step 1. Add the JitPack repository to your build file.

Add it in your root build.gradle at the end of repositories:

```Groovy
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
}
```

Step 2. Add the dependency

```Groovy
dependencies {
	        compile 'com.github.mampvenom:MampImagePicker:1.0.0'
}
```

How to use
====================

```Java
new MampImagePicker.Builder(getApplicationContext())
                .setMultiPickCallback(new MampImagePicker.MultiPickCallback() {
                    @Override
                    public void onMultiPick(List<Image> images) {
                        
                    }
                })
                .create().show(getSupportFragmentManager());
```
