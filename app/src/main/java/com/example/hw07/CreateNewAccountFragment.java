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
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;


public class CreateNewAccountFragment extends Fragment {

    EditText nameRegisterEditText, emailRegisterEditText, passwordRegisterEditText;
    RegisterAccountInterface mListener;
    String name, email, password;
    FirebaseAuth mAuth;

    public CreateNewAccountFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof RegisterAccountInterface){
            mListener = (RegisterAccountInterface) context;
        }
        else {
            throw new RuntimeException(getContext().toString() + " must implement RegisterAccountInterface");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_new_account, container, false);

        getActivity().setTitle(getResources().getString(R.string.createAccountTitle));

        mAuth = FirebaseAuth.getInstance();
        nameRegisterEditText = view.findViewById(R.id.nameRegisterEditText);
        emailRegisterEditText = view.findViewById(R.id.emailRegisterEditText);
        passwordRegisterEditText = view.findViewById(R.id.passwordRegisterEditText);

        view.findViewById(R.id.submitButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = nameRegisterEditText.getText().toString();
                email = emailRegisterEditText.getText().toString();
                password = passwordRegisterEditText.getText().toString();

                if (name.isEmpty() || email.isEmpty() || password.isEmpty()){
                    showAlertDialog(getResources().getString(R.string.mandatoryFields));
                }
                else {
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                sendUserInformationToFirebase(name, email);
                            }
                            else {
                                showAlertDialog(task.getException().getMessage());
                            }
                        }
                    });
                }
            }
        });


        view.findViewById(R.id.cancelRegister).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.cancelRegistration();
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


    void sendUserInformationToFirebase(String name, String email){

        HashMap<String, Object> userInformation = new HashMap<>();
        userInformation.put("Name", name);
        userInformation.put("Email", email);
        userInformation.put("UserID", FirebaseAuth.getInstance().getCurrentUser().getUid());

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").add(userInformation).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()){
                    mListener.goToUsersListFragment(FirebaseAuth.getInstance().getCurrentUser().getUid());
                }
                else {
                    showAlertDialog(task.getException().getMessage());
                }
            }
        });
    }


    interface RegisterAccountInterface{
        void cancelRegistration();
        void goToUsersListFragment(String id);
    }
}