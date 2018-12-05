package com.example.godgo.map;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class AdapterItem extends BaseAdapter {

    private LayoutInflater inflater;
    private ArrayList<LogItem> data;
    private int layout;

    public AdapterItem(Context context, int layout, ArrayList<LogItem> data) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.data = data;
        this.layout = layout;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public String getItem(int position) {
        return data.get(position).getCommander();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(layout, parent, false);
        }
        LogItem logItem = data.get(position);

        ImageView icon = (ImageView) convertView.findViewById(R.id.imageview);
        //icon.setImageResource(logItem.getIcon()); //이미지를 갖고오는것
        icon.setImageResource(R.drawable.mavic);

        TextView commander = (TextView) convertView.findViewById(R.id.textview);
        commander.setText("드론주소 :"+logItem.getCommander());

        TextView dstlon = (TextView) convertView.findViewById(R.id.textview2);
        dstlon.setText(String.valueOf("위도 :"+logItem.getDstlon()));

        TextView dstlat = (TextView) convertView.findViewById(R.id.textview3);
        dstlat.setText(String.valueOf("경도 :"+logItem.getDstlat()));
        return convertView;
    }
}
