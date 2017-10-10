package com.jc.sb_pos.sb_pos_app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jc.sb_pos.controller.AppConnectivity;
import com.jc.sb_pos.controller.SessionManager;
import com.jc.sb_pos.helper.AppConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterScreenActivity extends AppCompatActivity {

    private static final String TAG = RegisterScreenActivity.class.getSimpleName();

    private AppCompatButton buttonLogin;
    private AppCompatButton buttonRegister;
    private EditText inputUserName;
    private EditText inputFirstName;
    private EditText inputLastName;
    private EditText inputEmail;
    private EditText inputAddress;
    private EditText inputContactNumber;
    private EditText inputPassword;
    private EditText inputConfirmPassword;
    private ProgressDialog pDialog;
    private SessionManager session;

    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_screen);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.registerActivityCoordinatorLayout);

        buttonRegister = (AppCompatButton) findViewById(R.id.buttonRegister);
        buttonLogin = (AppCompatButton) findViewById(R.id.buttonLinkToLogin);

        inputUserName = (EditText) findViewById(R.id.editTextUserName);
        inputFirstName = (EditText) findViewById(R.id.editTextFirstName);
        inputLastName = (EditText) findViewById(R.id.editTextLastName);
        inputEmail = (EditText) findViewById(R.id.editTextEmail);
        inputAddress = (EditText) findViewById(R.id.editTextAddress);
        inputContactNumber = (EditText) findViewById(R.id.editTextContactNumber);
        inputPassword = (EditText) findViewById(R.id.editTextPassword);
        inputConfirmPassword = (EditText) findViewById(R.id.editTextConfirmPassword);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // If user is already logged in go to 'Home Screen Activity'
            Intent intent = new Intent(RegisterScreenActivity.this, MainPosScreenActivity.class);
            startActivity(intent);
            finish();
        }

        // Adding click listeners
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Calling the register function
                attemptRegister();
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Calling the Login Screen Activity
                Intent intent = new Intent(RegisterScreenActivity.this, LoginScreenActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * Attempts to register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual register attempt is made.
     */
    private void attemptRegister(){

        // Store values at the time of the register attempt.
        final String user_name = inputUserName.getText().toString().trim();
        final String first_name = inputFirstName.getText().toString().trim();
        final String last_name = inputLastName.getText().toString().trim();
        final String email = inputEmail.getText().toString().trim();
        final String address = inputAddress.getText().toString().trim();
        final String contact_number = inputContactNumber.getText().toString().trim();
        final String password = inputPassword.getText().toString().trim();
        final String confirm_password = inputConfirmPassword.getText().toString().trim();

        View focusView = null;

        if (TextUtils.isEmpty(user_name)) {
            inputUserName.setError(getString(R.string.error_field_required));
            focusView = inputUserName;
            focusView.requestFocus();
        }
        else if (TextUtils.isEmpty(first_name)) {
            inputFirstName.setError(getString(R.string.error_field_required));
            focusView = inputFirstName;
            focusView.requestFocus();
        }
        else if (TextUtils.isEmpty(last_name)) {
            inputLastName.setError(getString(R.string.error_field_required));
            focusView = inputLastName;
            focusView.requestFocus();
        }
        else if (TextUtils.isEmpty(email)) {
            inputEmail.setError(getString(R.string.error_field_required));
            focusView = inputEmail;
            focusView.requestFocus();
        }
        // Check for a valid email address.
        else if (!isEmailValid(email)) {
            inputEmail.setError(getString(R.string.error_invalid_email));
            focusView = inputEmail;
            focusView.requestFocus();
        }
        else if (TextUtils.isEmpty(address)) {
            inputAddress.setError(getString(R.string.error_field_required));
            focusView = inputAddress;
            focusView.requestFocus();
        }
        else if (TextUtils.isEmpty(contact_number)) {
            inputContactNumber.setError(getString(R.string.error_field_required));
            focusView = inputContactNumber;
            focusView.requestFocus();
        }
        // Check for a valid contact number.
        else if (!isContactNumberValid(contact_number)) {
            inputContactNumber.setError(getString(R.string.error_invalid_contact_number));
            focusView = inputContactNumber;
            focusView.requestFocus();
        }
        else if (TextUtils.isEmpty(password)) {
            inputPassword.setError(getString(R.string.error_field_required));
            focusView = inputPassword;
            focusView.requestFocus();
        }
        // Check for a valid password.
        else if (!isPasswordValid(password)) {
            inputPassword.setError(getString(R.string.error_invalid_password));
            focusView = inputPassword;
            focusView.requestFocus();
        }
        else if (TextUtils.isEmpty(confirm_password)) {
            inputConfirmPassword.setError(getString(R.string.error_field_required));
            focusView = inputConfirmPassword;
            focusView.requestFocus();
        }
        // Check for a valid password.
        else if (!isPasswordValid(confirm_password)) {
            inputConfirmPassword.setError(getString(R.string.error_invalid_password));
            focusView = inputConfirmPassword;
            focusView.requestFocus();
        }
        // Check if password equals confirm password.
        else if (!isPasswordConfirmed(password, confirm_password)) {
            inputConfirmPassword.setError(getString(R.string.error_invalid_password_match));
            focusView = inputConfirmPassword;
            focusView.requestFocus();
        }
        else{
            // Check Internet connection before register attempt
            checkInternetConnection(user_name, first_name, last_name, email, address, contact_number, password);
        }
    }

    private boolean isContactNumberValid(String contact_number) {
        //TODO: Replace this with your own logic
        return contact_number.length() > 10;
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    private boolean isPasswordConfirmed(String password, String confirm_password) {
        //TODO: Replace this with your own logic
        if (password.equals(confirm_password)){
            return true;
        }
        else{
            return  false;
        }
    }

    private void checkInternetConnection(String user_name, String first_name, String last_name, String email, String address, String contact_number, String password){
        // Flag for Internet Connection Status
        Boolean isInternetConn;

        // Connection Detector Class
        AppConnectivity connection;

        connection = new AppConnectivity(getApplicationContext());

        // Get Internet status
        isInternetConn = connection.isConnectingToInternet();

        // If we have Internet Connect
        if(isInternetConn){
            // Send user details to register
            register(user_name, first_name, last_name, email, address, contact_number, password);

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

    private void register(String user_name, String first_name, String last_name, String email, String address, String contact_number, String password){
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Registering ...");
        showDialog();

        // Getting values from edit texts
        final String mUsername = user_name;
        final String mFirstName = first_name;
        final String mLastName = last_name;
        final String mEmail = email;
        final String mAddress = address;
        final String mContactNumber = contact_number;
        final String mPassword = password;

        // Creating a string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.API_URL + AppConfig.REGISTER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Register Response: " + response.toString());
                        hideDialog();

                        try {
                            JSONObject jObj = new JSONObject(response);

                            boolean error = jObj.getBoolean("error");

                            if (!error) {
                                Toast.makeText(getApplicationContext(), "A new User created.", Toast.LENGTH_LONG).show();

                                // Going back to 'Login Screen Activity' after user register
                                Intent intent = new Intent(RegisterScreenActivity.this, LoginScreenActivity.class);
                                startActivity(intent);
                                finish();

                            } else {
                                // Error occurred on registration. Get the error message
                                String errorMsg = jObj.getString("error_msg");
                                Log.e(TAG, "Error Msg: " + errorMsg);
                                // If the server response is not success
                                // Displaying an error message on a Snackbar
                                Snackbar snackbar = Snackbar.make(coordinatorLayout, "User not registered!", Snackbar.LENGTH_LONG);
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
                        Log.e(TAG, "Registration Error: " + error.getMessage());
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
                params.put(AppConfig.KEY_USER_NAME, mUsername);
                params.put(AppConfig.KEY_FIRST_NAME, mFirstName);
                params.put(AppConfig.KEY_LAST_NAME, mLastName);
                params.put(AppConfig.KEY_EMAIL, mEmail);
                params.put(AppConfig.KEY_ADDRESS, mAddress);
                params.put(AppConfig.KEY_CONTACT_NUMBER, mContactNumber);
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
        // Going back to 'Login Screen Activity'
        Intent intent = new Intent(RegisterScreenActivity.this, LoginScreenActivity.class);
        startActivity(intent);
        finish();
    }
}
