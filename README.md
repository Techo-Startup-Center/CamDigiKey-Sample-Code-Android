# Sample Code for CamDigiKey Integration with Android


To integrate app-to-app authentication with CamDigiKey app, a client must have an API server which its endpoints communicate with CamDigiKey server using CamDigiKey client library. Then, client mobile application requests to client API server to get `loginToken`.

## 1. Login with CamDigiKey:  

- Get LoginToken from your own server: 
- Handle callback from Login with CamDigiKey:  

```kotlin
val resultLauncher = 
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result -> 
        if (result.resultCode == Activity.RESULT_OK) { 
            findViewById<TextView>(R.id.tvMessage).text = 
                "Approved: ${result.data.toString()}" 
            val authToken = result.data?.getStringExtra("authToken") 
            Log.d("authToken =", "$authToken") 
            val i = Intent(this, ClientInfoActivity::class.java) 
            val flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK 
            flags.let { i.flags = it } 
 
            i.putExtra("authToken", authToken) 
            startActivity(i) 
        } else { 
            findViewById<TextView>(R.id.tvMessage).text = 
                "Rejected: ${result.data.toString()}" 
        } 
    } 
```

## 2. Setup Login with CamDigiKey on your activity:  

```kotlin
findViewById<Button>(R.id.btSendRequest).setOnClickListener { 
    getLoginToken() 
    Log.d("LoginToken", "${loginToken}") 
    val intent = Intent(Intent.ACTION_ASSIST).apply { 
        data = Uri.parse("camdigikey://login?token=${loginToken}") 
        setPackage("kh.gov.camdx.camdigikey.debug") 
        putExtra(Intent.EXTRA_ASSIST_PACKAGE, BuildConfig.APPLICATION_ID ) 
    } 
 
    try { 
        resultLauncher.launch(intent) 
    } catch (e: Exception) { 
        Toast.makeText(this, "Please install camDigiKey app", Toast.LENGTH_SHORT).show() 
    } 
}	 
```

## Contact us

- Tel: +855 81 922 329
- Email: info@techostartup.center
- Address: RUPP's Compound Russian Federation Blvd., Toul Kork, Phnom Penh, Cambodia

