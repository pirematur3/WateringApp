package com.example.wateringapp;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import com.bumptech.glide.Glide;
import com.example.wateringapp.databinding.EditFragmentBinding;
import com.example.wateringapp.models.PlantModel;
import com.example.wateringapp.repository.AppDatabase;
import com.example.wateringapp.repository.PlantDao;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import static android.app.Activity.RESULT_OK;

public class EditPlantFragment extends Fragment {

    private EditFragmentBinding binding;

    private String selectedRoutine = "";
    private int selectedRoutineId = 0;
    private String photoUrlEdit;
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = EditFragmentBinding.inflate(inflater,container,false);


        Bundle args = getArguments();
        int uid = args.getInt("uid");
        String plantName = args.getString("plantName");
        String startDate = args.getString("startDate");
        String startTime = args.getString("startTime");
        String routine = args.getString("routine");
        int routineId = args.getInt("routineId");
        String photoUrl = args.getString("photoUrl");

        photoUrlEdit = photoUrl;

        PlantModel plantModel = new PlantModel(uid,plantName,startDate,startTime,routine,routineId,photoUrl);

        ContextWrapper cw = new ContextWrapper(getContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File file = new File(directory, plantModel.photoUrl);

        binding.editTextNameEdit.setText(plantModel.plantName);
        binding.editTextTimePickerEdit.setText(plantModel.startTime);
        binding.editTextDatePickerEdit.setText(plantModel.startDate); // tarih, periyot çekmeye bak
        binding.imageButtonEdit.setImageDrawable(Drawable.createFromPath(file.toString()));

        AppDatabase db = Room.databaseBuilder(getContext(),
                AppDatabase.class, "ozkan").allowMainThreadQueries().build();
        PlantDao plantDao = db.userDao();

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.CAMERA
            }, 100 );
        }

        binding.imageButtonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                builder.setMessage("Fotoğraf Ekleyiniz");


                builder.setPositiveButton("kamera", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Open camera
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent,100);

                    }

                });

                builder.setNegativeButton("galeri", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Open gallery
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                                    == PackageManager.PERMISSION_DENIED){
                                //permission not granted, request it
                                String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                                requestPermissions(permissions, PERMISSION_CODE);
                            }else {
                                //if permission already granted, calling picking image from gallery method
                                pickImageFromGallery();
                            }
                        }else {
                            pickImageFromGallery();
                        }
                    }
                });
                builder.create().show();
            }
        });


        binding.buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                PlantModel model = new PlantModel(plantModel.getUid(),binding.editTextNameEdit.getText().toString(),
                        binding.editTextDatePickerEdit.getText().toString(),
                        binding.editTextTimePickerEdit.getText().toString(),
                        selectedRoutine,
                        selectedRoutineId,
                        photoUrlEdit);
                if (binding.editTextNameEdit.getText().toString() == null || binding.editTextTimePickerEdit.getText().toString() == null ||
                        binding.editTextDatePickerEdit.getText().toString() == null || selectedRoutine == null || photoUrlEdit == null){
                    Toast.makeText(getContext(), "Eksik bilgi",Toast.LENGTH_SHORT).show();
                    return;
                }
                plantDao.update(model);

                PlantListFragment plantListFragment = new PlantListFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentHolder, plantListFragment, "null")
                        .commit();
            }
        });

        binding.editTextDatePickerEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog;
                datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month+1;
                        String date = dayOfMonth+"/"+month+"/"+year;
                        binding.editTextDatePickerEdit.setText(date);
                    }
                },year,month,day);
                datePickerDialog.setTitle("Tarih Seçiniz");
                datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE,"Ayarla",datePickerDialog);
                datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE,"İptal",datePickerDialog);
                datePickerDialog.show();
            }
        });

        binding.editTextTimePickerEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance(); // telefondan zaman değerlerini aldık
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog; // burası dialog
                timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        if (minute<10){
                            binding.editTextTimePickerEdit.setText(hourOfDay+":"+"0"+minute);
                        } else {
                            binding.editTextTimePickerEdit.setText(hourOfDay+":"+minute);
                        }
                    }
                },hour,minute,true);
                timePickerDialog.setTitle("Saat Seçiniz");
                timePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE,"Tamam",timePickerDialog);
                timePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE,"İptal",timePickerDialog);

                timePickerDialog.show();
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.plants_routine, R.layout.color_spinner_layout);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        binding.spinnerEdit.setAdapter(adapter);

        binding.spinnerEdit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedRoutine = parent.getItemAtPosition(position).toString();
                selectedRoutineId = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedRoutine = "";
            }
        });

        return binding.getRoot();
    }


    private void pickImageFromGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == 100 ){
            //Get capture image
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            //Set capture image to image view
            updateUrl(bitmap);

            Glide.with(this).load(bitmap).into(binding.imageButtonEdit);



        }else if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE){
            Uri imageUri = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            updateUrl(bitmap);

            Glide.with(this).load(imageUri).into(binding.imageButtonEdit);

        }

    }

    public void updateUrl(Bitmap bitmap){
        ContextWrapper cw = new ContextWrapper(getContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

        Calendar cal = Calendar.getInstance();
        long time = cal.getTimeInMillis();

        String oldPhotoUrl = photoUrlEdit;

        photoUrlEdit = time + ".jpg";

        File file = new File(directory, photoUrlEdit);
        if (!file.exists()) {
            Log.d("path", file.toString());
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();

                File file2 = new File(directory, oldPhotoUrl);
                file2.delete();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {


        super.onViewCreated(view, savedInstanceState);
    }
}
