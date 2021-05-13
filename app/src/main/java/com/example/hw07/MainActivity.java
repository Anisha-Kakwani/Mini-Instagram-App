/*
File Name: Main Activity
Assignment: Homework07
Group: B8
Group Members:
Anisha Kakwani
Hiten Changlani
 */
package com.example.hw07;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity implements LoginFragment.LoginInterface, CreateNewAccountFragment.RegisterAccountInterface, UsersListFragment.UsersListInterface, MyProfileFragment.MyProfileFragmentInterface {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        getSupportFragmentManager().beginTransaction()
                .add(R.id.containerView, new LoginFragment())
                .commit();
    }

    @Override
    public void cancelRegistration() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.containerView, new LoginFragment())
                .commit();
    }

    @Override
    public void goToUsersListFragment(String id) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.containerView, UsersListFragment.newInstance(id))
                .commit();
    }

    @Override
    public void loginUser(String id) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.containerView, UsersListFragment.newInstance(id))
                .commit();
    }

    @Override
    public void createNewUser() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.containerView, new CreateNewAccountFragment())
                .commit();
    }

    @Override
    public void logout() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.containerView, new LoginFragment())
                .commit();
    }

    @Override
    public void viewMyProfile(String id) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.containerView, MyProfileFragment.newInstance(id))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void getPictureDetails(String userId, String name, String selectedUserId, Pictures p) {
        Log.d("demo",name+"MainActivity@@@@@@");
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.containerView, PictureDetailFragment.newInstance(userId,p,name,selectedUserId))
                .addToBackStack(null)
                .commit();
    }
}