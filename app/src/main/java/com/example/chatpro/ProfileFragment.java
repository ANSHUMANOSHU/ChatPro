package com.example.chatpro;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.gesture.GestureLibraries;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chatpro.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;


public class ProfileFragment extends Fragment {
    private UserModel model;
    private static final int PROFILE_IMAGE_REQUEST = 1;
    private static final int COVER_IMAGE_REQUEST = 2;
    private Uri uri;
    private File f;
    private ImageView profileImage, coverImage;
    private TextView name, phone, email, dob,status;
    private FloatingActionButton floatingActionButton;
    private Context context;
    private boolean ownProfile;
    private String uid;

    public ProfileFragment(Context context,boolean ownProfile,String uid) {
        this.context = context;
        this.ownProfile = ownProfile;
        this.uid = uid;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profileImage = view.findViewById(R.id.profileImage);
        coverImage = view.findViewById(R.id.coverImage);
        name = view.findViewById(R.id.name);
        phone = view.findViewById(R.id.phone);
        email = view.findViewById(R.id.email);
        dob = view.findViewById(R.id.dob);
        status = view.findViewById(R.id.statustv);
        floatingActionButton = view.findViewById(R.id.fab);

        if(!ownProfile){
            floatingActionButton.setVisibility(View.GONE);
        }
        else{
            floatingActionButton.setVisibility(View.VISIBLE);
        }

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users/" + uid);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    model = dataSnapshot.getValue(UserModel.class);
                    setValues();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] array = {"Update Profile Image", "Update Cover Image", "Update Name", "Update Contact", "Update DOB","Update Status"};
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Choose Option");
                builder.setItems(array, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                updateProfileImage();
                                break;
                            case 1:
                                updateCoverImage();
                                break;
                            case 2:
                                updateName();
                                break;
                            case 3:
                                updateContact();
                                break;
                            case 4:
                                updateDOB();
                                break;
                            case 5:
                                updateStatus();
                                break;

                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        });
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LayoutInflater.from(context).inflate(R.layout.image_layout,null,false);
                TextView textView = view.findViewById(R.id.bigimagename);
                ImageView close , image;
                close = view.findViewById(R.id.close);
                image=view.findViewById(R.id.bigimage);

                final Dialog dialog =new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(view);
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
                if(model==null || model.getProfileimage().isEmpty())
                    image.setImageResource(R.drawable.person_high);
                else
                    Glide.with(context).load(model.getProfileimage()).into(image);
                textView.setText(name.getText().toString());
                dialog.show();
            }
        });

        coverImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LayoutInflater.from(context).inflate(R.layout.image_layout,null,false);
                TextView textView = view.findViewById(R.id.bigimagename);
                ImageView close , image;
                close = view.findViewById(R.id.close);
                image=view.findViewById(R.id.bigimage);

                final Dialog dialog =new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(view);
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
                if(model==null || model.getCoverimage().isEmpty())
                    image.setImageResource(R.drawable.coverimage);
                else
                    Glide.with(context).load(model.getCoverimage()).into(image);
                textView.setText(name.getText().toString());
                dialog.show();
            }
        });

        return view;
    }

    private void updateStatus() {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.status_feed_layout,null,false);
        final EditText editText = v.findViewById(R.id.field);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Update Status");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).setPositiveButton("Set", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                model.setStatus(editText.getText().toString());
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
                reference.child(model.getUid()).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getActivity(), "Status Updated", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }
        });
        if (model != null) {
            editText.setText(model.getStatus());
        }
        builder.setView(v);
        builder.create().show();
    }

    private void updateDOB() {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.layout_datechoose, null);
        final TextView textView = v.findViewById(R.id.date);
        final Button button = v.findViewById(R.id.chooserbtn);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Update Date Of Birth");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).setPositiveButton("Set", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                model.setDate(textView.getText().toString());
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
                reference.child(model.getUid()).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getActivity(), "D O B Updated", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        
                    }
                });
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        textView.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, 2000, 1, 1);

                datePickerDialog.show();
            }
        });
        if (model != null) {
            textView.setText(model.getDate());
        }

        builder.setView(v);
        builder.create().

                show();

    }

    private void updateContact() {
        final EditText editText = new EditText(getActivity());
        editText.setInputType(InputType.TYPE_CLASS_PHONE);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Update Contact");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).setPositiveButton("Set", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                model.setPhone(editText.getText().toString());
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
                reference.child(model.getUid()).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getActivity(), "Contact Updated", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        
                    }
                });
            }
        });
        if (model != null) {
            editText.setText(model.getPhone());
        }
        builder.setView(editText);
        builder.create().show();

    }

    private void updateName() {
        final EditText editText = new EditText(getActivity());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Update Name");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).setPositiveButton("Set", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (editText.getText().toString().isEmpty())
                    Toast.makeText(getActivity(), "Name cannot be Empty", Toast.LENGTH_SHORT).show();
                else {
                    model.setName(editText.getText().toString());
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
                    reference.child(model.getUid()).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getActivity(), "Name Updated", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            
                        }
                    });
                }
            }
        });
        if (model != null) {
            editText.setText(model.getName());
        }
        builder.setView(editText);
        builder.create().show();
    }

    private void updateCoverImage() {
        if (checkStoragePermission()) {
            Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            startActivityForResult(gallery, COVER_IMAGE_REQUEST);
        } else {
            Toast.makeText(getActivity(), "Permission Not Granted", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkStoragePermission() {
        //TODO------storage permission
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PROFILE_IMAGE_REQUEST) {
            uri = data.getData();
            f = null;
            try {
                f = File.createTempFile("temp",".jpg");
            } catch (IOException e) {
                e.printStackTrace();
            }
            UCrop.Options o = new UCrop.Options();
            o.setCircleDimmedLayer(true);
            UCrop.of(uri, Uri.fromFile(f))
                    .withAspectRatio(2, 1)
                    .withMaxResultSize(200, 400)
                    .withOptions(o)
                    .start(((DashBoard) context));
        }
        if (resultCode == RESULT_OK && requestCode == COVER_IMAGE_REQUEST) {
            uri = data.getData();
            final ProgressDialog dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Uploading Cover Image...");
            dialog.setTitle("Connecting...");
            dialog.setCancelable(false);
            dialog.show();
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                StorageReference reference = FirebaseStorage.getInstance().getReference().child("user/cover_" +
                        FirebaseAuth.getInstance().getCurrentUser().getUid());
                reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> taskUri = taskSnapshot.getStorage().getDownloadUrl();
                        while (!taskUri.isSuccessful()) {
                        }
                        String url = taskUri.getResult().toString();
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance()
                                .getCurrentUser().getUid()).child("coverimage");
                        ref.setValue(url);
                        uri = null;
                        dialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        
                        dialog.dismiss();
                    }
                });
            }

        }
    }

    private void updateProfileImage() {
        if (checkStoragePermission()) {
            Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            startActivityForResult(gallery, PROFILE_IMAGE_REQUEST);
        } else {
            Toast.makeText(getActivity(), "Permission Not Granted", Toast.LENGTH_SHORT).show();
        }
    }

    private void setValues() {
        name.setText(model.getName());
        dob.setText(model.getDate());
        email.setText(model.getEmail());
        phone.setText(model.getPhone());
        status.setText(model.getStatus());
        if (getActivity() != null) {
            if (!"".equals(model.getProfileimage()))
                Glide.with(getActivity()).load(model.getProfileimage()).into(profileImage);
            else
                profileImage.setImageResource(R.drawable.person_high);
            if (!"".equals(model.getCoverimage()))
                Glide.with(getActivity()).load(model.getCoverimage()).into(coverImage);
            else
                coverImage.setImageResource(R.drawable.coverimage);
        }
    }


}
