package com.buyucoin.buyucoin.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.buyucoin.buyucoin.R;
import com.buyucoin.buyucoin.pojos.Ask;

import java.text.DecimalFormat;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AsksAdapter extends RecyclerView.Adapter<AsksAdapter.AskViewHolder> {
    private Context context;
    private ArrayList<Ask> askArrayList;

    public AsksAdapter(Context context, ArrayList<Ask> askArrayList) {
        this.context = context;
        this.askArrayList = askArrayList;
    }

    @NonNull
    @Override
    public AskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.ask_item, parent, false);
        return new AsksAdapter.AskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AskViewHolder holder, int position) {

        DecimalFormat format2 = new DecimalFormat("0.####");
        holder.price.setText(String.valueOf(format2.format(askArrayList.get(position).getAsk_price())));
        holder.value.setText(askArrayList.get(position).getAsk_value());
        DecimalFormat format = new DecimalFormat("0.########");
        String v1 = format.format(askArrayList.get(position).getAsk_volume());
        holder.vol.setText(String.valueOf(v1).trim());
        if(position%2==0){
            holder.itemView.setBackgroundColor(Color.parseColor("#eeeeee"));
        }

        Log.d("WIDTH============>",String.valueOf(holder.itemView));

    }

    @Override
    public int getItemCount() {
        return (askArrayList.size()<10)?askArrayList.size():10;
    }

    class AskViewHolder extends RecyclerView.ViewHolder {
        TextView price,value,vol;
        View progress;
        AskViewHolder(@NonNull View itemView) {
            super(itemView);
            price = itemView.findViewById(R.id.item_price);
            value = itemView.findViewById(R.id.item_value);
            vol = itemView.findViewById(R.id.item_vol);

        }
    }
}
