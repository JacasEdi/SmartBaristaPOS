package com.jc.sb_pos.sb_pos_app;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jc.sb_pos.controller.AppConnectivity;
import com.jc.sb_pos.controller.SQLiteHandler;
import com.jc.sb_pos.controller.SessionManager;
import com.jc.sb_pos.helper.AppConfig;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginScreenActivity extends AppCompatActivity {

    private static final String TAG = LoginScreenActivity.class.getSimpleName();

    // Defining views
    private EditText editTextEmail;
    private EditText editTextPassword;
    private AppCompatButton buttonLogin;
    private AppCompatButton buttonRegister;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;

    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.loginActivityCoordinatorLayout);

        // Initializing views
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        buttonLogin = (AppCompatButton) findViewById(R.id.buttonLogin);
        buttonRegister = (AppCompatButton) findViewById(R.id.buttonLinkToRegister);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Session manager
        session = new SessionManager(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(LoginScreenActivity.this, MainPosScreenActivity.class);
            startActivity(intent);
            finish();
        }

        // Adding click listeners
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Calling the login function
                attemptLogin();
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Calling the Register Screen Activity
                Intent intent = new Intent(LoginScreenActivity.this, RegisterScreenActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * Attempts to sign in the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        // Store values at the time of the login attempt.
        final String email = editTextEmail.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError(getString(R.string.error_field_required));
            focusView = editTextEmail;
            cancel = true;

        } else if (!isEmailValid(email)) {
            editTextEmail.setError(getString(R.string.error_invalid_email));
            focusView = editTextEmail;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();

        } else {
            // Check for a valid password.
            if (TextUtils.isEmpty(password)) {
                editTextPassword.setError(getString(R.string.error_field_required));
                focusView = editTextPassword;
                focusView.requestFocus();

            } else if (!isPasswordValid(password)) {
                editTextPassword.setError(getString(R.string.error_invalid_password));
                focusView = editTextPassword;
                focusView.requestFocus();

            } else{
                // Check Internet connection before login attempt
                checkInternetConnection(email, password);
            }
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    private void checkInternetConnection(String email, String password){
        // Flag for Internet Connection Status
        Boolean isInternetConn;

        // Connection Detector Class
        AppConnectivity connection;

        connection = new AppConnectivity(getApplicationContext());

        // Get Internet status
        isInternetConn = connection.isConnectingToInternet();

        // If we have Internet Connect
        if(isInternetConn){
            // Send user details to login
            login(email, password);

        }else{
            // Show Warning Snackbar
            Snackbar snackbar = Snackbar.make(coordinatorLayout, "Error... Check your internet connection!", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    private void login(String email, String password){
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Logging in ...");
        showDialog();

        // Getting values from edit texts
        final String mEmail = email;
        final String mPassword = password;

        // Creating a string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.API_URL + AppConfig.LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Login Response: " + response.toString());
                        hideDialog();

                        try {
                            JSONObject jObj = new JSONObject(response);
                            JSONArray user = jObj.getJSONArray("user");

                            boolean error = jObj.getBoolean("error");

                            // Check for error node in json
                                if (!error) {
                                // user successfully logged in
                                // Create login session
                                session.setLogin(true);

                                // Now store the user in SQLite DB

                                // Parsing JSON Object from (webserver) response
                                JSONObject mUser = user.getJSONObject(0);
                                String user_id = mUser.getString("id");
                                String user_name = mUser.getString("user_name");
                                String first_name = mUser.getString("first_name");
                                String last_name = mUser.getString("last_name");
                                String email = mUser.getString("email");
                                String address = mUser.getString("address");
                                String contact_number = mUser.getString("contact_number");
                                String balance = mUser.getString("balance");
                                String api_key = mUser.getString("api_key");
                                String status = mUser.getString("status");
                                String created_at = mUser.getString("created_at");
                                String updated_at = mUser.getString("updated_at");

                                // Inserting row in 'user' table
                                db.addUser(user_id, user_name, first_name, last_name, email, address, contact_number, balance, api_key, status, created_at, updated_at);

                                // Launch main activity
                                Intent intent = new Intent(LoginScreenActivity.this, MainPosScreenActivity.class);
                                startActivity(intent);
                                finish();

                            } else {
                                // Error in login. Get the error message
                                String errorMsg = jObj.getString("error_msg");
                                Log.e(TAG, "Error Msg: " + errorMsg);
                                // If the server response is not success
                                // Displaying an error message on a Snackbar
                                Snackbar snackbar = Snackbar.make(coordinatorLayout, "User not logged in!", Snackbar.LENGTH_LONG);
                                snackbar.show();
                            }
                        } catch (JSONException e) {
                            // JSON error
                            e.printStackTrace();
                            Log.e(TAG, "Json Error: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Login Error: " + error.getMessage());
                        hideDialog();
                        // Show Warning Snackbar
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Error... Check your internet connection!", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                // Adding parameters to request
                params.put(AppConfig.KEY_EMAIL, mEmail);
                params.put(AppConfig.KEY_PASSWORD, mPassword);

                // Returning parameter
                return params;
            }
        };

        // Adding the string request to the queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onBackPressed(){
        //Creating an alert dialog to confirm exit app
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setIcon(R.drawable.logo);
        alertDialogBuilder.setTitle("Exit Smart-Barista...");
        alertDialogBuilder.setMessage("Are you sure?");

        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface exitDialog, int which) {
                exitDialog.dismiss();
                finish();
            }
        });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface exitDialog, int which) {
                exitDialog.dismiss();
            }
        });

        //Showing the alert dialog
        AlertDialog exitAlertDialog = alertDialogBuilder.create();
        exitAlertDialog.show();
    }
}
