package bhavya.me.architecture_comp;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/*This is the Entity class, it has all the columns which we want in our table and with
* Respective Getters and Setters which is meant to be Public so that Room can access these
* methods to perform Action.
*
* NOTE: Every Table in the Database has @Entity class which represent the Table in the Database.*/

@Entity(tableName = "word_table")
public class Word {

    /*If we have Id then we also can Autogenerate with every new Insertion, so we don't have
    * to pass Id manually by:
    *       @PrimaryKey(autogenerate = true)*/
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "word")
    private String mWord;

    public Word(String word) {this.mWord = word;}

    public String getWord(){return this.mWord;}
}