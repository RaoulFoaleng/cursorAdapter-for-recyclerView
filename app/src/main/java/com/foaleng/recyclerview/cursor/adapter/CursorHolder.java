package com.foaleng.recyclerview.cursor.adapter;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public abstract class CursorHolder extends RecyclerView.ViewHolder{
    public CursorHolder(View itemView) {
        super(itemView);
    }

    public abstract void bind(Cursor cursor);
}
