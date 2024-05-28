package org.hse.mylaundryapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class AuthorisationActivity extends AppCompatActivity {
    EditText mail, password;
    View loginButton;
    ImageButton showPassword;
    private boolean isShowPicture = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_student_layout);
        MainActivity.getDataFromDB();
        init();

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
                    Toast.makeText(getApplicationContext(), "Неверно введён логин или пароль!", Toast.LENGTH_LONG).show();
                    mail.setText("");
                    password.setText("");
                }
            }
        });

        showPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShowPicture) {
                    showPassword.setImageResource(R.drawable.hidepassword);
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    showPassword.setImageResource(R.drawable.showpassword);
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                isShowPicture = !isShowPicture;
            }
        });
    }

    public void init() {
        mail = findViewById(R.id.mail);
        password = findViewById(R.id.pass_1);
        loginButton = findViewById(R.id.loginButton);
        showPassword = findViewById(R.id.showPassword2);
    }

    public boolean loginPasswordCheck() {
        for (Users user : MainActivity.listData) {
            if (user.mail.equals(MainActivity.replacePointComma(mail.getText().toString())) && user.password.equals(password.getText().toString()))
                return true;
        }
        return false;
    }
}