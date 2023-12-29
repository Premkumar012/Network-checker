package com.example.myapp;

import static android.content.ContentValues.TAG;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity  {
    private EditText editText;
    private TextView textViewMessages;
    private ChatClient chatClient;

   JSONObject jsonObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);



        editText = findViewById(R.id.editText);
        Button buttonConnect = findViewById(R.id.buttonConnect);
        Button disConnect = findViewById(R.id.disConnect);
        Button accept = findViewById(R.id.accept);
        Button reject = findViewById(R.id.reject);

        textViewMessages = findViewById(R.id.textViewMessages);
        chatClient = new ChatClient(new ChatClient.ChatClientListener() {
            @Override
            public void onMessageReceived(String message) {
                try {
                    jsonObject = new JSONObject(message.substring(message.indexOf("{"), message.lastIndexOf("}") + 1));
                }catch (Exception e){
                    Log.d(TAG, ">>> Message received from server " +e.getMessage());
                }

                runOnUiThread(() -> textViewMessages.append("\n" + message));
            }
        });

        buttonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!editText.getText().toString().equalsIgnoreCase("")) {
                    editText.setEnabled(false);
                    buttonConnect.setEnabled(false);
                    chatClient.connectEstablish(editText.getText().toString());
                }else {
                    Toast.makeText(getApplicationContext(), "Ender valid UUID/Phone number", Toast.LENGTH_SHORT).show();
                }
            }
        });
        disConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatClient.disConnect(editText.getText().toString());
                buttonConnect.setEnabled(true);
            }
        });

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    if(jsonObject != null) {
                        jsonObject.put("subject","JOB_ACCEPTED");
                        chatClient.accept(jsonObject.toString());
                        accept.setVisibility(View.INVISIBLE);
                        reject.setVisibility(View.INVISIBLE);
                    }
                }catch (Exception e){

                }
            }
        });

        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    if(jsonObject != null) {
                        jsonObject.put("subject","JOB_REJECTED");
                        chatClient.reject(jsonObject.toString());
                        accept.setVisibility(View.INVISIBLE);
                        reject.setVisibility(View.INVISIBLE);
                    }
                }catch (Exception e){

                }
            }
        });
    }
}