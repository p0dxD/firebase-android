package com.example.notification;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.notification.MainActivity.PREF_NAME;

public class Login extends AppCompatActivity {

    private static final String TAG = "Login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        final Button logTokenButton = findViewById(R.id.accepId);
        final TextInputEditText inputEditText = findViewById(R.id.employeeId);
        logTokenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get token
                if (inputEditText.getText() != null && !inputEditText.getText().toString().equals("")) {
                    SharedPreferences settings = getSharedPreferences(PREF_NAME, 0);
                    String employeeId = inputEditText.getText().toString();
                    settings.edit().putString("id", inputEditText.getText().toString()).apply();
                    setTokenFirstTime(employeeId);
                    Toast.makeText(getApplicationContext(), "Welcome " + inputEditText.getText(), Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter employee id.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setTokenFirstTime(final String employeeId) {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }
                        OkHttpClient client = new OkHttpClient();
                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        Request request = new Request.Builder()
                                .url("http://192.168.1.251:8080/token")
                                .addHeader("token", token)
                                .addHeader("employeeId", employeeId)
                                .build();

                        Call call = client.newCall(request);
                        Response response = null;
                        try {
                            response = call.execute();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Log.i(TAG, "Response: " + response);
                    }
                });



    }
}
