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


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";


    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_STORAGE_PERMISSION = 1;

    private static final String FILE_PROVIDER_AUTHORITY = "com.example.android.fileprovider";

    @BindView(R.id.image_view) ImageView mImageView;

    @BindView(R.id.emojify_button) Button mEmojifyButton;
    @BindView(R.id.share_button) FloatingActionButton mShareFab;
    @BindView(R.id.save_button) FloatingActionButton mSaveFab;
    @BindView(R.id.clear_button) FloatingActionButton mClearFab;

    @BindView(R.id.title_text_view) TextView mTitleTextView;

    private String mTempPhotoPath;

    private Bitmap mResultsBitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind the views
        ButterKnife.bind(this);

        // Set up Timber
        Timber.plant(new Timber.DebugTree());
    }

    /**
     * OnClick method for "Emojify Me!" Button. Launches the camera app.
     */
    @OnClick(R.id.emojify_button)
    public void emojifyMe() {

        /**If we want Permission to do something outside the App Sandbox, then we need permission
         * to do that and for that we want to Declare the permission in 'Manifest' with the
         * <uses-Permission> </uses-permission> Tag. and some permissions are Considered to be
         * Normal Permissions and some are Dangerous Permissions.
         *
         * -> Normal Permissions are normal and granted by the Android but some dangerous
         * which can effect the use privacy can be ask at Runtime starting from Android-6 (Api-26)
         * But we can do it as Backward Compatible way using the compat Library not standard onw.
         *
         * -> We can ask by 'ContextCompat.checkSelfPermission' which internally use:
         *            "context.checkPermission(permission, android.os.Process.myPid(), Process.myUid())"
         *
         * -> permission is the Permission we want and it comes from "Manifest.permission.PERMISSION"
         * and then internally it will check on the behalf of our "android.os.myPid()" and
         * "Process.uid()"
         *
         *       pid: Process ID()
         *       uid: user ID(user ID of the application that owns that process) so every app has
         *       their own unique Linux UID
         *
         * -> And if the App already has Permission then "context.checkPermission" will return
         * the "PackageManager.GRANTED" or "PackageManager.DENIED" and if App doesn't have the permission
         * then we "requestPermission(context, Array_of_permissions, REQUEST_CODE)" but here's the
         * twist before asking the permission its a good idea to tell the user why your app need
         * the permission like: if your app is a audio player then user won't be surprised that
         * if your app want the storage access permission but if you want location or Call pemrission
         * then you may explain why you need that with the help of:
         *
         *                 "shouldShowRequestPermissionRationale()"
         *
         * This method return true if user previously denied the permission and still acessing
         * the your app, it will show that user want to use your app, so in that case give some
         * explaination with the help of "Toast".
         *
         * CLASSES:
         *    1. Process: Tools for managing OS processes.
         *
         *    2. Context: Interface to global information about an application environment.
         *    This is an abstract class whose implementation is provided by the Android system.
         *    It allows access to application-specific resources and classes, as well as up-calls for
         *    application-level operations such as launching activities, broadcasting and receiving intents, etc.
         *
         *    3. ActivityCompat: Helper for accessing features in Activity in a Backward
         *    compatible fashion.
         *
         * NOTE:
         *    1.Note that check for permission only when you want to access specific functionalty
         *    and check everytime, it may be possible that uer may deny the permission is
         *    future.
         *
         * SOURCE:
         *    1. PERMISSION_GROUP: https://stackoverflow.com/questions/17371326/what-is-the-use-of-permission-group-in-android
         *
         *    2. PERMISSIONS: https://developer.android.com/training/permissions/requesting
         * */
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // If you do not have permission, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_STORAGE_PERMISSION);
        } else {
            // Launch the camera if the permission exists
            launchCamera();
        }
    }

    /**-> Called when we request permission with the help of class "ActivityCompat" and
     * then REQUEST_CODE will matched with the code we passed when requesting the Permission
     * and if Request Code found then check for array of Results "grandResults"
     * in which we can check if the Persmission is granted or not.
     *
     * SOURCE:
     *    1. (Package Manager Full explain): http://kpbird.blogspot.com/2012/10/in-depth-android-package-manager-and.html#more
     *    */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        // Called when you request permission to read and write to external storage
        switch (requestCode) {
            case REQUEST_STORAGE_PERMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // If you get permission, launch the camera
                    launchCamera();
                } else {
                    // If you do not get permission, show a Toast
                    Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    /**
     * Creates a temporary image file and captures a picture to store in it.
     */
    private void launchCamera() {

        /**Basically this is the Implicit intent that we are passing to Android System
         * then only the system can resolve and check for given ACTION in the Manifest Files
         * of all the Apps and if any App have the same ACTION(ACTION_IMAGE_CAPTURE) in their
         * Manifest <Intent-Filter></Intent-Filter> Tag then that Application will be in the
         * pickup dialog below the screen, which have all the applications related to "ACTION_IMAGE_CAPTURE"
         *
         * -> Usually we pass data with the specified ACTION, so that ACTION and DATA
         * combination can help Android System to Resolve the Specified Activity from
         * different apps, like:
         *         Intent intent = new Intent(this, ACTION_VIEW);
         *         intent.setData(Uri.parse("https://....."));
         *
         * -> Now if any App present in the mobile has the <Intent-Filter></Intent-Filter>
         * which has "Action = ACTION_VIEW" and "Data = Scheme: "https", that Activity will
         * be present in the Pickup dialog that means, whoever made that App know that which
         * Activity can Handle "ACTION_VIEW" with the "https" Data Scheme.
         *
         * Explained:
         *      1. https://www.tutlane.com/tutorial/android/android-implicit-intents-with-examples
         *
         *      2. http://www.vogella.com/tutorials/AndroidIntent/article.html*/
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            /**Create the temporary File where the photo should go, File is the Abstract
             * Path, System independent. More explaination about file in BitmapUtils class.*/
            File photoFile = null;
            try {
                photoFile = BitmapUtils.createTempImageFile(this);
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {

                // Get the path of the temporary file
                mTempPhotoPath = photoFile.getAbsolutePath();

                // Get the content URI for the image file
                Uri photoURI = FileProvider.getUriForFile(this,
                        FILE_PROVIDER_AUTHORITY,
                        photoFile);

                // Add the URI so the camera can store the image
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                // Launch the camera activity
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // If the image capture activity was called and was successful
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // Process the image and set it to the TextView
            processAndSetImage();
        } else {

            // Otherwise, delete the temporary image file
            BitmapUtils.deleteImageFile(this, mTempPhotoPath);
        }
    }

    /**
     * Method for processing the captured image and setting it to the TextView.
     */
    private void processAndSetImage() {

        // Toggle Visibility of the views
        mEmojifyButton.setVisibility(View.GONE);
        mTitleTextView.setVisibility(View.GONE);
        mSaveFab.setVisibility(View.VISIBLE);
        mShareFab.setVisibility(View.VISIBLE);
        mClearFab.setVisibility(View.VISIBLE);

        // Resample the saved image to fit the ImageView
        mResultsBitmap = BitmapUtils.resamplePic(this, mTempPhotoPath);


        // Detect the faces and overlay the appropriate emoji
        mResultsBitmap = Emojifier.detectFacesandOverlayEmoji(this, mResultsBitmap);

        // Set the new bitmap to the ImageView
        mImageView.setImageBitmap(mResultsBitmap);
    }


    /**
     * OnClick method for the save button.
     */
    @OnClick(R.id.save_button)
    public void saveMe() {
        // Delete the temporary image file
        BitmapUtils.deleteImageFile(this, mTempPhotoPath);

        // Save the image
        BitmapUtils.saveImage(this, mResultsBitmap);
    }

    /**
     * OnClick method for the share button, saves and shares the new bitmap.
     */
    @OnClick(R.id.share_button)
    public void shareMe() {
        // Delete the temporary image file
        BitmapUtils.deleteImageFile(this, mTempPhotoPath);

        // Save the image
        String path = BitmapUtils.saveImage(this, mResultsBitmap);
        Log.i(TAG, "shareMe: " + mTempPhotoPath);

        // Share the image
        BitmapUtils.shareImage(this, mTempPhotoPath);
    }

    /**
     * OnClick for the clear button, resets the app to original state.
     */
    @OnClick(R.id.clear_button)
    public void clearImage() {
        // Clear the image and toggle the view visibility
        mImageView.setImageResource(0);
        mEmojifyButton.setVisibility(View.VISIBLE);
        mTitleTextView.setVisibility(View.VISIBLE);
        mShareFab.setVisibility(View.GONE);
        mSaveFab.setVisibility(View.GONE);
        mClearFab.setVisibility(View.GONE);

        // Delete the temporary image file
        BitmapUtils.deleteImageFile(this, mTempPhotoPath);
    }
}
