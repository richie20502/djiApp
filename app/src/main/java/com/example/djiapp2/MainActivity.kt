package com.example.djiapp2

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.djiapp2.ui.theme.DjiApp2Theme
import dji.common.error.DJIError
import dji.sdk.base.BaseComponent
import dji.sdk.base.BaseProduct
import dji.sdk.sdkmanager.DJISDKInitEvent
import dji.sdk.sdkmanager.DJISDKManager

class MainActivity : ComponentActivity() {

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val connectionStatus = remember { mutableStateOf("Checking connection...") }

            // Inicialización del SDK de DJI
            DJISDKManager.getInstance().registerApp(this, object : DJISDKManager.SDKManagerCallback {
                override fun onRegister(djiError: DJIError?) {
                    if (djiError == null) {
                        DJISDKManager.getInstance().startConnectionToProduct()
                        Toast.makeText(applicationContext, "Registro exitoso del SDK", Toast.LENGTH_LONG).show()
                        Log.d(TAG, "SDK registrado exitosamente.")
                    } else {
                        Toast.makeText(applicationContext, "Error en el registro del SDK: ${djiError.description}", Toast.LENGTH_LONG).show()
                        Log.e(TAG, "Error al registrar el SDK: ${djiError.description}")
                    }
                }

                override fun onProductDisconnect() {
                    connectionStatus.value = "Dron desconectado"
                    Toast.makeText(applicationContext, "Dron desconectado", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "El dron se ha desconectado.")
                }

                override fun onProductConnect(product: BaseProduct?) {
                    if (product != null && product.isConnected) {
                        connectionStatus.value = "Dron conectado: ${product.model.displayName}"
                        Toast.makeText(applicationContext, "Dron conectado: ${product.model.displayName}", Toast.LENGTH_LONG).show()
                        Log.d(TAG, "Dron conectado: ${product.model.displayName}")
                    } else {
                        connectionStatus.value = "Dron no conectado"
                        Toast.makeText(applicationContext, "Dron no conectado", Toast.LENGTH_SHORT).show()
                        Log.e(TAG, "Error al conectar el dron.")
                    }
                }

                override fun onComponentChange(
                    key: BaseProduct.ComponentKey?,
                    oldComponent: BaseComponent?,
                    newComponent: BaseComponent?
                ) {
                    Log.d(TAG, "Component key: $key cambiado de $oldComponent a $newComponent")
                }

                override fun onProductChanged(product: BaseProduct?) {
                    Log.d(TAG, "Producto cambiado: $product")
                }

                override fun onInitProcess(event: DJISDKInitEvent?, totalProcess: Int) {
                    Log.d(TAG, "Proceso de inicialización: $event con total $totalProcess")
                }

                override fun onDatabaseDownloadProgress(current: Long, total: Long) {
                    Log.d(TAG, "Progreso de descarga de base de datos: $current / $total")
                }
            })

            DjiApp2Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = connectionStatus.value,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = name,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DjiApp2Theme {
        Greeting("Hello World")
    }
}
