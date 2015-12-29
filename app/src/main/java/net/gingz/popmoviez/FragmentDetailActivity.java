package net.gingz.popmoviez;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.SyncStateContract;
import android.widget.CheckBox;
import android.widget.LinearLayout;


import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by ahjornz on 12/7/15.
 */
public class FragmentDetailActivity extends Fragment {

    SQLiteDatabase mDb;

    String TITLE_KEY = "title_key";
    String RELEASE_KEY = "release_key";
    String IMAGE_KEY = "image_key";
    String PLOT_KEY = "plot_key";
    String VOTE_KEY = "vote_key";
    String MOVIE_ID_KEY = "movie_id_key";

    String title;
    String release;
    String picture;
    String plot;
    String vote;
    String movieId;
    String youtubeLink;
    String[] youTubeLinkNames;
    String[] youTubeLinks;
    String[] movieReviews;
    String[] movieAuthors;

    TextView textViewTitle;
    TextView textViewPlot;

    LinearLayout ll;
    LinearLayout.LayoutParams lp;
    LinearLayout.LayoutParams lineParams;

    private final String LOG_TAG = FragmentDetailActivity.class.getSimpleName();


    public void setTitle(String title) {
        this.title = title;
    }

    public void setRelease(String release) {
        this.release = release;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public void setVote(String vote) {
        this.vote = vote;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    private void updateTrailers() {
        FetchTRData trailData = new FetchTRData();
        trailData.execute();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            title = savedInstanceState.getString(TITLE_KEY);
            release = savedInstanceState.getString(RELEASE_KEY);
            picture = savedInstanceState.getString(IMAGE_KEY);
            plot = savedInstanceState.getString(PLOT_KEY);
            vote = savedInstanceState.getString(VOTE_KEY);
            movieId = savedInstanceState.getString(MOVIE_ID_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_detail_activity, container, false);

        final SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();

        final CheckBox checkBox = (CheckBox) rootView.findViewById(R.id.star_checkbox);

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()) {
                    addToFavorites(sharedPref, editor);

                } else {
                    removeFromFavorites(sharedPref, editor);
                }
            }
        });

        textViewTitle = (TextView) rootView.findViewById(R.id.title_text_view);
        TextView textViewRelease = (TextView) rootView.findViewById(R.id.release_text_view);
        ImageView imageView = (ImageView) rootView.findViewById(R.id.movie_image_view);
        textViewPlot = (TextView) rootView.findViewById(R.id.plot_text_view);
        TextView textViewVote = (TextView) rootView.findViewById(R.id.vote_text_view);

        textViewTitle.setText(title);
        textViewRelease.setText("Release Date: " + release);
        Picasso.with(getActivity())
                .load(picture)
                .into(imageView);
        textViewPlot.setText(plot);
        textViewVote.setText("Rating: " + vote + "/10.0");

        Intent intent = getActivity().getIntent();

        if (intent != null && intent.hasExtra(MovieFragment.EXTRA_TITLE)){
            title = intent.getStringExtra(MovieFragment.EXTRA_TITLE);
            textViewTitle.setText(title);
        }

        if (intent != null && intent.hasExtra(MovieFragment.EXTRA_IMAGE)){
            picture = intent.getStringExtra(MovieFragment.EXTRA_IMAGE);

            Picasso.with(getActivity())
                    .load(picture)
                    .into(imageView);
        }

        if (intent != null && intent.hasExtra(MovieFragment.EXTRA_PLOT)) {
            plot = intent.getStringExtra(MovieFragment.EXTRA_PLOT);
            textViewPlot.setText(plot);
        }

        if (intent != null && intent.hasExtra(MovieFragment.EXTRA_VOTE)) {
            vote = intent.getStringExtra(MovieFragment.EXTRA_VOTE);
            textViewVote.setText("Rating: " + vote + "/10.0");
        }

        if (intent != null && intent.hasExtra(MovieFragment.EXTRA_RELEASE)) {
            release = intent.getStringExtra(MovieFragment.EXTRA_RELEASE);
            textViewRelease.setText("Release Date: " + release);
        }

        if (intent != null && intent.hasExtra(MovieFragment.EXTRA_ID)) {
            movieId = intent.getStringExtra(MovieFragment.EXTRA_ID);
        }

        updateTrailers();

        ll = (LinearLayout) rootView.findViewById(R.id.detail_linear_layout);
        lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.setMargins(16, 16, 16, 0); // lp.setMargins(left, top, right, bottom);

        lineParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lineParams.setMargins(16, 16, 16, 0); // lp.setMargins(left, top, right, bottom);
        lineParams.height = 1;

        keepFavoritesChecked(movieId, sharedPref, checkBox);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(TITLE_KEY, title);
        savedInstanceState.putString(RELEASE_KEY, release);
        savedInstanceState.putString(IMAGE_KEY, picture);
        savedInstanceState.putString(PLOT_KEY, plot);
        savedInstanceState.putString(VOTE_KEY, vote);
        savedInstanceState.putString(MOVIE_ID_KEY, movieId);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private void createTrailerButtons(String[] myLinks, String[] myLinkNames) {
        Button newButton[] = new Button[myLinks.length];

        View line = new View(getActivity());
        line.setLayoutParams(lineParams);
        line.setBackgroundColor(Color.WHITE);

        ll.addView(line);

        TextView message;
        message = new TextView(getActivity());
        message.setTextColor(Color.WHITE);
        message.setTextSize(18);
        message.setLayoutParams(lp);
        message.setText("Trailers");

        ll.addView(message);
        if (myLinks != null) {
            for (int i = 0; i < myLinks.length; i++) {
                if (myLinks != null) {
                    int j = i + 1;
                    newButton[i] = new Button(getActivity());
                    newButton[i].setText(myLinkNames[i]);
                    newButton[i].setLayoutParams(lp);
                    ll.addView(newButton[i]);

                    final Intent youtubeIntent = new Intent(Intent.ACTION_VIEW);

                    youtubeIntent.setPackage("com.google.android.youtube");
                    youtubeIntent.setData(Uri.parse(myLinks[i]));

                    newButton[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(youtubeIntent);
                        }
                    });
                }
            }
        }
    }

    private void createReviews(String[] myAuthors, String[] myReviews) {
        TextView authors[] = new TextView[myAuthors.length];
        TextView reviews[] = new TextView[myReviews.length];


        if (myAuthors.length != 0) {

            for (int i = 0; i < myAuthors.length; i++) {
                int j = i + 1;

                authors[i] = new TextView(getActivity());
                authors[i].layout(16, 16, 100, 100);
                authors[i].setLayoutParams(lp);
                authors[i].setTextColor(Color.WHITE);
                authors[i].setTextSize(18);
                authors[i].setText(myAuthors[i]);


                reviews[i] = new TextView(getActivity());
                reviews[i].setTextColor(Color.WHITE);
                reviews[i].setTextSize(15);
                reviews[i].setLayoutParams(lp);
                reviews[i].setText(myReviews[i]);

                View line = new View(getActivity());
                line.setLayoutParams(lineParams);
                line.setBackgroundColor(Color.WHITE);

                ll.addView(line);
                ll.addView(authors[i]);
                ll.addView(reviews[i]);

            }
        } else {
            TextView message;
            message = new TextView(getActivity());
            message.setTextColor(Color.WHITE);
            message.setTextSize(15);
            message.setLayoutParams(lp);
            message.setText("Please reconnect to internet to see trailers and reviews!");

            ll.addView(message);
        }
    }

    private void keepFavoritesChecked(String movieId, SharedPreferences sharedPref, CheckBox checkBox) {
        String containsItem = sharedPref.getString(movieId, "nothing");

        if (containsItem.equals("nothing")) {
            checkBox.setChecked(false);
        } else {
            checkBox.setChecked(true);
        }
    }

    private void addToFavorites(SharedPreferences sharedPref, SharedPreferences.Editor editor) {

        editor.putString(movieId, picture);
        editor.commit();
        String checkInside = sharedPref.getString(movieId, null);
        sharedPref.getAll();

        Map<String,?> entries = sharedPref.getAll();
        Set<String> keys = entries.keySet();
        for (String key : keys) {
            String checkAgain = sharedPref.getString(key, null);
        }

        // attempt to store data into database
        storeData();
    }

    private void removeFromFavorites(SharedPreferences sharedPref, SharedPreferences.Editor editor) {
        editor.remove(movieId);
        editor.commit();

        Map<String,?> entries = sharedPref.getAll();
        Set<String> keys = entries.keySet();
        for (String key : keys) {

        }
        // attempt to remove data from database
        removeData(title);
    }

    private void storeData() {
        SQLiteOpenHelper movieDbHelper = new MovieDbHelper(getActivity());
        mDb = movieDbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(MovieContract.MovieColumns.COLUMN_NAME_TITLE, title);
        cv.put(MovieContract.MovieColumns.COLUMN_NAME_MOVIE_ID, movieId);
        cv.put(MovieContract.MovieColumns.COLUMN_NAME_PLOT, plot);
        cv.put(MovieContract.MovieColumns.COLUMN_NAME_IMAGE, picture);
        cv.put(MovieContract.MovieColumns.COLUMN_NAME_RELEASE, release);
        cv.put(MovieContract.MovieColumns.COLUMN_NAME_VOTE, vote);

        String trailers = "";

        for (String link : youTubeLinks) {
            trailers += link + " ";
        }

        cv.put(MovieContract.MovieColumns.COLUMN_NAME_TRAILERs, trailers);

        String reviews = "";

        for (String review : movieReviews) {
            reviews += review + "\n\n\n\n";
        }

        cv.put(MovieContract.MovieColumns.COLUMN_NAME_REVIEWS, reviews);

        mDb.insert(MovieContract.MovieColumns.TABLE_NAME, null, cv);
    }

    private void removeData(String input) {
        SQLiteOpenHelper movieDbHelper = new MovieDbHelper(getActivity());
        mDb = movieDbHelper.getWritableDatabase();
        mDb.delete(MovieContract.MovieColumns.TABLE_NAME, MovieContract.MovieColumns.COLUMN_NAME_TITLE + " = ?", new String[]{input});
    }

    public class FetchTRData extends AsyncTask<String, Void, String[][]> {
        // REMEMBER TO ENTER API KEY HERE BEFORE YOU RUN
        // This is the 2nd of two places api-key is needed

        String appId = "< ENTER API KEY >";
        final String APPID_PARAM = "api_key";

        private final String LOG_TAG = FetchTRData.class.getSimpleName();

        int ytdLength;
        int webLength;

        @Override
        protected String[][] doInBackground(String... params) {
            String[][] youTubeData = getYouTubeDataFromWeb(movieId);
            String[][] webData = getReviewDataFromWeb(movieId);

            if (youTubeData != null) {
                ytdLength = youTubeData[0].length;
            } else {
                ytdLength = 1;
            }

            if (webData != null) {
                webLength = webData[0].length;
            } else {
                webLength = 1;
            }

            // to prevent overflow of the lesser item we take the max
            // later on in the onpostexecute we will dump the extra null spaces  :)
            String[][] usefulLinks = new String[4][Math.max(ytdLength, webLength)];

            if (youTubeData != null) {
                for (int i = 0; i < youTubeData[0].length; i++) {
                    usefulLinks[0][i] = youTubeData[0][i];
                }
            }

            if (webData != null) {
                for (int i = 0; i < webData[0].length; i++) {
                    usefulLinks[1][i] = webData[0][i];
                }
            }

            if (webData != null) {
                for (int i = 0; i < webData[1].length; i++) {
                    usefulLinks[2][i] = webData[1][i];
                }
            }

            if (youTubeData != null) {
                for (int i = 0; i < youTubeData[1].length; i++) {
                    usefulLinks[3][i] = youTubeData[1][i];
                }
            }
            return usefulLinks;
        }

        public String[][] getYouTubeDataFromWeb(String movieIdObject) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String movieJsonStr = null;

            try {
                // Create the URL
                Uri.Builder trailerUrl = new Uri.Builder();

                trailerUrl.scheme("http");
                trailerUrl.authority("api.themoviedb.org");
                trailerUrl.appendPath("3");
                trailerUrl.appendPath("movie");
                trailerUrl.appendPath((movieIdObject));
                trailerUrl.appendPath("videos");
                trailerUrl.appendQueryParameter(APPID_PARAM, appId);

                URL url = new URL(trailerUrl.toString());

                // Create the request to the movieDB, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    movieJsonStr = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    movieJsonStr = null;
                }

                movieJsonStr = buffer.toString();

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the movie data, there's no point in attempting
                // to parse it.
                movieJsonStr = null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                if (isOnline(getActivity())) {
                    return getYouTubeFromJson(movieJsonStr);
                }

            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        private String[][] getYouTubeFromJson(String movieJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String OWM_LIST = "results";
            final String OWM_NAME = "name";
            final String OWM_KEY = "key";

            String[][] imageStrs = new String[2][1];

            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(OWM_LIST);

            int arrayLength = movieArray.length();

            if (arrayLength > 0 ) {
                //somehow this index here limits the number of trailers
                imageStrs = new String[2][movieArray.length()];

                for(int i = 0; i < movieArray.length(); i++) {

                    // Get the JSON object representing the list of movies
                    JSONObject movieData = movieArray.getJSONObject(i);
                    String nameObject = movieData.getString(OWM_NAME);
                    String keyObject = movieData.getString(OWM_KEY);

                    Uri.Builder trailerUrl = new Uri.Builder();

                    trailerUrl.scheme("https");
                    trailerUrl.authority("youtu.be");
                    trailerUrl.appendPath(keyObject);

                    imageStrs[0][i] = trailerUrl.toString();
                    imageStrs[1][i] = nameObject;
                }
            } else {
                //Rick Rolled!
                imageStrs[0][0] = "https://youtu.be/dQw4w9WgXcQ";
                imageStrs[1][0] = "Official Trailer";

            }
            return imageStrs;
        }

        public String[][] getReviewDataFromWeb(String movieIdObject) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String movieJsonStr = null;

            try {
                // Create the URL
                Uri.Builder reviewUrl = new Uri.Builder();

                reviewUrl.scheme("http");
                reviewUrl.authority("api.themoviedb.org");
                reviewUrl.appendPath("3");
                reviewUrl.appendPath("movie");
                reviewUrl.appendPath((movieIdObject));
                reviewUrl.appendPath("reviews");
                reviewUrl.appendQueryParameter(APPID_PARAM, appId);

                URL url = new URL(reviewUrl.toString());

                // Create the request to the movieDB, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    movieJsonStr = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    movieJsonStr = null;
                }

                movieJsonStr = buffer.toString();

            } catch (IOException e) {
                // If the code didn't successfully get the movie data, there's no point in attempting
                // to parse it.
                movieJsonStr = null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                if (isOnline(getActivity())) {
                    return getReviewFromJson(movieJsonStr);
                }

            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        private String[][] getReviewFromJson(String movieJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String OWM_LIST = "results";
            final String OWM_AUTHOR = "author";
            final String OWM_CONTENT = "content";

            String[][] imageStrs = new String[2][1];

            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(OWM_LIST);

            int arrayLength = movieArray.length();

            if (arrayLength > 0 ) {
                imageStrs = new String[2][movieArray.length()];
                for (int i = 0; i < movieArray.length(); i++) {

                    // Get the JSON object representing the list of movies
                    JSONObject movieData = movieArray.getJSONObject(i);
                    String authorObject = movieData.getString(OWM_AUTHOR);
                    String contentObject = movieData.getString(OWM_CONTENT);


                    imageStrs[0][i] = authorObject;
                    imageStrs[1][i] = contentObject;
                }
            } else {
                imageStrs[0][0] = "No Author";
                imageStrs[1][0] = "No Reviews";
            }
            return imageStrs;
        }

        // As suggested to prevent crashing when no network is present
        public boolean isOnline(Context context) {
            ConnectivityManager cm =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();

            return isConnected;
        }

        @Override
        protected void onPostExecute(String[][] strings) {
            if (strings != null) {
                //These next four loops remove any null characters
                int youTubeLength = 0;
                for (int i = 0; i < strings[0].length; i++ ) {
                    if (strings[0][i] != null) {
                        youTubeLength += 1;
                    }
                }

                int reviewLength = 0;
                for (int i = 0; i < strings[2].length; i++ ) {
                    if (strings[2][i] != null) {
                        reviewLength += 1;
                    }
                }

                int authorLength = 0;
                for (int i = 0; i < strings[1].length; i++ ) {
                    if (strings[1][i] != null) {
                        authorLength += 1;
                    }
                }

                int youTubeNameLength = 0;
                for (int i = 0; i < strings[3].length; i++ ) {
                    if (strings[3][i] != null) {
                        youTubeNameLength += 1;
                    }
                }

                youTubeLinks = new String[youTubeLength];

                for (int i = 0; i < youTubeLength; i++) {
                    if (strings[0][i] != null) {
                        youTubeLinks[i] = strings[0][i];
                    }
                }

                youTubeLinkNames = new String[youTubeNameLength];

                for (int i = 0; i < youTubeNameLength; i++) {
                    if (strings[3][i] != null) {
                        youTubeLinkNames[i] = strings[3][i];
                    }
                }

                createTrailerButtons(youTubeLinks, youTubeLinkNames);

                movieReviews = new String[reviewLength];
                for (int i = 0; i < reviewLength; i++) {
                    movieReviews[i] = strings[2][i];
                }

                movieAuthors = new String[authorLength];
                for (int i = 0; i < authorLength; i++) {
                    movieAuthors[i] = strings[1][i];
                }
                createReviews(movieAuthors, movieReviews);
            }
        }
    }
}
