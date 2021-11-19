package com.andsoft.hexgenetator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

class GVAdapter extends ArrayAdapter {

    private Context context;
    private List<Integer> list = new ArrayList();

    GVAdapter(Context context, int textViewResourceId, List<Integer> data) {
        super(context, textViewResourceId);
        this.context = context;
        this.list = data;
    }

    public int getCount() {
        return this.list.size();
    }

    public long getItemId(int position) {
        return 0;
    }

    @NonNull
    public View getView(int i, View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item, parent, false);

//                    ((LayoutInflater) getContext().getSystemService("layout_inflater"))
//                    .inflate(R.layout.item, parent, false);
        }

        View v = convertView.findViewById(R.id.v_item);

//        TextView txtStatus = (TextView) row.findViewById(R.id.txtStatus);item_all_load_grid

//        RelativeLayout rl_main = (RelativeLayout) row.findViewById(R.id.rl_main);

//        View rl_main = (RelativeLayout) row.findViewById(R.id.rl_main);

        if (list.get(i) == 1) {
            v.setBackgroundResource(R.drawable.bg_select);
        } else {
            v.setBackgroundResource(R.drawable.bg_normal);
        }

        return convertView;
    }
}
