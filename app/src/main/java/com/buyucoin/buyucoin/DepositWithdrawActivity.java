package com.buyucoin.buyucoin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.buyucoin.buyucoin.Adapters.CoinHistoryAdapter;

public class DepositWithdrawActivity extends AppCompatActivity {
    LinearLayout qr_layout,buy_layout,sell_layout,deposite_layout,withdraw_layout;
    ImageView imageView;
    RecyclerView history_recyclerview;
    TextView card_coin_full_name,card_coin_availabel,card_coin_pending,card_coin_address,card_coin_base_address;
    Intent i;
    Button address_gen_btn;
    NestedScrollView nestedScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposite_withdraw);

        InitializeAllViews();



        i = getIntent();

        final String COIN = i.getStringExtra("coin_name");
        final String AVAILABEL = i.getStringExtra("available");
        final String PENDING = i.getStringExtra("pendings");
        final String ADDRESS = i.getStringExtra("address");
        final String BASE_ADDRESS = i.getStringExtra("base_address");
        final String DESCRIPTION = i.getStringExtra("description");
        final String TAG = i.getStringExtra("tag");
        final String COIN_FULL_NAME = i.getStringExtra("full_coin_name");

        card_coin_full_name.setText(COIN_FULL_NAME);
        card_coin_availabel.setText(AVAILABEL);
        card_coin_address.setText(ADDRESS);
        card_coin_base_address.setText(BASE_ADDRESS);
        card_coin_pending.setText(PENDING);


        if(ADDRESS.equals("null")){
            card_coin_address.setVisibility(View.GONE);
            address_gen_btn.setVisibility(View.VISIBLE);

        }

        address_gen_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateAddress(card_coin_address);
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
                nestedScrollView.scrollTo(0,0);
            }
        });

        buy_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),BuySellActivity.class);
                intent.putExtra("type","buy");
                intent.putExtra("price","234567");
                intent.putExtra("coin_name",COIN);
                intent.putExtra("available",AVAILABEL);
                intent.putExtra("address",ADDRESS);
                intent.putExtra("description",DESCRIPTION);
                intent.putExtra("tag",TAG);
                intent.putExtra("full_coin_name",COIN_FULL_NAME);
                startActivity(intent);

            }
        });
        sell_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),BuySellActivity.class);
                intent.putExtra("type","sell");
                intent.putExtra("price","234567");
                intent.putExtra("coin_name",COIN);
                intent.putExtra("available",AVAILABEL);
                intent.putExtra("address",ADDRESS);
                intent.putExtra("description",DESCRIPTION);
                intent.putExtra("tag",TAG);
                intent.putExtra("full_coin_name",COIN_FULL_NAME);
                startActivity(intent);

            }
        });
        deposite_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),CoinDepositWithdraw.class);
                intent.putExtra("type","DEPOSITE");
                intent.putExtra("coin_name",COIN);
                intent.putExtra("available",AVAILABEL);
                intent.putExtra("address",ADDRESS);
                intent.putExtra("description",DESCRIPTION);
                intent.putExtra("tag",TAG);
                intent.putExtra("full_coin_name",COIN_FULL_NAME);
                startActivity(intent);

            }
        });
        withdraw_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),CoinDepositWithdraw.class);
                intent.putExtra("type","WITHDRAW");
                intent.putExtra("coin_name",COIN);
                intent.putExtra("available",AVAILABEL);
                intent.putExtra("address",ADDRESS);
                intent.putExtra("description",DESCRIPTION);
                intent.putExtra("tag",TAG);
                intent.putExtra("full_coin_name",COIN_FULL_NAME);
                startActivity(intent);

            }
        });

        address_gen_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateAddress(card_coin_address);
            }
        });
    }

    private void InitializeAllViews() {
        imageView = findViewById(R.id.qrcodeimg);
        qr_layout = findViewById(R.id.qrcodelayout);
        history_recyclerview = findViewById(R.id.rvCoinHistory);

        buy_layout = findViewById(R.id.buy_layout_card);
        sell_layout = findViewById(R.id.sell_layout_card);
        deposite_layout = findViewById(R.id.deposite_layout_card);
        withdraw_layout = findViewById(R.id.withdraw_layout_card);

        card_coin_full_name = findViewById(R.id.card_coin_full_name);
        card_coin_availabel = findViewById(R.id.card_coin_availabel);
        card_coin_address = findViewById(R.id.card_coin_address);
        card_coin_base_address = findViewById(R.id.card_coin_base_address);
        card_coin_pending = findViewById(R.id.card_coin_pending);

        address_gen_btn = findViewById(R.id.card_coin_address_gen_btn);

        nestedScrollView = findViewById(R.id.card_coin_nested_view);
    }

    private void generateAddress(TextView card_coin_address) {

        card_coin_address.setText("ADDRESS GENERATED");

    }


}

