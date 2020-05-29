package com.dfsoft.iptvplayer.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dfsoft.iptvplayer.R;
import com.dfsoft.iptvplayer.manager.IPTVCategory;

import java.util.List;

public class CategoryAdapter extends BaseAdapter {

    private List<IPTVCategory> mCategory = null;
    private Context mContext;

    private int currentItem = 0;

    public CategoryAdapter(Context context, List<IPTVCategory> mCategory) {
        this.mContext = context;
        this.mCategory = mCategory;
    }

    public void setCurrentItem(int currentItem) {
        this.currentItem = currentItem;
    }

    private boolean listViewIsFocused = false;

    public void setListViewIsFocused(boolean listViewIsFocused) {
        this.listViewIsFocused = listViewIsFocused;
    }

    public int getCurrentItem() {
        return  this.currentItem;
    }

    @Override
    public int getCount() {
        return mCategory.size();
    }

    @Override
    public Object getItem(int position) {
        return mCategory.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_cate_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //设置文字 内容
        holder.mTextView.setText(mCategory.get(position).name);

        if (currentItem == position) {
            //如果被点击，设置当前TextView被选中
            holder.mTextView.setSelected(true);
            holder.mTextView.setPressed(true);
//            if (listViewIsFocused)
//                holder.mTextView.setBackground(mContext.getResources().getDrawable(R.drawable.item_left_bg));
//            else
//                holder.mTextView.setBackground(null);
            holder.mTextView.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
        } else {
            //如果没有被点击，设置当前TextView未被选中
            holder.mTextView.setSelected(false);
            holder.mTextView.setPressed(false);
//            holder.mTextView.setBackground(null);
            holder.mTextView.setTextColor(mContext.getResources().getColor(R.color.white));
        }

        return convertView;
    }

    class ViewHolder {
        TextView mTextView;

        public ViewHolder(View convertView) {
            mTextView = (TextView) convertView.findViewById(R.id.cate_item_id);
        }
    }
}
