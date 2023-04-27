package com.example.app2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.Console;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Pedimos los permisos necesarios para recibir notificaciones
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)!=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new
                    String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 11);
        }

        Button btnIniciarSesion = findViewById(R.id.buttonIniciarSesion);
        Button btnRegistrar = findViewById(R.id.buttonRegistrar);



        //PULSAMOS INICIAR SESIÓN
        btnIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                comprobarExistenteLogin();
            }
        });

        //PULSAMOS REGISTRAR
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Registro.class);
                startActivity(intent);

            }
        });
    }

    //Accedemos al fichero php para que compruebe si el usuario es correcto o no

    private void comprobarExistenteLogin(){

        EditText textoNombreUsuario = findViewById(R.id.editTextUsernameLog);
        EditText textoContraseña = findViewById(R.id.editTextPasswordLog);
        EditText textoCorreo = findViewById(R.id.editTextCorreoLog);
        String URL ="http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/kreche001/WEB/ComprobacionUsuario.php";

    StringRequest busquedaLog = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(response.contains("1")){
                    finish();
                    Toast.makeText(MainActivity.this, "Bienvenido...", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, MenuPrincipal.class);
                    startActivity(intent);

                }
                else{
                    Toast.makeText(MainActivity.this, "No existe el usuario", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Ha habido un problema al conectarse. Intentelo otra vez", Toast.LENGTH_SHORT).show();

            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String,String> parametros = new HashMap<String,String>();
                parametros.put("nombreUsuario",textoNombreUsuario.getText().toString());
                parametros.put("email",textoCorreo.getText().toString());
                parametros.put("contraseña",textoContraseña.getText().toString());
                return parametros;
            }
        };


        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(busquedaLog);

    }


}