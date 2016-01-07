package com.foaleng.recyclerview.cursor.adapter;

import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;

public abstract class CursorAdapter extends RecyclerView.Adapter<CursorHolder> {

    private Cursor mCursor;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    /**
     * query the data in the db
     */
    public abstract void onRequery();

    // Provide a suitable constructor (depends on the kind of dataset)
    public CursorAdapter(Cursor cursor) {
        setCursor(cursor);
    }

    public void swipe(Cursor cursor) {
        if (mCursor != null && !mCursor.isClosed()) {
            mCursor.unregisterContentObserver(mContentObserver);
            mCursor.close();
            mCursor = null;
        }
        setCursor(cursor);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(CursorHolder holder, int position) {
        mCursor.moveToPosition(position);
        holder.bind(mCursor);
    }

    private void setCursor(Cursor cursor) {
        mCursor = cursor;
        mCursor.registerContentObserver(mContentObserver);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (mCursor == null || mCursor.isClosed()) {
            return 0;
        }
        return mCursor.getCount();
    }

    private ContentObserver mContentObserver = new ContentObserver(mHandler) {
        @Override
        public void onChange(boolean selfChange) {
            onRequery();
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            onRequery();
        }
    };
}
