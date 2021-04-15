# Project overview
PhotoTag is a mobile application on both Android and iOS which is designed to eliminate time spent by the user searching in their gallery for a specific image. With PhotoTag, users will be able to attach keywords to their existing photos with suggested tags from an image processing API, or choose to add their own custom keywords. These keywords can be used to search through the image collection for a smaller subset, for less total time spent searching for the user. Options involving the tagging method are available to the user so that they can select whether they want their photos to be processed by an on-device image labeling API, or by making a connection to a specialized server and sending the photos for processing. Users may also create schedules so that photos taken within a specified time range are automatically tagged with certain phrases, as well as see all photos with location data on the map. 

# Feature List
* Manual user-entered tagging of photos
* Automatic tagging
  * On-device tagging using MLKit
  * Remote tagging using server
* Search photos by tag (using voice or text)
* Tag scheduling
* Map view

# Contributors
* Alex J St.Clair (Cross-platform, project leader)
* James Coolen (Android)
* Matthew Day (Android)
* Sebastian Tota (iOS, Git)
* Reed Ceniviva (Android)
* Ryan O' Connor (iOS)
* Tadeusz J Rzepka (Android, Git, Scrum-master)

***
# Testing Document
<https://tuprd.sharepoint.com/:x:/s/PhotoTag/ERevMYmlenpCgUyQsPsEZe4BNDv7zJY_jYF4JbRz6R4Shg?e=gB8asz>
***

# Instructions for buliding

### Option 1: Downloading APK directly from Github on device
1. Navigate to this github repository on your device. Make sure you are on the "Code" tab. 
2. Scroll down until you see "Releases"
3. Click on the latest release.
4. Under assets at the bottom, click on the APK file. 
    * On the pop-up that appears, ensure that the destination folder is somewhere you will be able to navigate to in the Files app.
5. Click download on the pop-up and "okay" if it asks you if you still want to download it, despite it not being on the Play Store.
6. Navigate to your Files app, to the location specified in the pop-up mentioned above
7. Click on the APK file in your device's Files app. When the pop-up appears that says "your phone is not allowed to install unknown apps from this source", click on the settings button.
8. Toggle the switch that says "Allow from this source". 
    * **NOTE:** The message that appears below applies to any application downloaded using this method, including PhotoTag. With this being said, PhotoTag does not track any user interactions, and has read permissions to **only** the local photos on your device. 
10. Click the back button, and then click "install" on the next pop-up. 
    * The app has now been added to your app library, so you can choose to launch now or launch from the app library later. 
11. Grant permission to access local photos on the first application launch, and you are ready to go!
**Note:** We recommended that, after you are able to launch the app successfully and are also able to find it in your app library, you should delete the APK file from the download destination in step 5. Files with the .apk extension tend to get rather large, and since they are only used for an inital build of the project (done in step 7), they should be deleted afterwards to preserve storage space on your device. 

***

### Option 2: Downloading APK onto computer and transferring file to device
1. Connect Android device to PC via USB cable and prepare to run the app by doing the following:
    1. If your phone's developer mode is not already enabled, navigate to Settings -> System -> About phone
    2. You will see either "Software version" or "Build number". Click on this exactly 7 times.
    3. Go back to the previous screen, and click "Developer options"
    4. Under "Debugging", enable "USB debugging". Click "ok" on the resulting pop-ups, checking "Always allow from this computer" on the second one if you would like to download apps using this method again. 
3. On your computer, navigate to the "release" tab above.
4. Download the APK file onto your computer, to a path where you will find it. 
5. Navigate to the file in your computer's file directory, and copy the .apk file to attached device's "Internal shared storage/download" folder.
6. When the file has been transferred onto the device, turn off USB debugging (refer to step 1.iv), and disconnect it from your PC.
7. Follow steps 7-11 above to install the application to your phone. 

***

# Starting Guide

When permission is granted to read local photos, and you log in successfully using Google, the application will load all of your photos into a gallery view. At this point, you may begin selecting photos and adding tags. In the settings page you will find an option to enable/disable auto-tagging, a feature where the application automatically processes your photos and applies searchable tags. The app offers two options for auto-tagging: on-device or server processing. On-device uses Google's ML Kit API, and server processing uses a more extensive library of photo labels for increased accuracy. 
