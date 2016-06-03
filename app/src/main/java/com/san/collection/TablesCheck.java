package com.san.collection;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.san.collection.adapter.TablesAdapter;
import com.san.collection.app.AppController;
import com.san.collection.bean.TablesBean;
import com.san.collection.utils.GlobalVariables;
import com.san.collection.utils.SpacesItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TablesCheck extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    RecyclerView rv_tables;
    SwipeRefreshLayout sr_alltables;
    Spinner spnrresttbl;
    TextView txtdiningtotal, txtpaidtotal, txtothertotal, txtdscnttotal, txttotalamount, txtparceltotal;
    int slctpos;
    String outlet = null;
    DecimalFormat decimvalue = new DecimalFormat("#.00");
    String slctdrest;
    private int year, month, day;
    public String reqdate = null;

    List<TablesBean> tblsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tables_check);

        initializations();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rv_tables.setHasFixedSize(true);

        GridLayoutManager glm = new GridLayoutManager(this, 4);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing);
        rv_tables.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        rv_tables.setLayoutManager(glm);

        sr_alltables.setOnRefreshListener(this);

        final Calendar cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH) + 1;
        String month2 = String.format("%02d", month);
        day = cal.get(Calendar.DAY_OF_MONTH);
        reqdate = String.valueOf(year + "-" + month2 + "-" + day);

        final String restaurants[] = this.getResources().getStringArray(R.array.restaurants);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item, restaurants);

        dataAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);

        spnrresttbl.setAdapter(dataAdapter);

        spnrresttbl.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Toast.makeText(MainActivity.this, "You selected " + restaurants[position], Toast.LENGTH_SHORT).show();
                slctdrest = restaurants[position].toString();
                slctpos = position;

                sr_alltables.post(new Runnable() {
                    @Override
                    public void run() {
                        sr_alltables.setRefreshing(true);
                        gettablesdetails(slctpos);
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initializations() {
        rv_tables = (RecyclerView) findViewById(R.id.rv_tables);
        sr_alltables = (SwipeRefreshLayout) findViewById(R.id.sr_alltables);
        spnrresttbl = (Spinner) findViewById(R.id.spnrresttbl);
        txtdiningtotal = (TextView) findViewById(R.id.txtdiningtotal);
        txtpaidtotal = (TextView) findViewById(R.id.txtpaidtotal);
        txtparceltotal = (TextView) findViewById(R.id.txtparceltotal);
        txtothertotal = (TextView) findViewById(R.id.txtothertotal);
        txtdscnttotal = (TextView) findViewById(R.id.txtdscnttotal);
        txttotalamount = (TextView) findViewById(R.id.txttotalamount);

    }


    private void gettablesdetails(int slctpos) {

        rv_tables.setAdapter(null);
        tblsList.clear();
        String tblsUrl = null;

        if (slctpos == 0) {
            tblsUrl = GlobalVariables.chowmeinbel + "getalltablesdtls.php?area=Hall";
            outlet = GlobalVariables.chowmeinbel;

        } else if (slctpos == 1) {
            tblsUrl = GlobalVariables.chowmeinkpurl + "getalltablesdtls.php?area=Hall";
            outlet = GlobalVariables.chowmeinkpurl;
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
                                    TablesBean tblbean = new TablesBean();
                                    JSONObject jobj = ja.getJSONObject(i);
                                    tblbean.setId(jobj.getInt("id"));
                                    tblbean.setTableno(jobj.getInt("tableno"));
                                    tblbean.setTstatus(jobj.getInt("tstatus"));
                                    tblbean.setSeats(jobj.getInt("seats"));

                                    tblsList.add(tblbean);
                                }
                                rv_tables.setAdapter(new TablesAdapter(TablesCheck.this, tblsList, outlet));

                                txtpaidtotal.setText("0.00");
                                txtdiningtotal.setText("0.00");
                                txtparceltotal.setText("0.00");
                                txtothertotal.setText("0.00");
                                txtdscnttotal.setText("0.00");
                                txttotalamount.setText("0.00");

                                gettotalbilleach(outlet);
                                getcolldate(outlet);
                                getparcelamount(outlet);

                            } else if (success == 0) {
                                Toast.makeText(TablesCheck.this, "No Items found...", Toast.LENGTH_SHORT).show();
                                txtpaidtotal.setText("0.00");
                                txtdiningtotal.setText("0.00");
                                txtparceltotal.setText("0.00");
                                txtothertotal.setText("0.00");
                                txtdscnttotal.setText("0.00");
                                txttotalamount.setText("0.00");

                            }

                            sr_alltables.setRefreshing(false);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                sr_alltables.setRefreshing(false);

            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jreq);
    }

    private void gettotalbilleach(final String outlet) {

        String tblsUrl = outlet + "totaldining.php?cdate=" + GlobalVariables.getcurrentdate();

        //  Toast.makeText(TablesCheck.this, " Table URL :" +tblsUrl, Toast.LENGTH_LONG).show();
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
                                // for (int i = 0; i < ja.length(); i++) {
                                JSONObject jobj = ja.getJSONObject(0);
                                int price = jobj.getInt("price");

                                // }
                                String strpprice = String.valueOf(price);
                                // txtdiningtotal.setText("" + decimvalue.format(Double.parseDouble(strpprice)));
                                txtdiningtotal.setText(String.format("%.2f", Double.parseDouble(strpprice)));

                            } else if (success == 0) {
                                Toast.makeText(TablesCheck.this, "No Items found...", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                sr_alltables.setRefreshing(false);

            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jreq);
    }

    private void getcolldate(final String outlet) {

        //  Toast.makeText(MainActivity.this," URL is" +url, Toast.LENGTH_SHORT).show();

        //JsonObjectRequest jrq = new JsonObjectRequest(url);

        String url = outlet + "billcollection.php?billdate=" + reqdate;
        JsonObjectRequest jreq = new JsonObjectRequest(Request.Method.GET, url, null,
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
                                //for (int i = 0; i < ja.length(); i++) {
                                // BillCollbean billbean = new BillCollbean();
                                JSONObject jobj = ja.getJSONObject(0);

                                //}

                                String subtot = jobj.getString("sub_tot");
                                String others = jobj.getString("scamount");
                                String grndtot = jobj.getString("grand_amt");
                                String gstrdisc = jobj.getString("discount");

                                if ((subtot.equals("null") || subtot == null) && (others.equals("null") || others == null) && (grndtot.equals("null") || grndtot == null)) {
                                    //  Toast.makeText(MainActivity.this, "Null",Toast.LENGTH_SHORT).show();
                                    txtpaidtotal.setText("0.00");
                                    txtothertotal.setText("0.00");
                                    txttotalamount.setText("0.00");
                                    txtdscnttotal.setText("0.00");
                                } else {
                                   /* txtpaidtotal.setText("" + decimvalue.format(Double.parseDouble(subtot)));
                                    txtothertotal.setText("" + decimvalue.format(Double.parseDouble(others)));
                                    txttotalamount.setText("" + decimvalue.format(Double.parseDouble(grndtot)));*/

                                    txtpaidtotal.setText(String.format("%.2f", Double.parseDouble(subtot)));
                                    txtothertotal.setText(String.format("%.2f", Double.parseDouble(others)));
                                    txttotalamount.setText(String.format("%.2f", Double.parseDouble(grndtot)));
                                    if (gstrdisc.equals("0") || gstrdisc == "0") {
                                        txtdscnttotal.setText("0.00");
                                    } else {
                                        // txtdscnttotal.setText("" + decimvalue.format(Double.parseDouble(gstrdisc)));
                                        txtdscnttotal.setText(String.format("%.2f", Double.parseDouble(gstrdisc)));
                                    }
                                }

                                //getparcelamount(outlet);

                            } else if (success == 0) {
                                Toast.makeText(TablesCheck.this, "No Items found...", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jreq);
    }

    private void getparcelamount(String outlet) {
        String tblsUrl = outlet + "totalparcel.php?cdate=" + GlobalVariables.getcurrentdate();
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
                                // for (int i = 0; i < ja.length(); i++) {
                                JSONObject jobj = ja.getJSONObject(0);
                                int price = jobj.getInt("price");

                                // }

                                String strprice = String.valueOf(price);
                                // txtparceltotal.setText("" + decimvalue.format(Double.parseDouble(strprice)));
                                txtparceltotal.setText(String.format("%.2f", Double.parseDouble(strprice)));


                            } else if (success == 0) {
                                Toast.makeText(TablesCheck.this, "No Items found...", Toast.LENGTH_SHORT).show();

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                sr_alltables.setRefreshing(false);

            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jreq);
    }

    @Override
    public void onRefresh() {
        gettablesdetails(slctpos);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                Intent hmintent = new Intent(TablesCheck.this, MainControl.class);
                startActivity(hmintent);
                TablesCheck.this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
