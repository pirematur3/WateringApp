/*
package com.example.wateringapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.bumptech.glide.Glide;
import com.example.wateringapp.databinding.PlantsCardviewBinding;
import com.example.wateringapp.models.PlantModel;
import com.example.wateringapp.repository.AppDatabase;
import com.example.wateringapp.repository.PlantDao;

import java.io.File;
import java.util.List;

public class    PlantsAdapter extends RecyclerView.Adapter<PlantsAdapter.PlantsObjHolder> {


    private Context mContext;
    private List<PlantModel> plantsList;
    public Animation animation1, animation2;
    public PlantModel plant;
    public FragmentManager fragmentManager;

    public PlantsAdapter(Context mContext, FragmentManager fragmentManager) {
        this.mContext = mContext;
        this.fragmentManager = fragmentManager;

    }

    public class PlantsObjHolder extends RecyclerView.ViewHolder{
        public PlantsCardviewBinding binding;


        public PlantsObjHolder(@NonNull View itemView) {
            super(itemView);
            imageViewPlant = itemView.findViewById(R.id.imageViewPlant);
            textViewPlantName = itemView.findViewById(R.id.textViewPlantName);
            textViewTime = itemView.findViewById(R.id.textViewTime);
            buttonWater = itemView.findViewById(R.id.buttonWater);
            buttonOption = itemView.findViewById(R.id.buttonOption);

            animation1 = AnimationUtils.loadAnimation(mContext,R.anim.watering_anim);
            animation2 = AnimationUtils.loadAnimation(mContext,R.anim.card_item_anim);

            itemView.setAnimation(animation2);

            binding.buttonWater.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    binding.buttonWater.startAnimation(animation1);
                }
            });
            binding.buttonOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopupMenu(v);
                }
            });
        }
        AppDatabase db = Room.databaseBuilder(mContext,
                AppDatabase.class, "ozkan").allowMainThreadQueries().build();
        PlantDao plantDao = db.userDao();

        private void showPopupMenu(View view){
            PopupMenu popupMenu = new PopupMenu(view.getContext(),view);
            popupMenu.inflate(R.menu.popup_menu);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    switch (item.getItemId()){  //edit ekranına yolla
                        case R.id.actionEdit:
                            PlantModel plantModel = plantsList.get(getAdapterPosition());
                            com.example.wateringapp.EditPlantFragment editPlantFragment = new com.example.wateringapp.EditPlantFragment();
                            Bundle bundle = new Bundle();
                            bundle.putInt("uid",plantModel.getUid());
                            bundle.putString("plantName",plantModel.getPlantName());
                            bundle.putString("startDate",plantModel.getStartDate());
                            bundle.putString("startTime",plantModel.getStartTime());
                            bundle.putString("routine",plantModel.getRoutine());
                            bundle.putString("photoUrl",plantModel.getPhotoUrl());

                            editPlantFragment.setArguments(bundle);
                            fragmentManager.beginTransaction().replace(R.id.fragmentHolder, editPlantFragment).commit();


                            return true;
                        case R.id.actionDelete:

                            int uid = plantsList.get(getAdapterPosition()).getUid();
                            cancelAlarm(uid);
                            String url = plantsList.get(getAdapterPosition()).getPhotoUrl();
                            plantsList.remove(getAdapterPosition());
                            notifyDataSetChanged();
                            plantDao.delete(uid);

                            ContextWrapper cw = new ContextWrapper(mContext);
                            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
                            File file2 = new File(directory, url);
                            file2.delete();

                            return true;
                        default: return false;
                    }
                }
            });
            popupMenu.show();
        }

    }


    private void cancelAlarm(int id) {
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(mContext, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, id, intent, 0);
        alarmManager.cancel(pendingIntent);
    }

    public void setList(List<PlantModel> plantsList) {
        this.plantsList = plantsList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PlantsObjHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.plants_row,parent,false);
        return new PlantsObjHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull PlantsObjHolder holder, int position) {

        plant = plantsList.get(position);

        holder.binding.textViewPlantName.setText(plant.plantName);
        holder.binding.textViewTime.setText(plant.startTime);

        ContextWrapper cw = new ContextWrapper(mContext);
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File file = new File(directory, plant.photoUrl);
        Glide.with(mContext).load(file.toString()).centerCrop().into(holder.binding.imageViewPlant);



    }
    @Override
    public int getItemCount() {
        return plantsList.size();
    }

}
*/
