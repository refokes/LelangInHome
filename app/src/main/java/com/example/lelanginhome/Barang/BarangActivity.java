package com.example.lelanginhome.Barang;

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

import com.example.lelanginhome.Lelang.HasilLelangActivity;
import com.example.lelanginhome.Lelang.HasilLelangActivitySpecial;
import com.example.lelanginhome.Lelang.LelangActivity;
import com.example.lelanginhome.Lelang.LelangActivitySpecial;
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

public class BarangActivity extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener {

    public static final String CARE_ID = "CARE_ID", CARE_PIC = "CARE_PIC", CARE_TITLE = "CARE_TITLE", CARE_CONTENT = "CARE_CONTENT", CARE_AUTHOR = "CARE_AUTHOR";

    private ArrayList<Barang> dataList;
    private FloatingActionButton btnAdd;
    private RecyclerView recyclerViewBarang;
    private BarangAdapter adapterCare;

    private Query refCare;
    private FirebaseDatabase database;

    private ImageView ivMenuBars;
    private NavigationView navigationSidebar;
    BarangActivity context;
    private FirebaseAuth mAuth;
    private static String lvl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barang);
        context=this;
        getlvl1();
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
        refCare = database.getReference("Barang");
//filter
//        refCare = database.getReference("Lelang").orderByChild("content").equalTo("Ada");
    }

    @Override
    protected void onStart() {
        super.onStart();
        refCare.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataList.clear();
                for (DataSnapshot value : dataSnapshot.getChildren()) {
                    Barang barang = value.getValue(Barang.class);
                    dataList.add(barang);
                }
                adapterCare = new BarangAdapter(BarangActivity.this, dataList);
                recyclerViewBarang.setAdapter(adapterCare);
                recyclerViewBarang.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
                adapterCare.notifyDataSetChanged();
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
                Barang barang = dataList.get(position);
                String id= barang.getIdBarang();
                showUpdateDialog(barang.getIdBarang());
            }
        }));
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BarangActivity.this, ManageBarangActivity.class);
                startActivity(intent);
            }
        });
    }

    private void showUpdateDialog(final String id){
        AlertDialog.Builder dialogb = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();

        final View dialogv = inflater.inflate(R.layout.dialog_barang,null);

        dialogb.setView(dialogv);

        final Button btn_delete = dialogv.findViewById(R.id.Delete);

        final AlertDialog dialog = dialogb.create();
        dialog.show();
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Delete(id);
                dialog.dismiss();
            }
        });

    }
    private void Delete(String id){
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Barang").child(id);
        db.removeValue();
        Toast.makeText(this,"Data berhasil dihapus",Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_barang) {
            finish();
            startActivity(new Intent(context, BarangActivity.class));
        } else if (id == R.id.menu_lelang) {
            finish();
            if (lvl.equals("Admin")){
                startActivity(new Intent(context, LelangActivity.class));
            }else{
                startActivity(new Intent(context, LelangActivitySpecial.class));
            }
        } else if (id == R.id.menu_hasil_lelang) {
            finish();
            if (lvl.equals("Admin")) {
                startActivity(new Intent(context, HasilLelangActivity.class));
            }else if(lvl.equals("Petugas")){
                startActivity(new Intent(context, HasilLelangActivitySpecial.class));
            }
        } else if (id == R.id.menu_Logout) {
            mAuth = FirebaseAuth.getInstance();
            mAuth.signOut();
            finish();
            startActivity(new Intent(BarangActivity.this, LoginActivity.class));


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
