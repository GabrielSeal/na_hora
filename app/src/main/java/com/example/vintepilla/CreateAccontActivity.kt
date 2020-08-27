//Projeto: NaHora Vinte Pilla
//Autor: Gabriel Seal
//Data:25/08/2020
//Versão:1.0.1


package com.example.vintepilla

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_create_accont.*
import com.google.firebase.auth.FirebaseAuth.getInstance as getInstance1

class CreateAccontActivity : AppCompatActivity()


{
    private var etFirstName: EditText? = null
    private var etLastName: EditText?= null
    private var etEmail: EditText? = null
    private var etPassword: EditText? = null
    private var btnCreateAccount: Button? = null
    private var etFirs: EditText? = null
    private var mProgressBar: ProgressDialog? = null


    // referencias do banco de dados

    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null

    private var TAG = "CreateAccontActivity"

    // variaveis globais

    private var firstName: String? = null
    private var lastName: String? = null
    private var email: String? = null
    private var password: String? = null




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_accont)

        initialise()




    }
    private fun initialise() {
        etFirstName = findViewById(R.id.et_first_name) as EditText
        etLastName = findViewById(R.id.et_last_name) as EditText
        etEmail = findViewById(R.id.et_email) as EditText
        etPassword = findViewById(R.id.et_password) as EditText
        btnCreateAccount = findViewById(R.id.btn_register) as Button
        mProgressBar = ProgressDialog( this )

        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference!!.child("Users")
        mAuth = FirebaseAuth.getInstance()
        btnCreateAccount!!.setOnClickListener{ createNewAccount() }


    }
    private fun createNewAccount () {

        firstName = etFirstName?.text.toString()
        lastName = etLastName?.text.toString()
        email = etEmail?.text.toString()
        password = etPassword?.text.toString()

        if (!TextUtils.isEmpty(firstName) && !TextUtils.isEmpty(lastName) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password))

        {
            Toast.makeText(this, "Informações preenchidas corretamente", Toast.LENGTH_SHORT).show()
        }
        else
        {
            Toast.makeText(this, "Entre com mais detalhes", Toast.LENGTH_SHORT).show()
        }
        mProgressBar!!.setMessage("Registrando usuário")
        mProgressBar!!.show()
        mAuth!!
            .createUserWithEmailAndPassword(email!!, password!!).addOnCompleteListener(this) { task ->
                mProgressBar!!.hide()

                if (task.isSuccessful)
                {
                    Log.d(TAG, "CreateUserWhithEmail:Sucess")
                    val userId = mAuth!!.currentUser!!.uid

                    verifyEmail();

                    val currentUserDb = mDatabaseReference!!.child(userId)
                    currentUserDb.child("firstName").setValue(firstName)
                    currentUserDb.child("lastName").setValue(lastName)

                    updateUserInfoandUi ()

                } else {
                    Log.w(TAG, "CreateUserWithEmail:Failure", task.exception)
                    Toast.makeText(this@CreateAccontActivity, "A autenticação falhou", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun updateUserInfoandUi()
    {
        // iniciar nova atividade
        val intent = Intent(this@CreateAccontActivity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)

    }
    private fun verifyEmail()
    {
        val mUser = mAuth!!.currentUser
        mUser!!.sendEmailVerification().addOnCompleteListener(this) {
            task->

            if (task.isSuccessful)
            {
                Toast.makeText(this@CreateAccontActivity,"Verification email sent to" + mUser.getEmail(), Toast.LENGTH_SHORT).show()

            } else {
                Log.e(TAG, "SendEmailVerification", task.exception)
                Toast.makeText(this@CreateAccontActivity, "Failed to send Verification email.", Toast.LENGTH_SHORT).show()

            }
        }
    }
}