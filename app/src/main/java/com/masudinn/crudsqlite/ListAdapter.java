package com.masudinn.crudsqlite;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ListAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private ArrayList<Model> listItem;

    public ListAdapter(Context context, int layout, ArrayList<Model> listItem) {
        this.context = context;
        this.layout = layout;
        this.listItem = listItem;
    }

    @Override
    public int getCount() {
        return listItem.size();
    }

    @Override
    public Object getItem(int i) {
        return listItem.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    private class ViewHolder {
        ImageView imageView;
        TextView tvName, tvNim, tvAlamat;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {
        View item = view;
        ViewHolder holder = new ViewHolder();

            //put from the item_List to activity_list
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            item = inflater.inflate(R.layout.item_list, null);
            holder.tvName = item.findViewById(R.id.tvnama);
            holder.tvNim = item.findViewById(R.id.tvnim);
            holder.tvAlamat = item.findViewById(R.id.tvalamat);
            holder.imageView = item.findViewById(R.id.imageView);
            item.setTag(holder);

        //Show list activity
        Model model = listItem.get(i);
        holder.tvName.setText(model.getNama());
        holder.tvNim.setText(model.getNim());
        holder.tvAlamat.setText(model.getAlamat());
        byte[] listImage = model.getImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(listImage,0,listImage.length);
        holder.imageView.setImageBitmap(bitmap);
        return item;
    }
}
