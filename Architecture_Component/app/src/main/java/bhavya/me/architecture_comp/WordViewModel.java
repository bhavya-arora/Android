package bhavya.me.architecture_comp;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

/*-> This is the class which is Responsible for Caching the LiveData with the Data in the LiveData,
* so in short ViewModel is responsible for Caching the data in itself and survive Configuration
* changes.*/

/*-> We can also Extend it from ViewModel if we want custom ViewModel, or if we want Custom(Extra)
* Data in the Constructor of the ViewModel, Like in this case we don't want anything in the
* constructor of ViewModel that's why we used already implemented "AnroidViewModel"
*
* -> Remember one thing that AndroidViewModel will use already implemented Factory called
* AndroidViewModelProvider factory for creation.
*
* If we want to get extra custom data in the construtor of ViewModel then we can make custom
* factory by extending the "ViewModelProvider.NewInstanceFactory" and @override create() method
* of Factory and give our custom logic for creation of our custom ViewModel.
*
* Then we pass "ViewModelProviders.of(this, customFactory)", so that ViewModelProvider will
* then use our custom Factory create() method for creation of ViewModel.
*
* NOTE: "AndroidViewModel" use Application Context, it's already implemented so we don't have
* to worry for getting Application context.*/
public class WordViewModel extends AndroidViewModel {

    private LiveData<List<Word>> mWords;
    private WordRepository mWordRepository;

    public WordViewModel(@NonNull Application application) {
        super(application);
        mWordRepository = new WordRepository(application);
        mWords = mWordRepository.getWords();
    }

    public LiveData<List<Word>> getmWords(){
        return mWords;
    }

    /*See we are using Repository to do Insert Operation and this ViewModel don't even know
    * that what we are using in the backend like: Room, Sqlite, REST(Server), we just
    * want to perform insert operation, now Repository will take of everything.
    *
    * But one thing to remember that ViewModel only want LiveData wrapped over data in return.*/
    public void insert(Word word){
        mWordRepository.insert(word);
    }
}
