/*

Assignment: Homework07
Group: B8
Group Members:
Anisha Kakwani
Hiten Changlani
 */
package com.example.hw07;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class MyProfileAdapter extends RecyclerView.Adapter<MyProfileAdapter.ProfileViewHolder>{


        ArrayList<Pictures> pictures;
        String userName, loggedInUser,selectedUser;
        MyProfileAdapterInterface listener;
        FirebaseAuth mAuth = FirebaseAuth.getInstance();


        public MyProfileAdapter(ArrayList<Pictures> usersList, String UserId, String selectedUser, String userName, MyProfileAdapterInterface listener) {
            pictures = usersList;
            this.loggedInUser =UserId;
            this.userName = userName;
            this.selectedUser =selectedUser;
            this.listener = listener;
        }

        @NonNull
        @Override
        public  ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_profile_layout, parent, false);
            ProfileViewHolder viewHolder = new MyProfileAdapter.ProfileViewHolder(view,listener);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyProfileAdapter.ProfileViewHolder holder, int position) {
            holder.likes.setText(pictures.get(position).getNoOflikes()+"");
            holder.picture = pictures.get(position);
            holder.loggedInUserId = loggedInUser;
            holder.loggedInUserName = userName;
            holder.selectedUserId = selectedUser;
            mAuth = FirebaseAuth.getInstance();
            if (pictures.get(position).getUserID().equals(loggedInUser)) {
                holder.imageView_delete.setVisibility(View.VISIBLE);
            } else {
                holder.imageView_delete.setVisibility(View.GONE);
            }
            if(pictures.get(position).getLikeBy().contains(loggedInUser)){
                holder.imageView_likes.setVisibility(View.VISIBLE);
                holder.imageView_unlike.setVisibility(View.GONE);
            }
            else{
                holder.imageView_likes.setVisibility(View.GONE);
                holder.imageView_unlike.setVisibility(View.VISIBLE);
            }
            StorageReference storeRef = FirebaseStorage.getInstance().getReference().child(selectedUser).child(pictures.get(position).getPhotoref());
            GlideApp.with(holder.imageview.getContext())
                    .load(storeRef)
                    .into(holder.imageview);

        }



        @Override
        public int getItemCount() {
            return pictures.size();
        }

        public static class ProfileViewHolder extends RecyclerView.ViewHolder{

            TextView likes;
            ImageView imageview, imageView_delete,imageView_likes, imageView_unlike;
            Pictures picture;
            String loggedInUserId, loggedInUserName,selectedUserId;
            MyProfileAdapterInterface listener;

            public ProfileViewHolder(@NonNull View itemView, MyProfileAdapterInterface listener) {
                super(itemView);
                this.listener = listener;
                likes = itemView.findViewById(R.id.textView_likes);
                imageview = itemView.findViewById(R.id.imageView);
                imageView_delete = itemView.findViewById(R.id.imageView_delete);
                imageView_likes = itemView.findViewById(R.id.imageView_likes);
                imageView_unlike = itemView.findViewById(R.id.imageView_unlike);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.getUserProfileDetails(loggedInUserId, loggedInUserName,selectedUserId,picture);
                    }
                });
                imageView_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.deletePicture(loggedInUserId,picture);
                    }
                });

                imageView_likes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.likeDislikePicture("Dislike", loggedInUserId,picture);
                    }
                });

                imageView_unlike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.likeDislikePicture("Like", loggedInUserId,picture);
                    }
                });

            }
        }

        interface MyProfileAdapterInterface{
            void getUserProfileDetails(String b, String name,String selectedUser,Pictures p);
            void deletePicture(String b, Pictures p);
            void likeDislikePicture(String a, String b, Pictures p);

        }
    }




