package com.example.clase12;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == 2) {
                subirArchivoConPutFile(null);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            subirArchivo(data.getData());

        }

    }

    UploadTask task = null;
    ProgressBar progressBar = null;
    int i = 5;

    public void subirArchivo(Uri uri) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        Log.d("infoApp", uri.getPath());

        storageReference.child("archivo.jpg")
                .putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d("infoApp", "subida exitosa");
                        i++;

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("infoApp", "error en la subida");
                        e.printStackTrace();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                        long bytesTransferred = snapshot.getBytesTransferred();
                        long totalByteCount = snapshot.getTotalByteCount();

                        int progreso = (int) Math.round((100.0 * bytesTransferred) / totalByteCount);

                        Log.d("infoApp", "progreso: " + progreso + "%");

                    }
                });
    }

    public void subirArchivoConPutFile(View view) {

        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permission == PackageManager.PERMISSION_GRANTED) {
            //subir archivo a firebase storage
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();

            progressBar = findViewById(R.id.progressBar);

            File externalStoragePublicDirectory =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);

            File file = new File(externalStoragePublicDirectory, "Clase 12 - Firebase Storage.pdf");

            Uri uri = Uri.fromFile(file);

            StorageMetadata storageMetadata = new StorageMetadata.Builder()
                    .setCustomMetadata("autor", "Stuardo Lucho")
                    .setCustomMetadata("clase", "12")
                    .build();

            task = storageReference
                    .child("documentos").child("Firebase Storage" + i + ".pdf")
                    .putFile(uri, storageMetadata);

            progressBar.setProgress(0);


            task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d("infoApp", "subida exitosa");
                    i++;
                }
            });
            task.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("infoApp", "error en la subida");
                    e.printStackTrace();
                }
            });
            task.addOnCanceledListener(new OnCanceledListener() {
                @Override
                public void onCanceled() {
                    Log.d("infoApp", "cancelado");
                }
            });
            task.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                    long bytesTransferred = snapshot.getBytesTransferred();
                    long totalByteCount = snapshot.getTotalByteCount();

                    int progreso = (int) Math.round((100.0 * bytesTransferred) / totalByteCount);

                    Log.d("infoApp", "progreso: " + progreso + "%");

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        progressBar.setProgress(progreso, true);
                    } else {
                        progressBar.setProgress(progreso);
                    }
                }
            });
            task.addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onPaused(@NonNull UploadTask.TaskSnapshot snapshot) {
                    Log.d("infoApp", "pauseado");
                }
            });

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (task != null) {
            task.cancel();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (task != null && task.isInProgress()) {
            task.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (task != null && task.isPaused()) {
            task.resume();
        }
    }

    public void subirImagen(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Seleccionar archivo"), 1);

    }

}