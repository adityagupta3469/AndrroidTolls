package com.example.androiduberclone;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button btnSignup,btnRegister;

    FirebaseAuth auth;
    FirebaseDatabase db;
    DatabaseReference users;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/Arkhip_font.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build());
        setContentView(R.layout.activity_main);

        //Init Firebase
        auth = FirebaseAuth.getInstance();
        db= FirebaseDatabase.getInstance();
        users = db.getReference("Users");

        //Init View
        btnRegister= (Button)findViewById(R.id.btnregisterIn);
        btnSignup= (Button)findViewById(R.id.btnSignIn);

        //Event
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRegisterDialog();
            }
        });
    }

    private void showRegisterDialog() {
        Al