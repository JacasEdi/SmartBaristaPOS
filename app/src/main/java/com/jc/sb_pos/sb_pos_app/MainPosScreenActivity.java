package com.jc.sb_pos.sb_pos_app;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jc.sb_pos.controller.SQLiteHandler;
import com.jc.sb_pos.controller.SessionManager;
import com.jc.sb_pos.helper.AppConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainPosScreenActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainPosScreenActivity.class.getSimpleName();

    private TextView txtName;
    private TextView txtEmail;

    //private static GlobalConstants gc = new GlobalConstants();
    //private static double userInitialCredits = gc.USER_INITIAL_CREDITS;

    private ProgressDialog pDialog;

    private SQLiteHandler db;
    private SessionManager session;

    RelativeLayout rlayout;
    ListView lv;

    ArrayList<HashMap<String, String>> orderList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_pos_screen);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Set the header view on the drawer
        View header = navigationView.getHeaderView(0);

        // Set user name and email fields
        txtName = (TextView) header.findViewById(R.id.userFullName);
        txtEmail = (TextView) header.findViewById(R.id.userEmailAddress);


        //Defining views
        rlayout = (RelativeLayout) findViewById(R.id.rl_main_screen);
        lv = (ListView) findViewById(R.id.orders_list);

        //ArrayList for storing orders from API
        orderList = new ArrayList<>();

        // SQLite db handler
        db = new SQLiteHandler(getApplicationContext());

        // Getting user details from SQLite db
        HashMap<String, String> user = db.getUserDetails();

        displayOrders();

        // Session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logout();
        }

        String user_name = user.get("user_name");
        String email = user.get("email");
        String balance = user.get("balance");

        // Displaying the user details on the drawer
        txtName.setText(user_name);
        txtEmail.setText(email);

        // Get the menu from navigationView
        Menu menu = navigationView.getMenu();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                onListViewItemSelected();
            }
        });

    }

    private void onListViewItemSelected()
    {
        Snackbar s = Snackbar.make(rlayout, "Order details currently unavailable", Snackbar.LENGTH_LONG);
        s.show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.menuChangeUserDetailsOption)
        {
            Intent intent = new Intent(MainPosScreenActivity.this, ChangeUserDetailsScreenActivity.class);
            startActivity(intent);
            finish();
        }
        else if (id == R.id.menuReportProblem)
        {
            Intent intent = new Intent(MainPosScreenActivity.this, ReportProblemScreenActivity.class);
            startActivity(intent);
            finish();
        }
        else if (id == R.id.menuLogoutOption)
        {
            // Logout User
            logout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // method for populating the layout with order history
    private void displayOrders()
    {
        // Creating a string request
        StringRequest stringRequest = new StringRequest(Request.Method.GET, AppConfig.API_URL + AppConfig.ORDERS,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        Log.d(TAG, "Response: " + response.toString());

                        try
                        {
                            JSONObject jsonObj = new JSONObject(response);

                            // Getting JSON Array node
                            JSONArray orders = jsonObj.getJSONArray("orders");

                            // looping through all orders
                            for (int i = 0; i < orders.length(); i++)
                            {
                                JSONObject o = orders.getJSONObject(i);

                                int id = o.getInt("id");
                                int user_id = o.getInt("user_id");
                                double amount = o.getDouble("amount");
                                String date = (String) o.get("created_at");

                                // tmp hash map for single order
                                HashMap<String, String> order = new HashMap<>();

                                // adding each child node to HashMap key => value
                                order.put("date", date);
                                order.put("amount", String.valueOf(amount));

                                // adding order to order list
                                orderList.add(order);
                            }

                            // Reversing the list of orders from the API so that the most recent ones
                            // can be seen on top of the list
                            Collections.reverse(orderList);

                            // Display orders in a ListView
                            ListAdapter adapter = new SimpleAdapter(
                                    MainPosScreenActivity.this, orderList, R.layout.list_item,
                                    new String[]{"date", "amount"},
                                    new int[]{R.id.order_date, R.id.amount});

                            lv.setAdapter(adapter);
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                            Log.e(TAG, "Json Error: " + e.getMessage());
                        }

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Log.e(TAG, "Retrieving Error: " + error.getMessage());
                    }
                }) {
        };

        // Adding the string request to the queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    //Logout function
    private void logout(){
        //Creating an alert dialog to confirm logout
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setIcon(R.drawable.logo);
        alertDialogBuilder.setTitle("Logout Smart-Barista...");
        alertDialogBuilder.setMessage("Are you sure you want to logout?");

        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface exitDialog, int which) {

                        session.setLogin(false);

                        db.deleteUser();

                        exitDialog.dismiss();

                        //Going back to 'Login Screen Activity'
                        Intent intent = new Intent(MainPosScreenActivity.this, LoginScreenActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

        alertDialogBuilder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface exitDialog, int which) {
                        exitDialog.dismiss();
                    }
                });

        //Showing the alert dialog
        AlertDialog exitAlertDialog = alertDialogBuilder.create();
        exitAlertDialog.show();

    }

    /**
     * Getting categories (webserver)
     * */
    /*private void getCategories(){
        // Tag used to cancel the request
        String tag_string_req = "req_categories";

//        pDialog.setMessage("Loading...");
//        showDialog();

        // Creating a JSON Object request
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                AppConfig.API_URL + AppConfig.CATEGORIES, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "Categories Response: " + response.toString());
                //hideDialog();

                try {
                    // Getting the 'categories' json array of objects
                    JSONArray categories = response.getJSONArray("categories");

                    boolean error = response.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        // Looping through all categories nodes and storing
                        // them in array list
                        for (int i = 0; i < categories.length(); i++) {

                            // Getting the objects from the array
                            JSONObject category = (JSONObject) categories.get(i);

                            String id = category.getString("id");
                            String title = category.getString("title");
                            String description = category.getString("description");
                            String image = category.getString("image");
                            String cover_image = category.getString("cover_image");
                            String created_at = category.getString("created_at");

                            // Casting into 'int' Category ID
                            final int cast_cID = Integer.parseInt(id);

                            mCategory = new Category(cast_cID, title, description, image, cover_image, created_at);

                            cList.add(mCategory);
                        }
                        // Notifying adapter about data changes, so the
                        // list renders with new data
                        cAdapter.notifyDataSetChanged();

                    } else {
                        // Error connecting to the server. Get the error message
                        String errorMsg = response.getString("error_msg");
                        Log.e(TAG, "Error Msg: " + errorMsg);
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Log.e(TAG, "Json Error: " + e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                //hideDialog();
                // Show Warning Snackbar
                //Snackbar snackbar = Snackbar.make(coordinatorLayout, "Error... Check your internet connection!", Snackbar.LENGTH_LONG);
                //snackbar.show();
            }
        });

        // Adding the string request to the queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjReq);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }*/


    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            // Show Exit Alert Dialog
            exitDialog();
        }
    }

    private void exitDialog() {

        // Show Exit Alert Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.logo);
        builder.setTitle("Exit without Logout...");
        builder.setMessage("          Are You Sure?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface exitDialog, int which) {
                exitDialog.dismiss();
                finish();
                //Kills the processes in memory before exiting the App
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface exitDialog, int which) {
                exitDialog.dismiss();
            }
        });
        AlertDialog exitAlertDialog = builder.create();
        exitAlertDialog.show();
    }

}
