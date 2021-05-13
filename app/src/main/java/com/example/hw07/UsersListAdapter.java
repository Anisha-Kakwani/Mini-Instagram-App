/*

Assignment: Homework07
Group: B8
Group Members:
Anisha Kakwani
Hiten Changlani

 */
package com.example.hw07;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class UsersListAdapter extends RecyclerView.Adapter<UsersListAdapter.UsersListViewHolder> {

    ArrayList<User> users;
    String userName, userID;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    UsersListAdapterInterface listener;

    public UsersListAdapter(ArrayList<User> usersList,UsersListAdapterInterface listener) {
        users = usersList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UsersListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_name_layout, parent, false);
        UsersListViewHolder viewHolder = new UsersListViewHolder(view, listener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UsersListViewHolder holder, int position) {
        holder.text1.setText(users.get(position).getName());
        holder.userId = users.get(position).getId();
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class UsersListViewHolder extends RecyclerView.ViewHolder{

        TextView text1;
        String userId;
        UsersListAdapterInterface listener;

        public UsersListViewHolder(@NonNull View itemView, UsersListAdapterInterface listener) {
            super(itemView);
            this.listener = listener;
            text1 = itemView.findViewById(R.id.textView_userName);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.getUserProfile(userId);
                }
            });
        }

    }

    interface UsersListAdapterInterface{
        void getUserProfile(String id);
    }
}
