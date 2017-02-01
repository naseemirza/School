package com.lead.infosystems.schooldiary.Main;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.lead.infosystems.schooldiary.Data.UserDataSP;
import com.lead.infosystems.schooldiary.R;

import java.util.ArrayList;
import java.util.List;

public class MainTabAdapter extends Fragment {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private FloatingActionButton fab;
    private View rootview;
    private int currentTab;
    private final int HOME_TAB = 0;
    private final int QA_TAB = 1;
    private final int CHAT_TAB = 2;
    private final int NOTIFICATION_TAB = 3;
    UserDataSP userDataSP;

    View b1,b2;
    public static final String NOTIFICATION_BC_FILTER = "NOTIFICATION_BC_FILTER";
    private static int preRot = 0;
    ViewPagerAdapter adapter;
    public MainTabAdapter() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.fragment_main_frag, container, false);
        viewPager = (ViewPager) rootview.findViewById(R.id.viewpager);
        adapter = new ViewPagerAdapter(getChildFragmentManager());
        userDataSP = new UserDataSP(getActivity().getApplicationContext());
        setupViewPager(viewPager);
        fab = (FloatingActionButton) rootview.findViewById(R.id.post_new_fab);
        tabLayout = (TabLayout) rootview.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {}

            @Override
            public void onPageScrollStateChanged(int state) {
                int position = viewPager.getCurrentItem();
                setTitle(position);
                if(position == HOME_TAB){
                    fab.show();
                    rotateFab(fab,position,state);
                }else if(position == QA_TAB){
                    fab.show();
                    rotateFab(fab,position, state);
                }
                else if(position == CHAT_TAB){
                    fab.show();
                    rotateFab(fab,position, state);
                    userDataSP.setNotificationNumber(0,UserDataSP.CHAT_NOTIFICATION_NUM);
                    updateTabs();
                }
                else if(position == NOTIFICATION_TAB){
                    fab.hide();
                    userDataSP.setNotificationNumber(0,UserDataSP.NOTIFICATION_NUM);
                    updateTabs();
                }
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                currentTab = viewPager.getCurrentItem();
                if(currentTab == HOME_TAB){
                    fab.show();
                    loadHomeFragDialog();
                }else if(currentTab == QA_TAB){
                    fab.show();
                    loadQuestionFragDialog();
                }
                else if(currentTab == CHAT_TAB){
                    fab.show();
                    startActivity(new Intent(getActivity(),ChatNew.class));
                }
                else if(currentTab == NOTIFICATION_TAB){
                    fab.hide();
                }
            }
        });

        getActivity().registerReceiver(receiver,new IntentFilter(NOTIFICATION_BC_FILTER));
        return rootview;
    }

    @Override
    public void onResume() {
        super.onResume();
        setTitle(viewPager.getCurrentItem());
    }

    private void setTitle(int position){

        if(position == HOME_TAB){
            getActivity().setTitle("Home");
        }else if(position == QA_TAB){
            getActivity().setTitle("Q & A");
        }
        else if(position == CHAT_TAB){
            getActivity().setTitle("Chat");
        }
        else if(position == NOTIFICATION_TAB){
            getActivity().setTitle("Notifications");
        }
    }
    private void loadHomeFragDialog(){
        android.app.FragmentManager fragmentManager = getActivity().getFragmentManager();
        PostDialog dialog = new PostDialog();
        dialog.show(fragmentManager,"frag");
    }

    private void loadQuestionFragDialog(){
        android.app.FragmentManager fragmentManager = getActivity().getFragmentManager();
        QuestionDialog dialog = new QuestionDialog();
        dialog.show(fragmentManager,"frag");
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter.addFragment(new FragTabHome(),"ONE");
        adapter.addFragment(new FragTabQA(),"TWO");
        adapter.addFragment(new FragTabChat(), "THREE");
        adapter.addFragment(new FragTabNotifications(), "FOUR");
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(0);
    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter {
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
            return null;
        }
    }


    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateTabs();
        }
    };

    private void updateTabs(){
        currentTab = viewPager.getCurrentItem();
        tabLayout.removeAllTabs();
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
    }

    private void setupTabIcons() {
        b1 = new Badges(getActivity().getApplicationContext()).getBadgeIcon(R.drawable.ic_chat);
        b2 = new Badges(getActivity().getApplicationContext()).getBadgeIcon(R.drawable.ic_notifications);
        tabLayout.getTabAt(HOME_TAB).setIcon(R.drawable.ic_home);
        tabLayout.getTabAt(QA_TAB).setIcon(R.drawable.ic_question_answer);
        tabLayout.getTabAt(CHAT_TAB).setCustomView(b1);
        tabLayout.getTabAt(NOTIFICATION_TAB).setCustomView(b2);
    }

    public void rotateFab(final FloatingActionButton fab, int dir, int state) {
        if((preRot - dir) > 0) {
            if(state != 0){
                final Animation an = new RotateAnimation(0, 180*state, fab.getWidth() / 3, fab.getHeight() / 3);
                an.setDuration(500);
                an.setFillAfter(true);
                fab.clearAnimation();
                fab.startAnimation(an);
            }
        }else{
            if(state != 0){
                final Animation an = new RotateAnimation(0, -180*state, fab.getWidth() / 3, fab.getHeight() / 3);
                an.setDuration(500);
                an.setFillAfter(true);
                fab.clearAnimation();
                fab.startAnimation(an);
            }
        }
        preRot = dir;
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            getActivity().unregisterReceiver(receiver);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
