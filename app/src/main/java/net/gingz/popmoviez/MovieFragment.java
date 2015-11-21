package net.gingz.popmoviez;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
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
    public List<String> movieList = new ArrayList<String>();
    public List<String> titleList = new ArrayList<String>();
    public List<String> plotList = new ArrayList<String>();
    public List<String> voteList = new ArrayList<String>();
    public List<String> dateList = new ArrayList<String>();
    ImageAdapter mAdapter;

    public final static String EXTRA_TITLE = "net.gingz.popmoviez.TITLE";
    public final static String EXTRA_IMAGE = "net.gingz.popmoviez.IMAGE";
    public final static String EXTRA_PLOT = "net.gingz.popmoviez.PLOT";
    public final static String EXTRA_VOTE = "net.gingz.popmoviez.VOTE";
    public final static String EXTRA_RELEASE = "net.gingz.popmoviez.RELEASE";

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
        setHasOptionsMenu(true);
        updateMovies();

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

        switch (item.getItemId()) {
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

                Intent intent = new Intent(getActivity(), DetailActivity.class);
                String title = titleList.get(position).toString();
                String movieImage = movieList.get(position).toString();
                //movieImage = movieImage.replace("w185", "w500");
                String plot = plotList.get(position).toString();
                String vote = voteList.get(position).toString();
                String release = dateList.get(position).toString();


                intent.putExtra(EXTRA_TITLE, title);
                intent.putExtra(EXTRA_IMAGE, movieImage);
                intent.putExtra(EXTRA_PLOT, plot);
                intent.putExtra(EXTRA_VOTE, vote);
                intent.putExtra(EXTRA_RELEASE, release);

                startActivity(intent);
            }
        });

        return rootView;
    }

    public class FetchMovieData extends AsyncTask<String, Void, String[][]> {


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

            //REMEMBER TO ENTER API KEY HERE BEFORE YOU RUN
            String appId = "< ENTER API KEY";

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are available at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                final String SORT_PARAM = "sort_by";
                final String APPID_PARAM = "api_key";

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


                imageStrs[0][i] = "https://image.tmdb.org/t/p/w500" + posterObject;
                imageStrs[1][i] = titleObject;
                imageStrs[2][i] = overviewObject;
                imageStrs[3][i] = Double.toString(voteAvgObject);
                imageStrs[4][i] = releaseDateObj;

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
            }
            mAdapter.notifyDataSetChanged();
        }
    }
}
