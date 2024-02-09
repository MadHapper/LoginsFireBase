package com.example.loginfirebase

import android.content.Intent
import android.os.Bundle
import android.text.Html.ImageGetter
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.loginfirebase.ui.theme.LoginFirebaseTheme
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("1057404761307-muflolu29jjtk7hi97eoi6598c6301s0.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        callbackManager = CallbackManager.Factory.create()
        setContent {
            LoginFirebaseTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    LoginList(
                        onGoogleClick = { signInWithGoogle() },
                        onFacebookClick = { signInWithFacebook() }
                    )
                }
            }
        }
    }

    //facebook
    private fun signInWithFacebook() {
        LoginManager.getInstance().logInWithReadPermissions(this, listOf("email", "public_profile"))
        LoginManager.getInstance().registerCallback(callbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                firebaseAuthWithFacebook(result.accessToken)
            }

            override fun onCancel() {
                // Manejar cancelación
            }

            override fun onError(error: FacebookException) {
                // Manejar error

            }
        })
    }

    private fun firebaseAuthWithFacebook(accessToken: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(accessToken.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Inicio de sesión exitoso
                    Toast.makeText(this, "Facebook sign in successful", Toast.LENGTH_LONG).show()
                } else {
                    // Si falla el inicio de sesión
                    Toast.makeText(this, "Facebook sign in failed", Toast.LENGTH_LONG).show()
                }
            }
    }

    fun onActivityResultfacebook(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }


    //Google
    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(this, "Google sign in failed: ${e.message}", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Inicio de sesión exitoso
                    Toast.makeText(this, "Google sign in successful", Toast.LENGTH_LONG).show()
                } else {
                    // Si falla el inicio de sesión
                    Toast.makeText(this, "Google sign in failed", Toast.LENGTH_LONG).show()
                }
            }
    }

    companion object {
        private const val RC_SIGN_IN = 9001
    }
}

@Composable
fun LoginList(onGoogleClick: () -> Unit, onFacebookClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .padding(16.dp)
                .shadow(elevation = 4.dp, shape = RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp))
                .background(Color.LightGray)
                .padding(10.dp)
                .clickable { onGoogleClick() }
        ) {
            Row() {
                Image(
                    painterResource(id = R.drawable.google),
                    contentDescription = "",
                    modifier = Modifier.size(25.dp)
                )
                Spacer(modifier = Modifier.padding(5.dp))
                Text(text = "Login Google")
            }
        }

        Box(
            modifier = Modifier
                .padding(16.dp)
                .shadow(elevation = 4.dp, shape = RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp))
                .background(Color.LightGray)
                .padding(10.dp)
                .clickable { onFacebookClick() }

        ) {
            Row() {
                Image(
                    painterResource(id = R.drawable.facebook),
                    contentDescription = "",
                    modifier = Modifier.size(30.dp)
                )
                Spacer(modifier = Modifier.padding(5.dp))
                Text(text = "Login Facebook")
            }
        }

        Box(
            modifier = Modifier
                .padding(16.dp)
                .shadow(elevation = 4.dp, shape = RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp))
                .background(Color.LightGray)
                .padding(10.dp)
                .clickable { }
        ) {
            Row() {
                Image(
                    painterResource(id = R.drawable.twitter),
                    contentDescription = "",
                    modifier = Modifier.size(30.dp)
                )
                Spacer(modifier = Modifier.padding(5.dp))
                Text(text = "Login Twitter")
            }
        }

        Box(
            modifier = Modifier
                .padding(16.dp)
                .shadow(elevation = 4.dp, shape = RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp))
                .background(Color.LightGray)
                .padding(10.dp)
                .clickable { }
        ) {
            Row() {
                Image(
                    painterResource(id = R.drawable.correo),
                    contentDescription = "",
                    modifier = Modifier.size(30.dp)
                )
                Spacer(modifier = Modifier.padding(5.dp))
                Text(text = "Login Correo")
            }
        }
    }
}
