package bhavya.me.architecture_comp;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

/*This is the Main Class of RoomDatabase. This is the Singleton class and here we
* join the pieces(Entities and Dao)*/

/*Give the database Entities(Tables) and Version of the Database.*/
@Database(entities = {Word.class}, version = 1, exportSchema = false)
public abstract class WordRoomDatabase extends RoomDatabase {

    /*the volatile modifier guarantees that any thread that reads a field will see
    the most recently written value. Because with Variables we cannot use synchronized keyword,
    that's when Volatile comes in.

    -> Singleton classes is reponsible for creating their own instance only one time in Whole
    Application Lifecycle.

    SOURCE: https://stackoverflow.com/questions/106591/do-you-ever-use-the-volatile-keyword-in-java*/
    private static volatile WordRoomDatabase INSTANCE;

    static WordRoomDatabase getDatabase(final Context context){
        if(INSTANCE == null){
            synchronized (WordRoomDatabase.class){
                if(INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            WordRoomDatabase.class, "word_database")
                            .addCallback(new RoomDatabase.Callback(){
                                @Override
                                public void onOpen(@NonNull SupportSQLiteDatabase db) {
                                    super.onOpen(db);
                                    new PopulateAsync(INSTANCE).execute();
                                }
                            })
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    /*We will get Dao(WordDao) instance through the Database Instance(Which is Singleton),
    * so that we don't access the Database with different Object in different Places. Only
    * One Instance through out the Lifecycle of the App is responsible for Accessing
    * the Database.*/
    public abstract WordDao wordDao();

    /*-> This is The Temporary Data we are adding whenever Database INSTANCE is created,
    * and for this Run the App and see that even if we minimize the App and Resume there will
    * be no Database INSTANCE creation.
    *
    * -> Because everytime Database(WordRoomDatabase) INSTANCE is created a callback to this
    * Class will trigger and all the Data in our database will be vanished and only we left
    * with
    *                          -> Hello
    *                          -> World
    *
    * So that will tell us that Database INSTANCE creation only happen only once in a Application
    * Lifecycle.*/
    private static class PopulateAsync extends AsyncTask<Void, Void, Void>{
        private WordDao mDao;

        public PopulateAsync(WordRoomDatabase db) {
            this.mDao = db.wordDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mDao.deleteAllWords();
            Word word1 = new Word("Hello");
            mDao.insertTask(word1);
            Word word2 = new Word("World");
            mDao.insertTask(word2);
            return null;
        }
    }
}
