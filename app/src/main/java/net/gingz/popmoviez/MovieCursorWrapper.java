package net.gingz.popmoviez;

import android.database.Cursor;
import android.database.CursorWrapper;

/**
 * Created by ahjornz on 12/27/15.
 */


public class MovieCursorWrapper extends CursorWrapper {
    public MovieCursorWrapper (Cursor cursor) {
        super(cursor);
    }

    public String[] getMovies() {
        int movieId = getInt(getColumnIndex(MovieContract.MovieColumns.COLUMN_NAME_MOVIE_ID));
        String title = getString(getColumnIndex(MovieContract.MovieColumns.COLUMN_NAME_TITLE));
        String image = getString(getColumnIndex(MovieContract.MovieColumns.COLUMN_NAME_IMAGE));
        String release = getString(getColumnIndex(MovieContract.MovieColumns.COLUMN_NAME_RELEASE));
        String vote = getString(getColumnIndex(MovieContract.MovieColumns.COLUMN_NAME_VOTE));
        String plot = getString(getColumnIndex(MovieContract.MovieColumns.COLUMN_NAME_PLOT));
        String trailer = getString(getColumnIndex(MovieContract.MovieColumns.COLUMN_NAME_TRAILERs));
        String review = getString(getColumnIndex(MovieContract.MovieColumns.COLUMN_NAME_REVIEWS));

        String[] imgStrs = new String[8];
        imgStrs[0] = "https://image.tmdb.org/t/p/w500" + image;
        imgStrs[1] = title;
        imgStrs[2] = plot;
        imgStrs[3] = vote;
        imgStrs[4] = release;
        imgStrs[5] = Integer.toString((movieId));
        imgStrs[6] = trailer;
        imgStrs[7] = review;

        return imgStrs;
    }
}
