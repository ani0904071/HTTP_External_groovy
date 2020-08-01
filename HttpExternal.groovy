package com.bv.util

import groovy.json.JsonSlurper
import java.nio.charset.StandardCharsets

class HttpExternal {

    static convertStringToJSON(String response) {
        def parser = new JsonSlurper()
        def jsonResponse = parser.parseText(response)

        return jsonResponse
    }

    static sendPostURLformEncoded(String requestUrl, String urlParameters) {

        def jsonResponse
        InputStream ins = null
        HttpURLConnection conn = null
        Map httpResponseBody = [responseCode: 400, responseData: null, responseMessage: '']

        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8)
        int postDataLength = postData.length
        URL url = new URL(requestUrl)

        try {
            System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2")
            conn = (HttpURLConnection) url.openConnection()
            conn.setDoOutput(true)
            conn.setInstanceFollowRedirects(false)
            conn.setRequestMethod("POST")
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
            conn.setRequestProperty("charset", "utf-8")
            conn.setRequestProperty("Content-Length", Integer.toString(postDataLength))
            conn.setUseCaches(false)

            DataOutputStream wr
            try {
                wr = new DataOutputStream(conn.getOutputStream())
                wr.write(postData)
            } catch (IOException io) {
                println(io.toString())
                httpResponseBody['responseMessage'] = io.toString()
            } catch(Exception e) {
                println(e.toString())
                httpResponseBody['responseMessage'] = e.toString()
            } finally {
                if(wr)
                    wr.close()
            }
            int responseCode = conn.getResponseCode()
            System.out.println("POST Response Code :: " + responseCode)
            httpResponseBody['responseCode'] = responseCode

            try {
                ins = new BufferedInputStream(conn.getInputStream())
                String result = org.apache.commons.io.IOUtils.toString(ins, "UTF-8")
                jsonResponse = convertStringToJSON(result.toString())
                httpResponseBody['responseData'] = jsonResponse
                httpResponseBody['responseMessage'] = 'ok'
            } catch (Exception e) {
                println(e.toString())
                httpResponseBody['responseMessage'] = e.toString()
            } finally {
                if(ins)
                    ins.close()
            }

        }catch (Exception e) {
            println(e.toString())
            httpResponseBody['responseMessage'] = e.toString()
        } finally {
            if (conn)
                conn.disconnect()
        }


        return httpResponseBody
    }

    static sendPostJSON(String requestUrl, String authorizationToken, String jsonInputString, String authType) {

        def jsonResponse
        InputStream ins
        HttpURLConnection conn
        Map httpResponseBody = [responseCode: 400,  responseData: null, responseMessage: '']

        try {
            System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2")
            URL url = new URL(requestUrl)
            conn = (HttpURLConnection) url.openConnection()
            conn.setConnectTimeout(5000)
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
            if(authorizationToken)
                conn.setRequestProperty("Authorization", authType + " " + authorizationToken)
            conn.setDoOutput(true)
            //conn.setDoInput(true)
            conn.setRequestMethod("POST")
            OutputStream os
            try {
                os = conn.getOutputStream()
                os.write(jsonInputString.getBytes("UTF-8"))
            } catch (IOException e) {
                println(e.toString())
            } catch(Exception e) {
                println(e.toString())
            } finally{
                if(os)
                    os.close()
            }

            int responseCode = conn.getResponseCode()
            System.out.println("POST Response Code :: " + responseCode)
            httpResponseBody['responseCode'] = responseCode

            // read the response
            try {
                ins = new BufferedInputStream(conn.getInputStream())
                String result = org.apache.commons.io.IOUtils.toString(ins, "UTF-8")
                jsonResponse = convertStringToJSON(result)
                httpResponseBody['responseData'] = jsonResponse
                httpResponseBody['responseMessage'] = 'ok'
            } catch (IOException io) {
                println(io.toString())
                httpResponseBody['responseMessage'] = io.toString()
            } catch (Exception e) {
                println(e.toString())
                httpResponseBody['responseMessage'] = e.toString()
            } finally {
                if(ins)
                    ins.close()
            }

        } catch(Exception e) {
            println(e.toString())
            httpResponseBody['responseMessage'] = e.toString()
        } finally {
            if (conn != null) {
                try {
                    conn.disconnect()
                } catch (Exception e) { /* ignored */}
            }
        }

        return httpResponseBody
    }

    static sendGetWithAuthToken(String getUrl, String authorizationToken, String authType) {

        def jsonResponse
        InputStream ins = null
        HttpURLConnection conn = null
        Map httpResponseBody = [responseCode: 400,  responseData: null, responseMessage: '']

        try {
            System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2")
            URL obj = new URL(getUrl)
            conn = (HttpURLConnection) obj.openConnection()
            conn.setRequestMethod("GET")
            if(authorizationToken)
                conn.setRequestProperty("Authorization", authType + " " + authorizationToken)

            int responseCode = conn.getResponseCode()
            System.out.println("GET Response Code :: " + responseCode)
            httpResponseBody['responseCode'] = responseCode

            try {
                ins = new BufferedInputStream(conn.getInputStream())
                String result = org.apache.commons.io.IOUtils.toString(ins, "UTF-8")
                jsonResponse = convertStringToJSON(result.toString())
                httpResponseBody['responseData'] = jsonResponse
                httpResponseBody['responseMessage'] = 'ok'
            } catch (IOException io) {
                jsonResponse = io.toString()
                println(io.toString())
                httpResponseBody['responseMessage'] = io.toString()
            } finally {
                if(ins)
                    ins.close()
            }
        } catch (Exception e) {
            println(e.toString())
            httpResponseBody['responseMessage'] = e.toString()
        } finally {
            if(conn)
                conn.disconnect()
        }

        return  httpResponseBody
    }
}
