package com.giangnd_svmc.ghalo;

/**
 * Created by GIANGND-SVMC on 27/01/2016.
 */

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.giangnd_svmc.ghalo.entity.Account;
import com.giangnd_svmc.ghalo.fragment.ChatOnlineFragment;
import com.giangnd_svmc.ghalo.fragment.PersonalFragment;
import com.giangnd_svmc.ghalo.fragment.SmsFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Account session_user = new Account();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_activity);

        session_user = (Account) getIntent().getSerializableExtra("SESSION");
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        getIntent().putExtra("SESSION", session_user);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.chatonline);
        tabLayout.getTabAt(1).setIcon(R.drawable.sms);
        tabLayout.getTabAt(2).setIcon(R.drawable.heart);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ChatOnlineFragment(), getString(R.string.title_tab_one));
        adapter.addFragment(new SmsFragment(), getString(R.string.title_tab_two));
        adapter.addFragment(new PersonalFragment(), getString(R.string.title_tab_three));
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
