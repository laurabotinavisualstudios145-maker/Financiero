package com.miempresaxyz.financiero;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONObject;

public class RegistroCliente extends AppCompatActivity {

    private Spinner listaTipoDoc;
    private EditText numDoc, apellidos, nombres, usuario, clave, confirmaClave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro_cliente2);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Vinculación de vistas
        listaTipoDoc = findViewById(R.id.spnTD);
        numDoc = findViewById(R.id.edtNumDoc2);
        apellidos = findViewById(R.id.edtApellidos2);
        nombres = findViewById(R.id.edtNombres2);
        usuario = findViewById(R.id.edtUsuario);
        clave = findViewById(R.id.edtClave);
        confirmaClave = findViewById(R.id.edtConfirmaClave);

        String[] strOpcTipDoc = {
                "Seleccione una Opción",
                "Tarjeta de Identidad",
                "Cedula de Ciudadanía",
                "Cedula de Extranjeria",
                "Pasaporte",
                "Permiso de Protección Temporal (PPT)"
        };

        ArrayAdapter<String> adaptadorListaTD = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, strOpcTipDoc);
        adaptadorListaTD.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        listaTipoDoc.setAdapter(adaptadorListaTD);
    }

    public void registraCliente(View v) {
        AlertDialog.Builder Dialogo = new AlertDialog.Builder(RegistroCliente.this);
        String mensaje = "-1";

        if (listaTipoDoc.getSelectedItem().toString().equals("Seleccione una Opción")) {
            mensaje += "\nERROR falta ingresar: Tipo Documento";
        }
        if (numDoc.getText().toString().trim().isEmpty()) {
            mensaje += "\nERROR falta ingresar: Número Documento";
        }
        if (nombres.getText().toString().trim().isEmpty()) {
            mensaje += "\nERROR falta ingresar: Nombres";
        }
        if (apellidos.getText().toString().trim().isEmpty()) {
            mensaje += "\nERROR falta ingresar: Apellidos";
        }
        if (usuario.getText().toString().trim().isEmpty()) {
            mensaje += "\nERROR falta ingresar: Usuario";
        }
        if (clave.getText().toString().trim().isEmpty()) {
            mensaje += "\nERROR falta ingresar: Contraseña";
        }
        if (confirmaClave.getText().toString().trim().isEmpty()) {
            mensaje += "\nERROR falta ingresar: Confirmar Contraseña";
        }
        if (!clave.getText().toString().isEmpty() && !confirmaClave.getText().toString().isEmpty()) {
            if (!clave.getText().toString().equals(confirmaClave.getText().toString())) {
                mensaje += "\nERROR: Las contraseñas no coinciden";
            }
        }

        if (mensaje.equals("-1")) {
            JSONArray jsaArreglo;
            JSONObject jsoElemento;
            String catalogoActual;

            SharedPreferences shp = getSharedPreferences("usuariosFinanciero.txt", Context.MODE_PRIVATE);
            catalogoActual = shp.getString("Catalogo_Usuarios", "-1");

            try {
                if (catalogoActual.equals("-1")) {
                    jsaArreglo = new JSONArray();
                } else {
                    jsaArreglo = new JSONArray(catalogoActual);
                }

                // Generación de Hash SHA-256
                String claveCifrada = Seguridad.sha256(clave.getText().toString());

                jsoElemento = new JSONObject();
                jsoElemento.put("tipo_documento", listaTipoDoc.getSelectedItem().toString());
                jsoElemento.put("num_documento", numDoc.getText().toString().trim());
                jsoElemento.put("nombres", nombres.getText().toString().trim());
                jsoElemento.put("apellidos", apellidos.getText().toString().trim());
                jsoElemento.put("usuario", usuario.getText().toString().trim());
                jsoElemento.put("clave", claveCifrada);

                jsaArreglo.put(jsoElemento);

                SharedPreferences.Editor edt = shp.edit();
                edt.putString("Catalogo_Usuarios", jsaArreglo.toString(0));
                edt.apply();

                Dialogo.setMessage("Cliente Registrado Exitosamente")
                        .setCancelable(false)
                        .setPositiveButton("ACEPTAR", (dialogInterface, i) -> dialogInterface.cancel());

                AlertDialog DialogoEmergente = Dialogo.create();
                DialogoEmergente.setTitle("CONFIRMACIÓN EXITOSA");
                DialogoEmergente.show();

            } catch (Exception e) {
                Dialogo.setMessage("Error ocurrido -> " + e.getMessage())
                        .setCancelable(false)
                        .setPositiveButton("ACEPTAR", (dialogInterface, i) -> dialogInterface.cancel());

                AlertDialog DialogoEmergente = Dialogo.create();
                DialogoEmergente.setTitle("Error al almacenar en el dispositivo");
                DialogoEmergente.show();
            }

        } else {
            mensaje = mensaje.replace("-1", "");
            Dialogo.setMessage(mensaje)
                    .setCancelable(false)
                    .setPositiveButton("CORREGIR", (dialogInterface, i) -> dialogInterface.cancel());

            AlertDialog DialogoEmergente = Dialogo.create();
            DialogoEmergente.setTitle("ERROR OCURRIDO");
            DialogoEmergente.show();
        }
    }

    public void limpiarInfo(View v) {
        listaTipoDoc.setSelection(0);
        numDoc.setText("");
        apellidos.setText("");
        nombres.setText("");
        usuario.setText("");
        clave.setText("");
        confirmaClave.setText("");
        numDoc.requestFocus();
        Toast.makeText(this, "Campos limpiados correctamente", Toast.LENGTH_SHORT).show();
    }

    public void Regresar(View v) {
        Intent intActPPal = new Intent(this, Login.class);
        startActivity(intActPPal);
        finish();
    }
}