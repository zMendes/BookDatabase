package br.edu.insper.al.leonardomm4.bookdatabase;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class About extends AppCompatActivity {

    private TextView icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        icon = findViewById(R.id.icon);
        icon.setOnClickListener(view -> {
            Intent intent = new Intent(this, Homepage.class);
            startActivity(intent);
        });
    }
}
