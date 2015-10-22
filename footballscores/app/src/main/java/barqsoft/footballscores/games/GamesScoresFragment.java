package barqsoft.footballscores.games;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.R;
import barqsoft.footballscores.data.DatabaseContract;
import barqsoft.footballscores.data.GameScoreCursorAdapter;

/**
 * A placeholder fragment containing a simple view.
 */
public class GamesScoresFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final int SCORES_LOADER = 0;
    public GameScoreCursorAdapter mAdapter;
    private String[] fragmentdate = new String[1];
    private int last_selected_item = -1;
    private ListView mScoreList;
    private View emptyView;

    public GamesScoresFragment() {
    }

    public void setFragmentDate(String date) {
        fragmentdate[0] = date;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
//        update_scores();
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mScoreList = (ListView) rootView.findViewById(R.id.scores_list);
        mAdapter = new GameScoreCursorAdapter(getActivity(), null, 0);
        mScoreList.setAdapter(mAdapter);
        getLoaderManager().initLoader(SCORES_LOADER, null, this);
        mAdapter.detail_match_id = MainActivity.selected_match_id;
//        mScoreList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
//            {
//                GameScoreViewHolder selected = (GameScoreViewHolder) view.getTag();
//                mAdapter.detail_match_id = selected.match_id;
//                MainActivity.selected_match_id = (int) selected.match_id;
//                mAdapter.notifyDataSetChanged();
//            }
//        });
        emptyView = rootView.findViewById(R.id.error);
        return rootView;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(getActivity(), DatabaseContract.scores_table.buildScoreWithDate(),
                null, null, fragmentdate, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        //Log.v(FetchScoreTask.LOG_TAG,"loader finished");

        int i = 0;
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            i++;
            cursor.moveToNext();
        }
        //Log.v(FetchScoreTask.LOG_TAG,"Loader query: " + String.valueOf(i));
        mAdapter.swapCursor(cursor);
        mScoreList.setEmptyView(emptyView);
        //mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mAdapter.swapCursor(null);
    }


    public void updateView()
    {
        if (getActivity() == null) {
            return;
        }
        getLoaderManager().restartLoader(SCORES_LOADER, null, this);
    }
}
