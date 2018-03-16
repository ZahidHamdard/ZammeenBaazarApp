package com.zameenbaaazar.zameenbaazar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {


private static final String TAG = "RegisterActivity";
    private static final String URL_FOR_REGISTRATION = "http://zameenbaazar.com/api/signup-agent";
    ProgressDialog progressDialog;

    private EditText signupInputfName,signupInputlName, signupInputEmail, signupInputPassword,phoneNo,signupInputConPassword ;
    private Button btnSignUp;
    private Button btnLinkLogin;
    private Spinner catg;
    private Spinner pack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        signupInputfName = (EditText) findViewById(R.id.signup_input_fname);
        signupInputlName = (EditText) findViewById(R.id.signup_input_last_name);
        phoneNo = (EditText) findViewById(R.id.phonetext);
        signupInputConPassword = (EditText) findViewById(R.id.confirmPasswordText);
        signupInputEmail = (EditText) findViewById(R.id.signup_input_email);
        signupInputPassword = (EditText) findViewById(R.id.signup_input_password);
        pack=(Spinner)findViewById(R.id.packages);
        catg=(Spinner)findViewById(R.id.categories);

        btnSignUp = (Button) findViewById(R.id.btn_signup);
        btnLinkLogin = (Button) findViewById(R.id.btn_link_login);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();

            }
        });
        btnLinkLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void submitForm() {
        Integer choice=5;
        switch(pack.getSelectedItem().toString()){
            case "Trial Version":
                choice=5;
                break;


            case "Bronze":
                choice=1;
                break;


            case "Silver":
                choice=2;
                break;



            case "Gold":
                choice=3;
                break;


            case "Platinum":
                choice=4;
                break;

        }
        registerUser(signupInputfName.getText().toString(),
                signupInputlName.getText().toString(),
                signupInputEmail.getText().toString(),
                phoneNo.getText().toString(),
                signupInputPassword.getText().toString(),
                signupInputConPassword.getText().toString(),
                catg.getSelectedItem().toString(),
                 choice
                );


    }


    private void registerUser(final String fname,final String lname,  final String email, final String phone, final String password,final String confirmPassword,final String categories,final int pack) {
        // Tag used to cancel the request
        String cancel_req_tag = "register";

        progressDialog.setMessage("Adding you ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_FOR_REGISTRATION, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Register Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
//                    boolean error = jObj.getBoolean("error");
               JSONObject obj=jObj.getJSONObject("Header");
                int error=obj.getInt("error");
                    System.out.println(error);
                    if (error==0) {

                       // String user = jObj.getJSONObject("user").getString("name");
                        Toast.makeText(getApplicationContext(), "Hi " + fname+" " +lname+", You are successfully Added!", Toast.LENGTH_LONG).show();

                        // Launch login activity
                        Intent i = new Intent(getApplicationContext(),VerificationActivity.class);
                        i.putExtra("Email",signupInputEmail.getText().toString());
                        startActivity(i);
                        finish();
                    } else {

                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {

                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("fname", fname);
                params.put("lname", lname);
                params.put("phone", phone);
                params.put("email", email);
                params.put("password", password);
                params.put("cpassword", confirmPassword);
                params.put("category", categories);
                params.put("package", String.valueOf(pack));

                return params;
            }
        };
        // Adding request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(strReq, cancel_req_tag);
    }

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }
}

