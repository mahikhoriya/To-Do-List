package com.example.assg2;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private EditText editTextDescription, editTextPriority;
    private Button buttonAddTask;
    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextDescription = findViewById(R.id.editTextDescription);
        editTextPriority = findViewById(R.id.editTextPriority);
        buttonAddTask = findViewById(R.id.buttonAddTask);
        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(this, taskList);
        recyclerView.setAdapter(taskAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("tasks");

        buttonAddTask.setOnClickListener(v -> addTask());

        loadTasks();
    }

    private void addTask() {
        String description = editTextDescription.getText().toString().trim();
        String priority = editTextPriority.getText().toString().trim();

        if (TextUtils.isEmpty(description) || TextUtils.isEmpty(priority)) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        String id = databaseReference.push().getKey();
        Task task = new Task(id, description, priority);
        databaseReference.child(id).setValue(task);

        editTextDescription.setText("");
        editTextPriority.setText("");
        Toast.makeText(this, "Task Added!", Toast.LENGTH_SHORT).show();
    }

    private void loadTasks() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                taskList.clear();
                for (DataSnapshot taskSnapshot : snapshot.getChildren()) {
                    Task task = taskSnapshot.getValue(Task.class);
                    taskList.add(task);
                }
                taskAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Failed to load tasks!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
