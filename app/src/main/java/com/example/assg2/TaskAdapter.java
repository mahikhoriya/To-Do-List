package com.example.assg2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<Task> taskList;
    private Context context;
    private DatabaseReference databaseReference;

    public TaskAdapter(Context context, List<Task> taskList) {
        this.context = context;
        this.taskList = taskList;
        databaseReference = FirebaseDatabase.getInstance().getReference("tasks"); // Firebase reference
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.description.setText(task.getDescription());
        holder.priority.setText("Priority: " + task.getPriority());

        holder.btnEdit.setOnClickListener(v -> showUpdateDialog(task));

        holder.btnDelete.setOnClickListener(v -> deleteTask(task));
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView description, priority;
        ImageButton btnEdit, btnDelete;

        TaskViewHolder(View itemView) {
            super(itemView);
            description = itemView.findViewById(R.id.taskDescription);
            priority = itemView.findViewById(R.id.taskPriority);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    private void showUpdateDialog(Task task) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Update Task");

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_update_task, null);
        EditText editDescription = view.findViewById(R.id.editTaskDescription);
        EditText editPriority = view.findViewById(R.id.editTaskPriority);

        editDescription.setText(task.getDescription());
        editPriority.setText(task.getPriority());

        builder.setView(view);
        builder.setPositiveButton("Update", (dialog, which) -> {
            String newDescription = editDescription.getText().toString().trim();
            String newPriority = editPriority.getText().toString().trim();

            if (!newDescription.isEmpty() && !newPriority.isEmpty()) {
                DatabaseReference taskRef = databaseReference.child(task.getId());
                taskRef.child("description").setValue(newDescription);
                taskRef.child("priority").setValue(newPriority);
                Toast.makeText(context, "Task updated!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Fields cannot be empty!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteTask(Task task) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Task")
                .setMessage("Are you sure you want to delete this task?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    databaseReference.child(task.getId()).removeValue();
                    taskList.remove(task);
                    notifyDataSetChanged();
                    Toast.makeText(context, "Task deleted!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
