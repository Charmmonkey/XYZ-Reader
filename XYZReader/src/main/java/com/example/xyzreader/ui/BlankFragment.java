package com.example.xyzreader.ui;


import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class BlankFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String ARTICLE_POSITION_KEY = "position";
    private Cursor mCursor;
    private View mRootView;
    private GregorianCalendar START_OF_EPOCH = new GregorianCalendar(2,1,1);
    private SimpleDateFormat outputFormat = new SimpleDateFormat();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss");
    private String TAG = "BlankFragment.java";


    public BlankFragment() {
        // Required empty public constructor
    }

    public static BlankFragment newInstance(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt(ARTICLE_POSITION_KEY, position);
        BlankFragment fragment = new BlankFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return ArticleLoader.newAllArticlesInstance(getActivity());
    }


    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mCursor = cursor;

        bindViews();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mCursor = null;
        Log.d(TAG, "reset");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        int position = this.getArguments().getInt(ARTICLE_POSITION_KEY);

        Log.d("MainActivity.java", "fragment view created, position: " + position);

        getLoaderManager().initLoader(position, null, this);


        mRootView = inflater.inflate(R.layout.fragment_blank, container, false);

        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void bindViews() {
        if (mRootView == null) {
            return;
        }

        TextView titleView = (TextView) mRootView.findViewById(R.id.fragment_text);
        int position = this.getArguments().getInt(ARTICLE_POSITION_KEY);
        if (mCursor.moveToPosition(position)) {
            titleView.setText(mCursor.getString(ArticleLoader.Query.TITLE)+
                    mCursor.getString(ArticleLoader.Query.TITLE)+
                    mCursor.getString(ArticleLoader.Query.TITLE)
            );
//            Date publishedDate = parsePublishedDate();
//            if (!publishedDate.before(START_OF_EPOCH.getTime())) {
//                bylineView.setText(Html.fromHtml(
//                        DateUtils.getRelativeTimeSpanString(
//                                publishedDate.getTime(),
//                                System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
//                                DateUtils.FORMAT_ABBREV_ALL).toString()
//                                + " by <font color='#ffffff'>"
//                                + mCursor.getString(ArticleLoader.Query.AUTHOR)
//                                + "</font>"));
//
//            } else {
//                // If date is before 1902, just show the string
//                bylineView.setText(Html.fromHtml(
//                        outputFormat.format(publishedDate) + " by <font color='#ffffff'>"
//                                + mCursor.getString(ArticleLoader.Query.AUTHOR)
//                                + "</font>"));
//
//            }
//            bodyView.setText(Html.fromHtml(mCursor.getString(ArticleLoader.Query.BODY).replaceAll("(\r\n|\n)", "<br />")));

        } else {
            Log.d(TAG, "else");

            mRootView.setVisibility(View.GONE);
            titleView.setText("N/A");
//            bylineView.setText("N/A");
//            bodyView.setText("N/A");
        }
    }

    private Date parsePublishedDate() {
        try {
            String date = mCursor.getString(ArticleLoader.Query.PUBLISHED_DATE);
            return dateFormat.parse(date);
        } catch (ParseException ex) {
            Log.e(TAG, ex.getMessage());
            Log.i(TAG, "passing today's date");
            return new Date();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

    }
}
