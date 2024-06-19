package com.example.tienda;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.core.view.WindowInsetsCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DasboardInicioSesion extends AppCompatActivity {
    EditText inputNombre, inputContrasena;
    Button btnIniciarSesion;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference().child("usuario");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dasboard_inicio_sesion);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inputNombre = findViewById(R.id.Etxt_ingresarNombre);
        inputContrasena = findViewById(R.id.Etxt_ingresarClave);
        btnIniciarSesion = findViewById(R.id.Btn_Ingresar);

        btnIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarSesion();
            }
        });
    }

    public void iniciarSesion() {
        String nombre = inputNombre.getText().toString().trim();
        String contrasena = inputContrasena.getText().toString().trim();

        if (nombre.isEmpty() || contrasena.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Por favor, complete todos los campos", Toast.LENGTH_LONG).show();
            return;
        }

        reference.orderByChild("nombre").equalTo(nombre).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //String admin = String.valueOf(database.getReference("nombre"));
                if (dataSnapshot.exists()) {
                    boolean usuarioEncontrado = false;
                    for (DataSnapshot usuarioSnapshot : dataSnapshot.getChildren()) {
                        String contrasenaDB = usuarioSnapshot.child("contrasena").getValue(String.class);
                        if (contrasenaDB != null && contrasenaDB.equals(contrasena)) {
                            usuarioEncontrado = true;
                            String esAdmin = usuarioSnapshot.child("nombre").getValue(String.class);
                            if (esAdmin != null && "Admin".equals(nombre)) {
                                irADashboardAdmin();
                            } else {
                                irAHome();
                            }
                            break; // Salimos del bucle si encontramos al usuario.
                        }
                    }
                    if (!usuarioEncontrado) {
                        Toast.makeText(getApplicationContext(), "Contraseña incorrecta", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Nombre de usuario incorrecto", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Error en la base de datos: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void irAHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void irADashboardAdmin() {
        Intent intent = new Intent(this, ActivityDasboarAdin.class);
        startActivity(intent);
    }
}