package bhavya.me.architecture_comp;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/*Data Access Object it's meant to be a Interface or Abstract class in which we have
* unimplemented methods which then be Implemented automatically be RoomDatabase.
* OR we can say that these methods will be converted to Queries which then executed on the
* Database.
*
* It's where Room shines, it's a ORM(Object Relational Mapping), where Methods will be
* converted to Queries.
*
* We have @Insert, @Query, @Delete, @Update: which then be reused to make our custom queries or
* use use same as it is like @Insert, @Delete*/

@Dao
public interface WordDao {

    @Insert
    void insertTask(Word word);

    @Query("SELECT * FROM word_table")
    LiveData<List<Word>> getAllWords();

    @Query("DELETE FROM word_table")
    void deleteAllWords();
}
