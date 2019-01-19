package com.buyucoin.buyucoin.customDialogs;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.buyucoin.buyucoin.BuySellActivity;
import com.buyucoin.buyucoin.Fragments.AccountFragment;
import com.buyucoin.buyucoin.OkHttpHandler;
import com.buyucoin.buyucoin.R;
import com.buyucoin.buyucoin.pref.BuyucoinPref;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CustomDialogs extends BottomSheetDialogFragment {

    private String dialog_title;
    private String quantity;
    private String price;
    private String fees;
    private String total;
    private String type;
    private String coin;

    public static CustomDialogs newInstance(){
        return new CustomDialogs();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.buy_dialog_layout,container,false);

        TextView title,tv_quantity,tv_price,tv_fees,tv_total;
        final Button agree,cancel;
        title = view.findViewById(R.id.dialog_title);
        tv_quantity = view.findViewById(R.id.dialog_quantity);
        tv_price = view.findViewById(R.id.dialog_price);
        tv_fees = view.findViewById(R.id.dialog_fees);
        tv_total = view.findViewById(R.id.dialog_total);

        agree = view.findViewById(R.id.agree_action_btn);
        cancel = view.findViewById(R.id.cancel_action_btn);



        if(getArguments()!=null){
            Bundle bundle = getArguments();
            quantity = bundle.getString("quantity");
            price = bundle.getString("price");
            fees = bundle.getString("fees");
            total = bundle.getString("total");
            type = bundle.getString("type");
            coin = bundle.getString("coin");
            dialog_title = type.toUpperCase()+" ORDER";
        }

        title.setText(dialog_title);
        tv_quantity.setText(quantity);
        tv_price.setText(price);
        tv_fees.setText(fees);
        tv_total.setText(total);

        agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final JSONObject order = new JSONObject();
                try {
                    order.put("amount",Double.parseDouble(quantity))
                            .put("rate",Double.parseDouble(price))
                            .put("type",type);

                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            AccountFragment.makeViewDisable(agree);
                            PlaceOrder(order.toString(),coin,view.getContext());
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;


    }





    private void PlaceOrder(String s,String coin,final Context context){
        OkHttpHandler.auth_post("create_order?currency="+coin, new BuyucoinPref(context).getPrefString(BuyucoinPref.ACCESS_TOKEN), s, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("ORDER FAILED =====>",e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String s = response.body().string();
                try {
                    JSONObject jsonObject1 = new JSONObject(s);

                Log.d("ORDER DONE =====> ","ORDER PLACE SUCCESS FULL"+jsonObject1.toString());
                Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                         Toast.makeText(context, "ORDER PLACED SUCCESSFULLY", Toast.LENGTH_SHORT).show();

                    }
                });
                dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });
    }



}
