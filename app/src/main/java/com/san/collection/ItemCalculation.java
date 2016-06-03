package com.san.collection;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.san.collection.adapter.ItemwiseAdapter;
import com.san.collection.adapter.TablesAdapter;
import com.san.collection.app.AppController;
import com.san.collection.bean.ItemBean;
import com.san.collection.bean.TablesBean;
import com.san.collection.utils.GlobalVariables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ItemCalculation extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    RecyclerView rv_itemswise;
    Spinner spnrrest;
    SwipeRefreshLayout sr_allitems;

    int slctpos;
    String slctdrest, outlet;

    List<ItemBean> itemlist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_calculation);

        initializations();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rv_itemswise.setHasFixedSize(true);
        LinearLayoutManager llm1 = new LinearLayoutManager(this);
        llm1.setOrientation(LinearLayoutManager.VERTICAL);
        rv_itemswise.setLayoutManager(llm1);
        sr_allitems.setOnRefreshListener(this);
        final String restaurants[] = this.getResources().getStringArray(R.array.restaurants);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item, restaurants);

        dataAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);

        spnrrest.setAdapter(dataAdapter);

        spnrrest.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Toast.makeText(MainActivity.this, "You selected " + restaurants[position], Toast.LENGTH_SHORT).show();
                slctdrest = restaurants[position].toString();
                slctpos = position;

                sr_allitems.post(new Runnable() {
                    @Override
                    public void run() {
                        sr_allitems.setRefreshing(true);
                        getallitems(slctpos);
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getallitems(int slctpos) {
        rv_itemswise.setAdapter(null);
        itemlist.clear();
        String tblsUrl = null;

        if (slctpos == 0) {
            //BEL Road
            tblsUrl = GlobalVariables.chowmeinbel + "itemdailytotal.php?cdate=" + GlobalVariables.getcurrentdate();
            //tblsUrl = "http://192.168.0.102/chowmein/itemdailytotal.php?cdate=" + GlobalVariables.getcurrentdate();
        } else if (slctpos == 1) {
            tblsUrl = GlobalVariables.chowmeinkpurl + "itemdailytotal.php?cdate=" + GlobalVariables.getcurrentdate();
        }

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
                                    itmbean.setRate(jobj.getString("rate"));
                                    itmbean.setQty(jobj.getInt("qty"));
                                    itmbean.setPrice(jobj.getString("price"));

                                    itemlist.add(itmbean);
                                }
                                rv_itemswise.setAdapter(new ItemwiseAdapter(ItemCalculation.this, itemlist));

                            } else if (success == 0) {
                                Toast.makeText(ItemCalculation.this, "No Items found...", Toast.LENGTH_SHORT).show();

                            }

                            sr_allitems.setRefreshing(false);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                sr_allitems.setRefreshing(false);

            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jreq);
    }

    private void initializations() {
        spnrrest = (Spinner) findViewById(R.id.spnrrest);
        rv_itemswise = (RecyclerView) findViewById(R.id.rv_itemswise);
        sr_allitems = (SwipeRefreshLayout) findViewById(R.id.sr_allitems);

    }

    @Override
    public void onRefresh() {
        getallitems(slctpos);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                Intent hmintent = new Intent(ItemCalculation.this, MainControl.class);
                startActivity(hmintent);
                ItemCalculation.this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
