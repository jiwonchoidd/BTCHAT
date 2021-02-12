package com.nsu.btchat;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;
public class HostActivity extends AppCompatActivity {
    //박민수의변수
    SoundPool soundPool;
    int soundID;
    Bitmap inputbit;
    private Button addBtn;
    private Button delBtn;
    private Button btnStart, btnEnd;

    private ListView myList;
    private ArrayList<Bitmap> list;

    private MyAdapter adapter = null;
    private boolean status = false;
    private ImageView ssss;
    private LinearLayout abc;
    private Button eeee;
    MyPaintView view;



    //블루투스 장치 식별 번호
    static final UUID BT_UUID= UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static final int REQ_ENABLE= 10;
    public static final int REQ_DISCOVERYABLE= 20;
    TextView tv;
    SocketThread mSocketThread = null; // 데이터 송수신 스레드
    BluetoothAdapter bluetoothAdapter;
    BluetoothServerSocket serverSocket;
    BluetoothSocket socket;
    LinearLayout chatting;
    private BackPressedForFinish backPressedForFinish;
    //이너클래스 참조 멤버 변수
    ServerThread serverThread;
    boolean accept=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);

        //뒤로가기 눌렀을때 종료되게
        backPressedForFinish = new BackPressedForFinish(this);

        soundPool = new SoundPool(5,AudioManager.STREAM_MUSIC,0);	//작성
        soundID = soundPool.load(this,R.raw.smw_kick,1);
        view = new MyPaintView(this);

        final LinearLayout pic = findViewById(R.id.pic);
        final Resources res = getResources();

        final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        pic.addView(view, params);
        abc=(LinearLayout)findViewById(R.id.abc);
       eeee=(Button)findViewById(R.id.eeee);
        ssss=(ImageView)findViewById(R.id.ssss);
        chatting=(LinearLayout)findViewById(R.id.chatgone);
        getSupportActionBar().setTitle("SERVER");
        tv=findViewById(R.id.tv);
        addBtn=(Button)findViewById(R.id.addBtn);
        delBtn=(Button)findViewById(R.id.delBtn);
        myList = (ListView) findViewById(R.id.myList);
        list = new ArrayList<Bitmap>();
        adapter = new MyAdapter(HostActivity.this, R.layout.list_form, list, myList);
        btnStart = (Button)findViewById(R.id.btn_start);
        btnEnd = (Button)findViewById(R.id.btn_end);
        final SeekBar seekBar=(SeekBar)findViewById(R.id.seekBar);
        addBtn.setOnClickListener(onClickListener);
        delBtn.setOnClickListener(onClickListener);
        final TextView colorName = (TextView) findViewById(R.id.colorName);
        final TextView result = (TextView) findViewById(R.id.result);
        myList.setAdapter(adapter);
        view.color=Color.BLACK;
        //시크바를 건드렸을때 색상을 변화시킨다.
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                TextView colorName = (TextView) findViewById(R.id.colorName);

                TextView result = (TextView) findViewById(R.id.result);
                result.setText(String.valueOf(progress));
                if(progress==1){
                    view.color=Color.BLUE;
                    colorName.setText("BLUE");
                }else if(progress==2){
                    view.color=Color.YELLOW;
                    colorName.setText("YELLOW");
                }else if(progress==3){
                    view.color=Color.GREEN;
                    colorName.setText("GREEN");
                }
                else{
                    view.color=Color.BLACK;
                    colorName.setText("BLACK");
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });


        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Service 시작",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(HostActivity.this,MyService.class);
                startService(intent);

            }
        });

        btnEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Service 끝",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(HostActivity.this,MyService.class);
                stopService(intent);
            }
        });

        //처음에 저장했던 이름을 nameData 변수에 저장!
        final SharedPreferences saveName = getSharedPreferences("saveName", MODE_PRIVATE);
        String nameData = saveName.getString("saveName", "");


        //블루투스 관리자 객체 소환하기
        bluetoothAdapter= BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter==null){
            Toast.makeText(this, "이 기기에는 블루투스가 없습니다.", Toast.LENGTH_SHORT).show();
            finish(); //이 순간 곧바로 종료되지 않음
            return;     //그래서 꼭 return 써주자.
        }
        //블루투스가 켜져있는지 확인
        if(bluetoothAdapter.isEnabled()){
            //서버소켓 생성 작업 실행
            createSetverSocket();

        }else{
            //블루투스장치 ON 선택 액티비티 보이기(단, 액티비티의 스타일이 다이얼로그로 되어 있음)
            Intent intent= new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent,REQ_ENABLE);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case REQ_ENABLE:
                if(resultCode==RESULT_CANCELED){
                    //Enable을 시키지 않았으므로 프로그램 종료
                    Toast.makeText(this, "블루투스를 허용하지 않았습니다.\n앱을 종료합니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    //서버 소켓 생성 및 실행
                    createSetverSocket();
                }
                break;
            case REQ_DISCOVERYABLE:
                if(resultCode==RESULT_CANCELED){
                    Toast.makeText(this, "블루투스 탐색을 허용하지 않았습니다.\n다른 장치에서 이 장치를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }// onActivityResult

    //서버소켓 생성 작업을 하는 메소드
    void createSetverSocket(){
        // 통신을 하기 위한 스레드 객체 생성
        serverThread= new ServerThread();
        serverThread.start();

        //상대방이 내 디바이스의 블루투스를
        //탐색하는 것을 허용하기
        allowDiscovery();
    }

    //탐색 허용 작업 메소드
    void allowDiscovery(){
        //탐색 허용 여부를 보여주는 다이얼로그 스타일의 액티비티를 실행
        Intent intent=new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        //기본으로 120초 동안 탐색을 허용하도록...
        //최대 300초까지 설정 가능함
        intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,300);// 300초
        startActivityForResult(intent, REQ_DISCOVERYABLE);

    }

    //inner class////
    class ServerThread extends Thread{
        @Override
        public void run() {
            try {
                //서버소켓 생성
                serverSocket=bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord("SERVER",BT_UUID);
                showText("서버 소켓 생성했습니다.\n클라이언트의 접속을 기다립니다.\n");

                //클라이언트의 접속 기다리기..
                socket=serverSocket.accept();//클라이언트가 접속할 때까지 대기
                showText("클라이언트가 접속했습니다.\n");
                accept=true;
               onConnected(socket);
                //dis & dos 를 이용해서 원하는 통신 작업 수행!
            } catch (IOException e) {e.printStackTrace();}
        }

        void showText(final String msg){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tv.append(msg);
                    if(accept==true)chatting.setVisibility(View.VISIBLE);
                    // 글씨 누적
                }
            });

        }

        // 클라이언트 소켓 중지
        public void cancel() {
            try {
                serverSocket.close();
            } catch (IOException e) {
                showMessage("Client Socket close error");
            }
        }
    }   // 메시지를 화면에 표시
    // 메시지를 화면에 표시
    public void showMessage(String strMsg) {
        // 메시지 텍스트를 핸들러에 전달
        Message msg = Message.obtain(mHandler, 0, strMsg);
        mHandler.sendMessage(msg);
        Log.d("tag1", strMsg);
    }

    // 메시지 화면 출력을 위한 핸들러
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                String strMsg = (String)msg.obj;
                tv.setText(strMsg);
            }
        }
    };

    // 데이터 송수신 스레드
    // 원격 디바이스와 접속되었으면 데이터 송수신 스레드를 시작
    public void onConnected(BluetoothSocket socket) {
        showMessage("Socket connected");

        // 데이터 송수신 스레드가 생성되어 있다면 삭제한다
        if( mSocketThread != null )
            mSocketThread = null;
        // 데이터 송수신 스레드를 시작
        mSocketThread = new HostActivity.SocketThread(socket);
        mSocketThread.start();
    }

    // 데이터 송수신 스레드
    private class SocketThread extends Thread {
        private final BluetoothSocket mmSocket; // 클라이언트 소켓
        private InputStream mmInStream; // 입력 스트림
        private OutputStream mmOutStream; // 출력 스트림

        public SocketThread(BluetoothSocket socket) {
            mmSocket = socket;

            // 입력 스트림과 출력 스트림을 구한다
            try {
                mmInStream = socket.getInputStream();
                mmOutStream = socket.getOutputStream();
                // 직렬화된 전송 방법

            } catch (IOException e) {
                showMessage("Get Stream error");
            }
        }

        // 소켓에서 수신된 데이터를 화면에 표시한다
        public void run() {

            while (true) {
                try {
                    // 입력 스트림에서 데이터를 읽는다
                    byte[] imagebuffer = null;
                    int size = 0;
                    byte[] buffer = new byte[10240];

                    int read;
                    while((read = mmInStream.read(buffer)) != -1 ) {
                        if (imagebuffer == null) {
                            //처음 4byte에서 비트맵이미지의 총크기를 추출해 따로 저장한다
                            byte[] sizebuffer = new byte[4];
                            System.arraycopy(buffer, 0, sizebuffer, 0, sizebuffer.length);
                            size = getInt(sizebuffer);
                            read -= sizebuffer.length;

                            //나머지는 이미지버퍼 배열에 저장한다
                            imagebuffer = new byte[read];
                            System.arraycopy(buffer, sizebuffer.length, imagebuffer, 0, read);
                        }
                        else {
                            //이미지버퍼 배열에 계속 이어서 저장한다
                            byte[] preimagebuffer = imagebuffer.clone();
                            imagebuffer = new byte[read + preimagebuffer.length];
                            System.arraycopy(preimagebuffer, 0, imagebuffer, 0, preimagebuffer.length);

                            System.arraycopy(buffer, 0, imagebuffer, imagebuffer.length - read, read);
                        }

                        //이미지버퍼 배열에 총크기만큼 다 받아졌다면 이미지를 저장하고 끝낸다
                        if(imagebuffer.length >= size) {
                            final byte[] finalImagebuffer = imagebuffer;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    list.add(byteArrayToBitmap(finalImagebuffer,0));
                                    view.invalidate();
                                    adapter.notifyDataSetChanged();
                                }
                            });
                            imagebuffer = null;
                            size = 0;

                        }

                    }

                    SystemClock.sleep(1);
                } catch (IOException e) {
                    showMessage("Socket disconneted");
                    break;
                }
            }
        }

        // 데이터를 소켓으로 전송한다
        public void write(Bitmap strBuf) {
            try {
                // 출력 스트림에 데이터를 저장한다
                list.add(strBuf);
                view.invalidate();
                adapter.notifyDataSetChanged();
                byte[] buffer = getImageByte(strBuf);
                //비트맵이미지의 총크기를 4byte배열에 담아서 먼저 보낸다 (이때 꼭 4byte일 필요는 없다. 마음내키는대로~)
                //총크기를 보내는 이유는 비트맵이미지가 전부 전송된 시점을 클라이언트에 알리기 위함이다
                byte[] size = getByte(buffer.length);
                mmOutStream.write(size, 0, size.length);
                mmOutStream.flush();
                mmOutStream.write(buffer, 0 , buffer.length);
                mmOutStream.flush();
            } catch (IOException e) {
                showMessage("Socket write error");
            }
        }
    }

    // 버튼 클릭 이벤트 함수

    Button.OnClickListener onClickListener = new Button.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.addBtn: {
                    // 데이터 송수신 스레드가 생성되지 않았다면 함수 탈출
                    soundPool.play(soundID,1f,1f,0,0,1f);
                    if (mSocketThread == null) return;
                    // 사용자가 입력한 텍스트를 소켓으로 전송한다
                    Bitmap s =savePicture();
                    //  if (s.getDensity() < 0) return;
                    tv.setText("버튼눌림");
                    view.clear();
                    mSocketThread.write(s);
                    break;

                }

                case R.id.delBtn:{
                    view.clear();
                    view.invalidate();
                    //바로지워지게
                    break;

                    }

                }
            }

    };
    // 앱이 종료될 때 디바이스 검색 중지
    public void onDestroy() {
        super.onDestroy();
        // 디바이스 검색 중지
        // 스레드를 종료
        if(serverThread!= null ) {
            serverThread.cancel();
            serverThread= null;
        }
        if( mSocketThread != null )
            mSocketThread = null;
    }

    private Bitmap savePicture() {

        // 1. 캐쉬(Cache)를 허용시킨다.

        // 2. 그림을 Bitmap 으로 저장.

        // 3. 캐쉬를 막는다.

        view.setDrawingCacheEnabled(true);    // 캐쉬허용
        // 캐쉬에서 가져온 비트맵을 복사해서 새로운 비트맵(스크린샷) 생성
        Bitmap screenshot = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);   // 캐쉬닫기
        return screenshot;
    }
    public byte[] getImageByte(Bitmap bitmap) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 25, out);
        return out.toByteArray();
    }
    private byte[] getByte(int num) {
        byte[] buf = new byte[4];
        buf[0] = (byte)( (num >>> 24) & 0xFF );
        buf[1] = (byte)( (num >>> 16) & 0xFF );
        buf[2] = (byte)( (num >>>  8) & 0xFF );
        buf[3] = (byte)( (num >>>  0) & 0xFF );

        return buf;
    }
    private int getInt(byte[] data) {
        int s1 = data[0] & 0xFF;
        int s2 = data[1] & 0xFF;
        int s3 = data[2] & 0xFF;
        int s4 = data[3] & 0xFF;

        return ((s1 << 24) + (s2 << 16) + (s3 << 8) + (s4 << 0));
    }

    public Bitmap byteArrayToBitmap(byte[] byteArray, int num){
        Bitmap bitmap = null;
        bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        byteArray=null;
        return  bitmap;
    }

    @Override public void onBackPressed() {
        backPressedForFinish.onBackPressed();
    }

}