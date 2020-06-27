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

The first time you start the application you will need to set the server URL and Bearer to the [ShoppingList server](https://gitlab.com/cquintana92/shoppinglist-server). You can change it any time you want.

### 2.1. Adding new elements

In order to add a new element you only need to write the element name in the section at the bottom of the screen, and either press the "Enter" key or the "+" button.

### 2.2. Updating elements

In order to rename one of the elements, you only need to press the element's name, edit it, and either press the "Enter" key or the "check" button.

### 2.3. Removing elements

In order to remove one of the elements, you need to press the element's name and click the "X" button.
