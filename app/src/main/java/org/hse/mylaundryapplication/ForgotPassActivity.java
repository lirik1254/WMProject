package org.hse.mylaundryapplication;

import static org.hse.mylaundryapplication.MainActivity.hashPass;
import static org.hse.mylaundryapplication.MainActivity.listData;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class ForgotPassActivity extends AppCompatActivity {

    private EditText mail, code_mail, pass_1, pass_2;
    private Button send_code, check_code, set_new_pass;
    boolean isShowPicture = false;
    private ImageButton show_password;
    boolean isConfirmMail = false;
    private String CODE = "1234567890098765";
    private static String USER_KEY = "Users";
    private static DatabaseReference WMDataBase;
    private List<Users> listData = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_pass_layout);
        init();
        getDataFromDB();

        show_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShowPicture) {
                    show_password.setImageResource(R.drawable.showpassword);
                    pass_1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    pass_2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                } else {
                    show_password.setImageResource(R.drawable.hidepassword);
                    pass_1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    pass_2.setInputType(InputType.TYPE_CLASS_TEXT |InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

                }
                isShowPicture = !isShowPicture;
            }
        });

        send_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mailcheck() && checkRegistration(replacePointComma(mail.getText().toString()))) {
                    Log.d("code", new SendEmailTask().execute(mail.getText().toString()).toString());
                }
            }
        });
        check_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (code_mail.getText().toString().equals(CODE)) {
                    code_mail.setEnabled(false);
                    mail.setEnabled(false);
                    check_code.setEnabled(false);
                    send_code.setEnabled(false);
                    Toast.makeText(getApplicationContext(), "Почта подтверждена!", Toast.LENGTH_LONG).show();
                    isConfirmMail = true;
                }
                else {
                    Toast.makeText(getApplicationContext(), "Неверно введен код!", Toast.LENGTH_LONG).show();
                    code_mail.setText("");
                }
            }
        });

        set_new_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isConfirmMail)
                    Toast.makeText(getApplicationContext(), "Сначала нужно подтвердить почту!", Toast.LENGTH_LONG).show();
                else if (check_password()) {
                    for (Users us : listData) {
                        if (replacePointComma(us.mail).equals(mail.getText().toString()))
                        {
                            Users user = null;
                            try {
                                user = new Users(us.first_name, us.last_name, us.pat_name, hashPass(pass_1.getText().toString()), replacePointComma(mail.getText().toString()), us.dormitory, us.notifications);
                            } catch (NoSuchAlgorithmException e) {
                                throw new RuntimeException(e);
                            }
                            WMDataBase.child(replacePointComma(mail.getText().toString())).setValue(user);
                            Toast.makeText(getApplicationContext(), "Пароль изменён!", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(ForgotPassActivity.this, AuthorisationActivity.class);
                            startActivity(intent);
                            return;
                        }
                    }
                }
            }
        });
    }

    private void init() {
        WMDataBase = FirebaseDatabase.getInstance().getReference().child(USER_KEY);
        mail = findViewById(R.id.mail);
        code_mail = findViewById(R.id.code_mail);
        pass_1 = findViewById(R.id.pass_1);
        pass_2 = findViewById(R.id.pass_2);
        send_code = findViewById(R.id.send_code);
        check_code = findViewById(R.id.check_code_button);
        set_new_pass = findViewById(R.id.set_new_pass);
        show_password = findViewById(R.id.show_password);
    }

    public boolean mailcheck () {
        if (mail.getText().toString().length() < 11 || !mail.getText().toString().substring(mail.getText().toString().length() - 11).equals("@edu.hse.ru")) {
            Toast.makeText(getApplicationContext(), "Домен почты должен быть @edu.hse.ru", Toast.LENGTH_LONG).show();
            return false;
        }
        else if (mail.getText().toString().length() == 11) {
            Toast.makeText(getApplicationContext(), "Почта не может содержать только домен!", Toast.LENGTH_LONG).show();
            return false;
        }
        else if (mail.getText().toString().chars().filter(ch -> ch == '@').count() > 1) {
            Toast.makeText(getApplicationContext(), "Некорректно введена почта", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
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
                        Toast.makeText(ForgotPassActivity.this, "Код успешно отправлен", Toast.LENGTH_SHORT).show();
                    }
                });
                CODE = code;
            } catch (MessagingException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ForgotPassActivity.this, "Не удалось отправить код", Toast.LENGTH_SHORT).show();
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

    public boolean checkRegistration(String mail) {
        for (Users us : listData) {
            if (us.mail.equals(mail))
                return true;
        }
        Toast.makeText(getApplicationContext(), "Такого пользователя нет в системе!", Toast.LENGTH_LONG).show();
        return false;
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

    private void getDataFromDB() {
        ValueEventListener vListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (listData.size() > 0)
                    listData.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren())
                {
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

    private void setNewPassword(String pass){
        if (!isConfirmMail)
            Toast.makeText(getApplicationContext(), "Сначала нужно подтвердить почту!", Toast.LENGTH_LONG);
        check_password();
    }

    private boolean check_password () {
        if (pass_1.getText().toString().length() == 0)
        {
                Toast.makeText(getApplicationContext(), "Сначал введите пароль и подтверждение пароля!", Toast.LENGTH_LONG).show();
                return false;
        }
        if (pass_1.getText().toString().length() < 8) {
                Toast.makeText(getApplicationContext(), "Длина пароля должна быть не меньше 8 символов!", Toast.LENGTH_LONG).show();
                pass_1.setText("");
                pass_2.setText("");
                return false;
        }
        else if (!pass_1.getText().toString().equals(pass_2.getText().toString())) {
                Toast.makeText(getApplicationContext(), "Пароли не совпадают!", Toast.LENGTH_LONG).show();
                pass_1.setText("");
                pass_2.setText("");
                return false;
            }
        return true;
    }
}