package com.jherrera.taskactivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jherrera.taskactivity.config.WebServices;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ModificarActivity extends AppCompatActivity {

    //id de la tarea a borrar o modificar
    private int idTarea = 0;
    private EditText editTextNombre;
    private EditText editTextDescripcion;
    private EditText editTextFecha;
    private EditText editTextHora;
    private int estadoTarea = 0;
    private Switch switchStatusTarea;
    private Button buttonModificar;
    private Button buttonEliminar;
    private WebServices webServices = new WebServices();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar);
        setInitComponents();
        setTask();
        setActionButtons();
    }

    private void setTask() {
        Bundle bundle = getIntent().getExtras();
        idTarea = bundle.getInt("id");

        String nombre = bundle.getString("nombre");
        String descripcion = bundle.getString("descripcion");
        String fecha = bundle.getString("fecha");
        String hora = bundle.getString("hora");
        estadoTarea = bundle.getInt("status");

        editTextNombre.setText(nombre);
        editTextDescripcion.setText(descripcion);
        editTextFecha.setText(fecha);
        editTextHora.setText(hora);

        if (estadoTarea == 1) {
            switchStatusTarea.setChecked(true);
        }
    }

    private void setActionButtons() {
        buttonModificar.setOnClickListener(view -> {
            //agregar funcionalidad de modificar aqui
            ModifyTask();
        });

        buttonEliminar.setOnClickListener(view -> {
            //agregar funcion de eliminar aqui
            deleteTask();
        });

        //Eventos para mostrar DatePickerDialog
        editTextFecha.setOnFocusChangeListener((view, isFocus) -> {
            if(isFocus) {
                mostrarCalendario();
            }
        });

        //evento para mostrar TimePickerDialog
        editTextHora.setOnFocusChangeListener((view, isFocus) -> {
            if (isFocus) {
                mostrarReloj();
            }
        });

        //marcar y desmarcar estado de tarea
        switchStatusTarea.setOnCheckedChangeListener((item, isCheck) -> {
            if(isCheck){
                estadoTarea = 1;
            }else {
                estadoTarea = 0;
            }
        });
    }

    private void mostrarReloj() {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        // Mostrar dialogo para seleeccionar minutos
        TimePickerDialog timePickerDialog = new TimePickerDialog(ModificarActivity.this,
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
                ModificarActivity.this,
                (v, yearDatePiker, monthOfYear, dayOfMonth) -> {
                    editTextFecha.setText(yearDatePiker + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                },
                year, month, day
        );
        datePickerDialog.show();
    }


    //peticion para modificicacion de datos
    private void ModifyTask() {
        RequestQueue queue = Volley.newRequestQueue(ModificarActivity.this);
        try {
            StringRequest request = new StringRequest(Request.Method.POST, webServices.urlWebServices, response -> {
                try {
                    JSONObject json = new JSONObject(response);
                    String result = json.getString("resultado");
                    Toast.makeText(ModificarActivity.this, result, Toast.LENGTH_SHORT).show();
                }catch (Exception e) {
                    Log.e("Error", e.getMessage());
                }
            }, error -> {
                Toast.makeText(ModificarActivity.this, "Error "+error.getMessage(), Toast.LENGTH_LONG).show();
            }){
                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("accion", "actualizar");
                    params.put("id", String.valueOf(idTarea));
                    params.put("nombre", editTextNombre.getText().toString());
                    params.put("descripcion", editTextDescripcion.getText().toString());
                    params.put("fecha", editTextFecha.getText().toString());
                    params.put("hora", editTextHora.getText().toString());
                    params.put("status", String.valueOf(estadoTarea));
                    return params;
                }
            };

            queue.add(request);
        }catch (Exception e) {
            Toast.makeText(ModificarActivity.this, "Error en tiempo de ejecución: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    //Peticion para eliminar la tarea
    private void deleteTask() {
        RequestQueue queue = Volley.newRequestQueue(ModificarActivity.this);
        try {
            StringRequest request = new StringRequest(Request.Method.POST, webServices.urlWebServices, response -> {
                try {
                    JSONObject json = new JSONObject(response);
                    String result = json.getString("resultado");
                    Toast.makeText(ModificarActivity.this, result, Toast.LENGTH_SHORT).show();

                    //Espera 3 segundos para cerrar la ventana luego de eliminar
                    new Handler(Looper.getMainLooper()).postDelayed(
                            ModificarActivity.this::finish, 3000
                    );

                }catch (Exception e) {
                    Log.e("Error", e.getMessage());
                }
            }, error -> {
                Toast.makeText(ModificarActivity.this, "Error "+error.getMessage(), Toast.LENGTH_LONG).show();
            }){
                @NonNull
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("accion", "eliminar");
                    params.put("id", String.valueOf(idTarea));
                    return params;
                }
            };

            queue.add(request);
        }catch (Exception e) {
            Toast.makeText(ModificarActivity.this, "Error en tiempo de ejecución: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    //inicializamos componentes de la vista
    private void setInitComponents() {
        editTextNombre = findViewById(R.id.editTextNombre);
        editTextDescripcion = findViewById(R.id.ediTextDescripcion);
        editTextFecha = findViewById(R.id.editTextFecha);
        editTextHora = findViewById(R.id.editTextHora);
        buttonModificar = findViewById(R.id.buttonModificar);
        buttonEliminar = findViewById(R.id.buttonEliminar);
        switchStatusTarea = findViewById(R.id.switchStatusTarea);
    }

    @Override
    public void onBackPressed() {
        super.finish();
    }
}