package com.example.rina.movieapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RINA on 4/25/2016.
 */
public class MovieAdapter extends BaseAdapter {

    private Context mContext;
    private List <Movie> listOfMovies;
    private LayoutInflater layoutinflate;

    public MovieAdapter(Context c) {
        this.mContext = c;
        this.layoutinflate = LayoutInflater.from(c);
        this.listOfMovies = new ArrayList();
    }

    public int getCount() {

        return listOfMovies.size();
    }

    public Object getItem(int position) {

        return listOfMovies.get(position);
    }

    public long getItemId(int position) {

        return 0;
    }

    public void moviesList (List <Movie> movies){

        this.listOfMovies.clear();

        this.listOfMovies.addAll(movies);
        notifyDataSetChanged();

    }
    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView movView ;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            convertView = layoutinflate.inflate(R.layout.movie_adapter, parent, false);
            movView = (ImageView) convertView.findViewById(R.id.movieadapter);

        } else {
            movView = (ImageView) convertView;
        }

        Picasso.with(mContext).load("http://image.tmdb.org/t/p/w185/" + listOfMovies.get(position).getPosterPath()).into(movView);

        movView.setScaleType(ImageView.ScaleType.FIT_XY);
        return movView;
    }

}
