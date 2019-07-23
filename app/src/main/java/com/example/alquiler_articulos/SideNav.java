package com.example.alquiler_articulos;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tapadoo.alerter.Alerter;

public class SideNav extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private ImageView photoImageViewh;
    private TextView nameTextViewh;
    private TextView emailTextViewh;
    private View header;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference UsersRef;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_user);
        UsersRef = FirebaseDatabase.getInstance().getReference().child("User").child("Name");
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new ArticlesFragments()).commit();
            navigationView.setCheckedItem(R.id.nav_List);
        }
        View headerLayout = navigationView.getHeaderView(0);
        photoImageViewh = (ImageView)headerLayout.findViewById(R.id.photoImageViewh);
        nameTextViewh = (TextView) headerLayout.findViewById(R.id.nameTextViewh);
        emailTextViewh = (TextView) headerLayout.findViewById(R.id.emailTextViewh);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    setUserData(user);
                } else {
                    Alerter.create(SideNav.this)
                            .setTitle(R.string.error)
                            .setText(R.string.error)
                            .setIcon(R.drawable.ic_person)
                            .setBackgroundColorRes(R.color.colorAccent)
                            .enableVibration(true)
                            .setDismissable(true)
                            .show();
                }
            }
        };

    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.nav_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new UserDetailsFragments()).commit();
                break;
            case R.id.nav_List:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ArticlesFragments()).commit();
                break;
            case R.id.nav_Additem:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new CreateArticleFragment()).commit();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    private void setUserData(FirebaseUser user) {
        nameTextViewh.setText(user.getDisplayName());
        emailTextViewh.setText(user.getEmail());

        // idTextView.setText(user.getUid());
        Glide.with(this).load(user.getPhotoUrl()).placeholder(R.drawable.ic_person).into(photoImageViewh);
    }
    @Override
    public void onStart() {
        super.onStart();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (firebaseAuthListener != null) {
            firebaseAuth.removeAuthStateListener(firebaseAuthListener);
        }
    }

}