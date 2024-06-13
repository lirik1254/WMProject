package org.hse.mylaundryapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Build;
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

import java.security.NoSuchAlgorithmException;
import android.content.Context;
import android.content.SharedPreferences;

public class AuthorisationActivity extends AppCompatActivity {
    EditText mail, password;
    private TextView notRegistred, forgotPass;
    private static DatabaseReference WMDataBase;
    private String USER_KEY = "Users";
    View loginButton;
    ImageButton showPassword;
    private boolean isShowPicture = false;
    public static Users currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            Intent intent = new Intent(AuthorisationActivity.this, SlotsActivity.class);
            startActivity(intent);
            finish();
        }

        setContentView(R.layout.login_student_layout);
        init();
        MainActivity.getDataFromDB();


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInternetAvailable(AuthorisationActivity.this)) {
                    try {
                        if (loginPasswordCheck()) {
                            Toast.makeText(getApplicationContext(), "Вы успешно авторизованы!", Toast.LENGTH_LONG).show();
                            for (Users us : MainActivity.listData) {
                                if (us.mail.equals(MainActivity.replacePointComma(mail.getText().toString()))) {
                                    currentUser = new Users(us.first_name, us.last_name, us.pat_name, us.password, us.mail, us.dormitory, us.notifications);
                                    saveUser(AuthorisationActivity.this, us.first_name, us.last_name, us.pat_name, us.password, us.mail, us.dormitory, us.notifications);
                                }
                            }
                            saveUserLoginState(AuthorisationActivity.this, true);
                            Intent intent = new Intent(AuthorisationActivity.this, SlotsActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
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
                    } catch (NoSuchAlgorithmException e) {
                        throw new RuntimeException(e);
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), "Проверьте подключение к сети..", Toast.LENGTH_LONG).show();
                }
            }
        });
//
        showPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShowPicture) {
                    showPassword.setImageResource(R.drawable.showpassword);
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                } else {
                    showPassword.setImageResource(R.drawable.hidepassword);
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }
                isShowPicture = !isShowPicture;
            }
        });
//
        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AuthorisationActivity.this,  ForgotPassActivity.class);
                startActivity(intent);
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

    public boolean loginPasswordCheck() throws NoSuchAlgorithmException {
        if (mail.getText().toString().length() < 11 || !mail.getText().toString().substring(mail.getText().toString().length() - 11).equals("@edu.hse.ru")) {
            return false;
        }
        else if (mail.getText().toString().length() == 11) {
            return false;
        }
        for (Users user : MainActivity.listData) {
            if (user.mail.equals(MainActivity.replacePointComma(mail.getText().toString())) && user.password.equals(MainActivity.hashPass(password.getText().toString())))
                return true;
        }
        return false;
    }


    public void saveUserLoginState(Context context, boolean isLoggedIn) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", isLoggedIn);
        editor.apply();
    }

    public void saveUser(Context context, String first_name, String last_name, String pat_name, String password, String mail, Integer dormitory, Integer notifications) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("first_name", first_name);
        editor.putString("pat_name", pat_name);
        editor.putString("password", password);
        editor.putString("mail", mail);
        editor.putString("last_name", last_name);
        editor.putInt("dormitory", dormitory);
        editor.putInt("nots", notifications);
        editor.apply();
    }

    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            android.net.Network network = connectivityManager.getActiveNetwork();
            if (network == null) {
                return false;
            }
            NetworkCapabilities activeNetwork = connectivityManager.getNetworkCapabilities(network);
            if (activeNetwork == null) {
                return false;
            }
            return activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET);
        }
        return false;
    }
}