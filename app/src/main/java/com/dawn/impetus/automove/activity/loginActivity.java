package com.dawn.impetus.automove.activity;


import android.content.Intent;
import android.os.Handler;
import android.os.Message;
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
import com.dawn.impetus.automove.utils.SSHUtil;
import com.dawn.impetus.automove.utils.ServerUtil;


/**
 * 登录activity
 */
public class LoginActivity extends AppCompatActivity {


    public  static final String TAG = LoginActivity.class.getName();

    private EditText userEdit, pswEdit;
    private Button loginBtn;
    private String userName, passWord;
    private Handler loginHandler;
    private ServerUtil svUtil;



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

    /**
     * 初始化
     */
    private void init() {

        loginBtn = (Button) findViewById(R.id.login_btnLogin);
        userEdit = (EditText) findViewById(R.id.login_edtId);
        pswEdit = (EditText) findViewById(R.id.login_edtPwd);

    }

    /**
     * 设置监听
     */
    private void setListener() {
        setEditorChangeListener();
        setLoginListener();

    }


    /**
     * 设置文本框变化监听
     */
    private void setEditorChangeListener() {
        //用户名框
        userEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                userName = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //登录密码框

        pswEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                passWord = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    /**
     * 设置登录按钮监听
     */
    private void setLoginListener() {

        //按钮点击事件

        loginBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isValid()) {
                    saveUser();
                    loginBtn.setEnabled(false);
                    login();
                }
            }

        });


    }

    /**
     * 登录功能
     *
     * @return
     */
    private void login() {

        //登录handler
        loginHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();

                        break;
                    case 0:
                        Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                        loginBtn.setEnabled(true);
                        break;
                }
            }


        };

        new Thread() {

            @Override
            public void run() {
                Message msg = new Message();
                if (svUtil.connect()) {
                    msg.what = 1;
                } else {
                    msg.what = 0;
                    SSHUtil sshUtil = SSHUtil.getInstance();
                    try {
                        sshUtil.connect();
                        msg.what = 1;
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                        msg.what = 0;

                    }
                    loginHandler.sendMessage(msg);

                }

            }

        }.start();
    }

    /**
     * 判断用户名密码是否合法
     *
     * @return
     */
    private boolean isValid() {

        if (userName == null || userName.equals("")) {
            Toast.makeText(LoginActivity.this, "请输入账号", Toast.LENGTH_SHORT).show();
            return false;

        } else if (passWord == null || passWord.equals("")) {
            Toast.makeText(LoginActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;

    }

    /**
     * 存入用户名和密码
     */
    private void saveUser() {

        userName = userEdit.getText().toString();
        passWord = pswEdit.getText().toString();
        SPUtil.put(getApplicationContext(), "userName", userName);
        SPUtil.put(getApplicationContext(), "passWord", passWord);

    }

    @Override
    protected void onStart() {
        super.onStart();

        //设置初始用户名
        userName = SPUtil.get(getApplicationContext(), "userName", "").toString();
        if (userName != null || !userName.equals(""))
            userEdit.setText(userName);
    }
}
