package nhannt.note.utils;

import android.app.Application;
import android.content.Context;

/**
 * Created by iceman on 1/23/2017.
 */

public class AppController extends Application {

    private static AppController mInstance;
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mContext = getApplicationContext();
    }

    private static Context getContext() {
        return mContext;
    }

    public static AppController getInstance() {
        return mInstance;
    }


}
