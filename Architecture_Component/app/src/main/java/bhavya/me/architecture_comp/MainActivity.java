package bhavya.me.architecture_comp;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    //SOURCE: https://codelabs.developers.google.com/codelabs/android-room-with-a-view
    //***************************EXPLAINED BY ME*******************************

    private static final String TAG = "MainActivity";

    private WordViewModel viewModel;
    public static final int NEW_WORD_ACTIVITY_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Using Recycler View in the MainActivity for showing the list of words.
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final WordListAdapter adapter = new WordListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        /*-> We want VieModel Associated with this Activity(MainActivity), this context
        * ViewModelProviders.of() use for Creating or getting the already created ViewModel
        * and it will generate ViewModelProvider with the Parameters
        *      @ViewModelStore
        *      @Factory
        *
        * -> Now ViewModelProvider has both the parameters on which it's dependent on.
        * @ViewModelStore: Associated with given context and it's functionality is
        * to store ViewModel with the help of HashMap.
        * @Factory: If ViewModelStore doesn't have existing ViewModel then ViewModelProvider
        * use Factory to create ViewModel by calling create() method of Factory(that's where
        * logic exist of creation of ViewModel).
        *
        * You may be Wondering what's the Factory?
        * -> Creating of ViewModelProvider and ViewModel has done by "Creational" design pattern,
        * and Factory object creation approach they used. For abstraction.
        *
        * -> Note that we can pass our own custom Factory by:
        *           -> ViewModelProviders.of(this, customFactory)
        *                    INSTEAD OF
        *           -> ViewModelProviders.of(this)
        *
        * -> Just Look inside the method "of(this)":
        *
        *       public static ViewModelProvider of(@NonNull FragmentActivity activity,
        *       @Nullable Factory factory) {
        *            if (factory == null) {
        *               factory = ViewModelProvider.AndroidViewModelFactory.getInstance(application);
        *            }
        *            return new ViewModelProvider(ViewModelStores.of(activity), factory);
        *       }
        *
        *       -> It's the Implementation if look caefully then we can notice that Factory is Mandatory,
        *       if we pass our custom Factory then it will be good and if not then it will use
        *       already implemented Factory called "AndroidViewModelFactory" for creation of ViewMode,
        *       which has Application Context.
        *
        *  NOTE: We can make our Custom Factory by Extending from "ViewModelProvider.NewInstanceFactory"
        *  and can override create() method, with our code.
        *                  */
        viewModel = ViewModelProviders.of(this).get(WordViewModel.class);

        /*-> We have RoomDatabase(WordRoomDatabase) class and a Dao(WordDao) which responsible for
        * executing queries on Database and Dao has a method called "getAllWords()" which return
        * List of Words in the database wrapped into LiveData.
        *
        * -> On this LiveData we can set a observer which will be called if there is any change in
        * the Database. LiveData is very useful so that we don't have to query the database
        * again again for checking if there is any change. But LiveData object we Instantiate in
        * onCreate(), so whenever screen Rotated then new Activity been created then again we want to
        * Query the Database, and it's redundant.
        *
        * -> Then viewModel comes into Party and wrap the LiveData under ViewModel, so when to call
        * the Database will decided by ViewModel, and now if Configuration change even then ViewModel
        * don't destroy and has all the Data even after screen being Rotated.
        *
        * -> But in ViewModel we cannot access Database, we can access but it's not nice way to access
        * any DataSource, instead we use "Repositiory" for this dirty work and then Repositiory
        * will be Reposible for accessing Database and Dao and perform any Database Access or
        * any Network Operation( like fetching Data from REST API's)
        *
        * So it's like:
        *
        *     VIEWMODEL ----------> REPOSITORY ----------> DAO ------(EXECUTE QUERIES)----> ACCESS DATABASE.
        *                                      ----------> NETWORK OPERATION(REST API) -----> ANY DATABASE
        *
        *                                      RETURN:
        *
        *     VIEWMODEL <--(LIVEDATA)--- REPOSITORY <---(LIVEDATA)--- DAO ---(EXECUTE QUERIES)--> DATABASE(ROOM)
        *                                           <---(LIVEDATA)--- REST API's
        *
        *
        * NOTE:
        *      1. Our Data will be wrapped under LiveData and LiveData is wrapped under ViewMode.
        *
        *      2. LiveData is LifeCycle Aware Component, that means it will invoke their Subscribed
        *      Observers only when the Observer Activity(MainActivity) is in RESUME or START state.
        *
        *      3. (IMPORTANT) We often want to off load the Database Execution to another Thread but with
        *      LivaData we don't want to take care of that manually, LiveData by Default perform
        *      Operations off the Thread. But if Database Operations are performed off the Main
        *      thread and if Activity is destroyed then it may lead to Memory leak and That's when
        *      ViewModel help us.
        *         */
        viewModel.getmWords().observe(this, new Observer<List<Word>>() {

            /*It's the Observer method associated with LiveData which is being called when there
            * will be any change in the Underlying Database and Requery the Database and then
            * we get List of Words, the same data we wrapped under LiveData and tell LiveData to
            * observe this Data(List of Words(List<Word>))*/
            @Override
            public void onChanged(@Nullable List<Word> words) {

                /*It's Custom method we made for our Convenience in the RecyclerView Adapter( Because Adapter
                is Responsible for Binding the Views with Data from DataSource), Then call
                this method to change the Data in the RecyclerView and then our UI will be
                updated with new Data.*/
                adapter.setWords(words);
            }
        });

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_WORD_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Word word = new Word(data.getStringExtra(NewWordActivity.EXTRA_REPLY));
            viewModel.insert(word);
        } else {
            Toast.makeText(
                    getApplicationContext(),
                    R.string.empty_not_saved,
                    Toast.LENGTH_LONG).show();
        }
    }

    /*This is the onClick() method which will trigger when FAB button clicked.*/
    public void startAddWord(View view) {
        Intent intent = new Intent(MainActivity.this, NewWordActivity.class);

        /*We are starting the Activity for getting result back, if EditText of "NewWordActivity"
        * has text and then if click save button then, resultCode = RESULT_OK will be send
        * back in the MainActivity onActivityResult() method with the text, and then
        * we are creatng new Word object and perform insertion in the Database.*/
        startActivityForResult(intent, NEW_WORD_ACTIVITY_REQUEST_CODE);
    }
}
