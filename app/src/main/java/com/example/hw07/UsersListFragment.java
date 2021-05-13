/*

Assignment: Homework07
Group: B8
Group Members:
Anisha Kakwani
Hiten Changlani

 */
package com.example.hw07;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class UsersListFragment extends Fragment implements UsersListAdapter.UsersListAdapterInterface {

    RecyclerView recyclerViewUsersList;
    LinearLayoutManager layoutManager;
    UsersListInterface mListener;
    ArrayList<User> usersList;
    UsersListAdapter adapter;
    private static final String ARG_PARAM1 = "param1";
    private String mParam1;


    public UsersListFragment() {
        // Required empty public constructor
    }

    public static UsersListFragment newInstance(String param1) {
        UsersListFragment fragment = new UsersListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof UsersListInterface){
            mListener = (UsersListInterface) context;
        }
        else {
            throw new RuntimeException(getContext().toString() + " must implement UsersListInterface");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_users_list, container, false);

        getActivity().setTitle(getResources().getString(R.string.usersListTitle));

        usersList = new ArrayList<>();
        recyclerViewUsersList = view.findViewById(R.id.recyclerViewUsersList);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerViewUsersList.setHasFixedSize(true);
        recyclerViewUsersList.setLayoutManager(layoutManager);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null){
                    showAlertDialog(error.getMessage());
                    return;
                }
                if (value != null){
                    for (QueryDocumentSnapshot documentSnapshot: value){
                        User user = new User();
                        if(!documentSnapshot.get("UserID").equals(mParam1)){
                            user.setName(documentSnapshot.get("Name").toString());
                            user.setId(documentSnapshot.get("UserID").toString());
                            usersList.add(user);
                        }


                    }
                    adapter = new UsersListAdapter(usersList,UsersListFragment.this);
                    Log.d("demo",usersList+"");
                    recyclerViewUsersList.setAdapter(adapter);
                }
            }
        });

        view.findViewById(R.id.button_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.logout();
            }
        });

        view.findViewById(R.id.button_profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.viewMyProfile("");
            }
        });

        return view;
    }


    private void showAlertDialog(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    interface UsersListInterface{
        void logout();
        void viewMyProfile(String id);
    }

    @Override
    public void getUserProfile(String id) {
        mListener.viewMyProfile(id);
    }
}