package com.example.rina.movieapplication;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by RINA on 4/25/2016.
 */
public class MovieFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private MovieAdapter movie_adapter ;
    private List<Movie> movies_list ;
    private OnFragmentInteractionListener mListener;
    private MovieInterface movieInterface ;

    public MovieFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MovieFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MovieFragment newInstance(String param1, String param2) {
        MovieFragment fragment = new MovieFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.movieInterface = (MovieInterface) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public void onStart() {
        super.onStart();

        SharedPreferences shpref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String choice = shpref.getString("pref_key", "popular");

        DetailedFragment.favMovies = shpref.getStringSet("fav_key", null);
        Log.d("here is mine", choice);

        if (choice.equals("favourite")) {
            Log.d("here is mine", "ay 7aga");

            if (DetailedFragment.favMovies != null) {
                Log.d("favorite234", DetailedFragment.favMovies.toString());

                try {

                    movies_list = getFavouriteMovie(DetailedFragment.favMovies.toString());
                    movie_adapter.moviesList(movies_list);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        } else {

            MovieApi mapi = new MovieApi();
            mapi.execute(choice);
        }
    }
    public List<Movie> getFavouriteMovie(String json) throws JSONException {

        List<Movie> movie = new ArrayList<>();

        final String path = "poster_path";
        final String overview = "overview";
        final String movietitle = "original_title";
        final String releasedate = "release_date";
        final String voteaverge = "vote_average";
        final String id = "id";


        JSONArray jarray =new JSONArray(json);

        for(int i=0; i<jarray.length(); i++){

            JSONObject m = jarray.getJSONObject(i);

            Movie mov = new Movie();

            mov.setMovieTite(m.getString(movietitle));
            mov.setOverView(m.getString(overview));
            mov.setPosterPath(m.getString(path));
            mov.setReleaseDate(m.getString(releasedate));
            mov.setVoteAverage(m.getString(voteaverge));
            mov.setId(m.getString(id));

            movie.add(mov);
        }
        Log.d("here","length=" + movie.size());
        return movie;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movie, container, false);
        GridView gview = (GridView) view.findViewById(R.id.gridview);
        movie_adapter = new MovieAdapter(getActivity());
        gview.setAdapter(movie_adapter);
        gview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie m = (Movie) movie_adapter.getItem(position);
                movieInterface.selectedMovie(m);
            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public class MovieApi extends AsyncTask<String,Void,List<Movie>> {

        @Override
        protected void onPostExecute(List<Movie> movies) {
            super.onPostExecute(movies);
            Log.d("Movies Size", movies.size() + "");
            movie_adapter.moviesList(movies);
        }

        @Override
        protected List<Movie> doInBackground(String... params) {

            List<Movie> movies = null;

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String resultJSON = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast

                final String URL = "http://api.themoviedb.org/3/movie/"+ params[0] +"?";

                final String key = "api_key";
                final String apiKey = "";

                Uri builtUri = Uri.parse(URL).buildUpon()
                        .appendQueryParameter(key, apiKey)
                        .build();

                Log.d("URL", "JSON = " + builtUri.toString());

                java.net.URL url = new URL(builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }

                resultJSON = buffer.toString();

                Log.d("Movies in JSON", "JSON = " + resultJSON);

                movies = getMovieDetails(resultJSON);
            }
            catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            finally{

                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }

            return movies;
        }

        public List<Movie> getMovieDetails(String json) throws JSONException {

            List<Movie> movie = new ArrayList<>();

            final String path = "poster_path";
            final String overview = "overview";
            final String movietitle = "original_title";
            final String releasedate = "release_date";
            final String voteaverge = "vote_average";
            final String id = "id";

            JSONObject ob = new JSONObject(json);
            JSONArray jarray = ob.getJSONArray("results");

            for(int i=0; i<jarray.length(); i++){

                JSONObject m = jarray.getJSONObject(i);

                Movie mov = new Movie();

                mov.setMovieTite(m.getString(movietitle));
                mov.setOverView(m.getString(overview));
                mov.setPosterPath(m.getString(path));
                mov.setReleaseDate(m.getString(releasedate));
                mov.setVoteAverage(m.getString(voteaverge));
                mov.setId(m.getString(id));

                movie.add(mov);
            }
            Log.d("here","length=" + movie.size());
            return movie;
        }
    }}
