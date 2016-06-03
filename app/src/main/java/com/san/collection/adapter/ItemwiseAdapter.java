package com.san.collection.adapter;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.san.collection.CustomDialog;
import com.san.collection.R;
import com.san.collection.app.AppController;
import com.san.collection.bean.ItemBean;
import com.san.collection.bean.TablesBean;
import com.san.collection.utils.GlobalVariables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SANTECH on 27/05/2016.
 */
public class ItemwiseAdapter extends RecyclerView.Adapter<ItemwiseAdapter.TableViewHolder> {

    Context mContext;
    List<ItemBean> itmList = new ArrayList<>();

    public ItemwiseAdapter(Context mContext, List<ItemBean> itmList) {
        this.mContext = mContext;
        this.itmList = itmList;

    }

    @Override
    public TableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itmview = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemtxt, parent, false);
        return new TableViewHolder(itmview);
    }

    @Override
    public void onBindViewHolder(TableViewHolder holder, int position) {
        final ItemBean tblbean = itmList.get(position);
        holder.txtitmnam.setText("" + tblbean.getItemname());
        holder.txtitmrate.setText("" + tblbean.getRate());
        holder.txtitmqty.setText("" + tblbean.getQty());
        holder.txtsubtotal.setText("" + tblbean.getPrice());
    }

    @Override
    public int getItemCount() {
        return itmList.size();
    }

    public class TableViewHolder extends RecyclerView.ViewHolder {

        TextView txtitmnam, txtitmqty, txtitmrate, txtsubtotal;
        LinearLayout ll_gridlayout;

        public TableViewHolder(View itemView) {
            super(itemView);

            txtitmnam = (TextView) itemView.findViewById(R.id.txtitmnam);
            txtitmrate = (TextView) itemView.findViewById(R.id.txtitmrate);
            txtsubtotal = (TextView) itemView.findViewById(R.id.txtsubtotal);
            txtitmqty = (TextView) itemView.findViewById(R.id.txtitmqty);
            ll_gridlayout = (LinearLayout) itemView.findViewById(R.id.ll_gridlayout);


            // ll_gridlayout.setOnClickListener(this);
        }

    }

}
