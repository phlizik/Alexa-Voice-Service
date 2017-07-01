package com.game.dhanraj.myownalexa.Alarm;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.game.dhanraj.myownalexa.DatabaseForAlarmAndTimer.DataBase;
import com.game.dhanraj.myownalexa.R;

/**
 * Created by Dhanraj on 10-06-2017.
 */

public class AlarmService extends Service {

    private MediaPlayer mediaPlayer;
    private int startId;
    private DataBase db;
    private AlarmManager alarmManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        int fakeId=0;
        db = new DataBase(AlarmService.this);
        alarmManager =  (AlarmManager) getSystemService(ALARM_SERVICE);
       // this.startId=0;
        String state = null;
        if(intent.getExtras().getString("extra")!=null)
         state = intent.getExtras().getString("extra");



      //  assert state !=null;
        if(state.equals("alarm on"))
            fakeId=1;
        else
            fakeId=0;

        if(fakeId==0)
        {
            if(mediaPlayer!=null)
            {
                mediaPlayer.stop();
                mediaPlayer.reset();
            }

            long idtocancelled = db.getTime();
            Intent i = new Intent(AlarmService.this, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(AlarmService.this,(int)(idtocancelled/10000),i,PendingIntent.FLAG_CANCEL_CURRENT);

            alarmManager.cancel(pendingIntent);

            db.deleteTime(idtocancelled);


            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(AlarmService.this, "Alarm has been cancelled !", Toast.LENGTH_SHORT).show();
                }
            });

        }
        if(fakeId==1){
            mediaPlayer = MediaPlayer.create(this, R.raw.alarm);
            mediaPlayer.start();

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            Intent i = new Intent(AlarmService.this, AlarmReceiver.class);
            i.putExtra("alarm","alarm off");
            //FLAG_UPDATE_CURRENT use kia tha
            long idtocancelled = db.getTime();
            PendingIntent pendingIntent = PendingIntent.getBroadcast(AlarmService.this,(int)(idtocancelled/10000),i,PendingIntent.FLAG_CANCEL_CURRENT);


            Notification notification_popu = new Notification.Builder(this)
                    .setContentTitle("Turn off the alarm!!")
                    .setContentText("Click me !")
                    .setSmallIcon(R.drawable.ic_alarm_off_black_24dp)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build();


            notificationManager.notify(0,notification_popu);
        }

        return START_NOT_STICKY;
    }
}