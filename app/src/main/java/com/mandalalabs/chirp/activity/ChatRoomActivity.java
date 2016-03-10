package com.mandalalabs.chirp.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.mandalalabs.chirp.R;

public class ChatRoomActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        Toast.makeText(ChatRoomActivity.this, "Welcome to the chat room!!!", Toast.LENGTH_SHORT).show();
    }
}
