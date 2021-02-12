package com.nsu.btchat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class MyAdapter extends ArrayAdapter<Bitmap>
        implements AdapterView.OnItemClickListener {

    private Context context;
    private ArrayList<Bitmap>list;
    private ListView myList;
    public TextView textname;
    ImageView img;

    public MyAdapter(Context context, int resource, ArrayList<Bitmap>list, ListView myList){
        super(context,resource,list);

        this.context=context;
        this.list=list;
        this.myList=myList;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater layoutInflater=
                (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        convertView=layoutInflater.inflate(R.layout.list_form,null);
        img = (ImageView) convertView.findViewById(R.id.ddd);

        Bitmap str=list.get(position);
        img.setImageBitmap(str);

        myList.setOnItemClickListener(this);

        return convertView;
    }
    @Override
    public  void onItemClick(AdapterView<?> adapterView,View view,int i,long l){

        Bitmap b= (Bitmap)getItem(i);
        ByteArrayOutputStream stream= new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.JPEG,100,stream);
        byte[] a=stream.toByteArray();
                Intent intent = new Intent(context, second.class);
               intent.putExtra("1", a);
                context.startActivity(intent);
    }
}
