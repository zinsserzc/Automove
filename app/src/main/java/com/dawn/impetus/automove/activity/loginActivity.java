package com.dawn.impetus.automove.activity;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dawn.impetus.automove.R;
import com.dawn.impetus.automove.utils.SPUtil;



public class loginActivity extends AppCompatActivity {

    private EditText userName,passWord;
    private Button loginBtn;

    private String userString,pswString;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去掉标题
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_login);
        init();
        setListener();


    }
    //初始化
    private void init(){

        loginBtn = (Button)findViewById(R.id.login_btnLogin);
        userName=(EditText)findViewById(R.id.login_edtId);
        passWord=(EditText)findViewById(R.id.login_edtPwd);

    }

    //设置监听
    private void  setListener(){
       setEditorChangeListener();
        setLoginListener();

    }


//    //设置文本框变化监听
    private void setEditorChangeListener(){
        //用户名框
        userName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                userString=charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //登录密码框

        passWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                pswString=charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    //设置登录监听
    private void setLoginListener(){

        //按钮点击事件

        loginBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public  void  onClick(View v)
            {
                //登录中ui


                if(userString==null||userString.equals(""))
                {
                    Toast.makeText(loginActivity.this,"请输入账号",Toast.LENGTH_SHORT).show();

                }else if(pswString==null||pswString.equals(""))
                {
                    Toast.makeText(loginActivity.this,"请输入密码",Toast.LENGTH_SHORT).show();
                }else{
                    saveUser();
                    //if(登录成功)
                    //{
                    // saveUser();
                    //跳转
                    // }
                    //else{
                    // 登录失败toast
                    //
                    // }

                }

            }


        });
    }

    private void saveUser(){
        //存入用户名和密码
        userString = userName.getText().toString();
        pswString  = passWord.getText().toString();
        SPUtil.put(getApplicationContext(),"userName",userString);
        SPUtil.put(getApplicationContext(),"passWord",pswString);

    }

    @Override
    protected void onStart(){
        super.onStart();

        //设置初始用户名
        userString=SPUtil.get(getApplicationContext(),"userName","").toString();
        if(userString!=null||!userName.equals(""))
        userName.setText(userString);
    }
}
