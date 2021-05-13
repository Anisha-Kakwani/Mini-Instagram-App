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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LoginFragment extends Fragment {

    EditText emailLoginEditText, passwordLoginEditText;
    String email, password;
    FirebaseAuth mAuth;
    LoginInterface mListener;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof LoginInterface){
            mListener = (LoginInterface) context;
        }
        else {
            throw new RuntimeException(getContext().toString() + " must implement LoginInterface");
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        getActivity().setTitle(getResources().getString(R.string.loginTitle));

        emailLoginEditText = view.findViewById(R.id.emailLoginEditText);
        passwordLoginEditText = view.findViewById(R.id.passwordLoginEditText);
        mAuth = FirebaseAuth.getInstance();

        view.findViewById(R.id.loginButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailLoginEditText.getText().toString();
                password = passwordLoginEditText.getText().toString();

                if (email.isEmpty() || password.isEmpty()){
                    showAlertDialog(getResources().getString(R.string.mandatoryFields));
                }
                else {
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                mListener.loginUser(mAuth.getCurrentUser().getUid());
                            }
                            else {
                                showAlertDialog(task.getException().getMessage());
                            }
                        }
                    });
                }
            }
        });


        view.findViewById(R.id.createAccountTextView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.createNewUser();
            }
        });

        return view;
    }

    interface LoginInterface{
        void loginUser(String email);
        void createNewUser();
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

}