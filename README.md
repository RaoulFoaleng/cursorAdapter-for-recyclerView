# cursorAdapter-for-recyclerView
Custom CursorAdapter to use with RecyclerView

Android Framework provide CursorAdapter object use with a ListView, but does not provide one for RecyclerView.

**[CursorAdapter :](http://http://developer.android.com/reference/android/widget/CursorAdapter.html "CursorAdapter :")** Adapter that exposes data from a Cursor to a ListView widget.

The gold of this tutorial is to provided a custom CursorAdapter for [RecyclerView.java](http://developer.android.com/reference/android/support/v7/widget/RecyclerView.html "RecyclerView.java")

An abstract class with provide an interface _bind(Cursor cursor)_ which give access to the cursor current entry info.

	public abstract class CursorHolder extends RecyclerView.ViewHolder{
    public CursorHolder(View itemView) {
        super(itemView);
    }
    
    /**
     * retrieve data from the cursor to present it to the view
     * @param cursor
     */
    public abstract void bind(Cursor cursor);}

CusorAdapter extend RecyclerView.Adapter and support Cursor object as a dataset.

	public abstract class CursorAdapter extends RecyclerView.Adapter<CursorHolder> {

    private Cursor mCursor;
    ...

    /**
     * query the data in the db
     */
    public abstract void onRequery();

    // Provide a suitable constructor (depends on the kind of dataset)
    public CursorAdapter(Cursor cursor) {
        setCursor(cursor);
    }

    // replace current cursor with the new cursor, and close the old cursor.
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
    ...}

create MyCursorAdapter

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
        }}

set myCursorAdapter to the RecyclerView

	public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;

    ...

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        ...

        // specify an adapter (see also next example)
        mAdapter = new MyCursorAdapter(query());
        mRecyclerView.setAdapter(mAdapter);
    }
    ...}
