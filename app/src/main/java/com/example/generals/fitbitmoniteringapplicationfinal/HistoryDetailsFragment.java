package com.example.generals.fitbitmoniteringapplicationfinal;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidplot.util.PixelUtils;
import com.androidplot.xy.CatmullRomInterpolator;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by generals on 05/09/2016.
 */
public class HistoryDetailsFragment extends Fragment {

    View rootView;
    Context context;

    private XYPlot plotHrv;
    private XYPlot plotRrr;
    SharedPreferences sharedPreferences;
    public static final String imagePath="profilePath";
    public static final String MyPreferences="MyPrefs";

    LineChart lineChart ;
    LineChart lineChartRR ;


    ArrayList<Entry> entries = new ArrayList<>();
    ArrayList<Entry> entriesRR = new ArrayList<>();

    Bundle bundle;

    List<HrvValue> array_hrv=new ArrayList<>();
    List<RrValue> array_rr=new ArrayList<>();

    private ProgressDialog pDialog=null;
    JSONArray array_json=null;
    private static String url = "http://192.168.43.103/getHRV.php";
    HrvValue hrv_objet=null;
    JSONObject object_i=null;

    //json history malade
    JSONArray array_json_rr=null;
    private static String url2 = "http://192.168.43.103/getRR";
    RrValue rr_objet=null;
    JSONObject object_i_rr=null;

    int id_history;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView=inflater.inflate(R.layout.history_details_fragment_layout, container, false);
        bundle= this.getArguments();
        context=rootView.getContext();

        url2 = "http://192.168.43.103/getRR";
        url = "http://192.168.43.103/getHRV.php";

        id_history=bundle.getInt("history_id");

        lineChart = (LineChart) rootView.findViewById(R.id.HRVplot);

        lineChartRR=(LineChart)rootView.findViewById(R.id.RRRplot);

        lineChart.setDescription("");
        lineChart.setNoDataTextDescription("no data");
        lineChart.setHighlightPerTapEnabled(true);
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setDrawGridBackground(false);
        lineChart.setPinchZoom(true);
        lineChart.setBackgroundColor(Color.BLACK);


        lineChartRR.setDescription("");
        lineChartRR.setNoDataTextDescription("no data");
        lineChartRR.setHighlightPerTapEnabled(true);
        lineChartRR.setTouchEnabled(true);
        lineChartRR.setDragEnabled(true);
        lineChartRR.setScaleEnabled(true);
        lineChartRR.setDrawGridBackground(false);
        lineChartRR.setPinchZoom(true);
        lineChartRR.setBackgroundColor(Color.BLACK);
    /*

        entries.add(new Entry(4f, 0));
        entries.add(new Entry(8f, 1));
        entries.add(new Entry(6f, 2));
        entries.add(new Entry(2f, 3));
        entries.add(new Entry(18f, 4));
        entries.add(new Entry(9f, 5));

        entriesRR.add(new Entry(4f, 0));
        entriesRR.add(new Entry(8f, 1));
        entriesRR.add(new Entry(6f, 2));
        entriesRR.add(new Entry(2f, 3));
        entriesRR.add(new Entry(18f, 4));
        entriesRR.add(new Entry(9f, 5));
    */


        new GetHistoryGraph(context).execute();

        LineDataSet dataset=createSet(entries,"HRV signal");
        LineDataSet datasetRR=createSet(entriesRR,"RR signal");


        ArrayList<String> labels = new ArrayList<String>();
        ArrayList<String> labelsRR = new ArrayList<String>();

        for(int j=0;j<entries.size();++j)
        {
            labels.add("");
        }

        for(int j=0;j<entriesRR.size();++j)
        {
            labelsRR.add("");
        }

        LineData data = new LineData(labels, dataset);
        LineData dataRR = new LineData(labelsRR, datasetRR);


        data.setValueTextColor(Color.WHITE);
        dataRR.setValueTextColor(Color.WHITE);


        Legend l = lineChart.getLegend();
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.WHITE);

        Legend lRR = lineChartRR.getLegend();
        lRR.setForm(Legend.LegendForm.LINE);
        lRR.setTextColor(Color.WHITE);

        XAxis x1=lineChart.getXAxis();
        x1.setTextColor(Color.WHITE);
        x1.setDrawGridLines(false);
        x1.setAvoidFirstLastClipping(true);

        XAxis x1RR=lineChartRR.getXAxis();
        x1RR.setTextColor(Color.WHITE);
        x1RR.setDrawGridLines(false);
        x1RR.setAvoidFirstLastClipping(true);

        YAxis y1=lineChart.getAxisLeft();
        y1.setTextColor(Color.WHITE);
        y1.setAxisMaxValue(220f);
        y1.setAxisMinValue(-100f);
        y1.setDrawGridLines(true);

        YAxis y1RR=lineChartRR.getAxisLeft();
        y1RR.setTextColor(Color.WHITE);
        y1RR.setAxisMaxValue(220f);
        y1RR.setAxisMinValue(-100f);
        y1RR.setDrawGridLines(true);

        lineChart.setData(data);
        lineChartRR.setData(dataRR);

        TextView maladie=(TextView)rootView.findViewById(R.id.maladie_id);
        TextView pnn=(TextView)rootView.findViewById(R.id.pnn50_id);

        String maladieValue=bundle.getString("history_maladie");
        int pnn50Value=bundle.getInt("pnn50_value");

        maladie.setText(maladieValue);
        pnn.setText(""+pnn50Value);

        return rootView;
    }
    private LineDataSet createSet(ArrayList<Entry> list, String n)
    {
        LineDataSet set=new LineDataSet(list,n);
        set.setDrawCubic(true);
        set.setCubicIntensity(0.2f);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(ColorTemplate.getHoloBlue());
        set.setCircleColor(ColorTemplate.getHoloBlue());
        set.setLineWidth(2f);
        set.setCircleSize(4f);
        set.setFillAlpha(65);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.rgb(224, 117, 117));
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(10f);
        return set;
    }


    private class GetHistoryGraph extends AsyncTask<Void, Void, Void> {

        Context context;

        public  GetHistoryGraph(Context context)
        {
            this.context=context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(rootView.getContext());
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response
            // fix the url to retrieve the history for this user having id id1;

            url=url+"?idHistory="+id_history;
            url2=url2+"?idHistory"+id_history;

            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);//get json hrv;
            String jsonStr2=sh.makeServiceCall(url2,ServiceHandler.GET);//get json rr;

            Log.d("Response: ", "> " + jsonStr);
            Log.d("Response: ", "> " + jsonStr2);

            if (jsonStr != null && jsonStr2!=null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONObject jsonObj2 = new JSONObject(jsonStr2);

                    // Getting JSON Array node
                    array_json= jsonObj.getJSONArray("data");//sa doit correspondre a une liste de hrv value;
                    array_json_rr=jsonObj2.getJSONArray("data");

                    for(int i=0;i<array_json.length();++i)
                    {
                        object_i=array_json.getJSONObject(i);

                        hrv_objet=new HrvValue();


                        //c dans le cas ou je cherche les history hrv qui sont pas malades;
                            hrv_objet.setId_hrv_value(object_i.getInt("idHRVValue"));
                            hrv_objet.setValue(object_i.getDouble("value"));
                        //    hrv_objet.setTime(object_i.getDouble("hrv_time"));
                            //remplir graph
                            entries.add(new Entry((float)hrv_objet.getValue(), i));
                            //entries.add(new Entry(4f, 0));
                            array_hrv.add(hrv_objet);
                    }
                    //maintenant je vais faire sa pour les history malades;

                    for(int i=0;i<array_json_rr.length();++i)
                    {
                        object_i_rr=array_json_rr.getJSONObject(i);

                        rr_objet=new RrValue();

                        rr_objet.setId_rr_value(object_i_rr.getInt("idRRValue"));
                        //idRRValue
                        rr_objet.setValue(object_i.getDouble("value"));
                      //  rr_objet.setTime(object_i.getDouble("rr_time"));
                        //remplir graph
                        entriesRR.add(new Entry((float)rr_objet.getValue(), i));
                        //
                        array_rr.add(rr_objet);
                    }




                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (pDialog.isShowing())
                pDialog.dismiss();

        }
    }

}
