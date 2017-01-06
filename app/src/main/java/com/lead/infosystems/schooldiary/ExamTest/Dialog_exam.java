package com.lead.infosystems.schooldiary.ExamTest;


import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.lead.infosystems.schooldiary.Data.UserDataSP;
import com.lead.infosystems.schooldiary.Generic.Utils;
import com.lead.infosystems.schooldiary.Model_Paper.FilePath;
import com.lead.infosystems.schooldiary.R;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import java.util.UUID;

import static android.app.Activity.RESULT_OK;

public class Dialog_exam extends DialogFragment implements View.OnClickListener{

    UserDataSP userDataSp;
    EditText exam_name, exam_date, exam_description, exam_pdf_name;
    Button button_upload;
    ImageView btn_pdf;
    String path;
    private int PDF_REQ = 1;
    public Dialog_exam() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.dialog_exam_layout, container, false);
        getDialog().setTitle("Upload PDF");
        userDataSp = new UserDataSP(getActivity().getApplicationContext());
        exam_name = (EditText) rootView.findViewById(R.id.examName);
        exam_date = (EditText) rootView.findViewById(R.id.examDate);
        exam_description = (EditText) rootView.findViewById(R.id.examDescription);
        exam_pdf_name = (EditText)rootView.findViewById(R.id.examPDFName);
        btn_pdf =(ImageView)rootView.findViewById(R.id.uploadexam_pdf);
        button_upload =(Button)rootView.findViewById(R.id.button_upload_exam);
        btn_pdf.setOnClickListener(this);
        button_upload.setOnClickListener(this);



        return rootView;
    }


    public void uploadMultipart(String path) {
        String name = exam_pdf_name.getText().toString().trim();

        if (path == null) {
            Toast.makeText(getActivity().getApplicationContext(), "Please move your .pdf file to internal storage and retry", Toast.LENGTH_LONG).show();
        } else {
            if (name.length()>3) {
                try {
                    String uploadId = UUID.randomUUID().toString();
                    new MultipartUploadRequest(getActivity().getApplicationContext(), uploadId, Utils.EXAM_DETAIL)
                            .addFileToUpload(path, "pdf")
                            .addParameter("name", exam_name.getText().toString().trim())
                            .addParameter("exam_date",exam_date.getText().toString().trim())
                            .addParameter("exam_description", exam_description.getText().toString().trim())
                            .addParameter("school_number",userDataSp.getUserData(UserDataSP.SCHOOL_NUMBER))
                            .setNotificationConfig(new UploadNotificationConfig())
                            .setMaxRetries(2)
                            .startUpload();

                } catch (Exception exc) {
                    Toast.makeText(getActivity().getApplicationContext(), exc.getMessage(), Toast.LENGTH_SHORT).show();
                }
                getDialog().dismiss();
            }else {
                Toast.makeText(getActivity().getApplicationContext(),"File Name Length Should Be Atleast 4",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select PDF"), PDF_REQ);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PDF_REQ && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            Log.e("data :::::", String.valueOf(data.getData()));
            Log.e("path_file", String.valueOf(filePath));

            path = FilePath.getPath(getActivity().getApplicationContext(), filePath);
            Log.e("path_file::::", path);
            String[] s = path.split("/");
            Log.e("string:::::", String.valueOf(s));
            String fileName = s[s.length - 1].replace(".txt", "");
            exam_pdf_name.setText(fileName);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == btn_pdf) {
            showFileChooser();
        }
        if (v == button_upload) {
            uploadMultipart(path);
        }
    }


}
