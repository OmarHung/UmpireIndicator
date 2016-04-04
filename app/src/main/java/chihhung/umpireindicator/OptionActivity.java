package chihhung.umpireindicator;

import android.content.Context;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceChangeListener;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class OptionActivity extends PreferenceActivity implements OnPreferenceChangeListener {
    CheckBoxPreference vibrate, doublecheck;
    Context context = this;
    DataStore db = new DataStore(this);
    String dataVib,dataDou;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.optionpreference);
        db.open();
        db.addData();
        setPrefer();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.actionbar_layout_options, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.done:
                finish();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        // TODO Auto-generated method stub
        if (preference.getKey().equals("vibrate")) {
            dataVib = newValue.toString();
        }
        if (preference.getKey().equals("doublecheck")) {
            dataDou = newValue.toString();
        }
        db.updateOption(1, dataVib, dataDou);
        return true;
    }
    public void setPrefer() {
        vibrate = (CheckBoxPreference) findPreference("vibrate");
        vibrate.setOnPreferenceChangeListener(this);
        if(db.getData("vibrate").equals("true")) {
            vibrate.setChecked(true);
            dataVib="true";
        }else {
            vibrate.setChecked(false);
            dataVib="false";
        }

        doublecheck = (CheckBoxPreference) findPreference("doublecheck");
        doublecheck.setOnPreferenceChangeListener(this);
        if(db.getData("doublecheck").equals("true")) {
            doublecheck.setChecked(true);
            dataDou="true";
        }else {
            doublecheck.setChecked(false);
            dataDou="false";
        }
    }

}