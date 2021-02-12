package com.nsu.btchat;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class second extends AppCompatActivity {
    private ScaleGestureDetector mScaleGestureDetector;
    private float mScaleFactor = 1.0f;
    private ImageView mImageView;
    ImageView img12;
    Button saveBtn;
    public boolean onTouchEvent(MotionEvent motionEvent) {
        //변수로 선언해 놓은 ScaleGestureDetector
        mScaleGestureDetector.onTouchEvent(motionEvent);
        return true;
    }
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector){
            // ScaleGestureDetector에서 factor를 받아 변수로 선언한 factor에 넣고
            mScaleFactor *= scaleGestureDetector.getScaleFactor();
            // 최대 10배, 최소 10배 줌 한계 설정
            mScaleFactor = Math.max(0.1f,
                    Math.min(mScaleFactor, 10.0f));
            // 이미지뷰 스케일에 적용
            img12.setScaleX(mScaleFactor);
            img12.setScaleY(mScaleFactor);
            return true;
        }
    }
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second);
       img12=(ImageView)findViewById(R.id.img12);
        byte[] b= getIntent().getByteArrayExtra("1");
        final Bitmap img= BitmapFactory.decodeByteArray(b,0,b.length);
        img12.setImageBitmap(img);
        mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

        saveBtn=(Button)findViewById(R.id.saveBtn);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String fileName = String.valueOf(System.currentTimeMillis())+".png";

//파일의 이름이 같을경우 계속 중복생성이 되므로 System.currentTimeMillis를 사용해 파일명 중복을 피한다.

                File f = new File(Environment.getExternalStorageDirectory(), fileName);

                OutputStream out = null;

                try {

                    f.createNewFile();//파일생성

                    out = new FileOutputStream(f);

                    img.compress(Bitmap.CompressFormat.PNG, 100, out);

                    Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    scanIntent.setData(Uri.fromFile(f));

                    getApplicationContext().sendBroadcast(scanIntent);
                    Toast.makeText(getApplicationContext(),"저장완료",Toast.LENGTH_SHORT).show();
                    out.close();

                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),"저장오류",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();

                }

            }
        });
    }


}
