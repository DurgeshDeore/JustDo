package com.example.justdo;

import android.content.DialogInterface;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> tasks = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private ListView tasksListView;
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "TodoApp";
    private static final String TASKS_KEY = "tasks";
    private int priority=1; // default

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//nav bar color
            Window window = getWindow();
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.white));
        }

        tasksListView = findViewById(R.id.tasksListView);
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        loadTasks();  // load tasks from SharedPreferences

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tasks);
        tasksListView.setAdapter(adapter);

        findViewById(R.id.addButton).setOnClickListener(new View.OnClickListener() { //task button
            @Override
            public void onClick(View view) {
                addTask();
            }
        });

        findViewById(R.id.showManualButton).setOnClickListener(new View.OnClickListener() { //manaul button
            @Override
            public void onClick(View v) {
                showUserManualDialog();
            }
        });


        tasksListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() { //long click to delete
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                deleteTask(position);
                return true;
            }
        });
    }

    private void showUserManualDialog() {
        //  user manual text
        String userManualText = getString(R.string.user_manual);

        //  dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("User Manual")
                .setMessage(userManualText)
                .setCancelable(true)  // canceled by tapping outside
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void addTask() {
        final EditText taskInput = new EditText(this);
        new AlertDialog.Builder(this)
                .setTitle("Add New Task")
                .setView(taskInput)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String task = taskInput.getText().toString();
                        if (!task.isEmpty()) {
                            tasks.add(task+"\t\t"+priority);
                            priority++;
                            adapter.notifyDataSetChanged();
                            saveTasks();  // Save tasks after adding
                            Toast.makeText(MainActivity.this, "Task Added", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteTask(int position) {
        tasks.remove(position);
        adapter.notifyDataSetChanged();
        saveTasks();  // save tasks after deletion
        Toast.makeText(MainActivity.this, "Task Deleted", Toast.LENGTH_SHORT).show();
        priority--;
    }

    private void loadTasks() {
        String tasksString = sharedPreferences.getString(TASKS_KEY, "");
        if (!tasksString.isEmpty()) {
            String[] tasksArray = tasksString.split(",");
            for (String task : tasksArray) {
                tasks.add(task);
            }
        }
    }

    private void saveTasks() {
        StringBuilder tasksString = new StringBuilder();
        for (String task : tasks) {
            tasksString.append(task).append(",");
        }
        sharedPreferences.edit().putString(TASKS_KEY, tasksString.toString()).apply();
    }
}
