package dev.moutamid.mathtestproject.startup;

import android.app.Application;

import dev.moutamid.mathtestproject.utils.Utils;

public class AppContext extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);
    }
}
