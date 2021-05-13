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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class PictureDetailAdapter extends RecyclerView.Adapter<PictureDetailAdapter.PictureViewHolder>{
    ArrayList<Comments> commentList;
    PictureDetailAdapterInterface listener;
    String userName, userID;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    PictureDetailAdapter(ArrayList<Comments> comments, String UserId, String userName,PictureDetailAdapterInterface listener){
        commentList = comments;
        this.userID = UserId;
        this.userName = userName;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PictureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_detail_layout, parent, false);
        PictureDetailAdapter.PictureViewHolder viewHolder = new PictureDetailAdapter.PictureViewHolder(view,listener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PictureViewHolder holder, int position) {
        holder.author.setText(commentList.get(position).getWriter());
        holder.date.setText(commentList.get(position).getDateValue());;
        holder.description.setText(commentList.get(position).getComment());
        holder.comment = commentList.get(position);
        holder.userId = userID;
        if (commentList.get(position).getUserID().equals(userID)) {
            holder.img.setVisibility(View.VISIBLE);
        } else {
            holder.img.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public static class PictureViewHolder extends RecyclerView.ViewHolder{
        PictureDetailAdapterInterface listener;
        TextView author,date,description;
        Comments comment;
        ImageView img;
        String userId;
        public PictureViewHolder(@NonNull View itemView, PictureDetailAdapterInterface listener) {
            super(itemView);
            this.listener =listener;
            author = itemView.findViewById(R.id.textView_userName_comments);
            date = itemView.findViewById(R.id.textView_date_comments);
            description = itemView.findViewById(R.id.textView_commentDesc_comments);
            img = itemView.findViewById(R.id.imageView_delete_comments);

            itemView.findViewById(R.id.imageView_delete_comments).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.deleteComment(userId,comment);
                }
            });
        }
    }

    public interface PictureDetailAdapterInterface{
        void deleteComment(String userId, Comments comment);
    }
}
