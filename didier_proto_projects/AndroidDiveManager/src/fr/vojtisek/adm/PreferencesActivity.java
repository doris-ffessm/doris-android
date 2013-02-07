package fr.vojtisek.adm;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.view.MenuItem;

public class PreferencesActivity extends PreferenceActivity {
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences); 
       
        /*setContentView(R.layout.preferences);
        
        
        Spinner spinner_length = (Spinner) findViewById(R.id.spinner_length);
        ArrayAdapter<CharSequence> adapter_length = ArrayAdapter.createFromResource(
                this, R.array.length_units_array, android.R.layout.simple_spinner_item);
        adapter_length.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_length.setAdapter(adapter_length);
        spinner_length.setOnItemSelectedListener(new MyOnLengthItemSelectedListener());
        
        
        Spinner spinner_temperature = (Spinner) findViewById(R.id.spinner_temperature);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.temperature_units_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_temperature.setAdapter(adapter);
        spinner_length.setOnItemSelectedListener(new MyOnTemperatureItemSelectedListener());
        */
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, 0, 0, "Back to main menu");
        return super.onCreateOptionsMenu(menu);
    }
 
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                startActivity(new Intent(this, AndroidDiveManagerMainActivity.class));
                return true;
        }
        return false;
    }
        
    
    
   /* 
    public class MyOnLengthItemSelectedListener implements OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent,
            View view, int pos, long id) {
          Toast.makeText(parent.getContext(), "The length unit is " +
              parent.getItemAtPosition(pos).toString(), Toast.LENGTH_LONG).show();
        }

        public void onNothingSelected(AdapterView parent) {
          // Do nothing.
        }
    }
    
    public class MyOnTemperatureItemSelectedListener implements OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent,
            View view, int pos, long id) {
          Toast.makeText(parent.getContext(), "The temperature unit is " +
              parent.getItemAtPosition(pos).toString(), Toast.LENGTH_LONG).show();
        }

        public void onNothingSelected(AdapterView parent) {
          // Do nothing.
        }
    }
    */
    
}
