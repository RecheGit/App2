package com.example.app2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
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
import java.util.HashMap;
import java.util.Map;

public class Registro extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        Button btnRegistrar = findViewById(R.id.buttonRegistrarDesdeMenuRegistro);
        Button btnVolverAIniciarSesion = findViewById(R.id.buttonVolverIniciarSesion);


        EditText textoNombreUsuario = findViewById(R.id.editTextUsernameRegistro);
        EditText textoContraseña = findViewById(R.id.editTextPasswordRegistro);

        //Pulsamos Volver

        btnVolverAIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    //PULSAMOS REGISTRAR
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String URLComprobacion ="http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/kreche001/WEB/ComprobacionRegistro.php";
                verificarCampos(URLComprobacion);
            }
        });
    }

    //COmprobamos que los campos estan correctamente rellenados y no es un usuario ya registrado
    private void verificarCampos(String URLComprobacion){
        EditText textoNombreUsuario = findViewById(R.id.editTextUsernameRegistro);
        EditText textoContraseña = findViewById(R.id.editTextPasswordRegistro);
        EditText textoCorreo = findViewById(R.id.editTextCorreoReg);

        if(textoNombreUsuario.getText().toString().length() > 0 & textoContraseña.getText().toString().length() > 0 & textoCorreo.getText().toString().length()>0){

                StringRequest stringRequest = new StringRequest(Request.Method.POST, URLComprobacion, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.contains("1")){
                            Toast.makeText(Registro.this, "Este usuario ya existe", Toast.LENGTH_SHORT).show();
                        }
                        else{

                            registrarDispositivo();
                           
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Registro.this, "Ha habido un problema al conectarse. Intentelo otra vez", Toast.LENGTH_SHORT);

                    }
                }) {
                    @Nullable
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {

                        Map<String, String> parametros = new HashMap<String, String>();
                        parametros.put("nombreUsuario", textoNombreUsuario.getText().toString());
                        parametros.put("email",textoCorreo.getText().toString());
                        parametros.put("contraseña", textoContraseña.getText().toString());
                        return parametros;
                    }
                };


                RequestQueue requestQueue = Volley.newRequestQueue(Registro.this);
                requestQueue.add(stringRequest);

        }
        else{
            Toast.makeText(Registro.this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
        }
    }

    //una vez lo comprobamos añadimos al usuario a la bd
    private void insertarNuevoUser(String URLInsercion, String tokenUsu){
        EditText textoNombreUsuario = findViewById(R.id.editTextUsernameRegistro);
        EditText textoContraseña = findViewById(R.id.editTextPasswordRegistro);
        EditText textoCorreo = findViewById(R.id.editTextCorreoReg);


         StringRequest busquedaLog = new StringRequest(Request.Method.POST, URLInsercion, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                /*
                Toast.makeText(Registro.this, "Usuario creado correctamente", Toast.LENGTH_SHORT).show();
                finish();
                Intent intent = new Intent(Registro.this, MenuPrincipal.class);
                startActivity(intent);
                */


            }
            }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Registro.this, "Ha habido un problema al conectarse. Intentelo otra vez", Toast.LENGTH_SHORT);

            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String,String> parametros = new HashMap<String,String>();
                parametros.put("nombreUsuario",textoNombreUsuario.getText().toString());
                parametros.put("contraseña",textoContraseña.getText().toString());
                parametros.put("email",textoCorreo.getText().toString());
                parametros.put("tokenID",tokenUsu);
                return parametros;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(Registro.this);
        requestQueue.add(busquedaLog);

    }


    //tambien añadimos el tokenID a la vase de datos.Cada usuario con su deviceId(Las cuentas del mismo dispositivo tienen mismo tokenID)
    private void registrarDispositivo(){


        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(ContentValues.TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        String URLInsertar = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/kreche001/WEB/Registro.php";


                        insertarNuevoUser(URLInsertar,token);
                        enviarNotificacionFireBase(token);
                    }
                });
    }

    //Enviamos la notificacion al registrarnos correctamente
    private void enviarNotificacionFireBase(String tokenUsu){
        Log.i("ENVIADO", tokenUsu);
        String URL = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/kreche001/WEB/EnviarNotificacionFirebase.php";

        EditText textoNombreUsuario = findViewById(R.id.editTextUsernameRegistro);

        StringRequest busquedaLog = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(Registro.this, "Usuario creado correctamente", Toast.LENGTH_SHORT).show();
                finish();
                Intent intent = new Intent(Registro.this, MenuPrincipal.class);
                startActivity(intent);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Registro.this, "Ha habido un problema al conectarse. Intentelo otra vez", Toast.LENGTH_SHORT);

            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String,String> parametros = new HashMap<String,String>();
                parametros.put("nombreUsuario",textoNombreUsuario.getText().toString());
                parametros.put("tokenID",tokenUsu);
                return parametros;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(Registro.this);
        requestQueue.add(busquedaLog);




    }
}
