package org.androidtown.new_chatting.LoginPackage;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.androidtown.new_chatting.R;


/**
 * Created by 김승훈 on 2017-07-05.
 */
public class signUp extends Activity implements View.OnClickListener{

    public int view_id = 0; //팝업창의 뷰 아이디를 받아주는 변수

    private EditText Email_Form,Nickname_Form,Name_Form,Pwd_Form,PwdConfirm_Form; //Edittext의 변수명
    private Button Sign_up_Btn;
    private org.androidtown.new_chatting.LoginPackage.sign_up_DB signUpDb;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        Email_Form = (EditText)findViewById(R.id.UserEmail);
        Nickname_Form=(EditText)findViewById(R.id.UserNickname);
        Name_Form=(EditText)findViewById(R.id.UserName);
        Pwd_Form=(EditText)findViewById(R.id.UserPassword);
        PwdConfirm_Form=(EditText)findViewById(R.id.UserPasswordAgain);
        Sign_up_Btn=(Button)findViewById(R.id.SU_SignupBtn);

        Email_Form.setOnClickListener(this);
        Nickname_Form.setOnClickListener(this);
        Name_Form.setOnClickListener(this);
        Pwd_Form.setOnClickListener(this);
        PwdConfirm_Form.setOnClickListener(this);
        Sign_up_Btn.setOnClickListener(this);

        signUpDb = new org.androidtown.new_chatting.LoginPackage.sign_up_DB(); // 클래스 초기화 시켜주는 부분

    }

    //각각의 뷰들을 클릭할 때 뜨는 팝업창 관리

    @Override
    public void onClick(View id) {

        //패스워드와 이메일 변수
        String pwd_var = Pwd_Form.getText().toString();
        String pwd_confirm_var = PwdConfirm_Form.getText().toString();
        String email_form_var = Email_Form.getText().toString();

        switch (id.getId()){


            //클릭 했을 때 뷰마다 아이디를 받고  그 아이디에 해당하는 팝업을 띄워준다.
            case R.id.UserEmail:
                System.out.println("눌리나요?");
                popup(R.id.UserEmail,"이메일 입력");
                break;

            case R.id.UserNickname:
                popup(R.id.UserNickname,"닉네임 입력");
                break;

            case R.id.UserName:
                popup(R.id.UserName,"이름 입력");
                break;

            case R.id.UserPassword:
                popup(R.id.UserPassword,"패스워드 입력");
                break;

            case R.id.UserPasswordAgain:
                popup(R.id.UserPasswordAgain,"패스워드 재입력");
                break;


            //입력폼에 예외적인 것이 발생했을때 예외처리
            case R.id.SU_SignupBtn:
            if(Email_Form.length()<1){
                Toast.makeText(getApplicationContext(),"이메일이 공백입니다!",Toast.LENGTH_SHORT).show();
                break;
            }
            if(Nickname_Form.length()<1){
                Toast.makeText(getApplicationContext(),"닉네임이 공백입니다!",Toast.LENGTH_SHORT).show();
                break;
            }

            if(Name_Form.length()<1){
                Toast.makeText(getApplicationContext(),"이름이 공백입니다!",Toast.LENGTH_SHORT).show();
                break;
            }

//            if(Idcheckvar<2){
//                Toast.makeText(getApplicationContext(),"사용하실 수 없는 아이디입니다!",Toast.LENGTH_SHORT).show();
//                break;
//            }
//            if(Nickchekvar<2){
//                Toast.makeText(getApplicationContext(),"사용하실 수 없는 닉네임입니다!",Toast.LENGTH_SHORT).show();
//                break;
//            }

            //이메일 형식 체크 하는 유효성 테스트
//            if(!Patterns.EMAIL_ADDRESS.matcher(email_form_var).matches()){
//                Toast.makeText(getApplicationContext(), "이메일 형식이 아닙니다", Toast.LENGTH_SHORT).show();
//                break;
//            }

            if(pwd_var.length()<1) {
                Toast.makeText(getApplicationContext(), "비밀번호가 공백입니다!", Toast.LENGTH_SHORT).show();

            }
            if(!pwd_var.equals(pwd_confirm_var)){
                Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않습니다",Toast.LENGTH_SHORT).show();
                break;
            }


            //비밀번호 유효성 체크 받는 부분

//            else if(!Pattern.matches("^(?=.*\\d)(?=.*[~`!@#$%\\^&*()-])(?=.*[a-zA-Z]).{8,20}$",pwd_var)) {
//                Toast.makeText(getApplicationContext(), "8~20자 영문 대 소문자,숫자,특수문자를 사용하세요.",Toast.LENGTH_LONG).show();
//
//            }
              else{
                String user_email = Email_Form.getText().toString();
                String user_nickname =Nickname_Form.getText().toString();
                String user_name = Name_Form.getText().toString();
                String user_password = Pwd_Form.getText().toString();

                signUpDb.User_Info_Array(user_email,user_nickname,user_name,user_password);
                Intent intent = new Intent();
                intent.putExtra("user_email ",user_email);
                setResult(RESULT_OK,intent);
                finish();
            }


            break;
        }



    }

    //회원가입 입력 양식 팝업창
    public void popup(final int id, String title){

        view_id = id;
        Context mcontext = getApplicationContext();
        LayoutInflater inflater = (LayoutInflater)mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);



        final AlertDialog.Builder aDialog = new AlertDialog.Builder(signUp.this);


        aDialog.setTitle(title);
        if(view_id==R.id.UserEmail){
            final View layout = inflater.inflate(R.layout.popup_signup,(ViewGroup)findViewById(R.id.poptest));
            aDialog.setView(layout);
        } else if(view_id==R.id.UserNickname){
            final View layout = inflater.inflate(R.layout.popup_signup,(ViewGroup)findViewById(R.id.poptest));
            aDialog.setView(layout);

        }else if(view_id==R.id.UserName){
            final View layout = inflater.inflate(R.layout.popup_signup,(ViewGroup)findViewById(R.id.poptest));
            aDialog.setView(layout);

        }else if(view_id==R.id.UserPassword){
            final View layout = inflater.inflate(R.layout.popup_password,(ViewGroup)findViewById(R.id.poptest));
            aDialog.setView(layout);

        } else if(view_id==R.id.UserPasswordAgain){
            final View layout = inflater.inflate(R.layout.popup_password,(ViewGroup)findViewById(R.id.poptest));
            aDialog.setView(layout);

        }




        aDialog.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        aDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if(view_id==R.id.UserEmail) {

                    EditText edit = (EditText) ((AlertDialog) dialog).findViewById(R.id.edittext);
                    String user = edit.getText().toString();

                  //  new IdCheck().execute(user);
//                    Log.e("log", "삽질번호"+Idcheckvar);
                    Email_Form.setText(user);

                }
                if(view_id==R.id.UserNickname){
                    EditText edit = (EditText) ((AlertDialog) dialog).findViewById(R.id.edittext);
                    String user = edit.getText().toString();
//                    new NicknameCheck().execute(user);
                    Nickname_Form.setText(user);

                }
                if(view_id==R.id.UserName){
                    EditText edit = (EditText) ((AlertDialog) dialog).findViewById(R.id.edittext);
                    String user = edit.getText().toString();
                    Name_Form.setText(user);
                }

                if(view_id==R.id.UserPassword){
                    EditText edit = (EditText) ((AlertDialog) dialog).findViewById(R.id.edittext);
                    Log.e("log", "패스워드에디트"+edit);

                    String user = edit.getText().toString();
                    Pwd_Form.setText(user);

                }

                if(view_id==R.id.UserPasswordAgain){
                    EditText edit = (EditText) ((AlertDialog) dialog).findViewById(R.id.edittext);
                    Log.e("log", "패스워드확인에디트"+edit);
//                    edit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD); 이메일 유효성 검사
                    String user = edit.getText().toString();
                    PwdConfirm_Form.setText(user);

                }




            }
        });


        AlertDialog ad = aDialog.create();
        ad.getWindow().setBackgroundDrawable(new ColorDrawable(0xba43e6b6));
        ad.show();

    }

}
