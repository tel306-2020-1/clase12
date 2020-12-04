package com.example.clase12;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.ImageCapture;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button button = findViewById(R.id.btnMa2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, MainActivity2.class);
                startActivity(i);
            }
        });

    }

    public void subirArchivoPutStream(View view) {

        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permission == PackageManager.PERMISSION_GRANTED) {
            //subir archivo a firebase storage
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();

            File externalStoragePublicDirectory =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

            File file = new File(externalStoragePublicDirectory, "pucp.jpg");

            try {
                InputStream inputStream = new FileInputStream(file);

                storageReference.child("imagenes/pucpSubido2.jpg").putStream(inputStream)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Log.d("infoApp", "subida exitosa");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("infoApp", "error en la subida");
                                e.printStackTrace();
                            }
                        });

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }

    public void subirArchivoConPutFile(View view) {

        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permission == PackageManager.PERMISSION_GRANTED) {
            //subir archivo a firebase storage
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();

            File externalStoragePublicDirectory =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);

            File file = new File(externalStoragePublicDirectory, "Clase 12 - Firebase Storage.pdf");

            Uri uri = Uri.fromFile(file);

            StorageMetadata storageMetadata = new StorageMetadata.Builder()
                    .setCustomMetadata("autor", "Stuardo Lucho")
                    .setCustomMetadata("clase", "12")
                    .build();

            UploadTask task = storageReference
                    .child("documentos").child("Firebase Storage3.pdf")
                    .putFile(uri, storageMetadata);


            task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d("infoApp", "subida exitosa");
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

                }
            });
            task.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                    long bytesTransferred = snapshot.getBytesTransferred();
                    long totalByteCount = snapshot.getTotalByteCount();

                    double progreso = (100.0 * bytesTransferred) / totalByteCount;

                    Log.d("progreso", String.valueOf(progreso));

                }
            });

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
        }
    }

    public void descargarDocumento(View view) {
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission == PackageManager.PERMISSION_GRANTED) {
            //descarga archivo de firebase storage
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            StorageReference storageReference1 = storageReference.child("documentos/Firebase Storage3.pdf");

            File externalStoragePublicDirectory =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);

            File file = new File(externalStoragePublicDirectory, "Firebase Storage3.pdf");

            storageReference1.getFile(file)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Log.d("infoApp", "descarga exitosa");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("infoApp", "error en la descarga");
                            e.printStackTrace();
                        }
                    });

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 3);
        }
    }

    public void descargarImagenConGlide(View view) {

        StorageReference reference =
                FirebaseStorage.getInstance().getReference().child("imagenes/pucpSubido2.jpg");

        ImageView imageView = findViewById(R.id.imageView);

        Glide.with(this).load(reference).into(imageView);
    }

    public void listarArchivos(View view) {
        StorageReference reference = FirebaseStorage.getInstance().getReference();

        reference.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        int cantidadElementos = listResult.getItems().size();
                        Log.d("infoApp", "cantidad de elementos: " + cantidadElementos);

                        Log.d("infoApp", "carpetas: " + listResult.getPrefixes().size());

                        for (StorageReference ref : listResult.getItems()) {
                            Log.d("infoApp", "elemento: " + ref.getName());

                        }
                        /*for (StorageReference ref : listResult.getPrefixes()) {
                            Log.d("infoApp", "carpeta: " + ref.getName());
                            ref.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                                @Override
                                public void onSuccess(ListResult listResult) {
                                    for (StorageReference ref2 : listResult.getItems()) {
                                        Log.d("infoApp", "elemento: " + ref2.getName());
                                    }
                                }
                            });
                        }*/


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        Log.d("infoApp", "Error al listar");
                    }
                });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == 1) {
                subirArchivoPutStream(null);
            } else if (requestCode == 2) {
                subirArchivoConPutFile(null);
            } else if (requestCode == 3) {
                descargarDocumento(null);
            }
        }
    }


    public void buscarArchivo(View view) {

        EditText editText = findViewById(R.id.editTextFileName);
        String textoABuscar = editText.getText().toString();

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        Task<StorageMetadata> metadataTask = storageReference.child(textoABuscar).getMetadata();

        metadataTask.addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
                Log.d("infoApp", "existe! - on Success");
                Toast.makeText(MainActivity.this, "existe el archivo", Toast.LENGTH_SHORT).show();

                Log.d("infoApp", "updated:" + storageMetadata.getUpdatedTimeMillis());
                Timestamp stamp = new Timestamp(storageMetadata.getCreationTimeMillis());
                Date date = new Date(stamp.getTime());
                Log.d("infoApp", "created:" + date);
            }
        });
        metadataTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("infoApp", e.getMessage());
                if (e.getMessage().equals("Object does not exist at location.")) {
                    Toast.makeText(MainActivity.this, "archivo no encontrado", Toast.LENGTH_SHORT).show();
                    Log.d("infoApp", "error al obtener la metadata");
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

}