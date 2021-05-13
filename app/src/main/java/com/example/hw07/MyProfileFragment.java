/*

Assignment: Homework07
Group: B8
Group Members:
Anisha Kakwani
Hiten Changlani
 */
package com.example.hw07;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;


public class MyProfileFragment extends Fragment implements MyProfileAdapter.MyProfileAdapterInterface {

    private static final String ARG_PARAM1 = "param1";
    private String selectedUser;
    static final int PICK_IMAGE = 1;
    FirebaseAuth mAuth;
    private String loggedInUserId, loggedInUserName;
    RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    MyProfileAdapter adapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<Pictures> dataList = new ArrayList<>();
    MyProfileFragmentInterface mListener;


    public MyProfileFragment() {
        // Required empty public constructor
    }

    public static MyProfileFragment newInstance(String param1) {
        MyProfileFragment fragment = new MyProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selectedUser = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_IMAGE && data!=null){
            Uri ImageUri = data.getData();
            StorageReference storeRef = FirebaseStorage.getInstance().getReference();
            String fileName = UUID.randomUUID().toString() + ".jpg";
            storeRef.child(mAuth.getUid()).child(fileName)
                    .putFile(ImageUri)
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful()){
                                storePhotoToFireStore(fileName);
                            }
                        }
                    });
        }
    }

    public void storePhotoToFireStore(String imageRef){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        HashMap<String,Object> data = new HashMap<>();
        data.put("PhotoRef",imageRef);
        data.put("UserID",FirebaseAuth.getInstance().getCurrentUser().getUid());
        data.put("userName",FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        data.put("Date", formatter.format(date));
        data.put("likeCount", new ArrayList<String>());
        db.collection("pictures")
                .add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        dataList = new ArrayList<>();
                        adapter = new MyProfileAdapter(dataList, loggedInUserId, selectedUser, loggedInUserName,MyProfileFragment.this);
                        getData();
                        Log.d("demo","Successfully added");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), getResources().getString(R.string.error_message) + " " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_profile, container, false);
        mAuth = FirebaseAuth.getInstance();
        loggedInUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if(selectedUser != ""){
            view.findViewById(R.id.button_addPhoto).setVisibility(View.GONE);
            getActivity().setTitle(getResources().getString(R.string.profile_details));
        }
        else{
            getActivity().setTitle(getResources().getString(R.string.my_profile));
            selectedUser = loggedInUserId;
        }
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        getUserName();
        getData();

        view.findViewById(R.id.button_addPhoto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery  = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery,PICK_IMAGE);
            }
        });
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MyProfileFragment.MyProfileFragmentInterface){
            mListener = (MyProfileFragment.MyProfileFragmentInterface) context;
        }
        else {
            throw new RuntimeException(getContext().toString() + " must implement UsersListInterface");
        }
    }

    public void getUserName() {

        db.collection("Users")
                .whereEqualTo("UserID", loggedInUserId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                loggedInUserName = (String) document.getData().get("Name");
                            }
                        } else {
                            Toast.makeText(getActivity(), getResources().getString(R.string.error_message) + " : " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void getData() {
        dataList = new ArrayList<>();
        db.collection("pictures")
                .whereEqualTo("UserID", selectedUser)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("demo", "Listen failed.", e);
                            return;
                        }

                        for (DocumentChange dataChanged : value.getDocumentChanges()) {
                            Log.d("demo", "onEvent: " + dataChanged.getType());
                            QueryDocumentSnapshot data;
                            switch (dataChanged.getType()) {
                                case ADDED:
                                    data = dataChanged.getDocument();
                                    addInList(data);
                                    break;
                                case MODIFIED:
                                    data = dataChanged.getDocument();
                                    updateInList(data);
                                    break;
                                case REMOVED:
                                    data = dataChanged.getDocument();
                                    deleteFromList(data);
                                    break;
                            }
                        }
                        adapter = new MyProfileAdapter(dataList, loggedInUserId, selectedUser,loggedInUserName,MyProfileFragment.this);
                        recyclerView.setAdapter(adapter);
                    }
    });}

    public void addInList(QueryDocumentSnapshot data) {
        Pictures Picdata = setData(data);
        if (dataList.contains(Picdata)) {
//            do nothing
        } else {
            dataList.add(Picdata);
        }
    }

    public void updateInList(QueryDocumentSnapshot data) {
        Pictures Picdata = setData(data);
        int index = -1;
        for (int i = 0; i < dataList.size(); i++) {
            if (dataList.get(i).getId().equals(data.getId())) {
                index = i;
                break;
            }
        }
        if (index != -1) {
            dataList.set(index, Picdata);
        }
    }

    public void deleteFromList(QueryDocumentSnapshot data) {
        int index = -1;
        for (int i = 0; i < dataList.size(); i++) {
            if (dataList.get(i).getId().equals(data.getId())) {
                index = i;
                break;
            }
        }
        if (index != -1) {
            dataList.remove(index);
        }
    }

    public Pictures setData(QueryDocumentSnapshot data) {
        ArrayList<String> like = (ArrayList<String>) data.getData().get("likeCount");
        Pictures picture = new Pictures();
        picture.setId(data.getId());
        picture.setUserID((String) data.getData().get("UserID"));
        picture.setDateValue((String) data.getData().get("Date"));
        picture.setPhotoref((String) data.getData().get("PhotoRef"));
        picture.setName((String) data.getData().get("userName"));
        picture.setNoOflikes(like.size());
        picture.setLikeBy(like);
        return picture;
    }

    @Override
    public void getUserProfileDetails(String b, String name,String selectedUser, Pictures p) {
        dataList = new ArrayList<>();
        adapter = new MyProfileAdapter(dataList, loggedInUserId, selectedUser, loggedInUserName,MyProfileFragment.this);
        mListener.getPictureDetails(loggedInUserId,loggedInUserName,selectedUser, p);
        // call main activity
    }

    @Override
    public void deletePicture(String b, Pictures p) {
        db.collection("pictures").document(p.getId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dataList = new ArrayList<>();
                        adapter = new MyProfileAdapter(dataList, loggedInUserId, selectedUser, loggedInUserName,MyProfileFragment.this);
                        getData();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), getResources().getString(R.string.error_message) + " " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public void likeDislikePicture(String a, String b, Pictures p) {
        if (a == "Like"){
            ArrayList likes = p.getLikeBy();
            likes.add(b);
            DocumentReference docRef = db.collection("pictures").document(p.getId());
            docRef.update("likeCount",likes)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            dataList = new ArrayList<>();
                            adapter = new MyProfileAdapter(dataList, loggedInUserId, selectedUser, loggedInUserName,MyProfileFragment.this);
                            getData();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), getResources().getString(R.string.error_message) + " " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });


        }
        else{
            ArrayList likes = p.getLikeBy();
            likes.remove(b);
            DocumentReference docRef = db.collection("pictures").document(p.getId());
            docRef.update("likeCount",likes)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            dataList = new ArrayList<>();
                            adapter = new MyProfileAdapter(dataList, loggedInUserId, selectedUser, loggedInUserName, MyProfileFragment.this);
                            getData();
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

    public interface MyProfileFragmentInterface {
        void getPictureDetails(String userId, String name, String selecteduserId, Pictures p);
    }
}