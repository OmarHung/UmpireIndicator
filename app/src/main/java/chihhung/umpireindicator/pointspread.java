package chihhung.umpireindicator;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

public class pointspread extends Activity {
    private AdView adView;
    private Button Enter, Cancel;
    private EditText point;
    private RadioGroup whichteam;
    private int team=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pointspread);
        // 建立 adView。
        adView = new AdView(this);
        adView.setAdUnitId("ca-app-pub-3246097450290535/9731919808");
        adView.setAdSize(AdSize.SMART_BANNER);
        LinearLayout layout = (LinearLayout)findViewById(R.id.adspoint);
        layout.addView(adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        Enter = (Button) findViewById(R.id.enter);
        Cancel = (Button) findViewById(R.id.cancel);
        point = (EditText) findViewById(R.id.editText1);
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(2);
        point.setFilters(FilterArray);
        whichteam  = (RadioGroup) findViewById(R.id.radioGroup1);

        Enter.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub 
                Intent intent = new Intent();
                if(point.getText().toString().equals(""))
                    intent.putExtra("point", "0");
                else
                    intent.putExtra("point", point.getText().toString());
                intent.putExtra("team", team);
                pointspread.this.setResult(RESULT_OK, intent);
                pointspread.this.finish();
            }
        });

        Cancel.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub 
                Intent intent = new Intent();
                intent.putExtra("point", "0");
                intent.putExtra("team", 0);
                pointspread.this.setResult(RESULT_OK, intent);
                pointspread.this.finish();
            }
        });

        whichteam.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, final int checkedId) {
                switch (checkedId) {
                    case R.id.radio0:
                        team = 0;
                        break;
                    case R.id.radio1:
                        team = 1;
                        break;
                }
            }
        });
    }
}
