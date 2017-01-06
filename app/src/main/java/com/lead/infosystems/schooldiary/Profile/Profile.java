package com.lead.infosystems.schooldiary.Profile;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.lead.infosystems.schooldiary.Data.Post_Data;
import com.lead.infosystems.schooldiary.Data.UserDataSP;
import com.lead.infosystems.schooldiary.Generic.CompressImage;
import com.lead.infosystems.schooldiary.ICompressedImage;
import com.lead.infosystems.schooldiary.IPostInterface;
import com.lead.infosystems.schooldiary.IVolleyResponse;
import com.lead.infosystems.schooldiary.Main.PostAdaptor;
import com.lead.infosystems.schooldiary.Main.PostAnimData;
import com.lead.infosystems.schooldiary.Main.PostComments;
import com.lead.infosystems.schooldiary.R;
import com.lead.infosystems.schooldiary.Generic.MyVolley;
import com.lead.infosystems.schooldiary.Generic.ServerConnect;
import com.lead.infosystems.schooldiary.Generic.Utils;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadStatusDelegate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class Profile extends AppCompatActivity implements IPostInterface,SwipeRefreshLayout.OnRefreshListener,IVolleyResponse,ICompressedImage {

    private static ArrayList<Post_Data> itemlist = new ArrayList<Post_Data>();;
    private SwipeRefreshLayout swipeRefreshLayout;
    private static PostAdaptor postAdaptor;
    private String POST_MIN = "0";
    private RecyclerView recyclerView;
    private JSONArray jsonPost,jsonLikes;
    private boolean backPressed = false;
    private boolean noMorePost = false;
    private MyVolley myVolley;
    private UserDataSP userDataSP;
    private Toolbar toolbar;
    private ImageView proPic;
    private ImageButton proPicChange;
    private Activity activity;
    private Bitmap newProPic;
    private CompressImage compressImage;
    private ProgressBar proPicProgressBar;
    private Target target;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        userDataSP = new UserDataSP(getApplicationContext());
        toolbar.setTitle(userDataSP.getUserData(UserDataSP.FIRST_NAME)+" "+userDataSP.getUserData(UserDataSP.LAST_NAME));
        proPicChange = (ImageButton) findViewById(R.id.change_propic);
        activity = this;
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        prepare();
        mPropicChange();
    }

    private void mPropicChange() {
        proPicChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater factory = LayoutInflater.from(activity);
                final View deleteDialogView = factory.inflate(R.layout.propic_dialog, null);
                final AlertDialog deleteDialog = new AlertDialog.Builder(activity).create();
                deleteDialog.setView(deleteDialogView);
                deleteDialogView.findViewById(R.id.propic_camera).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        File f = new File(android.os.Environment
                                .getExternalStorageDirectory(), Utils.TEMP_IMG);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                        startActivityForResult(intent, 0);
                        deleteDialog.dismiss();
                    }
                });
                deleteDialogView.findViewById(R.id.propic_gallery).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(
                                Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/*");
                        startActivityForResult(
                                Intent.createChooser(intent, "Select File"),
                                1);
                        deleteDialog.dismiss();
                    }
                });

                deleteDialog.show();
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        compressImage = new CompressImage(activity,this);
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case 0:
                if(resultCode == activity.RESULT_OK){
                    File f = new File(Environment.getExternalStorageDirectory()
                            .toString());
                    for (File temp : f.listFiles()) {
                        if (temp.getName().equals(Utils.TEMP_IMG)) {
                            f = temp;
                            break;
                        }
                    }
                   crop(Uri.fromFile(f));
                }
                break;
            case 1:
                if(resultCode == activity.RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    crop(selectedImage);
                }
                break;

            case 99:
                if(resultCode == activity.RESULT_OK){
                    try {
                        newProPic = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageReturnedIntent.getData());
                        if(newProPic != null){
                            compressImage.setImg(newProPic);
                            compressImage.execute();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                break;

        }}

    private void crop(Uri uri){
        String appPackageName = "com.android.camera.action.CROP";

        try {
            File file = new File(android.os.Environment
                    .getExternalStorageDirectory(), Utils.TEMP_IMG);
            Intent cropIntent = new Intent(appPackageName);
            cropIntent.setDataAndType(uri, "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputX", 480);
            cropIntent.putExtra("outputY", 480);
            cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
            startActivityForResult(cropIntent, 99);
        }catch (Exception e){
        Toast.makeText(getApplicationContext(),"You Dont have any image cropping app installed..",Toast.LENGTH_LONG).show();
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.plus")));
        }
    }
    @Override
    public void compressedImageFile(File imageFile) {
        if(imageFile != null){
            uploadMultipart(imageFile.getPath());
            proPic.setAlpha(0.3f);
        }else{
            Toast.makeText(getApplicationContext(),"Some Error Occurred",Toast.LENGTH_SHORT).show();
        }
    }
    private void uploadMultipart(final String path) {
        if (path == null) {
            Toast.makeText(getApplicationContext(), "Please move your image to internal storage and retry", Toast.LENGTH_LONG).show();
        } else {
            try {
                String uploadId = UUID.randomUUID().toString();
                proPicProgressBar.setVisibility(View.VISIBLE);
                proPicProgressBar.setMax(100);
                new MultipartUploadRequest(getApplicationContext(), uploadId, Utils.PROPIC_UPDATE)
                        .addFileToUpload(path, "jpg")
                        .addParameter(UserDataSP.NUMBER_USER,userDataSP.getUserData(UserDataSP.NUMBER_USER))
                        .setMaxRetries(2)
                        .setDelegate(new UploadStatusDelegate() {
                    @Override
                    public void onProgress(UploadInfo uploadInfo) {
                        proPicProgressBar.setProgress(uploadInfo.getProgressPercent());
                    }

                    @Override
                    public void onError(UploadInfo uploadInfo, Exception exception) {
                        proPicProgressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_SHORT).show();
                        proPic.setAlpha(1f);
                    }

                    @Override
                    public void onCompleted(UploadInfo uploadInfo, ServerResponse serverResponse) {
                        proPicProgressBar.setVisibility(View.GONE);
                        proPic.setBackgroundDrawable(Drawable.createFromPath(path));
                        Toast.makeText(getApplicationContext(),"Done",Toast.LENGTH_SHORT).show();
                        proPic.setAlpha(1f);


                        loadProPic(true);
                    }

                    @Override
                    public void onCancelled(UploadInfo uploadInfo) {
                        proPicProgressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(),"Cancelled",Toast.LENGTH_SHORT).show();
                        proPic.setAlpha(1f);
                    }
                })
                .startUpload();
            } catch (Exception exc) {
                Toast.makeText(getApplicationContext(), exc.getMessage(), Toast.LENGTH_SHORT).show();
                proPic.setAlpha(1f);
            }
        }
    }
    private void prepare(){
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        proPic = (ImageView) findViewById(R.id.pro_pic_toolbar);
        if(userDataSP.getUserData(UserDataSP.PROPIC_URL).contains("jpeg")){
           loadProPic(false);
        }
        proPicProgressBar = (ProgressBar) findViewById(R.id.propic_progress);
        proPicProgressBar.setVisibility(View.GONE);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setNestedScrollingEnabled(true);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        postAdaptor = new PostAdaptor(this, this);
        postAdaptor.setLinearLayoutManager(linearLayoutManager);
        postAdaptor.setRecyclerView(recyclerView);
        recyclerView.setAdapter(postAdaptor);
        recyclerView.getItemAnimator().setChangeDuration(0);
        userDataSP = new UserDataSP(getApplicationContext());
        swipeRefreshLayout.setColorSchemeResources(
                R.color.colorAccent,
                R.color.colorPrimary,
                R.color.colorPrimaryDark);
        swipeRefreshLayout.setOnRefreshListener(this);
        myVolley = new MyVolley(getApplicationContext(),this);
        swipeRefreshLayout.setEnabled(false);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int toolBarHeight = toolbar.getMeasuredHeight();
                int appBarHeight = appBarLayout.getTotalScrollRange() + toolBarHeight;
                Float f = ((((float) appBarHeight - toolBarHeight) + verticalOffset) / ( (float) appBarHeight - toolBarHeight)) * 255;
                proPic.getBackground().setAlpha(Math.round(f));
            }
        });
    }

    private void loadProPic(boolean refresh){
        proPic.setMinimumWidth(getWindowManager().getDefaultDisplay().getHeight());
        target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                proPic.setBackgroundDrawable(new BitmapDrawable(bitmap));
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Log.e("picasso","fail");
                proPic.setBackgroundDrawable(getResources().getDrawable(R.drawable.defaultpropic));
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                proPic.setBackgroundDrawable(placeHolderDrawable);
            }
        };

        if(refresh){
            Picasso.with(getApplicationContext())
                    .invalidate(Utils.SERVER_URL+userDataSP.getUserData(UserDataSP.PROPIC_URL));
            Picasso.with(getApplicationContext())
                    .invalidate(Utils.SERVER_URL+userDataSP.getUserData(UserDataSP.PROPIC_URL).replace("profilepic","propic_thumb"));
            Picasso.with(getApplicationContext())
                    .load(Utils.SERVER_URL+userDataSP.getUserData(UserDataSP.PROPIC_URL))
                    .placeholder(R.drawable.defaultpropic)
                    .networkPolicy(
                            ServerConnect.checkInternetConenction(this) ?
                                    NetworkPolicy.NO_CACHE : NetworkPolicy.OFFLINE)
                    .into(target);
            recyclerView.setAdapter(postAdaptor);
        }else{
            Picasso.with(getApplicationContext())
                    .load(Utils.SERVER_URL+userDataSP.getUserData(UserDataSP.PROPIC_URL))
                    .placeholder(R.drawable.defaultpropic)
                    .into(target);
        }
    }

    @Override
    public void onStart() {
        itemlist.clear();
        noMorePost = false;
        if(ServerConnect.checkInternetConenction(this) && !backPressed){
            refresh();
        }else{
            if(userDataSP.getPostData()!=""){
                swipeRefreshLayout.setRefreshing(false);
                postAdaptor.addAll(getJsonData(userDataSP.getPostData()));
            }
        }
        super.onStart();
    }


    private void getData(String min){
        myVolley.setUrl(Utils.POST_FETCH);
        myVolley.setParams(Utils.POST_FETCH_PARAM, min);
        myVolley.setParams(UserDataSP.NUMBER_USER,userDataSP.getUserData(UserDataSP.NUMBER_USER));
        myVolley.connect();
    }

    @Override
    public void volleyResponse(String result) {
        try {
            storeData(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void storeData(String s) throws JSONException {
        swipeRefreshLayout.setEnabled(false);
        if(!s.isEmpty() && s != "ERROR") {
            itemlist.clear();
            if (POST_MIN == "0") {
                swipeRefreshLayout.setRefreshing(false);
                postAdaptor.addAll(getJsonData(s));
                userDataSP.storePostData(s);
            } else {
                postAdaptor.setProgressMore(false);
                postAdaptor.addItemMore(getJsonData(s));
                postAdaptor.setMoreLoading(false);
                userDataSP.appendToPostData(s);
            }

        }else{
            Toast.makeText(getApplicationContext(),"No more posts...",Toast.LENGTH_SHORT).show();
            noMorePost = true;
            postAdaptor.setProgressMore(false);
            postAdaptor.setMoreLoading(false);
        }
    }
    private ArrayList<Post_Data> getJsonData(String jsonString){

        JSONObject jsonPostObj = null;
        JSONObject jsonLikeObj = null;
        ArrayList<String> likedStudents = new ArrayList<>();
        try {
            jsonPost = new JSONArray(jsonString);
            for (int i = 0; i <= jsonPost.length() - 1; i++) {
                boolean isLiked = false;
                jsonPostObj = jsonPost.getJSONObject(i);
                //get likes list
                likedStudents = new ArrayList<>();
                if(jsonPostObj.getString("like") != "null"){
                    jsonLikes = new JSONArray(jsonPostObj.getString("like"));
                    for(int j = 0 ; j < jsonLikes.length();j++){
                        jsonLikeObj = jsonLikes.getJSONObject(j);
                        String sNo = jsonLikeObj.getString("number_user");
                        if(Integer.parseInt(sNo) == Integer.parseInt(userDataSP.getUserData(UserDataSP.NUMBER_USER)))
                        {isLiked = true;}
                        likedStudents.add(sNo);
                    }
                }

                itemlist.add(new Post_Data(jsonPostObj.getString(UserDataSP.NUMBER_USER),jsonPostObj.getString(UserDataSP.PROPIC_URL),
                        jsonPostObj.getString(UserDataSP.FIRST_NAME), jsonPostObj.getString(UserDataSP.LAST_NAME),
                        jsonPostObj.getString("post_id"), jsonPostObj.getString("text_message"),
                        jsonPostObj.getString("src_link"), jsonPostObj.getString("date"),isLiked,likedStudents,
                        jsonPostObj.getString("num_comment")));
                if (i == jsonPost.length() - 1) {
                    POST_MIN = jsonPostObj.getString("post_id");
                }
            }
            return itemlist;
        } catch (JSONException e) {
            e.printStackTrace();
            itemlist.clear();
            return itemlist;
        }
    }
    @Override
    public void onRefresh() {
        refresh();
    }

    private void refresh(){
        swipeRefreshLayout.setEnabled(true);
        swipeRefreshLayout.setRefreshing(true);
        POST_MIN = "0";
        noMorePost = false;
        postAdaptor.setMoreLoading(false);
        getData(POST_MIN);
    }
    @Override
    public void onLoadMore() {
        if(ServerConnect.checkInternetConenction(this) && !noMorePost){
            getData(POST_MIN);
            postAdaptor.setProgressMore(true);
        }
    }

    @Override
    public void onCommentClick(PostAnimData postAnimData) {
        ActivityOptionsCompat activityOptionsCompat = null;
        backPressed = true;

        Intent intent = new Intent(getApplicationContext(),PostComments.class);
//        Bundle b = new Bundle();
//        b.putSerializable(PostComments.ANIM_DATA,postAnimData);
//        intent.putExtras(b);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Pair<View,String> p1 = Pair.create(postAnimData.getName(), postAnimData.getName().getTransitionName());
            Pair<View,String> p2 = Pair.create(postAnimData.getTime(), postAnimData.getTime().getTransitionName());
            Pair<View,String> p3 = Pair.create(postAnimData.getText(), postAnimData.getText().getTransitionName());
            Pair<View,String> p4 = Pair.create(postAnimData.getPropic(), postAnimData.getPropic().getTransitionName());
            if(postAnimData.isImageAvailable()){
                Pair<View,String> p5 = Pair.create(postAnimData.getPost_img(), postAnimData.getPost_img().getTransitionName());
                activityOptionsCompat = ActivityOptionsCompat
                        .makeSceneTransitionAnimation(this,p1,p2,p3,p4,p5);
            }else{
                activityOptionsCompat = ActivityOptionsCompat
                        .makeSceneTransitionAnimation(this,p1,p2,p3,p4);
            }
            startActivity(intent,activityOptionsCompat.toBundle());
        }else{
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_settings){
            refresh();
        }else if(id == R.id.action_settings){
            //startActivity(new Intent(this,ProfileEdit.class));
        }
        return super.onOptionsItemSelected(item);
    }


}
