package com.example.nitu.popularmovies.adaptors;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.nitu.popularmovies.R;
import com.example.nitu.popularmovies.model.ReviewData;


import java.util.List;

public class ReviewListViewAdapter extends ArrayAdapter<ReviewData> {
    private final Context mContext;
    private final int mReviewCellRes;
    private final List<ReviewData> mRowObjs;

    public ReviewListViewAdapter(Context context, int reviewCellRes, List<ReviewData> rowObjs) {
        super(context, reviewCellRes, rowObjs);
        mContext = context;
        mReviewCellRes = reviewCellRes;
        mRowObjs = rowObjs;
    }

    @Override
    public int getCount() {
        return mRowObjs.size();
    }

    @Override
    public ReviewData getItem(int position) {
        return mRowObjs.get(position);
    }

    @Override
    public int getPosition(ReviewData item) {
        return mRowObjs.indexOf(item);
    }

    @Override
    public View getView(int position, View row, ViewGroup parent) {
        //Utils.log(getClass().getSimpleName());
        if (checkRowAndObj(row, position)) {
            final ReviewData rev = getItem(position);
            ViewHolder holder = new ViewHolder();
            holder.reviewHashCode = rev.hashCode();
            row = ((Activity) mContext).getLayoutInflater().inflate(mReviewCellRes, parent, false);
            holder.contentText = (TextView) row.findViewById(R.id.content);
            holder.authorText = (TextView) row.findViewById(R.id.author);
            holder.contentText.setText(rev.content);
            holder.authorText.setText(rev.author);
            holder.position = position;
            /*holder.authorText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(rev.url)));
                }
            });*/
            row.setTag(holder);
        }
        return row;
    }

    private boolean checkRowAndObj(View row, int position) {
        if (row != null) {
            ViewHolder vh = (ViewHolder) row.getTag();
            return vh.position != position || vh.reviewHashCode != getItem(position).hashCode();
        } else
            return true;
    }

    public void setData() {
       // mRowObjs = data;
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        int position = -1;
        TextView contentText;
        TextView authorText;
        int reviewHashCode;
    }
}
