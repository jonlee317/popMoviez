package net.gingz.popmoviez;

import android.provider.BaseColumns;

/**
 * Created by ahjornz on 12/18/15.
 */

public class MovieContract {

    public MovieContract() {}

    public static abstract class MovieColumns implements BaseColumns {
        public static final String TABLE_NAME = "movies";
        public static final String COLUMN_NAME_MOVIE_ID = "movie_id";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_IMAGE = "image";
        public static final String COLUMN_NAME_PLOT = "plot";
        public static final String COLUMN_NAME_VOTE = "vote";
        public static final String COLUMN_NAME_RELEASE = "release";
        public static final String COLUMN_NAME_TRAILERs = "trailers";
        public static final String COLUMN_NAME_REVIEWS = "reviews";
    }
}

