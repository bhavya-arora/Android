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

package com.example.android.android_me.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.android.android_me.R;
import com.example.android.android_me.data.AndroidImageAssets;

/**SOURCE:
 * 1. This Example: https://in.udacity.com/course/advanced-android-app-development--ud855
 *
 * 2.Best Explaination: https://developer.android.com/guide/components/fragments
 *
 * 3.Other Fragment Explaination: http://www.vogella.com/tutorials/AndroidFragments/article.html
 *
 * OTHER(SCREEN_COMPATIBILITY):
 *        1. https://developer.android.com/guide/practices/screens_support
 *        2. https://developer.android.com/training/multiscreen/screendensities
 *        3. https://developer.android.com/guide/topics/resources/providing-resources
 *        4. https://developer.android.com/training/multiscreen/screensizes
 *
 * */

/**This Activity consist of two types of Layouts
*      1. If Device is under sw600dp
*      2. If the Device Screen greater than sw600dp
*
* and it will reponse as per the screen size, but the basic concept behind
* is ""MASTER-DETAIL"" flow, Baically Master has List/Grid of Items and user
* select from the Layout and Detail Layout will Show the Deatils of Selected item.
*
* Question - But How it will show Layout as per the screen size?
*
* -> 1. First if Screen < sw600dp:
*       Now as we know screen size is smaller than sw600dp and screen doesn't have enough
*       space to show both Fragments(Master and Detail) on same screen side by side.
*       So, what we can do is make a new Activity called "AndroidMeActivity" for
*       Detail View. Now whenever we click the item from the Master View(MasterListFragment)
*       then the position will be send from the MasterListFragment to the MainActivity.
*       and the this position will be send to "AndroidMeActivity" through Intent Bundle.
*       Then "AndroidMeActivity" will make Fragments to show in Detail View dynamically.
*
*    2. Second if Screen > sw600dp:
*       Now as we know screen is bigger than sw600dp so now we can show 2 fragments
*       that is Master(MasterListFragment(Static Fragment)) and Detail(BodyPartFragment(with FrameLayout Container))
*       so Now we create new Layout file for sw600dp called "activity_main.xml(sw600dp)"
*       and this layout file will be used when the screen size will be greater than sw600dp,
*       and now we can replace the particular bodyPartFragment as per the selection from the
*       MasterView.
*
* NOTE:
*    1. Master View(MasterListFragment) is a static Fragment, we included in the
*    MainActivity with the "<fragment>" tag and a property called "name=.MasterListFragment"
*    which tell MainActivity to use MasterListFragment class in this <fragment> tag
*    statically.*/

// Implement the MasterListFragment callback, OnImageClickListener
public class MainActivity extends AppCompatActivity implements MasterListFragment.OnImageClickListener{

    public static final String TAG = "MainActivity";

    /**These variables used for storing the Positions of specific bodyPart
    * like: For Head: We have "headIndex"
    *       For Body: We have "bodyIndex"
    *       For Leg:  We have "legIndex"
    *
    * And One thing to keep in mind that if we are in "smaller Screen > 600dp"
    * and we go to AndroidMeActivity with the Intent and return back to "MainActivity"
    * even after returning these variable do have same values. Because these Associated
    * with Activity/Class members.*/
    private int headIndex;
    private int bodyIndex;
    private int legIndex;

    /**This Variable store the boolean that if the device is greater than sw600dp or not
    * and if greater than it consist of true.*/
    private Boolean biggerScreen;

    Button nextButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Extra - These lines will give us Height and Widht in dp's(Display Independent Pixels)
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        //Inflating Views or initializing
        nextButton = (Button) findViewById(R.id.next_button);

        /**This will give us Boolean from the bools.xml value file and we defined
        * two bools.xml as per to the screen size the boolean value
        * "bigger" comes from the bools.xml as per respective screen size.*/
        biggerScreen = getResources().getBoolean(R.bool.bigger);

        if(biggerScreen){
            nextButton.setVisibility(View.GONE);

            /**Creating Fragment when the MainActivity will launch, so user has
            * something to see on screen and before creating new Fragment we are
            * checking if savedInstanceState is equal to null or other wise if any
            * configuration change like Rotation we don't want to create Fragments
            * and add fragment in the container even when previous fragment is not
            * destroyed.
            *
            * Fragments also like Activities...and also has savedInstanceState in their
            * respective Fragment class, so that we can store variables and small data
            * in Bundle.
            *
            * NOTE: Fragment's SavedInstanceState is only useful when we don't create
            * new Fragment on Screen Rotation.*/
            if(savedInstanceState == null){

                /**BodyPartFragment which extends from Fragment(Support library)
                * , we are instantiating the instance and giving the data to Fragment
                * through setters, and adding in the MainActivity's Layout
                * "activity_main" in the Container(FrameLayout).
                *
                * NOTE: only Fragment class is responsible for inflating their layout
                * in onCreateView() method.*/
                BodyPartFragment headFragment = new BodyPartFragment();
                headFragment.setImageIds(AndroidImageAssets.getHeads());
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .add(R.id.head_container, headFragment)
                        .commit();

                /**Go to "activity_main.xml" and check yourself that we declare 3 different
                * containers for each "BodyPartFragment" which we are gonna add dynamically.*/
                BodyPartFragment bodyFragment = new BodyPartFragment();
                bodyFragment.setImageIds(AndroidImageAssets.getBodies());
                fragmentManager.beginTransaction()
                        .add(R.id.body_container, bodyFragment)
                        .commit();

                BodyPartFragment legFragment = new BodyPartFragment();
                legFragment.setImageIds(AndroidImageAssets.getLegs());
                fragmentManager.beginTransaction()
                        .add(R.id.leg_container, legFragment)
                        .commit();
            }
        }

    }

    /**This is the Interface "MasterListFragment.OnImageClickListener" method comes
    * from MasterListFragment or we can say that it's a way to talk to MainActivity.
    * Whenever any item being clicked in the MasterListFragment fragment class
    * this Methods will get called.*/
    public void onImageSelected(int position) {
        // Create a Toast that displays the position that was clicked
        Toast.makeText(this, "Position clicked = " + position, Toast.LENGTH_SHORT).show();

        /**In AndroidImageAssets class we have 12 Head pics and 12 Body pics and
        * 12 Leg Pics and now on the basis of that data we are calculating the
        * clicked position corresponds to which bodypart: Head, Body Or Leg.*/
        if(position >= 0 && position <= 11){
            headIndex = position;
        }else if (position >= 12 && position <= 23){
            bodyIndex = position - 12;
        }else {
            legIndex = position - 24;
        }

        /**This if block only be executed if and only if screen size will be higher than
        * sw600dp and the layout file MainActivity use is "activity_main.xml(sw600dp)
        * in this case both static(MasterListFragment) and Dynamic(BodyPartFragments)
        * are on the same screen.
        *
        * and one of the 3 BodyPartFrament will get replaced with the newly Created
        * fragment when user click the item in the MasterListFragment.*/
        if(biggerScreen){
            BodyPartFragment replaceFragment  = new BodyPartFragment();

            if(position >= 0 && position <= 11){

                replaceFragment.setImageIds(AndroidImageAssets.getHeads());
                replaceFragment.setListIndex(headIndex);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.head_container, replaceFragment)
                        .commit();

            }else if (position >= 12 && position <= 23){

                replaceFragment.setImageIds(AndroidImageAssets.getBodies());
                replaceFragment.setListIndex(bodyIndex);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.body_container, replaceFragment)
                        .commit();

            }else {

                replaceFragment.setImageIds(AndroidImageAssets.getLegs());
                replaceFragment.setListIndex(legIndex);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.leg_container, replaceFragment)
                        .commit();
            }

            /**And this else block only be executed when screen size is smaller than
            * sw600dp means we don't have enough space on the screen to place both
            * Fragments that is MasterListFagment(Static) and BodyPartFragments(Dynamic),
            * So what we can do is to separate these Master-Detail flow in separate Activities,
            * So now If user select any item from MasterListFragment then that Position will
            * be send to another Activity which will be responsible for creation of
            * BodyPartFragments.*/
        }else {

            /**Put all these varibales in the Bundle and send to AndroidMeActivity
            * Remember one thing that even if this MainActivity will go in backgroud
            * after starting the AndroidMeActivity these variables: headIndex,
            * bodyIndex, legIndex will not get Destroyed and remain in memory.
            * Because these are global variables.*/
            Bundle b = new Bundle();
            b.putInt("headIndex", headIndex);
            b.putInt("bodyIndex", bodyIndex);
            b.putInt("legIndex", legIndex);

            // Attach the Bundle to an intent
            final Intent intent = new Intent(this, AndroidMeActivity.class);
            intent.putExtras(b);

            // The "Next" button launches a new AndroidMeActivity
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(intent);
                }
            });
        }

    }

}
