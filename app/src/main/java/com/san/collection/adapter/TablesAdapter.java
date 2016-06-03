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
import android.widget.Button;
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
public class TablesAdapter extends RecyclerView.Adapter<TablesAdapter.TableViewHolder> {

    Context mContext;
    List<TablesBean> tbllist = new ArrayList<>();
    List<ItemBean> tblitmList = new ArrayList<>();
    String outlet;
    CustomDialog custmdlg;

    public TablesAdapter(Context mContext, List<TablesBean> tbllist, String outlet) {
        this.mContext = mContext;
        this.tbllist = tbllist;
        this.outlet = outlet;

    }

    @Override
    public TableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itmview = LayoutInflater.from(parent.getContext()).inflate(R.layout.gridtablestxt, parent, false);
        return new TableViewHolder(itmview);
    }

    @Override
    public void onBindViewHolder(TableViewHolder holder, int position) {
        final TablesBean tblbean = tbllist.get(position);
        holder.txt_tblno.setText("" + tblbean.getTableno());
        if (tblbean.getTstatus() == 0) {
            holder.txtFrEng.setText("Free");
        } else if (tblbean.getTstatus() == 1) {
            // holder.txtFrEng.setText("Occupied");
            holder.ll_gridlayout.setBackgroundResource(R.drawable.gridtbl_selectorred);
            holder.txtFrEng.setTextColor(mContext.getResources().getColor(R.color.white));
            holder.txt_tblno.setTextColor(mContext.getResources().getColor(R.color.white));
            gettablebill(tblbean.getTableno(), holder.txtFrEng);

        } else if (tblbean.getTstatus() == 2) {
            //holder.txtFrEng.setText("Bill");
            holder.ll_gridlayout.setBackgroundResource(R.drawable.gridtbl_selectorylw);
            // holder.txtFrEng.setTextColor(mContext.getResources().getColor(R.color.white));
            holder.txt_tblno.setTextColor(mContext.getResources().getColor(R.color.white));
            gettablebill(tblbean.getTableno(), holder.txtFrEng);
        }/*else if (tblbean.getTstatus() == 3) {
           // holder.txtFrEng.setText("Yey");
            holder.ll_gridlayout.setBackgroundResource(R.drawable.gridtbl_selectorylw);
            // holder.txtFrEng.setTextColor(mContext.getResources().getColor(R.color.white));
            holder.txt_tblno.setTextColor(mContext.getResources().getColor(R.color.white));
            gettablebill(tblbean.getTableno(),holder.txtFrEng);
        }*/
    }

    private void gettablebill(int tblno, final TextView txtprice) {

        String tblsUrl = outlet + "tblsamount.php?tableno=" + tblno;
        final JsonObjectRequest jreq = new JsonObjectRequest(Request.Method.GET, tblsUrl, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int success = 0;
                            try {
                                success = response.getInt("success");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if (success == 1) {
                                JSONArray ja = response.getJSONArray("items");
                                // for (int i = 0; i < ja.length(); i++) {
                                TablesBean tblbean = new TablesBean();
                                JSONObject jobj = ja.getJSONObject(0);
                                // tblbean.setId(jobj.getInt("id"));
                                int tblprice = jobj.getInt("price");
                                int table_no = jobj.getInt("table_no");
                                int order_id = jobj.getInt("order_id");
                                    /*tblbean.setTstatus(jobj.getInt("tstatus"));
                                    tblbean.setSeats(jobj.getInt("seats"));

                                    tblsList.add(tblbean);
*/
                                // }
                                txtprice.setText("" + tblprice);
                                double total = 0;

                            } else if (success == 0) {
                                //Toast.makeText(mContext, "No Items found...", Toast.LENGTH_SHORT).show();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mContext, "Error...", Toast.LENGTH_SHORT).show();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jreq);
    }

    @Override
    public int getItemCount() {
        return tbllist.size();
    }

    public class TableViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txt_tblno, txtFrEng;
        LinearLayout ll_gridlayout;

        public TableViewHolder(View itemView) {
            super(itemView);

            txt_tblno = (TextView) itemView.findViewById(R.id.txt_tblno);
            txtFrEng = (TextView) itemView.findViewById(R.id.txtFrEng);
            ll_gridlayout = (LinearLayout) itemView.findViewById(R.id.ll_gridlayout);


            ll_gridlayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ll_gridlayout:
                    gettableitems(outlet, getAdapterPosition());
                    break;
            }
        }
    }

    private void gettableitems(String outlet, int postion) {
        tblitmList.clear();
         String tblsUrl = outlet + "tbaleitems.php?table_no=" + tbllist.get(postion).getTableno() + "&odate=" + GlobalVariables.getcurrentdate();

       // String tblsUrl = "http://192.168.0.102/chowmein/tbaleitems.php?table_no=" + tbllist.get(postion).getTableno() + "&odate=" + GlobalVariables.getcurrentdate();
        JsonObjectRequest jreq = new JsonObjectRequest(Request.Method.GET, tblsUrl, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int success = 0;
                            try {
                                success = response.getInt("success");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if (success == 1) {
                                JSONArray ja = response.getJSONArray("items");
                                for (int i = 0; i < ja.length(); i++) {
                                    ItemBean itmbean = new ItemBean();
                                    JSONObject jobj = ja.getJSONObject(i);
                                    itmbean.setItemname(jobj.getString("item_name"));
                                    itmbean.setQty(jobj.getInt("qty"));

                                    tblitmList.add(itmbean);
                                }

                                getitemsinview(tblitmList);
                                //rv_.setAdapter(new TablesAdapter(mContext, tblsList, outlet));

                            } else if (success == 0) {
                                Toast.makeText(mContext, "No Items found...", Toast.LENGTH_SHORT).show();

                            }

                            // sr_alltables.setRefreshing(false);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //sr_alltables.setRefreshing(false);

            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jreq);
    }

    private void getitemsinview(List<ItemBean> tblitemlist) {
        custmdlg = new CustomDialog(mContext);
        custmdlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        custmdlg.setContentView(R.layout.tblitemslayout);
        WindowManager.LayoutParams a = custmdlg.getWindow().getAttributes();
        custmdlg.getWindow().setAttributes(a);
        custmdlg.setCancelable(false);
        custmdlg.getWindow().setLayout(ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.WRAP_CONTENT);
        custmdlg.show();

        RecyclerView rv_items = (RecyclerView) custmdlg.findViewById(R.id.rv_items);
        Button btnok = (Button) custmdlg.findViewById(R.id.btnok);
        rv_items.setHasFixedSize(true);
        LinearLayoutManager llm1 = new LinearLayoutManager(mContext);
        llm1.setOrientation(LinearLayoutManager.VERTICAL);
        rv_items.setLayoutManager(llm1);

        rv_items.setAdapter(new TableItemsAdapter(mContext, tblitemlist));

        btnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                custmdlg.dismiss();
            }
        });

    }
}
