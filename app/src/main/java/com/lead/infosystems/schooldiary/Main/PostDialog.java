package com.lead.infosystems.schooldiary.Main;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lead.infosystems.schooldiary.Data.UserDataSP;
import com.lead.infosystems.schooldiary.Generic.CompressImage;
import com.lead.infosystems.schooldiary.Generic.MyVolley;
import com.lead.infosystems.schooldiary.ICompressedImage;
import com.lead.infosystems.schooldiary.IVolleyResponse;
import com.lead.infosystems.schooldiary.R;
import com.lead.infosystems.schooldiary.Generic.Utils;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadStatusDelegate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class PostDialog extends DialogFragment implements ICompressedImage{

    private View rootView;
    private EditText postText;
    private Button postBtn;
    private ImageView postImage;
    private Bitmap uploadImage;
    private String textData;
    private String encoded_image;
    private ProgressDialog progressDialog;
    private UserDataSP userDataSP;
    private final int MAX_IMAGE_SIZE = 150;
    private CompressImage compressImage;
    private Bitmap postImageBitmap;
    private String imageFilePath;
    public PostDialog() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.new_post_dialog,container,false);
        getDialog().setTitle("New Post");
        postBtn = (Button) rootView.findViewById(R.id.post_btn);
        postText = (EditText) rootView.findViewById(R.id.post_text);
        postImage = (ImageView) rootView.findViewById(R.id.upload_post_image);
        userDataSP = new UserDataSP(getActivity().getApplicationContext());
        progressDialog = new ProgressDialog(getActivity());
        compressImage = new CompressImage(getActivity(),this);
        postText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            }
        });
        postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textData = postText.getText().toString().trim();
                progressDialog.setTitle("Uploading Post..");
                if(textData.length()>0 && imageFilePath != null){
                    uploadMultipart(imageFilePath,textData);
                }else if(textData.length() == 0 && imageFilePath.length()>0){
                    uploadMultipart(imageFilePath,"  ");
                }else if(textData.length()>0 && imageFilePath == null){
                    postWithoutImage(textData);
                }
            }
        });
        return rootView;
    }


    private void selectImage() {
        final CharSequence[] items = { "Camera", "Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Camera")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(android.os.Environment
                            .getExternalStorageDirectory(), Utils.TEMP_IMG);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, 0);

                } else if (items[item].equals("Gallery")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            1);
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case 0:
                if(resultCode == getActivity().RESULT_OK){
                    postImageBitmap = Utils.getCameraImage();
                    compressImage.setImg(postImageBitmap);
                    compressImage.execute();
                }
                break;
            case 1:
                if(resultCode == getActivity().RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    InputStream img = null;
                    try {
                        img  = getActivity().getContentResolver().openInputStream(selectedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    if(img != null){
                        postImageBitmap = BitmapFactory.decodeStream(img);
                        compressImage.setImg(postImageBitmap);
                        compressImage.execute();
                    }
                }

                break;

        }

    }

    @Override
    public void compressedImageFile(File imageFile) {
        if(imageFile != null){
            postImage.setImageDrawable(Drawable.createFromPath(imageFile.getPath()));
            imageFilePath = imageFile.getPath();
        }else{
            Toast.makeText(getActivity().getApplicationContext(),"Some Error Occurred",Toast.LENGTH_SHORT).show();
        }
    }
    private void uploadMultipart(String path,String textData) {
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(100);
        progressDialog.show();
        if (path == null) {
            Toast.makeText(getActivity().getApplicationContext(), "Please move your image to internal storage and retry", Toast.LENGTH_LONG).show();
        } else {
            try {
                String uploadId = UUID.randomUUID().toString();
                new MultipartUploadRequest(getActivity().getApplicationContext(), uploadId, Utils.NEW_POST)
                        .addFileToUpload(path, "jpg")
                        .addParameter(UserDataSP.NUMBER_USER,userDataSP.getUserData(UserDataSP.NUMBER_USER))
                        .addParameter(Utils.POST_TEXT,textData)
                        .setMaxRetries(2)
                        .setDelegate(new UploadStatusDelegate() {
                            @Override
                            public void onProgress(UploadInfo uploadInfo) {
                                progressDialog.setProgress(uploadInfo.getProgressPercent());
                            }

                            @Override
                            public void onError(UploadInfo uploadInfo, Exception exception) {
                                Toast.makeText(getActivity().getApplicationContext(),"Failed",Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }

                            @Override
                            public void onCompleted(UploadInfo uploadInfo, ServerResponse serverResponse) {
                                success(serverResponse.getBodyAsString());
                            }

                            @Override
                            public void onCancelled(UploadInfo uploadInfo) {
                                Toast.makeText(getActivity().getApplicationContext(),"Cancelled",Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        })
                .startUpload();
            } catch (Exception exc) {
                Toast.makeText(getActivity().getApplicationContext(), exc.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }
    }

    private void postWithoutImage(String textData){
        progressDialog.setMessage("Please Wait..");
        progressDialog.show();
        MyVolley myVolley = new MyVolley(getActivity().getApplicationContext(), new IVolleyResponse() {
            @Override
            public void volleyResponse(String result) {
                success(result);
            }
        });
        myVolley.setUrl(Utils.NEW_POST);
        myVolley.setParams(UserDataSP.NUMBER_USER,userDataSP.getUserData(UserDataSP.NUMBER_USER));
        myVolley.setParams("text", textData);
        myVolley.connect();
    }

    private void success(String result){
        if(result.contains("post_id")){
            Toast.makeText(getActivity(),"Done",Toast.LENGTH_SHORT).show();
            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(result);
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                FragTabHome.addPostedItem(PostDialog.this.textData,jsonObject.getString("src_link")
                        ,jsonObject.getString("date"),jsonObject.getString("post_id"));
                getDialog().dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            Toast.makeText(getActivity(),"Failed",Toast.LENGTH_SHORT).show();
        }
        progressDialog.dismiss();
    }
}
