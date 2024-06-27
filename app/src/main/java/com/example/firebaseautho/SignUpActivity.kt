package com.example.firebaseautho

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.firebaseautho.databinding.ActivitySignUpBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSinginClient: GoogleSignInClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.textView.setOnClickListener{
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
        firebaseAuth =FirebaseAuth.getInstance()
        binding.button.setOnClickListener{
            val email= binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()
            val confrmPass = binding.confirmPassEt.text.toString()
            if(email.isNotEmpty() && pass.isNotEmpty() && confrmPass.isNotEmpty()) {
               if(pass == confrmPass){
                   firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener{
                       if(it.isSuccessful){
                           val intent = Intent(this, SignInActivity::class.java)
                           startActivity(intent)
                       }else{
                           Toast.makeText(this,"Error: ${it.exception?.message}", Toast.LENGTH_SHORT).show()
                       }
                   }
               }else{
                   Toast.makeText(this,"Password and Confirm Password should be same", Toast.LENGTH_SHORT).show()
               }
            }
            else{
                Toast.makeText(this,"Please fill all the fields", Toast.LENGTH_SHORT).show()
            }
        }
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSinginClient = com.google.android.gms.auth.api.signin.GoogleSignIn.getClient(this, gso)
//        binding.gSignInBtn.setOnClickListener {
//            val signInIntent = googleSinginClient.signInIntent
//            startActivityForResult(signInIntent, 100)
//        }
        findViewById<Button>(R.id.gSignUpBtn).setOnClickListener{
            SingInGoogle()
        }

    }
    private fun SingInGoogle() {
        val signInIntent = googleSinginClient.signInIntent
        launcher.launch(signInIntent)
    }
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result->

        if(result.resultCode== Activity.RESULT_OK){
            val Task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResult(Task)
        }

    }

    private fun handleResult(task: Task<GoogleSignInAccount>) {
        if(task.isSuccessful){
            val account : GoogleSignInAccount =  task.result
            firebaseAuthWithGoogle(account)
        }
        else{
            Toast.makeText(this,"Google Sign In Failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener{
            if(it.isSuccessful){
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }else{
                Toast.makeText(this,"Google Sign In Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}