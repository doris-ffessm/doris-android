package fr.ffessm.doris.android;

import org.acra.*;
import org.acra.annotation.*;

import android.app.Application;

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
    }
}
