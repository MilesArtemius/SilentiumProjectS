package com.ekdorn.silentiumproject.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ekdorn.silentiumproject.R;
import com.ekdorn.silentiumproject.silent_core.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by User on 21.04.2017.
 */

public class SignNewUserIn extends AppCompatActivity {
    EditText SetName;
    EditText SetPassword;
    EditText SetConfirmation;
    String name;
    String password;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("users");
    DatabaseReference mySilentiumRef = database.getReference("message").child("Silentium").child("members");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.signin_sign_in);

        SetName = (EditText) findViewById(R.id.setusername);
        SetPassword = (EditText) findViewById(R.id.setpassword);
        SetConfirmation = (EditText) findViewById(R.id.setconfirmation);
        Button SubmitButton = (Button) findViewById(R.id.submitbuttom);
        SubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = SetName.getText().toString();
                password = SetPassword.getText().toString();


                if (!password.equals(SetConfirmation.getText().toString())) {
                    Toast.makeText(SignNewUserIn.this, getString(R.string.password_match_error), Toast.LENGTH_SHORT).show(); //password validity check
                } else if (name.indexOf('&') != 0) {
                    Toast.makeText(SignNewUserIn.this, getString(R.string.password_ampersand_error), Toast.LENGTH_SHORT).show(); //& start name check
                } else if (name.contains(".") || name.contains("#") || name.contains("$") || name.contains("[") || name.contains("]") || name.contains("@")) {
                    Toast.makeText(SignNewUserIn.this, getString(R.string.password_symbols_error), Toast.LENGTH_SHORT).show(); //forbidden symbols check
                } else if (name.contains(" ")) {
                    Toast.makeText(SignNewUserIn.this, getString(R.string.password_space_error), Toast.LENGTH_SHORT).show(); //space check
                } else if (name.length() <= 5) {
                    Toast.makeText(SignNewUserIn.this, getString(R.string.password_length_error), Toast.LENGTH_SHORT).show(); //ID length check
                } else if (password.length() <= 7) {
                    Toast.makeText(SignNewUserIn.this, getString(R.string.password_longer_error), Toast.LENGTH_SHORT).show(); //password length check

                } else {
                    try {
                        FirebaseAuth.getInstance().createUserWithEmailAndPassword(name + "@silentium.notspec", password).addOnCompleteListener(SignNewUserIn.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d("TAG", "createUserWithEmail:onComplete:" + task.isSuccessful());
                                if (!task.isSuccessful()) {
                                    Toast.makeText(SignNewUserIn.this, getString(R.string.email_exists), Toast.LENGTH_SHORT).show();
                                } else {
                                    HashMap<String,String> arrayList = new HashMap<>();
                                    arrayList.put(UUID.randomUUID().toString(), FirebaseInstanceId.getInstance().getToken());
                                    HashMap<String,String> arrayList1 = new HashMap<>();
                                    arrayList1.put("0" + UUID.randomUUID().toString(), "Silentium");
                                    myRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(new User(name, name + "@silentium.notspec", arrayList, arrayList1));
                                    mySilentiumRef.child(UUID.randomUUID().toString()).setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());

                                    Intent intent = new Intent();
                                    setResult(RESULT_OK, intent);
                                    finish();
                                }
                            }
                        });
                    } catch (Exception e) {
                        Log.e("TAG", "onClick: ", e);
                        Toast.makeText(SignNewUserIn.this, getString(R.string.authentication_failed), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

}