package org.letfbehinds.www;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    TextInputLayout inputPasswordWrapper;
    TextInputLayout inputEmailWrapper;
    TextInputLayout inputNameWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Firebase.setAndroidContext(this);
        inputEmailWrapper = (TextInputLayout) findViewById(R.id.input_emailWrapper);
        inputNameWrapper = (TextInputLayout) findViewById(R.id.input_nameWrapper);
        inputPasswordWrapper = (TextInputLayout) findViewById(R.id.input_passwordWrapper);

        inputPasswordWrapper.setHint("Password");
        inputEmailWrapper.setHint("Email");
        inputNameWrapper.setHint("Name");
    }

    protected void signUp(View view){
        final Firebase ref = new Firebase("https://left-behinds.firebaseio.com");

        findViewById(R.id.btn_signup).setEnabled(false);

        final EditText email = (EditText) findViewById(R.id.input_email);
        final EditText password = (EditText) findViewById(R.id.input_password);

        final ProgressDialog progressDialog = new ProgressDialog(SignUpActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();


        ref.createUser(email.getText().toString(), password.getText().toString(), new Firebase.ValueResultHandler<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> stringObjectMap) {

                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Account Created. Logging in...");

                ref.authWithPassword(email.getText().toString(), password.getText().toString(), new Firebase.AuthResultHandler() {
                    @Override
                    public void onAuthenticated(AuthData authData) {
                        progressDialog.dismiss();
                        goToMainScreen();
                    }

                    @Override
                    public void onAuthenticationError(FirebaseError firebaseError) {

                    }
                });


            }

            @Override
            public void onError(FirebaseError firebaseError) {
                //error in signup
                findViewById(R.id.btn_signup).setEnabled(true);

                switch (firebaseError.getCode()){
                    case FirebaseError.EMAIL_TAKEN:
                        inputEmailWrapper.setError("This email is already in use");
                        break;

                    case FirebaseError.NETWORK_ERROR:
                        Toast.makeText(getApplicationContext(), "No network available", Toast.LENGTH_LONG).show();
                        break;

                    default:
                        Toast.makeText(getApplicationContext(), "Unknown error. Please try again", Toast.LENGTH_LONG).show();

                }

                progressDialog.dismiss();
                findViewById(R.id.btn_signup).setEnabled(true);

            }
        });
    }

    public void goToMainScreen(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }

    public void goToLogin(View view){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);


    }

}
