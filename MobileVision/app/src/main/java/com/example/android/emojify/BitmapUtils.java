/*
* Copyright (C) 2017 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*  	http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.example.android.emojify;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.example.android.emojify.MainActivity.TAG;

class BitmapUtils {

    private static final String FILE_PROVIDER_AUTHORITY = "com.example.android.fileprovider";


    /**
     * Resamples the captured photo to fit the screen for better memory usage.
     * Or can say convert or Represent the bitmap Image through Bitmap Class.
     *
     * @param context   The application context.
     * @param imagePath The path of the photo to be resampled.
     * @return The resampled bitmap
     */
    static Bitmap resamplePic(Context context, String imagePath) {

        // Get device screen size information
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        manager.getDefaultDisplay().getMetrics(metrics);

        //Getting the Actual screen size in the form of Pixels.
        int targetH = metrics.heightPixels;
        int targetW = metrics.widthPixels;

        // Get the dimensions of the original bitmap without processing the image in the Memory, this give us meta data.
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();

        /**Setting true means it will return the Decoder and not Image Processing
         * or Allocating of memory will happen because we don't want to allocate memory
         * for every large pic in the memory and that way Decoder will return from there
         * but Pixel for height and width will be set.*/
        bmOptions.inJustDecodeBounds = true;

        /**Now in this case Memory for the Bitmap will not be Allocated, because
         * we passed BitmapFactory.Options.*/
        BitmapFactory.decodeFile(imagePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;

        /**inSampleSize: This integer indicates how much the dimensions of the bitmap should
         *  be reduced. Given an image of 1000×400, an inSampleSize of 4 will result in a bitmap of 250×100.
         *  The dimensions are reduced by a factor of 4.*/
        bmOptions.inSampleSize = scaleFactor;

        /**Now it will Actually Allocate the memory and Decode the Bitmap image and
         * return resampled Bitmap.
         *
         * */
        return BitmapFactory.decodeFile(imagePath);

        /**
         * SOURCES:
         *      1. (Best Explain): http://www.informit.com/articles/article.aspx?p=2143148&seqNum=2
         *
         *      2. (Best): https://android.jlelse.eu/loading-large-bitmaps-efficiently-in-android-66826cd4ad53
         *
         *      3. Official(Documentation): https://developer.android.com/topic/performance/graphics/
         *
         * NOTE:
         *   1. In android Bitmap configuration used is: ARGB_8888 means that each pixel
         *   take 4 bytes of space in the Memory that means if a picture has "4048x3036"
         *   then it will take "4048x3036x4" = 48MB of space, and it's such a huge space
         *   can take all the memory in one shot.
         *
         *   2. Do note this is just the inner working, in production always prefer
         *   Libraries: Like GLIDE, OR PICASSO*/
    }

    /**
     * Creates the temporary image file in the cache directory.
     *
     * @return The temporary image file.
     * @throws IOException Thrown if there is an error creating the file
     */
    static File createTempImageFile(Context context) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        /**Getting the Cache Directory belong to this Package or App. SO that we can
         * Store our Temporary Files under this Cache Folder
         *
         * It's Like:
         *    -> /storage/emulated/0/Android/data/com.example.android.emojify/cache*/
        File storageDir = context.getExternalCacheDir();

        Log.i(TAG, "createTempImageFile: " + storageDir.toString());

        /**Creating FIle instance doesn't mean creating new File in File System, it's
         * just a Abstract Representation of the file and Directory path names independent
         * of System. As we created the FIle Instance with the prefix and suffix(.jpg, ..png)
         * in the Cache Directory.
         *
         * Basically every Operating System has their own way of specifying the Path names
         * of files and Directories but this class represents and System Independent
         * Abstact Path name and has 2 components:
         *
         *    1. Optional System-dependent prefix String such as disk Specifier, "/"
         *    for the UNIX directory and "\\\" for Microsoft UNC(universal naming Convention) Path
         *    (and this dependent on from which operating system we are making File Instance
         *    it will convert that into Abstract Path name independent of OS).
         *
         *    2. Sequence of Zero or more String names.
         *
         * Source:
         *   1. https://developer.android.com/reference/java/io/File
         *
         *
         * NOTE:
         * 1.Basically Prefix is used to define the Root when specifying the Absolute
         * path, dependent on the underlying System or OS. For Example:
         *    -> When Path(String) given from the Unix then the Prefix will be "/"
         *    and when path(String) given from the Microsoft Windows then the Prefix
         *    will be "DriveLetter:\\" and for Microsoft UNC it will be "\\\"
         *
         * 2. The COnversion of Pathname from or to Abstract Pathname require of System defined
         * Directory separator like for Windows it's = "\" and for UNIX it's = "/" these
         * separators are available through static fields called "separator" in this
         * FIle Class.
         *
         * 3. File System may Restrict File System Instance to Reading and Writing called
         * Access Permissions.
         *
         * It's Like:
         *    -> /storage/emulated/0/Android/data/com.example.android.emojify/cache*/
        File tempFile =  File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        Log.i(TAG, "createTempImageFile: " + tempFile.toString());
        return tempFile;
    }

    /**
     * Deletes image file for a given path.
     *
     * @param context   The application context.
     * @param imagePath The path of the photo to be deleted.
     */
    static boolean deleteImageFile(Context context, String imagePath) {

        /**Getting the File Instance or Creating the File Instance from the Path "imagePath"
         * this is because we want to access the File System so we want the File System
         * Instance because the methods for accessing or perform any action on Files are
         * Available in this class.*/
        File imageFile = new File(imagePath);

        // Delete the image
        boolean deleted = imageFile.delete();

        // If there is an error deleting the file, show a Toast
        if (!deleted) {
            String errorMessage = context.getString(R.string.error);
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
        }

        return deleted;
    }

    /**
     * Helper method for adding the photo to the system photo gallery so it can be accessed
     * from other apps.
     *
     * @param imagePath The path of the saved image
     */
    private static void galleryAddPic(Context context, String imagePath) {

        /**Intent are useful when we want to start any App component like: Activity,
         * Services, Broadcast Action.
         *
         * ->  And "ACTION_MEDIA_SCANNER_SCAN_FILE" is the Broadcast Action for invoking
         * the Media Scanner Service and then Media Scanner Service will check for any new
         * Media providing the specific Data to check, which means that we gave the imagePath like:
         *
         *           "/storage/emulated/0/Pictures/Emojify/JPEG_20190124_134052.jpg"
         *
         * Now this String path converted into FIle Instance which is Abstract Path Name
         * and then converted into Uri like:
         *
         *           "file:///storage/emulated/0/Pictures/Emojify/JPEG_20190124_134519.jpg"
         *
         * And then this URI will be passed with Broadcast Intent as Data which will
         * Invoke the Broadcast-Receiver related to "ACTION_MEDIA_SCAN_FILE" and
         * BroadCast Receiver has the Service which accept the URI as Data to Scan for
         * that Specific File in the FIle System.
         *
         * It's like:
         *   BroadCastIntent(Action: ACTION_MEDIA_SCANNER_SCAN_FILE, Data: URI) ------(find broadcast receiver for this Broadcast intent)-----> BroadCastReceiver(for handling ACTION_SCANNER_SCAN_FILE) ----(invoke scan service)----> get the URI and Start scan service.*/

        Log.i(TAG, "galleryAddPic: " + imagePath);
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(imagePath);

        Uri contentUri = FileProvider.getUriForFile(context, FILE_PROVIDER_AUTHORITY, f);
        //Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }


    /**
     * Helper method for saving the image.
     *
     * @param context The application context.
     * @param image   The image to be saved.
     * @return The path of the saved image.
     */
    static String saveImage(Context context, Bitmap image) {

        String savedImagePath = null;

        /**It's same like when we created the File Instance, same Procedure.*/
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";

        /**-> But here instead of Cache Directory we are requesting the External/Shared Directory
         * which can be Accessible by the user and it's basically a Shared Memory Storage
         * which is Accessible by User and all the Data of the User has been into this Public
         * Directory(Private things as well like photos and Videos), So before accessing this
         * Storage make sure to get "WRITE_EXTERNAL_STORAGE" Permission. Because it's a
         * Dangerous Category permission.
         *
         * -> Environment.DIRECTORY_PICTURES: Standard directory in which to place pictures that are available to the user.
         *
         * -> and we also append the "/Emojify" because we want the Shared Directory of Pictures
         * which named as "Emojify".
         *
         * EXTRA_INFO:
         *      1. Android has 3 types of Storages available, if mobile has SD Card
         *      Mounted then that will be treated as External/Shared Storage and if not
         *      then internal Storage treated as External Storage and Require Permission
         *      to Access but we have "getExternalFilesDir()" method which give the Directory
         *      to store Application Specific Private Data which can also be Removed when
         *      Application is removed. and also this doesn't need any Write , Read Permission.*/
        File storageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                        + "/Emojify");
        boolean success = true;

        /**Check if the Directory named as "Emojify" is exist or not in the Shared Public
         * Directory or we can say if the Emojify folder already been there no need to
         * call mkdirs() which is responsble for creating the new Directory in the Shared
         * /Public Environment.*/
        if (!storageDir.exists()) {
            success = storageDir.mkdirs();
        }

        // Save the new Bitmap
        if (success) {

            /**Now after getting the Directory of name "Emojify", now we can make a
             * "FIle" Instance so that we can write ot read to/from the File.
             *
             * and we created the File Instance in the: "/storage/emulated/0/Pictures/Emojify"
             * now it's turn to write to that file, in our case we want to save Image(Bitmap)
             * that we are getting in this method parameter into that file.*/
            File imageFile = new File(storageDir, imageFileName);
            savedImagePath = imageFile.getAbsolutePath();
            try {
                /**OutPutStream is for writting to the File and InputStream is for
                 * Reading from the File. That's why we made OutputStream instance
                 * so that we can write Image(Bitmap) to that file(imageFile).
                 *
                 * A FileOutputStream will handle file existence test/creation/opening etc for you.*/
                OutputStream fOut = new FileOutputStream(imageFile);

                /**Now Specify which format to write(in our case JPEG) and the
                 * Quality we want in our case it's 100 percent.*/
                image.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                fOut.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            /**Now after wrtting the Image(Bitmap) to the External/Shared Directory
             * namely "Emojify", how can Android System know that new Media has been
             * Added, so for that we want to specifically start the System Service
             * which can run in background and scan for new Media.*/
            galleryAddPic(context, savedImagePath);

            // Show a Toast with the save location
            String savedMessage = context.getString(R.string.saved_message, savedImagePath);
            Toast.makeText(context, savedMessage, Toast.LENGTH_SHORT).show();
        }

        return savedImagePath;
    }

    /**
     * Helper method for sharing an image.
     *
     * @param context   The image context.
     * @param imagePath The path of the image to be shared.
     */
    static void shareImage(Context context, String imagePath) {
        // Create the share intent and start the share activity
        File imageFile = new File(imagePath);
        Log.i(TAG, "shareImage: " + imagePath);

        /**This is the Implicit Intent which can be resolved by the Android System
         * on the basis of ACTION and DATA(but in our case it's type of Data) we Specify. Android System check every App
         * Manifest File and check for Intent-FIlter Tags in any App specify
         * ACTION_SEND and MIME type: image then that App will be shown into the pickup
         * dialog.*/
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");

        /**Because we are creating the Implicit intent with "ACTION_SEND" and also
         * type "image/*" and now all the Apps will be in the pickup dialog who support "Sending of Images"
         * like: Whatsapp, Gmail...etc.
         *
         * -> As all the Apps are available for choose but we want one more thing which is Data
         * we want to send and as we can't send whole Image(Bitmap). So, what we can do is to pass
         * the URI of the Location of the image, that's why we are converting File System
         * Instance which points to Actual image in the External/Shared Storage in the URI
         * which will give the Actual path of that particular file.
         *
         * -> But why we are not using this method for converting the File Instance which is some what like this:
         *
         *              "/storage/emulated/0/Pictures/Emojify/JPEG_20190125_080751.jpg"
         *
         * to this:
         *             "file:///storage/emulated/0/Pictures/Emojify/JPEG_20190125_080751.jpg"
         *
         * So, now we are passing the Image URI which is in our App("Emojfy") public Directory,
         * and as we all know that File Access need Permission to WRITE and READ, and imagine
         * we are passing this ACTION_SEND with the "file URI" to Whatsapp, So then Developers of Whatsapp
         * must declared in their Manifest <Intent-FIlter></Intent-Filter> Tag that which activity
         * in Whatsapp can handle or perform this Operation send image Operation. And Imagine
         * if that activity get the "file Uri" and don't have Permission for Accessing the
         * External Read, Write Permission then Whatsapp will be Crashed.
         *
         * NOTE:
         *
         * 1.File Access need READ and WRITE Permission because it's dangerous Permission
         * because this storage is not specific to the App but instead this is the
         * External/Shared Storage which also has User private Data. And this is the major
         * security issue. So that's why we used "FileProvider" to create the URI...See below Explaination.
         *
         * 2.If you try to use this method and pass "file://" Uri then your App will
         * be crashed "FileUriExposedException" because you can't pass File Uri now, it's not advisable.*/
        //Uri photoURI = Uri.fromFile(imageFile);

        /**Instead we used this method for creating the URI of the Image(which is in our
         * App Public Directory("Emojify")), and Remember it's not the "File(file://) URI" instead
         *  it's a "content(content://) URI".
         *
         * -> "Content" scheme is specifically for android and it's meant to be a safer way to
         * exchange the Data with other apps and thus provide Abstraction. That's why we
         * are using "ContentProvider" to share our external File Data to other apps with the
         * temporary Access Permission.
         *
         * -> ContentProvider help us in many ways like:  if we want to share our Database
         * data with other apps, we can use ContentProvider. More Specifically we can extend
         * ContentProvider and make our custom Provider which has a Unique ID called "Authority"
         * and then whenever any app use ContentResolver with our unique "Authority" then
         * "ContentResolver" will resolve or find which contentProvider can map to
         * given "Authority" and then anyone can execute and "Insert", "Query" Operations
         * or anyone have the Access to the Data of the Particular App. That's where ContentProvider
         * Shines and it's the Primary Component of Android.
         *
         * -> And as we know every App must have their own Custom ContentProvider to expose
         * their data to outside world. but Android "FileProvider" make things easier for
         * us. Because we can use this FileProvider for exposing the File Data to Outside
         * World, let's convert our File System Instance:
         *
         *            "/storage/emulated/0/Pictures/Emojify/JPEG_20190125_080751.jpg"
         *
         * to this:
         *            "content://com.example.android.fileprovider/my_images/Emojify/JPEG_20190125_080751.jpg"
         *
         * Now as we can see FileProver.getUriForFile() give us above content URI which then we
         * can pass through Intent and then any Activity which will get this URI doesn't have
         * to ask for File Permission:- WRITE or READ because this is not a
         *
         *                           "file://" URI
         *
         *                           INSTEAD IT"S A:
         *
         *                           "content://" URI
         *
         * And as we can see that it has "content://<authority>/<path>" and in this case in the place of
         * Authority we have the Unique Authority "com.example.android.fileprovider" which then
         * ContentResolver resolve and know that ohhh "com.example.android.fileprovider" belongs
         * to "Emojify" App because we declared <Provider></Provider> Tag in the Manifest
         * and finally After Resolving our ContentProvider from the Authority then it will
         * check for "name:" Property which is a "FileProvider" and it's done, because
         * "FileProvider" implementation already defined in the "android.support.v4.content.FileProvider"
         *
         * It's Mandatory to use "android.support.FILE_PROVIDER_PATHS" for better
         * Abstraction. and it will map automatically on the place of the path like
         * in the above content URI "my_images" will map to "Pictures/" which is pointing
         * to like this:
         *
         *              "<external-path name="my_images" path="Pictures/" />"  : means from the public external storage there's an "Picture" directory map "my_images" to that "Picture" directory.
         *
         *                                   AND
         *
         *              "<external-cache-path name="my_cache" path="." />"    : means from the external Cache Directory map "my_cache" to this current Directory which is "cache" directory.
         *
         * These values are under the "file_path.xml".
         *
         * And you may wondering whats the meaning of "<external-path />" and <external-cache/>
         * These Values pointing to specific paths in the Android Storage Directory like:
         *
         *
         *          <files-path/> - internal app storage, Context#getFilesDir()
         *          <cache-path/> - internal app cache storage, Context#getCacheDir()
         *          <external-path/> - public external storage, Environment.getExternalStorageDirectory()
         *          <external-files-path/> - external app storage, Context#getExternalFilesDir(null)
         *          <external-cache-path/> - external app cache storage, Context#getExternalCacheDir()
         *
         *
         * SOURCE:
         *      1. https://infinum.co/the-capsized-eight/share-files-using-fileprovider#disqus_thread
         *
         *      2. https://commonsware.com/blog/2015/10/07/runtime-permissions-files-action-send.html
         *
         *      3. https://stackoverflow.com/questions/32981194/android-6-cannot-share-files-anymore
         *
         *      4. (Types of Storages): https://developer.android.com/guide/topics/data/data-storage#filesInternal*/
        Uri photoURI = FileProvider.getUriForFile(context, FILE_PROVIDER_AUTHORITY, imageFile);

        Log.i(TAG, "shareImage: " + photoURI);
        shareIntent.putExtra(Intent.EXTRA_STREAM, photoURI);
        context.startActivity(shareIntent);
    }
}
