package app.custom_notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RemoteViews;

public class MainActivity extends AppCompatActivity {


    private NotificationCompat.Builder builder;
    private NotificationManager notificationManager;
    private int notification_id; // Cada notificação tem um id unico
    private RemoteViews remoteViews; // Permite criar um layout customizado e combinar com a notificação
    private Context context; // Apenas para evitar usar o metodo getApplication o tempo todo


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        builder = new NotificationCompat.Builder(this);

        remoteViews = new RemoteViews(getPackageName(), R.layout.custom_notification);
        remoteViews.setImageViewResource(R.id.notif_icon, R.mipmap.ic_launcher); // Alterar a imagem do ImageView
        remoteViews.setTextViewText(R.id.notif_title, "Novo Texto"); // Alterar o texto do TextView
        remoteViews.setProgressBar(R.id.progressBar, 100, 40, true);

        // Como não estamos no contexto da activity não podemos criar um setOnClickListener para o botão
        // é necessario utilizar o broadcast receiver que roda em background mesmo que a activity não esteja
        // em foreground

        // Exibe uma notificação
        findViewById(R.id.button_show_notif).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                notification_id = (int) System.currentTimeMillis(); // Geração de um id unico

                // Criação de um filtro para o broadcast [o broadcast so esta escutando intents com o filtro 'button_click']
                Intent button_intent = new Intent("button_click");
                button_intent.putExtra("id", notification_id); // Passando o id(unico) da notificação

                // Para fazer o broadcast desse intent devemos chamar o PendingIntent
                PendingIntent button_pending_event = PendingIntent.getBroadcast(context, notification_id, button_intent, 0);
                // Lingando o PendingIntent ao botão
                remoteViews.setOnClickPendingIntent(R.id.button,button_pending_event);

                // Criação de uma nova notificação e ligação do remote view a esta notificação
                // Se o usuario clicar na notificação em algum lugar que não seja no botão, a activity aparecerá
                Intent notification_intent = new Intent(context, MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notification_intent, 0);

                builder.setSmallIcon(R.mipmap.ic_launcher)
                        .setAutoCancel(true) // Remover a notificação quando ela é clicada
                        .setCustomBigContentView(remoteViews)
                        .setContentIntent(pendingIntent);

                notificationManager.notify(notification_id,builder.build());
            }
        });


    }

}
