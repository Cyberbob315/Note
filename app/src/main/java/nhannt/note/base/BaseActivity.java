package nhannt.note.base;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import nhannt.note.R;
import nhannt.note.listener.OnBackPressedListener;

public abstract class BaseActivity extends AppCompatActivity {

    private OnBackPressedListener mOnBackPressedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());
        settingToolbar();
    }

    private void settingToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(false);
                actionBar.setDisplayShowTitleEnabled(false);
            }
        }
    }

    public void setOnBackPressedListener(OnBackPressedListener onBackPressedListener){
        this.mOnBackPressedListener = onBackPressedListener;
    }

    @Override
    public void onBackPressed() {
        if(mOnBackPressedListener != null){
            mOnBackPressedListener.doBack();
        }else {
            super.onBackPressed();
        }
    }

    protected abstract int getLayout();
}
