package org.letfbehinds.www;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

public class LoginActivity extends AppCompatActivity {

    TextInputLayout passwordWrapper;
    TextInputLayout usernameWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        usernameWrapper = (TextInputLayout) findViewById(R.id.usernameWrapper);
        passwordWrapper = (TextInputLayout) findViewById(R.id.passwordWrapper);
        Firebase.setAndroidContext(this);
        usernameWrapper.setHint("Email-ID");
        passwordWrapper.setHint("Password");

    }

    public void loginEmail(View view){

        if (!validate(view)) {
            onLoginFailed(view);
            return;
        }


        EditText loginEmail = (EditText) findViewById(R.id.loginEmail);
        EditText loginPassword = (EditText) findViewById(R.id.loginPassword);
        String email = loginEmail.getText().toString();
        String password = loginPassword.getText().toString();

        Firebase ref = new Firebase("https://left-behinds.firebaseio.com");
        // Create a handler to handle the result of the authentication

        findViewById(R.id.loginButton).setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        Firebase.AuthResultHandler authResultHandler = new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                // Authenticated successfully with payload authData
                progressDialog.dismiss();
                goToMainScreen();
            }
            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                // Authenticated failed with error firebaseError
                switch (firebaseError.getCode()) {
                    case FirebaseError.USER_DOES_NOT_EXIST:
                        usernameWrapper.setError("This account does not exist");
                        break;
                    case FirebaseError.INVALID_PASSWORD:
                        passwordWrapper.setError("Wrong password. Please try again");
                        break;
                    default:
                        Toast.makeText(getApplicationContext(), "Unknown error. Please try again", Toast.LENGTH_LONG).show();
                        break;
                }

                progressDialog.dismiss();
                findViewById(R.id.loginButton).setEnabled(true);

            }
        };

        ref.authWithPassword(email, password, authResultHandler);

    }

    public void goToMainScreen(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void signUp(View view){
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    public void onLoginFailed(View view) {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        findViewById(R.id.loginButton).setEnabled(true);
    }


    public boolean validate(View view) {
        boolean valid = true;

        EditText loginEmail = (EditText) findViewById(R.id.loginEmail);
        EditText loginPassword = (EditText) findViewById(R.id.loginPassword);

        String email = loginEmail.getText().toString();
        String password = loginPassword.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            usernameWrapper.setError("enter a valid email address");
            valid = false;
        } else {
            usernameWrapper.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            passwordWrapper.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            passwordWrapper.setError(null);
        }

        return valid;
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

}

