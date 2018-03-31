package org.androidtown.new_chatting.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.afollestad.materialdialogs.MaterialDialog;

import org.androidtown.new_chatting.Adapter.NameBean;
import org.androidtown.new_chatting.Adapter.list_friend_adp;
import org.androidtown.new_chatting.Adapter.multiple_choice_adp;
import org.androidtown.new_chatting.Chat_Activity;
import org.androidtown.new_chatting.Friend.Add_Friend;
import org.androidtown.new_chatting.LoginPackage.LoginDTO;
import org.androidtown.new_chatting.MainActivity;
import org.androidtown.new_chatting.Network.SocketService;
import org.androidtown.new_chatting.R;
import org.androidtown.new_chatting.photo_view;
import org.androidtown.new_chatting.webRTC.ConnectActivity;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * Created by 김승훈 on 2017-07-06.
 */
public class Friend_Fragment extends Fragment {




    // 인텐트를 위한 변수
    static  final int REQUEST_CODE = 1234;

    private View Frend_fragment_view;
    private String photo_url;
    private ListView Friend_List_View;
    list_friend_adp list_friend_adp;
    multiple_choice_adp multi_choice_friend_adp;

    //http 통신을 위한 변수
    private final String UrlPath = "http://115.71.233.69/friend/friend_list.php";
    String results,Friend_Email,Friend_Nick,Friend_Profile,friend_confirm,Chat_Email,Chat_NickName;
    String Chat_UUID;
    public final ArrayList<LoginDTO> find_item = new ArrayList<>();
    public final ArrayList<LoginDTO> new_find_item = new ArrayList<>();
    public final ArrayList<NameBean> namebeanarray = new ArrayList<>();

    //소켓통신을 위한 변수

    SocketService socketService;
    boolean isService = false;


    private DataOutputStream dos;
    String My_Room;
    private static ArrayList<String> Room_user_list;


    // 로그인 변수
    String User_Nick,JSON_User_list,User_Email,User_Profile;
    int frament_id;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //프래그먼트 뷰가 세팅이 안되어있으면 인플레이트 해줌
        if(Frend_fragment_view==null){

            Frend_fragment_view = inflater.inflate(R.layout.friend_fragment,container,false);
            frament_id=getId();
        }

        //서비스 바인딩
        Intent intent = new Intent(getActivity(),SocketService.class);

        //getContext가 프래그먼트에 속해있는 context고 이걸 MainActivity를 캐스팅한다.
        //

        System.out.println("프랜드프래그먼트서비스"+ socketService);


        // 로그인 정보를 얻어오는 부분

        User_Email = org.androidtown.new_chatting.LoginPackage.person.getEmail();
        User_Nick = org.androidtown.new_chatting.LoginPackage.person.getNick();
        User_Profile = org.androidtown.new_chatting.LoginPackage.person.getId_img();
        Chat_UUID = UUID.randomUUID().toString();

        // http통신을 통해 친구목록 가져오는 쓰레드 실행
        try {
            new Find_information(User_Email).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        Friend_List_View = (ListView)Frend_fragment_view.findViewById(R.id.friend_fragment_listview);



        //리스트뷰 어댑터
        list_friend_adp = new list_friend_adp(find_item);
        multi_choice_friend_adp = new multiple_choice_adp(new_find_item);


        org.androidtown.new_chatting.Other.static_adp.setList_friend_adp(list_friend_adp);
        org.androidtown.new_chatting.Other.static_adp.setMultiple_choice_adp(multi_choice_friend_adp);

        Friend_List_View.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int pos, long id) {


                MaterialDialog dialog =new MaterialDialog.Builder(getActivity())
                        .title(R.string.friend_add)
                        .items(R.array.friend_addon)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {

                               // 영상통화인 경우
                                if (position == 0) {
                                    socketService = ((MainActivity)getContext()).socketService;
                                    Chat_Email=find_item.get(pos).get_email();
                                    String uuid = UUID.randomUUID().toString();
                                    System.out.println("프렌드프래그먼트"+"imageCall||"+uuid+"||"+Chat_Email+"||"+User_Nick+"||"+User_Profile);
                                    System.out.println("프렌드프래그먼트2"+socketService);

                                    socketService.send_message2("imageCall||"+uuid+"||"+Chat_Email+"||"+User_Nick+"||"+User_Profile);
                                    Intent send_imagecall = new Intent(getActivity(), ConnectActivity.class);
                                    send_imagecall.putExtra("send_uuid",uuid);
                                    send_imagecall.putExtra("from_fragment",true);
                                    startActivity(send_imagecall);

                                    // 대화일대
                                } else if (position == 1) {

                                    Chat_Email=find_item.get(pos).get_email();
                                    Chat_NickName =find_item.get(pos).get_nickname();
                                    Intent intent = new Intent(getActivity(),Chat_Activity.class);
                                    Room_user_list = new ArrayList<String>();
                                    Room_user_list.add(User_Email);
                                    Room_user_list.add(Chat_Email);

                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("result",Room_user_list);
                                    JSON_User_list = jsonObject.toString();
                                    intent.putExtra("User_List_Json",JSON_User_list);

                                    // 방이름을 닉네임으로 받을때 통일을 시켜주기 위해 정렬을 시켜주는 메소드
                                    String[] nick_arr = new String[2];
                                    nick_arr[0] = User_Nick.trim();
                                    nick_arr[1] = Chat_NickName.trim();
                                    Arrays.sort(nick_arr);
                                    StringBuilder stringBuilder = new StringBuilder(nick_arr[0]);
                                    for(int i=0; i<nick_arr.length-1; i++){
                                        stringBuilder.append("_"+nick_arr[i+1]);
                                    }
                                    //정렬 메소드 끝
                                    intent.putExtra("Room_name",stringBuilder.toString().trim());
                                    intent.putExtra("isRoom",false);
                                    startActivity(intent);
                                }else if(position==2){
                                    photo_url=find_item.get(pos).getImgurl();
                                    Intent intent = new Intent(getActivity(), photo_view.class);
                                    intent.putExtra("url_image",photo_url);
                                    startActivity(intent);
                                }
                            }
                        }).show();



            }
        });

        //친구추가 할 때 들어가는 버튼튼

        FloatingActionButton fab = (FloatingActionButton)Frend_fragment_view.findViewById(R.id.Friend_FAB);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),Add_Friend.class);
                startActivityForResult(intent,REQUEST_CODE);

            }
        });




        return Frend_fragment_view;
    }
    public interface  onSomeEventListner {
        public void someEvent(String Msg);
    }
    onSomeEventListner someEventListner;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        someEventListner = (onSomeEventListner)activity;
    }

//    ServiceConnection conn = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            SocketService.LocalBinder myBinder = (SocketService.LocalBinder)service;
//
//            socketService = myBinder.getService();
//            System.out.println("프렌드프래그먼트"+socketService);
//            isService=true;
//
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName componentName) {
//            isService=false;
//        }
//    };


    private void send_message(String str) // 서버에게 메세지를 보내는 부분

    {
        try {
            dos.writeUTF(str);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    class Invite_Protocol extends Thread{
        @Override
        public void run() {
            My_Room = UUID.randomUUID().toString();
            send_message("Invite_one/"+My_Room+"/"+JSON_User_list); // 채팅메세지 프로토콜
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_CODE:
                if (resultCode==getActivity().RESULT_OK){

                    //메인엑티비티 리프레쉬
                    getActivity().recreate();


                //프래그먼트만 리프레쉬 한다고 삽질한 부분
//                    Fragment frg = null;
//                    frg = getFragmentManager().findFragmentById(frament_id);
//
//                    final FragmentTransaction ft = getFragmentManager().beginTransaction();
//                    ft.replace(R.id.container,frg);
//                    ft.commit();
//                    System.out.println("프래그먼트확인"+frg);
//                    ft.detach(frg);
//                    ft.attach(frg);
//                    ft.commit();

                }

        }
    }

    class Find_information extends AsyncTask<Void,Void,String> {
        String email;

        Find_information(String email){
            this.email=email;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {

            try {
                URL url = new URL(UrlPath);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setDoInput(true);
                con.setDoOutput(true);
                con.setUseCaches(false);
                con.setRequestMethod("POST");

                //Email을 POST형식으로 보내주기 전에 UTF-8로 인코딩하고 PARAM을 만들어서 보내는 과정
                String param = URLEncoder.encode("Email", "UTF-8") + "=" +  URLEncoder.encode(email, "UTF-8");

                OutputStream outputStream = con.getOutputStream();
                outputStream.write(param.getBytes());
                outputStream.flush();
                outputStream.close();

                BufferedReader rd = null;
                rd = new BufferedReader(new InputStreamReader
                        (con.getInputStream(), "UTF-8"));
                String line = null;
                while ((line = rd.readLine()) != null) {
                    Log.d("회원정보찾기", line);
                    results=line;
                }



            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return results;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            String jsonPage = result;
            ArrayList<LoginDTO> loginDTOs = new ArrayList<>();

            JSONParser jsonParser = new JSONParser();
            try {

                // 결과값으로 온 스트링을 jsonobject로 파스
                JSONObject jsonObject = (JSONObject)jsonParser.parse(jsonPage);
                // jsonobject를 jsonarray로
                org.json.simple.JSONArray friend_array = (org.json.simple.JSONArray) jsonObject.get("dbresult");
                System.out.println("friend_array_확인"+friend_array);

                String Email,NickName,Profile,Confirm,Checker;
                int Check;


                for(int i =0; i< friend_array.size(); i++){
                    JSONObject obj = (JSONObject)friend_array.get(i);

                    Email = (String)obj.get("Email");
                    NickName=(String)obj.get("Nickname");
                    Profile=(String)obj.get("profile_img");
                    Confirm=(String)obj.get("result");

                    if(Profile!=null){
                        Profile.replaceAll("\\/","/");
                    }
                    System.out.println("형변환후"+Profile);

                    LoginDTO entity = new LoginDTO();

                    entity.set_email(Email);
                    entity.set_nickname(NickName);
                    System.out.println("형변환전"+Profile);
                    entity.setImgurl(Profile);
                    entity.set_confirm(Confirm);


                    loginDTOs.add(entity);

                }




            } catch (ParseException e) {
                e.printStackTrace();
            }




            Friend_List_View.setAdapter(list_friend_adp);

            // 만약 어댑터가 비워지지 않았으면
            if(list_friend_adp.isEmpty()==false){
                find_item.clear();
                list_friend_adp.notifyDataSetChanged();
            }




            for(LoginDTO entity : loginDTOs){

                Friend_Email = entity.get_email();
                Friend_Nick = entity.get_nickname();
                Friend_Profile= entity.getImgurl();
                friend_confirm=entity.get_confirm();

                if(friend_confirm.equals("true")){
                    multi_choice_friend_adp.additem(Friend_Nick,Friend_Email,Friend_Profile);
                    list_friend_adp.additem(Friend_Email,Friend_Nick,Friend_Profile);
                    multi_choice_friend_adp.notifyDataSetChanged();
                    list_friend_adp.notifyDataSetChanged();

                }else if(friend_confirm.equals("false")){
                    System.out.println("해당 회원이 존재하지 않습니다.");
                }

            }






        }
    }

    @Override
    public void onResume() {
        Intent intent = new Intent(getActivity(), SocketService.class);

        Log.e(getClass().getName(), "onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.e(getClass().getName(), "onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.e(getClass().getName(), "onStop");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.e(getClass().getName(), "onDestroy");
        super.onDestroy();
    }
}
