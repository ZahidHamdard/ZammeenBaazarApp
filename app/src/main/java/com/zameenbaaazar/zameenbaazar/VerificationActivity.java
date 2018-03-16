package com.zameenbaaazar.zameenbaazar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class VerificationActivity extends AppCompatActivity {
    private TextView setEmail;
    private ProgressDialog progressDialog;
    private static final String TAG = "Verification Activity";
    private EditText vText;
    private  final String URL_FOR_VERIFICATION="http://zameenbaazar.com/api/verify-agent";
    Intent email;
    String taken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        setEmail=(TextView)findViewById(R.id.email);
        vText=(EditText)findViewById(R.id.verificationCodeText);
            email=getIntent();
        if(email!=null){
            taken=email.getStringExtra("Email");
            setEmail.setText(taken);
        }
        Button verifyBtn=(Button)findViewById(R.id.verifybtn);
        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();

            }
        });



    }
    private void submitForm(){
        registerUser(Integer.parseInt(vText.getText().toString()),taken);
    }

    private void registerUser(final int verificatioinCode, final String email) {
        // Tag used to cancel the request
        String cancel_req_tag = "Verify";

        progressDialog.setMessage("Verifying you ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_FOR_VERIFICATION, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Verification Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
//                    boolean error = jObj.getBoolean("error");

                    int error=jObj.getInt("error");
                    System.out.println(error);
                    if (error==0) {

                        // String user = jObj.getJSONObject("user").getString("name");
                        Toast.makeText(getApplicationContext(), jObj.getString("message"), Toast.LENGTH_LONG).show();

//                        // Launch login activity
//                        Intent i = new Intent(getApplicationContext(),VerificationActivity.class);
//                        i.putExtra("Email",signupInputEmail.getText().toString());
//                        startActivity(i);
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
                Log.e(TAG, "Verification Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("code",String.valueOf(verificatioinCode) );

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
