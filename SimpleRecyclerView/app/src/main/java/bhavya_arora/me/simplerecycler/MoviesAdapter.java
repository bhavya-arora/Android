package bhavya_arora.me.simplerecycler;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MyViewHolder> {

    private List<Movie> movieList;
    //Final Variable for Interface
    final private ListItemClickListerner mOnClickListener;


    public interface ListItemClickListerner{
        void onItemClick(String itemClickedName);
    }

    //ViewHolder Class
    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView title, genre, year;

        public MyViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            genre = itemView.findViewById(R.id.genre);
            year = itemView.findViewById(R.id.year);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Movie movie = movieList.get(position);
            mOnClickListener.onItemClick(movie.getTitle());
        }
    }

    public MoviesAdapter(List<Movie> movieList, ListItemClickListerner listerner) {
        this.movieList = movieList;
        mOnClickListener = listerner;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_list_row,
                parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Movie movie = movieList.get(position);
        holder.title.setText(movie.getTitle());
        holder.genre.setText(movie.getGenre());
        holder.year.setText(movie.getYear());



    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }


}
