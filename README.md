![Release](https://jitpack.io/v/dionep/imageful.svg)

# DEPRECATED
The library is out of date

# Imageful
This library will help you get images from camera/gallery more simplifier. It requests all permissions you needed to work with camera & gallery.
Also this library has ImageSaver, which helps to save images to gallery from url.

## Gradle
Add it in your root build.gradle at the end of repositories:
```groovy
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
```
Add the dependency:
```groovy
dependencies {
  implementation 'com.github.dionep:imageful:0.3.0'
}
```

## How to use Imageful?
First, you need to add permission to AndroidManifest.xml:
```xml
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.CAMERA"/>
```

Imageful extends DialogFragment, thereby it works like:
```kotlin
Imageful.create(
  inputType = Imageful.InputType.CAMERA,
  imagesGotCallback = { image -> onImageGot(image) },
  uriMapper = { Image(it) }
).show(supportFragmentManager, SomeTag)
```

Imageful supports to receive image from camera, single & mutliple images from gallery.
- InputType.CAMERA
- InputType.GALLERY_SINGLE
- InputType.GALLERY_MULTIPLE

UriMapper used for uri transormation to model.

Also we can handle that permissions is denied:
```kotlin
Imageful.create(
  inputType = Imageful.InputType.CAMERA,
  imagesGotCallback = { image -> onImageGot(image) },
  uriMapper = { Image(it) },
  permissionsFailureCallback = {
    Toast.makeText(applicationContext, "Permissions failure", Toast.LENGTH_LONG).show()
  }
).show(supportFragmentManager, SomeTag)
```

## How to use ImageSaver?
All what you need is to have this permission in AndroidManifest.xml:
```xml
  <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
  <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
  <uses-permission android:name="android.permission.CAMERA"/>
  <uses-permission android:name="android.permission.INTERNET"/>
  <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
  <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```
And call ImageSaver:
```kotlin
ImageSaver.create(
  imageUrl = "https://image.url",
  permissionsFailureCallback = {
      Toast.makeText(applicationContext, "Permissions failure", Toast.LENGTH_LONG).show()
  },
  saveSuccess = {
      Toast.makeText(applicationContext, "Success", Toast.LENGTH_LONG).show()
  },
  saveFailure = {
      Toast.makeText(applicationContext, "Fail", Toast.LENGTH_LONG).show()
  }
).show(supportFragmentManager, null)
```

You can see samples in repository.
