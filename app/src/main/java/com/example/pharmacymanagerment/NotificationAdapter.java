package com.example.pharmacymanagerment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class NotificationAdapter extends BaseAdapter {

    private Context context;
    private List<NotificationModel> notifications;
    private LayoutInflater inflater;

    public NotificationAdapter(Context context, List<NotificationModel> notifications) {
        this.context = context;
        this.notifications = notifications;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return notifications.size();
    }

    @Override
    public Object getItem(int position) {
        return notifications.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_notification, parent, false);
            holder = new ViewHolder();
            holder.imgIcon = convertView.findViewById(R.id.imgIcon);
            holder.tvTitle = convertView.findViewById(R.id.tvTitle);
            holder.tvMessage = convertView.findViewById(R.id.tvMessage);
            holder.tvTime = convertView.findViewById(R.id.tvTime);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        NotificationModel notification = notifications.get(position);
        holder.tvTitle.setText(notification.getTitle());
        holder.tvMessage.setText(notification.getMessage());
        holder.tvTime.setText(notification.getTime());

        return convertView;
    }

    static class ViewHolder {
        ImageView imgIcon;
        TextView tvTitle, tvMessage, tvTime;
    }
}
