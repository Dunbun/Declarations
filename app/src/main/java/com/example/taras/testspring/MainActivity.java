package com.example.taras.testspring;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;



public class MainActivity extends AppCompatActivity {
    int personSalaryLastYear = 0;
    int incomeTotalPriceLastYear = 0;

    String name = "Володимир";
    String fathername = "Борисович";
    String surname = "Гройсман";
    int totalPrice = 0;
    SQLite sqLite;
    SQLiteLastYear sqLiteLastYear;
    ContentValues contentValues;
    customadapter ca;
    String sDeclarationLastYear = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ListView listView = (ListView)findViewById(R.id.listView);
        ca = new customadapter(null);
        listView.setAdapter(ca);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TextView PIB = (TextView)view.findViewById(R.id.PIB);
                String tmp = PIB.getText().toString();
                String[] sList = tmp.split(" ");
                long page = 0;

                sqLite = new SQLite(MainActivity.this);
                SQLiteDatabase database = sqLite.getWritableDatabase();
                String selection = SQLite.KEY_NAME + " = ? AND " + SQLite.KEY_SURNAME + " = ? AND " + SQLite.KEY_FATHERNAME + " = ?";
                String [] selectionArgs = {sList[1],sList[0],sList[2]};

                Cursor cursor = database.query(SQLite.TABLE_NAME, null, selection, selectionArgs, null, null, null);

                if(cursor.moveToFirst()) {
                    //Toast.makeText(MainActivity.this,cursor.getLong(cursor.getColumnIndex(SQLite.KEY_ID)) + "",Toast.LENGTH_SHORT).show();
                    page = cursor.getLong(cursor.getColumnIndex(SQLite.KEY_ID));
                }
                cursor.close();
                database.close();


                Intent intent = new Intent(getBaseContext(), MoreInformation.class);
                intent.putExtra("Position", page);
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                TextView PIB = (TextView)view.findViewById(R.id.PIB);
                String sPIB = PIB.getText().toString();
                String[] sList = sPIB.split(" ");

                SQLiteLastYear sqLite = new SQLiteLastYear(MainActivity.this);
                SQLiteDatabase database = sqLite.getReadableDatabase();

                String selection = SQLiteLastYear.KEY_NAME + " = ? AND " + SQLiteLastYear.KEY_SURNAME + " = ? AND " + SQLiteLastYear.KEY_FATHERNAME + " = ?";
                String [] selectionArgs = {sList[1],sList[0],sList[2]};

                Cursor cursor = database.query(SQLiteLastYear.TABLE_NAME, null, selection, selectionArgs, null, null, null);

                if(cursor.moveToFirst()) {
                    //Toast.makeText(MainActivity.this, cursor.getString(cursor.getColumnIndex(SQLiteLastYear.KEY_NAME)), Toast.LENGTH_SHORT).show();
                    TextView textview = (TextView)view.findViewById(R.id.totalPricelastyear);
                    textview.setText(cursor.getString(cursor.getColumnIndex(SQLiteLastYear.KEY_PRICEOFALLPROPERTY)));

                    textview = (TextView)view.findViewById(R.id.totalIncomelastyear);
                    textview.setText(cursor.getString(cursor.getColumnIndex(SQLiteLastYear.KEY_INCOMETOTAL)));

                    textview = (TextView)view.findViewById(R.id.salarylastyear);
                    textview.setText(cursor.getString(cursor.getColumnIndex(SQLiteLastYear.KEY_SALARY)));

                    textview = (TextView)view.findViewById(R.id.incomeCoflastyear);
                    textview.setText(cursor.getString(cursor.getColumnIndex(SQLiteLastYear.KEY_INCOMECOF)));

                    textview = (TextView)view.findViewById(R.id.salaryCoflastyear);
                    textview.setText(cursor.getString(cursor.getColumnIndex(SQLiteLastYear.KEY_SALARYCOF)));
                }
                return true;
            }
        });

        contentValues = new ContentValues();
        //sqLite = new SQLite(this);

        Button searchBtn = (Button)findViewById(R.id.searchbutton);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tmp = "";
                TextView PIBtextView = (TextView)findViewById(R.id.PIBtextView);
                tmp = PIBtextView.getText().toString();
                String[] sList = tmp.split(" ");
                if(sList.length == 3){
                    surname = sList[0];
                    name = sList[1];
                    fathername = sList[2];
                    DeclarationIDTusk newtusk = new DeclarationIDTusk();
                    newtusk.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,"https://public-api.nazk.gov.ua/v1/declaration/?q=" + tmp);
                }else{
                    Toast.makeText(MainActivity.this,"Введіть ПІБ",Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button sortBtn = (Button)findViewById(R.id.button2);

        sortBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ca = new customadapter(SQLite.KEY_SALARY + " DESC");
                listView.setAdapter(ca);
            }
        });
    }

    class DeclarationIDTusk extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... urls) {
            totalPrice = 0;
            String sDeclaration = "";
            //String sDeclarationLastYear = "";
            BufferedReader br = null;
            String line = "";
            StringBuilder sb = new StringBuilder();

            try {
                URL url = new URL(urls[0]);
                br = new BufferedReader(new InputStreamReader(url.openStream()));

                while((line = br.readLine()) != null){
                    sb.append(line);
                    sb.append(System.lineSeparator());
                }

                sDeclaration = String.valueOf(sb);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                JSONObject jsonObject = new JSONObject(sDeclaration);
                JSONArray jsonArray = jsonObject.getJSONArray("items");

                sDeclaration = "";

                int counter = 0;
                for (int i = 0; i < jsonArray.length(); i++) {
                  /*  if (jsonArray.getJSONObject(i).length() == 6) {
                        sDeclaration = jsonArray.getJSONObject(i).getString("id");
                        break;
                    }*/
                    if (jsonArray.getJSONObject(i).length() == 6 && counter == 1) {
                        sDeclarationLastYear = jsonArray.getJSONObject(i).getString("id");
                        break;
                    }
                    if (jsonArray.getJSONObject(i).length() == 6) {
                        sDeclaration = jsonArray.getJSONObject(i).getString("id");
                        counter++;
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            br = null;
            line = "";
            sb = new StringBuilder();

            if(!sDeclaration.equals("")) {
                contentValues = new ContentValues();
                try {
                    URL url = new URL("https://public-api.nazk.gov.ua/v1/declaration/" + sDeclaration);
                    br = new BufferedReader(new InputStreamReader(url.openStream()));

                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                        sb.append(System.lineSeparator());
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                getRealty(String.valueOf(sb),false);
                getMovablesWithoutCars(String.valueOf(sb),false);
                getMovablesCars(String.valueOf(sb),false);
                getPaper(String.valueOf(sb),false);
                getIncome(String.valueOf(sb),false);
                putToDB(false);
            }


            if(!sDeclarationLastYear.equals("")){
                br = null;
                line = "";
                sb = new StringBuilder();
                totalPrice = 0;
                contentValues = new ContentValues();
                try {
                    URL url = new URL("https://public-api.nazk.gov.ua/v1/declaration/" + sDeclarationLastYear);
                    br = new BufferedReader(new InputStreamReader(url.openStream()));

                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                        sb.append(System.lineSeparator());
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                getRealty(String.valueOf(sb),true);
                getMovablesWithoutCars(String.valueOf(sb),true);
                getMovablesCars(String.valueOf(sb),true);
                getPaper(String.valueOf(sb),true);
                getIncome(String.valueOf(sb),true);
                putToDB(true);
            }else{
                Toast.makeText(MainActivity.this,"Нема відомостей за попередній рік",Toast.LENGTH_SHORT).show();
            }

            return String.valueOf(sb);
        }

        @Override
        protected void onPostExecute(String id) {
            super.onPostExecute(id);
            ca.refresh();

            SQLiteDatabase database = sqLite.getWritableDatabase();

            Cursor cursor = database.query(SQLite.TABLE_NAME, null, null, null, null, null, null);

            Toast.makeText(MainActivity.this,cursor.getCount() + "" , Toast.LENGTH_SHORT).show();
        }
    }

    public String getRealty(String data,boolean lastYear){
        String result = "";
        Iterator<String> keys;
        String keyValue = "";
        int maxprice = 0;

        String realtyMaxPrice = "";
        int realtyTotalPrice = 0;
        String sRealty = "";

        try {
            JSONObject jsonObject = new JSONObject(data);
            result =  jsonObject.getString("data");
            jsonObject = new JSONObject(result);
            result = jsonObject.getString("step_3");
            jsonObject = new JSONObject(result);
            keys = jsonObject.keys();

            while(keys.hasNext()) {
                keyValue = (String) keys.next();
                String tmpString = jsonObject.getString(keyValue);
                JSONObject tmpJson = new JSONObject(tmpString);

                if(!tmpJson.getString("costDate").equals("")) {
                    if(maxprice < Integer.parseInt(tmpJson.getString("costDate"))){
                        maxprice = Integer.parseInt(tmpJson.getString("costDate"));
                        realtyMaxPrice = tmpJson.getString("objectType") + " : " + maxprice;
                    }

                    realtyTotalPrice += Integer.parseInt(tmpJson.getString("costDate"));

                    sRealty += tmpJson.getString("objectType") + " : " + tmpJson.getString("costDate") + "\n";
                }
            }

            Log.i("Declarationloginfo", "sRealty: " + sRealty );
            Log.i("Declarationloginfo", "TotalPrice: " + realtyTotalPrice );
            Log.i("Declarationloginfo", "MaxPrice: " + realtyMaxPrice );

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(sRealty.equals("")){
            sRealty = "Не вказано";
            realtyMaxPrice = "Не вказано";
            realtyTotalPrice = 0;
        }

        if(!lastYear) {
            contentValues.put(SQLite.KEY_SREALTY, sRealty);
            contentValues.put(SQLite.KEY_REALTYTOTAL, realtyTotalPrice);
            contentValues.put(SQLite.KEY_REALTYMAX, realtyMaxPrice);
        }
        if(lastYear) {
            contentValues.put(SQLiteLastYear.KEY_REALTYTOTAL, realtyTotalPrice);
        }
        totalPrice += realtyTotalPrice;

        return result;
    }

    public String getMovablesWithoutCars(String data,boolean lastYear){
        String result = "";
        Iterator<String> keys;
        String keyValue = "";
        int maxprice = 0;

        String movablesWithoutCarsMaxPrice = "";
        int movablesWithoutCarsTotalPrice = 0;
        String sMovablesWithoutCars = "";

        try {
            JSONObject jsonObject = new JSONObject(data);
            result =  jsonObject.getString("data");
            jsonObject = new JSONObject(result);
            result = jsonObject.getString("step_5");
            jsonObject = new JSONObject(result);
            keys = jsonObject.keys();

            while(keys.hasNext()) {
                keyValue = (String) keys.next();
                String tmpString = jsonObject.getString(keyValue);
                JSONObject tmpJson = new JSONObject(tmpString);

                if(!tmpJson.getString("costDateUse").equals("")) {
                    if(maxprice < Integer.parseInt(tmpJson.getString("costDateUse"))){
                        maxprice = Integer.parseInt(tmpJson.getString("costDateUse"));
                        movablesWithoutCarsMaxPrice = tmpJson.getString("propertyDescr") + " : " + maxprice;
                    }

                    movablesWithoutCarsTotalPrice += Integer.parseInt(tmpJson.getString("costDateUse"));

                    sMovablesWithoutCars += tmpJson.getString("propertyDescr") + " : " + tmpJson.getString("costDateUse") + "\n";
                }
            }

            Log.i("Declarationloginfo", "sMovablesWithoutCars: " + sMovablesWithoutCars );
            Log.i("Declarationloginfo", "TotalPrice: " + movablesWithoutCarsTotalPrice );
            Log.i("Declarationloginfo", "MaxPrice: " + movablesWithoutCarsMaxPrice );

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(sMovablesWithoutCars.equals("")){
            sMovablesWithoutCars = "Не вказано";
            movablesWithoutCarsMaxPrice = "Не вказано";
            movablesWithoutCarsTotalPrice = 0;
        }
        if(!lastYear) {
            contentValues.put(SQLite.KEY_SMOVABLESWITHOUTCARS, sMovablesWithoutCars);
            contentValues.put(SQLite.KEY_MOVABLESWITHOUTCARSTOTAL, movablesWithoutCarsTotalPrice);
            contentValues.put(SQLite.KEY_MOVABLESWITHOUTCARSMAX, movablesWithoutCarsMaxPrice);
        }
        if(lastYear) {
            contentValues.put(SQLiteLastYear.KEY_MOVABLESWITHOUTCARSTOTAL, movablesWithoutCarsTotalPrice);
        }
        totalPrice += movablesWithoutCarsTotalPrice;

        return result;
    }

    public String getMovablesCars(String data,boolean lastYear){
        String result = "";
        Iterator<String> keys;
        String keyValue = "";
        int maxprice = 0;

        String movablesCarsMaxPrice = "";
        int movablesCarsTotalPrice = 0;
        String sMovablesCars = "";

        try {
            JSONObject jsonObject = new JSONObject(data);
            result =  jsonObject.getString("data");
            jsonObject = new JSONObject(result);
            result = jsonObject.getString("step_6");
            jsonObject = new JSONObject(result);
            keys = jsonObject.keys();

            while(keys.hasNext()) {
                keyValue = (String) keys.next();
                String tmpString = jsonObject.getString(keyValue);
                JSONObject tmpJson = new JSONObject(tmpString);

                if(!tmpJson.getString("costDate").equals("")) {
                    if(maxprice < Integer.parseInt(tmpJson.getString("costDate"))){
                        maxprice = Integer.parseInt(tmpJson.getString("costDate"));
                        movablesCarsMaxPrice = tmpJson.getString("brand") + " " + tmpJson.getString("model") + " : " + maxprice;
                    }

                    movablesCarsTotalPrice += Integer.parseInt(tmpJson.getString("costDate"));

                    sMovablesCars += tmpJson.getString("brand") + " " + tmpJson.getString("model") + " : " + tmpJson.getString("costDate") + "\n";
                }
            }

            Log.i("Declarationloginfo", "sMovablesCars: " + sMovablesCars );
            Log.i("Declarationloginfo", "TotalPrice: " + movablesCarsTotalPrice );
            Log.i("Declarationloginfo", "MaxPrice: " + movablesCarsMaxPrice );

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(sMovablesCars.equals("")){
            sMovablesCars = "Не вказано";
            movablesCarsMaxPrice = "Не вказано";
            movablesCarsTotalPrice = 0;
        }

        if(!lastYear) {
            contentValues.put(SQLite.KEY_SMOVABLESCARS, sMovablesCars);
            contentValues.put(SQLite.KEY_MOVABLESCARSTOTAL, movablesCarsTotalPrice);
            contentValues.put(SQLite.KEY_MOVABLESCARSMAX, movablesCarsMaxPrice);
        }
        if(lastYear) {
            contentValues.put(SQLiteLastYear.KEY_MOVABLESCARSTOTAL, movablesCarsTotalPrice);
        }
        totalPrice += movablesCarsTotalPrice;

        return result;
    }

    public String getPaper(String data,boolean lastYear){
        String result = "";
        Iterator<String> keys;
        String keyValue = "";
        int maxprice = 0;

        String paperMaxPrice = "";
        int paperTotalPrice = 0;
        String sPaper = "";

        try {
            JSONObject jsonObject = new JSONObject(data);
            result =  jsonObject.getString("data");
            jsonObject = new JSONObject(result);
            result = jsonObject.getString("step_7");
            jsonObject = new JSONObject(result);
            keys = jsonObject.keys();
            String tmp = "";

            while(keys.hasNext()) {
                keyValue = (String) keys.next();
                String tmpString = jsonObject.getString(keyValue);
                JSONObject tmpJson = new JSONObject(tmpString);

                if(!tmpJson.getString("cost").equals("")) {
                    tmp = tmpJson.getString("cost");
                    tmp = tmp.replace(",",".");

                    if(maxprice < (int)Float.parseFloat(tmp)){
                        maxprice = (int)Float.parseFloat(tmp);
                        paperMaxPrice = tmpJson.getString("typeProperty") + " : " + maxprice;
                    }

                    paperTotalPrice += (int)Float.parseFloat(tmp);

                    sPaper += tmpJson.getString("typeProperty") + " : " + tmpJson.getString("cost") + "\n";
                }
            }

            Log.i("Declarationloginfo", "sPaper: " + sPaper );
            Log.i("Declarationloginfo", "TotalPrice: " + paperTotalPrice );
            Log.i("Declarationloginfo", "MaxPrice: " + paperMaxPrice );

        } catch (JSONException e) {
            e.printStackTrace();

        }

        if(sPaper.equals("")){
            sPaper = "Не вказано";
            paperMaxPrice = "Не вказано";
            paperTotalPrice = 0;
        }

        if(!lastYear) {
            contentValues.put(SQLite.KEY_SPAPER, sPaper);
            contentValues.put(SQLite.KEY_PAPERTOTAL, paperTotalPrice);
            contentValues.put(SQLite.KEY_PAPERMAX, paperMaxPrice);
        }
        if(lastYear) {
            contentValues.put(SQLiteLastYear.KEY_PAPERTOTAL, paperTotalPrice);
        }

        totalPrice += paperTotalPrice;

        return result;
    }

    public String getIncome(String data,boolean lastYear){
        String result = "";
        Iterator<String> keys;
        String keyValue = "";
        int maxprice = 0;

        int incomeTotalPrice = 0;
        String incomeMaxPrice = "";
        String sIncome = "";
        int personSalary = 0;
        double incomeCof = 0;
        double salaryCof = 0;

        try {
            JSONObject jsonObject = new JSONObject(data);
            result =  jsonObject.getString("data");
            jsonObject = new JSONObject(result);
            result = jsonObject.getString("step_11");
            jsonObject = new JSONObject(result);
            keys = jsonObject.keys();

            sIncome = "";
            personSalary = 0;
            incomeTotalPrice = 0;

            while(keys.hasNext()) {
                keyValue = (String) keys.next();
                String tmpString = jsonObject.getString(keyValue);
                JSONObject tmpJson = new JSONObject(tmpString);

                if(tmpJson.getString("objectType").equals("Заробітна плата отримана за основним місцем роботи")){
                    personSalary += Integer.parseInt(tmpJson.getString("sizeIncome"));
                }
                    if(maxprice < Integer.parseInt(tmpJson.getString("sizeIncome"))){
                        maxprice = Integer.parseInt(tmpJson.getString("sizeIncome"));
                        incomeMaxPrice = tmpJson.getString("objectType") + " : " + maxprice;
                    }

                    incomeTotalPrice += Integer.parseInt(tmpJson.getString("sizeIncome"));

                    sIncome += tmpJson.getString("objectType") + " : " + tmpJson.getString("sizeIncome") + "\n";

            }



            incomeCof = ((double) totalPrice)/((double)incomeTotalPrice);
            salaryCof = ((double) totalPrice)/((double)personSalary);

            Log.i("Declarationloginfo", "sIncome: " + sIncome );
            Log.i("Declarationloginfo", "TotalPrice: " + incomeTotalPrice );
            Log.i("Declarationloginfo", "MaxPrice: " + incomeMaxPrice );
            Log.i("Declarationloginfo", "PersonSalary: " + personSalary );

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(sIncome.equals("")){
            sIncome = "Не вказано";
            incomeMaxPrice = "Не вказано";
            incomeTotalPrice = 0;
            salaryCof = 0;
            personSalary = 0;
            incomeCof = 0;
        }

        if(!lastYear) {
            contentValues.put(SQLite.KEY_SINCOME, sIncome);
            contentValues.put(SQLite.KEY_INCOMEMAX, incomeMaxPrice);
            contentValues.put(SQLite.KEY_SALARY, personSalary);
            contentValues.put(SQLite.KEY_INCOMETOTAL, incomeTotalPrice);
            contentValues.put(SQLite.KEY_NAME, name);
            contentValues.put(SQLite.KEY_FATHERNAME, fathername);
            contentValues.put(SQLite.KEY_SURNAME, surname);
            contentValues.put(SQLite.KEY_INCOMECOF, incomeCof);
            contentValues.put(SQLite.KEY_SALARYCOF, salaryCof);
            contentValues.put(SQLite.KEY_PRICEOFALLPROPERTY, totalPrice);
        }
        if(lastYear) {
            contentValues.put(SQLiteLastYear.KEY_SALARY, personSalary);
            contentValues.put(SQLiteLastYear.KEY_INCOMETOTAL, incomeTotalPrice);
            contentValues.put(SQLiteLastYear.KEY_NAME, name);
            contentValues.put(SQLiteLastYear.KEY_FATHERNAME, fathername);
            contentValues.put(SQLiteLastYear.KEY_SURNAME, surname);
            contentValues.put(SQLiteLastYear.KEY_INCOMECOF, incomeCof);
            contentValues.put(SQLiteLastYear.KEY_SALARYCOF, salaryCof);
            contentValues.put(SQLiteLastYear.KEY_PRICEOFALLPROPERTY, totalPrice);
        }

        return result;
    }

    public void putToDB(boolean lastYear){
        if(!lastYear) {
            sqLite = new SQLite(this);
            SQLiteDatabase database = sqLite.getWritableDatabase();
            String selection = SQLite.KEY_NAME + " = ? AND " + SQLite.KEY_SURNAME + " = ? AND " + SQLite.KEY_FATHERNAME + " = ?";
            String[] selectionArgs = {name, surname, fathername};

            Cursor cursor = database.query(SQLite.TABLE_NAME, null, selection, selectionArgs, null, null, null);

            if (!cursor.moveToFirst()) {
                database.insert(SQLite.TABLE_NAME, null, contentValues);
            }
            cursor.close();
            database.close();
        }

        if(lastYear) {
            sqLiteLastYear = new SQLiteLastYear(this);
            SQLiteDatabase database = sqLiteLastYear.getWritableDatabase();
            String selection = SQLiteLastYear.KEY_NAME + " = ? AND " + SQLiteLastYear.KEY_SURNAME + " = ? AND " + SQLiteLastYear.KEY_FATHERNAME + " = ?";
            String[] selectionArgs = {name, surname, fathername};

            Cursor cursor = database.query(SQLiteLastYear.TABLE_NAME, null, selection, selectionArgs, null, null, null);

            if (!cursor.moveToFirst()) {
                database.insert(SQLiteLastYear.TABLE_NAME, null, contentValues);
            }
            cursor.close();
            database.close();
            Log.i("Declarationloginfo",contentValues.toString());
        }
        //Log.i("Declarationloginfo",contentValues.toString());
    }

    class customadapter extends BaseAdapter {
        String param = "";
        customadapter(String param){
            this.param = param;
        }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            sqLiteLastYear = new SQLiteLastYear(MainActivity.this);
            sqLite = new SQLite(MainActivity.this);
            SQLiteDatabase db = sqLite.getReadableDatabase();
            Cursor cursor = db.query(SQLite.TABLE_NAME, null, null, null, null, null, null);
            int count = cursor.getCount();
            cursor.close();
            db.close();
            return count;
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub

            return null;
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return 0;
        }

        public void refresh(){
            getCount();
            notifyDataSetChanged();
        }
        @Override
        public View getView(final int position, View convertview, ViewGroup arg2) {
            // TODO Auto-generated method stub
            LayoutInflater inflater = getLayoutInflater();
            convertview = inflater.inflate(R.layout.list_item, null);
            final View myView = convertview;
            TextView pib = (TextView) convertview.findViewById(R.id.PIB);
            TextView totalPrice = (TextView) convertview.findViewById(R.id.totalPrice);
            TextView totalIncome = (TextView) convertview.findViewById(R.id.totalIncome);
            TextView salary = (TextView) convertview.findViewById(R.id.salary);
            TextView incomeCof = (TextView) convertview.findViewById(R.id.incomeCof);
            TextView salaryCof = (TextView) convertview.findViewById(R.id.salaryCof);

            ImageView deleteView = (ImageView)convertview.findViewById(R.id.deleteView);
            deleteView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView PIB = (TextView)myView.findViewById(R.id.PIB);
                    String sPIB = PIB.getText().toString();
                    String[] sList = sPIB.split(" ");

                    SQLite sqLite = new SQLite(MainActivity.this);
                    SQLiteDatabase database = sqLite.getReadableDatabase();

                    String selection = SQLite.KEY_NAME + " = ? AND " + SQLite.KEY_SURNAME + " = ? AND " + SQLite.KEY_FATHERNAME + " = ?";
                    String [] selectionArgs = {sList[1],sList[0],sList[2]};

                    database.delete(SQLite.TABLE_NAME, selection, selectionArgs);
                    notifyDataSetChanged();
                    Toast.makeText(MainActivity.this, "Видалено", Toast.LENGTH_SHORT).show();

                }
            });


            SQLiteDatabase db = sqLite.getReadableDatabase();
            Cursor cursor = db.query(SQLite.TABLE_NAME, null, null, null, null, null, param);

            if(cursor.moveToFirst()) {
                cursor.moveToPosition(position);
                String sPIB = cursor.getString(cursor.getColumnIndex(SQLite.KEY_SURNAME)) + " " +
                        cursor.getString(cursor.getColumnIndex(SQLite.KEY_NAME)) + " " +
                        cursor.getString(cursor.getColumnIndex(SQLite.KEY_FATHERNAME));
                pib.setText(sPIB);
                totalPrice.setText(cursor.getString(cursor.getColumnIndex(SQLite.KEY_PRICEOFALLPROPERTY)));
                totalIncome.setText(cursor.getString(cursor.getColumnIndex(SQLite.KEY_INCOMETOTAL)));
                salary.setText(cursor.getString(cursor.getColumnIndex(SQLite.KEY_SALARY)));
                incomeCof.setText(cursor.getString(cursor.getColumnIndex(SQLite.KEY_INCOMECOF)));
                salaryCof.setText(cursor.getString(cursor.getColumnIndex(SQLite.KEY_SALARYCOF)));

            }




            return convertview;
        }

    }


}


