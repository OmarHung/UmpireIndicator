package chihhung.umpireindicator;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class indicatorForBaseballTime extends Activity{
    Context context=this;
    DataStore db = new DataStore(this);
    private Button SA,SD,BA,BD,OA,OD,GA,GD,Pause,Hit;
    private ImageView SLight1, SLight2, BLight1, BLight2, BLight3, OLight1,
            OLight2;
    private TextView visiting,home,textView1, timeView,visitingcount,homecount,
            visitingget1,homeget1,
            visitingget2,homeget2,
            visitingget3,homeget3,
            visitingget4,homeget4,
            visitingget5,homeget5,
            visitingget6,homeget6,
            visitingget7,homeget7,
            visitingget8,homeget8,
            visitingget9,homeget9,
            visitingget10,homeget10;
    private int Number_Count=1, Team, whichTeam=0, Time, pause=0, stop=0, Scount = 0, Bcount = 0,
            Ocount = 0;; //whichTeam 0=上半局  1=下半局
    private String Number = "1", gameTeam = " ▲",Visiting,Home,Point=null;
    private MyCount mc;
    private Vibrator vibrate;
    private long sTime;
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.indicator, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionbar_options:
                Intent optionTntent = new Intent(context, OptionActivity.class);
                startActivity(optionTntent);
                break;
            default:
                break;
        }
        return true;
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_indicatorfortime);
        db.open();
        setWidget();
        Bundle bundle = this.getIntent().getExtras();
        Visiting = bundle.getString("visiting"); //先攻隊名
        Home = bundle.getString("home"); //後攻隊名
        if(!Visiting.equals(""))
            visiting.setText(Visiting);
        if(!Home.equals(""))
            home.setText(Home);
        Point = bundle.getString("point"); //讓分數
        Team = bundle.getInt("team"); //讓分隊伍 0=先攻隊  1=後攻隊
        Time = bundle.getInt("time");
        mc = new MyCount(Time*1000*60, 1000);
        mc.start();
        pointspread(Point, Team);

        Pause.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v) {
                if(stop==1)
                    Toast.makeText(context, getString(R.string.timeisover), Toast.LENGTH_SHORT).show();
                else {
                    pause=1;
                    if(isVib())
                        vibrate.vibrate(300);
                    mc.cancel();
                    Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(getString(R.string.layout_pause));
                    builder.setMessage(getString(R.string.gameispausing));
                    builder.setCancelable(false);
                    builder.setPositiveButton(getString(R.string.gamecontinue), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            pause=0;
                            if(isVib())
                                vibrate.vibrate(300);
                            mc = new MyCount(sTime, 1000);
                            mc.start();
                        }
                    });
                    builder.create().show();
                }
            }
        });
        SA.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isVib())
                    vibrate.vibrate(300);
                if(isDou()) {
                    Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(getString(R.string.messege));
                    builder.setMessage(getString(R.string.addstrike));
                    builder.setPositiveButton(getString(R.string.yes),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,	int which) {
                                    Scount = Scount + 1;
                                    setLight("SLight",Scount);
                                }
                            });
                    builder.setNegativeButton(getString(R.string.layout_cancel),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    builder.create().show();
                }else {
                    Scount = Scount + 1;
                    setLight("SLight",Scount);
                }
            }
        });
        SD.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isVib())
                    vibrate.vibrate(300);
                if(isDou()) {
                    Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(getString(R.string.messege));
                    builder.setMessage(getString(R.string.reducestrike));
                    builder.setPositiveButton(getString(R.string.yes),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(Scount==0)
                                        Toast.makeText(context, getString(R.string.strikelow0), Toast.LENGTH_SHORT).show();
                                    else {
                                        Scount = Scount-1;
                                        setLight("SLight",Scount);
                                    }
                                }
                            });
                    builder.setNegativeButton(getString(R.string.layout_cancel),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    builder.create().show();
                }else {
                    if(Scount==0)
                        Toast.makeText(context, getString(R.string.strikelow0), Toast.LENGTH_SHORT).show();
                    else {
                        Scount = Scount-1;
                        setLight("SLight",Scount);
                    }
                }
            }
        });
        BA.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isVib())
                    vibrate.vibrate(300);
                if(isDou()) {
                    Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(getString(R.string.messege));
                    builder.setMessage(getString(R.string.addball));
                    builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Bcount = Bcount+1;
                            setLight("BLight",Bcount);
                        }
                    });
                    builder.setNegativeButton(getString(R.string.layout_cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    builder.create().show();
                }else {
                    Bcount = Bcount+1;
                    setLight("BLight",Bcount);
                }
            }
        });
        BD.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isVib())
                    vibrate.vibrate(300);
                if(isDou()) {
                    Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(getString(R.string.messege));
                    builder.setMessage(getString(R.string.reduceball));
                    builder.setPositiveButton(getString(R.string.yes),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(Bcount==0)
                                        Toast.makeText(context, getString(R.string.balllow0), Toast.LENGTH_SHORT).show();
                                    else {
                                        Bcount = Bcount-1;
                                        setLight("BLight",Bcount);
                                    }
                                }
                            });
                    builder.setNegativeButton(getString(R.string.layout_cancel),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    builder.create().show();
                }else {
                    if(Bcount==0)
                        Toast.makeText(context, getString(R.string.balllow0), Toast.LENGTH_SHORT).show();
                    else {
                        Bcount = Bcount-1;
                        setLight("BLight",Bcount);
                    }
                }
            }
        });
        OA.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isVib())
                    vibrate.vibrate(300);
                Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(getString(R.string.messege));
                builder.setMessage(getString(R.string.addout));
                builder.setNeutralButton(getString(R.string.runnerout), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Ocount = Ocount+1;
                        setLight("OLight",Ocount);
                    }
                });
                builder.setPositiveButton(getString(R.string.hitterout), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Ocount = Ocount+1;
                        Scount=0;
                        Bcount=0;
                        setLight("OLight",Ocount);
                        setLight("SLight",Scount);
                        setLight("BLight",Bcount);
                    }
                });
                builder.setNegativeButton(getString(R.string.layout_cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                builder.create().show();
            }
        });
        OD.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isVib())
                    vibrate.vibrate(300);
                if(isDou()) {
                    Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(getString(R.string.messege));
                    builder.setMessage(getString(R.string.reduceout));
                    builder.setPositiveButton(getString(R.string.yes),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(Ocount==0)
                                        Toast.makeText(context, getString(R.string.outlow0), Toast.LENGTH_SHORT).show();
                                    else {
                                        Ocount = Ocount-1;
                                        setLight("OLight",Ocount);
                                    }
                                }
                            });
                    builder.setNegativeButton(getString(R.string.layout_cancel),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    builder.create().show();
                }else {
                    if(Ocount==0)
                        Toast.makeText(context, getString(R.string.outlow0), Toast.LENGTH_SHORT).show();
                    else {
                        Ocount = Ocount-1;
                        setLight("OLight",Ocount);
                    }
                }
            }
        });
        GA.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isVib())
                    vibrate.vibrate(300);
                if(isDou()) {
                    Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(getString(R.string.messege));
                    builder.setMessage(getString(R.string.addscore));
                    builder.setPositiveButton(getString(R.string.yes),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch(Number_Count) {
                                        case 1:
                                            if (whichTeam == 0) {
                                                String visitinggetText = visitingget1.getText().toString();
                                                int Int_visitinggetLast = Integer.valueOf(visitinggetText);
                                                Int_visitinggetLast = Int_visitinggetLast + 1;
                                                visitingget1.setText(String.valueOf(Int_visitinggetLast));
                                                int Vcount = Integer.valueOf(visitingcount.getText().toString());
                                                Vcount = Vcount + 1;
                                                visitingcount.setText(String.valueOf(Vcount));
                                            } else {
                                                String homegetText = homeget1.getText().toString();
                                                int Int_homegetLast = Integer.valueOf(homegetText);
                                                Int_homegetLast = Int_homegetLast + 1;
                                                homeget1.setText(String.valueOf(Int_homegetLast));
                                                int Hcount = Integer.valueOf(homecount.getText().toString());
                                                Hcount = Hcount + 1;
                                                homecount.setText(String.valueOf(Hcount));
                                            }
                                            break;
                                        case 2:
                                            if (whichTeam == 0) {
                                                String visitinggetText = visitingget2.getText().toString();
                                                int Int_visitinggetLast = Integer.valueOf(visitinggetText);
                                                Int_visitinggetLast = Int_visitinggetLast + 1;
                                                visitingget2.setText(String.valueOf(Int_visitinggetLast));
                                                int Vcount = Integer.valueOf(visitingcount.getText().toString());
                                                Vcount = Vcount + 1;
                                                visitingcount.setText(String.valueOf(Vcount));
                                            } else {
                                                String homegetText = homeget2.getText().toString();
                                                int Int_homegetLast = Integer.valueOf(homegetText);
                                                Int_homegetLast = Int_homegetLast + 1;
                                                homeget2.setText(String.valueOf(Int_homegetLast));
                                                int Hcount = Integer.valueOf(homecount.getText().toString());
                                                Hcount = Hcount + 1;
                                                homecount.setText(String.valueOf(Hcount));
                                            }
                                            break;
                                        case 3:
                                            if (whichTeam == 0) {
                                                String visitinggetText = visitingget3.getText().toString();
                                                int Int_visitinggetLast = Integer.valueOf(visitinggetText);
                                                Int_visitinggetLast = Int_visitinggetLast + 1;
                                                visitingget3.setText(String.valueOf(Int_visitinggetLast));
                                                int Vcount = Integer.valueOf(visitingcount.getText().toString());
                                                Vcount = Vcount + 1;
                                                visitingcount.setText(String.valueOf(Vcount));
                                            } else {
                                                String homegetText = homeget3.getText().toString();
                                                int Int_homegetLast = Integer.valueOf(homegetText);
                                                Int_homegetLast = Int_homegetLast + 1;
                                                homeget3.setText(String.valueOf(Int_homegetLast));
                                                int Hcount = Integer.valueOf(homecount.getText().toString());
                                                Hcount = Hcount + 1;
                                                homecount.setText(String.valueOf(Hcount));
                                            }
                                            break;
                                        case 4:
                                            if (whichTeam == 0) {
                                                String visitinggetText = visitingget4.getText().toString();
                                                int Int_visitinggetLast = Integer.valueOf(visitinggetText);
                                                Int_visitinggetLast = Int_visitinggetLast + 1;
                                                visitingget4.setText(String.valueOf(Int_visitinggetLast));
                                                int Vcount = Integer.valueOf(visitingcount.getText().toString());
                                                Vcount = Vcount + 1;
                                                visitingcount.setText(String.valueOf(Vcount));
                                            } else {
                                                String homegetText = homeget4.getText().toString();
                                                int Int_homegetLast = Integer.valueOf(homegetText);
                                                Int_homegetLast = Int_homegetLast + 1;
                                                homeget4.setText(String.valueOf(Int_homegetLast));
                                                int Hcount = Integer.valueOf(homecount.getText().toString());
                                                Hcount = Hcount + 1;
                                                homecount.setText(String.valueOf(Hcount));
                                            }
                                            break;
                                        case 5:
                                            if (whichTeam == 0) {
                                                String visitinggetText = visitingget5.getText().toString();
                                                int Int_visitinggetLast = Integer.valueOf(visitinggetText);
                                                Int_visitinggetLast = Int_visitinggetLast + 1;
                                                visitingget5.setText(String.valueOf(Int_visitinggetLast));
                                                int Vcount = Integer.valueOf(visitingcount.getText().toString());
                                                Vcount = Vcount + 1;
                                                visitingcount.setText(String.valueOf(Vcount));
                                            } else {
                                                String homegetText = homeget5.getText().toString();
                                                int Int_homegetLast = Integer.valueOf(homegetText);
                                                Int_homegetLast = Int_homegetLast + 1;
                                                homeget5.setText(String.valueOf(Int_homegetLast));
                                                int Hcount = Integer.valueOf(homecount.getText().toString());
                                                Hcount = Hcount + 1;
                                                homecount.setText(String.valueOf(Hcount));
                                            }
                                            break;
                                        case 6:
                                            if (whichTeam == 0) {
                                                String visitinggetText = visitingget6.getText().toString();
                                                int Int_visitinggetLast = Integer.valueOf(visitinggetText);
                                                Int_visitinggetLast = Int_visitinggetLast + 1;
                                                visitingget6.setText(String.valueOf(Int_visitinggetLast));
                                                int Vcount = Integer.valueOf(visitingcount.getText().toString());
                                                Vcount = Vcount + 1;
                                                visitingcount.setText(String.valueOf(Vcount));
                                            } else {
                                                String homegetText = homeget6.getText().toString();
                                                int Int_homegetLast = Integer.valueOf(homegetText);
                                                Int_homegetLast = Int_homegetLast + 1;
                                                homeget6.setText(String.valueOf(Int_homegetLast));
                                                int Hcount = Integer.valueOf(homecount.getText().toString());
                                                Hcount = Hcount + 1;
                                                homecount.setText(String.valueOf(Hcount));
                                            }
                                            break;
                                        case 7:
                                            if (whichTeam == 0) {
                                                String visitinggetText = visitingget7.getText().toString();
                                                int Int_visitinggetLast = Integer.valueOf(visitinggetText);
                                                Int_visitinggetLast = Int_visitinggetLast + 1;
                                                visitingget7.setText(String.valueOf(Int_visitinggetLast));
                                                int Vcount = Integer.valueOf(visitingcount.getText().toString());
                                                Vcount = Vcount + 1;
                                                visitingcount.setText(String.valueOf(Vcount));
                                            } else {
                                                String homegetText = homeget7.getText().toString();
                                                int Int_homegetLast = Integer.valueOf(homegetText);
                                                Int_homegetLast = Int_homegetLast + 1;
                                                homeget7.setText(String.valueOf(Int_homegetLast));
                                                int Hcount = Integer.valueOf(homecount.getText().toString());
                                                Hcount = Hcount + 1;
                                                homecount.setText(String.valueOf(Hcount));
                                            }
                                            break;
                                        case 8:
                                            if (whichTeam == 0) {
                                                String visitinggetText = visitingget8.getText().toString();
                                                int Int_visitinggetLast = Integer.valueOf(visitinggetText);
                                                Int_visitinggetLast = Int_visitinggetLast + 1;
                                                visitingget8.setText(String.valueOf(Int_visitinggetLast));
                                                int Vcount = Integer.valueOf(visitingcount.getText().toString());
                                                Vcount = Vcount + 1;
                                                visitingcount.setText(String.valueOf(Vcount));
                                            } else {
                                                String homegetText = homeget8.getText().toString();
                                                int Int_homegetLast = Integer.valueOf(homegetText);
                                                Int_homegetLast = Int_homegetLast + 1;
                                                homeget8.setText(String.valueOf(Int_homegetLast));
                                                int Hcount = Integer.valueOf(homecount.getText().toString());
                                                Hcount = Hcount + 1;
                                                homecount.setText(String.valueOf(Hcount));
                                            }
                                            break;
                                        case 9:
                                            if (whichTeam == 0) {
                                                String visitinggetText = visitingget9.getText().toString();
                                                int Int_visitinggetLast = Integer.valueOf(visitinggetText);
                                                Int_visitinggetLast = Int_visitinggetLast + 1;
                                                visitingget9.setText(String.valueOf(Int_visitinggetLast));
                                                int Vcount = Integer.valueOf(visitingcount.getText().toString());
                                                Vcount = Vcount + 1;
                                                visitingcount.setText(String.valueOf(Vcount));
                                            } else {
                                                String homegetText = homeget9.getText().toString();
                                                int Int_homegetLast = Integer.valueOf(homegetText);
                                                Int_homegetLast = Int_homegetLast + 1;
                                                homeget9.setText(String.valueOf(Int_homegetLast));
                                                int Hcount = Integer.valueOf(homecount.getText().toString());
                                                Hcount = Hcount + 1;
                                                homecount.setText(String.valueOf(Hcount));
                                            }
                                            break;
                                        default:
                                            if (whichTeam == 0) {
                                                String visitinggetText = visitingget10.getText().toString();
                                                int Int_visitinggetLast = Integer.valueOf(visitinggetText);
                                                Int_visitinggetLast = Int_visitinggetLast + 1;
                                                visitingget10.setText(String.valueOf(Int_visitinggetLast));
                                                int Vcount = Integer.valueOf(visitingcount.getText().toString());
                                                Vcount = Vcount + 1;
                                                visitingcount.setText(String.valueOf(Vcount));
                                            } else {
                                                String homegetText = homeget10.getText().toString();
                                                int Int_homegetLast = Integer.valueOf(homegetText);
                                                Int_homegetLast = Int_homegetLast + 1;
                                                homeget10.setText(String.valueOf(Int_homegetLast));
                                                int Hcount = Integer.valueOf(homecount.getText().toString());
                                                Hcount = Hcount + 1;
                                                homecount.setText(String.valueOf(Hcount));
                                            }
                                    }
                                }
                            });
                    builder.setNegativeButton(getString(R.string.layout_cancel),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    builder.create().show();
                }else {
                    switch(Number_Count) {
                        case 1:
                            if (whichTeam == 0) {
                                String visitinggetText = visitingget1.getText().toString();
                                int Int_visitinggetLast = Integer.valueOf(visitinggetText);
                                Int_visitinggetLast = Int_visitinggetLast + 1;
                                visitingget1.setText(String.valueOf(Int_visitinggetLast));
                                int Vcount = Integer.valueOf(visitingcount.getText().toString());
                                Vcount = Vcount + 1;
                                visitingcount.setText(String.valueOf(Vcount));
                            } else {
                                String homegetText = homeget1.getText().toString();
                                int Int_homegetLast = Integer.valueOf(homegetText);
                                Int_homegetLast = Int_homegetLast + 1;
                                homeget1.setText(String.valueOf(Int_homegetLast));
                                int Hcount = Integer.valueOf(homecount.getText().toString());
                                Hcount = Hcount + 1;
                                homecount.setText(String.valueOf(Hcount));
                            }
                            break;
                        case 2:
                            if (whichTeam == 0) {
                                String visitinggetText = visitingget2.getText().toString();
                                int Int_visitinggetLast = Integer.valueOf(visitinggetText);
                                Int_visitinggetLast = Int_visitinggetLast + 1;
                                visitingget2.setText(String.valueOf(Int_visitinggetLast));
                                int Vcount = Integer.valueOf(visitingcount.getText().toString());
                                Vcount = Vcount + 1;
                                visitingcount.setText(String.valueOf(Vcount));
                            } else {
                                String homegetText = homeget2.getText().toString();
                                int Int_homegetLast = Integer.valueOf(homegetText);
                                Int_homegetLast = Int_homegetLast + 1;
                                homeget2.setText(String.valueOf(Int_homegetLast));
                                int Hcount = Integer.valueOf(homecount.getText().toString());
                                Hcount = Hcount + 1;
                                homecount.setText(String.valueOf(Hcount));
                            }
                            break;
                        case 3:
                            if (whichTeam == 0) {
                                String visitinggetText = visitingget3.getText().toString();
                                int Int_visitinggetLast = Integer.valueOf(visitinggetText);
                                Int_visitinggetLast = Int_visitinggetLast + 1;
                                visitingget3.setText(String.valueOf(Int_visitinggetLast));
                                int Vcount = Integer.valueOf(visitingcount.getText().toString());
                                Vcount = Vcount + 1;
                                visitingcount.setText(String.valueOf(Vcount));
                            } else {
                                String homegetText = homeget3.getText().toString();
                                int Int_homegetLast = Integer.valueOf(homegetText);
                                Int_homegetLast = Int_homegetLast + 1;
                                homeget3.setText(String.valueOf(Int_homegetLast));
                                int Hcount = Integer.valueOf(homecount.getText().toString());
                                Hcount = Hcount + 1;
                                homecount.setText(String.valueOf(Hcount));
                            }
                            break;
                        case 4:
                            if (whichTeam == 0) {
                                String visitinggetText = visitingget4.getText().toString();
                                int Int_visitinggetLast = Integer.valueOf(visitinggetText);
                                Int_visitinggetLast = Int_visitinggetLast + 1;
                                visitingget4.setText(String.valueOf(Int_visitinggetLast));
                                int Vcount = Integer.valueOf(visitingcount.getText().toString());
                                Vcount = Vcount + 1;
                                visitingcount.setText(String.valueOf(Vcount));
                            } else {
                                String homegetText = homeget4.getText().toString();
                                int Int_homegetLast = Integer.valueOf(homegetText);
                                Int_homegetLast = Int_homegetLast + 1;
                                homeget4.setText(String.valueOf(Int_homegetLast));
                                int Hcount = Integer.valueOf(homecount.getText().toString());
                                Hcount = Hcount + 1;
                                homecount.setText(String.valueOf(Hcount));
                            }
                            break;
                        case 5:
                            if (whichTeam == 0) {
                                String visitinggetText = visitingget5.getText().toString();
                                int Int_visitinggetLast = Integer.valueOf(visitinggetText);
                                Int_visitinggetLast = Int_visitinggetLast + 1;
                                visitingget5.setText(String.valueOf(Int_visitinggetLast));
                                int Vcount = Integer.valueOf(visitingcount.getText().toString());
                                Vcount = Vcount + 1;
                                visitingcount.setText(String.valueOf(Vcount));
                            } else {
                                String homegetText = homeget5.getText().toString();
                                int Int_homegetLast = Integer.valueOf(homegetText);
                                Int_homegetLast = Int_homegetLast + 1;
                                homeget5.setText(String.valueOf(Int_homegetLast));
                                int Hcount = Integer.valueOf(homecount.getText().toString());
                                Hcount = Hcount + 1;
                                homecount.setText(String.valueOf(Hcount));
                            }
                            break;
                        case 6:
                            if (whichTeam == 0) {
                                String visitinggetText = visitingget6.getText().toString();
                                int Int_visitinggetLast = Integer.valueOf(visitinggetText);
                                Int_visitinggetLast = Int_visitinggetLast + 1;
                                visitingget6.setText(String.valueOf(Int_visitinggetLast));
                                int Vcount = Integer.valueOf(visitingcount.getText().toString());
                                Vcount = Vcount + 1;
                                visitingcount.setText(String.valueOf(Vcount));
                            } else {
                                String homegetText = homeget6.getText().toString();
                                int Int_homegetLast = Integer.valueOf(homegetText);
                                Int_homegetLast = Int_homegetLast + 1;
                                homeget6.setText(String.valueOf(Int_homegetLast));
                                int Hcount = Integer.valueOf(homecount.getText().toString());
                                Hcount = Hcount + 1;
                                homecount.setText(String.valueOf(Hcount));
                            }
                            break;
                        case 7:
                            if (whichTeam == 0) {
                                String visitinggetText = visitingget7.getText().toString();
                                int Int_visitinggetLast = Integer.valueOf(visitinggetText);
                                Int_visitinggetLast = Int_visitinggetLast + 1;
                                visitingget7.setText(String.valueOf(Int_visitinggetLast));
                                int Vcount = Integer.valueOf(visitingcount.getText().toString());
                                Vcount = Vcount + 1;
                                visitingcount.setText(String.valueOf(Vcount));
                            } else {
                                String homegetText = homeget7.getText().toString();
                                int Int_homegetLast = Integer.valueOf(homegetText);
                                Int_homegetLast = Int_homegetLast + 1;
                                homeget7.setText(String.valueOf(Int_homegetLast));
                                int Hcount = Integer.valueOf(homecount.getText().toString());
                                Hcount = Hcount + 1;
                                homecount.setText(String.valueOf(Hcount));
                            }
                            break;
                        case 8:
                            if (whichTeam == 0) {
                                String visitinggetText = visitingget8.getText().toString();
                                int Int_visitinggetLast = Integer.valueOf(visitinggetText);
                                Int_visitinggetLast = Int_visitinggetLast + 1;
                                visitingget8.setText(String.valueOf(Int_visitinggetLast));
                                int Vcount = Integer.valueOf(visitingcount.getText().toString());
                                Vcount = Vcount + 1;
                                visitingcount.setText(String.valueOf(Vcount));
                            } else {
                                String homegetText = homeget8.getText().toString();
                                int Int_homegetLast = Integer.valueOf(homegetText);
                                Int_homegetLast = Int_homegetLast + 1;
                                homeget8.setText(String.valueOf(Int_homegetLast));
                                int Hcount = Integer.valueOf(homecount.getText().toString());
                                Hcount = Hcount + 1;
                                homecount.setText(String.valueOf(Hcount));
                            }
                            break;
                        case 9:
                            if (whichTeam == 0) {
                                String visitinggetText = visitingget9.getText().toString();
                                int Int_visitinggetLast = Integer.valueOf(visitinggetText);
                                Int_visitinggetLast = Int_visitinggetLast + 1;
                                visitingget9.setText(String.valueOf(Int_visitinggetLast));
                                int Vcount = Integer.valueOf(visitingcount.getText().toString());
                                Vcount = Vcount + 1;
                                visitingcount.setText(String.valueOf(Vcount));
                            } else {
                                String homegetText = homeget9.getText().toString();
                                int Int_homegetLast = Integer.valueOf(homegetText);
                                Int_homegetLast = Int_homegetLast + 1;
                                homeget9.setText(String.valueOf(Int_homegetLast));
                                int Hcount = Integer.valueOf(homecount.getText().toString());
                                Hcount = Hcount + 1;
                                homecount.setText(String.valueOf(Hcount));
                            }
                            break;
                        default:
                            if (whichTeam == 0) {
                                String visitinggetText = visitingget10.getText().toString();
                                int Int_visitinggetLast = Integer.valueOf(visitinggetText);
                                Int_visitinggetLast = Int_visitinggetLast + 1;
                                visitingget10.setText(String.valueOf(Int_visitinggetLast));
                                int Vcount = Integer.valueOf(visitingcount.getText().toString());
                                Vcount = Vcount + 1;
                                visitingcount.setText(String.valueOf(Vcount));
                            } else {
                                String homegetText = homeget10.getText().toString();
                                int Int_homegetLast = Integer.valueOf(homegetText);
                                Int_homegetLast = Int_homegetLast + 1;
                                homeget10.setText(String.valueOf(Int_homegetLast));
                                int Hcount = Integer.valueOf(homecount.getText().toString());
                                Hcount = Hcount + 1;
                                homecount.setText(String.valueOf(Hcount));
                            }
                    }
                }
            }
        });
        GD.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isVib())
                    vibrate.vibrate(300);
                if(isDou()) {
                    Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(getString(R.string.messege));
                    builder.setMessage(getString(R.string.reducescore));
                    builder.setPositiveButton(getString(R.string.yes),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch(Number_Count) {
                                        case 1:
                                            if (whichTeam == 0) {
                                                String visitinggetText = visitingget1.getText().toString();
                                                int Int_visitinggetLast = Integer.valueOf(visitinggetText);
                                                if(Int_visitinggetLast==0) {
                                                    Toast.makeText(context, getString(R.string.scorelow), Toast.LENGTH_SHORT).show();
                                                }else {
                                                    Int_visitinggetLast = Int_visitinggetLast - 1;
                                                    visitingget1.setText(String.valueOf(Int_visitinggetLast));
                                                    int Vcount = Integer.valueOf(visitingcount.getText().toString());
                                                    Vcount = Vcount - 1;
                                                    visitingcount.setText(String.valueOf(Vcount));
                                                }
                                            } else {
                                                String homegetText = homeget1.getText().toString();
                                                int Int_homegetLast = Integer.valueOf(homegetText);
                                                if(Int_homegetLast==0) {
                                                    Toast.makeText(context, getString(R.string.scorelow), Toast.LENGTH_SHORT).show();
                                                }else {
                                                    Int_homegetLast = Int_homegetLast - 1;
                                                    homeget1.setText(String.valueOf(Int_homegetLast));
                                                    int Hcount = Integer.valueOf(homecount.getText().toString());
                                                    Hcount = Hcount - 1;
                                                    homecount.setText(String.valueOf(Hcount));
                                                }
                                            }
                                            break;
                                        case 2:
                                            if (whichTeam == 0) {
                                                String visitinggetText = visitingget2.getText().toString();
                                                int Int_visitinggetLast = Integer.valueOf(visitinggetText);
                                                if(Int_visitinggetLast==0) {
                                                    Toast.makeText(context, getString(R.string.scorelow), Toast.LENGTH_SHORT).show();
                                                }else {
                                                    Int_visitinggetLast = Int_visitinggetLast - 1;
                                                    visitingget2.setText(String.valueOf(Int_visitinggetLast));
                                                    int Vcount = Integer.valueOf(visitingcount.getText().toString());
                                                    Vcount = Vcount - 1;
                                                    visitingcount.setText(String.valueOf(Vcount));
                                                }
                                            } else {
                                                String homegetText = homeget2.getText().toString();
                                                int Int_homegetLast = Integer.valueOf(homegetText);
                                                if(Int_homegetLast==0) {
                                                    Toast.makeText(context, getString(R.string.scorelow), Toast.LENGTH_SHORT).show();
                                                }else {
                                                    Int_homegetLast = Int_homegetLast - 1;
                                                    homeget2.setText(String.valueOf(Int_homegetLast));
                                                    int Hcount = Integer.valueOf(homecount.getText().toString());
                                                    Hcount = Hcount - 1;
                                                    homecount.setText(String.valueOf(Hcount));
                                                }
                                            }
                                            break;
                                        case 3:
                                            if (whichTeam == 0) {
                                                String visitinggetText = visitingget3.getText().toString();
                                                int Int_visitinggetLast = Integer.valueOf(visitinggetText);
                                                if(Int_visitinggetLast==0) {
                                                    Toast.makeText(context, getString(R.string.scorelow), Toast.LENGTH_SHORT).show();
                                                }else {
                                                    Int_visitinggetLast = Int_visitinggetLast - 1;
                                                    visitingget3.setText(String.valueOf(Int_visitinggetLast));
                                                    int Vcount = Integer.valueOf(visitingcount.getText().toString());
                                                    Vcount = Vcount - 1;
                                                    visitingcount.setText(String.valueOf(Vcount));
                                                }
                                            } else {
                                                String homegetText = homeget3.getText().toString();
                                                int Int_homegetLast = Integer.valueOf(homegetText);
                                                if(Int_homegetLast==0) {
                                                    Toast.makeText(context, getString(R.string.scorelow), Toast.LENGTH_SHORT).show();
                                                }else {
                                                    Int_homegetLast = Int_homegetLast - 1;
                                                    homeget3.setText(String.valueOf(Int_homegetLast));
                                                    int Hcount = Integer.valueOf(homecount.getText().toString());
                                                    Hcount = Hcount - 1;
                                                    homecount.setText(String.valueOf(Hcount));
                                                }
                                            }
                                            break;
                                        case 4:
                                            if (whichTeam == 0) {
                                                String visitinggetText = visitingget4.getText().toString();
                                                int Int_visitinggetLast = Integer.valueOf(visitinggetText);
                                                if(Int_visitinggetLast==0) {
                                                    Toast.makeText(context, getString(R.string.scorelow), Toast.LENGTH_SHORT).show();
                                                }else {
                                                    Int_visitinggetLast = Int_visitinggetLast - 1;
                                                    visitingget4.setText(String.valueOf(Int_visitinggetLast));
                                                    int Vcount = Integer.valueOf(visitingcount.getText().toString());
                                                    Vcount = Vcount - 1;
                                                    visitingcount.setText(String.valueOf(Vcount));
                                                }
                                            } else {
                                                String homegetText = homeget4.getText().toString();
                                                int Int_homegetLast = Integer.valueOf(homegetText);
                                                if(Int_homegetLast==0) {
                                                    Toast.makeText(context, getString(R.string.scorelow), Toast.LENGTH_SHORT).show();
                                                }else {
                                                    Int_homegetLast = Int_homegetLast - 1;
                                                    homeget4.setText(String.valueOf(Int_homegetLast));
                                                    int Hcount = Integer.valueOf(homecount.getText().toString());
                                                    Hcount = Hcount - 1;
                                                    homecount.setText(String.valueOf(Hcount));
                                                }
                                            }
                                            break;
                                        case 5:
                                            if (whichTeam == 0) {
                                                String visitinggetText = visitingget5.getText().toString();
                                                int Int_visitinggetLast = Integer.valueOf(visitinggetText);
                                                if(Int_visitinggetLast==0) {
                                                    Toast.makeText(context, getString(R.string.scorelow), Toast.LENGTH_SHORT).show();
                                                }else {
                                                    Int_visitinggetLast = Int_visitinggetLast - 1;
                                                    visitingget5.setText(String.valueOf(Int_visitinggetLast));
                                                    int Vcount = Integer.valueOf(visitingcount.getText().toString());
                                                    Vcount = Vcount - 1;
                                                    visitingcount.setText(String.valueOf(Vcount));
                                                }
                                            } else {
                                                String homegetText = homeget5.getText().toString();
                                                int Int_homegetLast = Integer.valueOf(homegetText);
                                                if(Int_homegetLast==0) {
                                                    Toast.makeText(context, getString(R.string.scorelow), Toast.LENGTH_SHORT).show();
                                                }else {
                                                    Int_homegetLast = Int_homegetLast - 1;
                                                    homeget5.setText(String.valueOf(Int_homegetLast));
                                                    int Hcount = Integer.valueOf(homecount.getText().toString());
                                                    Hcount = Hcount - 1;
                                                    homecount.setText(String.valueOf(Hcount));
                                                }
                                            }
                                            break;
                                        case 6:
                                            if (whichTeam == 0) {
                                                String visitinggetText = visitingget6.getText().toString();
                                                int Int_visitinggetLast = Integer.valueOf(visitinggetText);
                                                if(Int_visitinggetLast==0) {
                                                    Toast.makeText(context, getString(R.string.scorelow), Toast.LENGTH_SHORT).show();
                                                }else {
                                                    Int_visitinggetLast = Int_visitinggetLast - 1;
                                                    visitingget6.setText(String.valueOf(Int_visitinggetLast));
                                                    int Vcount = Integer.valueOf(visitingcount.getText().toString());
                                                    Vcount = Vcount - 1;
                                                    visitingcount.setText(String.valueOf(Vcount));
                                                }
                                            } else {
                                                String homegetText = homeget6.getText().toString();
                                                int Int_homegetLast = Integer.valueOf(homegetText);
                                                if(Int_homegetLast==0) {
                                                    Toast.makeText(context, getString(R.string.scorelow), Toast.LENGTH_SHORT).show();
                                                }else {
                                                    Int_homegetLast = Int_homegetLast - 1;
                                                    homeget6.setText(String.valueOf(Int_homegetLast));
                                                    int Hcount = Integer.valueOf(homecount.getText().toString());
                                                    Hcount = Hcount - 1;
                                                    homecount.setText(String.valueOf(Hcount));
                                                }
                                            }
                                            break;
                                        case 7:
                                            if (whichTeam == 0) {
                                                String visitinggetText = visitingget7.getText().toString();
                                                int Int_visitinggetLast = Integer.valueOf(visitinggetText);
                                                if(Int_visitinggetLast==0) {
                                                    Toast.makeText(context, getString(R.string.scorelow), Toast.LENGTH_SHORT).show();
                                                }else {
                                                    Int_visitinggetLast = Int_visitinggetLast - 1;
                                                    visitingget7.setText(String.valueOf(Int_visitinggetLast));
                                                    int Vcount = Integer.valueOf(visitingcount.getText().toString());
                                                    Vcount = Vcount - 1;
                                                    visitingcount.setText(String.valueOf(Vcount));
                                                }
                                            } else {
                                                String homegetText = homeget7.getText().toString();
                                                int Int_homegetLast = Integer.valueOf(homegetText);
                                                if(Int_homegetLast==0) {
                                                    Toast.makeText(context, getString(R.string.scorelow), Toast.LENGTH_SHORT).show();
                                                }else {
                                                    Int_homegetLast = Int_homegetLast - 1;
                                                    homeget7.setText(String.valueOf(Int_homegetLast));
                                                    int Hcount = Integer.valueOf(homecount.getText().toString());
                                                    Hcount = Hcount - 1;
                                                    homecount.setText(String.valueOf(Hcount));
                                                }
                                            }
                                            break;
                                        case 8:
                                            if (whichTeam == 0) {
                                                String visitinggetText = visitingget8.getText().toString();
                                                int Int_visitinggetLast = Integer.valueOf(visitinggetText);
                                                if(Int_visitinggetLast==0) {
                                                    Toast.makeText(context, getString(R.string.scorelow), Toast.LENGTH_SHORT).show();
                                                }else {
                                                    Int_visitinggetLast = Int_visitinggetLast - 1;
                                                    visitingget8.setText(String.valueOf(Int_visitinggetLast));
                                                    int Vcount = Integer.valueOf(visitingcount.getText().toString());
                                                    Vcount = Vcount - 1;
                                                    visitingcount.setText(String.valueOf(Vcount));
                                                }
                                            } else {
                                                String homegetText = homeget8.getText().toString();
                                                int Int_homegetLast = Integer.valueOf(homegetText);
                                                if(Int_homegetLast==0) {
                                                    Toast.makeText(context, getString(R.string.scorelow), Toast.LENGTH_SHORT).show();
                                                }else {
                                                    Int_homegetLast = Int_homegetLast - 1;
                                                    homeget8.setText(String.valueOf(Int_homegetLast));
                                                    int Hcount = Integer.valueOf(homecount.getText().toString());
                                                    Hcount = Hcount - 1;
                                                    homecount.setText(String.valueOf(Hcount));
                                                }
                                            }
                                            break;
                                        case 9:
                                            if (whichTeam == 0) {
                                                String visitinggetText = visitingget9.getText().toString();
                                                int Int_visitinggetLast = Integer.valueOf(visitinggetText);
                                                if(Int_visitinggetLast==0) {
                                                    Toast.makeText(context, getString(R.string.scorelow), Toast.LENGTH_SHORT).show();
                                                }else {
                                                    Int_visitinggetLast = Int_visitinggetLast - 1;
                                                    visitingget9.setText(String.valueOf(Int_visitinggetLast));
                                                    int Vcount = Integer.valueOf(visitingcount.getText().toString());
                                                    Vcount = Vcount - 1;
                                                    visitingcount.setText(String.valueOf(Vcount));
                                                }
                                            } else {
                                                String homegetText = homeget9.getText().toString();
                                                int Int_homegetLast = Integer.valueOf(homegetText);
                                                if(Int_homegetLast==0) {
                                                    Toast.makeText(context, getString(R.string.scorelow), Toast.LENGTH_SHORT).show();
                                                }else {
                                                    Int_homegetLast = Int_homegetLast - 1;
                                                    homeget9.setText(String.valueOf(Int_homegetLast));
                                                    int Hcount = Integer.valueOf(homecount.getText().toString());
                                                    Hcount = Hcount - 1;
                                                    homecount.setText(String.valueOf(Hcount));
                                                }
                                            }
                                            break;
                                        default:
                                            if (whichTeam == 0) {
                                                String visitinggetText = visitingget10.getText().toString();
                                                int Int_visitinggetLast = Integer.valueOf(visitinggetText);
                                                if(Int_visitinggetLast==0) {
                                                    Toast.makeText(context, getString(R.string.scorelow), Toast.LENGTH_SHORT).show();
                                                }else {
                                                    Int_visitinggetLast = Int_visitinggetLast - 1;
                                                    visitingget10.setText(String.valueOf(Int_visitinggetLast));
                                                    int Vcount = Integer.valueOf(visitingcount.getText().toString());
                                                    Vcount = Vcount - 1;
                                                    visitingcount.setText(String.valueOf(Vcount));
                                                }
                                            } else {
                                                String homegetText = homeget10.getText().toString();
                                                int Int_homegetLast = Integer.valueOf(homegetText);
                                                if(Int_homegetLast==0) {
                                                    Toast.makeText(context, getString(R.string.scorelow), Toast.LENGTH_SHORT).show();
                                                }else {
                                                    Int_homegetLast = Int_homegetLast - 1;
                                                    homeget10.setText(String.valueOf(Int_homegetLast));
                                                    int Hcount = Integer.valueOf(homecount.getText().toString());
                                                    Hcount = Hcount - 1;
                                                    homecount.setText(String.valueOf(Hcount));
                                                }
                                            }
                                    }
                                }
                            });
                    builder.setNegativeButton(getString(R.string.layout_cancel),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    builder.create().show();
                }else {
                    switch(Number_Count) {
                        case 1:
                            if (whichTeam == 0) {
                                String visitinggetText = visitingget1.getText().toString();
                                int Int_visitinggetLast = Integer.valueOf(visitinggetText);
                                if(Int_visitinggetLast==0) {
                                    Toast.makeText(context, getString(R.string.scorelow), Toast.LENGTH_SHORT).show();
                                }else {
                                    Int_visitinggetLast = Int_visitinggetLast - 1;
                                    visitingget1.setText(String.valueOf(Int_visitinggetLast));
                                    int Vcount = Integer.valueOf(visitingcount.getText().toString());
                                    Vcount = Vcount - 1;
                                    visitingcount.setText(String.valueOf(Vcount));
                                }
                            } else {
                                String homegetText = homeget1.getText().toString();
                                int Int_homegetLast = Integer.valueOf(homegetText);
                                if(Int_homegetLast==0) {
                                    Toast.makeText(context, getString(R.string.scorelow), Toast.LENGTH_SHORT).show();
                                }else {
                                    Int_homegetLast = Int_homegetLast - 1;
                                    homeget1.setText(String.valueOf(Int_homegetLast));
                                    int Hcount = Integer.valueOf(homecount.getText().toString());
                                    Hcount = Hcount - 1;
                                    homecount.setText(String.valueOf(Hcount));
                                }
                            }
                            break;
                        case 2:
                            if (whichTeam == 0) {
                                String visitinggetText = visitingget2.getText().toString();
                                int Int_visitinggetLast = Integer.valueOf(visitinggetText);
                                if(Int_visitinggetLast==0) {
                                    Toast.makeText(context, getString(R.string.scorelow), Toast.LENGTH_SHORT).show();
                                }else {
                                    Int_visitinggetLast = Int_visitinggetLast - 1;
                                    visitingget2.setText(String.valueOf(Int_visitinggetLast));
                                    int Vcount = Integer.valueOf(visitingcount.getText().toString());
                                    Vcount = Vcount - 1;
                                    visitingcount.setText(String.valueOf(Vcount));
                                }
                            } else {
                                String homegetText = homeget2.getText().toString();
                                int Int_homegetLast = Integer.valueOf(homegetText);
                                if(Int_homegetLast==0) {
                                    Toast.makeText(context, getString(R.string.scorelow), Toast.LENGTH_SHORT).show();
                                }else {
                                    Int_homegetLast = Int_homegetLast - 1;
                                    homeget2.setText(String.valueOf(Int_homegetLast));
                                    int Hcount = Integer.valueOf(homecount.getText().toString());
                                    Hcount = Hcount - 1;
                                    homecount.setText(String.valueOf(Hcount));
                                }
                            }
                            break;
                        case 3:
                            if (whichTeam == 0) {
                                String visitinggetText = visitingget3.getText().toString();
                                int Int_visitinggetLast = Integer.valueOf(visitinggetText);
                                if(Int_visitinggetLast==0) {
                                    Toast.makeText(context, getString(R.string.scorelow), Toast.LENGTH_SHORT).show();
                                }else {
                                    Int_visitinggetLast = Int_visitinggetLast - 1;
                                    visitingget3.setText(String.valueOf(Int_visitinggetLast));
                                    int Vcount = Integer.valueOf(visitingcount.getText().toString());
                                    Vcount = Vcount - 1;
                                    visitingcount.setText(String.valueOf(Vcount));
                                }
                            } else {
                                String homegetText = homeget3.getText().toString();
                                int Int_homegetLast = Integer.valueOf(homegetText);
                                if(Int_homegetLast==0) {
                                    Toast.makeText(context, getString(R.string.scorelow), Toast.LENGTH_SHORT).show();
                                }else {
                                    Int_homegetLast = Int_homegetLast - 1;
                                    homeget3.setText(String.valueOf(Int_homegetLast));
                                    int Hcount = Integer.valueOf(homecount.getText().toString());
                                    Hcount = Hcount - 1;
                                    homecount.setText(String.valueOf(Hcount));
                                }
                            }
                            break;
                        case 4:
                            if (whichTeam == 0) {
                                String visitinggetText = visitingget4.getText().toString();
                                int Int_visitinggetLast = Integer.valueOf(visitinggetText);
                                if(Int_visitinggetLast==0) {
                                    Toast.makeText(context, getString(R.string.scorelow), Toast.LENGTH_SHORT).show();
                                }else {
                                    Int_visitinggetLast = Int_visitinggetLast - 1;
                                    visitingget4.setText(String.valueOf(Int_visitinggetLast));
                                    int Vcount = Integer.valueOf(visitingcount.getText().toString());
                                    Vcount = Vcount - 1;
                                    visitingcount.setText(String.valueOf(Vcount));
                                }
                            } else {
                                String homegetText = homeget4.getText().toString();
                                int Int_homegetLast = Integer.valueOf(homegetText);
                                if(Int_homegetLast==0) {
                                    Toast.makeText(context, getString(R.string.scorelow), Toast.LENGTH_SHORT).show();
                                }else {
                                    Int_homegetLast = Int_homegetLast - 1;
                                    homeget4.setText(String.valueOf(Int_homegetLast));
                                    int Hcount = Integer.valueOf(homecount.getText().toString());
                                    Hcount = Hcount - 1;
                                    homecount.setText(String.valueOf(Hcount));
                                }
                            }
                            break;
                        case 5:
                            if (whichTeam == 0) {
                                String visitinggetText = visitingget5.getText().toString();
                                int Int_visitinggetLast = Integer.valueOf(visitinggetText);
                                if(Int_visitinggetLast==0) {
                                    Toast.makeText(context, getString(R.string.scorelow), Toast.LENGTH_SHORT).show();
                                }else {
                                    Int_visitinggetLast = Int_visitinggetLast - 1;
                                    visitingget5.setText(String.valueOf(Int_visitinggetLast));
                                    int Vcount = Integer.valueOf(visitingcount.getText().toString());
                                    Vcount = Vcount - 1;
                                    visitingcount.setText(String.valueOf(Vcount));
                                }
                            } else {
                                String homegetText = homeget5.getText().toString();
                                int Int_homegetLast = Integer.valueOf(homegetText);
                                if(Int_homegetLast==0) {
                                    Toast.makeText(context, getString(R.string.scorelow), Toast.LENGTH_SHORT).show();
                                }else {
                                    Int_homegetLast = Int_homegetLast - 1;
                                    homeget5.setText(String.valueOf(Int_homegetLast));
                                    int Hcount = Integer.valueOf(homecount.getText().toString());
                                    Hcount = Hcount - 1;
                                    homecount.setText(String.valueOf(Hcount));
                                }
                            }
                            break;
                        case 6:
                            if (whichTeam == 0) {
                                String visitinggetText = visitingget6.getText().toString();
                                int Int_visitinggetLast = Integer.valueOf(visitinggetText);
                                if(Int_visitinggetLast==0) {
                                    Toast.makeText(context, getString(R.string.scorelow), Toast.LENGTH_SHORT).show();
                                }else {
                                    Int_visitinggetLast = Int_visitinggetLast - 1;
                                    visitingget6.setText(String.valueOf(Int_visitinggetLast));
                                    int Vcount = Integer.valueOf(visitingcount.getText().toString());
                                    Vcount = Vcount - 1;
                                    visitingcount.setText(String.valueOf(Vcount));
                                }
                            } else {
                                String homegetText = homeget6.getText().toString();
                                int Int_homegetLast = Integer.valueOf(homegetText);
                                if(Int_homegetLast==0) {
                                    Toast.makeText(context, getString(R.string.scorelow), Toast.LENGTH_SHORT).show();
                                }else {
                                    Int_homegetLast = Int_homegetLast - 1;
                                    homeget6.setText(String.valueOf(Int_homegetLast));
                                    int Hcount = Integer.valueOf(homecount.getText().toString());
                                    Hcount = Hcount - 1;
                                    homecount.setText(String.valueOf(Hcount));
                                }
                            }
                            break;
                        case 7:
                            if (whichTeam == 0) {
                                String visitinggetText = visitingget7.getText().toString();
                                int Int_visitinggetLast = Integer.valueOf(visitinggetText);
                                if(Int_visitinggetLast==0) {
                                    Toast.makeText(context, getString(R.string.scorelow), Toast.LENGTH_SHORT).show();
                                }else {
                                    Int_visitinggetLast = Int_visitinggetLast - 1;
                                    visitingget7.setText(String.valueOf(Int_visitinggetLast));
                                    int Vcount = Integer.valueOf(visitingcount.getText().toString());
                                    Vcount = Vcount - 1;
                                    visitingcount.setText(String.valueOf(Vcount));
                                }
                            } else {
                                String homegetText = homeget7.getText().toString();
                                int Int_homegetLast = Integer.valueOf(homegetText);
                                if(Int_homegetLast==0) {
                                    Toast.makeText(context, getString(R.string.scorelow), Toast.LENGTH_SHORT).show();
                                }else {
                                    Int_homegetLast = Int_homegetLast - 1;
                                    homeget7.setText(String.valueOf(Int_homegetLast));
                                    int Hcount = Integer.valueOf(homecount.getText().toString());
                                    Hcount = Hcount - 1;
                                    homecount.setText(String.valueOf(Hcount));
                                }
                            }
                            break;
                        case 8:
                            if (whichTeam == 0) {
                                String visitinggetText = visitingget8.getText().toString();
                                int Int_visitinggetLast = Integer.valueOf(visitinggetText);
                                if(Int_visitinggetLast==0) {
                                    Toast.makeText(context, getString(R.string.scorelow), Toast.LENGTH_SHORT).show();
                                }else {
                                    Int_visitinggetLast = Int_visitinggetLast - 1;
                                    visitingget8.setText(String.valueOf(Int_visitinggetLast));
                                    int Vcount = Integer.valueOf(visitingcount.getText().toString());
                                    Vcount = Vcount - 1;
                                    visitingcount.setText(String.valueOf(Vcount));
                                }
                            } else {
                                String homegetText = homeget8.getText().toString();
                                int Int_homegetLast = Integer.valueOf(homegetText);
                                if(Int_homegetLast==0) {
                                    Toast.makeText(context, getString(R.string.scorelow), Toast.LENGTH_SHORT).show();
                                }else {
                                    Int_homegetLast = Int_homegetLast - 1;
                                    homeget8.setText(String.valueOf(Int_homegetLast));
                                    int Hcount = Integer.valueOf(homecount.getText().toString());
                                    Hcount = Hcount - 1;
                                    homecount.setText(String.valueOf(Hcount));
                                }
                            }
                            break;
                        case 9:
                            if (whichTeam == 0) {
                                String visitinggetText = visitingget9.getText().toString();
                                int Int_visitinggetLast = Integer.valueOf(visitinggetText);
                                if(Int_visitinggetLast==0) {
                                    Toast.makeText(context, getString(R.string.scorelow), Toast.LENGTH_SHORT).show();
                                }else {
                                    Int_visitinggetLast = Int_visitinggetLast - 1;
                                    visitingget9.setText(String.valueOf(Int_visitinggetLast));
                                    int Vcount = Integer.valueOf(visitingcount.getText().toString());
                                    Vcount = Vcount - 1;
                                    visitingcount.setText(String.valueOf(Vcount));
                                }
                            } else {
                                String homegetText = homeget9.getText().toString();
                                int Int_homegetLast = Integer.valueOf(homegetText);
                                if(Int_homegetLast==0) {
                                    Toast.makeText(context, getString(R.string.scorelow), Toast.LENGTH_SHORT).show();
                                }else {
                                    Int_homegetLast = Int_homegetLast - 1;
                                    homeget9.setText(String.valueOf(Int_homegetLast));
                                    int Hcount = Integer.valueOf(homecount.getText().toString());
                                    Hcount = Hcount - 1;
                                    homecount.setText(String.valueOf(Hcount));
                                }
                            }
                            break;
                        default:
                            if (whichTeam == 0) {
                                String visitinggetText = visitingget10.getText().toString();
                                int Int_visitinggetLast = Integer.valueOf(visitinggetText);
                                if(Int_visitinggetLast==0) {
                                    Toast.makeText(context, getString(R.string.scorelow), Toast.LENGTH_SHORT).show();
                                }else {
                                    Int_visitinggetLast = Int_visitinggetLast - 1;
                                    visitingget10.setText(String.valueOf(Int_visitinggetLast));
                                    int Vcount = Integer.valueOf(visitingcount.getText().toString());
                                    Vcount = Vcount - 1;
                                    visitingcount.setText(String.valueOf(Vcount));
                                }
                            } else {
                                String homegetText = homeget10.getText().toString();
                                int Int_homegetLast = Integer.valueOf(homegetText);
                                if(Int_homegetLast==0) {
                                    Toast.makeText(context, getString(R.string.scorelow), Toast.LENGTH_SHORT).show();
                                }else {
                                    Int_homegetLast = Int_homegetLast - 1;
                                    homeget10.setText(String.valueOf(Int_homegetLast));
                                    int Hcount = Integer.valueOf(homecount.getText().toString());
                                    Hcount = Hcount - 1;
                                    homecount.setText(String.valueOf(Hcount));
                                }
                            }
                    }
                }
            }
        });
        Hit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if(isVib())
                    vibrate.vibrate(300);
                if(isDou()) {
                    Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(getString(R.string.messege));
                    builder.setMessage(getString(R.string.addhit));
                    builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            Scount=0;
                            Bcount=0;
                            setLight("SLight", Scount);
                            setLight("BLight", Bcount);
                        }
                    });
                    builder.setNegativeButton(getString(R.string.layout_cancel),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    builder.create().show();
                }else {
                    Scount=0;
                    Bcount=0;
                    setLight("SLight", Scount);
                    setLight("BLight", Bcount);
                }
            }
        });
    }
    public void setLight(String light, int count) {
        switch(light) {
            case "SLight":
                switch(count) {
                    case 0:
                        SLight1.setImageResource(R.drawable.nonelight);
                        SLight2.setImageResource(R.drawable.nonelight);
                        break;
                    case 1:
                        SLight1.setImageResource(R.drawable.slight);
                        SLight2.setImageResource(R.drawable.nonelight);
                        break;
                    case 2:
                        SLight1.setImageResource(R.drawable.slight);
                        SLight2.setImageResource(R.drawable.slight);
                        break;
                    case 3:
                        Toast.makeText(context, getString(R.string.out), Toast.LENGTH_SHORT).show();
                        Scount=0;
                        Bcount=0;
                        Ocount =Ocount+1;
                        setLight("SLight", Scount);
                        setLight("BLight", Bcount);
                        setLight("OLight", Ocount);
                }
                break;
            case "BLight":
                switch(count) {
                    case 0:
                        BLight1.setImageResource(R.drawable.nonelight);
                        BLight2.setImageResource(R.drawable.nonelight);
                        BLight3.setImageResource(R.drawable.nonelight);
                        break;
                    case 1:
                        BLight1.setImageResource(R.drawable.blight);
                        BLight2.setImageResource(R.drawable.nonelight);
                        BLight3.setImageResource(R.drawable.nonelight);
                        break;
                    case 2:
                        BLight1.setImageResource(R.drawable.blight);
                        BLight2.setImageResource(R.drawable.blight);
                        BLight3.setImageResource(R.drawable.nonelight);
                        break;
                    case 3:
                        BLight1.setImageResource(R.drawable.blight);
                        BLight2.setImageResource(R.drawable.blight);
                        BLight3.setImageResource(R.drawable.blight);
                        break;
                    case 4:
                        Toast.makeText(context, getString(R.string.pass), Toast.LENGTH_SHORT).show();
                        Scount=0;
                        Bcount=0;
                        setLight("BLight", Bcount);
                        setLight("SLight", Scount);
                }
                break;
            case "OLight":
                switch(count) {
                    case 0:
                        OLight1.setImageResource(R.drawable.nonelight);
                        OLight2.setImageResource(R.drawable.nonelight);
                        break;
                    case 1:
                        OLight1.setImageResource(R.drawable.olight);
                        OLight2.setImageResource(R.drawable.nonelight);
                        break;
                    case 2:
                        OLight1.setImageResource(R.drawable.olight);
                        OLight2.setImageResource(R.drawable.olight);
                        break;
                    case 3:
                        changeTeam();
                }
        }
    }

    protected void onPause() {
        super.onPause();
        if(pause==0)
            Toast.makeText(context, getString(R.string.pausetoast), Toast.LENGTH_LONG).show();
        //pause=1;
        //mc.cancel();
    }
    protected void onResume() {
        super.onResume();
		/*
		 * if(pause==1) { mc = new MyCount(sTime, 1000); mc.start(); }
		 */
    }
    protected void onDestroy() {
        mc.cancel();
        db.close();
        super.onDestroy();
    }
    public void setWidget() {
        SA = (Button) findViewById(R.id.SA); //加S
        SD = (Button) findViewById(R.id.SD); //減S
        BA = (Button) findViewById(R.id.BA); //加B
        BD = (Button) findViewById(R.id.BD); //減B
        OA = (Button) findViewById(R.id.OA); //加O
        OD = (Button) findViewById(R.id.OD); //減O
        GA = (Button) findViewById(R.id.GA); //進分
        GD = (Button) findViewById(R.id.GD); //退分
        Hit = (Button) findViewById(R.id.hitbutton);
        Pause = (Button) findViewById(R.id.pause);
        textView1 = (TextView) findViewById(R.id.textView1);
        textView1.setText(Number+gameTeam);
        visiting = (TextView) findViewById(R.id.visiting); // 先攻總的分
        home = (TextView) findViewById(R.id.home); // 後攻總的分
        visitingcount = (TextView) findViewById(R.id.visitingcount); // 先攻總的分
        homecount = (TextView) findViewById(R.id.homecount); // 後攻總的分
        visitingget1 = (TextView) findViewById(R.id.visitingget1); // 先攻各局得分
        homeget1 = (TextView) findViewById(R.id.homeget1); // 後攻各局得分
        visitingget2 = (TextView) findViewById(R.id.visitingget2); // 先攻各局得分
        homeget2 = (TextView) findViewById(R.id.homeget2); // 後攻各局得分
        visitingget3 = (TextView) findViewById(R.id.visitingget3); // 先攻各局得分
        homeget3 = (TextView) findViewById(R.id.homeget3); // 後攻各局得分
        visitingget4 = (TextView) findViewById(R.id.visitingget4); // 先攻各局得分
        homeget4 = (TextView) findViewById(R.id.homeget4); // 後攻各局得分
        visitingget5 = (TextView) findViewById(R.id.visitingget5); // 先攻各局得分
        homeget5 = (TextView) findViewById(R.id.homeget5); // 後攻各局得分
        visitingget6 = (TextView) findViewById(R.id.visitingget6); // 先攻各局得分
        homeget6 = (TextView) findViewById(R.id.homeget6); // 後攻各局得分
        visitingget7 = (TextView) findViewById(R.id.visitingget7); // 先攻各局得分
        homeget7 = (TextView) findViewById(R.id.homeget7); // 後攻各局得分
        visitingget8 = (TextView) findViewById(R.id.visitingget8); // 先攻各局得分
        homeget8 = (TextView) findViewById(R.id.homeget8); // 後攻各局得分
        visitingget9 = (TextView) findViewById(R.id.visitingget9); // 先攻各局得分
        homeget9 = (TextView) findViewById(R.id.homeget9); // 後攻各局得分
        visitingget10 = (TextView) findViewById(R.id.visitingget10); // 先攻各局得分
        homeget10 = (TextView) findViewById(R.id.homeget10); // 後攻各局得分
        timeView = (TextView) findViewById(R.id.timeView);
        SLight1 = (ImageView) findViewById(R.id.Slight1img);
        SLight2 = (ImageView) findViewById(R.id.Slight2img);
        BLight1 = (ImageView) findViewById(R.id.Blight1img);
        BLight2 = (ImageView) findViewById(R.id.Blight2img);
        BLight3 = (ImageView) findViewById(R.id.Blight3img);
        OLight1 = (ImageView) findViewById(R.id.Olight1img);
        OLight2 = (ImageView) findViewById(R.id.Olight2img);
        vibrate = (Vibrator) getApplication().getSystemService(Service.VIBRATOR_SERVICE);
    }
    public void pointspread(String point, int team) {
        if (!point.equals("0")) {
            if (team == 0) {
                visitingget1.setText(point);
                visitingcount.setText(point);
            } else {
                homeget1.setText(point);
                homecount.setText(point);
            }
        }
    }
    public void changeTeam() {
        Toast.makeText(context, getString(R.string.change), Toast.LENGTH_LONG).show();
        if(isVib()) vibrate.vibrate(new long[]{300, 100, 300, 100}, -1);
        if (whichTeam == 0) {
            whichTeam = 1;
            Number = String.valueOf(Number_Count);
            SetgameTeam();
            switch (Number_Count) {
                case 1:
                    if(homeget1.getText().toString().equals(""))
                        homeget1.setText("0");
                    break;
                case 2:
                    homeget2.setText("0");
                    break;
                case 3:
                    homeget3.setText("0");
                    break;
                case 4:
                    homeget4.setText("0");
                    break;
                case 5:
                    homeget5.setText("0");
                    break;
                case 6:
                    homeget6.setText("0");
                    break;
                case 7:
                    homeget7.setText("0");
                    break;
                case 8:
                    homeget8.setText("0");
                    break;
                case 9:
                    homeget9.setText("0");
                    break;
                case 10:
                    homeget10.setText("0");
            }
            textView1.setText(Number+gameTeam);
        }else {
            Number_Count = Number_Count + 1;
            whichTeam = 0;
            Number = String.valueOf(Number_Count);
            SetgameTeam();
            switch (Number_Count) {
                case 1:
                    visitingget1.setText("0");
                    break;
                case 2:
                    visitingget2.setText("0");
                    break;
                case 3:
                    visitingget3.setText("0");
                    break;
                case 4:
                    visitingget4.setText("0");
                    break;
                case 5:
                    visitingget5.setText("0");
                    break;
                case 6:
                    visitingget6.setText("0");
                    break;
                case 7:
                    visitingget7.setText("0");
                    break;
                case 8:
                    visitingget8.setText("0");
                    break;
                case 9:
                    visitingget9.setText("0");
                    break;
                case 10:
                    visitingget10.setText("0");
            }
            textView1.setText(Number+gameTeam);
        }
        Scount=0;
        Bcount=0;
        Ocount=0;
        setLight("OLight", Ocount);
        setLight("SLight", Scount);
        setLight("BLight", Bcount);
    }
    public void SetgameTeam() {
        if(whichTeam==0)
            gameTeam=" ▲";
        else gameTeam=" ▼";
    }
    //倒數計時
    class MyCount extends CountDownTimer {
        String minute, second;
        public MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }
        @Override
        public void onFinish() {
            stop=1;
            pause=1;
            timeView.setText("00 : 00");
            vibrate.vibrate(3000);
            Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(getString(R.string.timesup));
            builder.setMessage(getString(R.string.gametimesup));
            builder.setPositiveButton(getString(R.string.dialog_action_dismiss), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    vibrate.cancel();
                }
            });
            builder.create().show();
        }
        @Override
        public void onTick(long millisUntilFinished) {
            sTime=millisUntilFinished;
            if(millisUntilFinished/1000/60<10)
                minute="0";
            else minute="";
            if(millisUntilFinished/1000%60<10)
                second="0";
            else second="";
            timeView.setText(minute+millisUntilFinished/1000/60+ " : " + second+millisUntilFinished/1000%60);
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitDoubleClick();
        }
        return false;
    }

    private static boolean isExit = false;

    private void exitDoubleClick() {
        Timer timer = null;
        if (isExit == false) {
            isExit = true;
            Toast.makeText(context, getString(R.string.doubleclick), Toast.LENGTH_SHORT).show();
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false;
                }
            }, 2000);
        } else {
            finish();
            System.exit(0);
        }
    }
    public boolean isVib() {
        if(db.getData("vibrate").equals("true"))
            return true;
        else return false;
    }
    public boolean isSou() {
        if(db.getData("sound").equals("true"))
            return true;
        else return false;
    }
    public boolean isDou() {
        if(db.getData("doublecheck").equals("true"))
            return true;
        else return false;
    }
}
