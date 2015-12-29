package net.gingz.popmoviez;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Movie;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
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
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieFragment extends Fragment {
    int countBeforeCrash = 0;

    SQLiteDatabase mDb;

    public ArrayList<String> movieList = new ArrayList<String>();
    public ArrayList<String> titleList = new ArrayList<String>();
    public ArrayList<String> plotList = new ArrayList<String>();
    public ArrayList<String> voteList = new ArrayList<String>();
    public ArrayList<String> dateList = new ArrayList<String>();
    public ArrayList<String> idList = new ArrayList<String>();

    ImageAdapter mAdapter;

    public final static String EXTRA_TITLE = "net.gingz.popmoviez.TITLE";
    public final static String EXTRA_IMAGE = "net.gingz.popmoviez.IMAGE";
    public final static String EXTRA_PLOT = "net.gingz.popmoviez.PLOT";
    public final static String EXTRA_VOTE = "net.gingz.popmoviez.VOTE";
    public final static String EXTRA_RELEASE = "net.gingz.popmoviez.RELEASE";
    public final static String EXTRA_ID = "net.gingz.popmoviez.ID";

    public final static String SAVED_TITLE = "net.gingz.popmoviez.sTITLE";
    public final static String SAVED_IMAGE = "net.gingz.popmoviez.sIMAGE";
    public final static String SAVED_PLOT = "net.gingz.popmoviez.sPLOT";
    public final static String SAVED_VOTE = "net.gingz.popmoviez.sVOTE";
    public final static String SAVED_RELEASE = "net.gingz.popmoviez.sRELEASE";
    public final static String SAVED_ID = "net.gingz.popmoviez.sID";

    private final String LOG_TAG = MovieFragment.class.getSimpleName();

    public MovieFragment() {
    }

    private void updateMovies() {
        FetchMovieData movData = new FetchMovieData();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sorts = prefs.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_default));
        movData.execute(sorts);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            movieList = savedInstanceState.getStringArrayList(SAVED_IMAGE);
            idList = savedInstanceState.getStringArrayList(SAVED_ID);
            voteList = savedInstanceState.getStringArrayList(SAVED_VOTE);
            titleList = savedInstanceState.getStringArrayList(SAVED_TITLE);
            dateList = savedInstanceState.getStringArrayList(SAVED_RELEASE);
            plotList = savedInstanceState.getStringArrayList(SAVED_PLOT);

            Log.v(LOG_TAG, "print movieList 1" + movieList);
        }
        setHasOptionsMenu(true);

    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment, menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_refresh:
                updateMovies();

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        GridView gridview = (GridView) rootView.findViewById(R.id.gridview_movie);
        mAdapter = new ImageAdapter(getActivity(), movieList);
        gridview.setAdapter(mAdapter);


        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                String title = titleList.get(position).toString();
                String movieImage = movieList.get(position).toString();
                String plot = plotList.get(position).toString();
                String vote = voteList.get(position).toString();
                String release = dateList.get(position).toString();
                String movieId = idList.get(position).toString();

                if (getActivity().findViewById(R.id.fragment_detail_container) == null) {

                    Intent intent = new Intent(getActivity(), DetailActivity.class);

                    intent.putExtra(EXTRA_TITLE, title);
                    intent.putExtra(EXTRA_IMAGE, movieImage);
                    intent.putExtra(EXTRA_PLOT, plot);
                    intent.putExtra(EXTRA_VOTE, vote);
                    intent.putExtra(EXTRA_RELEASE, release);
                    intent.putExtra(EXTRA_ID, movieId);

                    startActivity(intent);
                } else {
                    FragmentDetailActivity newDetail = new FragmentDetailActivity();

                    newDetail.setTitle(title);
                    newDetail.setRelease(release);
                    newDetail.setPicture(movieImage);
                    newDetail.setPlot(plot);
                    newDetail.setVote(vote);
                    newDetail.setMovieId(movieId);

                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.fragment_detail_container, newDetail);
                    ft.commit();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putStringArrayList(SAVED_ID, idList);
        savedInstanceState.putStringArrayList(SAVED_IMAGE, movieList);
        savedInstanceState.putStringArrayList(SAVED_PLOT, plotList);
        savedInstanceState.putStringArrayList(SAVED_RELEASE, dateList);
        savedInstanceState.putStringArrayList(SAVED_TITLE, titleList);
        savedInstanceState.putStringArrayList(SAVED_VOTE, voteList);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private MovieCursorWrapper queryMovies() {
        SQLiteOpenHelper movieDbHelper = new MovieDbHelper(getActivity());
        mDb = movieDbHelper.getReadableDatabase();
        Cursor cursor = mDb.query(MovieContract.MovieColumns.TABLE_NAME, null, null, null, null, null, null);
        return new MovieCursorWrapper(cursor);
    }

    public String[][] getFavoriteMovies() {

        MovieCursorWrapper cursor = queryMovies();
        String[][] imageStrs = new String[8][cursor.getCount()];

        try {
            cursor.moveToFirst();
            int i = 0;
            while (!cursor.isAfterLast()) {
                String[] movieInfo = cursor.getMovies();

                for (int j=0; j<8; j++){
                    imageStrs[j][i] = movieInfo[j];
                }
                i += 1;
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return imageStrs;
    }

    @Override
    public void setTargetFragment(Fragment fragment, int requestCode) {
        super.setTargetFragment(fragment, requestCode);
    }

    public class FetchMovieData extends AsyncTask<String, Void, String[][]> {
        // REMEMBER TO ENTER API KEY HERE BEFORE YOU RUN
        // This the first of two places where api key is needed
        String appId = "7e1cef5c1ec1a8c924df71aaf4e004c5";
        final String SORT_PARAM = "sort_by";
        final String APPID_PARAM = "api_key";


        private final String LOG_TAG = FetchMovieData.class.getSimpleName();

        @Override
        protected String[][] doInBackground(String... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String movieJsonStr = null;

            String chosenSort = params[0];

            if (chosenSort.equals("favoritez")) {
                if (!getFavoriteMovies().equals(null)) {
                     return(getFavoriteMovies());
                }
            } else {

                try {
                    // Create the URL
                    Uri.Builder urlTest = new Uri.Builder();

                    urlTest.scheme("http");
                    urlTest.authority("api.themoviedb.org");
                    urlTest.appendPath("3");
                    urlTest.appendPath("discover");
                    urlTest.appendPath("movie");
                    urlTest.appendQueryParameter(APPID_PARAM, appId);
                    urlTest.appendQueryParameter(SORT_PARAM, chosenSort);

                    URL url = new URL(urlTest.toString());

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
                        return getMovieDataFromJson(movieJsonStr);
                    }
                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }
                return null;
            }
            return null;
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

        private String[][] getMovieDataFromJson(String movieJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String OWM_LIST = "results";
            final String OWM_POSTER = "poster_path";
            final String OWM_OVERVIEW = "overview";
            final String OWM_POPULAR = "popularity";
            final String OWM_VOTEAVG = "vote_average";
            final String OWM_TITLE = "title";
            final String OWM_YEARREL = "release_date";
            final String OWM_MOVIEID = "id";

            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(OWM_LIST);

            String[][] imageStrs = new String[movieArray.length()][movieArray.length()];

            for(int i = 0; i < movieArray.length(); i++) {

                // Get the JSON object representing the list of movies
                JSONObject movieData = movieArray.getJSONObject(i);
                String titleObject = movieData.getString(OWM_TITLE);
                String posterObject = movieData.getString(OWM_POSTER);
                double popularityObject = movieData.getDouble(OWM_POPULAR);
                double voteAvgObject = movieData.getDouble(OWM_VOTEAVG);
                String overviewObject = movieData.getString(OWM_OVERVIEW);
                String releaseDateObj = movieData.getString(OWM_YEARREL);
                int movieIdObject = movieData.getInt(OWM_MOVIEID);

                imageStrs[0][i] = "https://image.tmdb.org/t/p/w500" + posterObject;
                imageStrs[1][i] = titleObject;
                imageStrs[2][i] = overviewObject;
                imageStrs[3][i] = Double.toString(voteAvgObject);
                imageStrs[4][i] = releaseDateObj;
                imageStrs[5][i] = Integer.toString(movieIdObject);

            }

            return imageStrs;
        }

        @Override
        protected void onPostExecute(String[][] strings) {
            if (strings != null) {
                movieList.clear();
                titleList.clear();
                plotList.clear();
                voteList.clear();
                dateList.clear();
                idList.clear();

                for (String moviePosterLinks : strings[0]) {
                    // some of the movies have no posters so to eliminate blackspace
                    if (!moviePosterLinks.equals("https://image.tmdb.org/t/p/w500null")) {
                        movieList.add(moviePosterLinks);
                    }
                }
                for (String movieTitleLinks : strings[1]) {
                    titleList.add(movieTitleLinks);
                }
                for (String movieOverviewLinks : strings[2]) {
                    plotList.add(movieOverviewLinks);
                }
                for (String movieVoteLinks : strings[3]) {
                    voteList.add(movieVoteLinks);
                }
                for (String movieReleaseLinks : strings[4]) {
                    dateList.add(movieReleaseLinks);
                }
                if (strings[5] != null) {
                    for (String movieIdLinks : strings[5]) {
                        idList.add(movieIdLinks);
                    }
                }
            }
            mAdapter.notifyDataSetChanged();
        }
    }
}
