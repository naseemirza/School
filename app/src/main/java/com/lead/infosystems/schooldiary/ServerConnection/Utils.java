package com.lead.infosystems.schooldiary.ServerConnection;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Faheem on 10-10-2016.
 */

public class Utils {

    public static final String SERVER_URL = "http://leadinfosystems.com/school_diary/SchoolDiary/";
    public static final String LOGIN = SERVER_URL+"login.php";
    public static final String POST_FETCH = SERVER_URL+"fetchpost.php";
    public static final String NEW_POST = SERVER_URL + "post.php";
    public static final String LIKE = SERVER_URL+"like_comment.php";
    public static final String DELETE = SERVER_URL+"delete_post_like.php";
    public static final String COMMENTS = SERVER_URL+"comment_fetch.php";
    public static final String POST_FETCH_PARAM = "min";
    public static final String REGESTRATION = SERVER_URL+"reg_user.php";
    public static final String QA_FETCH = SERVER_URL+"question_answer_fetch.php";
    public static final String Q_SUBMIT = SERVER_URL+"question_post.php";
    public static final String ANSWER_SUBMIT = SERVER_URL+"answer_post.php";
    public static final String QA_DELETE = SERVER_URL+"question_answer_delete.php";
    public static final String CHAT_LIST = SERVER_URL+"chat_list.php";
    public static final String CHAT_CONTACT = SERVER_URL+"chat_contact.php";
    public static final String NOTIFY = SERVER_URL+"push_notification.php";
    public static final String ATTENDANCE = SERVER_URL+"attendance_insert.php";
    public static final String ATTENDANCE_FETCH = SERVER_URL+"attendance_fetch.php";
    public static final String NOTIFICATION_FETCH = SERVER_URL+"notification_fetch.php";
    public static final String MARKS = SERVER_URL+"marks.php";
    public static final String MODEL_PAPER = SERVER_URL+"model_paper_insert.php";
    public static final String GOOGLE_DRIVE_VIEWER = "http://drive.google.com/viewer?url=";
    public static final String APPLICATION_FORMS = SERVER_URL+"application_form_insert.php";
    public static final String EVENT_FETCH = SERVER_URL+"events_fetch.php";
    public static final String EVENT_INSERT = SERVER_URL+"events_insert.php";



   public static String getTimeString(String dateString){
       Date date;
       try {
           SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
           date = dateFormat.parse(dateString);
       String time = "";
        long seconds = (System.currentTimeMillis() - date.getTime())/1000;
        if(seconds < 60 ){
            time  = "seconds ago";
        }else if(seconds <  60 * 60){
            time = (int)(seconds /60)+" min ago";
        }else if(seconds < 86400){
            time = (int) (seconds / (60*60))+"hr ago";
        }else if(seconds < 604800){
            time = (int) (seconds / (60*60*24)) + " days ago";
        }else if(seconds > 518400) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM-dd,  HH:mm a");
            time = simpleDateFormat.format(new Date(date.getTime())).replace("  ", " at ");
        } else if((seconds/(60*60*24*30))>365){
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy-MMM-dd,  HH:mm a");
            time = simpleDateFormat.format(new Date(date.getTime())).replace("  ", " at ");
        }
        return time;
       } catch (ParseException e) {
           e.printStackTrace();
           return null;
       }
    }
    public static long getTimeInMili(String timeString){
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = dateFormat.parse(timeString);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
