package com.example.firebaseautho

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class PhoneActivity : AppCompatActivity() {
    private lateinit var sendOTPBtn : Button
    private lateinit var phoneNumberET : EditText
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var number : String
    private lateinit var mProgressBar : ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_phone)
        init()
        sendOTPBtn.setOnClickListener {
            number = phoneNumberET.text.trim().toString()
            if (number.isNotEmpty()) {
                if(number.length==10){
                    mProgressBar.visibility=View.VISIBLE
                    number="+91$number"
                    val options= PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(number)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(callbacks)
                        .build()
                    PhoneAuthProvider.verifyPhoneNumber(options)
                }
                else{
                    Toast.makeText(this,"Please Enter correct Number",Toast.LENGTH_SHORT).show()

                }
            } else {
                Toast.makeText(this,"Please Enter correct Number",Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun init() {
        sendOTPBtn=findViewById(R.id.sendOTPBtn)
        mProgressBar= findViewById(R.id.phoneProgressBar)
        mProgressBar.visibility= View.INVISIBLE
        phoneNumberET=findViewById(R.id.phoneEditTextNumber)
        firebaseAuth= FirebaseAuth.getInstance()
    }
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(this,"verified Successfully!",Toast.LENGTH_SHORT)
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.d("TAG","signInWithPhoneAuthCredential: ${task.exception.toString()}")
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                }
            }
    }
    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.

            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                Log.d("TAG","onVerificationFailed: ${e.toString()}")
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                Log.d("TAG","onVerificationFailed: ${e.toString()}")

            } else if (e is FirebaseAuthMissingActivityForRecaptchaException) {
                // reCAPTCHA verification attempted with null Activity
                Log.d("TAG","onVerificationFailed: ${e.toString()}")

            }

            // Show a message and update the UI
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken,
        ) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.

            // Save verification ID and resending token so we can use them later
           val intent= Intent(this@PhoneActivity,OTPActivity::class.java)
            intent.putExtra("OTP",verificationId)
            intent.putExtra("resendToken",token)
            intent.putExtra("phoneNumber",number)
            startActivity(intent)
            mProgressBar.visibility=View.INVISIBLE

        }
    }
}