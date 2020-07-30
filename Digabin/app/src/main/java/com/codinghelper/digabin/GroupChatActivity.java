package com.codinghelper.digabin;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class GroupChatActivity extends AppCompatActivity {
    private ImageButton send_btn;
    private EditText userMessage;
    private ScrollView scrollView;
    private TextView displayText;
    private String currentGroupName;
    private String CurrentUserId,CurrentUserName,CurrentDate,CurrentTime;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference,GroupNameRef,global_message_key;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
       // androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.navigation_toolbar);
       // setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        firebaseAuth= FirebaseAuth.getInstance();
        CurrentUserId=firebaseAuth.getCurrentUser().getUid();
        databaseReference= FirebaseDatabase.getInstance().getReference().child("User");
        currentGroupName=getIntent().getExtras().get("groupName").toString();
        GroupNameRef= FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName);
        actionBar.setTitle(currentGroupName);
        send_btn=(ImageButton)findViewById(R.id.send_text_btn);
        userMessage=(EditText)findViewById(R.id.input_message);
        displayText=(TextView)findViewById(R.id.global_chat_text);
        scrollView=(ScrollView)findViewById(R.id.my_scroll_view);
        GetUserInfo();
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message=userMessage.getText().toString();
                String messageKey=GroupNameRef.push().getKey();
                if (TextUtils.isEmpty(message)) {
                    userMessage.setError("Type something!!");
                    userMessage.setFocusable(true);
                    return;
                }else{
                    Calendar calendar_date= Calendar.getInstance();
                    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("MMM dd, yyyy");
                    CurrentDate=simpleDateFormat.format(calendar_date.getTime());

                    Calendar calendar_time= Calendar.getInstance();
                    SimpleDateFormat simpleTimeFormat=new SimpleDateFormat("hh:mm a");
                    CurrentTime=simpleTimeFormat.format(calendar_time.getTime());

                    HashMap<String, Object> global_message=new HashMap<>();
                    GroupNameRef.updateChildren(global_message);
                    global_message_key=GroupNameRef.child(messageKey);
                    HashMap<String, Object> messageInfo=new HashMap<>();
                    GroupNameRef.updateChildren(messageInfo);
                    HashMap<String, Object> privateMessageInfo=new HashMap<>();
                    privateMessageInfo.put("name",CurrentUserName);
                    privateMessageInfo.put("message",message);
                    privateMessageInfo.put("date",CurrentDate);
                    privateMessageInfo.put("time",CurrentTime);
                    global_message_key.updateChildren(privateMessageInfo);
                    userMessage.setText("");
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);

                }
            }
        });

    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    @Override
    public void onStart(){
        super.onStart();
        GroupNameRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                    DisplayMessages(dataSnapshot);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                    DisplayMessages(dataSnapshot);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void DisplayMessages(DataSnapshot dataSnapshot) {
        Iterator iterator=dataSnapshot.getChildren().iterator();
        while(iterator.hasNext()){
            String charData=(String)((DataSnapshot)iterator.next()).getValue();
            String charMessage=(String)((DataSnapshot)iterator.next()).getValue();
            String charName=(String)((DataSnapshot)iterator.next()).getValue();
            String charTime=(String)((DataSnapshot)iterator.next()).getValue();
            displayText.append(":D"+"("+charName+")"+"\n"+charMessage+"\n"+"----------------------------------------------- "+"\n");
            scrollView.fullScroll(ScrollView.FOCUS_DOWN);
        }
    }
    private void GetUserInfo() {
        databaseReference.child(CurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    CurrentUserName= String.valueOf(dataSnapshot.child("userName").getValue());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
