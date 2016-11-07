package com.lead.infosystems.schooldiary.Main;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Base64;
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
import com.lead.infosystems.schooldiary.R;
import com.lead.infosystems.schooldiary.ServerConnection.Utils;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


public class PostDialog extends DialogFragment {

    View rootView;
    EditText postText;
    Button postBtn;
    ImageView postImage;
    Bitmap uploadImage;
    String textData;
    String encoded_image;
    ProgressDialog progressDialog;
    UserDataSP userDataSP;
    IRefereshHome iRefereshHome;

    public interface IRefereshHome{
        void refereshHome();
    }

    public PostDialog(IRefereshHome iRefereshHome) {
        this.iRefereshHome = iRefereshHome;
    }
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
                if(!userDataSP.getUserData(UserDataSP.STUDENT_NUMBER).isEmpty()){
                    if(!textData.isEmpty() || uploadImage != null){
                        makeRequest(encoded_image);
                    }else{
                        Toast.makeText(getActivity(),"No Image or Text..",Toast.LENGTH_SHORT).show();
                    }
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
                            .getExternalStorageDirectory(), "temp.jpg");
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
                    getCameraImage();
                }
                new UploadImage().execute();
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
                        uploadImage = BitmapFactory.decodeStream(img);
                        new UploadImage().execute();
                    }
                }

                break;

        }

    }
    private void getCameraImage(){
        File f = new File(Environment.getExternalStorageDirectory()
                .toString());
        for (File temp : f.listFiles()) {
            if (temp.getName().equals("temp.jpg")) {
                f = temp;
                break;
            }
        }
        try {
            BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
            uploadImage = BitmapFactory.decodeFile(f.getAbsolutePath(),
                    btmapOptions);
            new UploadImage().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private class UploadImage extends AsyncTask<Void,Void,Boolean>{
        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Compressing Image please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if(uploadImage != null){
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                uploadImage.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
                int size = byteArrayOutputStream.toByteArray().length/1024;
                if(size>350) {
                    Bitmap b = uploadImage;
                    uploadImage = null;
                    int wr = 16;
                    int hr = 9;
                    int res = 100;
                    int quality = 100;
                    uploadImage = resize(b, wr*res, hr*res);
                    do {
                        b = uploadImage;
                        uploadImage = null;
                        byteArrayOutputStream = new ByteArrayOutputStream();
                        b.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream);
                        uploadImage = BitmapFactory.decodeByteArray(byteArrayOutputStream.toByteArray(),0,
                                        byteArrayOutputStream.toByteArray().length);
                        size = byteArrayOutputStream.toByteArray().length / 1024;
                        quality = quality - 10;
                        if(quality < 40){
                            res = res -10;
                            uploadImage = resize(uploadImage,wr *res, hr*res );
                            quality = 100;}
                    }while (size > 350);
                }
                encoded_image = Base64.encodeToString(byteArrayOutputStream.toByteArray(),0);
                return true;
            }else{
                return false;
            }
        }
        @Override
        protected void onPostExecute(Boolean send) {
            progressDialog.dismiss();
            if(send){
                postImage.setImageBitmap(uploadImage);
            }
        }

    }

    public static Bitmap resize(Bitmap image, int maxWidth, int maxHeight) {
        if (maxHeight > 0 && maxWidth > 0) {
            int width = image.getWidth();
            int height = image.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;

            int finalWidth = maxWidth;
            int finalHeight = maxHeight;
            if (ratioMax > 1) {
                finalWidth = (int) ((float)maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float)maxWidth / ratioBitmap);
            }
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
            return image;
        } else {
            return image;
        }
    }

    public void makeRequest(final String imageData){
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(true);
        progressDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, Utils.SERVER_URL + "post.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        if(response.contains("DONE")){
                            Toast.makeText(getActivity(),"Done",Toast.LENGTH_SHORT).show();
                            iRefereshHome.refereshHome();
                            getDialog().dismiss();
                        }else{
                            Toast.makeText(getActivity(),"Failed",Toast.LENGTH_SHORT).show();
                        }
                        new FragTabHome();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.getStackTrace();
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(),"Failed",Toast.LENGTH_SHORT).show();
                    }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map =  new HashMap<>();
                if(userDataSP.getUserData(UserDataSP.STUDENT_NUMBER) != null){
                map.put(UserDataSP.STUDENT_NUMBER,userDataSP.getUserData(UserDataSP.STUDENT_NUMBER));
                    if(textData != null){
                        map.put("text",textData);
                    }
                    if(imageData != null){
                        map.put("encoded_image",imageData);
                    }
                }
                return map;
            }
        };
        int socketTimeout = 20000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        requestQueue.add(request);

    }


}
