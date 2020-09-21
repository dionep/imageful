# Imageful
This library will help you get images from camera/gallery more simplifier. It requests all permissions you needed to work with camera & gallery.

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
  implementation 'com.github.dionep:imageful:0.2.1'
}
```

## How to use?
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

You can see sample in repository.
