package org.mixare;

import android.app.Activity;
import android.os.Bundle;
import com.qaz.client.R;

public class MixareActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
}