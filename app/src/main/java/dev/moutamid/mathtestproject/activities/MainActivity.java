package dev.moutamid.mathtestproject.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import dev.moutamid.mathtestproject.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        b = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        b.viewUsersListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Permissions.check(MainActivity.this, Manifest.permission.READ_CONTACTS, null, new PermissionHandler() {
                    @Override
                    public void onGranted() {
                        //CODE HERE
//                        startActivity(new Intent(MainActivity.this, ContactPickerActivity.class)
//                                .putExtra(Constants.PARAMS, Constants.LOGIN_ACTIVITY));
                        startActivity(new Intent(MainActivity.this, ViewUsersListActivity.class));
                    }
                });

            }
        });

        b.registerStudentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Permissions.check(MainActivity.this, Manifest.permission.READ_CONTACTS, null, new PermissionHandler() {
                    @Override
                    public void onGranted() {
                        //CODE HERE
                        startActivity(new Intent(MainActivity.this, ContactPickerActivity.class));
//                                .putExtra(Constants.PARAMS, Constants.SIGN_UP_ACTIVITY));
//                        startActivity(new Intent(MainActivity.this, RegisterStudentActivity.class));
                    }
                });

            }
        });


    }
}