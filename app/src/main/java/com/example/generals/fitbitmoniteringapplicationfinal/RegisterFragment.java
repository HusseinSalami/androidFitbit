package com.example.generals.fitbitmoniteringapplicationfinal;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class RegisterFragment extends Fragment {
    View rootView;
    Context context;
    ImageView profile;
    SharedPreferences sharedPreferences;
    public static final String imagePath="profilePath";
    public static final String MyPreferences="MyPrefs";
    Bitmap bitmap;
    Bundle bundle=new Bundle();
    String json;

    private ProgressDialog pDialog=null;
    private static String url_validate = "http://192.168.43.103/validate.php";
    private static String url_getUser = "http://192.168.43.103/getCredentials.php";
    private static String url_setUser = "http://192.168.43.103/editUser.php";
    UserModel userModel=null;
    JSONObject object_i=null;
    boolean username_validate=false;
    String usernameValue;
    String passwordValue;
    String ageValue;

    String sexText;
    RadioButton male;
    RadioButton female;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView=inflater.inflate(R.layout.register_fragment_layout, container, false);
        context=rootView.getContext();
        usernameValue="";
        profile=(ImageView)rootView.findViewById(R.id.profile_image);
        url_validate = "http://192.168.43.103/validate.php";
        url_getUser = "http://192.168.43.103/getCredentials.php";
        url_setUser = "http://192.168.43.103/editUser.php";
        male=(RadioButton) rootView.findViewById(R.id.radioMale);
        female=(RadioButton) rootView.findViewById(R.id.radioFemale);

        male.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                RadioButton a=(RadioButton)view;
                usernameValue=a.getText().toString();
                return true;
            }
        });
        female.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                RadioButton a=(RadioButton)view;
                usernameValue=a.getText().toString();
                return true;
            }
        });
        sharedPreferences=getActivity().getSharedPreferences(MyPreferences, 0);

        if(sharedPreferences.contains(imagePath))
        {
            String picturePath= sharedPreferences.getString(imagePath,"rien");

            bitmap=(BitmapFactory.decodeFile(picturePath));
        }
        else
        {
             bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            // profile.setImageBitmap(bitmap);
        }

        bitmap = getRoundedShape(bitmap);

        profile.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 500, 500, false));

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);

           fab.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view)
               {
                   //check if all fields had been filled;
                   EditText username=(EditText) rootView.findViewById(R.id.usernameId);
                   EditText password=(EditText) rootView.findViewById(R.id.passwordId);
                   EditText age=(EditText) rootView.findViewById(R.id.ageId);

                        if(username.getText().equals("") || password.getText().equals("") || age.getText().equals(""))
                       {
                           Snackbar.make(view, "the fields cannot be blank", Snackbar.LENGTH_LONG)
                                   .setAction("Action", null).show();
                       }

                       else
                       {
                           //je vais remplir la base de donner avec les nouveaux data donc je vais devoir faire un insert et reavoir un resultat
                           // si cest valide donc je vais remplir;

                           usernameValue=username.getText().toString();
                            passwordValue=password.getText().toString();
                           ageValue=age.getText().toString();
                           try {
                               Void str_result=new UsernameValidate(context).execute().get();
                           } catch (InterruptedException e) {
                               e.printStackTrace();
                           } catch (ExecutionException e) {
                               e.printStackTrace();
                           }
                           if(username_validate==true) {

                               userModel = new UserModel();
                               userModel.setUsername(username.getText().toString());
                               userModel.setAge(Integer.parseInt(age.getText().toString()));
                               userModel.setPassword(password.getText().toString());

                                if(male.isChecked())
                                {
                                    sexText="male";
                                    userModel.setSexe(sexText);
                                }
                                else
                                {
                                    sexText="female";
                                    userModel.setSexe("female");
                                }
                               try {
                                   Void str_result=new EditUser(context).execute().get();
                               } catch (InterruptedException e) {
                                   e.printStackTrace();
                               } catch (ExecutionException e) {
                                   e.printStackTrace();
                               }

                               sharedPreferences.edit().putString("username", userModel.getUsername().toString()).commit();

                               MainFragment mainFragment=new MainFragment();
                               FragmentManager main_manager=getActivity().getSupportFragmentManager();

                               main_manager.beginTransaction().replace(R.id.container, mainFragment).commit();

                           }
                           else
                           {
                               Snackbar.make(view, "unvalid username", Snackbar.LENGTH_LONG)
                                       .setAction("Action", null).show();
                           }

                       }
               }
           });
        bundle= this.getArguments();
        //Boolean first=bundle.getBoolean("premiere");
        //if its the first time the fields will be empty else i will get the data to fill them;;
        if(bundle!=null)
        {
            //first sera egal a true;
            //ils vont rester nuls;
        }
        else
        {
            //pas la premiere fois. donc je vais chercher les data w 3abiyon;

            EditText username=(EditText) rootView.findViewById(R.id.usernameId);
            EditText password=(EditText) rootView.findViewById(R.id.passwordId);
            EditText age=(EditText) rootView.findViewById(R.id.ageId);

            try {
                Void strg= new GetUser(context).execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            username.setText(userModel.getUsername());
            password.setText(userModel.getPassword());
            age.setText(""+userModel.getAge());

            if(userModel.getSexe().equals("male"))
            {
                RadioButton radio =(RadioButton)rootView.findViewById(R.id.radioMale);
                radio.setChecked(true);
            }
            else
            {
                RadioButton radio =(RadioButton)rootView.findViewById(R.id.radioFemale);
                radio.setChecked(true);
            }
        }
        return rootView;
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

    private void selectImage() {

        final CharSequence[] options = {"Choose from Gallery","Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Choose from Gallery"))
                {
                    Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);
                }
                else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 2) {

                Uri selectedImage = data.getData();
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor c = getActivity().getContentResolver().query(selectedImage, filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
          //      Log.w("path of image from gallery......******************.........", picturePath + "");
                thumbnail=getRoundedShape(thumbnail);
                profile.setImageBitmap(Bitmap.createScaledBitmap(thumbnail, 500, 500, false));


                sharedPreferences.edit().putString(imagePath, picturePath).commit();
            }
        }
    }

    private class EditUser extends AsyncTask<Void, Void, Void> {

        Context context;

        public  EditUser(Context context)
        {
            this.context=context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(rootView.getContext());
            pDialog.setMessage("Please wait...4");
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
            String jsonStr;
            String oldUsername= sharedPreferences.getString("username", "rien");

            if(oldUsername.equals("rien"))
            {
               url_setUser=url_setUser+"?"+"username="+userModel.getUsername()+"&password="+userModel.getPassword()+"&oldusername=&age="+userModel.getAge()+"&sexe="+userModel.getSexe();
                jsonStr=sh.makeServiceCall(url_setUser,ServiceHandler.GET);
            }
            else
            {
                url_setUser=url_setUser+"?"+"username="+userModel.getUsername()+"&password="+userModel.getPassword()+"&oldusername="+oldUsername+"&age="+userModel.getAge()+"&sexe="+userModel.getSexe();

                jsonStr=sh.makeServiceCall(url_setUser,ServiceHandler.GET);
            }


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

    private class GetUser extends AsyncTask<Void, Void, Void> {

        Context context;

        public  GetUser(Context context)
        {
            this.context=context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(rootView.getContext());
            pDialog.setMessage("Please wait...3");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response
            // fix the url to retrieve the history for this user having id id1;
            String username=sharedPreferences.getString("username","rien");
            url_getUser=url_getUser+"?"+"username="+username;
            String jsonStr = sh.makeServiceCall(url_getUser, ServiceHandler.GET);

            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray array_json =jsonObj.getJSONArray("data");
                    JSONObject object_i=array_json.getJSONObject(0);


                    userModel=new UserModel();


                    userModel.setAge(object_i.getInt("age"));
                    userModel.setUsername(object_i.getString("username"));
                    userModel.setSexe(object_i.getString("sexe"));
                    userModel.setPassword(object_i.getString("password"));


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



    private class UsernameValidate extends AsyncTask<Void, Void, Void> {

        Context context;

        public  UsernameValidate(Context context)
        {
            this.context=context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
          //  pDialog = new ProgressDialog(rootView.getContext());
          //  pDialog.setMessage("Please wait...2");
          //  pDialog.setCancelable(false);
           // pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            if(android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();

            ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response
            // fix the url to retrieve the history for this user having id id1;

            String urlValidate=url_validate+"?username="+usernameValue+"&password="+passwordValue+"&age="+ageValue;

 //           url_validate=url_validate+"?username="+usernameValue+"&password="+passwordValue+"&age="+ageValue;

            String jsonStr = sh.makeServiceCall(urlValidate, ServiceHandler.GET);

            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try {

                    JSONObject jsonObj = new JSONObject(jsonStr);

                    boolean value= jsonObj.getBoolean("result");
                    username_validate=value;
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

         //   if (pDialog.isShowing())
          //      pDialog.dismiss();

        }
    }

}
