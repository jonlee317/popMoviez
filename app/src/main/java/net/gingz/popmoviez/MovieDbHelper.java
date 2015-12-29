package net.gingz.popmoviez;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Movie;
import android.util.Log;

/**
 * Created by ahjornz on 12/20/15.
 */
public class MovieDbHelper extends SQLiteOpenHelper {
    private final String LOG_TAG = MovieDbHelper.class.getSimpleName();
    
    private static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "movie.db";

    public MovieDbHelper (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // must override this
    // called when database first gets created
    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_MOVIE_TABLE = "CREATE TABLE " + MovieContract.MovieColumns.TABLE_NAME + " (" +
                MovieContract.MovieColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieContract.MovieColumns.COLUMN_NAME_MOVIE_ID + " INTEGER NOT NULL, " +
                MovieContract.MovieColumns.COLUMN_NAME_TITLE + " TEXT NOT NULL, " +
                MovieContract.MovieColumns.COLUMN_NAME_IMAGE + " TEXT NOT NULL, " +
                MovieContract.MovieColumns.COLUMN_NAME_RELEASE + " TEXT NOT NULL, " +
                MovieContract.MovieColumns.COLUMN_NAME_VOTE + " TEXT NOT NULL, " +
                MovieContract.MovieColumns.COLUMN_NAME_PLOT + " TEXT NOT NULL, " +
                MovieContract.MovieColumns.COLUMN_NAME_TRAILERs + " TEXT NOT NULL, " +
                MovieContract.MovieColumns.COLUMN_NAME_REVIEWS + " TEXT NOT NULL" +
                ")";

        Log.v(LOG_TAG, "inside SQL: " + SQL_MOVIE_TABLE);

        db.execSQL(SQL_MOVIE_TABLE);
    }

    //must override this
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieColumns.TABLE_NAME);
        onCreate(db);
    }
}
