package com.san.collection;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.san.collection.app.AppController;
import com.san.collection.bean.BillCollbean;
import com.san.collection.utils.GlobalVariables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener {

    Spinner spnrrest,spnrdaymonth;
    TextView txtdate,txtdatefrom, txtdateto;
    Button btndate, btnok, btngetbill;
    TextView txtsubtot, txtothers, txtgrndtot, txterrormsg, txtdiscount;
    static final int DATE_DIALOG_ID = 1,DATE_FROM = 2, DATE_TO = 3;

    public String reqdate = null;
    public String fromdate = null, todate = null;
    String todaydate, slctdrest;
    List<BillCollbean> billList = new ArrayList<>();
    private int year, month, day;
    ProgressDialog PD;
    int slctpos,dayposition;
    DecimalFormat decimvalue = new DecimalFormat("#.00");
    LinearLayout ll_currentday, ll_fromtodate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializations();

        final Calendar cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH) + 1;
        String month2 = String.format("%02d", month);
        day = cal.get(Calendar.DAY_OF_MONTH);
        reqdate = String.valueOf(year + "-" + month2 + "-" + day);

        ll_fromtodate.setVisibility(View.GONE);
        ll_currentday.setVisibility(View.VISIBLE);
        getbillamount();

        txtdate.setText("" + day + "-" + month2 + "-" + year);
        final String restaurants[] = this.getResources().getStringArray(R.array.restaurants);
        final String daytypes[] = this.getResources().getStringArray(R.array.daytype);


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
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<String> daytypeadapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item, daytypes);

        daytypeadapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);

        spnrdaymonth.setAdapter(daytypeadapter);

        spnrdaymonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Toast.makeText(MainActivity.this, "You selected " + daytypes[position], Toast.LENGTH_SHORT).show();
              /*  slctdrest = restaurants[position].toString();
                slctpos = position;*/

                dayposition = position;

                if(position ==0){
                    ll_currentday.setVisibility(View.VISIBLE);
                    ll_fromtodate.setVisibility(View.GONE);
                }else if(position ==1){
                    ll_currentday.setVisibility(View.GONE);
                    ll_fromtodate.setVisibility(View.VISIBLE);

                    Calendar cal = Calendar.getInstance();
                    year = cal.get(Calendar.YEAR);

                    month = cal.get(Calendar.MONTH) + 1;
                    String month2 = String.format("%02d", month);
                    day = cal.get(Calendar.DAY_OF_MONTH);

                    txtdateto.setText(day +"-"+month2 +"-"+year);
                    todate = String.valueOf(year+"-"+month2+"-"+day);

                    String lessmonth =  String.format("%02d", month-1);
                    txtdatefrom.setText(day+"-"+lessmonth+"-"+year);
                    fromdate = String.valueOf(year+"-"+(lessmonth)+"-"+day);

                    getdatafromtodate(fromdate, todate, slctpos);

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void getbillamount() {
        String url = null;
        //getdata();
        if (slctpos == 0) {
            // Toast.makeText(MainActivity.this, "BEL ROAD", Toast.LENGTH_SHORT).show();
            if(dayposition ==0) {
                url = GlobalVariables.chowmeinbel + "billcollection.php?billdate=" + reqdate;
                //Toast.makeText(MainActivity.this, " BEL DAY WISE" +url, Toast.LENGTH_SHORT).show();
                getcolldate(url);
            }else if(dayposition ==1){
               // Toast.makeText(MainActivity.this, " BEL MONTH WISE", Toast.LENGTH_SHORT).show();
              int lcldayposition = dayposition;
              /*  String fromdate = txtdatefrom.getText().toString();
                String todate = txtdateto.getText().toString();*/
              //  Toast.makeText(MainActivity.this, " BEL MONTH WISE " +lcldayposition, Toast.LENGTH_SHORT).show();
                getdatafromtodate(fromdate, todate, slctpos);
            }
        } else if (slctpos == 1) {
            if(dayposition ==0) {
                url = GlobalVariables.chowmeinkpurl + "billcollection.php?billdate=" + reqdate;

                getcolldate(url);
              //   Toast.makeText(MainActivity.this, "KP DAY WISE", Toast.LENGTH_SHORT).show();
            }else if(dayposition ==1){
              //  Toast.makeText(MainActivity.this, "KP MONTH WISE", Toast.LENGTH_SHORT).show();
                int lcldayposition = dayposition;
               /* String fromdate = txtdatefrom.getText().toString();
                String todate = txtdateto.getText().toString();*/
                getdatafromtodate(fromdate, todate, slctpos);
            }
        }


    }

    private void getcolldate(String url){

        PD = new ProgressDialog(MainActivity.this);
        PD.setMessage("Loading...");
        PD.setCancelable(false);
        PD.show();

      //  Toast.makeText(MainActivity.this," URL is" +url, Toast.LENGTH_SHORT).show();

        //JsonObjectRequest jrq = new JsonObjectRequest(url);
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
                                   /* billbean.setGrndtot(jobj.getString("sub_tot"));
                                    billbean.setOthers(jobj.getString("vat"));
                                    billbean.setGrndtot(jobj.getString("grand_amt"));
                                    // catbean.setRcatstatus(jobj.getInt("Ritem_price"));
                                    billList.add(billbean);*/

                                //}

                                String subtot = jobj.getString("sub_tot");
                                String others = jobj.getString("scamount");
                                String grndtot = jobj.getString("grand_amt");
                                String gstrdisc = jobj.getString("discount");

                                if ((subtot.equals("null") || subtot == null) && (others.equals("null") || others == null) && (grndtot.equals("null") || grndtot == null)) {
                                    //  Toast.makeText(MainActivity.this, "Null",Toast.LENGTH_SHORT).show();
                                    txtsubtot.setText("0.00");
                                    txtothers.setText("0.00");
                                    txtgrndtot.setText("0.00");
                                    txterrormsg.setText("Sorry... It seems no orders yet");
                                    txtdiscount.setText("0.00");
                                } else {
                                    txtsubtot.setText("" + decimvalue.format(Double.parseDouble(subtot)));
                                    txtothers.setText("" + decimvalue.format(Double.parseDouble(others)));
                                    txtgrndtot.setText("" + decimvalue.format(Double.parseDouble(grndtot)));
                                    if (gstrdisc.equals("0") || gstrdisc == "0") {
                                        txtdiscount.setText("0.00");
                                    } else {
                                        txtdiscount.setText("" + decimvalue.format(Double.parseDouble(gstrdisc)));
                                    }
                                    txterrormsg.setText("");
                                }


                                PD.dismiss();
                            } else if (success == 0) {
                                Toast.makeText(MainActivity.this, "No Items found...", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                txterrormsg.setText("Sorry... It seems Server closed");
                txtsubtot.setText("0.00");
                txtothers.setText("0.00");
                txtgrndtot.setText("0.00");
                txtdiscount.setText("0.00");
                PD.dismiss();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jreq);
    }

    private void getdatafromtodate(String frmdate, String todate,int slctpos) {
        String url = null;

        if (todate == null || frmdate == null) {
            Toast.makeText(MainActivity.this, "Please select dates", Toast.LENGTH_SHORT).show();
        } else {
            if (slctpos == 0) {
                url = GlobalVariables.chowmeinbel + "betweenbill.php?fromdate=" + frmdate + "&todate=" + todate;
            } else if (slctpos == 1) {
                url = GlobalVariables.chowmeinkpurl + "betweenbill.php?fromdate=" + frmdate + "&todate=" + todate;
            }

            PD = new ProgressDialog(MainActivity.this);
            PD.setMessage("Loading...");
            PD.setCancelable(false);
            PD.show();

            //JsonObjectRequest jrq = new JsonObjectRequest(url);
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
                                    JSONObject jobj = ja.getJSONObject(0);

                                    String subtot = jobj.getString("sub_tot");
                                    String others = jobj.getString("scamount");
                                    String grndtot = jobj.getString("grand_amt");
                                    String gstrdisc = jobj.getString("discount");

                                    if ((subtot.equals("null") || subtot == null) && (others.equals("null") || others == null) && (grndtot.equals("null") || grndtot == null)) {
                                        txtsubtot.setText("0.00");
                                        txtothers.setText("0.00");
                                        txtgrndtot.setText("0.00");
                                        txterrormsg.setText("Sorry... It seems no orders yet");
                                        txtdiscount.setText("0.00");
                                    } else {
                                        txtsubtot.setText("" + decimvalue.format(Double.parseDouble(subtot)));
                                        txtothers.setText("" + decimvalue.format(Double.parseDouble(others)));
                                        txtgrndtot.setText("" + decimvalue.format(Double.parseDouble(grndtot)));
                                        if (gstrdisc.equals("0") || gstrdisc == "0") {
                                            txtdiscount.setText("0.00");
                                        } else {
                                            txtdiscount.setText("" + decimvalue.format(Double.parseDouble(gstrdisc)));
                                        }
                                        txterrormsg.setText("");
                                    }


                                    PD.dismiss();
                                } else if (success == 0) {
                                    Toast.makeText(MainActivity.this, "No Items found...", Toast.LENGTH_SHORT).show();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    txterrormsg.setText("Sorry... It seems Server closed");
                    txtsubtot.setText("0.00");
                    txtothers.setText("0.00");
                    txtgrndtot.setText("0.00");
                    txtdiscount.setText("0.00");
                    PD.dismiss();
                }
            });

            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(jreq);
        }
    }

    private void initializations() {
        spnrrest = (Spinner) findViewById(R.id.spnrrest);
        spnrdaymonth = (Spinner) findViewById(R.id.spnrdaymonth);
        txtdate = (TextView) findViewById(R.id.txtdate);
        btndate = (Button) findViewById(R.id.btndate);
        btnok = (Button) findViewById(R.id.btnok);
        btngetbill = (Button) findViewById(R.id.btngetbill);
        txtsubtot = (TextView) findViewById(R.id.txtsubtot);
        txtothers = (TextView) findViewById(R.id.txtothers);
        txtgrndtot = (TextView) findViewById(R.id.txtgrndtot);
        txterrormsg = (TextView) findViewById(R.id.txterrormsg);
        txtdiscount = (TextView) findViewById(R.id.txtdiscount);

        ll_fromtodate = (LinearLayout) findViewById(R.id.ll_fromtodate);
        ll_currentday = (LinearLayout) findViewById(R.id.ll_currentday);

        txtdateto = (TextView) findViewById(R.id.txtdateto);
        txtdatefrom = (TextView) findViewById(R.id.txtdatefrom);

        btndate.setOnClickListener(this);
        btnok.setOnClickListener(this);
        btngetbill.setOnClickListener(this);

        txtdateto.setOnClickListener(this);
        txtdatefrom.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btndate:
                showDialog(DATE_DIALOG_ID);
                break;

            case R.id.btnok:
                finish();
                break;

            case R.id.btngetbill:
                getbillamount();
                break;
            case R.id.txtdatefrom:
                showDialog(DATE_FROM);
                break;
            case R.id.txtdateto:
                showDialog(DATE_TO);
                break;
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                DatePickerDialog dialog = new DatePickerDialog(MainActivity.this, dateListener, year, month, day);
                dialog.getDatePicker().setMaxDate(new Date().getTime());

                return dialog;

            case DATE_FROM:
                DatePickerDialog dialog1 = new DatePickerDialog(MainActivity.this, dateListener1, year, month, day);
                dialog1.getDatePicker().setMaxDate(new Date().getTime());

                return dialog1;
            case DATE_TO:
                DatePickerDialog dialog2 = new DatePickerDialog(MainActivity.this, dateListener2, year, month, day);
                dialog2.getDatePicker().setMaxDate(new Date().getTime());

                return dialog2;
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener dateListener =
            new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker view, int yr, int monthOfYear,
                                      int dayOfMonth) {
                    year = yr;
                    month = monthOfYear + 1;
                    day = dayOfMonth;
                    String month1 = String.format("%02d", month);

                        txtdate.setText(day + "-" + month1 + "-" + year);
                        reqdate = String.valueOf(year + "-" + month1 + "-" + day);

                }
            };

    private DatePickerDialog.OnDateSetListener dateListener1 =
            new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker view, int yr, int monthOfYear,
                                      int dayOfMonth) {
                    year = yr;
                    month = monthOfYear + 1;
                    day = dayOfMonth;
                    String month1 = String.format("%02d", month);
                     txtdatefrom.setText(day + "-" + month1 + "-" + year);
                    fromdate = String.valueOf(year + "-" + month1 + "-" + day);

                }
            };

    private DatePickerDialog.OnDateSetListener dateListener2 =
            new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker view, int yr, int monthOfYear,
                                      int dayOfMonth) {
                    year = yr;
                    month = monthOfYear + 1;
                    day = dayOfMonth;
                    String month1 = String.format("%02d", month);
                    txtdateto.setText(day + "-" + month1 + "-" + year);
                    todate = String.valueOf(year + "-" + month1 + "-" + day);

                }
            };
}
