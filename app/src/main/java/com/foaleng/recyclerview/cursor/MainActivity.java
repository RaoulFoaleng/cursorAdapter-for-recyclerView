package com.foaleng.recyclerview.cursor;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.foaleng.recyclerview.cursor.adapter.CursorAdapter;
import com.foaleng.recyclerview.cursor.adapter.CursorHolder;
import com.foaleng.recyclerview.cursor.content.Data;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fillDb();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);


        // specify an adapter (see also next example)
        mAdapter = new MyCursorAdapter(query());
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(Data.Entry.COLUMN_NAME_VALUE, "row " + mAdapter.getItemCount());
            getContentResolver().insert(Data.Entry.CONTENT_URI, contentValues);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * fill up the db with entries if empty
     * */
    private void fillDb() {
        ContentResolver cr = getContentResolver();
        Cursor cursor = cr.query(Data.Entry.CONTENT_URI, null, null, null, null);
        int count = 0;
        if (cursor != null) {
            try {
                count = cursor.getCount();
            } finally {
                cursor.close();
            }
        }

        if (count == 0) {
            ContentValues[] contentValues = new ContentValues[50];
            for (int i = 0; i < contentValues.length; i++) {
                contentValues[i] = new ContentValues();
                contentValues[i].put(Data.Entry.COLUMN_NAME_VALUE, "row " + i);
            }
            cr.bulkInsert(Data.Entry.CONTENT_URI, contentValues);
        }
    }

    /**
     * query a new Cursor from the db, this need to be done in a background thread
     * @return
     */
    private Cursor query() {
        grantUriPermission(getPackageName(), Data.Entry.CONTENT_URI, 0);
        return getContentResolver().query(Data.Entry.CONTENT_URI, null, null, null, null);
    }

    public class MyCursorAdapter extends CursorAdapter {

        public class MyCursorHolder extends CursorHolder {
            // each data item is just a string in this case
            public TextView mTextView;

            public MyCursorHolder(TextView v) {
                super(v);
                mTextView = v;
            }

            @Override
            public void bind(Cursor cursor) {
                mTextView.setText(cursor.getString(1));
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public MyCursorAdapter(Cursor cursor) {
            super(cursor);
        }

        @Override
        public void onRequery() {
            Cursor cursor = query();
            //replace cursor
            swipe(cursor);
        }

        // Create new views (invoked by the layout manager)
        @Override
        public CursorHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
            // create a new view
            TextView v = (TextView) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.my_text_view, parent, false);
            CursorHolder vh = new MyCursorHolder(v);
            return vh;
        }
    }
}
