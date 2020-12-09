package com.miniapp.account.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import de.hdodenhof.circleimageview.CircleImageView;

import com.miniapp.account.R;

/**
 * Created by zl on 20-12-7.
 */
public class AccountCategoryAdapter extends RecyclerView.Adapter<AccountCategoryAdapter.ViewHolder> {
    private static final String TAG = "AccountCategoryAdapter";
    private ArrayList<Category> mCategoryList = null;
    private Context mContext = null;
    private View.OnClickListener mOnClickListener = null;

    @Override
    public AccountCategoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.category_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AccountCategoryAdapter.ViewHolder holder, int position) {
        Category category = mCategoryList.get(position);
        if(!category.getImageId().equals("0")) {
            if(category.getName().equals("add")) {
                holder.categoryImage.setImageResource(Integer.valueOf(category.getImageId()));
            }else {
                Bitmap bitmap = BitmapFactory.decodeFile(category.getImageId());
                holder.categoryImage.setImageBitmap(bitmap);
            }
        }
        holder.categoryName.setText(category.getName());
        holder.categoryName.setTextSize(22);
        holder.categoryName.setTag(position);
        holder.categoryImage.setTag(position);
    }

    @Override
    public int getItemCount() {
        return mCategoryList.size();
    }

    public AccountCategoryAdapter(ArrayList<Category> categoryList, Context context, View.OnClickListener listener) {
        mCategoryList = categoryList;
        mContext = context;
        mOnClickListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView categoryImage = null;
        TextView categoryName = null;

        public ViewHolder(View itemView) {
            super(itemView);
            categoryImage = (CircleImageView) itemView.findViewById(R.id.category_image);
            categoryName = (TextView) itemView.findViewById(R.id.category_name);
            categoryImage.setOnClickListener(mOnClickListener);
            categoryName.setOnClickListener(mOnClickListener);
        }
    }

    public static class Category {
        private String name;
        private String imageId;

        public Category(String name, String imageId) {
            this.name = name;
            this.imageId = imageId;
        }

        public String getName() {
            return name;
        }

        public String getImageId() {
            return imageId;
        }
    }
}
