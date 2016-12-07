package com.lead.infosystems.schooldiary.Main;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.lead.infosystems.schooldiary.ApplicationForm.ApplicationForm;
import com.lead.infosystems.schooldiary.Attendance.Attendance;
import com.lead.infosystems.schooldiary.Data.MyDataBase;
import com.lead.infosystems.schooldiary.Data.UserDataSP;
import com.lead.infosystems.schooldiary.Login;
import com.lead.infosystems.schooldiary.Model_Paper.ModelQuestionPapers;
import com.lead.infosystems.schooldiary.Profile.Profile;
import com.lead.infosystems.schooldiary.Progress.Progress_Report;
import com.lead.infosystems.schooldiary.R;
import com.lead.infosystems.schooldiary.ServerConnection.ServerConnect;
import com.lead.infosystems.schooldiary.ServerConnection.Utils;
import com.lead.infosystems.schooldiary.StudentDiery;

import java.io.IOException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    Toolbar toolbar;
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    FragmentTransaction frag;
    UserDataSP userDataSP;
    public static String BACK_STACK_TAG = "tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userDataSP = new UserDataSP(getApplicationContext());
        if(ServerConnect.checkInternetConenction(this)) {
            new RegistoreUserCloudID(this).execute();
        }
        toolbar  = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    MainTabAdapter frag = new MainTabAdapter();
    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    transaction.replace(R.id.main_con,frag);
    transaction.commit();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navViewSet();

        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void navViewSet(){
        View holder = navigationView.getHeaderView(0);
        TextView name = (TextView) holder.findViewById(R.id.title);
        TextView rollnum = (TextView) holder.findViewById(R.id.rollnum);
        name.setText(userDataSP.getUserData(UserDataSP.FIRST_NAME)+" "+userDataSP.getUserData(UserDataSP.LAST_NAME));
        rollnum.setText(userDataSP.getUserData(UserDataSP.ROLL_NO).toString());
    }

    @Override
    public void onBackPressed() {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                FragmentManager fn = getSupportFragmentManager();
                fn.popBackStack(BACK_STACK_TAG,0);
                super.onBackPressed();
            }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView search = (SearchView) MenuItemCompat.getActionView(menuItem);
        MenuItemCompat.setOnActionExpandListener(menuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                MainSearch blankFragment = new MainSearch();
                frag = getSupportFragmentManager().beginTransaction();
                frag.replace(R.id.main_con,blankFragment);
                frag.addToBackStack("search");
                frag.commit();
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                FragmentManager fn = getSupportFragmentManager();
                fn.popBackStack("search",0);
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the FragTabHome/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            startActivity(new Intent(this,Profile.class));
        } else if (id == R.id.nav_diery) {
            StudentDiery blankFragment = new StudentDiery();
            frag = getSupportFragmentManager().beginTransaction();
            frag.replace(R.id.main_con,blankFragment);
            frag.addToBackStack(BACK_STACK_TAG);
            frag.commit();

        } else if (id == R.id.nav_attendance) {
            Attendance blankFragment = new Attendance();
            frag = getSupportFragmentManager().beginTransaction();
            frag.replace(R.id.main_con,blankFragment);
            frag.addToBackStack(BACK_STACK_TAG);
            frag.commit();
        } else if (id == R.id.nav_progress) {
            Progress_Report blankFragment = new Progress_Report();
            frag = getSupportFragmentManager().beginTransaction();
            frag.replace(R.id.main_con,blankFragment);
            frag.addToBackStack(BACK_STACK_TAG);
            frag.commit();
        } else if (id == R.id.nav_fees) {
            Uri path = Uri.parse(userDataSP.getUserData(UserDataSP.SCHOOL_FEES));
            Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
            pdfIntent.setDataAndType(path, "application/pdf");
            pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(pdfIntent);
        } else if (id == R.id.nav_question_papers) {
            ModelQuestionPapers blankFragment = new ModelQuestionPapers();
            frag = getSupportFragmentManager().beginTransaction();
            frag.replace(R.id.main_con,blankFragment);
            frag.addToBackStack(BACK_STACK_TAG);
            frag.commit();
        } else if (id == R.id.nav_test) {

        } else if (id == R.id.nav_event) {

        }  else if (id == R.id.nav_teachers_feedback) {

        } else if (id == R.id.nav_suggestions) {

        }else if (id == R.id.nav_Principle) {

        }else if (id == R.id.nav_management) {

        }else if (id==R.id.nav_formdownload) {
            ApplicationForm myform=new ApplicationForm();
            frag=getSupportFragmentManager().beginTransaction();
                    frag.replace(R.id.main_con,myform);
            frag.addToBackStack(BACK_STACK_TAG);
            frag.commit();



        }else if (id == R.id.nav_settings) {

        }else if (id == R.id.nav_log_out) {
            userDataSP.clearUserData();
            MyDataBase myDataBase = new MyDataBase(getApplicationContext());
            myDataBase.clearDb();
            startActivity(new Intent(getApplicationContext(), Login.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class RegistoreUserCloudID extends AsyncTask<String,Void,String>{
        Activity activity;
        UserDataSP userDataSP;

        RegistoreUserCloudID(Activity activity){
            this.activity = activity;
            userDataSP = new UserDataSP(activity.getApplicationContext());
        }
        @Override
        protected String doInBackground(String... params) {

            if(ServerConnect.checkInternetConenction(activity) && !userDataSP.getUserData(UserDataSP.CLOUD_ID).isEmpty()){
                Uri.Builder builder = new Uri.Builder();
                builder.appendQueryParameter(UserDataSP.STUDENT_NUMBER, userDataSP.getUserData(UserDataSP.STUDENT_NUMBER));
                builder.appendQueryParameter("regid",userDataSP.getUserData(userDataSP.CLOUD_ID));
                try {
                    return ServerConnect.downloadUrl(Utils.REGESTRATION,builder.build().getQuery());
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }else{
                return null;
            }
        }
    }

}
