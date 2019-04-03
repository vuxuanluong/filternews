package com.t3h.filternews.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.t3h.filternews.R;
import com.t3h.filternews.model.News;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHoder> {
    private LayoutInflater inflater;
    private ArrayList<News> arrNews;
    private OnItemEventCallback onItemEventCallback;

    public void setOnItemEventCallback(OnItemEventCallback onItemEventCallback) {
        this.onItemEventCallback = onItemEventCallback;
    }

    public Adapter(ArrayList<News> news, Context context) {
        this.arrNews = news;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_view, parent, false);
        ViewHoder viewHoder = new ViewHoder(view);
        return viewHoder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHoder holder, final int position) {
        News news = arrNews.get(position);
        holder.binData(news);
        if (onItemEventCallback != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemEventCallback.onClickItem(position);
                }
            });
            View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onItemEventCallback.onClickItemView(v);
                    onItemEventCallback.onLongClickItem(position);
                    return true;
                }
            };
//            holder.tvTitle.setOnLongClickListener(onLongClickListener);
//            holder.tvDesc.setOnLongClickListener(onLongClickListener);
//            holder.tvDate.setOnLongClickListener(onLongClickListener);
            holder.imNews.setOnLongClickListener(onLongClickListener);

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onItemEventCallback.onLongClickItem(position);
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return arrNews.size();
    }

    public class ViewHoder extends RecyclerView.ViewHolder {
        private ImageView imNews;
        private TextView tvTitle;
        private TextView tvDesc;
        private TextView tvDate;


        public ViewHoder(View itemView) {
            super(itemView);
            imNews = itemView.findViewById(R.id.im_news);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDesc = itemView.findViewById(R.id.tv_desc);
            tvDate = itemView.findViewById(R.id.tv_pubdate);
        }

        public void binData(News news) {
            Glide.with(itemView.getContext())
                    .load(news.getImg())
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(imNews);
            tvTitle.setText(news.getTitle());
            tvDesc.setText(news.getDesc());
            tvDate.setText(news.getPubDate());
        }
    }

    public interface OnItemEventCallback {
        void onClickItem(int position);
        void onLongClickItem(int position);
        void onClickItemView(View view);
    }
}

