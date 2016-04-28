package com.example.rina.movieapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity implements MovieInterface {

    boolean twoPane;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.settings_id){
            Intent setting_intent = new Intent(this,SettingsActivity.class);
            startActivity(setting_intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FrameLayout detailLayout = (FrameLayout) findViewById(R.id.secFragment);


        if ( detailLayout == null ){

            twoPane = false;
            //Log.d("Pane is", "one pane");
        }
        else {

            twoPane = true;
            //Log.d("Pane is", "two pane");
        }
    }

    @Override
    public void selectedMovie(Movie m) {
        if ( twoPane == true ) {

            Bundle b = new Bundle();
            b.putSerializable("Movie_Key", m);

            DetailedFragment detailFragment = new DetailedFragment();
            detailFragment.setArguments(b);

            getFragmentManager().beginTransaction()
                    .add(R.id.secFragment, detailFragment)
                    .commit();
        }
        else {

            Intent intent = new Intent(this, MovieDetails.class);
            intent.putExtra("Movie_Key", m);
            startActivity(intent);
        }
    }
}
