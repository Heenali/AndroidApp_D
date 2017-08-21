package trukkeruae.trukkertech.com.trukkeruae.TrukkerUae;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatActivity;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import trukkeruae.trukkertech.com.trukkeruae.Helper.DataBaseHelper;
import trukkeruae.trukkertech.com.trukkeruae.Helper.SessionManager;
import trukkeruae.trukkertech.com.trukkeruae.Helper.UserFunctions;
import trukkeruae.trukkertech.com.trukkeruae.R;

public class SplashScreen extends Activity {
    protected boolean _active = true;
    protected int _splashTime = 5000;
    SessionManager session;
    String role_id;
    UserFunctions UF;
    DataBaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_splash_screen);

        initValue();

        Thread splashThread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    while (_active && (waited < _splashTime)) {
                        sleep(100);

                        if (_active) {
                            waited += 100;
                        }
                    }

                } catch (InterruptedException e) {

                } finally {
                    finish();
                    if (session.isLoggedIn()) {
                        Intent intent = new Intent(SplashScreen.this, DriverDashboard.class);
                        intent.putExtra("from", "splash");
                        startActivity(intent);
                        finish();
                        SplashScreen.this.overridePendingTransition(
                                R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
                    } else {
                        Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }


                }
            }
        };
        splashThread.start();
    }

    private void initValue() {
        session = new SessionManager(SplashScreen.this);
        db = new DataBaseHelper(SplashScreen.this);
        UF = new UserFunctions(SplashScreen.this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }
}
