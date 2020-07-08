/*
 * The MIT License
 *
 * Copyright: Copyright (C) 2014 T2Ti.COM
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * The author may be contacted at: t2ti.com@gmail.com
 *
 * @author Claudio de Barros (T2Ti.com)
 * @version 2.0
 */
package com.t2ti.nfcemobile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.t2tierp.model.bean.nfe.NfeCabecalho;

import org.apache.commons.io.IOUtils;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

public class RequisicaoWebService {

    private final String TAG = "RequisicaoWEB";
    private Gson gson;

    public RequisicaoWebService() {

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(java.util.Date.class, new JsonDeserializer<Date>() {
            @Override
            public java.util.Date deserialize(com.google.gson.JsonElement p1, java.lang.reflect.Type p2,
                                              com.google.gson.JsonDeserializationContext p3) {
                try {
                    return new java.util.Date(p1.getAsLong());
                } catch(Exception e) {
                    return null;
                }
            }
        });
        gson = builder.create();
    }

    public NfeCabecalho emissao(NfeCabecalho nfe) throws Exception {
        URL url = new URL("http://192.168.0.20:8080/nfce-mobile/rest/emissao/envio");

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(5000);

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        connection.setDoOutput(true);

        String nfeJson = gson.toJson(nfe);

        OutputStream os = connection.getOutputStream();
        os.write(nfeJson.getBytes("UTF-8"));
        os.close();

        connection.connect();
        int status = connection.getResponseCode();
        if (status == HttpURLConnection.HTTP_OK) {
            String retorno = IOUtils.toString(connection.getInputStream(), "UTF-8");
            nfe = gson.fromJson(retorno, NfeCabecalho.class);
        } else {
            throw new Exception("Erro: " + IOUtils.toString(connection.getErrorStream(), "UTF-8"));
        }
        return nfe;
    }

    public String cancelamento(String numero, String justificativa) throws Exception {
        URL url = new URL("http://192.168.0.20:8080/nfce-mobile/rest/emissao/cancela?numero=" + numero + "&justificativa=" + justificativa.replaceAll(" ", "%20"));

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(5000);

        connection.setRequestMethod("GET");

        connection.connect();
        int status = connection.getResponseCode();
        if (status == HttpURLConnection.HTTP_OK) {
            return IOUtils.toString(connection.getInputStream(), "UTF-8");
        } else {
            throw new Exception("Erro: " + IOUtils.toString(connection.getErrorStream(), "UTF-8"));
        }
    }

}
