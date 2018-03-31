package org.androidtown.new_chatting;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.androidtown.new_chatting.Fragment.Chat_Fragment;
import org.androidtown.new_chatting.Fragment.Friend_Fragment;
import org.androidtown.new_chatting.Fragment.Option_Fragment;
import org.androidtown.new_chatting.LoginPackage.person;
import org.androidtown.new_chatting.LoginPackage.signIn;
import org.androidtown.new_chatting.Network.SocketService;
import org.androidtown.new_chatting.Other.ServiceMonitor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

/**
 * Created by 김승훈 on 2017-08-17.
 */

public class MainActivity extends AppCompatActivity implements Friend_Fragment.onSomeEventListner {

    private ServiceMonitor serviceMonitor = ServiceMonitor.getInstance();

    final int MY_PERMISSION_REQUEST_STORAGE = 0;

    public SocketService socketService;
    boolean isService = false;

    // 네트워크를 위한 자원 변수
    private Handler mHandler;
    private String ip ="115.71.233.69";
    private int port=9999;
    private String User_Email;
    private Socket socket;
    private InputStream is;
    private OutputStream os;
    private DataInputStream dis;
    private DataOutputStream dos;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            checkPermission();
        }
        setContentView(R.layout.activity_main);
        //퍼미션체크








        person person = new person();

        //로그인한 유저의 이메일 정보를 가져오는 부분
        User_Email= org.androidtown.new_chatting.LoginPackage.person.getEmail();









        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager)findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        if (serviceMonitor.isMonitoring() == false) {
            serviceMonitor.startMonitoring(getApplicationContext());
        }



        //플로팅 버튼입니다.

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                    Intent intent = new Intent(MainActivity.this,Add_Friend.class);
//                    startActivity(intent);
////
////                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
////                        .setAction("Action", null).show();
//            }
//        });

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        //서비스 시작 부분 인텐트
        SharedPreferences pref = getSharedPreferences("idbundle",MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("check",true);
        editor.commit();
        System.out.println("서비스 시작");
        Intent intent = new Intent(getApplicationContext(), SocketService.class);
        startService(intent);
        bindService(intent,conn, Context.BIND_AUTO_CREATE);
        System.out.println("MainActivity_onResume");
        super.onResume();

    }

    @Override
    protected void onPause() {
        System.out.println("MainActivity_onPause");

        super.onPause();
    }

    @Override
    protected void onStop() {

        System.out.println("MainActivity_onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.e(getClass().getName(),"onDestroy");

        unbindService(conn);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        // 로그아웃 버튼을 눌렀을때 작동하게 됨
        if (id == R.id.action_settings) {

            socketService.send_message2("Disconnect||"+User_Email);
            serviceMonitor.stopMonitoring(getApplicationContext());
            SharedPreferences.Editor prefs = getSharedPreferences("idbundle", MODE_PRIVATE).edit();
            prefs.remove("id");
            prefs.putBoolean("check",false);
            System.out.println("저장된 객체 삭제");
            prefs.commit();
            socketService.DisConnectSocket();
            socketService.DisConnectService();
            unbindService(conn);

            Intent stopintent = new Intent(getApplicationContext(), SocketService.class);
            stopService(stopintent);
            Intent intent = new Intent(MainActivity.this, signIn.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //서비스와 연결하는 메서드
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            SocketService.LocalBinder myBinder = (SocketService.LocalBinder) service;
            //왜 이부분이 public method로 호출되어야 할지?
            socketService = myBinder.getService();
            isService = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

            isService=false;
        }
    };

    @Override
    public void someEvent(String Msg) {

        StringTokenizer st = new StringTokenizer(Msg,"/");


        String Room_NAME=st.nextToken();
        String message = st.nextToken();

        socketService.send_message2("Invite_one/"+Room_NAME+"/"+message);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            if(position==0){
                return new Friend_Fragment();
            }else if(position==1)
            {
                return  new Chat_Fragment();
            }else
                return  new Option_Fragment();

//            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "친구";
                case 1:
                    return "대화";
                case 2:
                    return "옵션";
            }
            return null;
        }
    }

    //소켓통신을 위한 쓰레드
    class BackThread extends Thread{
        @Override
        public void run() {
            NetWork();
        }
    }


    private void  NetWork(){



        try {
            socket = new Socket(ip,port);

            if(socket!=null)
            {
                Connection(User_Email);
            }

        } catch (UnknownHostException e){
            System.out.println("소켓 연결 실패");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void Connection(String Email) //실제적인 메소드 연결 부분
    {
        this.User_Email=Email;

        try{
            is = socket.getInputStream();
            dis = new DataInputStream(is);

            os = socket.getOutputStream();
            dos = new DataOutputStream(os);
        }
        catch(IOException e) //에러처리 부분
        {

        } 	//stream 설정 끝

        // 처음 접속시에 ID 전송(이메일)
        send_message(Email);

        //서버로부터 계속 수신하는 쓰레드
        Thread th = new Thread(new Runnable() {

            @Override
            public void run() {

                while(true)
                {

                    try {
                        String msg = dis.readUTF(); // 메세지 수신

                        System.out.println("서버로부터 수신한 메세지"+msg);


                    } catch (IOException e) {

                    }
                }

            }
        });

        th.start();

    }


    private void send_message(String str) // 서버에게 메세지를 보내는 부분

    {
        try {
            dos.writeUTF(str);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermission() {



        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "Read/Write external Storage", Toast.LENGTH_SHORT).show();
            }
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST_STORAGE);
        }
        else{

        }
    }
    // 권한설정 결과값 받기
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    //권한 허용시

                } else {
                    Toast.makeText(MainActivity.this, "권한사용을 동의해주셔야 이용이 가능합니다.", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
        }
    }





}
