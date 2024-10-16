package com.example.restclientservweb;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

import com.example.restclientservweb.Track;
import com.example.restclientservweb.TrackApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private TrackApiService tracksApiService;
    private EditText titleEditText;
    private EditText singerEditText;
    private EditText idEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        titleEditText = findViewById(R.id.titleEditText);
        singerEditText = findViewById(R.id.singerEditText);
        idEditText = findViewById(R.id.idEditText);

        Button addTrackButton = findViewById(R.id.addTrackButton);
        Button getTrackButton = findViewById(R.id.getTrackButton);
        Button updateTrackButton = findViewById(R.id.updateTrackButton);
        Button deleteTrackButton = findViewById(R.id.deleteTrackButton);

        // Configurar Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/dsaApp/") // Cambia a la URL de tu servidor
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        tracksApiService = retrofit.create(TrackApiService.class);

        // Configurar el botón para agregar una pista
        addTrackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTrack();
            }
        });

        // Configurar el botón para obtener todas las pistas
        getTrackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAllTracks();
            }
        });

        // Configurar el botón para actualizar una pista
        updateTrackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTrack();
            }
        });

        // Configurar el botón para eliminar una pista
        deleteTrackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTrack();
            }
        });
    }

    private void addTrack() {
        String title = titleEditText.getText().toString();
        String singer = singerEditText.getText().toString();

        Track track = new Track(null, title, singer);
        Call<Track> call = tracksApiService.addTrack(track);
        call.enqueue(new Callback<Track>() {
            @Override
            public void onResponse(Call<Track> call, Response<Track> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Track added successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Failed to add track", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Track> call, Throwable t) {
                Log.e("API Error", "Failed to add track", t);
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getAllTracks() {
        Call<List<Track>> call = tracksApiService.getAllTracks();
        call.enqueue(new Callback<List<Track>>() {
            @Override
            public void onResponse(Call<List<Track>> call, Response<List<Track>> response) {
                if (response.isSuccessful()) {
                    List<Track> tracks = response.body();
                    if (tracks != null && !tracks.isEmpty()) {
                        StringBuilder resultBuilder = new StringBuilder();
                        for (Track track : tracks) {
                            resultBuilder.append("ID: ").append(track.getId())
                                    .append("\nTitle: ").append(track.getTitle())
                                    .append("\nSinger: ").append(track.getSinger())
                                    .append("\n\n");
                        }
                        // Muestra los resultados en el TextView
                        TextView resultsTextView = findViewById(R.id.resultsTextView);
                        resultsTextView.setText(resultBuilder.toString());
                    } else {
                        Toast.makeText(MainActivity.this, "No tracks found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Failed to fetch tracks", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Track>> call, Throwable t) {
                Log.e("API Error", "Failed to fetch tracks", t);
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    // Función para actualizar una pista
    private void updateTrack() {
        String id = idEditText.getText().toString();
        String title = titleEditText.getText().toString();
        String singer = singerEditText.getText().toString();

        // Validación de campos vacíos
        if (id.isEmpty() || title.isEmpty() || singer.isEmpty()) {
            Toast.makeText(MainActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Creación del objeto Track actualizado
        Track updatedTrack = new Track(id, title, singer);
        Call<Track> call = tracksApiService.updateTrack(id, updatedTrack);

        // Llamada a Retrofit para actualizar el Track
        call.enqueue(new Callback<Track>() {
            @Override
            public void onResponse(Call<Track> call, Response<Track> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Track updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Update failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Track> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void deleteTrack() {
        String id = idEditText.getText().toString();

        Call<Void> call = tracksApiService.deleteTrack(id);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Track deleted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Failed to delete track", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("API Error", "Failed to delete track", t);
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
