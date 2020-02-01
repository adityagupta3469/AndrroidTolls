package com.example.andrroidtoll;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.example.andrroidtoll.Model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

public class MainActivity extends AppCompatActivity {

    Button btnSignup,btnRegister;

    RelativeLayout rootLayout;
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
        rootLayout = (RelativeLayout)findViewById(R.id.rootLayout);

        //Event
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRegisterDialog();
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoginDialog();
            }
        });
    }

    private void showLoginDialog() {
        AlertDialog.Builder dialog= new AlertDialog.Builder(this);
        dialog.setTitle("REGISTER");
        dialog.setMessage("Please use email to Sign in");

        LayoutInflater inflator = LayoutInflater.from(this);
        View login_layout = inflator.inflate(R.layout.layout_login,null);

        final MaterialEditText estEmail = login_layout.findViewById(R.id.edtEmail);
        final MaterialEditText estPassword = login_layout.findViewById(R.id.edtPassword);

        dialog.setView(login_layout);



        dialog.setPositiveButton("SIGN IN", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();

//                btnSignup.setEnabled(false);

                //Check Validation
                if(TextUtils.isEmpty(estEmail.getText().toString()))
                {
                    Snackbar.make(rootLayout,"Please enter email address:",Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(estPassword.getText().toString()))
                {
                    Snackbar.make(rootLayout,"Please enter Password:",Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if(estPassword.getText().toString().length()<6)
                {
                    Snackbar.make(rootLayout,"Password too short!!!...",Snackbar.LENGTH_SHORT).show();
                    return;
                }


//                        AlertDialog waitingdialog = new SpotsDialog(MainActivity.this);
                //        waitingdialog.show();


                //Login
                auth.signInWithEmailAndPassword(estEmail.getText().toString(),estPassword.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        //waitingdialog.dismiss();
                        startActivity(new Intent(MainActivity.this,Welcome.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        //waitingdialog.dismiss();
                        Snackbar.make(rootLayout,"Failed "+e.getMessage(),Snackbar.LENGTH_SHORT).show();

                        //btnSignup.setEnabled(true);
                    }
                });

            }

        });

        dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void showRegisterDialog() {
        AlertDialog.Builder dialog= new AlertDialog.Builder(this);
        dialog.setTitle("REGISTER");
        dialog.setMessage("Please use email to register");

        LayoutInflater inflator = LayoutInflater.from(this);
        View Register_layout = inflator.inflate(R.layout.layout_register,null);

        final MaterialEditText estEmail = Register_layout.findViewById(R.id.edtEmail);
        final MaterialEditText estPassword = Register_layout.findViewById(R.id.edtPassword);
        final MaterialEditText estName = Register_layout.findViewById(R.id.edtName);
        final MaterialEditText estPhone = Register_layout.findViewById(R.id.edtPhone);

        dialog.setView(Register_layout);

        dialog.setPositiveButton("Register", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();

                //Check Validation
                if(TextUtils.isEmpty(estEmail.getText().toString()))
                {
                    Snackbar.make(rootLayout,"Please enter email address:",Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(estPhone.getText().toString()))
                {
                    Snackbar.make(rootLayout,"Please enter Phone Number:",Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(estPassword.getText().toString()))
                {
                    Snackbar.make(rootLayout,"Please enter Password:",Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if(estPassword.getText().toString().length()<6)
                {
                    Snackbar.make(rootLayout,"Password too short!!!...",Snackbar.LENGTH_SHORT).show();
                    return;
                }

                //Register user

                auth.createUserWithEmailAndPassword(estEmail.getText().toString(),estPassword.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        // Save to user to db
                        User user = new User();
                        user.setEmail(estEmail.getText().toString());
                        user.setName(estName.getText().toString());
                        user.setPassword(estPassword.getText().toString());
                        user.setPhone(estPhone.getText().toString());

                        // use email to key
                        users.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Snackbar.make(rootLayout,"Register Successfully!!!...",Snackbar.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Snackbar.make(rootLayout,"Failed "+e.getMessage(),Snackbar.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Snackbar.make(rootLayout,"Failed "+e.getMessage(),Snackbar.LENGTH_SHORT).show();
                    }
                });
            }
        });

        dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
