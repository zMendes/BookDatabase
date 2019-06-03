package br.edu.insper.al.leonardomm4.bookdatabase;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Homepage extends AppCompatActivity {

    private GridView gridView;
    private ArrayList<Item> bookList;


    private ImageView sortName;
    private ImageView icon;
    private ImageView about;
    private ImageView add;
    private CheckBox checkBox;

    private TextView nautor;
    private TextView nlivros;

    private int picture = R.drawable.ic_launcher_background;

    private Boolean sorted;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);


        gridView = findViewById(R.id.gv);
        nautor = findViewById(R.id.nautores);
        nlivros = findViewById(R.id.nlivros);

        icon = findViewById(R.id.icon);
        icon.setOnClickListener(view -> {
            Intent intent = new Intent(this, Homepage.class);
            startActivity(intent);
        });


        sorted = true;

        sortName = findViewById(R.id.sortName);
        sortName.setOnClickListener(view -> {
            if (sorted){

            ArrayList<JSONObject> list = new ArrayList<>();

            String json = loadData();
            JSONObject root;
            try {
                root = new JSONObject(json);
                JSONObject data = root.getJSONObject("database");
                JSONArray books = data.getJSONArray("books");
                for (int i=0;i < books.length();i++){
                    list.add(books.getJSONObject(i));
                }
                Collections.sort(list, (jsonObject, t1) -> {
                    int compare = 0;
                    try {
                        compare =jsonObject.getString("name").compareTo(t1.getString("name"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return compare;
                });
                JSONArray Sorted = new JSONArray();
                for (int i = 0; i < list.size(); i++) {
                    Sorted.put(list.get(i));
                }

                data.put("books",Sorted);
                root.put("database", data);
                saveData(root.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            RefreshList();
            Toast toast = Toast.makeText(getApplicationContext(), "Sorting by: title", Toast.LENGTH_SHORT);
            toast.show();
            sorted = false;
            }
            else {
                ArrayList<JSONObject> list = new ArrayList<>();

                String json = loadData();
                JSONObject root;
                try {
                    root = new JSONObject(json);
                    JSONObject data = root.getJSONObject("database");
                    JSONArray books = data.getJSONArray("books");
                    for (int i=0;i < books.length();i++){
                        list.add(books.getJSONObject(i));
                    }
                    Collections.sort(list, (jsonObject, t1) -> {
                        int compare = 0;
                        try {
                            compare =jsonObject.getString("author").compareTo(t1.getString("author"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return compare;
                    });
                    JSONArray Sorted = new JSONArray();
                    for (int i = 0; i < list.size(); i++) {
                        Sorted.put(list.get(i));
                    }

                    data.put("books",Sorted);
                    root.put("database", data);
                    saveData(root.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RefreshList();
                Toast toast = Toast.makeText(getApplicationContext(), "Sorting by: author", Toast.LENGTH_SHORT);
                toast.show();
                sorted = true;
            }


        });

        about = findViewById(R.id.about);
        add = findViewById(R.id.add);

        checkBox = findViewById(R.id.checkbox_owned);
        checkBox.setChecked(false);

        if (checkBox.isChecked()) {
            checkBox.setChecked(false);
        }



        about.setOnClickListener(view -> {
            Intent intent = new Intent(this, About.class);
            startActivity(intent);
        });

        add.setOnClickListener(view -> {
            Intent intent = new Intent(this, AddPage.class);
            startActivity(intent);
        });

        RefreshList();


        countBooks();


        gridView.setOnItemClickListener((parent, view, position, id) -> goToPage(position));}
  

    protected void RefreshList() {

        bookList =new ArrayList<>();


        String json_f =loadData();
        if (json_f == null){
        String json = loadJSON();
        saveData(json);
        json_f = loadData();}

        else {
            json_f = loadData() ;
        }

        LinkedList<StringBuilder> builders = new LinkedList<>();
        LinkedList<StringBuilder> builders2 = new LinkedList<>();

        try {

            JSONObject root = new JSONObject(json_f);
            JSONObject data =  root.getJSONObject("database");

            JSONArray books = data.getJSONArray("books");

            for(int i=0; i<books.length(); i++){
                //int i2 = 2;
                builders.add(new StringBuilder());
                builders2.add(new StringBuilder());
                JSONObject book = books.getJSONObject(i);
                builders.get(i).append(book.getString("name"));
                builders2.get(i).append(book.getString("author"));
                String imageFile = book.optString("image");

                int picture = R.drawable.cover;
                if (checkBox.isChecked()) {
                    if (book.getBoolean("has")) {

                        Item item = new Item(builders.get(i).toString(),builders2.get(i).toString(), picture, i);
                        item.setbookImageFile(imageFile);
                        bookList.add(item);
                    }
                } else {
                    Item item = new Item(builders.get(i).toString(),builders2.get(i).toString(), picture, i);
                    item.setbookImageFile(imageFile);
                    bookList.add(item);

                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MyAdapter myAdapter=new MyAdapter(this,R.layout.grid_view_items,bookList);
        gridView.setAdapter(myAdapter);

    }

    public void countBooks() {
        int nbooks = 0;
        int nauthors = 0;

        bookList = new ArrayList<>();

        ArrayList<String> listauthors = new ArrayList<>();


        String json_f = loadData();
        if (json_f == null) {
            String json = loadJSON();
            saveData(json);
            json_f = loadData();
        } else {
            json_f = loadData();
        }

        try {
            JSONObject root = new JSONObject(json_f);
            JSONObject data = root.getJSONObject("database");

            JSONArray livros = data.getJSONArray("books");


            for (int i = 0; i < livros.length(); i++) {
                nbooks++;
                JSONObject book = livros.getJSONObject(i);
                String aut = book.getString("name");

                if (!(listauthors.contains(aut))) {
                    nauthors++;
                    listauthors.add(aut);
                }

            }
        }
        catch(JSONException e){
                e.printStackTrace();
            }
        nlivros.setText("Livros: "+nbooks);
        nautor.setText("Autores: "+nauthors);
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
    public void onCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        switch(view.getId()) {
            case R.id.checkbox_owned:
                if (checked){
                    RefreshList();
                }
                // Do something
            else
                RefreshList();// Do something else
                break;

        }
    }

    }
