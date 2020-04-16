package com.example.lelanginhome.Lelang;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.lelanginhome.R;
import com.example.lelanginhome.auth.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.Locale;

public class DetailHasilLelangActivity extends AppCompatActivity {
    public static String lvl;
    private TextView txtTitle,txtDesc,txtTelp, txtHargaAwal,txtHargaAkhir,txtPenawar,
            txtPic,txtCreateAt,txtCloseAt,txtPemilik,txtLabelTelp;
    Button btnTawar;
    private String id;
    EditText edtKolom;
    ImageView ivMenuBack, ivDetail;
    FirebaseDatabase firebaseDatabase,firebaseDatabase1;
    DatabaseReference ref,ref1;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_detail_hasil_lelang);
         Intent intent = getIntent();
         id = intent.getStringExtra(LelangActivitySpecial.LELANG_ID);

        firebaseDatabase = FirebaseDatabase.getInstance();
        ref = firebaseDatabase.getReference("Lelang");

         txtPic= (TextView) findViewById(R.id.tvPicTips);
         txtCreateAt=(TextView)findViewById(R.id.tvCreateAt);
         txtCloseAt=(TextView)findViewById(R.id.tvCloseAt);
         txtPemilik=(TextView)findViewById(R.id.tvPemilik);
         txtTitle = (TextView) findViewById(R.id.tvTitle);
         txtDesc = (TextView) findViewById(R.id.tvContent);
         txtHargaAwal = (TextView) findViewById(R.id.tvHargaAwal);
         txtHargaAkhir= (TextView) findViewById(R.id.tvHargaAkhir);
         txtPenawar= (TextView) findViewById(R.id.tvPenawar);
         ivDetail = (ImageView) findViewById(R.id.ivDetail);
         edtKolom = (EditText) findViewById(R.id.edtKolom);
         btnTawar = (Button) findViewById(R.id.btnTawar);
         txtLabelTelp=(TextView) findViewById(R.id.labelTelp);
         txtTelp =(TextView) findViewById(R.id.tvNoTelp);
         ivMenuBack = findViewById(R.id.ivMenuBack);
         ivMenuBack.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 finish();
                 if(lvl.equals("Admin")) {
                     Intent n = new Intent(DetailHasilLelangActivity.this, HasilLelangActivity.class);
                     startActivity(n);
                 }else if (lvl.equals("Petugas")){
                     Intent n = new Intent(DetailHasilLelangActivity.this, HasilLelangActivitySpecial.class);
                     startActivity(n);
                 }else {
                     Intent n = new Intent(DetailHasilLelangActivity.this, HasilLelangActivity.class);
                     startActivity(n);
                 }

             }
         });
         initComponent();

    }

    private void initComponent() {
        String al= FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference a =  firebaseDatabase.getReference("User").child(al);
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

        DatabaseReference db = ref.child(id);
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Lelang a  = dataSnapshot.getValue(Lelang.class);
                txtPic.setText(a.getPicTips());
                txtTitle.setText(a.getTitleTips());
                txtDesc.setText(a.getContent());
                txtPemilik.setText(a.getPemilik());
                txtCreateAt.setText(a.getCreateAt());
                txtCloseAt.setText(a.getCloseAt());
                txtHargaAwal.setText("Rp."+a.getHargaAwal());
                txtHargaAkhir.setText("Rp."+a.getHargaAkhir());
                txtPenawar.setText(a.getNamePemenang());
                Glide.with(DetailHasilLelangActivity.this).load(a.getPicTips()).into(ivDetail);
                if (lvl.equals("Petugas")){
                    txtLabelTelp.setText("No Pemenang:");
                    txtTelp.setText(a.getNoPemenang());
                }else if (lvl.equals("Admin")){
                    txtLabelTelp.setText("No Pemenang:");
                    txtTelp.setText(a.getNoPemenang());
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }



}

