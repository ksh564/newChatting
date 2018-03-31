package org.androidtown.new_chatting.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.androidtown.new_chatting.Other.CircleTransform;
import org.androidtown.new_chatting.R;
import org.androidtown.new_chatting.photo_view;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

/**
 * Created by 김승훈 on 2017-07-06.
 */
public class Option_Fragment extends Fragment {

    private View Option_fragment;
    int frament_id;
    private TextView tvemail,tvnickname,tvmodyNick;
    private ImageView ivprofileImg;
    String str_profile,str_email,str_nick,new_uri,Uri_img;



    //파일업로드 변수

    int serverResponseCode = 0;
    ProgressDialog dialog = null;

    String upLoadServerUri = "http://115.71.233.69/user_info/uploadprofile.php";


    private Uri mImageCaptureUri;
    private String absolutePath;

//    /**********  File Path *************/
//    final String uploadFilePath = "storage/emulated/0/";//경로를 모르겠으면, 갤러리 어플리케이션 가서 메뉴->상세 정보
//    final String uploadFileName = "testimage.jpg"; //전송하고자하는 파일 이름


    private static final int PICK_FROM_CAMERA=0;
    private static final int PICK_FROM_ALBUM=1;
    private static final int CROM_FROM_IMAGE=2;
    private static final int PICK_FROM_ALBUM_FILTER=3;
    private static final int PICK_FROM_ALBUM_BlackFILTER=4;
    private static final int PICK_FROM_ALBUM_FaceFILTER=5;



    //opencv 관련 변수

    private Mat img_input;
    private Mat img_output;

    private static final String TAG = "opencv";



//    public static native long loadCascade(String cascadeFileName );
//    public static native void detect(long cascadeClassifier_face,
//                                     long cascadeClassifier_eye, long matAddrInput, long matAddrResult);
    public long cascadeClassifier_face = 0;
    public long cascadeClassifier_eye = 0;

    public native  void loadImage(String imageFileName, long img);
    public native void blackFilter(long inputImage, long outputImage);
    public native void whiteFilter(long inputImage, long outputImage);
    public native  void imageprocessing(long inputImage, long outputImage);
    public native  void bilateralFilter(long inputImage, long outputImage);
    static  {
        System.loadLibrary("opencv_java3");
        System.loadLibrary("native-lib");
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if(Option_fragment==null){

            Option_fragment = inflater.inflate(R.layout.fragment_setting,container,false);
            frament_id=getId();
        }
        initcontrols();
        dialog = new ProgressDialog(getActivity());
        str_profile=org.androidtown.new_chatting.LoginPackage.person.getId_img();
        str_email = org.androidtown.new_chatting.LoginPackage.person.getEmail();
        str_nick = org.androidtown.new_chatting.LoginPackage.person.getNick();


        tvemail.setText(str_email);
        tvnickname.setText(str_nick);
        tvmodyNick.setText(str_nick);
        Glide.with(this).load(str_profile)
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(getActivity()))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivprofileImg);



     ivprofileImg.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {


             MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                     .title(R.string.friend_add)
                     .items(R.array.option_addon)
                     .itemsCallback(new MaterialDialog.ListCallback() {
                         @Override
                         public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                             switch (position){
                                 case 0:
                                     doTakeAlbumAction();

                                     break;
                                 case 1:
                                     doTakeAlbumFilterAction();
                                     break;
                                 case 2:
                                     doTakeAlbumBlackFilterAction();
                                     break;
                                 case 3:
                                     doTakeAlbumFaceFilterAction();
                                     break;
                                 case 4:
                                     Intent intent = new Intent(getActivity(), photo_view.class);
                                     intent.putExtra("url_image",org.androidtown.new_chatting.LoginPackage.person.getId_img());
                                     startActivity(intent);
                                     break;
                             }
                         }
                     }).show();


         }
     });







        return Option_fragment;
    }
    public void doTakeAlbumFilterAction(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent,PICK_FROM_ALBUM_FILTER);
    }
    public void doTakeAlbumBlackFilterAction(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent,PICK_FROM_ALBUM_BlackFILTER);
    }
    public void doTakeAlbumFaceFilterAction(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent,PICK_FROM_ALBUM_FaceFILTER);
    }


    public void doTakeAlbumAction(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent,PICK_FROM_ALBUM);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode!=RESULT_OK)
            return;
        switch (requestCode){
            //앨범에서 그냥 이미지 업로드
            case PICK_FROM_ALBUM:
            {
                mImageCaptureUri=data.getData();
                System.out.println("앨범에서이미지"+mImageCaptureUri);
                new_uri = getPath(mImageCaptureUri);


                Glide.with(this).load(new_uri)
                        .crossFade()
                        .thumbnail(0.5f)
                        .bitmapTransform(new CircleTransform(getActivity()))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(ivprofileImg);

                new Thread(new Runnable() {
                    @Override
                    public void run() {


                        uploadFile(new_uri);
                    }
                }).start();

                break;
            }
            //캐니 필터로 업로드
            case PICK_FROM_ALBUM_FILTER: {
                mImageCaptureUri = data.getData();
                new_uri = getPath(mImageCaptureUri);


                img_input = new Mat();
                img_output = new Mat();
                loadImage(new_uri, img_input.getNativeObjAddr());
                System.out.println("오브젝트이미지?" + img_input.getNativeObjAddr());
                System.out.println("uri이미지??" + img_output.getNativeObjAddr());
                imageprocess_and_showResult();
                break;
            }
            //흑백필터로 업로드
            case  PICK_FROM_ALBUM_BlackFILTER: {

                mImageCaptureUri = data.getData();
                new_uri = getPath(mImageCaptureUri);


                img_input = new Mat();
                img_output = new Mat();
                loadImage(new_uri, img_input.getNativeObjAddr());
                blackFilter(img_input.getNativeObjAddr(), img_output.getNativeObjAddr());
                Bitmap bitmapOutput = Bitmap.createBitmap(img_output.cols(), img_output.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(img_output, bitmapOutput);
                //여기서 추가적으로 생성해서 저장하고 그다음에 파일 업로드 하면 될듯?
                saveBitmaptoJpeg(bitmapOutput);
                getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + Uri_img)));

        //파일 업로드 하는것
        new Thread(new Runnable() {
            @Override
            public void run() {


                uploadFile(Uri_img);
            }
        }).start();
                org.androidtown.new_chatting.LoginPackage.person.setId_img(Uri_img);
                Glide.with(this).load(Uri_img)
                        .crossFade()
                        .thumbnail(0.5f)
                        .bitmapTransform(new CircleTransform(getActivity()))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(ivprofileImg);
                break;
            }
            //사진으로만 업로드
            case PICK_FROM_ALBUM_FaceFILTER: {

                Bitmap bitmapOutput = Bitmap.createBitmap(img_output.cols(), img_output.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(img_output, bitmapOutput);
                //여기서 추가적으로 생성해서 저장하고 그다음에 파일 업로드 하면 될듯?
                saveBitmaptoJpeg(bitmapOutput);
                getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + Uri_img)));

        //파일 업로드 하는것
        new Thread(new Runnable() {
            @Override
            public void run() {


                uploadFile(Uri_img);
            }
        }).start();

                org.androidtown.new_chatting.LoginPackage.person.setId_img(Uri_img);
                Glide.with(this).load(Uri_img)
                        .crossFade()
                        .thumbnail(0.5f)
                        .bitmapTransform(new CircleTransform(getActivity()))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(ivprofileImg);
                break;


//                new_uri = getPath(mImageCaptureUri);
            }

        }
    }

    private void imageprocess_and_showResult() {

//        whiteFilter(img_input.getNativeObjAddr(), img_output.getNativeObjAddr());
        imageprocessing(img_input.getNativeObjAddr(), img_output.getNativeObjAddr());

//        Bitmap bitmapInput = Bitmap.createBitmap(img_input.cols(), img_input.rows(), Bitmap.Config.ARGB_8888);
//        Utils.matToBitmap(img_input, bitmapInput);
//        ivprofileImg.setImageBitmap(bitmapInput);

        Bitmap bitmapOutput = Bitmap.createBitmap(img_output.cols(), img_output.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(img_output, bitmapOutput);
        //여기서 추가적으로 생성해서 저장하고 그다음에 파일 업로드 하면 될듯?
        saveBitmaptoJpeg(bitmapOutput);
        getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,Uri.parse("file://"+Uri_img)));

        //파일 업로드 하는것
        new Thread(new Runnable() {
            @Override
            public void run() {


                uploadFile(Uri_img);
            }
        }).start();

        org.androidtown.new_chatting.LoginPackage.person.setId_img(Uri_img);
        Glide.with(this).load(Uri_img)
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(getActivity()))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivprofileImg);

//        ivprofileImg.setImageBitmap(bitmapOutput);
    }

    public void initcontrols(){
        tvemail = (TextView)Option_fragment.findViewById(R.id.Upper_email);
        tvnickname=(TextView)Option_fragment.findViewById(R.id.Upper_nick);
        tvmodyNick=(TextView)Option_fragment.findViewById(R.id.Bottom_nick);
        ivprofileImg=(ImageView)Option_fragment.findViewById(R.id.information_profile);
    }


//파일을 업로드 해주는 메소드
    public int uploadFile(String sourceFileUri) {

        String fileName = sourceFileUri;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        System.out.println("파일업로드전파일경로" + sourceFileUri);
        File sourceFile = new File(sourceFileUri);
        System.out.println("파일생성되었나" + sourceFile);
//
//
//        if (!sourceFile.isFile()) {
//
//            dialog.dismiss();
//
//            Log.e("uploadFile", "Source File not exist");
//
//            return 0;
//
//        }
//        else
//        {
            try {

                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + fileName + "\"" + lineEnd);

                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);

                String encoded_email = URLEncoder.encode(str_email, "UTF-8");

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition:form-data; name=\"id\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(encoded_email + lineEnd);





                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

                StringBuilder sb = new StringBuilder();
                String line = null;
                try {
                    while ((line = br.readLine()) != null) {
                        if (sb.length() > 0) {
                            sb.append("\n");
                        }
                        sb.append(line);

                        System.out.println("파일 전송 결과 : " + sb.toString());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(serverResponseCode == 200){

                    Log.i("FileUpload", "File Upload Complete");

//                            Toast.makeText(getActivity(), "File Upload Complete.",
//                                    Toast.LENGTH_SHORT).show();

                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                dialog.dismiss();
                ex.printStackTrace();


                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                dialog.dismiss();
                e.printStackTrace();
                Log.e("Exception e", "Got Exception : see logcat ");
//                Toast.makeText(getActivity(), "Got Exception : see logcat ",
//                                Toast.LENGTH_SHORT).show();

            }
            dialog.dismiss();
            return serverResponseCode;


    }

    private String getPath(Uri uri) {
        String[]  data = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(getActivity(), uri, data, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private  void copyFile(String filename){
        String baseDir = Environment.getExternalStorageDirectory().getPath();
        String pathDir = baseDir + File.separator + filename;

        AssetManager assetManager = getActivity().getAssets();

        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            Log.d( TAG, "copyFile :: 다음 경로로 파일복사 "+ pathDir);
            inputStream = assetManager.open(filename);
            outputStream = new FileOutputStream(pathDir);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            inputStream.close();
            inputStream = null;
            outputStream.flush();
            outputStream.close();
            outputStream = null;
        } catch (Exception e) {
            Log.d(TAG, "copyFile :: 파일 복사 중 예외 발생 "+e.toString() );
        }

    }

    private void read_image_file(){
    }

    public void saveBitmaptoJpeg(Bitmap bitmap){
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyyMMddHH:mm:ss");
        String formatDate = sdfNow.format(date);



        String download = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
//        String ex_storage =Environment.getExternalStorageDirectory().getAbsolutePath();
        // Get Absolute Path in External Sdcard
        String file_name = "/"+formatDate+".jpg";
        String string_path = download;

        System.out.println("임시파일경로"+string_path+file_name);
        Uri_img=string_path+file_name;

        File file_path;
        try{
            file_path = new File(string_path);
            System.out.println("임시파일경로2"+file_path);
            if(!file_path.isDirectory()){
                file_path.mkdirs();
            }
            FileOutputStream out = new FileOutputStream(string_path+file_name);
            System.out.println("임시파일경로3"+out);
            if((bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out))){

                System.out.println("임시파일경로4완료");
            }
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();


        }catch(FileNotFoundException exception){
            Log.e("FileNotFoundException", exception.getMessage());
        }catch(IOException exception){
            Log.e("IOException", exception.getMessage());
        }
    }






}
