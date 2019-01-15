package bhavya.me.architecture_comp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/*Adapter of the RecyclerView which can do all the Backend work like: Caching of Views and
* Binding the Views with the Data. Caching of the Views where RecyclerView Shines.
*
* Adapter(RecyclerView.Adapter) has:
*
*        -> ViewHolder(RecyclerView.ViewHolder) inner class for Caching the Views.
*
*        -> onCreateViewHolder() which is responsible for Inflating the Layout of an Item View and
*        also for creating the ViewHolder Object. Then ViewHolder will manage and Cache the View.
*
*        -> onBindViewHolder() which will Populate or Bind the Cached Views with the Data from the
*        DataSource(List, Array, Json, anything)
*
*        -> getItemCount() This will tell the RecyclerView that how many items we have in the
*        DataSource.
*
* */

public class WordListAdapter extends RecyclerView.Adapter<WordListAdapter.WordViewHolder> {

    class WordViewHolder extends RecyclerView.ViewHolder{

        private TextView wordItemView;

        //Helps in chaching the views for future use.
        public WordViewHolder(@NonNull View itemView) {
            super(itemView);
            wordItemView = itemView.findViewById(R.id.textView);
        }
    }
    private LayoutInflater inflater;
    private List<Word> mWords;

    public WordListAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    //Inflating the Particular Item layout and Creating the ViewHolder Object for Caching.
    @NonNull
    @Override
    public WordListAdapter.WordViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.recyclerview_item, viewGroup, false);
        return new WordViewHolder(view);
    }

    //Get cached views from the ViewHolder and bind the different data everytime.
    @Override
    public void onBindViewHolder(@NonNull WordListAdapter.WordViewHolder wordViewHolder, int i) {
        if(mWords != null){
            Word currentWord = mWords.get(i);
            wordViewHolder.wordItemView.setText(currentWord.getWord());
        }
        else {
            wordViewHolder.wordItemView.setText("No Word");
        }
    }

    //Custom made method for setting or Updating the Data in the Adapter.
    public void setWords(List<Word> words){
        this.mWords = words;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // mWords has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (mWords != null)
            return mWords.size();
        else return 0;
    }
}
