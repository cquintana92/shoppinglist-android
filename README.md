# ShoppingList (Android)

This repository contains the Android client for the ShoppingList.

Link to the ShoppingList server repository: https://gitlab.com/cquintana92/shoppinglist-server

## 1. How to get
### 1.1. Download a generated APK

You can go to the [releases section of this project](https://gitlab.com/cquintana92/shoppinglist-android/-/releases) and download the generated APK.

### 1.2. Build from source

Feel free to edit the `app/build.gradle.kts` file to define your signing configuration, and you should be able to generate an APK by running

```
$ make compile_release
```

The generated APK will be located at `app/build/outputs/apk/release/`.


## 2. How to use

The first time you start the application you will need to set the server URL (pointing) to the [ShoppingList server](https://gitlab.com/cquintana92/shoppinglist-server). You can change it any time you want.

