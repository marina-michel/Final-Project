package com.example.rina.movieapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by RINA on 4/27/2016.
 */
public class ReviewAdapter extends ArrayAdapter<Review> {
    Context context;
    List<Review> mov_review;
    LayoutInflater reviewinflater;

    public ReviewAdapter(Context context, List<Review> objects) {

        super(context, 0, objects);

        this.context=context;
        this.mov_review=objects;
        this.reviewinflater=LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = reviewinflater.inflate(R.layout.review_item,parent,false);
        TextView author = (TextView) v.findViewById(R.id.author_name);
        author.setText(mov_review.get(position).getAuthor());

        TextView content = (TextView) v.findViewById(R.id.review_content);
        content.setText(mov_review.get(position).getContent());

        return v;
    }

    @Override
    public int getCount() {
        return mov_review.size();
    }

    @Override
    public Review getItem(int position) {
        return mov_review.get(position);
    }
}
