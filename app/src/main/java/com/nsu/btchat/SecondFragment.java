package com.nsu.btchat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

public class SecondFragment extends Fragment {
    public static SecondFragment newInstance(int page, String title) {
        SecondFragment fragment = new SecondFragment();
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_second, container, false);
        ImageView img = (ImageView) view.findViewById(R.id.img);
        img.setImageResource(R.drawable.img2);
        return view;
    }
}