package fr.ffessm.doris.android;

import java.io.IOException;

import org.acra.*;
import org.acra.annotation.*;

import fr.ffessm.doris.android.datamodel.SQLiteDataBaseHelper;

import android.app.Application;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

@ReportsCrashes(
	    formKey = "", // This is required for backward compatibility but not used
	    mailTo = "dvojtise@gmail.com",
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.crash_toast_text
	)
public class DorisApplication extends Application {

	@Override
    public void onCreate() {
        super.onCreate();

        // The following line triggers the initialization of ACRA
        ACRA.init(this);
        
        
      /*  
        // The following initialize the DB from file
        SQLiteDataBaseHelper myDbHelper = new SQLiteDataBaseHelper(this);
        //myDbHelper = new DataBaseHelper(this);
         
        try {
         
        	myDbHelper.createDataBase();
         
        } catch (IOException ioe) {
         
        	throw new Error("Unable to create database");
         
        }
*/
    }
	
	
}
