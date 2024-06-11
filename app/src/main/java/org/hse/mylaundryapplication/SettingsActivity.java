package org.hse.mylaundryapplication;
import static android.content.ContentValues.TAG;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

public class SettingsActivity extends AppCompatActivity {

    final static String PERMISSION= "android.permission.POST_NOTIFICATIONS";
    final static String PERMISSION1 = "android.permission.SCHEDULE_EXACT_ALARM";
    private static final int REQUEST_PERMISSION_CODE = 1;
    private static final int REQUEST_PERMISSION_CODE1 = 2;
    private static String userId = "mmgolovanova@edu,hse,ru";
    static ArrayList<Users> listData = new ArrayList<>();
    static ArrayList<Slots> slotsArray = new ArrayList<>();
    private String USERS_KEY = "Users";
    private String SLOTS_KEY = "Slots";
    private String WASHING_MACHINES_KEY = "WM";

    static Integer[] timeArray = {5, 10, 15, 30, 60};

    private static Integer nots = 0;

    private static DatabaseReference WMDataBaseUsers;
    private static DatabaseReference WMDataBaseWM;
    private static DatabaseReference WMDataBaseSlots;
    private static ImageView logout;
    private static ImageView profile;

    private static Switch notificationToggler;
    private static View notificationLabel;
    Spinner spinner;
    ArrayAdapter<?> adapter;
    Button save_button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("mail", "error");
        notificationToggler = findViewById(R.id.notification_switch);
        notificationLabel = findViewById(R.id.notification_label);
        logout = findViewById(R.id.logout);
        profile = findViewById(R.id.profile);
        spinner = findViewById(R.id.groupList);
        save_button = findViewById(R.id.save_button);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Arrays.stream(timeArray).toArray());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        WMDataBaseUsers = FirebaseDatabase.getInstance().getReference().child(USERS_KEY);
        WMDataBaseWM = FirebaseDatabase.getInstance().getReference().child(WASHING_MACHINES_KEY);
        WMDataBaseSlots = FirebaseDatabase.getInstance().getReference().child(SLOTS_KEY);
        getUserFromDB();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {
                Object item = adapter.getItem(selectedItemPosition);
                Log.d(TAG, "selectedItem: " + item);
            }

            public void onNothingSelected(AdapterView<?> parent) {
                //
            }
        });
        spinner.setSelected(true);
        notificationToggler.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                 //   CheckPermission("Пора стираться совсем скоро", 5);
                } else {
                    turnOffNotification();
                }
            }
        });

        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AuthorisationActivity.isInternetAvailable(SettingsActivity.this)) {
                    changeNots();
                    Toast.makeText(getApplicationContext(), "Изменения сохранены!", Toast.LENGTH_LONG).show();
                    SharedPreferences sharedPreferences = SettingsActivity.this.getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("nots", nots);
                    editor.putBoolean("isChecked", notificationToggler.isChecked());
                    editor.apply();
                }
                else
                    Toast.makeText(getApplicationContext(), "Проверьте подключение к сети..", Toast.LENGTH_LONG).show();

            }
        });
        Toolbar toolbar = findViewById(R.id.toolbar).findViewById(R.id.my_toolbar);

// Находим элементы в Toolbar
        View button_settings = toolbar.findViewById(R.id.settings);

        button_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Settings();
                finish();
            }
        });

        View button_slots = toolbar.findViewById(R.id.slots);

        button_slots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Slots();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = SettingsActivity.this.getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isLoggedIn", false);
                editor.apply();
                Intent intent = new Intent(SettingsActivity.this, AuthorisationActivity.class);
                startActivity(intent);
                finish();
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        if (!AuthorisationActivity.isInternetAvailable(SettingsActivity.this)) {
            notificationToggler.setChecked(sharedPreferences.getBoolean("isChecked", false));
            spinner.setSelection(Arrays.asList(timeArray).indexOf(sharedPreferences.getInt("nots", 5)));
        }
    }
    private void Settings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
    private void Slots() {
        Intent intent = new Intent(this, SlotsActivity.class);
        startActivity(intent);
    }

    public void changeNots() {
        Users user = listData.get(0);
        if (!notificationToggler.isChecked() && nots!=0)
        {
            Log.d("nots", "turning_off");
            user.notifications=0;
            WMDataBaseUsers.child(userId).setValue(user);
        }
        if (notificationToggler.isChecked()) {
            if (nots!=(Integer)adapter.getItem(spinner.getSelectedItemPosition())){
                user.notifications=(Integer)adapter.getItem(spinner.getSelectedItemPosition());
                WMDataBaseUsers.child(userId).setValue(user);
            }
        }
    }
    public  void getNotificationTime() {
        nots = listData.get(0).notifications;
        if (nots>0) {
            notificationToggler.setChecked(true);
            if (nots==5) {
                spinner.setSelection(0);

            }
            if (nots==10) {
                spinner.setSelection(1);
            }
            if (nots==15) {
                spinner.setSelection(2);
            }
            if (nots==30) {
                spinner.setSelection(3);
            }
            if (nots==60) {
                spinner.setSelection(4);
            }
        }
        else {
            notificationToggler.setChecked(false);
        }
        getSlotsFromDB();

    }

    public void getSlotsFromDB() {
        ValueEventListener vListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                if (slotsArray.size() > 0)
                    slotsArray.clear();
                for (DataSnapshot ds : datasnapshot.getChildren()) {
                    Slots slot = (Slots) ds.getValue(Slots.class);
                    assert slot != null;
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    try {
                        TimeZone permTimeZone = TimeZone.getTimeZone("Asia/Yekaterinburg");
                        Calendar calendar = Calendar.getInstance(permTimeZone); // Устанавливаем часовой пояс при создании календаря
                        SimpleDateFormat f1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        f1.setTimeZone(permTimeZone); // Устанавливаем часовой пояс для форматирования
                        dateFormat.setTimeZone(permTimeZone);

                        if (slot.user_id.toString().equals(userId) && dateFormat.parse(slot.start).after(f1.parse(f1.format(calendar.getTime())))) {
                            slotsArray.add(slot);
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

                if (nots != 0)
                    for (Slots s : slotsArray) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        TimeZone permTimeZone = TimeZone.getTimeZone("Asia/Yekaterinburg");
                        Calendar calendar = Calendar.getInstance(permTimeZone); // Устанавливаем часовой пояс при создании календаря
                        SimpleDateFormat f1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        f1.setTimeZone(permTimeZone); // Устанавливаем часовой пояс для форматирования
                        dateFormat.setTimeZone(permTimeZone);
                        Date d;
                        try {
                            d = f1.parse(f1.format(calendar.getTime()));
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }

                        long differenceInSeconds = 0;
                        try {
                            differenceInSeconds = (f1.parse(s.start).getTime() - d.getTime()) / 1000 - 60 * nots;
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                        turnOffNotification();
                        Log.d("going here", "there");
                        if (differenceInSeconds>0) CheckPermission("Скоро пора стираться!", ((int) differenceInSeconds));
                    }

            else {
                    turnOffNotification();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        WMDataBaseSlots.addValueEventListener(vListener);

    }

    public  void getUserFromDB() {
        ValueEventListener vListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                if (listData.size() > 0)
                    listData.clear();
                for(DataSnapshot ds: datasnapshot.getChildren()) {
                    Users user = ds.getValue(Users.class);
                    assert user != null;
                    if (Objects.equals( (String)user.mail, userId))
                    {listData.add(user);
                    }
                }
             getNotificationTime();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        WMDataBaseUsers.addValueEventListener(vListener);
    }



    public void turnOnNotification(String message, Integer delay){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

// Создаем Intent для запуска уведомления
        Intent notificationIntent = new Intent(this, NotificationReceiver.class);
        notificationIntent.putExtra("notificationId", 1); // Уникальный ID уведомления
        notificationIntent.putExtra("notificationMessage", message);


// Создаем PendingIntent, который будет запускать уведомление
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
// Устанавливаем отложенное событие на 20 секунд
        Log.d("time_to_wait", String.valueOf(delay));
        long futureInMillis = System.currentTimeMillis() + delay*1000; // Текущее время + 20 секунд
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, futureInMillis, pendingIntent);
    }


    public void turnOffNotification()
    {
        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);

    }
    public void CheckPermission(String message, Integer delay) {
        int permissionCheck = ActivityCompat.checkSelfPermission(this, PERMISSION);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, PERMISSION1) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, PERMISSION1)) {
                Log.d("au", "kuk2u");
                showExplanation("Внимание", "Объясняю", PERMISSION1, REQUEST_PERMISSION_CODE1);
            } else {
                Log.d("au", "kuku");
            //    requestPermission(PERMISSION, REQUEST_PERMISSION_CODE);
                requestPermission(PERMISSION1, REQUEST_PERMISSION_CODE1);
            }
        }

        else {

            Log.d("au", "we are here");
            turnOnNotification(message, delay);
        }

    }

    private void showExplanation(String title,
                                 String message,
                                 final String permission,
                                 final int permissionRequestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermission(permission, permissionRequestCode);
                    }
                });
        builder.create().show();
    }

    private void requestPermission(String permissionName, int permissionRequestCode) {
        ActivityCompat.requestPermissions(this,
                new String[]{permissionName}, permissionRequestCode);
    }

}
