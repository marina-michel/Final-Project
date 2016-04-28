package com.example.rina.movieapplication;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by RINA on 4/25/2016.
 */
public class DetailedFragment extends Fragment {

    List<Trailer> trailers;
    TrailerAdapter trailer_adapter;
    ListView trailer_list;

    List<Review> reviews;
    ReviewAdapter review_adapter;
    ListView review_list;

    public Movie mov;
    public static Set<String> favMovies;

    public DetailedFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getArguments();
        mov = (Movie) extras.getSerializable("Movie_Key");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.detailed_fragment, container, false);

        TrailerApi tapi = new TrailerApi();
        tapi.execute(mov.getId());

        ReviewApi rapi = new ReviewApi();
        rapi.execute(mov.getId());

        trailer_list = (ListView) view.findViewById(R.id.movie_trailer);
        trailer_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(
                        "http://www.youtube.com/watch?v=" + trailer_adapter.getItem(position).getKey()))
                );
            }
        });

        review_list = (ListView) view.findViewById(R.id.movie_review);

        TextView movieName = (TextView) view.findViewById(R.id.movie_name);
        TextView movieYear = (TextView) view.findViewById(R.id.movie_year);
        TextView movieRating = (TextView) view.findViewById(R.id.movie_rating);
        TextView movieOverview = (TextView) view.findViewById(R.id.movie_overview);

        ImageView movieImage = (ImageView) view.findViewById(R.id.movie_image);

        ImageView favouriteMovie = (ImageView) view.findViewById(R.id.favourite);
        favouriteMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (favMovies == null)
                    favMovies = new HashSet<String>();

                try {
                    favMovies.add(mov.toJson().toString());
                    Log.d("favorite", favMovies.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                PreferenceManager.getDefaultSharedPreferences(getActivity())
                        .edit()
                        .putStringSet("fav_key", favMovies)
                        .commit();

                favMovies = PreferenceManager.getDefaultSharedPreferences(getActivity()).getStringSet("fav_key", null) ;
                Log.d("favorite2", favMovies.toString());

            }
        });
        Log.d("Movie", mov.getMovieTite());

        movieName.setText(mov.getMovieTite());
        movieRating.setText(mov.getVoteAverage());
        movieOverview.setText(mov.getOverView());
        movieYear.setText(mov.getReleaseDate());

        Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w185/" + mov.getPosterPath()).into(movieImage);

        return view;

    }

    public class TrailerApi extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
//            Log.d("Movies Size", trailers.size() + "");
            trailer_adapter = new TrailerAdapter(getActivity(), trailers);
            trailer_list.setAdapter(trailer_adapter);
        }

        @Override
        protected Void doInBackground(String... params) {

            trailers = null;

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

                final String URL = "http://api.themoviedb.org/3/movie/" + params[0] + "/videos?";

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

                trailers = getTrailerDetails(resultJSON);
            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {

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
            return null;
        }

        public List<Trailer> getTrailerDetails(String json) throws JSONException {

            List<Trailer> trailers = new ArrayList<>();

            final String name = "name";
            final String key = "key";

            JSONObject ob = new JSONObject(json);
            JSONArray jarray = ob.getJSONArray("results");

            for (int i = 0; i < jarray.length(); i++) {

                JSONObject t = jarray.getJSONObject(i);

                Trailer trailer = new Trailer();

                trailer.setName(t.getString(name));
                trailer.setKey(t.getString(key));


                trailers.add(trailer);
            }
            Log.d("here", "length=" + trailers.size());
            return trailers;
        }
    }


    public class ReviewApi extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
//            Log.d("reviews" , reviews.size() + "");
            review_adapter = new ReviewAdapter(getActivity(), reviews);
            review_list.setAdapter(review_adapter);
        }

        @Override
        protected Void doInBackground(String... params) {

            reviews = null;

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

                final String URL = "http://api.themoviedb.org/3/movie/" + params[0] + "/reviews?";

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

//                Log.d("Movies in JSON", "JSON = " + resultJSON);

                reviews = getReviewDetails(resultJSON);
            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {

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
            return null;
        }

        public List<Review> getReviewDetails(String json) throws JSONException {

            List<Review> reviews = new ArrayList<>();

            final String authorName = "author";
            final String reviewContent = "content";

            JSONObject ob = new JSONObject(json);
            JSONArray jarray = ob.getJSONArray("results");

            for (int i = 0; i < jarray.length(); i++) {

                JSONObject t = jarray.getJSONObject(i);

                Review review = new Review();

                review.setAuthor(t.getString(authorName));
                review.setContent(t.getString(reviewContent));


                reviews.add(review);
            }
            Log.d("here", "length=" + reviews.size());
            return reviews;
        }
    }
}



