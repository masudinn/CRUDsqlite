package com.masudinn.crudsqlite;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {
    EditText edtName,edtNim,edtAlamat;
    Button btnList,btnSave;
    ImageView imgView;

    final int Req_Code_Galery = 999;
    public static SqliteHelper msqlitehelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("New Record");

        edtName = findViewById(R.id.nama);
        edtAlamat = findViewById(R.id.alamat);
        edtNim = findViewById(R.id.nim);
        btnSave = findViewById(R.id.save);
        btnList = findViewById(R.id.list);
        imgView = findViewById(R.id.imageview);

        //database
        msqlitehelper = new SqliteHelper(this,"RECORDDB.sqlite",null,1);

        //tabel
        msqlitehelper.queryData("CREATE TABLE IF NOT EXISTS RECORD(id INTEGER PRIMARY KEY AUTOINCREMENT,name VARCHAR, nim VARVHAR,alamat VARCHAR,image BLOB)");


        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(
                        MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        Req_Code_Galery
                );
            }
        });

        btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,ListActivity.class));
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                 msqlitehelper.insertData(
                         edtName.getText().toString().trim(),
                         edtNim.getText().toString().trim(),
                         edtNim.getText().toString().trim(),
                         imgViewToByte(imgView)
                 );
                    Toast.makeText(MainActivity.this, "success", Toast.LENGTH_SHORT).show();
                    edtName.setText("");
                    edtNim.setText("");
                    edtAlamat.setText("");
                    imgView.setImageResource(R.drawable.ic_launcher_background);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    public static byte[] imgViewToByte(ImageView imgView) {
        Bitmap bitmap = ((BitmapDrawable)imgView.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == Req_Code_Galery){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,Req_Code_Galery);
            }else{
                Toast.makeText(this, "PERMISSION DENIED", Toast.LENGTH_SHORT).show();
            }
            return;
        }        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == Req_Code_Galery && resultCode==RESULT_OK){
            Uri image = data.getData();
            CropImage.activity(image)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK){
                Uri uri = result.getUri();
                imgView.setImageURI(uri);
            }else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error = result.getError();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
