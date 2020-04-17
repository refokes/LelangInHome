package com.example.lelanginhome.Lelang;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.lelanginhome.Barang.Barang;
import com.example.lelanginhome.R;
import com.example.lelanginhome.auth.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.R.layout.simple_spinner_item;

public class ManageLelangActivity extends AppCompatActivity implements  AdapterView.OnItemSelectedListener {
    public static String noTelp;
    Spinner spin;
    private Query refCare;
    private FirebaseDatabase database;
    //Firebase Database
    FirebaseDatabase firebaseDatabase;
    DatabaseReference ref,mDatabase;

    //Firebase Storage
    FirebaseStorage storage;
    StorageReference storageReference;
    StorageTask uploadTask;

    private EditText txtJudul, txtDesc,txtBatasWaktu,txtHargaAwal;
    private TextView btnSimpan;
    private ImageView ivChange,ivMenuback;

    private SimpleDateFormat dateFormatter;
    private static String url;
    private final int PICK_IMAGE_REQUEST = 71;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_lelang);

        firebaseDatabase = FirebaseDatabase.getInstance();
        ref = firebaseDatabase.getReference("Lelang");

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        initComponent();
        ivMenuback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(ManageLelangActivity.this,LelangActivitySpecial.class));
            }
        });
        initSpin();
        spin.setOnItemSelectedListener(this);
        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                Insert();
            }
        });
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        txtBatasWaktu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog();
            }
        });
    }

    private void initComponent() {
        txtJudul = (EditText) findViewById(R.id.txt_title);
        txtDesc = (EditText) findViewById(R.id.txt_desc);
        txtHargaAwal = (EditText)findViewById(R.id.txt_hargaAwal);
        txtBatasWaktu= (EditText)findViewById(R.id.txt_batasWaktu);
        btnSimpan = (TextView) findViewById(R.id.btn_simpan);
        ivChange = (ImageView) findViewById(R.id.iv_change);
        ivMenuback =(ImageView) findViewById(R.id.ivMenuBars);
        spin =(Spinner) findViewById(R.id.spin);
        String al= FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference a =  firebaseDatabase.getReference("User").child(al);
        a.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User u = dataSnapshot.getValue(User.class);
                noTelp= u.getNoTelp();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    private void Insert(){
        // Calendar
        Calendar now = Calendar.getInstance();
        int years = now.get(Calendar.YEAR);
        int months = now.get(Calendar.MONTH);
        int days = now.get(Calendar.DAY_OF_MONTH);

        // Get value to string
        String title = txtJudul.getText().toString();
        String desc = txtDesc.getText().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String createdAt = sdf.format(new Date());
        String author = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        String value = txtHargaAwal.getText().toString();
        int harga = Integer.parseInt(value);
        String batasWaktu = txtBatasWaktu.getText().toString();
        if(!TextUtils.isEmpty(title)){
            // get key id
            String id = ref.push().getKey();

            Lelang lelangData = new Lelang(id,url,title,desc,author,createdAt,batasWaktu,harga,harga,"none","1",noTelp);

            // Add data to firebase
            ref.child(id).setValue(lelangData);

            Toast.makeText(this,"Lelang berhasil di masukan",Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this,"Lelang gagal di masukan",Toast.LENGTH_LONG).show();
        }
    }



    public void initSpin(){
        mDatabase = FirebaseDatabase.getInstance().getReference("Barang");
        spin = (Spinner)findViewById(R.id.spin);

        Query query = mDatabase.orderByChild("namaBarang");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<Barang> titleList = new ArrayList<Barang>();

                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    String nama = dataSnapshot1.child("namaBarang").getValue(String.class);
                    String desk = dataSnapshot1.child("content").getValue(String.class);
                    String picTips= dataSnapshot1.child("picTips").getValue(String.class);

                    Barang barang = new Barang(picTips,nama,desk);
                    titleList.add(barang);
                }
                ArrayAdapter<Barang> arrayAdapter = new ArrayAdapter<Barang>(ManageLelangActivity.this,
                        android.R.layout.simple_spinner_item, titleList);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spin.setAdapter(arrayAdapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ManageLelangActivity.this,databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transform(new RoundedCorners(16));
        Barang barang = (Barang)parent.getSelectedItem();
        txtJudul.setText(barang.getNamaBarang());
        txtDesc.setText(barang.getContent());
        url = barang.getPicTips();
        Glide.with(ManageLelangActivity.this).load(barang.getPicTips()).into(ivChange);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    private void showDateDialog(){

        /**
         * Calendar untuk mendapatkan tanggal sekarang
         */
        Calendar newCalendar = Calendar.getInstance();

        /**
         * Initiate DatePicker dialog_barang
         */
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                /**
                 * Method ini dipanggil saat kita selesai memilih tanggal di DatePicker
                 */

                /**
                 * Set Calendar untuk menampung tanggal yang dipilih
                 */
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);

                /**
                 * Update TextView dengan tanggal yang kita pilih
                 */
                txtBatasWaktu.setText( dateFormatter.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        /**
         * Tampilkan DatePicker dialog_
         */
        datePickerDialog.show();
    }
}

