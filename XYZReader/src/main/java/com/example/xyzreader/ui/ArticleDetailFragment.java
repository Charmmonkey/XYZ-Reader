package com.example.xyzreader.ui;


import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.Loader;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArticleDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String ARTICLE_POSITION_KEY = "position";
    private Cursor mCursor;
    private View mRootView;
    private GregorianCalendar START_OF_EPOCH = new GregorianCalendar(2, 1, 1);
    private SimpleDateFormat outputFormat = new SimpleDateFormat();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss");
    private String TAG = "BlankFragment.java";
    private List<String> bodyList;
    private ProgressBar progressBar;
    private ImageView greyImageView, imageView;

    public ArticleDetailFragment() {
        // Required empty public constructor
    }

    public static ArticleDetailFragment newInstance(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt(ARTICLE_POSITION_KEY, position);
        ArticleDetailFragment fragment = new ArticleDetailFragment();
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


        mRootView = inflater.inflate(R.layout.article_article_fragment, container, false);

        mRootView.findViewById(R.id.article_fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setText("Some sample text")
                        .getIntent(), getString(R.string.action_share)));
            }
        });
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

        imageView = (ImageView) mRootView.findViewById(R.id.article_image);
        TextView titleView = (TextView) mRootView.findViewById(R.id.article_title);
        TextView dateView = (TextView) mRootView.findViewById(R.id.article_date);
        progressBar = (ProgressBar) mRootView.findViewById(R.id.progress_bar);
        greyImageView = (ImageView) mRootView.findViewById(R.id.image_grey);
        RecyclerView bodyRecyclerview = (RecyclerView) mRootView.findViewById(R.id.article_body_list);
        bodyRecyclerview.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        bodyRecyclerview.setAdapter(new ArticleAdapter());

        int position = this.getArguments().getInt(ARTICLE_POSITION_KEY);
        if (mCursor.moveToPosition(position)) {
            String articlePhotoUrl = mCursor.getString(ArticleLoader.Query.PHOTO_URL);

            Glide.with(getContext())
                    .asBitmap()
                    .load(articlePhotoUrl)
                    .into(new BitmapImageViewTarget(imageView) {
                        @Override
                        public void onResourceReady(Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            super.onResourceReady(resource, transition);
                            Palette.Builder paletteBuilder = Palette.from(resource);
                            int mutedColor = paletteBuilder.generate().getMutedColor(0xFF333333);

                            progressBar.setVisibility(View.GONE);
                            greyImageView.setVisibility(View.GONE);
                            imageView.setVisibility(View.VISIBLE);

                            Log.d(TAG, "resource ready");
//
                            mRootView.findViewById(R.id.meta_bar).setBackgroundColor(mutedColor);
                        }
                    });


            bodyList = Arrays.asList(mCursor.getString(ArticleLoader.Query.BODY).split("\r\n|\n"));
            bodyList = bodyList.subList(0, 20);

            titleView.setText(mCursor.getString(ArticleLoader.Query.TITLE));


            Date publishedDate = parsePublishedDate();
            if (!publishedDate.before(START_OF_EPOCH.getTime())) {
                dateView.setText(Html.fromHtml(
                        DateUtils.getRelativeTimeSpanString(
                                publishedDate.getTime(),
                                System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                                DateUtils.FORMAT_ABBREV_ALL).toString()
                                + " by <font color='#ffffff'>"
                                + mCursor.getString(ArticleLoader.Query.AUTHOR)
                                + "</font>"));

            } else {
                // If date is before 1902, just show the string
                dateView.setText(Html.fromHtml(
                        outputFormat.format(publishedDate) + " by <font color='#ffffff'>"
                                + mCursor.getString(ArticleLoader.Query.AUTHOR)
                                + "</font>"));

            }
//            bodyView.setText(Html.fromHtml(mCursor.getString(ArticleLoader.Query.BODY)));

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

    public class ArticleAdapter extends RecyclerView.Adapter<ArticleViewHolder> {
        @Override
        public void onBindViewHolder(ArticleViewHolder holder, int position) {
            holder.bodyView.setText(bodyList.get(position));
        }

        @Override
        public ArticleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.list_item_body, parent, false);
            return new ArticleViewHolder(view);
        }

        @Override
        public int getItemCount() {
            return bodyList.size();
        }
    }

    private static class ArticleViewHolder extends RecyclerView.ViewHolder {
        public TextView bodyView;

        public ArticleViewHolder(View view) {
            super(view);
            bodyView = (TextView) view.findViewById(R.id.article_body);
        }

    }


}
