package com.mustafakamber.composeretro

import android.media.MediaCrypto
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import com.mustafakamber.composeretro.model.CryptoModel
import com.mustafakamber.composeretro.service.CryptoAPI
import com.mustafakamber.composeretro.ui.theme.ComposeRetroTheme
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeRetroTheme {
               MainScreen()

            }
        }
    }
}

@Composable
fun MainScreen(){

    var cryptoModels = remember { mutableStateListOf<CryptoModel>() }


    val BASE_URL = "https://raw.githubusercontent.com/"

    //Retrofit kurulumu
    val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(CryptoAPI::class.java)

    //Jsondan veriyi cekme
    val call = retrofit.getData()

    call.enqueue(object: Callback<List<CryptoModel>>{
        override fun onResponse(
            call: Call<List<CryptoModel>>,
            response: Response<List<CryptoModel>>
        ) {
            //Veri cekme isteginden bir cevap gelirse
            if(response.isSuccessful){
                //Cevap basarili geldi
                response.body()?.let {
                    //Dolu bir cevap geldi(Veriler Ekranda gosterilecek)
                    //List
                    cryptoModels.addAll(it)
                }
            }

        }

        override fun onFailure(call: Call<List<CryptoModel>>, t: Throwable) {
            //Veri cekme isteginden bir cevap gelmezse
            t.printStackTrace()
        }
    })

    Scaffold(topBar = {AppBar()}) {
        CryptoList(cryptos = cryptoModels)
    }

}

@Composable
fun CryptoList(cryptos : List<CryptoModel>){
    //LazyColumn
    LazyColumn(contentPadding = PaddingValues(5.dp)){
        items(cryptos){ crypto ->
            CryptoRow(crypto = crypto)
        }
    }
}

@Composable
fun CryptoRow(crypto: CryptoModel){
    //Ekranda gosterilecek objeler
    Column(modifier = Modifier
        .fillMaxWidth()
        .background(color = MaterialTheme.colors.surface)){
        Text(text = crypto.currency,
            style = MaterialTheme.typography.h4,
            modifier = Modifier.padding(2.dp),
            fontWeight = FontWeight.Bold
            )
        Text(text = crypto.price,
            style = MaterialTheme.typography.h5,
            modifier = Modifier.padding(2.dp)
            )
    }

}

@Composable
fun AppBar(){
    //TopBar olusturma
    TopAppBar(contentPadding =  PaddingValues(10.dp)){
        Text(text = "Retrofit Compose",fontSize = 26.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeRetroTheme {
        CryptoRow(crypto = CryptoModel("BTC","1233"))
    }
}