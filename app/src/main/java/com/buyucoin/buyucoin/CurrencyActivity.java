package com.buyucoin.buyucoin;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.buyucoin.buyucoin.Adapters.AsksAdapter;
import com.buyucoin.buyucoin.Adapters.BidsAdapter;
import com.buyucoin.buyucoin.Adapters.MarketHistoryAdapter;
import com.buyucoin.buyucoin.DataClasses.Markets;
import com.buyucoin.buyucoin.config.Config;
import com.buyucoin.buyucoin.pojos.Ask;
import com.buyucoin.buyucoin.pojos.Bids;

import com.buyucoin.buyucoin.pojos.MarketHistory;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CurrencyActivity extends AppCompatActivity {

    DatabaseReference myRef;
    GraphView graphView;
    Button sell,buy;
    Intent intent;
    Bundle bundle;
    ProgressBar pb;
    RecyclerView bids_recview,ask_recview,market_recview;
    DatabaseReference myref;
    ArrayList<Bids> arrayListBids ;
    ArrayList<Ask> arrayListAsks ;
    ArrayList<MarketHistory> arrayListMarketHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency);


        FirebaseDatabase db = new Config().getProductionFirebaseDatabase(getApplicationContext());
        //Toast.makeText(getApplicationContext(), ""+db.getReference().toString(), Toast.LENGTH_LONG).show();
        myRef = db.getReference();



        pb = findViewById(R.id.pbCurrencyActivity);
        buy = findViewById(R.id.tvCurrencyBuy);
        sell = findViewById(R.id.tvCurrencySell);
        bids_recview = findViewById(R.id.rvBid);
        ask_recview = findViewById(R.id.rvAsk);
        market_recview = findViewById(R.id.rvMarketHIstory);


        bids_recview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        ask_recview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        market_recview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));




    }

    @Override
    protected void onStart() {
        super.onStart();

        intent = getIntent();
        bundle = intent.getExtras();

        TextView tv = findViewById(R.id.tvCurrencyCurr);
        TextView hrs24 = findViewById(R.id.tvCurrency24Hrs);
        ImageView img = findViewById(R.id.ivCurrencyImg);

        final String s = bundle.getString("currency").split("_")[0];


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String marketstring = "market_"+s;
                DataSnapshot data = dataSnapshot.child(marketstring).child("data");
                Log.d("MARKET___", data.toString());

                arrayListBids = new ArrayList<>();
                arrayListAsks = new ArrayList<>();
                arrayListMarketHistory = new ArrayList<>();

                arrayListBids.clear();
                arrayListAsks.clear();



                if(data.hasChild("buy_orders")){
                    for(DataSnapshot d : data.child("buy_orders").getChildren()){
                        Double price = d.child("price").getValue(Double.class);
                        String value = d.child("value").getValue(String.class);
                        Double vol = d.child("vol").getValue(Double.class);
                        Bids b = new Bids(price,value,vol);
                        arrayListBids.add(b);
                    }
                    BidsAdapter bidsAdapter = new BidsAdapter(getApplicationContext(),arrayListBids);
                    bids_recview.setAdapter(bidsAdapter);
                }
                if(data.hasChild("sell_orders")){
                    for(DataSnapshot d : data.child("sell_orders").getChildren()){
                        Double price = d.child("price").getValue(Double.class);
                        String value = d.child("value").getValue(String.class);
                        Double vol = d.child("vol").getValue(Double.class);
                        Ask a = new Ask(price,value,vol);
                        arrayListAsks.add(a);
                    }
                    AsksAdapter asksAdapter = new AsksAdapter(getApplicationContext(),arrayListAsks);
                    ask_recview.setAdapter(asksAdapter);
                }
                if (data.hasChild("market_history")){
                    for(DataSnapshot d : data.child("market_history").getChildren()){
                        Double amount = d.child("amount").getValue(Double.class);
                        Double price = d.child("price").getValue(Double.class);
                        Long time = d.child("time").getValue(Long.class);
                        String type = d.child("type").getValue(String.class);
                        String value = d.child("value").getValue(String.class);

                        MarketHistory m = new MarketHistory();
                        m.setAmount(amount);
                        m.setPrice(price);
                        m.setTime(time);
                        m.setType(type);
                        m.setValue(value);

                        arrayListMarketHistory.add(m);


                    }
                    MarketHistoryAdapter marketHistoryAdapter = new MarketHistoryAdapter(getApplicationContext(),arrayListMarketHistory);
                    market_recview.setAdapter(marketHistoryAdapter);
                }

                Log.d("LIST SIZE__", arrayListAsks.size()+""+arrayListBids.size());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("TAG", "Failed to read value", databaseError.toException());
            }
        });


        try {
            img.setImageDrawable(getResources().getDrawable(getResources().getIdentifier(s, "drawable", getPackageName())));
        }catch(Exception e){
            e.printStackTrace();
        }
        tv.setText(s.toUpperCase());
        buy.setText("Buy \u20B9 "+bundle.getString("ask"));
        sell.setText("Sell \u20B9 "+bundle.getString("bid"));
        hrs24.setText("24Hrs Change "+bundle.getString("high_24"));

        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BuySellButtonFunction("buy",bundle.getString("ask"));

            }
        });

        sell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BuySellButtonFunction("sell",bundle.getString("bid"));
            }
        });


        OkHttpHandler.get("https://www.buyucoin.com/market-graph?currency=" + s, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String s = null;
                if (response.body() != null) {
                    s = response.body().string();

//                    Log.d("currency response", s);
                    try {
                        JSONObject object = new JSONObject(s).getJSONObject("data");
                        JSONArray prices = object.getJSONArray("price");
                        JSONArray time = object.getJSONArray("time");

                        List<XY> list = new ArrayList<>();
                        for(int i=0; i<prices.length(); i++){
                            list.add(new XY(time.getString(i), prices.getDouble(i)));
                        }
                        addToGraph(list);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }

    public class XY{
        public String x;
        public Double y;
        public XY(String x, Double y){
            this.x = x;
            this.y = y;
        }
    }

    private void BuySellButtonFunction(String type, String price) {
            Intent buysellintent = new Intent(this,BuySellActivity.class);
            buysellintent.putExtra("price",price);
            buysellintent.putExtra("type",type);
            startActivity(buysellintent);
    }

    public void addToGraph(List<XY> list) {
        Log.d("ARRAY LENGTH", list.size()+ "");

        Collections.sort(list, new Comparator<XY>() {
            DateFormat f = new SimpleDateFormat("YYYY-MM-dd hh:mm:ss");
            @Override
            public int compare(XY xy, XY t1) {
                try {
                    return f.parse(xy.x).compareTo(f.parse(t1.x));
                } catch (ParseException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        });


        DataPoint[] dp = new DataPoint[list.size()];
        try {
            for (int i = 0; i < list.size(); i++) {
                DateFormat df = new SimpleDateFormat("YYYY-MM-dd hh:mm:ss", Locale.ENGLISH);
                Date date = df.parse(list.get(i).x);
                dp[i] = new DataPoint(date, list.get(i).y);
                Log.d("Date", dp[i].getX()+"");
            }
        } catch(ParseException e){
            e.printStackTrace();
        }


        Log.d("DP LENGTH", dp.length+"");
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dp);

        graphView = (GraphView) findViewById(R.id.graphView);

        graphView.getViewport().setScrollable(true);
        graphView.getViewport().setScrollableY(true);
//        graphView.getViewport().setScalable(true);
        graphView.getViewport().setScalableY(true);

        graphView.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getApplicationContext()));
        graphView.getGridLabelRenderer().setNumHorizontalLabels(3);
        graphView.addSeries(series);

        Utilities.hideProgressBar(pb);
    }


}
