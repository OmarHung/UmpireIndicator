package chihhung.umpireindicator;
import com.google.android.gms.ads.*;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
//import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private AdView adView;
    private Button playball, pointspread;
    //private RadioGroup whichball;
    private RadioButton baseball, softball;
    private EditText visiting, home, time;
    private String point = "0";
    private int team = 0;
    Context context = this;
    DataStore db = new DataStore(this);
    String isRating;
    int useDays;
    String URL = "https://docs.google.com/forms/d/1LbbWNgj3PxIr3tj0r_qpv33t9tIZdYLrNyxsbYUiPYE/viewform";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 建立 adView。
        adView = new AdView(this);
        adView.setAdUnitId("ca-app-pub-3246097450290535/9731919808");
        adView.setAdSize(AdSize.SMART_BANNER);
        LinearLayout layout = (LinearLayout)findViewById(R.id.adsmain);
        layout.addView(adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        getData();
        letsRating();
        setWidget();

        playball.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (!baseball.isChecked() && !softball.isChecked())
                    Toast.makeText(MainActivity.this, getString(R.string.pleasechoicesport), Toast.LENGTH_LONG).show();
                else {
                    if ("".equals(time.getText().toString().trim())) {
                        if(baseball.isChecked()) {
                            Intent intent = new Intent(MainActivity.this, indicatorForBaseball.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("visiting", visiting.getText().toString());
                            bundle.putString("home", home.getText().toString());
                            bundle.putString("point", point);
                            bundle.putInt("team", team);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }else {
                            Intent intent = new Intent(MainActivity.this, indicatorForSoftball.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("visiting", visiting.getText().toString());
                            bundle.putString("home", home.getText().toString());
                            bundle.putString("point", point);
                            bundle.putInt("team", team);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }

                    } else {
                        if(baseball.isChecked()) {
                            Intent intent = new Intent(MainActivity.this, indicatorForBaseballTime.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("visiting", visiting.getText().toString());
                            bundle.putString("home", home.getText().toString());
                            bundle.putString("point", point);
                            bundle.putInt("team", team);
                            bundle.putInt("time", Integer.parseInt(time.getText().toString()));
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }else {
                            Intent intent = new Intent(MainActivity.this, indicatorForSoftballTime.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("visiting", visiting.getText().toString());
                            bundle.putString("home", home.getText().toString());
                            bundle.putString("point", point);
                            bundle.putInt("team", team);
                            bundle.putInt("time", Integer.parseInt(time.getText().toString()));
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    }
                }
            }
        });

        pointspread.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                startActivityForResult(new Intent(MainActivity.this, pointspread.class), 1);
            }
        });
    }
    @Override
    public void onPause() {
        adView.pause();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        adView.resume();
    }

    @Override
    protected void onDestroy() {
        adView.destroy();
        db.open();
        db.updateUseDays(1, String.valueOf(useDays));
        db.close();
        super.onDestroy();
    }
    public static String getVersion(Context context)
    {
        try {
            PackageInfo pi=context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pi.versionName;
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block  
            e.printStackTrace();
            return "不明版本號";
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionbar_aboutme:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setIcon(android.R.drawable.ic_dialog_info)
                        .setTitle(R.string.actionbar_about)
                        .setMessage(getString(R.string.editor)+"： Chang-Chih Hung" + "\n" + "\n" + "chihhungdevelop@gmail.com"+ "\n" + "\n" +getString(R.string.vername)+"："+getVersion(this))
                        .setPositiveButton(getString(R.string.close), null);
                AlertDialog dialog = builder.show();
                TextView messageText = (TextView) dialog.findViewById(android.R.id.message);
                messageText.setGravity(Gravity.CENTER);
                dialog.show();
                break;
            case R.id.actionbar_bugreport:
                Intent gotoweb = new Intent(Intent.ACTION_VIEW,Uri.parse(URL));
                startActivity(gotoweb);
                break;
            case R.id.actionbar_options:
                Intent optionTntent = new Intent(context, OptionActivity.class);
                startActivity(optionTntent);
                break;
            case R.id.actionbar_rating:
                AlertDialog.Builder dialograting = new AlertDialog.Builder(this);
                dialograting
                        .setTitle(getString(R.string.actionbar_rating))
                        .setIcon(android.R.drawable.btn_star_big_on)
                        .setMessage(getString(R.string.gotoplayrating))
                        .setPositiveButton(getString(R.string.noneforever),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        db.open();
                                        db.update(1, "never");
                                        db.close();
                                    }
                                })
                        .setNegativeButton(getString(R.string.nonenow),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        db.open();
                                        db.update(1, "no");
                                        db.close();
                                    }
                                })
                        .setNeutralButton(getString(R.string.gotorating),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        Uri uri = Uri.parse("market://details?id="
                                                + context.getPackageName());
                                        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                                        try {
                                            db.open();
                                            db.update(1, "ok");
                                            db.close();
                                            startActivity(goToMarket);
                                        } catch (ActivityNotFoundException e) {
                                            Toast.makeText(context,
                                                    "無法啟動Google Play !",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }).show();
                break;

            default:
                break;
        }
        return true;
    }
    public void getData() {
        db.open();
        db.addData();
        isRating = db.getData("rate");
        useDays = Integer.parseInt(db.getData("usedays"))+1;
        db.close();
    }
    public void letsRating() {
        if(useDays==5 && isRating.equals("no")) {
            AlertDialog.Builder dialograting = new AlertDialog.Builder(this);
            dialograting
                    .setTitle("評分")
                    .setIcon(android.R.drawable.btn_star_big_on)
                    .setMessage("到Google Play為本程式評分吧！")
                    .setPositiveButton(getString(R.string.noneforever),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    db.open();
                                    db.update(1, "never");
                                    db.close();
                                }
                            })
                    .setNegativeButton(getString(R.string.nonenow),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    db.open();
                                    db.update(1, "no");
                                    db.close();
                                }
                            })
                    .setNeutralButton(getString(R.string.gotorating),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
                                    Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                                    try {
                                        db.open();
                                        db.update(1, "ok");
                                        db.close();
                                        startActivity(goToMarket);
                                    } catch (ActivityNotFoundException e) {
                                        Toast.makeText(context, "無法啟動Google Play !", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }).show();
        }else if(useDays>=10 && useDays%5==0 && isRating.equals("no")) {
            AlertDialog.Builder dialograting = new AlertDialog.Builder(this);
            dialograting
                    .setTitle(getString(R.string.noneforever))
                    .setIcon(android.R.drawable.btn_star_big_on)
                    .setMessage("到Google Play為本程式評分吧！")
                    .setPositiveButton("永遠不需要",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    db.open();
                                    db.update(1, "never");
                                    db.close();
                                }
                            })
                    .setNegativeButton(getString(R.string.nonenow),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    db.open();
                                    db.update(1, "no");
                                    db.close();
                                }
                            })
                    .setNeutralButton(getString(R.string.gotorating),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    Uri uri = Uri.parse("market://details?id="
                                            + context.getPackageName());
                                    Intent goToMarket = new Intent(
                                            Intent.ACTION_VIEW, uri);
                                    try {
                                        db.open();
                                        db.update(1, "ok");
                                        db.close();
                                        startActivity(goToMarket);
                                    } catch (ActivityNotFoundException e) {
                                        Toast.makeText(context,
                                                "無法啟動Google Play !",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }).show();
        }
    }
    public void setWidget() {
        //whichball = (RadioGroup) findViewById(R.id.radioGroup1);
        baseball = (RadioButton) findViewById(R.id.baseballbtn);
        softball = (RadioButton) findViewById(R.id.softballbtn);
        playball = (Button) findViewById(R.id.playball);
        pointspread = (Button) findViewById(R.id.pointspread);
        visiting = (EditText) findViewById(R.id.visiting);
        InputFilter[] visitingFilterArray = new InputFilter[1];
        visitingFilterArray[0] = new InputFilter.LengthFilter(4);
        visiting.setFilters(visitingFilterArray);
        home = (EditText) findViewById(R.id.home);
        InputFilter[] homeFilterArray = new InputFilter[1];
        homeFilterArray[0] = new InputFilter.LengthFilter(4);
        home.setFilters(homeFilterArray);
        time = (EditText) findViewById(R.id.time);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            point = data.getExtras().getString("point");// 得到新Activity 關閉後回傳的數據
            team = data.getExtras().getInt("team");
        } catch (java.lang.RuntimeException r) {

        }
    }
}
