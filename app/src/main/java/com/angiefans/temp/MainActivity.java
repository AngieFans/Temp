package com.angiefans.temp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.angiefans.temp.entity.Person;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View mBtnShowHashCode = findViewById(R.id.mBtnShowHashCode);
        mBtnShowHashCode.setOnClickListener(v -> {
            Person p1 = new Person();
            Person p2 = new Person();
            p1.name = "aaa";
            p2.name = "aaa";
            System.out.println(p1.hashCode());
            System.out.println(p2.hashCode());
            System.out.println("1");
        });
    }

}
