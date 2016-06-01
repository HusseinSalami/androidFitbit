package com.example.generals.fitbitmoniteringapplicationfinal;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class HistoryFragment extends Fragment {

    View rootView;
    Context context;
    ListView historyList;
    //ne pas oublier que la liste commence par 0;
    int normal_list_taille=0;
    //
    ListHistoryAdapter listAdapter;
    ArrayList<History> arrayHistory=new ArrayList<History>();
    Bundle bundle=new Bundle();
    //retrieve json file;
    private ProgressDialog pDialog=null;
    JSONArray array_json=null;
    private static String url = "http://192.168.43.103/getHRVNormalID.php";
    History history_objet=null;
    JSONObject object_i=null;

    public static final String MyPreferences="MyPrefs";

    //json history malade
    JSONArray array_json_malade=null;
    private static String url2 = "http://192.168.43.103/getHRVMaladeID.php";
    History history_objet_malade=null;
    JSONObject object_i_malade=null;

    SharedPreferences sharedPreferences;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView=inflater.inflate(R.layout.history_fragment_layout, container, false);
        context=rootView.getContext();
        historyList=(ListView)rootView.findViewById(R.id.history_listview_id);
        sharedPreferences=getActivity().getSharedPreferences(MyPreferences, 0);
   //     arrayHistory=new ArrayList<>();

        if(arrayHistory.isEmpty())
        {

            try {
                Void str= new GetHistory(context).execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }


            listAdapter=new ListHistoryAdapter(context,arrayHistory);
            historyList.setAdapter(listAdapter);
            listAdapter.notifyDataSetChanged();
            //  new  GetHistory(context).execute();
        }

        else
        {
            this.setRetainInstance(true);
            historyList.setAdapter(listAdapter);
            listAdapter.notifyDataSetChanged();
        }

        listAdapter.setButtonListenerHistory(new ListHistoryAdapter.ButtonListenerHistory() {
            @Override
            public void clickedHistory(View view, int position) {

                HistoryDetailsFragment historyDetails=new HistoryDetailsFragment();

                FragmentManager main_manager=getActivity().getSupportFragmentManager();

                // bade eb3at 3al fragment history clicked l id tabe3 l history clicked;
                int id_history=arrayHistory.get(position).getIdHistory();

                bundle.putInt("history_id",id_history);

                bundle.putInt("pnn50_value",arrayHistory.get(position).getPnn50());
                bundle.putString("history_maladie",arrayHistory.get(position).getMaladie());

                historyDetails.setArguments(bundle);

                main_manager.beginTransaction().replace(R.id.container, historyDetails).addToBackStack(null).commit();
            }

        });

        historyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

               //machi
            }
        });
        return rootView;
    }

    public static List<History> initialiserHistoryArray(List<History> array)
    {
        History a;
        for(int i=0;i<10;++i)
        {
            a=new History();
            a.setMaladie("maladie" + i);
            array.add(i,a);
        }
        return array;
    }

    private class GetHistory extends AsyncTask<Void, Void, Void> {

        Context context;

        public  GetHistory(Context context)
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
            if(android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();

            ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response
        // fix the url to retrieve the history for this user having id id1;
//url va me renvoyer les history hrv qui sont pas malades;
            String urlGeorge=url+"?username="+sharedPreferences.getString("username", "rien");
           // url=url+"?username="+sharedPreferences.getString("username", "rien");
//url2 va me renvoyer les history hrv qui sont m
// alades
            String urlHussein=url2+"?username="+sharedPreferences.getString("username","rien");

      //      url2=url2+"?username="+sharedPreferences.getString("username","rien");

            String jsonStr = sh.makeServiceCall(urlGeorge, ServiceHandler.GET);
            String jsonStr2=sh.makeServiceCall(urlHussein,ServiceHandler.GET);

            Log.d("Response: ", "> " + jsonStr);
            Log.d("Response: ", "> " + jsonStr2);

            if (jsonStr != null && jsonStr2!=null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    array_json= jsonObj.getJSONArray("data");

                    normal_list_taille=array_json.length();

                    for(int i=0;i<array_json.length();++i)
                    {
                        object_i=array_json.getJSONObject(i);

                        history_objet=new History();
                        //c dans le cas ou je cherche les history hrv qui sont pas malades;
                        history_objet.setMaladie("normal");
                        history_objet.setIdHistory(object_i.getInt("idHistory"));
                        history_objet.setPnn50(object_i.getInt("pnn50"));


                       // history_objet.setMaladieLogo();
                       arrayHistory.add(history_objet);
                    }
                    //maintenant je vais faire sa pour les history malades;

                    JSONObject jsonObjmalade = new JSONObject(jsonStr2);

                    // Getting JSON Array node
                    array_json_malade= jsonObjmalade.getJSONArray("data");

                    for(int i=0;i<array_json_malade.length();++i)
                    {
                        object_i_malade=array_json_malade.getJSONObject(i);

                        history_objet_malade=new History();


                        //c dans le cas ou je cherche les history hrv qui sont pas malades;
                        history_objet_malade.setMaladie(object_i_malade.getString("nom"));
                        history_objet_malade.setIdHistory(object_i_malade.getInt("idHistory"));
                        history_objet_malade.setPnn50(object_i_malade.getInt("pnn50"));

                        // history_objet.setMaladieLogo();
                        arrayHistory.add(history_objet_malade);
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

//            listAdapter=new ListHistoryAdapter(context,arrayHistory);
 //           historyList.setAdapter(listAdapter);

        }

    }

}
