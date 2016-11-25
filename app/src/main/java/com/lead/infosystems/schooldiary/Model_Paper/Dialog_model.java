package com.lead.infosystems.schooldiary.Model_Paper;

import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lead.infosystems.schooldiary.Data.UserDataSP;
import com.lead.infosystems.schooldiary.R;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import java.util.UUID;

/**
 * Created by Naseem on 17-11-2016.
 */

public class Dialog_model extends DialogFragment implements View.OnClickListener {
    private static final int RESULT_OK =-1 ;
    public Button btn_choose, btn_upload;
    EditText file_name;
    View rootview;
    UserDataSP userdatasp;

    public static final String Upload_url = "http://leadinfosystems.com/school_diary/SchoolDiary/model_paper_insert.php";

    private int pdf_reqst = 1;
    private static final int STORAGE_PERMISSION_CODE = 123;

    private Uri filePath;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootview= inflater.inflate(R.layout.dialog_layout,null);


        btn_choose=(Button)rootview.findViewById(R.id.button_chooser);
        btn_upload=(Button)rootview.findViewById(R.id.button_upload);
        file_name=(EditText)rootview.findViewById(R.id.editText_name);

        btn_choose.setOnClickListener(this);
        btn_upload.setOnClickListener(this);
        userdatasp=new UserDataSP(getActivity().getApplicationContext());


        return rootview;

    }

    public void uploadMultipart() {
        String name = file_name.getText().toString().trim();

        String path = FilePath.getPath(getActivity().getApplicationContext(), filePath);

        if (path == null) {

            Toast.makeText(getActivity().getApplicationContext(), "Please move your .pdf file to internal storage and retry", Toast.LENGTH_LONG).show();
        } else {
            try {
                String uploadId = UUID.randomUUID().toString();
                new MultipartUploadRequest(getActivity().getApplicationContext(), uploadId, Upload_url)
                        .addFileToUpload(path, "pdf")
                        .addParameter("name", name)
                        .addParameter("class",userdatasp.getUserData(UserDataSP.CLASS))
                        .setNotificationConfig(new UploadNotificationConfig())
                        .setMaxRetries(2)
                        .startUpload();

            } catch (Exception exc) {
                Toast.makeText(getActivity().getApplicationContext(), exc.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Pdf"), pdf_reqst);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == pdf_reqst && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == btn_choose) {
            showFileChooser();
        }
        if (v == btn_upload) {
            uploadMultipart();
        }
    }


    public void show(FragmentManager manager, String dialog_model) {

    }
}
