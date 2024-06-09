package org.hse.mylaundryapplication;

import static org.hse.mylaundryapplication.SettingsActivity.PERMISSION;
import static org.hse.mylaundryapplication.SettingsActivity.PERMISSION1;
import static java.lang.Integer.parseInt;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

public class SlotsActivity extends AppCompatActivity {
    private static final Integer REQUEST_PERMISSION_CODE1 =1 ;
    private RecyclerView recyclerView;
    private static ItemAdapter adapter;
    private String USERS_KEY = "Users";
    private String SLOTS_KEY = "Slots";
    private String WASHING_MACHINES_KEY = "WM";
    private static String userId = "mmgolovanova@edu,hse,ru";
    private static Integer dormId = 1;
    static String[] timeArray = {"09:00", "11:00", "15:00", "17:00", "19:00", "21:00"};
    View reset_slot_button;
    static ArrayList<Users> listData = new ArrayList<>();
    static ArrayList<WM> wmArrayList = new ArrayList<>();
    static Map<String, Integer> wm_id_floor = new HashMap<>();
    static ArrayList<String> wmIdArray = new ArrayList<>();
    static ArrayList<Slots> slotsArray = new ArrayList<>();
    private static DatabaseReference WMDataBaseUsers;
    private static DatabaseReference WMDataBaseWM;
    private static DatabaseReference WMDataBaseSlots;
    public static TextView chosen_slot;
    private static ImageView logout;
    public static View delete_slot;
    private static ImageView profile;

    public static Integer nots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slots_layout);
        chosen_slot = findViewById(R.id.chosen_slot);
        delete_slot = findViewById(R.id.reset_chosen_slot);
        logout = findViewById(R.id.logout);

        recyclerView = findViewById(R.id.listView);

        profile = findViewById(R.id.profile);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        adapter = new ItemAdapter(this::onScheduleItemClick);
        recyclerView.setAdapter(adapter);
        init();
       // userId =getIntent().getStringExtra('USER_ID');
        WMDataBaseUsers = FirebaseDatabase.getInstance().getReference().child(USERS_KEY);
        WMDataBaseWM = FirebaseDatabase.getInstance().getReference().child(WASHING_MACHINES_KEY);
        WMDataBaseSlots = FirebaseDatabase.getInstance().getReference().child(SLOTS_KEY);
        getUserFromDB();
        chosen_slot.setVisibility(View.INVISIBLE);
        delete_slot.setVisibility(View.INVISIBLE);
        delete_slot.setActivated(false);
        delete_slot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             delete_chosen_slot();
            }
        });
        Toolbar toolbar = findViewById(R.id.toolbar).findViewById(R.id.my_toolbar);

// Находим элементы в Toolbar
        View button_settings = toolbar.findViewById(R.id.settings);

        button_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Settings();
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
                Intent intent = new Intent(SlotsActivity.this, AuthorisationActivity.class);
                startActivity(intent);
                finish();
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SlotsActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

    }
    private void Settings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
    private void Slots() {
        Intent intent = new Intent(this, SlotsActivity.class);
        startActivity(intent);
    }

    public void delete_chosen_slot(){

        for (Slots s: slotsArray)
        {
            if (s.user_id.equals(userId))
            {
                TimeZone permTimeZone = TimeZone.getTimeZone("Asia/Yekaterinburg");
                Calendar calendar = Calendar.getInstance(permTimeZone); // Устанавливаем часовой пояс при создании календаря
                SimpleDateFormat f1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                f1.setTimeZone(permTimeZone);
                String str_date = f1.format(calendar.getTime());
                if (s.start.compareTo(str_date)>0)
            {WMDataBaseSlots.child(s.start.substring(s.start.length()-5)+wm_id_floor.get(String.valueOf(s.wm_id))+String.valueOf(dormId)).removeValue();
        Log.d("deleting", s.start.substring(s.start.length()-5)+wm_id_floor.get(String.valueOf(s.wm_id))+String.valueOf(dormId));
            }
            else {
   Toast.makeText(getApplication(), "Вы уже не можете отменить свой слот", Toast.LENGTH_LONG).show();
                }
            }}
    }
    public static ScheduleItem baseColor (ScheduleItem scheduleItem){
        scheduleItem.setTime1("#003399");
        scheduleItem.setTime2("#003399");
        scheduleItem.setTime3("#003399");
        scheduleItem.setTime4("#003399");
        scheduleItem.setTime5("#003399");
        scheduleItem.setTime6("#003399");
        return scheduleItem;
    }

    public static ScheduleItem color_past_slots_Item(ScheduleItem scheduleItem, String str_date) {
        if ( str_date.compareTo("09:00")>0){
            scheduleItem.setTime1("#848482");

        }
        if ( str_date.compareTo("11:00")>0) {
            scheduleItem.setTime2("#848482");
        }
        if (str_date.compareTo("15:00")>0) {
            scheduleItem.setTime3("#848482");
        }
        if (str_date.compareTo("17:00")>0) {
            scheduleItem.setTime4("#848482");
        }
        if (str_date.compareTo("19:00")>0) {
            scheduleItem.setTime5("#848482");
        }
        if (str_date.compareTo("21:00")>0) {
            scheduleItem.setTime6("#848482");
        }
        return scheduleItem;
    }

    public static ScheduleItem color_chosen_clot(ScheduleItem scheduleItem, String time) {
        if (time.equals("09:00")){
            scheduleItem.setTime1("#848482");
        }
        if (time.equals("11:00")) {
            scheduleItem.setTime2("#848482");
        }
        if (time.equals("15:00")) {
            scheduleItem.setTime3("#848482");
        }
        if (time.equals("17:00")) {
            scheduleItem.setTime4("#848482");
        }
        if (time.equals("19:00") ) {
            scheduleItem.setTime5("#848482");
        }
        if (time.equals("21:00")) {
            scheduleItem.setTime6("#848482");
        }
        return scheduleItem;
    }

    private void initSlots() throws ParseException {

        List<ScheduleItem> groupsRes = new ArrayList<>();
        for (String wm : wm_id_floor.keySet()) {
            ArrayList<Slots> slots = new ArrayList<>();
            for (Slots s: slotsArray) {
                if (s.wm_id.toString().equals(wm)) slots.add(s);}
            ScheduleItem scheduleItem = new ScheduleItem();
            scheduleItem.setFloor(wm_id_floor.get(wm));
            scheduleItem = baseColor(scheduleItem);
            TimeZone permTimeZone = TimeZone.getTimeZone("Asia/Yekaterinburg");
            Calendar calendar = Calendar.getInstance(permTimeZone); // Устанавливаем часовой пояс при создании календаря
            SimpleDateFormat f1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            f1.setTimeZone(permTimeZone); // Устанавливаем часовой пояс для форматирования
            Log.d("time", f1.format(calendar.getTime()));
            String str_date = f1.format(calendar.getTime()).substring(f1.format(calendar.getTime()).length()-5);
            Log.d("time ыек", f1.format(calendar.getTime()).substring(f1.format(calendar.getTime()).length()-5));
            for (String time: timeArray)
            {
                for (Slots slot: slots)
                {
                    if (Objects.equals(time, slot.start.substring(slot.start.length() - 5))){
                        scheduleItem = color_chosen_clot(scheduleItem, time);
                    }
                }
                scheduleItem = color_past_slots_Item(scheduleItem, str_date);
            }
            groupsRes.add(scheduleItem);
        }
        initData(groupsRes);
    }

    private static void initData(List<ScheduleItem> dv) {
       // adapter.clear();
        adapter.setDataList(dv);
    }
    private void onScheduleItemClick(ScheduleItem scheduleItem) {

    }

    private  void getDormitory() {
            Users user = (Users) listData.get(0);
            if (Objects.equals(user.mail, userId)) {
                dormId = user.dormitory;
            }
        getWMFromDB();
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
                    for (String id : wmIdArray) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        try {
                            TimeZone permTimeZone = TimeZone.getTimeZone("Asia/Yekaterinburg");
                            Calendar calendar = Calendar.getInstance(permTimeZone); // Устанавливаем часовой пояс при создании календаря
                            calendar.set(Calendar.HOUR_OF_DAY, 0); // Устанавливаем часы
                            calendar.set(Calendar.MINUTE, 0);
                            SimpleDateFormat f1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                            f1.setTimeZone(permTimeZone); // Устанавливаем часовой пояс для форматирования
                            dateFormat.setTimeZone(permTimeZone);

                            if (slot.wm_id.toString().equals(id)  &&  dateFormat.parse(slot.start).after(f1.parse(f1.format(calendar.getTime())))) {
                                slotsArray.add(slot);
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }

                try {

                    initSlots();
                    Boolean is_busy = false;
                    for (Slots s: slotsArray) {

                        if (s.user_id.equals(userId)) {
                            is_busy = true;
                            chosen_slot.setVisibility(View.VISIBLE);
                            chosen_slot.setText("Запись на "+s.start.substring(s.start.length()-5) + " на " + String.valueOf(wm_id_floor.get(String.valueOf(s.wm_id)))+ " этаж");
                            delete_slot.setVisibility(View.VISIBLE);
                            delete_slot.setActivated(true);
                        }
                    }
                    if (!is_busy) {

                        delete_slot.setVisibility(View.INVISIBLE);
                        delete_slot.setActivated(false);
                        chosen_slot.setText("Вы не записаны на сегодня!");

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

                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
        };
        WMDataBaseSlots.addValueEventListener(vListener);

    }
    public void CheckPermission(String message, Integer delay) {
        int permissionCheck = ActivityCompat.checkSelfPermission(this, PERMISSION);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, PERMISSION1) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, PERMISSION)) {
                Log.d("au", "kuk2u");
                showExplanation("Внимание", "Объясняю", PERMISSION, REQUEST_PERMISSION_CODE1);
            } else {
                Log.d("au", "kuku");
                //    requestPermission(PERMISSION, REQUEST_PERMISSION_CODE);
                requestPermission(PERMISSION, REQUEST_PERMISSION_CODE1);
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
    public void getWMFromDB() {
        ValueEventListener vListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                if (wmArrayList.size() > 0) {
                    wmArrayList.clear();
                    wmIdArray.clear();
                    wm_id_floor.clear();
                }

                for(DataSnapshot ds: datasnapshot.getChildren()) {
                    WM wm = (WM) ds.getValue(WM.class);
                    assert wm != null;
                    if (Objects.equals(wm.dormitory_id, dormId) & wm.is_working==1)
                    {wmArrayList.add(wm);
                        wm_id_floor.put(ds.getKey(), wm.floor);
                    wmIdArray.add(
                            ds.getKey());}

                }
                getSlotsFromDB();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        WMDataBaseWM.addValueEventListener(vListener);
    }


    public void getUserFromDB() {
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
                        nots = user.notifications;
                    }
                }
                getDormitory();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        WMDataBaseUsers.addValueEventListener(vListener);
    }


    public void init() {
        reset_slot_button = findViewById(R.id.reset_chosen_slot);
    }
    public final static class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private final static int TYPE_ITEM = 0;

        private List<ScheduleItem> dataList = new ArrayList<>();
        private ScheduleItem.OnItemClick onItemClick;

        public ItemAdapter(ScheduleItem.OnItemClick onItemClick) {
            this.onItemClick = onItemClick;
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            if (viewType == TYPE_ITEM) {
                View contactView = inflater.inflate(R.layout.floor_layout, parent, false);
                return new ViewHolder(contactView, context, onItemClick);
            } else{
            try {
                throw new IllegalAccessException("Invalid view type");
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }}
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ScheduleItem data = dataList.get(position);
            if (holder instanceof ViewHolder) {
                ((ViewHolder) holder).bind(data);

            }
        }

        public int getItemViewType(int position) {
            return TYPE_ITEM;
        }

        public void setDataList(List<ScheduleItem> list) {
            ScheduleDiffUtilCallback scheduleDiffUtilCallback = new ScheduleDiffUtilCallback(dataList, list);
            DiffUtil.DiffResult scheduleDiffres = DiffUtil.calculateDiff(scheduleDiffUtilCallback);
            this.dataList = list;
            scheduleDiffres.dispatchUpdatesTo(this);


        }

        public void clear() {
            dataList.clear();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private Context context;
        private ScheduleItem.OnItemClick onItemClick;
        private TextView floor;
        private Button time_1;
        private TextView time_2;
        private TextView time_3;
        private TextView time_4;
        private TextView time_5;
        private TextView time_6;
        public ViewHolder(View itemView, Context context, ScheduleItem.OnItemClick onItemClick) {
            super(itemView);
            this.context = context;
            this.onItemClick = onItemClick;
            floor = itemView.findViewById(R.id.floor);
            time_1 = itemView.findViewById(R.id.time_1);
            time_2 = itemView.findViewById(R.id.time_2);
            time_3 = itemView.findViewById(R.id.time_3);
            time_4 = itemView.findViewById(R.id.time_4);
            time_5 = itemView.findViewById(R.id.time_5);
            time_6 = itemView.findViewById(R.id.time_6);
            int colorToCompare = Color.parseColor("#003399");
            time_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        if (checkTime(time_1.getText().toString())  && get_chosen_slot_by_time_floor(time_1.getText().toString(), floor.getText().toString())) {
                            Log.d("MYGOT", "it is available");
                            if (user_does_not_laundry()) {
                                chooseSlot((String) floor.getText(), (String) time_1.getText());
                            }}
                        else {
                            Toast.makeText(context, "Not available", Toast.LENGTH_LONG).show();
                            Log.d("MYGOT", "it isnt available");
                        }}
            });
            time_2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkTime(time_2.getText().toString())  && get_chosen_slot_by_time_floor(time_2.getText().toString(), floor.getText().toString())) {
                        Log.d("MYGOT", "it is available");
                        if (user_does_not_laundry()) chooseSlot((String) floor.getText(), (String) time_2.getText());
                       } else {
                        Toast.makeText(context, "Not available", Toast.LENGTH_LONG).show();
                        Log.d("MYGOT", "it isnt available");
                    }}
            });
            time_3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ( checkTime(time_3.getText().toString())  && get_chosen_slot_by_time_floor(time_3.getText().toString(), floor.getText().toString())) {
                        Log.d("MYGOT", "it is available");
                        if (user_does_not_laundry()) chooseSlot((String) floor.getText(), (String) time_3.getText());
                     } else {
                        Toast.makeText(context, "Not available", Toast.LENGTH_LONG).show();
                        Log.d("MYGOT", "it isnt available");
                    }}
            });
            time_4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkTime(time_4.getText().toString())  && get_chosen_slot_by_time_floor(time_4.getText().toString(), floor.getText().toString())) {
                        Log.d("MYGOT", "it is available");
                        if (user_does_not_laundry()) chooseSlot((String) floor.getText(), (String) time_4.getText());
                    } else {
                        Toast.makeText(context, "Not available", Toast.LENGTH_LONG).show();
                        Log.d("MYGOT", "it isnt available");
                    }}
            });
            time_5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkTime(time_5.getText().toString())  && get_chosen_slot_by_time_floor(time_5.getText().toString(), floor.getText().toString())) {
                        Log.d("MYGOT", "it is available");
                        if (user_does_not_laundry()) chooseSlot((String) floor.getText(), (String) time_5.getText());
                   } else {
                        Toast.makeText(context, "Not available", Toast.LENGTH_LONG).show();
                        Log.d("MYGOT", "it isnt available");
                    }}
            });
            time_6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkTime(time_6.getText().toString()) && get_chosen_slot_by_time_floor(time_6.getText().toString(), floor.getText().toString())) {
                        Log.d("MYGOT", "it is available");
                       if (user_does_not_laundry()) chooseSlot((String) floor.getText(), (String) time_6.getText());
                    } else {
                        Toast.makeText(context, "Not available", Toast.LENGTH_LONG).show();
                        Log.d("MYGOT", "it isnt available");
                    }}
            });
        }

        public Boolean get_chosen_slot_by_time_floor(String time, String floor){
            for (String id: wm_id_floor.keySet()) {
                if (String.valueOf(wm_id_floor.get(id))==floor) {
                    for (Slots s : slotsArray){
                        if (String.valueOf(s.wm_id).equals(id) && String.valueOf(s.start.substring(s.start.length()-5)).equals(time)) return false;
                    }
                }
            }
            return true;
        }


        public Boolean user_does_not_laundry() {
            for (Slots s: slotsArray ){
                if (s.user_id.equals(userId)) {
                    Toast.makeText(this.context, "you alredy do laundry today", Toast.LENGTH_LONG).show();
                    Log.d("laundry", "false");
                    return false;
                }
            }
            Log.d("laundry", "true");
            return true;
        }

        public Boolean checkTime(String text) {
            TimeZone permTimeZone = TimeZone.getTimeZone("Asia/Yekaterinburg");
            Calendar calendar = Calendar.getInstance(permTimeZone); // Устанавливаем часовой пояс при создании календаря
            SimpleDateFormat f1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            f1.setTimeZone(permTimeZone); // Устанавливаем часовой пояс для форматирования
            Log.d("time", f1.format(calendar.getTime()));
            String str_date = f1.format(calendar.getTime()).substring(f1.format(calendar.getTime()).length()-5);
            if (text.toString().compareTo(str_date)>0) return true;
            else return false;
        }


        public void chooseSlot(String floor, String time){
            for (String id: wm_id_floor.keySet()) {
                if (String.valueOf(wm_id_floor.get(id))==floor) {
                    Slots s = new Slots();
                    TimeZone permTimeZone = TimeZone.getTimeZone("Asia/Yekaterinburg");
                    Calendar calendar = Calendar.getInstance(permTimeZone); // Устанавливаем часовой пояс при создании календаря
                    SimpleDateFormat f1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    f1.setTimeZone(permTimeZone); // Устанавливаем часовой пояс для форматирования
                    Log.d("time", f1.format(calendar.getTime()));
                    String str_date = f1.format(calendar.getTime());
                    s.start = str_date.substring(0,str_date.length()-5)+time;
                    Log.d("start", s.start);
                    s.wm_id = Integer.parseInt(id);
                    s.user_id = userId;
                    WMDataBaseSlots.child(time+floor+String.valueOf(dormId)).setValue(s);
                }
            }
        }


        public void bind(final ScheduleItem data) {
            floor.setText(String.valueOf(data.getFloor()));
            time_1.setBackgroundColor(Color.parseColor(data.getTime1()));
            time_2.setBackgroundColor(Color.parseColor(data.getTime2()));
            time_3.setBackgroundColor(Color.parseColor(data.getTime3()));
            time_4.setBackgroundColor(Color.parseColor(data.getTime4()));
            time_5.setBackgroundColor(Color.parseColor(data.getTime5()));
            time_6.setBackgroundColor(Color.parseColor(data.getTime6()));
        }

    }



}