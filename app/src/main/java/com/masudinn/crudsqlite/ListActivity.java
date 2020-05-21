package com.masudinn.crudsqlite;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

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
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                return false;
            }
        });
    }
}
