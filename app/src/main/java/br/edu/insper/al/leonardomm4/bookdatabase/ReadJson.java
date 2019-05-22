package br.edu.insper.al.leonardomm4.bookdatabase;

import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.Scanner;

public class ReadJson extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_json);
    }

    //ESSA PARTE É IGUAL PARA AMBAS == POSSIVEL SOLUÇÃO PARA O QUE O HASHI PEDIU SEGUNDA (COESAO ou ENCAPSULAMENTO)
    public void loadJson (View view) {

        Resources res = getResources();

        InputStream is = res.openRawResource(R.raw.data);

        Scanner scanner = new Scanner(is);

        StringBuilder builder = new StringBuilder();

        while (scanner.hasNextLine()){
            builder.append(scanner.nextLine());
        }

        parseJson(builder.toString());
    }

    //Essa parte varia para HOMEPAGE e BOOKPAGE
    public void parseJson(String s){
        TextView textView = findViewById(R.id.EXEMPLO); // Aqui seria o local para adequar ao XML (EU ACHO)
        StringBuilder builder = new StringBuilder();

        try {
            JSONObject root = new JSONObject(s);
            JSONObject data =  root.getJSONObject("database");

            JSONArray books = data.getJSONArray("books");

            for(int i=0; i<books.length(); i++){
                JSONObject book = books.getJSONObject(i);
                builder.append(book.getString("name"));
                builder.append("\n");
                builder.append(book.getString("author"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
