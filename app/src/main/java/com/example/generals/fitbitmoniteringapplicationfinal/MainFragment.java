package com.example.generals.fitbitmoniteringapplicationfinal;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainFragment extends Fragment{

    View rootView;
    Context context;

    public static final String MyPreferences="MyPrefs";
    public static final String toRegister="my_first_time";
    public static final String imagePath="profilePath";

    List<Maladie> list_maladie=new ArrayList<>();

    SharedPreferences sharedPreferences;
    Bitmap bitmap;
    String picturePath;

    private boolean isDone = false;

    private LineChart mChart;
    private LineChart mChartRR;
    private static String ip="192.168.43.103";


    private static String url_InsertMaladie="http://"+ip+"/addHistoryMaladie.php";

    private static String url_InsertHrv = "http://"+ip+"/addHRV.php";

    private static String url_getMaladie = "http://"+ip+"/allMaladies.php";

    private static String url_updatePnn50 = "http://"+ip+"/updatePNN.php";

    private static Handler mainHandler = new Handler(Looper.getMainLooper());

    private int hrvListOuterIndex;
    private int hrvListInnerIndex;

    private static boolean alreadyStarted = false;

    JSONArray array_maladie=null;
    JSONObject object_i_maladie=null;

    private ProgressDialog pDialog=null;
    JSONArray array_json_hrv=null;
    JSONArray array_json_rr;

    JSONArray array_hrv_value=null;
    JSONArray array_rr_value=null;
    private static String url = "http://"+ip+"/getRRHistory.php";


    JSONObject object_i_rr=null;
    List<HistoryHrv> list_hrv=new ArrayList<HistoryHrv>();
    List<HistoryRr> list_rr=new ArrayList<HistoryRr>();

    String maladie_nom="";
    int id_hrv_malade=-1;
    String jsonStr2;
    String jsonStr3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {



        rootView=inflater.inflate(R.layout.main_fragment_layout, container, false);

        View keybordView=getActivity().getCurrentFocus();

        if(keybordView!=null)
        {
            InputMethodManager imm=(InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(keybordView.getWindowToken(),0);
        }
        if(list_maladie.isEmpty())
        {
            ArrayList<String> symp = new ArrayList<String>();
            symp.add("Fatigue");
            symp.add("Anxiete");
            symp.add("Diarrhee");
            InitierMaladie("Fibrilation-Ventriculaire", symp);

            symp=new ArrayList<>();
            symp.add("Court Souffle");
            symp.add("Malaise Poitrine");
            symp.add("Difficulte Effort Physique");
            InitierMaladie("Flutter", symp);
        }


        url_InsertMaladie="http://"+ip+"/addHistoryMaladie.php";
        url_InsertHrv = "http://"+ip+"/addHRV.php";

        //  url_getMaladie = "http://"+ip+"/getHRVHistory.php";

        url_updatePnn50 = "http://"+ip+"/updatePNN.php";
        url = "http://"+ip+"/getRRHistory.php";

        url_getMaladie = "http://"+ip+"/allMaladies.php";

        context=rootView.getContext();

        sharedPreferences=getActivity().getSharedPreferences(MyPreferences, 0);

        mChart=(LineChart)rootView.findViewById(R.id.chartHRV);

        mChartRR=(LineChart)rootView.findViewById(R.id.chartRRR);

        mChart.setDescription("");
        mChart.setNoDataTextDescription("no data");
        mChart.setHighlightPerTapEnabled(true);
        mChart.setTouchEnabled(true);
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);
        mChart.setPinchZoom(true);
        mChart.setBackgroundColor(Color.BLACK);

        final LineData data=new LineData();
        data.setValueTextColor(Color.WHITE);
        mChart.setData(data);

        Legend l=mChart.getLegend();
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.WHITE);

        XAxis x1=mChart.getXAxis();
        x1.setTextColor(Color.WHITE);
        x1.setDrawGridLines(false);
        x1.setAvoidFirstLastClipping(true);
        x1.setAxisMaxValue(2000f);

        YAxis y1=mChart.getAxisLeft();
        y1.setTextColor(Color.WHITE);
        y1.setAxisMaxValue(500f);
        y1.setAxisMinValue(0f);
        y1.setDrawGridLines(true);

        YAxis y12=mChart.getAxisRight();
        y12.setEnabled(false);
        mChart.notifyDataSetChanged();
        mChart.setVisibleXRange(0, 6);
        mChart.moveViewToX(data.getXValCount() - 7);

//RR chart
        mChartRR.setDescription("");
        mChartRR.setNoDataTextDescription("no data");
        mChartRR.setHighlightPerTapEnabled(true);
        mChartRR.setTouchEnabled(true);
        mChartRR.setDragEnabled(true);
        mChartRR.setScaleEnabled(true);
        mChartRR.setDrawGridBackground(false);
        mChartRR.setPinchZoom(true);
        mChartRR.setBackgroundColor(Color.BLACK);

        LineData dataRR=new LineData();
        dataRR.setValueTextColor(Color.WHITE);
        mChartRR.setData(dataRR);

        Legend lRR=mChartRR.getLegend();
        lRR.setForm(Legend.LegendForm.LINE);
        lRR.setTextColor(Color.WHITE);

        XAxis x1RR=mChartRR.getXAxis();
        x1RR.setTextColor(Color.WHITE);
        x1RR.setDrawGridLines(false);
        x1RR.setAvoidFirstLastClipping(true);
        x1RR.setAxisMaxValue(2000f);

        YAxis y1RR=mChartRR.getAxisLeft();
        y1RR.setTextColor(Color.WHITE);
        y1RR.setAxisMaxValue(175f);
        y1RR.setAxisMinValue(0f);
        y1RR.setDrawGridLines(true);

        YAxis y12RR=mChartRR.getAxisRight();
        y12RR.setEnabled(false);
        mChartRR.notifyDataSetChanged();
        mChartRR.setVisibleXRange(0, 6);
        mChartRR.moveViewToX(data.getXValCount() - 7);

        final DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);

        drawer.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                View header = (View) drawer.findViewById(R.id.header);
                String usernamePost;
                ImageView profile_header = (ImageView) header.findViewById(R.id.profile);
                TextView profile_name=(TextView)header.findViewById(R.id.nav_username_id);

                if (sharedPreferences.contains(imagePath)) {
                    picturePath = sharedPreferences.getString(imagePath, "rien");

                    bitmap = (BitmapFactory.decodeFile(picturePath));

                } else {
                    bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                }

                bitmap = getRoundedShape(bitmap);
                profile_header.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 500, 500, false));


                if(sharedPreferences.contains("username"))
                {
                    usernamePost= sharedPreferences.getString("username","rien");
                }
                else
                {
                    usernamePost="username";
                }

                profile_name.setText(usernamePost);

            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        return rootView;
    }

    private void InitierMaladie(String maladie,ArrayList<String> symp) {
        Maladie m=new Maladie();
        List<Symptomes> s=new ArrayList<>();
        Symptomes sym = new Symptomes();
       for(int i=0;i<symp.size();++i) {
           sym=new Symptomes();
           sym.setSymptomes(symp.get(i));
           s.add(sym);
       }

        m.setNom(maladie);
        m.setList_symptomes(s);

        list_maladie.add(m);


    }

    private void showMaladies() {
        //get All maladies;
        // Toast.makeText(rootView.getContext(),"dialog error",Toast.LENGTH_LONG).show();

        final Dialog dialog = new Dialog(getActivity());

        dialog.setContentView(R.layout.custom_box_layout);
        dialog.getWindow().setBackgroundDrawableResource(R.color.colorPrimaryDark);
        dialog.getWindow().setTitleColor(Color.WHITE);
        LinearLayout main_linear = (LinearLayout) dialog.findViewById(R.id.dialog_main_layout);

        ScrollView scroll = (ScrollView)dialog.findViewById(R.id.dialog_scroll_view);

        LinearLayout scrollContainer = new LinearLayout(context);
        scrollContainer.setOrientation(LinearLayout.VERTICAL);
        scrollContainer.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        scroll.addView(scrollContainer);

        dialog.setTitle("Symptomes");

        final HashMap<Integer, RadioButton> buttons = new HashMap<>();

        for (int i = 0; i < list_maladie.size(); ++i) {
            LinearLayout LL = new LinearLayout(context);
            LL.setOrientation(LinearLayout.VERTICAL);
            LL.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            String symptText = "";
            TextView sympt = new TextView(context);
            sympt.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            LinearLayout LL_header = new LinearLayout(context);
            LL_header.setOrientation(LinearLayout.HORIZONTAL);
            LL_header.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            //                            TextView maladieDescription =new TextView(context);
            RadioButton butMaladie = new RadioButton(context);
            butMaladie.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        for (RadioButton r : buttons.values()) {
                            if (r != compoundButton) {
                                r.setChecked(false);
                            }
                        }
                    }
                }
            });
            buttons.put(i, butMaladie);
            //                              maladieDescription.setText(list_maladie.get(i).getNom());

            //                            LL_header.addView(maladieDescription);
            LL_header.addView(butMaladie);

            LL.addView(LL_header);

            for(Symptomes symptomes : list_maladie.get(i).getList_Symptomes()) {
                symptText = symptText + symptomes.getSymptomes() + "\n \n ";

            }
            sympt.setTextColor(Color.WHITE);
            sympt.setText(symptText);

            LL.addView(sympt);
            scrollContainer.addView(LL);
        }

        // main_linear.addView(LL);

        Button confirm = (Button)main_linear.findViewById(R.id.dialog_confirm_button);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int position = -1;
                int count = 0;
                for (int i = 0; i < list_maladie.size(); ++i) {

                    RadioButton radio = buttons.get(i);
                    if (radio.isChecked()) {
                        //jaurai besoin de la valeur pour envoyer a la base de donner et pour
                        //faire insert dans la table des hrv malade le id du hrv malade avec la maladie;
                        position = i;
                        count++;
                        // break;
                    } else {
                        //rien
                    }

                }
                if (count > 1) {
                    Snackbar.make(view, "choose one section of symptoms", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                } else {
                    insertMaladie(list_hrv.get(hrvListOuterIndex).getIdHistoryHrv(), position);
                    dialog.dismiss();
                    if (position == -1) {
                        Toast.makeText(context, "you are suffering from bloc auriculo-ventriculaire", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context, "you are suffering from " + list_maladie.get(position).getNom(), Toast.LENGTH_LONG).show();
                    }


                }
                processChunk();
            }
        });
        dialog.setCancelable(false);

        dialog.show();
    }

    private boolean checkForMaladies(HistoryHrv historyHrv) {
        if(historyHrv.getPnn50()<0.03*historyHrv.getList_valeur().size() || historyHrv.getPnn50()>0.75*historyHrv.getList_valeur().size())
        {
            showMaladies();
            return true;
        }
        return false;
    }

    private void processChunk(){
        if (isDone) return;

        HistoryHrv historyHrv = list_hrv.get(hrvListOuterIndex);
        HrvValue hrvValue = historyHrv.getList_valeur().get(hrvListInnerIndex);
        HistoryRr historyRr = list_rr.get(hrvListOuterIndex);
        RrValue rrValue = historyRr.getList_rr_value().get(hrvListInnerIndex);

        addEntry(hrvValue.getValue()*1000,rrValue.getValue()*100,"HRV signal (x10e3)","RR signal(x10e2)");

        boolean shouldStop = false;
        hrvListInnerIndex++;
        if (hrvListInnerIndex >= historyHrv.getList_valeur().size()) {
            hrvListInnerIndex = 0;
            hrvListOuterIndex++;
            shouldStop = shouldStop || checkForMaladies(historyHrv);
            if (hrvListOuterIndex >= list_hrv.size()) {
                hrvListOuterIndex = 0;
                isDone = true;
                shouldStop = true;
            }
        }

        if (shouldStop) {
            return;
        }
        mainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                processChunk();
            }
        }, 300);
    }

    private void continueProcessingData() {
        hrvListOuterIndex = 0;
        hrvListInnerIndex = 0;
        processChunk();
    }

    @Override
    public void onResume() {
        super.onResume();
        //chercher la liste des signaux'
        //ensuite iterer sur chaque signal, en faisant addEntry pour chaque valeur ;

        //
        if (alreadyStarted) return;
        alreadyStarted = true;

        new GetHistoryGraph(context).execute();
    }

    private void addEntry(String n) {
        LineData data = mChart.getData();

        if (data != null)
        {
            LineDataSet set= (LineDataSet) data.getDataSetByIndex(0);
            if(set ==null)
            {
                set=createSet(n);
                data.addDataSet(set);

            }

            data.addXValue("");
            data.addEntry(new Entry((float) (Math.random() * 75) + 60f, set.getEntryCount()), 0);
            mChart.notifyDataSetChanged();
            mChart.setVisibleXRange(6, 6);
            mChart.moveViewToX(data.getXValCount()-7);

        }


    }

    //cest celle utiliser avec la base de donner;

    private void addEntry(double value,double valueRR,String nHRV,String nRR) {
        LineData data = mChart.getData();
        LineData dataRR =mChartRR.getData();

        if (data != null)
        {
            LineDataSet set= (LineDataSet) data.getDataSetByIndex(0);
            // set.setLabel(nHRV);
            if(set ==null)
            {
                // LineDataSet dataset=createSet(entries,"HRV signal")
                set=createSet(nHRV);
                data.addDataSet(set);

            }

            data.addXValue("");
            data.addEntry(new Entry((float) value, set.getEntryCount()), 0);
            mChart.notifyDataSetChanged();
            mChart.setVisibleXRange(6, 6);
            mChart.moveViewToX(data.getXValCount()-7);

        }

        if (dataRR != null)
        {
            LineDataSet set= (LineDataSet) dataRR.getDataSetByIndex(0);
            if(set ==null)
            {
                set=createSetRR(nRR);
                dataRR.addDataSet(set);
                //    set.setLabel(nRR);

            }

            dataRR.addXValue("");
            dataRR.addEntry(new Entry((float) valueRR, set.getEntryCount()), 0);
            mChartRR.notifyDataSetChanged();
            mChartRR.setVisibleXRange(6, 6);
            mChartRR.moveViewToX(dataRR.getXValCount()-7);
        }
    }

    private void addEntryRR(String n) {
        LineData data = mChartRR.getData();

        if (data != null)
        {
            LineDataSet set= (LineDataSet) data.getDataSetByIndex(0);
            if(set ==null)
            {
                set=createSet(n);
                data.addDataSet(set);

            }

            data.addXValue("");
            data.addEntry(new Entry((float) (Math.random() * 75) + 60f, set.getEntryCount()), 0);
            mChartRR.notifyDataSetChanged();
            mChartRR.setVisibleXRange(6, 6);
            mChartRR.moveViewToX(data.getXValCount()-7);

        }


    }

    private LineDataSet createSet(String n) {
        LineDataSet set=new LineDataSet(null,n);
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
        set.setLabel(n);
        return set;
    }

    private LineDataSet createSetRR(String n) {
        LineDataSet set=new LineDataSet(null,n);
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
        set.setLabel(n);
        return set;
    }



    public Bitmap getRoundedShape(Bitmap scaleBitmapImage) {
        int targetWidth = 500;
        int targetHeight = 500;
        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth,
                targetHeight,Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(targetBitmap);
        Path path = new Path();
        path.addCircle(((float) targetWidth - 1) / 2,
                ((float) targetHeight - 1) / 2,
                (Math.min(((float) targetWidth),
                        ((float) targetHeight)) / 2),
                Path.Direction.CCW);

        canvas.clipPath(path);
        Bitmap sourceBitmap = scaleBitmapImage;
        canvas.drawBitmap(sourceBitmap,
                new Rect(0, 0, sourceBitmap.getWidth(),
                        sourceBitmap.getHeight()),
                new Rect(0, 0, targetWidth, targetHeight), null);
        return targetBitmap;
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
            //get JSONRR
            if(android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();

            ServiceHandler sh = new ServiceHandler();

            //modifier url pour emmener les rr de ce user donc:
            String userName=sharedPreferences.getString("username", "rien");
            url=url+"?username="+userName;
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);

            Log.v("GetHistory", jsonStr);
            int pnn50=0;


            Log.d("Response: ", "> " + jsonStr);

            HashMap<Integer, HistoryRr> signals = new HashMap<>();

            if (jsonStr != null) {
                try {
                    JSONObject jsonObjRR = new JSONObject(jsonStr);

                    array_json_rr = jsonObjRR.getJSONArray("data");

                    for (int i = 0; i < array_json_rr.length(); i++) {
                        JSONObject jsonHistoryElement = array_json_rr.getJSONObject(i);
                        int idHistory = jsonHistoryElement.getInt("idHistory");
                        RrValue rrValue = new RrValue(jsonHistoryElement);
                        HistoryRr historyRr = signals.get(idHistory);
                        if (historyRr == null) {
                            historyRr = new HistoryRr(idHistory);
                            signals.put(idHistory, historyRr);
                        }
                        historyRr.getList_rr_value().add(rrValue);
                    }
                } catch (JSONException e) {
                    Log.e("GetHistory", "Failed to parse history", e);
                }
            }
            Integer[] keys = signals.keySet().toArray(new Integer[signals.size()]);
            Arrays.sort(keys);
            list_rr = new ArrayList<>();
            for (int i : keys) {
                list_rr.add(signals.get(i));
            }
            list_hrv = new ArrayList<>();
            for (HistoryRr historyRr : list_rr) {
                HistoryHrv historyHrv = new HistoryHrv(historyRr);
                list_hrv.add(historyHrv);
            }
            url_InsertHrv = "http://"+ip+"/addHRV.php";
            url_updatePnn50 = "http://"+ip+"/updatePNN.php";
            for (HistoryHrv historyHrv : list_hrv) {
                for (HrvValue value: historyHrv.getList_valeur()) {
                    String url = url_InsertHrv + "?" + "idHistory=" + historyHrv.getIdHistoryHrv() + "&value=" + value.getValue() + "&pnn50=0";
                    sh.makeServiceCall(url, ServiceHandler.GET);
                }

                String url = url_updatePnn50+"?"+"idHistory="+historyHrv.getIdHistoryHrv()+"&value="+historyHrv.getPnn50();
                sh.makeServiceCall(url, ServiceHandler.GET);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (pDialog.isShowing())
                pDialog.dismiss();
            continueProcessingData();
        }
    }

    private class GetMaladie extends AsyncTask<Void, Void, Void> {

        Context context;

        public  GetMaladie(Context context)
        {
            this.context=context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(rootView.getContext());
            pDialog.setMessage("Please wait...GetMaladi");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            //get JSONRR
            if(android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();

            ServiceHandler sh = new ServiceHandler();

            //modifier url pour emmener les rr de ce user donc:
            String userName=sharedPreferences.getString("username", "rien");

            //url_getMaladie=url_getMaladie+"?username="+userName;

            String jsonStr = sh.makeServiceCall(url_getMaladie, ServiceHandler.GET);

            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObjRR = new JSONObject(jsonStr);

                    array_maladie= jsonObjRR.getJSONArray("data");

                    String maladie="";

                    List<Symptomes> symptomesMaladie=new ArrayList<>();

                    Maladie m=new Maladie();

                    for(int i=0;i<array_maladie.length();++i) {

                        object_i_maladie=array_maladie.getJSONObject(i);

                        if(maladie.equals(""))
                        {
                            //1er iteration
                            maladie=object_i_maladie.getString("maladie").toString();
                            m=new Maladie();
                            m.setNom(maladie);
                            symptomesMaladie=new ArrayList<>();
                            Symptomes s=new Symptomes();
                            s.setSymptomes(object_i_maladie.getString("symptome"));
                            symptomesMaladie.add(s);
                        }
                        else
                        {
                            if(maladie.equals(object_i_maladie.getString("maladie")))
                            {
                                Symptomes s=new Symptomes();
                                s.setSymptomes(object_i_maladie.getString("maladie"));
                                symptomesMaladie.add(s);
                            }
                            else
                            {
                                m.setList_symptomes(symptomesMaladie);
                                list_maladie.add(m);

                                symptomesMaladie=new ArrayList<>();
                                m=new Maladie();

                                maladie=object_i_maladie.getString("maladie").toString();
                                m.setNom(maladie);

                                Symptomes s=new Symptomes();
                                s.setSymptomes(object_i_maladie.getString("symptome"));
                                symptomesMaladie.add(s);
                            }

                        }
                    }
                }
                catch (JSONException e) {
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

            showMaladies();

        }
    }


    private void insertMaladie(int id_hrv,int maladie_position)
    {

        id_hrv_malade=id_hrv;
        if(maladie_position==-1)
        {
            maladie_nom="Bloc Auriculo-Ventriculaire";
        }
        else
        {
            maladie_nom = list_maladie.get(maladie_position).getNom();
        }
        new InsertMaladie(context).execute();

    }

    private class InsertMaladie extends AsyncTask<Void, Void, Void> {

        Context context;

        public  InsertMaladie(Context context)
        {
            this.context=context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(rootView.getContext());
            pDialog.setMessage("Please wait...InsertMaladi");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            //get JSONRR
            if(android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();

            ServiceHandler sh = new ServiceHandler();

            String jsonStr;
            String oldUsername= sharedPreferences.getString("username", "rien");

            try {
                maladie_nom = URLEncoder.encode(maladie_nom, "UTF-8");
            } catch (UnsupportedEncodingException e) {}

            String query = "idHistory="+id_hrv_malade+"&nom="+maladie_nom;

            String url = url_InsertMaladie+"?"+ query;

            jsonStr=sh.makeServiceCall(url,ServiceHandler.GET);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node

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
