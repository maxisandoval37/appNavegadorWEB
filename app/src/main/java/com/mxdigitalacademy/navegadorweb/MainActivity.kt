package com.mxdigitalacademy.navegadorweb

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.URLUtil
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.widget.SearchView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private fun clienteWebChrome(){
        webView.webChromeClient = object : WebChromeClient(){

        }
    }

    private fun clienteWebBrowser(){
        webView.webViewClient = object : WebViewClient() {

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) { //cuando se recarga una pag mostrar swipe
                super.onPageStarted(view, url, favicon)

                searchView.setQuery(url, false)//en false, nos muestra la url actual, en true no
                swipeRefrescar.isRefreshing = true
            }

            override fun onPageFinished(view: WebView?, url: String?) { //cuando se termina de refrescar, ocultar componente
                super.onPageFinished(view, url)

                swipeRefrescar.isRefreshing = false
            }

        }
    }

    private fun habilitarJavaScript(){
        val configuraciones = webView.settings
        configuraciones.javaScriptEnabled = true
    }

    private fun establecerPagInicio(urlInicio:String){
        webView.loadUrl(urlInicio)
    }

    override fun onBackPressed() {//para cuando presionamos la tecla volver
        if (webView.canGoBack())
            webView.goBack()
        else
            super.onBackPressed()
    }

    private fun refrescarPantalla(){
        swipeRefrescar.setOnRefreshListener {
            webView.reload()
        }
    }

    private fun extraerDominioDeUnaURL(URL: String): String? {

        var auxURLReverso = URL.reversed()
        var dominioValido:String? =""
        var cont =0
        //hacemos reverse para tomar el ultimo dom válido. Ej1 .com.ar (valida ra.)
        //                                                 Ej2 .com56465 (sabe que es invalido)

        if (!URL.contains('.'))
            return ""//el string, no tiene un dominio

        while (cont<auxURLReverso.length){
            if (auxURLReverso[cont] == '.') {
                dominioValido += auxURLReverso[cont]
                break
            }
            else{
                dominioValido += auxURLReverso[cont]
                cont++
            }
        }

        dominioValido=dominioValido?.reversed()
        return dominioValido
    }

    private fun verificarDominioValidoDeUnaURL(URL: String):Boolean {
       return ( Regex(pattern = """\.[aA-zZ]{1,4}""").matches(input = extraerDominioDeUnaURL(URL).toString()) )
    }

    private fun barraDeBusqueda(){
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,android.widget.SearchView.OnQueryTextListener {//captura el input

            override fun onQueryTextChange(newText: String?): Boolean {
                return false//ya que somos nosotros quien decide la busqueda
            }

            override fun onQueryTextSubmit(query: String?): Boolean {//se activa cuando damos enter
                query?.let{

                    if (verificarDominioValidoDeUnaURL(it)) {
                        if (URLUtil.isValidUrl(it))
                            webView.loadUrl(it)
                        else
                            webView.loadUrl("https://$it")
                    }
                    else
                        webView.loadUrl("https://google.com/search?q=$it")
                }
                return false
            }
        })
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        clienteWebChrome()

        clienteWebBrowser()

        habilitarJavaScript()

        establecerPagInicio("https://google.com")

       // onBackPressed()

        refrescarPantalla()

        barraDeBusqueda()

    }

}