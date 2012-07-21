package fr.ffessm.doris.android;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import fr.ffessm.doris.android.activities.ParticipantListViewActivity;
import fr.ffessm.doris.android.datamodel.OrmLiteDBHelper;
import fr.ffessm.doris.android.datamodel.Participant;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class DorisAndroidMainActivity extends OrmLiteBaseActivity<OrmLiteDBHelper> {
	
	protected RuntimeExceptionDao<Participant, Integer> participantDao;
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        getHelper().getParticipantDao();
    }
    
    public void onClickBtnParticipantList(View view){
    	// open the view activity
		startActivity(new Intent(this, ParticipantListViewActivity.class));
    }
}