package com.example.spider_man.roads360;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class NewUploadImage extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST =243;
    private FusedLocationProviderClient client;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private Geocoder geocoder;
    private double latitude, longtitude;
    public static String address;
    List<Address> addresses;

    Button btnUpload;
    ImageView imgLoc, imgTime, imgDate, imgChoose, imageView;
    TextView tvlocation;

    EditText etDetailLoc;
    //uri to store file
    private Uri filePath;

    //firebase objects
    private StorageReference storageReference;
    private DatabaseReference mDatabase;
    public static String currentTime, currentDate, avgTrafficSituation, trafficHours, trafficDays,currentLocation, detailsLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_upload_image);

        etDetailLoc = (EditText)findViewById(R.id.detailsLocation);
        //detailsLocation = (EditText)fin

        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        mDatabase = FirebaseDatabase.getInstance().getReference("uploads");

        btnUpload = (Button)findViewById(R.id.button);

        imageView = (ImageView)findViewById(R.id.imgView);
        imgChoose = (ImageView)findViewById(R.id.imgChoose);
        imgChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
            }
        });


        location();
        trafficDayFinder();
        trafficHourFinder();
        trafficSituation();
        time();
        date();
        addListenerToCheckBox();


    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    public String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
    private void uploadFile() {
        //checking if file is available
       // Toast.makeText(this, "HI", Toast.LENGTH_SHORT).show();
        if (filePath != null) {
            //displaying progress dialog while image is uploading
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.show();

            //getting the storage reference old
            //StorageReference sRef = storageReference.child(Constant.STORAGE_PATH_UPLOADS + System.currentTimeMillis() + "." + getFileExtension(filePath));

            StorageReference sRef = storageReference.child( System.currentTimeMillis() + "." + getFileExtension(filePath));


            //adding the file to reference
            sRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //dismissing the progress dialog
                            progressDialog.dismiss();

                            //displaying success toast
                            Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();

                            //creating the upload object to store uploaded image details
                            Upload upload = new Upload(currentLocation, taskSnapshot.getDownloadUrl().toString(), currentDate,currentTime,trafficHours,trafficDays,avgTrafficSituation,detailsLocation);

                            //adding an upload to firebase database
                            String uploadId = mDatabase.push().getKey();
                            mDatabase.child(uploadId).setValue(upload);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //displaying the upload progress
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
        } else {
            Toast.makeText(this, "No File Selected", Toast.LENGTH_SHORT).show();
            //display an error if no file is selected
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }



    public void date()
    {
        imgDate = (ImageView)findViewById(R.id.imgVDate);
        imgDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String daysArray[] = {"Sunday","Monday","Tuesday", "Wednesday","Thursday","Friday", "Saturday"};
                Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_WEEK);

                String currentDay = daysArray[day-1];
                currentDate = currentDay;
                TextView txtDate = (TextView)findViewById(R.id.txtVDate);
                txtDate.setText(currentDay);
            }
        });


    }

    private void time() {
        imgTime = (ImageView)findViewById(R.id.imgVTime);
        imgTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                String time = format.format(calendar.getTime());
                currentTime = time;
                TextView txtTime = (TextView)findViewById(R.id.txtVTime);
                txtTime.setText(time);
            }
        });
    }



    public void location ()
    {
        imgLoc = (ImageView) findViewById(R.id.imgLocation);
        imgLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(NewUploadImage.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(NewUploadImage.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                client.getLastLocation().addOnSuccessListener(NewUploadImage.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(android.location.Location location) {
                        if (location != null)
                        {
                            TextView tvLoc = (TextView)findViewById(R.id.textViewlocation);
                            tvLoc.setText(address);
                            Toast.makeText(NewUploadImage.this, address, Toast.LENGTH_SHORT).show();
                            currentLocation = address;

                        }

                    }
                });
            }
        });
        requestPermission();
        client = LocationServices.getFusedLocationProviderClient(this);
        address = "";
        geocoder = new Geocoder(this, Locale.getDefault());
        client = LocationServices.getFusedLocationProviderClient(this);
        currentLocationFormat();
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {

                for (android.location.Location location : locationResult.getLocations()) {
                    latitude = location.getLatitude();
                    longtitude = location.getLongitude();
                    show();
                }
            }
        };
    }
    public void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 1);
    }
    @Override
    protected void onResume() {
        super.onResume();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        client.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    @Override
    protected void onPause() {
        client.removeLocationUpdates(locationCallback);
        super.onPause();
    }
    private void show() {

        try {
            addresses = geocoder.getFromLocation(latitude,longtitude,1);
            address = addresses.get(0).getAddressLine(0);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private void currentLocationFormat() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public void trafficHourFinder()
    {
        Spinner SpinnerDay = findViewById(R.id.SpinnerHour);
        ArrayAdapter<CharSequence> Houradapter = ArrayAdapter.createFromResource(this,R.array.Hours,android.R.layout.simple_spinner_item);
        Houradapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpinnerDay.setAdapter(Houradapter);
        SpinnerDay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String trafficHour = adapterView.getItemAtPosition(i).toString();
                trafficHours = trafficHour;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }
    public void trafficDayFinder()
    {
        Spinner SpinnerDay = findViewById(R.id.SpinnerDay);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.Days,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpinnerDay.setAdapter(adapter);
        SpinnerDay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String trafficDay = adapterView.getItemAtPosition(i).toString();
                trafficDays= trafficDay;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
    public void addListenerToCheckBox()
    {
        CheckBox c1 = (CheckBox)findViewById(R.id.cb1);
        CheckBox c2 = (CheckBox)findViewById(R.id.cb2);
        CheckBox c3 = (CheckBox)findViewById(R.id.cb3);
        c1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cb1 = "Heavy traffic";
                avgTrafficSituation = cb1;
            }
        });

        c2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cb1 = "Moderate traffic";
                avgTrafficSituation = cb1;
            }
        });
        c3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cb1 = "Lite traffic";
                avgTrafficSituation = cb1;
            }
        });

    }
    public void trafficSituation()
    {

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                detailsLocation = etDetailLoc.getText().toString();
                uploadFile();

            }
        });

    }

}
