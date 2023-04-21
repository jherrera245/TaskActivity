package com.jherrera.taskactivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jherrera.taskactivity.config.WebServices;
import com.jherrera.taskactivity.controllers.TareasAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerViewTareas;
    private JSONArray jsonArrayTareas;
    private WebServices webServices = new WebServices();
    private Switch switchMostrarTodos;
    private Switch switchMostrarCompletadas;

    private int filtro = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setInitComponents();
        addTaskList();
        filterTaskList();
    }

    private void setInitComponents() {
        switchMostrarCompletadas = findViewById(R.id.switchMostrarCompletadas);
        switchMostrarTodos = findViewById(R.id.switchMostrarTodas);
        recyclerViewTareas = findViewById(R.id.recyclerViewTareas);
        recyclerViewTareas.setLayoutManager(new LinearLayoutManager(this));
    }

    private void filterTaskList() {

        switchMostrarTodos.setOnCheckedChangeListener((item, isCheck) -> {
            if (isCheck) {
                switchMostrarCompletadas.setEnabled(false);
                filtro = 3;
            }else {
                switchMostrarCompletadas.setEnabled(true);
                filtro = 0;
            }
            addTaskList();
        });

        switchMostrarCompletadas.setOnCheckedChangeListener((item, isCheck) -> {
            if (isCheck) {
                switchMostrarTodos.setEnabled(false);
                filtro = 1;
            }else {
                switchMostrarTodos.setEnabled(true);
                filtro = 0;
            }
            addTaskList();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_app, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int itemMenu = item.getItemId();

        if (itemMenu == R.id.itemAgregarTarea) {
            Intent intent = new Intent(MainActivity.this, AgregarActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void addTaskList(){
        RequestQueue queue = Volley.newRequestQueue(this);
        try {
            StringRequest request = new StringRequest(Request.Method.POST, webServices.urlWebServices, response -> {
                try {
                    JSONObject json = new JSONObject(response);
                    jsonArrayTareas = json.getJSONArray("resultado");
                    TareasAdapter adapter = new TareasAdapter(jsonArrayTareas, this);
                    recyclerViewTareas.setAdapter(adapter);

                    //Toast.makeText(this, response, Toast.LENGTH_LONG).show();
                }catch (Exception e){
                    Log.e("Error JSON", e.getMessage());
                }
            }, error -> {
                Toast.makeText(this, "Error peticion "+error.getMessage(), Toast.LENGTH_LONG).show();
            }){
                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("accion", "listar");
                    params.put("filtro", String.valueOf(filtro));
                    return params;
                }
            };

            queue.add(request);
        }catch (Exception e) {
            Toast.makeText(this, "Error en tiempo de ejecución"+e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        addTaskList();
    }
}