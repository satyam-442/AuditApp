package com.example.auditapp;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile_upload extends AppCompatActivity
{
    private CircleImageView UploadUserImage;

    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    private DatabaseReference mUserDatabase;
    private  Button dashbutton;

    private static final int GALLERY_PICK = 1;

    // Storage Firebase
    private StorageReference mImageStorage;
    private Uri imageUri;
    private StorageTask uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_upload);

        mAuth = FirebaseAuth.getInstance();

        UploadUserImage = (CircleImageView) findViewById(R.id.profile_photo_two);
        dashbutton=(Button)findViewById(R.id.dash_button);

        dashbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Profile_upload.this,Dashboard.class));
            }
        });



        UploadUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);


            }
        });


        loadingBar = new ProgressDialog(this);

        String currentUserID = mAuth.getCurrentUser().getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference("Users").child(currentUserID);
        mUserDatabase.keepSynced(true);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final String image = dataSnapshot.child("image").getValue().toString();
                if(!image.equals("default"))
                {
                    Picasso.with(Profile_upload.this).load(image).placeholder(R.drawable.profile).into(UploadUserImage);
                    Picasso.with(Profile_upload.this).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.profile).into(UploadUserImage, new Callback()
                    {
                        @Override
                        public void onSuccess()
                        {

                        }

                        @Override
                        public void onError()
                        {
                            Picasso.with(Profile_upload.this).load(image).placeholder(R.drawable.profile).into(UploadUserImage);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





        mImageStorage = FirebaseStorage.getInstance().getReference();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            imageUri = data.getData();
            if (uploadTask != null && uploadTask.isInProgress())
            {
                Toast.makeText(this, "Upload is in progress", Toast.LENGTH_SHORT).show();
            } else
            {
                uploadImage();
            }
        }
    }

    private void uploadImage() {
        loadingBar.setMessage("Uploading");
        loadingBar.show();
        if (imageUri != null)
        {
            String current_uid = mAuth.getUid();
            final StorageReference fileReference = mImageStorage.child("images").child(current_uid + "." + getFileExtension(imageUri));
            //final StorageReference fileReference = mImageStorage.child("images" + "." + getFileExtension(imageUri));
            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>()
            {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                {
                    if (!task.isSuccessful())
                    {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>()
            {
                @Override
                public void onComplete(@NonNull Task<Uri> task)
                {
                    if (task.isSuccessful())
                    {
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();
                        String current_uid = mAuth.getUid();
                        mUserDatabase = FirebaseDatabase.getInstance().getReference("Users").child(current_uid);
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("image", mUri);
                        mUserDatabase.updateChildren(map);
                        loadingBar.dismiss();
                    }
                    else
                    {
                        String message = task.getException().getMessage();
                        Toast.makeText(Profile_upload.this, "Error Occurred: " + message, Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener()
            {
                @Override
                public void onFailure(@NonNull Exception e)
                {
                    Toast.makeText(Profile_upload.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            });
        }
        else
        {
            Toast.makeText(this, "No Image Selected", Toast.LENGTH_SHORT).show();
        }
    }

    public String getFileExtension(Uri uri)
    {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getMimeTypeFromExtension(contentResolver.getType(uri));
    }

}
