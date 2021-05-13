/*

Assignment: Homework07
Group: B8
Group Members:
Anisha Kakwani
Hiten Changlani

 */
package com.example.hw07;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PictureDetailFragment extends Fragment implements PictureDetailAdapter.PictureDetailAdapterInterface{

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private static final String ARG_PARAM4 = "param4";
    TextView comments;
    ImageView img;
    RecyclerView recyclerView;
    EditText comment_Desc;
    String comment_desc;
    private LinearLayoutManager layoutManager;
    PictureDetailAdapter adapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<Comments> dataList = new ArrayList<>();
    MyProfileFragment.MyProfileFragmentInterface mListener;


    // TODO: Rename and change types of parameters
    private String loggedInUserId;
    private Pictures mParam2;
    private String loggedInUserName;
    private String selectedUserId;

    public PictureDetailFragment() {
        // Required empty public constructor
    }

    public static PictureDetailFragment newInstance(String param1, Pictures param2,String param3, String param4) {
        PictureDetailFragment fragment = new PictureDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putSerializable(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        args.putString(ARG_PARAM4, param4);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            loggedInUserId = getArguments().getString(ARG_PARAM1);
            mParam2 = (Pictures)getArguments().getSerializable(ARG_PARAM2);
            loggedInUserName = getArguments().getString(ARG_PARAM3);
            selectedUserId = getArguments().getString(ARG_PARAM4);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_picture_detail, container, false);
        comments = view.findViewById(R.id.textView_comments);
        comment_Desc = view.findViewById(R.id.editText_commentDesc);
        img = view.findViewById(R.id.imageView_picture);
        recyclerView = view.findViewById(R.id.recyclerView_Comments);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        StorageReference storeRef = FirebaseStorage.getInstance().getReference().child(selectedUserId).child(mParam2.getPhotoref());
            GlideApp.with(img.getContext())
                    .load(storeRef)
                    .into(img);

        getComments();
        view.findViewById(R.id.button_post).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comment_desc = comment_Desc.getText().toString();
                if(comment_desc.isEmpty()){
                    Toast.makeText(getActivity(),getResources().getString(R.string.comment_desc_blank),Toast.LENGTH_LONG).show();
                }
                else{
                    addComment(comment_desc);
                    comment_Desc.setText("");
                }
            }
        });

        return view;
    }

    public void addComment(String comment){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("comment", comment);
        userDetails.put("date", formatter.format(date));
        userDetails.put("pictureId", mParam2.getId());
        userDetails.put("author", loggedInUserName);
        userDetails.put("userId", loggedInUserId);
        db.collection("comments")
                .add(userDetails)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        dataList = new ArrayList<>();
                        adapter = new PictureDetailAdapter(dataList, loggedInUserId, loggedInUserName,PictureDetailFragment.this);
                        recyclerView.setAdapter(adapter);
                        getComments();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), getResources().getString(R.string.error_message) + " " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void getComments(){ db.collection("comments")
            .whereEqualTo("pictureId", mParam2.getId())
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){

                        for (QueryDocumentSnapshot document : task.getResult()) {

                            Comments commentData = new Comments();
                            commentData.setWriter((String) document.getData().get("author"));
                            commentData.setComment((String) document.getData().get("comment"));
                            commentData.setDateValue((String) document.getData().get("date"));
                            commentData.setPictureId((String) document.getData().get("pictureId"));
                            commentData.setUserID((String) document.getData().get("userId"));
                            commentData.setId(document.getId());

                            dataList.add(commentData);
                        }
                        comments.setText(dataList.size()+"");
                        adapter = new PictureDetailAdapter(dataList, loggedInUserId, loggedInUserName,PictureDetailFragment.this);
                        recyclerView.setAdapter(adapter);
                    }
                    else {
                        Toast.makeText(getActivity(), getResources().getString(R.string.error_message) + " : " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }

                }
            });
    }

    @Override
    public void deleteComment(String userId, Comments comment) {
        db.collection("comments").document(comment.getId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dataList = new ArrayList<>();
                        adapter = new PictureDetailAdapter(dataList, userId, "",PictureDetailFragment.this);
                        getComments();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), getResources().getString(R.string.error_message) + " " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}