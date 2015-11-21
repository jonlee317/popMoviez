package net.gingz.popmoviez;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.MovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();

        if (intent != null && intent.hasExtra(MovieFragment.EXTRA_TITLE)){
            String title = intent.getStringExtra(MovieFragment.EXTRA_TITLE);
            TextView textView = (TextView) findViewById(R.id.title_text_view);
            textView.setText(title);
        }

        if (intent != null && intent.hasExtra(MovieFragment.EXTRA_IMAGE)){
            ImageView imageView = (ImageView) findViewById(R.id.movie_image_view);

            String picture = intent.getStringExtra(MovieFragment.EXTRA_IMAGE);

            Picasso.with(this)
                    .load(picture)
                    .into(imageView);
        }

        if (intent != null && intent.hasExtra(MovieFragment.EXTRA_PLOT)) {
            String plot = intent.getStringExtra(MovieFragment.EXTRA_PLOT);
            TextView textView = (TextView) findViewById(R.id.plot_text_view);
            textView.setText(plot);
        }

        if (intent != null && intent.hasExtra(MovieFragment.EXTRA_VOTE)) {
            String vote = intent.getStringExtra(MovieFragment.EXTRA_VOTE);
            TextView textView = (TextView) findViewById(R.id.vote_text_view);
            textView.setText("Rating: " + vote + "/10.0");
        }

        if (intent != null && intent.hasExtra(MovieFragment.EXTRA_RELEASE)) {
            String release = intent.getStringExtra(MovieFragment.EXTRA_RELEASE);
            TextView textView = (TextView) findViewById(R.id.release_text_view);
            textView.setText("Release Date: " + release);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


}
