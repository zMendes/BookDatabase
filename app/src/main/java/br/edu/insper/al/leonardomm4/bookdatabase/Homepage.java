package br.edu.insper.al.leonardomm4.bookdatabase;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;

public class Homepage extends AppCompatActivity {

    private Button book1;
    private Button book2;
    private Button book3;
    LinkedList<TextView> titalts;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);


        book1 = findViewById(R.id.book1);
        book2 = findViewById(R.id.book2);
        book3 = findViewById(R.id.book3);

        book1.setOnClickListener(view -> {
            goToPage(0);
        });

        book2.setOnClickListener(view -> {
            goToPage(1);
        });

        book3.setOnClickListener(view -> {
            goToPage(2);
        });



        titalts = new LinkedList<>();

        titalts.add(findViewById(R.id.titalt1));
        titalts.add(findViewById(R.id.titalt2));
        titalts.add(findViewById(R.id.titalt3));

        loadJson();

    }

    public void loadJson () {

        Resources res = getResources();

        InputStream is = res.openRawResource(R.raw.data);

        Scanner scanner = new Scanner(is);

        StringBuilder builder = new StringBuilder();

        while (scanner.hasNextLine()){
            builder.append(scanner.nextLine());
        }

        parseJson(builder.toString());
    }

    public void parseJson(String s) {
        LinkedList<StringBuilder> builders = new LinkedList<>();

        try {
            JSONObject root = new JSONObject(s);
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
        String strName = null;
        intent.putExtra("idbook", id);
        startActivity(intent);
    }

}
