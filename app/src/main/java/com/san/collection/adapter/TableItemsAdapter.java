package com.san.collection.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.san.collection.R;
import com.san.collection.app.AppController;
import com.san.collection.bean.ItemBean;
import com.san.collection.bean.TablesBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SANTECH on 27/05/2016.
 */
public class TableItemsAdapter extends RecyclerView.Adapter<TableItemsAdapter.TableViewHolder> {

    Context mContext;
    List<ItemBean> tblitmslist = new ArrayList<>();

    public TableItemsAdapter(Context mContext, List<ItemBean> tblitmslist) {
        this.mContext = mContext;
        this.tblitmslist = tblitmslist;
    }

    @Override
    public TableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itmview = LayoutInflater.from(parent.getContext()).inflate(R.layout.tblordrtxt, parent, false);
        return new TableViewHolder(itmview);
    }

    @Override
    public void onBindViewHolder(TableViewHolder holder, int position) {

        final ItemBean tblbean = tblitmslist.get(position);
        holder.preitm_name.setText("" + tblbean.getItemname());
        holder.preitm_qty.setText("" + tblbean.getQty());
    }

    @Override
    public int getItemCount() {
        return tblitmslist.size();
    }

    public class TableViewHolder extends RecyclerView.ViewHolder {

        TextView preitm_name, preitm_qty;
        // LinearLayout ll_gridlayout;

        public TableViewHolder(View itemView) {
            super(itemView);

            preitm_name = (TextView) itemView.findViewById(R.id.preitm_name);
            preitm_qty = (TextView) itemView.findViewById(R.id.preitm_qty);
            // ll_gridlayout = (LinearLayout) itemView.findViewById(R.id.ll_gridlayout);

        }
    }
}
