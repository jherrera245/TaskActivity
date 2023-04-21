package com.jherrera.taskactivity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jherrera.taskactivity.config.WebServices;
import com.jherrera.taskactivity.controllers.TareasAdapter;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AgregarActivity extends AppCompatActivity {

    private EditText editTextNombre;
    private EditText editTextDescripcion;
    private EditText editTextFecha;
    private EditText editTextHora;
    private Button buttonAgregar;

    private WebServices webServices = new WebServices();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar);
        setInitComponents();
        setActionButtons();
    }

    private void setActionButtons() {
        buttonAgregar.setOnClickListener(view -> {
            saveNewTask();
        });

        editTextFecha.setOnFocusChangeListener((view, isFocus) -> {
            if(isFocus) {
                mostrarCalendario();
            }
        });

        editTextHora.setOnFocusChangeListener((view, isFocus) -> {
            if (isFocus) {
                mostrarReloj();
            }
        });
    }

    private void mostrarReloj() {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        // Mostrar dialogo para seleeccionar minutos
        TimePickerDialog timePickerDialog = new TimePickerDialog(AgregarActivity.this,
                (viewDialog, hourOfDay, minuteDialog) -> {
                    editTextHora.setText(hourOfDay + ":" + minuteDialog);
                },
                hour, minute, false);
        timePickerDialog.show();
    }

    private void mostrarCalendario() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        // Motramos un date piker para mostrar un calendario
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                AgregarActivity.this,
                (v, yearDatePiker, monthOfYear, dayOfMonth) -> {
                    editTextFecha.setText(yearDatePiker + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    //peticion para almacenamiento de datos
    private void saveNewTask() {
        RequestQueue queue = Volley.newRequestQueue(AgregarActivity.this);
        try {
            StringRequest request = new StringRequest(Request.Method.POST, webServices.urlWebServices, response -> {
                try {
                    JSONObject json = new JSONObject(response);
                    String result = json.getString("resultado");
                    clearEditText();
                    Toast.makeText(AgregarActivity.this, result, Toast.LENGTH_SHORT).show();
                }catch (Exception e) {
                    Log.e("Error", e.getMessage());
                }
            }, error -> {
                Toast.makeText(AgregarActivity.this, "Error "+error.getMessage(), Toast.LENGTH_LONG).show();
            }){
                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("accion", "insertar");
                    params.put("nombre", editTextNombre.getText().toString());
                    params.put("descripcion", editTextDescripcion.getText().toString());
                    params.put("fecha", editTextFecha.getText().toString());
                    params.put("hora", editTextHora.getText().toString());
                    return params;
                }
            };

            queue.add(request);
        }catch (Exception e) {
            Toast.makeText(AgregarActivity.this, "Error en tiempo de ejecución: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    //metodo para liempiar caja de texto
    private void clearEditText() {
        editTextNombre.setText(null);
        editTextDescripcion.setText(null);
        editTextHora.setText(null);
        editTextFecha.setText(null);
    }

    //inicializando componentes de la vista
    private void setInitComponents() {
        editTextNombre = findViewById(R.id.editTextNombre);
        editTextDescripcion = findViewById(R.id.ediTextDescripcion);
        editTextFecha = findViewById(R.id.editTextFecha);
        editTextHora = findViewById(R.id.editTextHora);
        buttonAgregar = findViewById(R.id.buttonAgregar);
    }

    @Override
    public void onBackPressed() {
        super.finish();
    }
}