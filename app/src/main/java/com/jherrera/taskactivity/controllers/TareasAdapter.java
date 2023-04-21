package com.jherrera.taskactivity.controllers;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jherrera.taskactivity.ModificarActivity;
import com.jherrera.taskactivity.R;
import com.jherrera.taskactivity.models.Tareas;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TareasAdapter extends RecyclerView.Adapter<TareasAdapter.ViewHolder> implements View.OnClickListener {

    private JSONArray jsonArrayTareas;
    private Context context;

    private View.OnClickListener clickListener;

    public TareasAdapter(JSONArray jsonArrayTareas, Context context) {
        this.jsonArrayTareas = jsonArrayTareas;
        this.context = context;
    }

    //crea un nuevo objeto de tipo tarea
    private Tareas getTarea(JSONObject jsonTarea) {
        try {
            return new Tareas(
                    Integer.parseInt(jsonTarea.getString("id")),
                    jsonTarea.getString("nombre"),
                    jsonTarea.getString("descripcion"),
                    jsonTarea.getString("fecha"),
                    jsonTarea.getString("hora"),
                    Integer.parseInt(jsonTarea.getString("status"))
            );
        }catch (JSONException e) {
            Log.e("Error al crear objeto tarea:", e.getMessage());
            return null;
        }
    }

    @NonNull
    @Override
    public TareasAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_tareas_desing, parent, false);
        view.setOnClickListener(this);
        return  new TareasAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TareasAdapter.ViewHolder holder, int position) {
        try {
            JSONObject jsonTarea =jsonArrayTareas.getJSONObject(position);
            Tareas tarea = getTarea(jsonTarea);

            if (tarea != null) {
                holder.textViewNombre.setText(tarea.getNombre());
                holder.textViewDescripcion.setText(tarea.getDescripcion());
                holder.textViewFecha.setText(tarea.getFecha());
                holder.textViewHora.setText(tarea.getHora());

                if (tarea.getStatus() == 0) {
                    holder.textViewEstado.setText("Pendiente");
                    holder.textViewEstado.setTextColor(Color.RED);
                }else {
                    holder.textViewEstado.setText("Completada");
                    holder.textViewEstado.setTextColor(Color.BLUE);
                }

                holder.buttonMostrarTarea.setOnClickListener(view -> {
                    startEditActivity(tarea);
                });
            }
        }catch (JSONException e) {
            Log.e("Error Json", e.getMessage());
        }
    }

    //metodo para abir actividad para modificar
    private void startEditActivity(Tareas tarea) {
        Intent intent = new Intent(context, ModificarActivity.class);
        intent.putExtra("id", tarea.getId());
        intent.putExtra("nombre", tarea.getNombre());
        intent.putExtra("descripcion", tarea.getDescripcion());
        intent.putExtra("fecha", tarea.getFecha());
        intent.putExtra("hora", tarea.getHora());
        intent.putExtra("status", tarea.getStatus());
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return (jsonArrayTareas != null) ? jsonArrayTareas.length() : 0;
    }

    @Override
    public void onClick(View view) {
        if (clickListener != null) {
            clickListener.onClick(view);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewNombre;
        private TextView textViewDescripcion;
        private TextView textViewFecha;
        private TextView textViewHora;
        private TextView textViewEstado;
        private Button buttonMostrarTarea;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNombre = itemView.findViewById(R.id.textViewTitulo);
            textViewDescripcion = itemView.findViewById(R.id.textViewDescripcion);
            textViewFecha = itemView.findViewById(R.id.textViewFecha);
            textViewHora = itemView.findViewById(R.id.textViewHora);
            textViewEstado = itemView.findViewById(R.id.textViewEstado);
            buttonMostrarTarea = itemView.findViewById(R.id.buttonMostrar);
        }
    }
}
