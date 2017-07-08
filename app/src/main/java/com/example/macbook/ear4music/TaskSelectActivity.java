package com.example.macbook.ear4music;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class TaskSelectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_select);
    }

    public void onTask1ButtonClick(View view) {
        Intent intent = new Intent(getApplicationContext(), RandomNotesTaskActivity.class);
        startActivity(intent);
    }

    public void onTask2ButtonClick(View view) {
        Intent intent = new Intent(getApplicationContext(), CountOfRandomNotesTaskActivity.class);
        startActivity(intent);
    }
}
