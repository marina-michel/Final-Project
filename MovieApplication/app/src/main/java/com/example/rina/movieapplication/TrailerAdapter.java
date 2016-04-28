package com.example.rina.movieapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by RINA on 4/26/2016.
 */
public class TrailerAdapter extends ArrayAdapter<Trailer> {
    Context context;
    List<Trailer> mov_trailer;
    LayoutInflater layinflater;

    public TrailerAdapter(Context context, List<Trailer> objects) {

        super(context, 0, objects);

        this.context=context;
        this.mov_trailer=objects;
        this.layinflater=LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = layinflater.inflate(R.layout.trailer_item,parent,false);
        TextView trailer_name = (TextView) v.findViewById(R.id.trailer_name);
        trailer_name.setText(mov_trailer.get(position).getName());

        return v;
    }

    @Override
    public int getCount() {
        return mov_trailer.size();
    }

    @Override
    public Trailer getItem(int position) {
        return mov_trailer.get(position);
    }
}
