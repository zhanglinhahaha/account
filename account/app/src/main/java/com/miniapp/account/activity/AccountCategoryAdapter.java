package com.miniapp.account.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    private boolean mStatus = false;
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

        if(mStatus) {
            holder.categoryDelete.setVisibility(View.VISIBLE);
            holder.categoryDelete.setTag(position);
            if(category.getName().equals(AccountConstants.ADD_CATEGORY_STRING)) {
                holder.categoryName.setVisibility(View.GONE);
                holder.categoryImage.setVisibility(View.GONE);
                holder.categoryDelete.setVisibility(View.GONE);
            }
        }else {
            holder.categoryDelete.setVisibility(View.GONE);
        }

        if(!category.getImageId().equals(AccountConstants.IMAGE_IS_NULL)) {
            if(category.getName().equals(AccountConstants.ADD_CATEGORY_STRING)) {
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


    public AccountCategoryAdapter(ArrayList<Category> categoryList, Context context, View.OnClickListener listener, boolean isDeleteStatus) {
        mCategoryList = categoryList;
        mContext = context;
        mOnClickListener = listener;
        mStatus = isDeleteStatus;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView categoryImage = null;
        TextView categoryName = null;
        Button categoryDelete = null;

        public ViewHolder(View itemView) {
            super(itemView);
            categoryDelete = (Button) itemView.findViewById(R.id.category_delete);
            categoryImage = (CircleImageView) itemView.findViewById(R.id.category_image);
            categoryName = (TextView) itemView.findViewById(R.id.category_name);

            categoryDelete.setOnClickListener(mOnClickListener);
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

    public class CategoryItemDecoration extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
        }
    }
}
