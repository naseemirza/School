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
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadStatusDelegate;

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
    UserDataSP userDataSp;
    EditText exam_name, exam_date, exam_description, exam_pdf_name;
    Button button_upload;
    ImageView btn_pdf;
   // TextView exam_date_display;
    String path;
    private int PDF_REQ = 1;
    private static final int DIALOG_ID = 0;
   // private int year_e;
   // private int month_e;
   // private int day_e;
   // ImageView btn_date;
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
        exam_date = (EditText)rootView.findViewById(R.id.examDate_test);
        //exam_date_display = (TextView) rootView.findViewById(exam_date_display);
        //btn_date = (ImageView) rootView.findViewById(R.id.button_date_exam);
        exam_description = (EditText) rootView.findViewById(R.id.examDescription);
        exam_pdf_name = (EditText) rootView.findViewById(R.id.examPDFName);
        btn_pdf = (ImageView) rootView.findViewById(R.id.uploadexam_pdf);
        button_upload = (Button) rootView.findViewById(R.id.button_upload_exam);
        btn_pdf.setOnClickListener(this);
        button_upload.setOnClickListener(this);
       // final Calendar calendar_date= Calendar.getInstance();
       // year_e = calendar_date.get(Calendar.YEAR);
       // month_e = calendar_date.get(Calendar.MONTH) + 1;
       // day_e = calendar_date.get(Calendar.DAY_OF_MONTH);
       // exam_date_display.setText(day_e + "/" + month_e + "/" + year_e);
       // showDialogOnButtonClickDate();
        return rootView;
    }

//    public void showDialogOnButtonClickDate()
//    {
//        btn_date.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getActivity().showDialog(DIALOG_ID);
//            }
//        });
//    }
//
//      @Override
//      protected Dialog onCreateDialog(int id)
//      {
//          if(id==DIALOG_ID);
//               return new DatePickerDialog(getActivity().getApplicationContext(), dpickerListener, year_e, month_e, day_e);
//          return null;
//      }
//      private DatePickerDialog.OnDateSetListener dpickerListener = new DatePickerDialog.OnDateSetListener() {
//          @Override
//          public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//              year_e=year;
//              month_e = monthOfYear;
//              day_e = dayOfMonth;
//              exam_date_display.setText(day_e+"/"+month_e+"/"+year_e);
//          }
//      };

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

                                }

                                @Override
                                public void onCompleted(UploadInfo uploadInfo, ServerResponse serverResponse) {
                                   Log.e("server response", serverResponse.toString());
                                    Log.e("server reponse 2", serverResponse.getBodyAsString());
                                    Log.e("server response 3 ", String.valueOf(serverResponse.getBody()));
                                    result = serverResponse.getBodyAsString();

                                }
                                @Override
                                public void onCancelled(UploadInfo uploadInfo) {

                                }
                            })
                            .startUpload();

                    parseExamData(exam_name.getText().toString().trim(),exam_date.getText().toString().trim(), exam_description.getText().toString().trim(), path , result , userDataSp.getUserData(UserDataSP.NUMBER_USER));

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
    public void parseExamData(String examName, String examDate, String examDescription, String examPdfLink, String submissionDate, String uploadUser )
    {
        Intent intent = new Intent(INTENTFILTEREXAM);
        intent.putExtra(EXAM_NAME, examName);
        intent.putExtra(EXAM_DATE, examDate);
        intent.putExtra(EXAM_DESCRIPTION, examDescription);
        intent.putExtra(EXAM_PDFLINK, examPdfLink);
        intent.putExtra(EXAM_SUBMISSION_DATE, submissionDate);
        intent.putExtra(EXAM_UPLOAD_USER, uploadUser);
        getActivity().sendBroadcast(intent);
    }

}
