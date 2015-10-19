package barqsoft.footballscores.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import barqsoft.footballscores.R;
import barqsoft.footballscores.data.DatabaseContract;
import barqsoft.footballscores.data.GameScoreCursorAdapter;
import barqsoft.footballscores.models.FootballLeagueGames;
import barqsoft.footballscores.models.Match;
import barqsoft.footballscores.utils.Constants;
import barqsoft.footballscores.utils.Utilities;

public class FootballScoresWidgetProvider extends AppWidgetProvider {

    public FootballScoresWidgetProvider()
    {
        super();
    }


    @Override
    public void onReceive(Context context, Intent intent)
    {
        super.onReceive(context, intent);
        if(intent.getAction() != null) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context.getPackageName(), FootballScoresWidgetProvider.class.getName()));
            if (intent.getAction().equalsIgnoreCase(Constants.SYNC_FINISHED)) {
                for (int i = 0; i < appWidgetIds.length; i++) {
                    Intent collectionService = new Intent(context, FootballWidgetListService.class);
                    collectionService.setData(Uri.parse(collectionService.toUri(Intent.URI_INTENT_SCHEME)));
                    collectionService.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
                    RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.appwidget_main);
                    rv.setRemoteAdapter(R.id.scores_list, collectionService);
                    appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
                }
            }
        }
    }

    @Override
    public void onEnabled(Context context)
    {
        super.onEnabled(context);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
    {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        //update each widget user has created
        for (int i = 0; i < appWidgetIds.length; i++) {
            Intent collectionService = new Intent(context, FootballWidgetListService.class);
            collectionService.setData(Uri.parse(collectionService.toUri(Intent.URI_INTENT_SCHEME)));
            collectionService.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.appwidget_main);
            rv.setRemoteAdapter(R.id.scores_list, collectionService);
            //todo empty view
            appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
        }


    }
}
