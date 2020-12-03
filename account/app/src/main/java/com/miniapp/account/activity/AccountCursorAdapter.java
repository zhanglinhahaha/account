package com.miniapp.account.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.miniapp.account.LogUtil;
import com.miniapp.account.R;
import com.miniapp.account.db.AccountItemDb;

/**
 * Created by zl on 20-12-2.
 */
public class AccountCursorAdapter extends SimpleCursorAdapter {
    private static final String TAG = "AccountCursorAdapter";
    private Cursor mCursor = null;
    private Context mContext = null;
    private LayoutInflater mInflater = null;
    private View.OnClickListener mOnClickListener = null;
    private ViewHolder mViewHolder = null;

    public AccountCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, View.OnClickListener listener) {
        super(context, layout, c, from, to);
        mCursor = c;
        mContext = context;
        mOnClickListener = listener;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.row_account, parent, false);
            mViewHolder = new ViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        try {
            mCursor.moveToPosition(position);

            String name = mCursor.getString(mCursor.getColumnIndex(AccountItemDb.ACCOUNT_ITEM_USERNAME));
            String comment = mCursor.getString(mCursor.getColumnIndex(AccountItemDb.ACCOUNT_ITEM_COMMENT));
            String date = mCursor.getString(mCursor.getColumnIndex(AccountItemDb.ACCOUNT_ITEM_DATE));
            double price = mCursor.getDouble(mCursor.getColumnIndex(AccountItemDb.ACCOUNT_ITEM_PRICE));
            LogUtil.d(TAG, " getview pos =" + position + " ,name  = " + name + ", comment = " + comment + ", price = " + price);
            mViewHolder.txtViewName.setText(name);
//            mViewHolder.txtViewComment.setText(comment);
//            mViewHolder.txtPrice.setText(""+price);
            mViewHolder.txtTime.setText(date);

            final int id = mCursor.getInt(mCursor.getColumnIndex(AccountItemDb.ID));
            mViewHolder.btnDelete.setTag(id);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LogUtil.d(TAG,"_id = " + id);
                    Intent intent1 = new Intent();
                    intent1.setClassName(AccountConstants.ACCOUNT_PACKAGE, AccountConstants.ACTIVITY_ACCOUNT_ADD_OR_UPDATE);
                    intent1.putExtra(AccountConstants.ADD_OR_UPDATE_TYPE, id);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent1);
                }
            });
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        convertView.setId(position);
        return (convertView);
    }

    private class ViewHolder {
        TextView txtViewName = null;
        TextView txtViewComment = null;
        TextView txtTime = null;
        TextView txtPrice = null;
        Button btnDelete = null;

        public ViewHolder(View v) {
            txtViewName = (TextView) v.findViewById(R.id.row_name);
//            txtViewComment = (TextView) v.findViewById(R.id.row_comment);
//            txtPrice = (TextView) v.findViewById(R.id.row_price);
            txtTime = (TextView) v.findViewById(R.id.row_time);
            btnDelete = (Button) v.findViewById(R.id.btn_delete);
            btnDelete.setOnClickListener(mOnClickListener);
        }
    }
}
