package org.hse.mylaundryapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MainActivity extends AppCompatActivity {

    private EditText last_name, first_name, pat_name, mail, password, confirm_password, code;
    private ImageButton showPassword;
    private static DatabaseReference WMDataBase;
    private static String USER_KEY = "Users"; // По сути название таблицы в базе данных WMDataBase
    private View registration_button, check_code_button, send_code;
    boolean isShowPicture = true;
    boolean isConfirmMail = false;
    boolean isConfirmPasswords = false;
    private String CODE = "1234567890098765";
    static ArrayList<Users> listData = new ArrayList<>();

    public static boolean isMessageSend = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_student_layout);
        init();
        getDataFromDB();
        showPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShowPicture) {
                    showPassword.setImageResource(R.drawable.hidepassword);
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    confirm_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    showPassword.setImageResource(R.drawable.showpassword);
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    confirm_password.setInputType(InputType.TYPE_CLASS_TEXT |InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                isShowPicture = !isShowPicture;
            }
        });

        registration_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check_password() && isConfirmMail) {
                    String first_name_text = first_name.getText().toString();
                    String last_name_text = last_name.getText().toString();
                    String pat_name_text = pat_name.getText().toString();
                    String mail_text = replacePointComma(mail.getText().toString());
                    String password_text = password.getText().toString();
                    Users newUser = new Users(first_name_text, last_name_text, pat_name_text, password_text, mail_text, 1, 0);
                    WMDataBase.child(mail_text).setValue(newUser);
                    Toast.makeText(getApplicationContext(), "Регистрация прошла успешно!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(MainActivity.this, AuthorisationActivity.class);
                    startActivity(intent);
                }
                else {
                    if (!isConfirmMail)
                        Toast.makeText(getApplicationContext(), "Сначала подтвердите почту", Toast.LENGTH_LONG).show();
                }
            }
        });

        send_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mailcheck() && checkRegistration(replacePointComma(mail.getText().toString()))) {
                    Log.d("code", new SendEmailTask().execute("kvshulzhik@edu.hse.ru").toString());
                }
            }
        });


        code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            // как только правильный код будет, результат залогируется, вместо этого можно чет другое делать
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (CODE.equals(s.toString())) {Log.d("tag","code_equals");}
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        check_code_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (code.getText().toString().equals(CODE)) {
                    code.setEnabled(false);
                    mail.setEnabled(false);
                    check_code_button.setEnabled(false);
                    send_code.setEnabled(false);
                    Toast.makeText(getApplicationContext(), "Почта подтверждена!", Toast.LENGTH_LONG).show();
                    isConfirmMail = true;
                }
                else {
                    Toast.makeText(getApplicationContext(), "Неверно введен код!", Toast.LENGTH_LONG).show();
                    code.setText("");
                }
            }
        });
    }

//    Инициализация едит текстов
    private void init() {
        WMDataBase = FirebaseDatabase.getInstance().getReference().child(USER_KEY); // Теперь будут добавляться записи в таблицу User благодаря гет референц
        first_name = findViewById(R.id.first_name);
        last_name = findViewById(R.id.last_name);
        pat_name = findViewById(R.id.pat_name);
        mail = findViewById(R.id.mail);
        password = findViewById(R.id.pass_1);
        registration_button = findViewById(R.id.register_button);
        confirm_password = findViewById(R.id.pass_2);
        showPassword = findViewById(R.id.showPassword);
        send_code = findViewById(R.id.send_code);
        code = findViewById(R.id.code_mail);
        check_code_button = findViewById(R.id.check_code_button);
    }

    private boolean check_password () {
        if (password.getText().toString().length() == 0)
        {
            if (isConfirmMail) {
                Toast.makeText(getApplicationContext(), "Сначал введите и подтвердите пароль!", Toast.LENGTH_LONG).show();
                return false;
            }
        }
        if (password.getText().toString().length() < 8) {
            if (isConfirmMail) {
                Toast.makeText(getApplicationContext(), "Длина пароля должна быть не меньше 8 символов!", Toast.LENGTH_LONG).show();
                password.setText("");
                confirm_password.setText("");
            }
            return false;
        }
        else if (!password.getText().toString().equals(confirm_password.getText().toString())) {
            if (isConfirmMail) {
                Toast.makeText(getApplicationContext(), "Пароли не совпадают!", Toast.LENGTH_LONG).show();
                password.setText("");
                confirm_password.setText("");
            }
            return false;
        }
        return true;
    }

    public boolean mailcheck () {
        if (mail.getText().toString().length() < 11 || !mail.getText().toString().substring(mail.getText().toString().length() - 11).equals("@edu.hse.ru")) {
            Toast.makeText(getApplicationContext(), "Домен почты должен быть @edu.hse.ru", Toast.LENGTH_LONG).show();
            mail.setText("");
            return false;
        }
        else if (mail.getText().toString().length() == 11) {
            Toast.makeText(getApplicationContext(), "Почта не может содержать только домен!", Toast.LENGTH_LONG).show();
            mail.setText("");
            return false;
        }
        return true;
    }

    public static String replacePointComma(String mail) {
        if (mail.substring(mail.length() - 10).contains(",")) {
            String lastElevenChars = mail.substring(mail.length() - 10);
            lastElevenChars = lastElevenChars.replace(',', '.');
            mail = mail.substring(0, mail.length() - 10) + lastElevenChars;
            return mail;
        }
        String lastElevenChars = mail.substring(mail.length() - 10);
        lastElevenChars = lastElevenChars.replace('.', ',');
        mail = mail.substring(0, mail.length() - 10) + lastElevenChars;
        return mail;
    }

    private class SendEmailTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            String host = "smtp.mail.ru";
            String username = "stiralnaya.mashina.21@mail.ru";
            String password = "QsEuVtbCK1JZLVe8K39v";
            String recipient = params[0];
            String code = String.valueOf((int) (Math.random() * 9000) + 1000); // Генерация случайного кода
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", "587");
            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });
            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(username));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
                message.setSubject("Код подтверждения");
                message.setText("Ваш код подтверждения: " + code);
                Transport.send(message);
                System.out.println("Сообщение отправлено успешно.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Код успешно отправлен", Toast.LENGTH_SHORT).show();
                    }
                });
                CODE = code;
            } catch (MessagingException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Не удалось отправить код", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            Log.d("message","sending");
        }
    }
    public static void getDataFromDB() {
        WMDataBase = FirebaseDatabase.getInstance().getReference().child(USER_KEY);
        ValueEventListener vListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                if (listData.size() > 0)
                    listData.clear();
                for(DataSnapshot ds: datasnapshot.getChildren()) {
                    Users user = ds.getValue(Users.class);
                    assert user != null;
                    listData.add(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        WMDataBase.addValueEventListener(vListener);
    }

    public boolean checkRegistration(String mail) {
        for (Users user : listData) {
            if (user.mail.equals(mail)){
                Toast.makeText(getApplicationContext(), "Такой пользователь уже зарегистрирован в системе!", Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return true;
    }
}