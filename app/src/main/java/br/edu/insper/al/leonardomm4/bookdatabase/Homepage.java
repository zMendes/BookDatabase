package br.edu.insper.al.leonardomm4.bookdatabase;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;

public class Homepage extends AppCompatActivity {

    private Button book1;
    private Button book2;
    private Button book3;
    private Button book4;
    private Button book5;
    private ImageView about;
    LinkedList<TextView> titalts;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);


        book1 = findViewById(R.id.book1);
        book2 = findViewById(R.id.book2);
        book3 = findViewById(R.id.book3);
        book4 = findViewById(R.id.book4);
        book5 = findViewById(R.id.book5);

        about = findViewById(R.id.about);

        about.setOnClickListener(view -> {
            Intent intent = new Intent(this, About.class);
            startActivity(intent);
        });

        book1.setOnClickListener(view -> goToPage(0));

        book2.setOnClickListener(view -> goToPage(1));

        book3.setOnClickListener(view -> goToPage(2));

        book4.setOnClickListener(view -> goToPage(3));

        book5.setOnClickListener(view -> goToPage(4));



        titalts = new LinkedList<>();

        titalts.add(findViewById(R.id.titalt1));
        titalts.add(findViewById(R.id.titalt2));
        titalts.add(findViewById(R.id.titalt3));
        titalts.add(findViewById(R.id.titalt4));
        titalts.add(findViewById(R.id.titalt5));
        String json_f =loadData();
        if (json_f.isEmpty()){
        String json = loadJSON();
        saveData(json);
        json_f = loadData();}
        else {
            json_f = loadData() ;
        }


        LinkedList<StringBuilder> builders = new LinkedList<>();

        try {
            JSONObject root = new JSONObject(json_f);
            JSONObject data =  root.getJSONObject("database");

            JSONArray books = data.getJSONArray("books");


            for(int i=0; i<books.length(); i++){
                builders.add(new StringBuilder());
                JSONObject book = books.getJSONObject(i);
                builders.get(i).append(book.getString("name"));
                builders.get(i).append("\n");
                builders.get(i).append(book.getString("author"));
                titalts.get(i).setText(builders.get(i).toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void goToPage(int id) {
        Intent intent = new Intent(this, BookPage.class);
        intent.putExtra("idbook", id);
        startActivity(intent);
    }


    public String loadJSON() {
        String json = null;
        try {
            InputStream is = getResources().openRawResource(R.raw.data);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    private void saveData(String s){
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("data",s);
        editor.apply();
    }

    private String loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences",0);
        String json = sharedPreferences.getString("data", null);
        return json;
    }
}