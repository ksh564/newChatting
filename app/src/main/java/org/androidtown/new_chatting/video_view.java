package org.androidtown.new_chatting;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.VideoView;

import org.androidtown.new_chatting.Network.SocketService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 김승훈 on 2017-08-30.
 */

public class video_view extends AppCompatActivity {


    static final int PERMISSION_REQUEST_CODE = 1;
    String[] PERMISSIONS = {"android.permission.READ_EXTERNAL_STORAGE","android.permission.WRITE_EXTERNAL_STORAGE"};


    private ProgressDialog progressBar;
    private File outputFile;
    SharedPreferences pref;

    VideoView videoView;
    String formatDate;

    SocketService socketService;
    boolean isService = false;
    String new_url,videoPath,load_to_video,intent_messageid,fileState;


    private boolean hasPermissions(String[] permissions) {
        int res = 0;
        //스트링 배열에 있는 퍼미션들의 허가 상태 여부 확인
        for (String perms : permissions){
            res = checkCallingOrSelfPermission(perms);
            if (!(res == PackageManager.PERMISSION_GRANTED)){
                //퍼미션 허가 안된 경우
                return false;
            }

        }
        //퍼미션이 허가된 경우
        return true;
    }

    private void requestNecessaryPermissions(String[] permissions) {
        //마시멜로( API 23 )이상에서 런타임 퍼미션(Runtime Permission) 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, PERMISSION_REQUEST_CODE);
        }
    }


    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            SocketService.LocalBinder myBinder = (SocketService.LocalBinder) service;

            socketService = myBinder.getService();
            System.out.println("서비스커넥트확인" + socketService);
            isService = true;



        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    protected void onResume() {
        Intent intent = new Intent(this, SocketService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
        super.onResume();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.video_view);
        super.onCreate(savedInstanceState);

        if (!hasPermissions(PERMISSIONS)) { //퍼미션 허가를 했었는지 여부를 확인
            requestNecessaryPermissions(PERMISSIONS);//퍼미션 허가안되어 있다면 사용자에게 요청
        } else {
            //이미 사용자에게 퍼미션 허가를 받음.
        }


        Bundle new_bundle = getIntent().getExtras();
        new_url = new_bundle.getString("url");
        intent_messageid = new_bundle.getString("message_id");



        initControl();

        //프로그레스바 세팅

        progressBar = new ProgressDialog(video_view.this);
        progressBar.setMessage("다운로드 중");
        progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressBar.setIndeterminate(true);
        progressBar.setCancelable(true);



        //shared preference 로딩하는 부분
        pref = getSharedPreferences(intent_messageid, MODE_PRIVATE);
        fileState = pref.getString("ing","");
        load_to_video = pref.getString("ing","");
        videoPath = pref.getString("video_uri","");


        // 미디어컨트롤러 추가하는 부분
//        videoView = (VideoView)findViewById(R.id.WatchVideo);
//        MediaController controller = new MediaController(video_view.this);
//        videoView.setMediaController(controller);

        // 비디오뷰 포커스를 요청함

//        videoView.requestFocus();
//
//        System.out.println("허용치"+videoPath);
//        if(fileState.equals("Done")){
//            Uri path_uri = Uri.parse(videoPath);
//            videoView.setVideoPath(videoPath);
//            videoView.start();
//        }





        //만약 파일이 다운로드가 완료가 되었으면 걸리는 조건문
        if(fileState.equals("Done")){
            // 저장한 비디오 경로를 불러주기
            videoPath = pref.getString("video_uri","");

            outputFile = new File(videoPath);

            ImageButton download = (ImageButton)findViewById(R.id.video_download);
            download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    long now = System.currentTimeMillis();
                    Date date = new Date(now);
                    SimpleDateFormat sdfNow = new SimpleDateFormat("yyyyMMddHH:mm:ss");
                    formatDate= sdfNow.format(date);
                    System.out.println("1:"+formatDate+"2:"+new_url);

                if(outputFile.exists()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(video_view.this);
                    builder.setTitle("파일 다운로드");
                    builder.setMessage("이미 파일이 존재합니다. 다시 다운로드 받을까요?");
                    builder.setNegativeButton("아니오",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    Toast.makeText(getApplicationContext(),"기존 파일을 플레이합니다.",Toast.LENGTH_LONG).show();
                                    playVideo(outputFile.getPath());
                                }
                            });
                    builder.setPositiveButton("예",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    outputFile.delete(); //파일 삭제

                                    //여기에 다운로드 받는 AsyncTask 넣어줘야함

                                    final Download downloadTask = new Download(video_view.this,formatDate,new_url);
                                    downloadTask.execute(fileState);

                                    progressBar.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                        @Override
                                        public void onCancel(DialogInterface dialog) {
                                            // 다운로드 캔슬 될때때
                                            downloadTask.cancel(true);
                                        }
                                    });
                                }
                            });
                    builder.show();
                } else{ // 새로  다운받는 경우


                        final Download downloadTask = new Download(video_view.this,formatDate,new_url);
                        downloadTask.execute(fileState);

                        progressBar.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                // 다운로드 캔슬 될때때
                                downloadTask.cancel(true);
                            }
                        });
                    }


//                socketService.callupdownload(formatDate,new_url);
                }
            });
            // 파일 다운로드 완료된 상태 끝
        }
        // 파일 다운로드 ing일 경우
        else if(fileState.equals("Ing")){
            videoPath = pref.getString("video_uri","");
            ImageButton download = (ImageButton)findViewById(R.id.video_download);
            download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    long now = System.currentTimeMillis();
                    Date date = new Date(now);
                    SimpleDateFormat sdfNow = new SimpleDateFormat("yyyyMMddHH:mm:ss");
                    formatDate= sdfNow.format(date);
                    System.out.println("1:"+formatDate+"2:"+new_url);
                    // 다운로드 시작
                    final Download downloadTask = new Download(video_view.this,formatDate,new_url);
                    downloadTask.execute(fileState);

                    progressBar.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            // 다운로드 캔슬 될때때
                            downloadTask.cancel(true);
                        }
                    });
                }
            });




        // 파일을 전혀 다운로드 하지 않았을 경우
        } else if(fileState.equals("")){
            ImageButton download = (ImageButton)findViewById(R.id.video_download);
            download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    long now = System.currentTimeMillis();
                    Date date = new Date(now);
                    SimpleDateFormat sdfNow = new SimpleDateFormat("yyyyMMddHH:mm:ss");
                    formatDate= sdfNow.format(date);
                    System.out.println("1:"+formatDate+"2:"+new_url);
                    // 다운로드 시작
                    final Download downloadTask = new Download(video_view.this,formatDate,new_url);
                    downloadTask.execute(fileState);

                    progressBar.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            // 다운로드 캔슬 될때때
                            downloadTask.cancel(true);
                        }
                    });
                }
            });

        }


        // 바로 시작하는 메소드
        if(load_to_video.equals("Done")){
            videoPath = pref.getString("video_uri","");
            playVideo(videoPath);

        }


    }


   @Override
    protected void onStop() {
        unbindService(conn);
        super.onStop();
    }

    public void initControl(){

    }

    private void playVideo(String path) {
        Uri videoUri = Uri.fromFile(new File(path));
        Intent videoIntent = new Intent(Intent.ACTION_VIEW);
        videoIntent.setDataAndType(videoUri, "video/*");
        if (videoIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(Intent.createChooser(videoIntent, null));
        }
    }


    public class Download extends AsyncTask<String,String,Long> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;

        String uri,Filename;
        Download(Context context,String Filename,String Uri){
            this.context = context;
            this.Filename=Filename;
            this.uri = Uri;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //사용자가 다운로드 중 파워 버튼을 누르더라도 CPU가 잠들지 않도록 해서
            //다시 파워버튼 누르면 그동안 다운로드가 진행되고 있게 됩니다.
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
            mWakeLock.acquire();

            progressBar.show();


        }

        @Override
        protected Long doInBackground(String... state) {


            // 여기까지 작업했다
            RandomAccessFile output = null;


            int DOWNLOAD_DONE = 0;
            int DEFAULT_TIMEOUT = 30000;
            long FileSize = -1;

            //이어받기용

            long wholeFileSize,remains, lenghtOfFile = 0;

            StringBuilder html = new StringBuilder();

            // 다운로드 완료거나 처음이거나
            if(state[0].equals("Done") || state[0].equals("")){
                try {

                    pref = getSharedPreferences(intent_messageid, MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    // 이어받기 중일때 저장
                    editor.putString("ing","Ing");

                    editor.commit();
                    URL url_ = new URL(uri);
                    HttpURLConnection conn = (HttpURLConnection)url_.openConnection();

                    conn.setConnectTimeout(10000);
                    conn.setUseCaches(false);
                    FileSize = conn.getContentLength();

                    if(conn.getResponseCode()==HttpURLConnection.HTTP_OK){


                        String download = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
                        String file_name = "/"+Filename+".mp4";
                        outputFile = new File(download+file_name);
                        editor.putString("video_uri",outputFile.getPath());

                        editor.commit();

                        // 파일이 존재하지 않으면 새로운 파일을 만든다
                        if (outputFile.exists()==false){
                            outputFile.createNewFile();
                        }



                        FileOutputStream fileOutPut = new FileOutputStream(outputFile);
                        InputStream inputStream = conn.getInputStream();

                        long downloadedSize = 0;
                        byte[] buffer = new byte[1024];
                        int bufferLength = 0;

                        while ((bufferLength=inputStream.read(buffer))>0){
                            //사용자가 BACK 버튼 누르면 취소
                            if(isCancelled()){
                                inputStream.close();
                                return  Long.valueOf(-1);
                            }

                            downloadedSize += bufferLength;



                            if(FileSize>0){
                                float per = ((float)downloadedSize/FileSize)*100;
                                String str = "Downloaded"+" " + downloadedSize + "KB / " + FileSize + "KB (" + (int)per + "%)";
                                publishProgress("" + (int) ((downloadedSize*100) / FileSize),str);
                            }

//                            Thread.sleep(50);
                            fileOutPut.write(buffer,0,bufferLength); // 파일에 데이터를 기록록
                        }
                        fileOutPut.flush();

                        fileOutPut.close();
                    }
                    conn.disconnect();


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
            // 이어받기 로직
            else if(state[0].equals("Ing")){

                pref = getSharedPreferences(intent_messageid, MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                System.out.println("갑자기꽂혀서"+videoPath);
                // 이어받기 중일때 저장
                editor.putString("ing","Ing");
                editor.commit();

                try {


                    String download = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
                    String file_name = "/"+Filename+".mp4";
                    outputFile = new File(videoPath);
                    output = new RandomAccessFile(outputFile.getAbsolutePath(), "rw");
                    // 전체 파일사이즈 찾기
                    wholeFileSize = output.length();
                    System.out.println("갑자기꽂혀서2"+wholeFileSize);
                    // 파일의 조각찾는듯?
                    output.seek(wholeFileSize);


                    URL url_ = new URL(uri);
                    HttpURLConnection conn = (HttpURLConnection)url_.openConnection();
                    conn.setRequestProperty("Range", "bytes=" + String.valueOf(wholeFileSize) + '-');
                    conn.connect();
                    conn.setConnectTimeout(DEFAULT_TIMEOUT);
                    conn.setReadTimeout(DEFAULT_TIMEOUT);
                    remains = conn.getContentLength();
                    FileSize = remains + wholeFileSize;



                    if ((remains <= DOWNLOAD_DONE) || (remains == wholeFileSize)) {
                        return null;
                    }
                    InputStream input = conn.getInputStream();

                    long downloadedSize = 0;
                    byte data[] = new byte[1024];
                    int count = 0;
                    int bufferLength = 0;

                    while ((bufferLength=input.read(data))>0){
                        //사용자가 BACK 버튼 누르면 취소
                        if(isCancelled()){
                            input.close();
                            return  Long.valueOf(-1);
                        }

                        downloadedSize += bufferLength;



                        if(FileSize>0){
                            float per = ((float)(downloadedSize+wholeFileSize)/FileSize)*100;
                            String str = "Downloaded   " + (int)(downloadedSize+wholeFileSize) + "Byte / " + FileSize + "Byte (" + (int)per + "%)";
                            publishProgress("" + (int) (((downloadedSize+wholeFileSize)*100) / FileSize),str);
                        }


                        Thread.sleep(30);
                        output.write(data,0,bufferLength); // 파일에 데이터를 기록록
                    }
                    output.close();
                    input.close();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }

            return FileSize;
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            super.onProgressUpdate(progress);

            progressBar.setIndeterminate(false);
            progressBar.setMax(100);
            progressBar.setProgress(Integer.parseInt(progress[0]));
            progressBar.setMessage(progress[1]);
        }

        @Override
        protected void onPostExecute(Long size) {
            super.onPostExecute(size);

            progressBar.dismiss();

            if(size > 0){
                Toast.makeText(getApplicationContext(), "다운로드 완료되었습니다. 파일 크기=" +size.toString(), Toast.LENGTH_LONG).show();
                Intent mediaScanIntent = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(Uri.fromFile(outputFile));
                sendBroadcast(mediaScanIntent);


                //쉐어드로 비디오 받으면 경로를 저장한다.
                pref = getSharedPreferences(intent_messageid, MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();

                editor.putString("ing","Done");
                editor.putString("video_uri",outputFile.getPath());

                editor.commit();



                playVideo(outputFile.getPath());


            }else{
                Toast.makeText(getApplicationContext(), "다운로드 에러", Toast.LENGTH_LONG).show();
            }
        }




    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(permsRequestCode){

            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean readAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                        if ( !readAccepted || !writeAccepted  )
                        {
                            showDialogforPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");
                            return;
                        }
                    }
                }
                break;
        }
    }
    private void showDialogforPermission(String msg) {

        final AlertDialog.Builder myDialog = new AlertDialog.Builder(video_view.this);
        myDialog.setTitle("알림");
        myDialog.setMessage(msg);
        myDialog.setCancelable(false);
        myDialog.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(PERMISSIONS, PERMISSION_REQUEST_CODE);
                }

            }
        });
        myDialog.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });
        myDialog.show();
    }
}
