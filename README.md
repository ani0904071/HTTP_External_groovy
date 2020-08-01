# HTTP_External_groovy
Due to using grails version 2.3.8, I was uanble to use latest HTTP builder APIs with groovy in my GRAILS project.
So that, I made my required GET and POST with the help of Java HttpURLConnection Class! 
The response jsonString is converted to jsonObject so that response can be easily parsed!
These included GET and POST functions perform really fast with proper checked exceptions! 
I was using external bank api called TINK! [Here I am not sharing the real clientIDs, clientSecrets and authCodes for privacy]
You can just pass the parameters on your own as required by your program!

The following examples are given as to understand how you should call the functions:

1. to use HttpGET:
   1.1 Without Authorization token: (usually public endpoint)
     String tinkProvidersUrl = 'https://api.tink.se/api/v1/providers/GB'
     HttpExternal.sendGetWithAuthToken(tinkProvidersUrl, null, null)
   1.2 With Authorization token: (usually protected endpoint)
     String tinkAccountsUrl = 'https://api.tink.com/api/v1/accounts/list'
     String accessToken = 'yourStringToken'
     String authType = "Bearer"  // it can be Bearer or Basic
     HttpExternal.sendGetWithAuthToken(tinkAccountsUrl, accessToken, authType)
     
2. to use HttpPOST:
   2.1 type -> URLformEncoded
      String tinkAuthenticationUrl= 'https://api.tink.com/api/v1/oauth/token'
      String urlParameters = "client_id=${yourClientID}" +
                                "&client_secret=${yourClientSecret}" +
                                "&grant_type=authorization_code" +
                                "&code=${theAuthCode}"
      HttpExternal.sendPostURLformEncoded(tinkAuthenticationUrl, urlParameters)
      
   2.2 type -> JSON body
        def tinkAccId = 'someBankAccountId'
        JSONObject searchFilter = new JSONObject()
        JSONArray jsonAccountsArray = new JSONArray()
        jsonAccountsArray.put(tinkAccId)
        try {
            searchFilter.put("order", "DESC")
            searchFilter.put("sort", "DATE")
            searchFilter.put("includeUpcoming", false)
            searchFilter.put("accounts", jsonAccountsArray)
            searchFilter.put("limit", 5000)
        } catch (JSONException e) {
            e.printStackTrace()
        }
       String tinkFilterTransactionUrl = 'https://api.tink.com/api/v1/search'
       String jsonBody = groovy.json.JsonOutput.prettyPrint(groovy.json.JsonOutput.toJson(searchFilter))
       String authType = "Bearer"  // it can be Bearer or Basic
       String accessToken = 'yourStringToken'
       HttpExternal.sendPostJSON(tinkFilterTransactionUrl, accessToken, jsonBody, authType)
   
   
 Succesful Response: 
 {LinkedHashMap@17665}  size = 3
 0 = {LinkedHashMap$Entry@17749} "responseCode" -> "200"
 1 = {LinkedHashMap$Entry@17750} "responseData" -> " size = 6"
 2 = {LinkedHashMap$Entry@17751} "responseMessage" -> "ok"
 
 Unsuccesful Response: 
 {LinkedHashMap@17665}  size = 3
 0 = {LinkedHashMap$Entry@17749} "responseCode" -> "401"  //might be 404, 500 and others
 1 = {LinkedHashMap$Entry@17750} "responseData" -> null
 2 = {LinkedHashMap$Entry@17751} "responseMessage" -> "particular failure message based on responseCode"

  
