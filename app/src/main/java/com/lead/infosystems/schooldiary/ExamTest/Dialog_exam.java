package com.lead.infosystems.schooldiary.ExamTest;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lead.infosystems.schooldiary.Data.UserDataSP;
import com.lead.infosystems.schooldiary.Generic.Utils;
import com.lead.infosystems.schooldiary.Model_Paper.FilePath;
import com.lead.infosystems.schooldiary.R;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadStatusDelegate;

import java.util.Calendar;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

public class Dialog_exam extends DialogFragment implements View.OnClickListener{
    public static final String INTENTFILTEREXAM = "intent_filter_exam";
    public static final String EXAM_NAME = "exam_name";
    public static final String EXAM_DATE = "exam_date";
    public static final String EXAM_DESCRIPTION = "exam_description";
    public static final String EXAM_PDFLINK = "exam_pdf_link";
    public static final String EXAM_SUBMISSION_DATE = "submission_date";
    public static final String EXAM_UPLOAD_USER = "upload_user";
    private UserDataSP userDataSp;
    private EditText exam_name, exam_date, exam_description, exam_pdf_name;
    private Button button_upload;
    private ImageView btn_pdf;
    private TextView exam_date_display;
    private String path;
    private int PDF_REQ = 1;
    private static final int DIALOG_ID = 0;
    private int year_e;
    private int month_e;
    private int day_e;
    ImageView btn_date;
    View rootView;
    String result;

    public Dialog_exam() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        rootView = inflater.inflate(R.layout.dialog_exam_layout, container, false);
        getDialog().setTitle("Upload PDF");
        userDataSp = new UserDataSP(getActivity().getApplicationContext());
        exam_name = (EditText) rootView.findViewById(R.id.examName);
       // exam_date = (EditText)rootView.findViewById(R.id.examDate_test);
        exam_date_display = (TextView) rootView.findViewById(R.id.exam_date_display);
        btn_date = (ImageView) rootView.findViewById(R.id.button_date_exam);
        exam_description = (EditText) rootView.findViewById(R.id.examDescription);
        exam_pdf_name = (EditText) rootView.findViewById(R.id.examPDFName);
        btn_pdf = (ImageView) rootView.findViewById(R.id.uploadexam_pdf);
        button_upload = (Button) rootView.findViewById(R.id.button_upload_exam);
        btn_pdf.setOnClickListener(this);
        button_upload.setOnClickListener(this);
        final Calendar calendar_date= Calendar.getInstance();
        year_e = calendar_date.get(Calendar.YEAR);
        month_e = calendar_date.get(Calendar.MONTH) + 1;
        day_e = calendar_date.get(Calendar.DAY_OF_MONTH);
        exam_date_display.setText(day_e + "-" + month_e + "-" + year_e);
        showDialogOnButtonClickDate();
        return rootView;
    }

    public void showDialogOnButtonClickDate()
    {
        btn_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateDialog dateDialog = new DateDialog();
                android.app.FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
                dateDialog.show(ft,"DatePicker");
            }
        });
    }



    private class DateDialog extends DialogFragment implements DatePickerDialog.OnDateSetListener{

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int y = c.get(Calendar.YEAR);
            int m = c.get(Calendar.MONTH);
            int d = c.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(),this,y,m,d);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            year_e=year;
            month_e = monthOfYear;
            day_e = dayOfMonth;
            exam_date_display.setText(day_e+"-"+month_e+"-"+year_e);
        }
    }

    public void uploadMultipart(final String path) {
        String name = exam_pdf_name.getText().toString().trim();

        if (path == null) {
            Toast.makeText(getActivity().getApplicationContext(), "Please move your .pdf file to internal storage and retry", Toast.LENGTH_LONG).show();
        } else {
            final String examName =  exam_name.getText().toString();
            final String examDate = exam_date_display.getText().toString();
            final String examDis = exam_description.getText().toString();
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            if (name.length()>3) {
                try {

                    String uploadId = UUID.randomUUID().toString();
                    new MultipartUploadRequest(getActivity().getApplicationContext(), uploadId, Utils.EXAM_DETAIL)
                            .addFileToUpload(path, "pdf")
                            .addParameter("name", exam_name.getText().toString().trim())
                            .addParameter("exam_date",examDate+" 10:00:00")
                            .addParameter("exam_description", exam_description.getText().toString().trim())
                            .addParameter(UserDataSP.SCHOOL_NUMBER,userDataSp.getUserData(UserDataSP.SCHOOL_NUMBER))
                            .addParameter(UserDataSP.NUMBER_USER, userDataSp.getUserData(UserDataSP.NUMBER_USER))
                            .setNotificationConfig(new UploadNotificationConfig())
                            .setMaxRetries(2)
                            .setDelegate(new UploadStatusDelegate() {
                                @Override
                                public void onProgress(UploadInfo uploadInfo) {

                                }

                                @Override
                                public void onError(UploadInfo uploadInfo, Exception exception) {
                                    Toast.makeText(getActivity().getApplicationContext(), "Failed to submit",
                                            Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                    getDialog().dismiss();
                                }

                                @Override
                                public void onCompleted(UploadInfo uploadInfo, ServerResponse serverResponse) {
                                    result = serverResponse.getBodyAsString().replaceAll("DONE","");
                                    Intent intent = new Intent(INTENTFILTEREXAM);
                                    intent.putExtra(EXAM_NAME, examName);
                                    intent.putExtra(EXAM_DATE, examDate);
                                    intent.putExtra(EXAM_DESCRIPTION, examDis );
                                    intent.putExtra(EXAM_PDFLINK, result);
                                    intent.putExtra(EXAM_SUBMISSION_DATE, "");
                                    intent.putExtra(EXAM_UPLOAD_USER, userDataSp.getUserData(UserDataSP.NUMBER_USER));
                                    getActivity().sendBroadcast(intent);
                                    Log.e("res",result);
                                    progressDialog.dismiss();
                                    getDialog().dismiss();
                                }
                                @Override
                                public void onCancelled(UploadInfo uploadInfo) {
                                    progressDialog.dismiss();
                                    getDialog().dismiss();
                                }
                            })
                            .startUpload();


                } catch (Exception exc) {
                    Toast.makeText(getActivity().getApplicationContext(), "Failed to submit", Toast.LENGTH_SHORT).show();
                }

            }else {
                Toast.makeText(getActivity().getApplicationContext(),"File name too short",Toast.LENGTH_SHORT).show();
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
            path = FilePath.getPath(getActivity().getApplicationContext(), filePath);
            String[] s = path.split("/");
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
