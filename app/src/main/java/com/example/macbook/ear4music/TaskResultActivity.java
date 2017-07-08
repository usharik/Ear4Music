package com.example.macbook.ear4music;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;

public class TaskResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_result);

        String[][] taskResult = (String[][]) getIntent().getSerializableExtra("taskResult");
        TableView<String[]> tableView = (TableView<String[]>) findViewById(R.id.tableView);
        SimpleTableDataAdapter simpleTableDataAdapter = new SimpleTableDataAdapter(this, taskResult);
        simpleTableDataAdapter.setTextSize(10);
        tableView.setDataAdapter(simpleTableDataAdapter);
        SimpleTableHeaderAdapter simpleTableHeaderAdapter = new SimpleTableHeaderAdapter(this, getResources().getStringArray(R.array.result_table_columns));
        simpleTableHeaderAdapter.setTextSize(10);
        simpleTableHeaderAdapter.setTextColor(Color.WHITE);
        tableView.setHeaderAdapter(simpleTableHeaderAdapter);
    }
}
