package com.codinghelper.digabin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.codinghelper.digabin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class editProfile extends AppCompatActivity {
    private Button btn_choose;
    private Button btn_upload;
    private EditText status;
    private ImageButton status_btn;
    private EditText name;
    private ImageButton namebtn;
    private ImageView imageView;
    private Uri filePath,resultUri;
    private final int PICK_IMAGE_REQUEST=22;
    private final int GALLERY_ACTIVITY_CODE=200;
    private final int RESULT_CROP=400;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        btn_choose=findViewById(R.id.chooseImg);
        user=FirebaseAuth.getInstance().getCurrentUser();
        reference=FirebaseDatabase.getInstance().getReference();
        btn_upload=findViewById(R.id.uploadImg);
        imageView=findViewById(R.id.imgView);
        status=findViewById(R.id.edit_status);
        status_btn=findViewById(R.id.status_confirm_bt);
        name=findViewById(R.id.edit_name);
        namebtn=findViewById(R.id.name_ok);
        storage=FirebaseStorage.getInstance();
        storageReference=storage.getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        btn_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectImage();
            }
        });
        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });
        status_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String updatestatus=status.getText().toString();
                if (TextUtils.isEmpty(updatestatus)) {
                    status.setError("enter new status");
                    status.setFocusable(true);
                    return;
                }else{
                    reference.child("User").child(user.getUid()).child("userstatus").setValue(updatestatus)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(editProfile.this,"Status Uploded!!",Toast.LENGTH_SHORT).show();
                                    }else {
                                        String message=task.getException().getMessage();
                                        Toast.makeText(editProfile.this,"Error:"+message,Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                }

            }
        });
        namebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String updateName=name.getText().toString();
                if (TextUtils.isEmpty(updateName)) {
                    status.setError("enter name");
                    status.setFocusable(true);
                    return;
                }else{

                    reference.child("User").child(user.getUid()).child("userName").setValue(updateName)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(editProfile.this,"Name Uploded!!",Toast.LENGTH_SHORT).show();
                                    }else {
                                        String message=task.getException().getMessage();
                                        Toast.makeText(editProfile.this,"Error:"+message,Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                }

            }
        });


    }
    private void SelectImage(){
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(
                    Intent.createChooser(
                            intent,"select Image from here..."),1);

    }
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==1 && resultCode==RESULT_OK && data!=null) {
            filePath = data.getData();
        }

        try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
    private void  uploadImage(){
        if(filePath!=null){
            final ProgressDialog progressDialog=new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            final StorageReference ref=storageReference.child("images/"+ UUID.randomUUID().toString());
            ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Toast.makeText(editProfile.this,"Image Uploaded!!",Toast.LENGTH_SHORT).show();
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            reference.child("User").child(user.getUid()).child("imageUrl").setValue(String.valueOf(uri))
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(editProfile.this,"Finally completed!!",Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        }
                    });
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(editProfile.this,"Failed!"+e.getMessage(),Toast.LENGTH_SHORT).show();

                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress=(100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploaded"+(int)progress+"%");
                        }
                    });
        }
    }
}
