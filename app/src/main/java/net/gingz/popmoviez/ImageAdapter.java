package net.gingz.popmoviez;

import android.content.Context;
import android.media.Image;
import android.text.method.MovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ahjornz on 11/5/15.
 */
public class ImageAdapter extends BaseAdapter {
    public List<String> mThumbIds;
    private Context mContext;
    MovieFragment myObject = new MovieFragment();
    private boolean mNotifyOnChange = true;

    public ImageAdapter(Context c, List<String> myList) {
        mContext = c;
        mThumbIds = myList;
    }

    public int getCount() {
        return mThumbIds.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            //imageView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
            imageView.setLayoutParams(new GridView.LayoutParams(400, 400));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(4, 4, 4, 4);
        } else {
            imageView = (ImageView) convertView;
        }

        Picasso.with(mContext)
                .load(mThumbIds.get(position).toString())
                .into(imageView);
        return imageView;
    }
}