package com.masudinn.crudsqlite;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    ListView mListView;
    ArrayList<Model> mList;
    ListAdapter mAdapter;
    ImageView mImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Record List");

        mListView = findViewById(R.id.list_view);
        mList = new ArrayList<>();
        mAdapter = new ListAdapter(this,R.layout.activity_list,mList);
        mListView.setAdapter(mAdapter);

        //get Data from sqlite
        Cursor cursor = MainActivity.msqlitehelper.getData("SELECT*FROM RECORD");
        mList.clear();
        while(cursor.moveToNext()){
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String nim = cursor.getString(2);
            String alamat = cursor.getString(3);
            byte[] image = cursor.getBlob(4);

            //add to List
            mList.add(new Model(id,name,nim,alamat,image));
        }
        mAdapter.notifyDataSetChanged();


        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final CharSequence[] items = {"update","delete"};
                AlertDialog.Builder dialog = new AlertDialog.Builder(ListActivity.this);
                dialog.setTitle("Pilih");
                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //update
                        if(position==0){
                            Cursor c = MainActivity.msqlitehelper.getData("SELECT id FROM RECORD");
                            ArrayList<Integer> arrId = new ArrayList<Integer>();
                            while(c.moveToNext()){
                                arrId.add(c.getInt(0));
                            }showDialogUpdate(ListActivity.this,arrId.get(position));
                        }
                        //delete
                        if(position==1){
                            Cursor c = MainActivity.msqlitehelper.getData("SELECT id FROM RECORD");
                            ArrayList<Integer> arrID = new ArrayList<>();
                            while(c.moveToNext()){
                                arrID.add(c.getInt(0));
                            }showDialogDelete(arrID.get(position));
                        }
                    }
                });
                dialog.show();
                return true;
            }
        });
    }

    private void showDialogDelete(final int idRecord){
        AlertDialog.Builder dialogDelete = new AlertDialog.Builder(ListActivity.this);
        dialogDelete.setTitle("Warning!");
        dialogDelete.setMessage("Are you sure to delete?");
        dialogDelete.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try{
                    MainActivity.msqlitehelper.deleteData(idRecord);
                    Toast.makeText(ListActivity.this, "Delete Success", Toast.LENGTH_SHORT).show();
                }catch(Exception e){
                    Log.e("error",e.getMessage());
                }
                updateList();
            }
        });
        dialogDelete.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialogDelete.show();
    }

    private void showDialogUpdate(Activity activity,final int position) {
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.update_activity);
        dialog.setTitle("UPDATE");

        mImageView = findViewById(R.id.UimageView);
        final EditText edtNama = dialog.findViewById(R.id.Unama);
        final EditText edtNim = dialog.findViewById(R.id.Unim);
        final EditText edtAlamat = dialog.findViewById(R.id.Ualamat);
        Button btnUpdate = dialog.findViewById(R.id.Uupdate);

        //set width dialog
        int widht = (int) (activity.getResources().getDisplayMetrics().widthPixels * 0.95);
        int height = (int) (activity.getResources().getDisplayMetrics().heightPixels * 0.7);
        dialog.getWindow().setLayout(widht, height);
        dialog.show();

        //click image to update
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(
                        ListActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        888
                );
            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    MainActivity.msqlitehelper.updateData(
                            edtNama.getText().toString().trim(),
                            edtNim.getText().toString().trim(),
                            edtAlamat.getText().toString().trim(),
                            MainActivity.imgViewToByte(mImageView),
                            position
                    );
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "success update", Toast.LENGTH_SHORT).show();
                }catch (Exception error){
                    Log.e("update error",error.getMessage());

                }
                updateList();
            }
          }
        );
    }

    private void updateList() {
        Cursor cursor = MainActivity.msqlitehelper.getData("SELECT * FROM RECORD");
        mList.clear();
        while(cursor.moveToNext()){
            int id = cursor.getInt(0);
            String nama = cursor.getString(1);
            String nim = cursor.getString(2);
            String alamat = cursor.getString(3);
            byte[] image = cursor.getBlob(4);

            mList.add(new Model(id,nama,nim,alamat,image));
        }
        mAdapter.notifyDataSetChanged();
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
        if(requestCode == 888){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,888);
            }else{
                Toast.makeText(this, "PERMISSION DENIED", Toast.LENGTH_SHORT).show();
            }
            return;
        }        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 888 && resultCode==RESULT_OK){
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
                mImageView.setImageURI(uri);
            }else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error = result.getError();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
