package com.example.lelanginhome.Lelang;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lelanginhome.Barang.BarangActivity;
import com.example.lelanginhome.R;
import com.example.lelanginhome.auth.LoginActivity;
import com.example.lelanginhome.auth.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LelangActivity extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener {
    public static final String LELANG_ID = "LELANG_ID";

    private ArrayList<Lelang> dataList;
    private FloatingActionButton btnAdd;
    private RecyclerView recyclerViewBarang;
    private LelangAdapter adapter;

    private Query ref;
    private FirebaseDatabase database;

    private ImageView ivMenuBars;
    private NavigationView navigationSidebar;
    LelangActivity context;
    private FirebaseAuth mAuth;
    private static String lvl;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lelang);
        context=this;
        // Get view by id
        recyclerViewBarang = (RecyclerView) findViewById(R.id.rvCare);
        btnAdd = (FloatingActionButton) findViewById(R.id.fab_add);

        dataList = new ArrayList<>();

        ivMenuBars = (ImageView) findViewById(R.id.ivMenuBars);

        navigationSidebar = (NavigationView)findViewById(R.id.navigationBar);
        navigationSidebar.setNavigationItemSelectedListener(this);


        final DrawerLayout mDrawerLayout = findViewById(R.id.drawer_layout);

        ivMenuBars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                } else {
                    mDrawerLayout.openDrawer(GravityCompat.END);
                }
            }
        });

        eventListener();

        // Get a reference to our posts
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("Lelang").orderByChild("status").equalTo("1");
//filter
//        refCare = database.getReference("Lelang").orderByChild("content").equalTo("Ada");
    }

    @Override
    protected void onStart() {
        super.onStart();
        getlvl1();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataList.clear();
                for (DataSnapshot value : dataSnapshot.getChildren()) {
                    Lelang lelang = value.getValue(Lelang.class);
                    dataList.add(lelang);
                }
                adapter = new LelangAdapter(LelangActivity.this, dataList);
                recyclerViewBarang.setAdapter(adapter);
                recyclerViewBarang.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    private void eventListener() {
        recyclerViewBarang.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (lvl.equals("Admin")){
                    Lelang lelang = dataList.get(position);
                    String id= lelang.getIdLelang();
                    Intent intent = new Intent(LelangActivity.this, DetailLelangActivitySpecial.class);
                    intent.putExtra(LELANG_ID, id);
                    startActivity(intent);
                }else {
                    Lelang lelang = dataList.get(position);
                    String id= lelang.getIdLelang();
                    Intent intent = new Intent(LelangActivity.this, DetailLelangActivity.class);
                    intent.putExtra(LELANG_ID, id);
                    startActivity(intent);

                }
            }
        }));

    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_barang) {
            finish();
            if (lvl.equals("Admin")){
                startActivity(new Intent(context, BarangActivity.class));
            }else {
                Toast.makeText(LelangActivity.this, "Maaf ini Fitur khusus admin dan petugas", Toast.LENGTH_LONG).show();
            }
        } else if (id == R.id.menu_lelang) {
            finish();
            startActivity(new Intent(context, LelangActivity.class));
        } else if (id == R.id.menu_hasil_lelang) {
            finish();
            startActivity(new Intent(context, HasilLelangActivity.class));
        } else if (id == R.id.menu_Logout) {
            mAuth = FirebaseAuth.getInstance();
            mAuth.signOut();
            finish();
            startActivity(new Intent(LelangActivity.this, LoginActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void getlvl1(){
        String al= FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference a =  FirebaseDatabase.getInstance().getReference("User").child(al);
        a.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User u = dataSnapshot.getValue(User.class);
                lvl= u.getLevel();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
