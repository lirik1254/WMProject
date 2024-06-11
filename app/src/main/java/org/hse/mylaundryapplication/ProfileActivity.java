package org.hse.mylaundryapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import java.security.NoSuchAlgorithmException;
import android.content.SharedPreferences;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private EditText last_name;
    private EditText first_name;
    private EditText pat_name;
    private TextView mail;
    private Spinner dormitoryList;
    private ImageView settings;
    private ImageView logout;
    private ImageView slots;
    private ImageView profile;
    private EditText pass1;
    private EditText pass2;
    private Button saveChangesButton;
    private static String USER_KEY = "Users";
    private static DatabaseReference WMDataBase;
    private ImageButton showPassword;
    private boolean isShowPicture = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_layout);
        init();
        setUserInfo();

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        slots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, SlotsActivity.class);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = ProfileActivity.this.getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isLoggedIn", false);
                editor.apply();
                Intent intent = new Intent(ProfileActivity.this, AuthorisationActivity.class);
                startActivity(intent);
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });

        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AuthorisationActivity.isInternetAvailable(ProfileActivity.this)) {
                    if (pass1.getText().toString().isEmpty() && pass2.getText().toString().isEmpty()) {
                        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("first_name", first_name.getText().toString());
                        editor.putString("last_name", last_name.getText().toString());
                        editor.putString("pat_name", pat_name.getText().toString());
                        editor.apply();
                        Users updateUser = new Users(sharedPreferences.getString("first_name", "error"), sharedPreferences.getString("last_name", "error"),
                                sharedPreferences.getString("pat_name", "error"), sharedPreferences.getString("password", "error"),
                                sharedPreferences.getString("mail", "error"),
                                sharedPreferences.getInt("dormitory", 1), sharedPreferences.getInt("notifications", 0));
                        int dormitory = 0;
                        switch (dormitoryList.getSelectedItem().toString()) {
                            case "Пермь, ул. Уинская, д. 34":
                                dormitory = 1;
                                break;
                            case "Пермь, бульвар Гагарина, д. 37А":
                                dormitory = 2;
                                break;
                            default:
                                dormitory = 3;
                                break;
                        }
                        updateUser.dormitory = dormitory;
                        editor.putInt("dormitory", updateUser.dormitory);
                        editor.apply();
                        WMDataBase.child(updateUser.mail).setValue(updateUser);
                        Toast.makeText(getApplicationContext(), "Изменения сохранены!", Toast.LENGTH_LONG).show();
                    } else {
                        if (check_password()) {
                            SharedPreferences sharedPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("first_name", first_name.getText().toString());
                            editor.putString("last_name", last_name.getText().toString());
                            editor.putString("pat_name", pat_name.getText().toString());
                            try {
                                editor.putString("password", MainActivity.hashPass(pass1.getText().toString()));
                            } catch (NoSuchAlgorithmException e) {
                                throw new RuntimeException(e);
                            }
                            Users updateUser = new Users(sharedPreferences.getString("first_name", "error"), sharedPreferences.getString("last_name", "error"),
                                    sharedPreferences.getString("pat_name", "error"), sharedPreferences.getString("password", "error"),
                                    sharedPreferences.getString("mail", "error"),
                                    sharedPreferences.getInt("dormitory", 1), sharedPreferences.getInt("notifications", 0));
                            int dormitory = 0;
                            switch (dormitoryList.getSelectedItem().toString()) {
                                case "Пермь, ул. Уинская, д. 34":
                                    dormitory = 1;
                                    break;
                                case "Пермь, бульвар Гагарина, д. 37А":
                                    dormitory = 2;
                                    break;
                                default:
                                    dormitory = 3;
                                    break;
                            }

                            try {
                                updateUser.password = MainActivity.hashPass(pass1.getText().toString());
                            } catch (NoSuchAlgorithmException e) {
                                throw new RuntimeException(e);
                            }

                            updateUser.dormitory = dormitory;

                            editor.putInt("dormitory", updateUser.dormitory);
                            editor.putString("password", updateUser.password);

                            WMDataBase.child(updateUser.mail).setValue(updateUser);
                            Toast.makeText(getApplicationContext(), "Изменения сохранены!", Toast.LENGTH_LONG).show();
                            pass1.setText("");
                            pass2.setText("");
                        }
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), "Проверьте подключение к сети..", Toast.LENGTH_LONG).show();
                }
            }
        });

        showPassword.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isShowPicture) {
                    showPassword.setImageResource(R.drawable.showpassword);
                    pass1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    pass2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                } else {
                    showPassword.setImageResource(R.drawable.hidepassword);
                    pass1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    pass2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }
                isShowPicture = !isShowPicture;
            }
        });
    }

    public void init() {
        settings = findViewById(R.id.settings);
        logout = findViewById(R.id.logout);
        slots = findViewById(R.id.slots);
        last_name = findViewById(R.id.last_name);
        first_name = findViewById(R.id.first_name);
        pat_name = findViewById(R.id.pat_name);
        mail = findViewById(R.id.mail);
        dormitoryList = findViewById(R.id.groupList);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.dormitory_names, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dormitoryList.setAdapter(adapter);
        pass1 = findViewById(R.id.pass_1);
        pass2 = findViewById(R.id.pass_2);
        saveChangesButton = findViewById(R.id.saveChangesButton);
        WMDataBase = FirebaseDatabase.getInstance().getReference().child(USER_KEY);
        showPassword = findViewById(R.id.show_password);
        profile = findViewById(R.id.profile);
    }

    public void setUserInfo() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE);

        last_name.setText(sharedPreferences.getString("last_name", "error"));
        first_name.setText(sharedPreferences.getString("first_name", "error"));
        pat_name.setText(sharedPreferences.getString("pat_name", "error"));
        mail.setText(MainActivity.replacePointComma(sharedPreferences.getString("mail", "error"))) ;
        dormitoryList.setSelection(sharedPreferences.getInt("dormitory", 1)-1);
        pass1.setHint("Введите новый пароль..");
        pass2.setHint("Подтвердите новый пароль..");
    }

    private boolean check_password () {
        if (pass1.getText().toString().isEmpty() && pass2.getText().toString().length() > 0) {
            Toast.makeText(getApplicationContext(), "Введите пароль!", Toast.LENGTH_LONG).show();
            pass1.setText("");
            pass2.setText("");
            return false;
        }
        else if (pass1.getText().toString().length() < 8) {
            Toast.makeText(getApplicationContext(), "Длина пароля должна быть не меньше 8 символов!", Toast.LENGTH_LONG).show();
            pass1.setText("");
            pass2.setText("");
            return false;
        }
        else if (!pass1.getText().toString().equals(pass2.getText().toString())) {
            Toast.makeText(getApplicationContext(), "Пароли не совпадают!", Toast.LENGTH_LONG).show();
            pass1.setText("");
            pass2.setText("");
            return false;
        }
        return true;
    }

}