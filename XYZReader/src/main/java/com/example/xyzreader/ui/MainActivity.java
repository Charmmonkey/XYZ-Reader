package com.example.xyzreader.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.example.xyzreader.R;

public class MainActivity extends AppCompatActivity {
    private ViewPager mPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPager = (ViewPager) findViewById(R.id.view_pager);
        mPager.setAdapter(new SimpleFragmentPageAdapter(getSupportFragmentManager()));
    }

    private class SimpleFragmentPageAdapter extends FragmentPagerAdapter{

        public SimpleFragmentPageAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return BlankFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return 6;
        }
    }
}
