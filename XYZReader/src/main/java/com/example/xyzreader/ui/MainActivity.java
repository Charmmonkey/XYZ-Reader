package com.example.xyzreader.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.xyzreader.R;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private ViewPager mPager;
    private String TAG = "MainActivity.java";
    private SimpleFragmentPageAdapter simpleFragmentPageAdapter;
    private int selectedItemPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();
        selectedItemPosition = bundle.getInt(ArticleListActivity.ACTION_ARTICLE_CLICK);
        mPager = (ViewPager) findViewById(R.id.view_pager);
        mPager.setOffscreenPageLimit(1);
        simpleFragmentPageAdapter = new SimpleFragmentPageAdapter(getSupportFragmentManager());
        mPager.setAdapter(simpleFragmentPageAdapter);
        mPager.setCurrentItem(selectedItemPosition);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                            }

            @Override
            public void onPageSelected(int position) {
                Log.d(TAG, "page selected: " + position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private class SimpleFragmentPageAdapter extends FragmentStatePagerAdapter {

        public SimpleFragmentPageAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Log.d(TAG, "getItem: " + position + "");
            return BlankFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return 6;
        }

        @Override
        public int getItemPosition(Object object) {

            return super.getItemPosition(object);
        }
    }
}
