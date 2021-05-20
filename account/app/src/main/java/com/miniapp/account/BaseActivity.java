package com.miniapp.account;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.miniapp.account.ActivityCollector;
/**
 * Created by zl on 20-11-27.
 */
public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

}
