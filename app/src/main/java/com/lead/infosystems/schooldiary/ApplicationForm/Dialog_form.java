package com.lead.infosystems.schooldiary.ApplicationForm;

import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.lead.infosystems.schooldiary.Data.UserDataSP;
import com.lead.infosystems.schooldiary.Model_Paper.FilePath;
import com.lead.infosystems.schooldiary.R;
import com.lead.infosystems.schooldiary.Generic.Utils;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import java.util.UUID;

public class Dialog_form extends DialogFragment implements View.OnClickListener {
    private static final int RESULT_OK =-1 ;
    private Button  btn_upload;
    private EditText file_name;
    private View rootview;
    private UserDataSP userdatasp;
    private ImageView btn_choose;
    private String path;
    public static final String APPLICATION_NAME = "application_name";
    private int REQ_PDF = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootview= inflater.inflate(R.layout.dialog_layout,null);
        getDialog().setTitle("Upload PDF");
        btn_choose=(ImageView) rootview.findViewById(R.id.upload_pdf);
        btn_upload=(Button)rootview.findViewById(R.id.button_post);
        file_name=(EditText)rootview.findViewById(R.id.editText_name);
        btn_choose.setOnClickListener(this);
        btn_upload.setOnClickListener(this);
        userdatasp=new UserDataSP(getActivity().getApplicationContext());
        return rootview;
    }

    public void uploadMultipart(String path) {
        String name = file_name.getText().toString().trim();

        if (path == null) {
            Toast.makeText(getActivity().getApplicationContext(), "Please move your .pdf file to internal storage and retry", Toast.LENGTH_LONG).show();
        } else {
            if (name.length()>3) {
                try {
                    String uploadId = UUID.randomUUID().toString();
                    new MultipartUploadRequest(getActivity().getApplicationContext(), uploadId, Utils.APPLICATION_FORMS)
                            .addFileToUpload(path, "pdf")
                            .addParameter(APPLICATION_NAME, name)
                            .addParameter(UserDataSP.SCHOOL_NUMBER,userdatasp.getUserData(UserDataSP.SCHOOL_NUMBER))
                            .addParameter(UserDataSP.NUMBER_USER,userdatasp.getUserData(UserDataSP.NUMBER_USER))
                            .setNotificationConfig(new UploadNotificationConfig())
                            .setMaxRetries(2)
                            // broadcast
                            .startUpload();

                } catch (Exception exc) {
                    Toast.makeText(getActivity().getApplicationContext(), "Failed to upload", Toast.LENGTH_SHORT).show();
                }
                getDialog().dismiss();
            }else {
                Toast.makeText(getActivity().getApplicationContext(),"File name too short",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Pdf"), REQ_PDF);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_PDF && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            path = FilePath.getPath(getActivity().getApplicationContext(), filePath);
            String[] s = path.split("/");
            String fileName = s[s.length - 1].replace(".pdf", "");
            file_name.setText(fileName);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == btn_choose) {
            showFileChooser();
        }
        if (v == btn_upload) {
            uploadMultipart(path);
        }
    }
}
