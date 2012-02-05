package fr.vojtisek.adm;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import fr.vojtisek.adm.data.DiveEntry;
import fr.vojtisek.adm.data.ORMLiteDBHelper;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class EditableDiveDetailViewActivity extends OrmLiteBaseActivity<ORMLiteDBHelper> {

    private Button mPickDate;
    private Button mPickTime;
    private ImageButton mLocation;
    private ImageButton mDepth;
    private ImageButton mDuration;
    //private int mYear;
    //private int mMonth;
    //private int mDay;
	
	protected RuntimeExceptionDao<DiveEntry, Integer> diveEntriesDao;
	protected int currentDiveId;
	
	protected DiveEntry currentDiveEntry;
	
	static final int DATE_DIALOG_ID = 0;
	static final int TIME_DIALOG_ID = 1;
	static final int NOTIMPLEMENTED_DIALOG_ID = 999;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editabledivedetail);
        currentDiveId = getIntent().getExtras().getInt("diveId");
        diveEntriesDao = getHelper().getDiveEntriesDao();
        currentDiveEntry = diveEntriesDao.queryForId(currentDiveId);
        //findViewById(R.id.divedate);
        
        
        // capture our View elements
        mPickDate = (Button) findViewById(R.id.editableDetail_pickDate);
        mPickTime = (Button) findViewById(R.id.editableDetail_pickTime);
        mLocation = (ImageButton) findViewById(R.id.editableDetail_pickLocation);
        mDepth    = (ImageButton) findViewById(R.id.editableDetail_pickDepth);
        mDuration = (ImageButton) findViewById(R.id.editableDetail_pickDuration);
        
        
        // add a click listener to the buttons
        mPickDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });
        mPickTime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(TIME_DIALOG_ID);
            }
        });

        mLocation.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(NOTIMPLEMENTED_DIALOG_ID);
            }
        });

        mDepth.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(NOTIMPLEMENTED_DIALOG_ID);
            }
        });

        mDuration.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(NOTIMPLEMENTED_DIALOG_ID);
            }
        });

        
        
        // capture edition  event in the detail view
        final EditText edittext = (EditText) findViewById(R.id.editabledetail_divelocation);
        edittext.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                  // Perform action on key press
                	currentDiveEntry.setLocation(edittext.getText().toString());
                	diveEntriesDao.update(currentDiveEntry);
                    //Toast.makeText(EditableDiveDetailViewActivity.this, "Saved with value "+edittext.getText().toString(), Toast.LENGTH_SHORT).show();
                  return true;
                }
                return false;
            }
        });
        
        //refreshScreenData();
    }
    
    @Override
	protected void onResume() {
		super.onResume();
		
		refreshScreenData();
	}
    
    
    private void refreshScreenData() {
    	// get our dao
    	RuntimeExceptionDao<DiveEntry, Integer> diveEntriesDao = getHelper().getDiveEntriesDao();
    	DiveEntry entry = diveEntriesDao.queryForId(currentDiveId);
    	
    	
    	SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy HH:mm");
    	((TextView) findViewById(R.id.editabledetail_divedate)).setText(dateFormatter.format(entry.getDate()));
		
    	((TextView) findViewById(R.id.editabledetail_divelocation)).setText(entry.getLocation());
    	
    	((TextView) findViewById(R.id.editabledetail_divedepth)).setText(entry.getMaxdepth().toString());
    	
    	((TextView) findViewById(R.id.editabledetail_diveduration)).setText(entry.getDuration().toString());
    	
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		// add options in the menu
        menu.add(Menu.NONE, 0, 0, R.string.preferences_option).setIcon(android.R.drawable.ic_menu_preferences);
        return super.onCreateOptionsMenu(menu);
    }
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	// behavior of option menu
        switch (item.getItemId()) {
            case 0:
                startActivity(new Intent(this, PreferencesActivity.class));
                return true;
        }
        return false;
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
    	Calendar c = Calendar.getInstance();
    	c.setTime(currentDiveEntry.getDate());
        switch (id) {
        case DATE_DIALOG_ID:
        	return new DatePickerDialog(this,
                        mDateSetListener,
                        c.get(Calendar.YEAR), 
                        c.get(Calendar.MONTH), 
                        c.get(Calendar.DAY_OF_MONTH));
        case TIME_DIALOG_ID:
        	
            return new TimePickerDialog(this,
                    mTimeSetListener, 
                    c.get(Calendar.HOUR_OF_DAY), 
                    c.get(Calendar.MINUTE), false);
        default :
        	Toast.makeText(EditableDiveDetailViewActivity.this, 
        			"TODO. Not implemented yet", Toast.LENGTH_LONG).show();

        }
        return null;
    }
    // the callback received when the user "sets" the date in the dialog
    private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year, 
                                      int monthOfYear, int dayOfMonth) {
                	Calendar c = Calendar.getInstance();
                	c.setTime(currentDiveEntry.getDate());
                	c.set(Calendar.YEAR, year);
                	c.set(Calendar.MONTH, monthOfYear);
                	c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                	currentDiveEntry.setDate(c.getTime());
                	diveEntriesDao.update(currentDiveEntry);
                	//Toast.makeText(EditableDiveDetailViewActivity.this, "Saved with value "+c.toString(), Toast.LENGTH_SHORT).show();
                    refreshScreenData();
                }
            };
    private TimePickerDialog.OnTimeSetListener mTimeSetListener =
    	    new TimePickerDialog.OnTimeSetListener() {
    	        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
    	        	Calendar c = Calendar.getInstance();
                	c.setTime(currentDiveEntry.getDate());
                	c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                	c.set(Calendar.MINUTE, minute);
                	currentDiveEntry.setDate(c.getTime());
                	diveEntriesDao.update(currentDiveEntry);
                	refreshScreenData();
    	        }
    	    };
}
