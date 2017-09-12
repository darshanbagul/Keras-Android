# Handwritten Digit Classification Android app

## Overview

This project comprises of a handwritten character image (MNIST) classifier that can run on any android device. The app stores a set of images (0-9) that we can cycle through and classify in order. It uses a pre-trained model to perform inference on the device. This idea can be applied to any images, both by using the camera and by pulling from the Web. We're using preloaded images so we can run the app in a simulator (no need for the device since it doesn't require a camera).

I used the popular MNIST dataset for this task. The significant focus is on deploying the trained model in Android framework, rather than training a classification model. If you need to understand the task of classifying handwritten digits, [this](https://github.com/darshanbagul/USPS_Digit_Classification) is a good starting point.

## Results

Here are a few output results, which depict the UI of the app along with the classification results.

![Image](https://github.com/darshanbagul/Keras-Android/blob/master/images/2.png)

![Image](https://github.com/darshanbagul/Keras-Android/blob/master/images/4.png)

![Image](https://github.com/darshanbagul/Keras-Android/blob/master/images/5.png)

![Image](https://github.com/darshanbagul/Keras-Android/blob/master/images/6.png)

The model predicts the output label along with the confidence score for the prediction.

## Setting up the Android Environment

### Installation Steps

If you already have Android Studio, skip to Step 5!

  1. Download Android studio
        https://developer.android.com/studio/index.html
        
  2. Download Android SDK
  
        ```
        $ wget https://dl.google.com/android/android-sdk_r24.4.1-linux.tgz
        $ tar xvzf android-sdk_r24.4.1-linux.tgz -C ~/tensorflow
        ```

  3. Download SDK Build Tools
  
        ```
        $ cd ~/tensorflow/android-sdk-linux
        $ tools/android update sdk --no-ui
        ```

  4. Download Android NDK
  
        ```
        $ wget https://dl.google.com/android/repository/android-ndk-r12b-linux-x86_64.zip
        $ unzip android-ndk-r12b-linux-x86_64.zip -d ~/tensorflow
        ```
      Set correct SDK and NDK versions in your workspace file

  5. Train Model in Python on your desktop or a server
        tensorflow_model/mnist_convnet_keras.py

  6. Download the latest Tensorflow Android release package (AAR)
        AAR (Android Archive) files are a convenient way to distribute packages– mainly libraries– for use with Android Studio and Gradle. Adding one of these files to your app requires creating some metadata files and updating your app’s gradle build files. Fortunately, Android Studio can handle all of this for us.
        http://docs.onemobilesdk.aol.com/android-ad-sdk/adding-aar-files.html
        
  7. Compile the code and run it on a simulator (or device if you have one)!
