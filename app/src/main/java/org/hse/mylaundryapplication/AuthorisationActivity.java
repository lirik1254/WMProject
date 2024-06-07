package org.hse.mylaundryapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AuthorisationActivity extends AppCompatActivity {
    EditText mail, password;
    private TextView notRegistred, forgotPass;
    private static DatabaseReference WMDataBase;
    private String USER_KEY = "Users";
    View loginButton;
    ImageButton showPassword;
    private boolean isShowPicture = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_student_layout);
        init();
        MainActivity.getDataFromDB();


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loginPasswordCheck())
                {
                    Toast.makeText(getApplicationContext(), "Вы успешно авторизованы!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(AuthorisationActivity.this, SlotsActivity.class);
                    startActivity(intent);
                }
                else {
                    if (mail.getText().toString().equals("") && password.getText().toString().equals(""))
                        Toast.makeText(getApplicationContext(), "Введите почту и пароль!", Toast.LENGTH_LONG).show();
                    else if (password.getText().toString().equals(""))
                        Toast.makeText(getApplicationContext(), "Введите пароль!", Toast.LENGTH_LONG).show();
                    else if (mail.getText().toString().equals(""))
                        Toast.makeText(getApplicationContext(), "Введите логин!", Toast.LENGTH_LONG).show();
                    else {
                        Toast.makeText(getApplicationContext(), "Неверно введён логин или пароль!", Toast.LENGTH_LONG).show();
                        mail.setText("");
                        password.setText("");
                    }
                }
            }
        });
//
        showPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShowPicture) {
                    showPassword.setImageResource(R.drawable.showpassword);
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    showPassword.setImageResource(R.drawable.hidepassword);
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                isShowPicture = !isShowPicture;
            }
        });
//
        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
//
        notRegistred.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AuthorisationActivity.this,  MainActivity.class);
                startActivity(intent);
            }
        });
    }

    public void init() {
        mail = findViewById(R.id.mail);
        password = findViewById(R.id.pass_1);
        loginButton = findViewById(R.id.loginButton);
        showPassword = findViewById(R.id.showPassword2);
        forgotPass = findViewById(R.id.forgot_pass);
        notRegistred = findViewById(R.id.notRegist);
    }

    public boolean loginPasswordCheck() {
        if (mail.getText().toString().length() < 11 || !mail.getText().toString().substring(mail.getText().toString().length() - 11).equals("@edu.hse.ru")) {
            return false;
        }
        else if (mail.getText().toString().length() == 11) {
            return false;
        }
        for (Users user : MainActivity.listData) {
            if (user.mail.equals(MainActivity.replacePointComma(mail.getText().toString())) && user.password.equals(password.getText().toString()))
                return true;
        }
        return false;
    }

}