package com.lead.infosystems.schooldiary.Main;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

import com.lead.infosystems.schooldiary.ModelQuestionPapers;
import com.lead.infosystems.schooldiary.R;

import java.util.ArrayList;
import java.util.List;

public class MainTabAdapter extends Fragment {

    ViewPager viewPager;
    TabLayout tabLayout;
    FloatingActionButton fab;
    View rootview;
    int currentTab;
    private final int HOME_TAB = 0;
    private final int QA_TAB = 1;
    private final int CHAT_TAB = 2;
    private final int NOTIFICATION_TAB = 3;
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
        adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        setupViewPager(viewPager);
        fab = (FloatingActionButton) rootview.findViewById(R.id.post_new_fab);
        tabLayout = (TabLayout) rootview.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                currentTab = viewPager.getCurrentItem();
                if(currentTab == HOME_TAB){
//                    rotateFab(fab,0);
                    loadHomeFragDialog();
                }else if(currentTab == QA_TAB){
//                    rotateFab(fab,1);
                    loadQuestionFragDialog();
                }
                else if(currentTab == CHAT_TAB){
//                    rotateFab(fab,2);
                        startActivity(new Intent(getActivity(),ChatNew.class));
                }
                else if(currentTab == NOTIFICATION_TAB){
//                    rotateFab(fab,3);
                }
            }
        });
        return rootview;
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
            return null;
        }
    }


    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_home);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_question_answer);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_message);
        tabLayout.getTabAt(3).setIcon(R.drawable.ic_net);
    }

//    public void rotateFab(FloatingActionButton fab, int dir) {
//        int previous_tab = 0;
////        if((previous_tab - dir) > 0){
//            ViewCompat.animate(fab)
//                    .rotation(90)
//                    .withLayer()
//                    .setDuration(300L)
//                    .setInterpolator(new OvershootInterpolator(10.0F))
//                    .start();
////        }else{
////            ViewCompat.animate(fab)
////                    .rotation(-90)
////                    .withLayer()
////                    .setDuration(300L)
////                    .setInterpolator(new OvershootInterpolator(10.0F))
////                    .start();
////        }
//    }

}
