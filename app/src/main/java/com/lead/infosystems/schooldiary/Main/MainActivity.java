package com.lead.infosystems.schooldiary.Main;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lead.infosystems.schooldiary.ApplicationForm.ApplicationForm;
import com.lead.infosystems.schooldiary.Attendance.Attendance_student;
import com.lead.infosystems.schooldiary.Attendance.Attendance_teacher;

import com.lead.infosystems.schooldiary.Data.MyDataBase;
import com.lead.infosystems.schooldiary.Data.NotificationData;
import com.lead.infosystems.schooldiary.Data.UserDataSP;
import com.lead.infosystems.schooldiary.Events.EventAll;
import com.lead.infosystems.schooldiary.Generic.ServerConnect;
import com.lead.infosystems.schooldiary.Login;
import com.lead.infosystems.schooldiary.MainSearch;
import com.lead.infosystems.schooldiary.Model_Paper.ModelQuestionPapers;
import com.lead.infosystems.schooldiary.Model_Paper.TeacherMQP;
import com.lead.infosystems.schooldiary.Profile.Profile;
import com.lead.infosystems.schooldiary.Progress.Progress_Report;
import com.lead.infosystems.schooldiary.R;
import com.lead.infosystems.schooldiary.Generic.Utils;
import com.lead.infosystems.schooldiary.StudentDiery;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private FragmentTransaction frag;
    private UserDataSP userDataSP;
    private static boolean backExit = false;
    public static String BACK_STACK_TAG = "tag";
    public static String BACK_STACK_TMQP = "tag_tmqp";
    public static String BACK_TAG ;
    private ImageView propic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userDataSP = new UserDataSP(getApplicationContext());
        toolbar  = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        openFrag();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        if(!userDataSP.isStudent()){
            Menu m = navigationView.getMenu();
            m.findItem(R.id.nav_progress).setVisible(false);
            navViewSet(userDataSP.getUserData(UserDataSP.EMAIL_ID));
        }else{
            navViewSet("Class: "+userDataSP.getUserData(UserDataSP.CLASS)+" "+userDataSP.getUserData(UserDataSP.DIVISION));
        }
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void openFrag() {
        try {
            if(getIntent().getAction().contains(NotificationData.EVENT)){
                openEventFrag();
            }
        }catch (Exception e){
            MainTabAdapter frag = new MainTabAdapter();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.main_con,frag);
            transaction.addToBackStack("main");
            transaction.commit();
        }

    }

    public static void setTag(String stakTag){
        BACK_TAG = stakTag;
    }
    private void navViewSet(String s){
        View holder = navigationView.getHeaderView(0);
        propic = (ImageView) holder.findViewById(R.id.propic);
        Picasso.with(getApplicationContext())
                .load(Utils.SERVER_URL+(userDataSP.getUserData(UserDataSP.PROPIC_URL).replace("profilepic","propic_thumb")))
                .networkPolicy(ServerConnect.checkInternetConenction(this)?
                        NetworkPolicy.NO_CACHE:NetworkPolicy.OFFLINE)
                .into(propic);
        TextView name = (TextView) holder.findViewById(R.id.title);
        TextView rollnum = (TextView) holder.findViewById(R.id.rollnum);
        name.setText(userDataSP.getUserData(UserDataSP.FIRST_NAME)+" "+userDataSP.getUserData(UserDataSP.LAST_NAME));
        rollnum.setText(s);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Picasso.with(getApplicationContext())
                .load(Utils.SERVER_URL+(userDataSP.getUserData(UserDataSP.PROPIC_URL).replace("profilepic","propic_thumb")))
                .networkPolicy(ServerConnect.checkInternetConenction(this)?
                        NetworkPolicy.NO_CACHE:NetworkPolicy.OFFLINE)
                .into(propic);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final FragmentManager fn = getSupportFragmentManager();
        if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
        }else if(BACK_TAG == BACK_STACK_TMQP){
            fn.popBackStack(BACK_STACK_TMQP,FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } else if(fn.getBackStackEntryCount() > 1){
            fn.popBackStack(BACK_STACK_TAG,FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }else{
            if(backExit){
                finish();
            }else{
                backExit = true;
                Toast.makeText(getApplicationContext(),"Press back button one more time to exit",Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                      backExit = false;
                    }
                },1000);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
      //  getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the FragTabHome/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if(id == R.id.search){
            startActivity(new Intent(this, MainSearch.class));
        }

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
            if(userDataSP.getUserData(UserDataSP.IDENTIFICATION).contains("student")){
                StudentDiery blankFragment = new StudentDiery();
                frag = getSupportFragmentManager().beginTransaction();
                frag.replace(R.id.main_con,blankFragment);
                frag.addToBackStack(BACK_STACK_TAG);
                frag.commit();
             }else{
                //teacher homework post like attendance
                }

        } else if (id == R.id.nav_attendance) {
            if(userDataSP.isStudent()) {
                Attendance_student blankFragment = new Attendance_student();
                frag = getSupportFragmentManager().beginTransaction();
                frag.replace(R.id.main_con, blankFragment);
                frag.addToBackStack(BACK_STACK_TAG);
                frag.commit();
            }
            else
            {
                Attendance_teacher blankFragment = new Attendance_teacher();
                frag = getSupportFragmentManager().beginTransaction();
                frag.replace(R.id.main_con, blankFragment);
                frag.addToBackStack(BACK_STACK_TAG);
                frag.commit();
            }
        } else if (id == R.id.nav_progress) {
                Progress_Report blankFragment = new Progress_Report();
                frag = getSupportFragmentManager().beginTransaction();
                frag.replace(R.id.main_con,blankFragment);
                frag.addToBackStack(BACK_STACK_TAG);
                frag.commit();
        } else if (id == R.id.nav_fees) {
            String link= userDataSP.getUserData(UserDataSP.SCHOOL_FEES);
            String pdfLink = link.replace(" ","%20");
            Intent intent=new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(pdfLink),"application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            try {
                startActivity(intent);
            }catch (Exception e){
                intent = new Intent(Intent.ACTION_VIEW,Uri.parse(Utils.GOOGLE_DRIVE_VIEWER + pdfLink));
                startActivity(intent);
            }
        } else if (id == R.id.nav_question_papers) {
            if(userDataSP.isStudent()){
                ModelQuestionPapers blankFragment = new ModelQuestionPapers();
                frag = getSupportFragmentManager().beginTransaction();
                frag.replace(R.id.main_con,blankFragment);
                frag.addToBackStack(BACK_STACK_TAG);
                frag.commit();
            }else {
                TeacherMQP blankFragment = new TeacherMQP();
                frag = getSupportFragmentManager().beginTransaction();
                frag.replace(R.id.main_con,blankFragment);
                frag.addToBackStack(BACK_STACK_TAG);
                frag.commit();
            }
        } else if (id == R.id.nav_test) {

        } else if (id == R.id.nav_event) {
           openEventFrag();
        } else if (id == R.id.nav_suggestions) {

        }else if (id == R.id.nav_management) {

        }else if (id==R.id.nav_formdownload) {
            ApplicationForm myform=new ApplicationForm();
            frag = getSupportFragmentManager().beginTransaction();
            frag.replace(R.id.main_con,myform);
            frag.addToBackStack(BACK_STACK_TAG);
            frag.commit();
        }else if (id == R.id.nav_settings) {

        }else if (id == R.id.nav_log_out) {
            String cloudID = userDataSP.getUserData(UserDataSP.CLOUD_ID);
            userDataSP.clearUserData();
            userDataSP.storeCloudId(cloudID);
            MyDataBase myDataBase = new MyDataBase(getApplicationContext());
            myDataBase.clearDb();
            startActivity(new Intent(getApplicationContext(), Login.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void openEventFrag() {
        EventAll blankFragment = new EventAll();
        frag = getSupportFragmentManager().beginTransaction();
        frag.replace(R.id.main_con, blankFragment);
        frag.addToBackStack(BACK_STACK_TAG);
        frag.commit();
    }
}
