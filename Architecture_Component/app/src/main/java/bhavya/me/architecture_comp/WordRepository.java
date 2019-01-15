package bhavya.me.architecture_comp;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

/*Repository Class is Responsible for Accessing the Database or any source for getting the Data.
* so, then we don't have to change the fetching Implementation to change everywhere.
*
* It makes our app more testable and modular, Now all the Data fetching logic will be in here,
* and we can fetch the data from the Database(Room) or From REST(Server). ViewModel will
* use this Repository class for getting the data, ViewModel don't want to know from where
* the Data comes from, ViewModel Just need "LiveData" like:
*                             -> Livedata<Data>
*
* Now in future if we want to remove the Database(Room) and want to add Different Database then,
* we just have to change the Repository Logic.
*
* NOTE:
*      1. We can use Both local and Server Datasource for getting data and this logic will just
*      be in this Repository.
*
*      2. Never Pass The Activity Context to the Singleton(WordRoomDatabase), because it will
*      be a memory leak, because Singleton has Lifecycle associated with Application.*/

public class WordRepository {

    private LiveData<List<Word>> words;
    private WordRoomDatabase mDatabase;
    private WordDao mWordDao;

    public WordRepository(Application application) {
        mDatabase = WordRoomDatabase.getDatabase(application);
        mWordDao = mDatabase.wordDao();
        words = mWordDao.getAllWords();
    }

    LiveData<List<Word>> getWords(){
        return words;
    }

    public void insert(Word word){
        new asyncTaskDb(mWordDao).execute(word);
    }

    /*Always perform the Database calls off the Thread, that's we used AsyncTask, and now insertion
    * is being done off the Thread. But if we are Fetching with the help of LiveData then
    * that will be automatically be done off the main thread.*/
    private static class asyncTaskDb extends AsyncTask<Word, Void, Void>{
        WordDao dao;

        asyncTaskDb(WordDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(Word... words) {
            dao.insertTask(words[0]);
            return null;
        }
    }
}
