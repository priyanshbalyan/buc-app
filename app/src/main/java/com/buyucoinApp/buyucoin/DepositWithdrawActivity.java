package com.buyucoinApp.buyucoin;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.buyucoinApp.buyucoin.Adapters.CoinActiveOrderAdapter;
import com.buyucoinApp.buyucoin.Adapters.CoinHistoryAdapter;
import com.buyucoinApp.buyucoin.customDialogs.CoustomToast;
import com.buyucoinApp.buyucoin.pojos.History;
import com.buyucoinApp.buyucoin.pref.BuyucoinPref;
import com.crashlytics.android.Crashlytics;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.fabric.sdk.android.Fabric;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class DepositWithdrawActivity extends AppCompatActivity {
    LinearLayout qr_layout, buy_layout, sell_layout, deposite_layout, withdraw_layout, empty_layout;
    ImageView imageView, card_coin_img,big_qr_code;
    RecyclerView history_recyclerview;
    TextView card_coin_full_name, card_coin_availabel, card_coin_pending, card_coin_address, card_coin_base_address;
    Intent i;
    Button address_gen_btn;
    NestedScrollView nestedScrollView;
    BuyucoinPref pref;
    ProgressBar pb;
    String coin,coin_full_name;
    private ArrayList<History> histories;
    Toolbar toolbar;
    private ClipboardManager clipboardManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Fabric.with(this, new Crashlytics());
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_deposite_withdraw);
        pref = new BuyucoinPref(getApplicationContext());
        clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        i = getIntent();

        final String COIN = coin = i.getStringExtra("coin_name");
        final String AVAILABEL = i.getStringExtra("available");
        final String PENDING = i.getStringExtra("pendings");
        final String ADDRESS = i.getStringExtra("address");
        final String BASE_ADDRESS = i.getStringExtra("base_address");
        final String DESCRIPTION = i.getStringExtra("description");
        final String TAG = i.getStringExtra("tag");
        final String COIN_FULL_NAME = coin_full_name = i.getStringExtra("full_coin_name");

        histories = new ArrayList<>();
        InitializeAllViews();


        card_coin_full_name.setText(COIN_FULL_NAME);
        card_coin_availabel.setText(AVAILABEL);
        card_coin_address.setText(ADDRESS);
        card_coin_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cd = card_coin_address.getText().toString();
                if(!cd.equals("") && cd!=null){
                    ClipData clipData = ClipData.newPlainText("ADDRESS",cd);
                    clipboardManager.setPrimaryClip(clipData);
                    new CoustomToast(getApplicationContext(), "ADDRESS COPIED !",CoustomToast.TYPE_NORMAL).showToast();

                }
            }
        });
        card_coin_pending.setText(PENDING);

        Log.d("BASE ADDRESS", "onCreate: ."+BASE_ADDRESS);

        try {
            card_coin_img.setImageResource(MyResourcesClass.COIN_ICON.getInt(coin));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (BASE_ADDRESS!=null && !BASE_ADDRESS.equals("null")) {
            card_coin_base_address.setText(BASE_ADDRESS);
            card_coin_base_address.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String cd = card_coin_base_address.getText().toString();
                    if(cd!=null && !cd.equals("")){
                        ClipData clipData = ClipData.newPlainText("ADDRESS",cd);
                        clipboardManager.setPrimaryClip(clipData);
                        new CoustomToast(getApplicationContext(), "ADDRESS COPIED !",CoustomToast.TYPE_NORMAL).showToast();
                    }
                }
            });
            card_coin_base_address.setVisibility(View.VISIBLE);
        }



        if (ADDRESS==null || ADDRESS.equals("null")) {
            card_coin_address.setVisibility(View.GONE);
            address_gen_btn.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
            card_coin_base_address.setVisibility(View.GONE);

        }

        qrCodeGenrator(ADDRESS,BASE_ADDRESS);

        address_gen_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateAddress();
            }
        });


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qr_layout.setVisibility(View.VISIBLE);
            }
        });

        qr_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qr_layout.setVisibility(View.GONE);
            }
        });

        history_recyclerview.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        CoinHistoryAdapter coinHistoryAdapter = new CoinHistoryAdapter(getApplicationContext());
        history_recyclerview.setAdapter(coinHistoryAdapter);

        nestedScrollView.post(new Runnable() {
            @Override
            public void run() {
                nestedScrollView.scrollTo(0, 0);
            }
        });

        buy_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BuySellActivity.class);
                intent.putExtra("type", "buy");
                intent.putExtra("price", "234567");
                intent.putExtra("coin_name", COIN);
                intent.putExtra("available", AVAILABEL);
                intent.putExtra("address", ADDRESS);
                intent.putExtra("description", DESCRIPTION);
                intent.putExtra("tag", TAG);
                intent.putExtra("full_coin_name", COIN_FULL_NAME);
                startActivity(intent);

            }
        });
        sell_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BuySellActivity.class);
                intent.putExtra("type", "sell");
                intent.putExtra("price", "234567");
                intent.putExtra("coin_name", COIN);
                intent.putExtra("available", AVAILABEL);
                intent.putExtra("address", ADDRESS);
                intent.putExtra("description", DESCRIPTION);
                intent.putExtra("tag", TAG);
                intent.putExtra("full_coin_name", COIN_FULL_NAME);
                startActivity(intent);

            }
        });
        deposite_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CoinDepositWithdraw.class);
                intent.putExtra("type", "DEPOSITE");
                intent.putExtra("coin_name", COIN);
                intent.putExtra("available", AVAILABEL);
                intent.putExtra("address", ADDRESS);
                intent.putExtra("base_address", BASE_ADDRESS);
                intent.putExtra("description", DESCRIPTION);
                intent.putExtra("tag", TAG);
                intent.putExtra("full_coin_name", COIN_FULL_NAME);
                startActivity(intent);

            }
        });
        withdraw_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CoinDepositWithdraw.class);
                intent.putExtra("type", "WITHDRAW");
                intent.putExtra("coin_name", COIN);
                intent.putExtra("available", AVAILABEL);
                intent.putExtra("address", ADDRESS);
                intent.putExtra("base_address", BASE_ADDRESS);
                intent.putExtra("description", DESCRIPTION);
                intent.putExtra("tag", TAG);
                intent.putExtra("full_coin_name", COIN_FULL_NAME);
                startActivity(intent);

            }
        });

        address_gen_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateAddress();
            }
        });

        getList("order");
    }


    public void qrCodeGenrator(String address,String bass_address){
        if(address!=null && !address.equals("null")){
            String fulladdress = "";
            if(bass_address!=null && !bass_address.equals("null")){
            fulladdress = "address : ["+address+"] \n tag : ["+bass_address+"]";
            }else{
                fulladdress = "address : ["+address+"]";
            }


            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            try {
                BitMatrix bitMatrix = multiFormatWriter.encode(fulladdress, BarcodeFormat.QR_CODE,500,500);
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                imageView.setImageBitmap(bitmap);
                big_qr_code.setImageBitmap(bitmap);

            } catch (WriterException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                finish();
                return true;
            default:
                finish();
                return true;
        }
    }

    private void InitializeAllViews() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(coin_full_name);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        imageView = findViewById(R.id.qrcodeimg);
        qr_layout = findViewById(R.id.qrcodelayout);
        history_recyclerview = findViewById(R.id.rvActiveCoinOrdrs);

        buy_layout = findViewById(R.id.buy_layout_card);
        sell_layout = findViewById(R.id.sell_layout_card);
        deposite_layout = findViewById(R.id.deposite_layout_card);
        withdraw_layout = findViewById(R.id.withdraw_layout_card);

        card_coin_full_name = findViewById(R.id.card_coin_full_name);
        card_coin_availabel = findViewById(R.id.card_coin_availabel);
        card_coin_address = findViewById(R.id.card_coin_address);
        card_coin_base_address = findViewById(R.id.card_coin_base_address);
        card_coin_pending = findViewById(R.id.card_coin_pending);
        card_coin_img = findViewById(R.id.card_coin_image);

        address_gen_btn = findViewById(R.id.card_coin_address_gen_btn);

        nestedScrollView = findViewById(R.id.card_coin_nested_view);
        pb = findViewById(R.id.order_pb);
        empty_layout = findViewById(R.id.empty_orders);

        big_qr_code = findViewById(R.id.big_qr_code);
    }

    private void generateAddress() {
        final ProgressDialog progressDialog = new ProgressDialog(DepositWithdrawActivity.this);
        progressDialog.setMessage("Generating Address");
        progressDialog.setCancelable(false);
        progressDialog.show();
        OkHttpHandler.auth_get("/generate_address/"+coin, pref.getPrefString(BuyucoinPref.ACCESS_TOKEN), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(final Call call, Response response) throws IOException {
                assert response.body() != null;
                String body = response.body().string();
                try {
                    JSONObject data = new JSONObject(body);
                    final String status = data.getString("status");
                    final String message = data.getJSONArray("message").getJSONArray(0).getString(0);
//                    final String address = data.getJSONObject("data").
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            switch (status){
                                case "error": card_coin_address.setText(message);
                                card_coin_address.setVisibility(View.VISIBLE);
                                    address_gen_btn.setVisibility(View.GONE);
                                break;
                                case "success": card_coin_address.setText(message);
                                    card_coin_address.setVisibility(View.VISIBLE);
                                    address_gen_btn.setVisibility(View.GONE);
                                break;
                            }
                            progressDialog.dismiss();
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });

    }

    public void getList(final String url) {
        OkHttpHandler.auth_get(url+"_history", pref.getPrefString(BuyucoinPref.ACCESS_TOKEN), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) {

                    try {
                        assert response.body() != null;
                        String s = response.body().string();


                            final JSONArray array = new JSONObject(s).getJSONObject("data").getJSONArray(url.equals("order") ? "orders" : url + "_comp");
                            Log.d("sdfghjsdfghjk", array.toString());
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject j = array.getJSONObject(i);
                                if (j.getString("curr").equals(coin) && j.getString("status").equals("Pending")) {
                                    histories.add(new History(
                                            j.getDouble("amount"),
                                            j.getString("curr"),
                                            j.getString("open"),
                                            j.getString("open"),
                                            j.getString("status"),
                                            "",
                                            "",
                                            j.getDouble("fee"),
                                            j.getDouble("filled"),
                                            j.getDouble("price"),
                                            j.getString("type"),
                                            j.getDouble("value"),
                                            j.getInt("id")
                                    ));
                                }

                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    if (histories.size() > 0) {
                                        history_recyclerview.setAdapter(new CoinActiveOrderAdapter(histories,getApplicationContext()));
                                        history_recyclerview.setVisibility(View.VISIBLE);
                                        pb.setVisibility(View.GONE);
                                    } else {
                                        pb.setVisibility(View.GONE);
                                        empty_layout.setVisibility(View.VISIBLE);

                                    }
                                }
                            });




                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pb.setVisibility(View.GONE);
                                empty_layout.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                }

        });
    }





}

