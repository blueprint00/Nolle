package com.example.adefault.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adefault.R;
import com.example.adefault.manager.ImageManager;
import com.example.adefault.model.LikeHistory;

import java.util.List;
import java.util.Random;

public class MyPageLikeHistoryAdapter extends RecyclerView.Adapter<MyPageLikeHistoryAdapter.MyPageLikeViewHolder> {
    private final List<LikeHistory> mDataList;

    public interface MyPageLikeClickListener {
        void onMyPageLikeItemClicked(int position);
        void onMyPageLikeImageClicked(int position);
        void onMyPageLikePlaceNameClicked(int position);
    }

    private MyPageLikeClickListener mListener;


    public void setOnClickListener(MyPageLikeClickListener listener) {
        mListener = listener;
    }

    public MyPageLikeHistoryAdapter(List<LikeHistory> dataList) {
        mDataList = dataList;
    }

    @NonNull
    @Override
    public MyPageLikeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mypage_liked_gallery_item, parent, false);
        return new MyPageLikeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyPageLikeViewHolder holder, int position) {


        String url = ImageManager.getInstance().getFullImageString(mDataList.get(position).getPosting().getImg_url_1());
        ImageManager.getInstance().GlideWithView(holder.itemView, holder.userImage, url);
        holder.ratingBar.setRating(mDataList.get(position).getPosting().getRating());
        holder.placeName.setText(mDataList.get(position).getPosting().getPlace_name());


        if(mListener != null) {
            final int pos = position;
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onMyPageLikeItemClicked(pos); // pos == holder.getAdapterPosition()
                }
            });
            holder.userImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onMyPageLikeImageClicked(pos);
                }
            });
            holder.placeName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onMyPageLikePlaceNameClicked(pos);
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return mDataList.size();
    };

    public static class MyPageLikeViewHolder extends RecyclerView.ViewHolder {

        ImageView userImage;
        RatingBar ratingBar;
        TextView placeName;

        public MyPageLikeViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.myPageItemView);
            ratingBar = itemView.findViewById(R.id.myPage_liked_ratingstars);
            placeName = itemView.findViewById(R.id.myPage_liked_placeName);
        }
    }

}
