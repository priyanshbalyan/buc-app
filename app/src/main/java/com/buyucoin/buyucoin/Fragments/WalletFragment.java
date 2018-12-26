package com.buyucoin.buyucoin.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.anychart.anychart.UiTitle;
import com.buyucoin.buyucoin.Adapters.MyItemRecyclerViewAdapter;
import com.buyucoin.buyucoin.Dashboard;
import com.buyucoin.buyucoin.LoginActivity;
import com.buyucoin.buyucoin.OkHttpHandler;
import com.buyucoin.buyucoin.R;
import com.buyucoin.buyucoin.Utilities;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.internal.Util;

import static android.content.Context.MODE_PRIVATE;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class WalletFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    String ACCESS_TOKEN = null;
    ArrayList<JSONObject> list;
    ArrayList<JSONObject> j = new ArrayList<>();
    RecyclerView recyclerView;
    ProgressBar pb;
    TextView err;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public WalletFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static WalletFragment newInstance(int columnCount) {
        WalletFragment fragment = new WalletFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getActivity().getSharedPreferences("BUYUCOIN_USER_PREFS", MODE_PRIVATE);
        ACCESS_TOKEN = prefs.getString("access_token", null);
        list = new ArrayList<>();
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        // Set the adapter
        recyclerView = (RecyclerView) view.findViewById(R.id.rvWallet);
        Context context = view.getContext();
        GridLayoutManager  linearLayoutManager = new GridLayoutManager(context,1);
        recyclerView.setLayoutManager(linearLayoutManager);

        pb = (ProgressBar) view.findViewById(R.id.pbWallet);
        err = (TextView) view.findViewById(R.id.tvWalletError);




        recyclerView.setAdapter(new MyItemRecyclerViewAdapter(getContext(),list, mListener));


        Utilities.hideProgressBar(pb);
//        getWalletData();
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(JSONObject item);
    }

    public void getWalletData(){
        OkHttpHandler.auth_get("get_wallet", ACCESS_TOKEN, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String s = response.body().string();
                Log.d("RESPONSE_____", s);
                try{
                    JSONObject jsonObject = new JSONObject(s);
                    if(jsonObject.getString("status").equals("error")){
                        if(jsonObject.has("msg") && jsonObject.getString("msg").equals("The token has expired")) {
                            SharedPreferences.Editor editor = getActivity().getSharedPreferences("BUYUCOIN_USER_PREFS", MODE_PRIVATE).edit();
                            editor.remove("access_token");
                            editor.remove("refresh_token");
                            editor.apply();
                            Utilities.showToast(getActivity(), "Login again to access wallet");
                            startActivity(new Intent(getActivity(), LoginActivity.class));
                            getActivity().finish();
                        }else{
                            if(jsonObject.has("message"))
                                Utilities.showToast(getActivity(), jsonObject.getString("message"));
                        }
                        return;
                    }
                    JSONObject data = jsonObject.getJSONObject("data");
                    String[] arr = {"btc", "eth", "inr", "ltc", "bcc", "xmr", "qtum", "etc", "zec", "xem", "gnt", "neo", "xrp", "dash", "strat", "steem", "rep", "lsk", "fct", "omg", "cvc", "sc", "pay", "ark", "doge", "dgb", "nxt", "bat", "bts", "cloak", "pivx", "dcn", "buc", "pac"};
                    for(int i=0; i<arr.length; i++){
                        try {
                            list.add(data.getJSONObject(arr[i]).put("currencyname", arr[i]));


                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                    if(getActivity()!=null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                recyclerView.setAdapter(new MyItemRecyclerViewAdapter(getContext(),list, mListener));
                                Utilities.hideProgressBar(pb);
//                            pb.animate().alpha(0f).setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime)).setListener(new AnimatorListenerAdapter(){
//                                public void onAnimationEnd(Animator animator) {
//                                    pb.setVisibility(View.GONE);
//                                    pb.setAlpha(1f);
//                                }
//                            });
                            }
                        });
                    }

                }catch(Exception e){
                    e.printStackTrace();
                    if(getActivity()!=null){

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Utilities.hideProgressBar(pb);
                            err.setVisibility(View.VISIBLE);
                        }
                    });
                    }
                }
            }
        });
    }
}

