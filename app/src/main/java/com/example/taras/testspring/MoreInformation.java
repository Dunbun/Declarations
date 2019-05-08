package com.example.taras.testspring;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class MoreInformation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_information);

        long position = getIntent().getExtras().getLong("Position");

        SQLite sqLite = new SQLite(this);
        SQLiteDatabase database = sqLite.getReadableDatabase();

        String selection = SQLite.KEY_ID + " = ?";
        String [] selectionArgs = {position + ""};

        Cursor cursor = database.query(SQLite.TABLE_NAME, null, selection, selectionArgs, null, null, null);

        cursor.moveToFirst();

        String sPIB = cursor.getString(cursor.getColumnIndex(SQLite.KEY_SURNAME)) + " " +
                cursor.getString(cursor.getColumnIndex(SQLite.KEY_NAME)) + " " +
                cursor.getString(cursor.getColumnIndex(SQLite.KEY_FATHERNAME));
        String priceOfAllProperty = cursor.getString(cursor.getColumnIndex(SQLite.KEY_PRICEOFALLPROPERTY));
        String totalIncome = cursor.getString(cursor.getColumnIndex(SQLite.KEY_INCOMETOTAL));
        String salary = cursor.getString(cursor.getColumnIndex(SQLite.KEY_SALARY));
        String incomeCof = cursor.getString(cursor.getColumnIndex(SQLite.KEY_INCOMECOF));
        String salaryCof = cursor.getString(cursor.getColumnIndex(SQLite.KEY_SALARYCOF));
        String incofeCof = cursor.getString(cursor.getColumnIndex(SQLite.KEY_INCOMECOF));

        String sRealty = cursor.getString(cursor.getColumnIndex(SQLite.KEY_SREALTY));
        String sMovablesWithoutCars = cursor.getString(cursor.getColumnIndex(SQLite.KEY_SMOVABLESWITHOUTCARS));
        String sMovablesCars = cursor.getString(cursor.getColumnIndex(SQLite.KEY_SMOVABLESCARS));
        String sPaper = cursor.getString(cursor.getColumnIndex(SQLite.KEY_SPAPER));
        String sIncome = cursor.getString(cursor.getColumnIndex(SQLite.KEY_SINCOME));
        String realtyMax = cursor.getString(cursor.getColumnIndex(SQLite.KEY_REALTYMAX));
        String movablesWithoutCarsMax = cursor.getString(cursor.getColumnIndex(SQLite.KEY_MOVABLESWITHOUTCARSMAX));
        String movablesCarsMax = cursor.getString(cursor.getColumnIndex(SQLite.KEY_MOVABLESCARSMAX));
        String incomeMax = cursor.getString(cursor.getColumnIndex(SQLite.KEY_INCOMEMAX));
        String paperMax = cursor.getString(cursor.getColumnIndex(SQLite.KEY_PAPERMAX));
        String realtyTotal = cursor.getString(cursor.getColumnIndex(SQLite.KEY_REALTYTOTAL));
        String movablesWithoutCarsTotal = cursor.getString(cursor.getColumnIndex(SQLite.KEY_MOVABLESWITHOUTCARSTOTAL));
        String movablesCarsTotal = cursor.getString(cursor.getColumnIndex(SQLite.KEY_MOVABLESCARSMAX));
        String paperTotal = cursor.getString(cursor.getColumnIndex(SQLite.KEY_PAPERTOTAL));






        String result = sRealty ;

        TextView textView =(TextView)findViewById(R.id.realty);
        textView.setText(result);

        textView =(TextView)findViewById(R.id.movablesWithoutCars);
        textView.setText(sMovablesWithoutCars);

        textView =(TextView)findViewById(R.id.movablesCars);
        textView.setText(sMovablesCars);

        textView =(TextView)findViewById(R.id.papers);
        textView.setText(sPaper);

        textView =(TextView)findViewById(R.id.income);
        textView.setText(sIncome);

        textView =(TextView)findViewById(R.id.totalPrice);
        textView.setText(priceOfAllProperty);

        textView =(TextView)findViewById(R.id.totalIncome);
        textView.setText(totalIncome);

        textView =(TextView)findViewById(R.id.salary);
        textView.setText(salary);

        textView =(TextView)findViewById(R.id.incomeCof);
        textView.setText(incofeCof);

        textView =(TextView)findViewById(R.id.salaryCof);
        textView.setText(salaryCof);

        textView =(TextView)findViewById(R.id.PIB);
        textView.setText(sPIB);

        //Toast.makeText(this,cursor.getString(cursor.getColumnIndex(SQLite.KEY_NAME)),Toast.LENGTH_SHORT).show();
    }
}
