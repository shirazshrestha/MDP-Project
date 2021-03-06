package com.example.jobfindme.activity

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.room.Room
import com.example.jobfindme.R
import com.example.jobfindme.model.User
import com.example.jobfindme.room.UserDAO
import com.example.jobfindme.room.UserDB
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity() {

    // List of Users initialization
    var users = ArrayList<User>()

    // Users
    val admin = User("admin","admin","admin@mail.com","admin")
    var db= Room.databaseBuilder(applicationContext,UserDB::class.java,"UserDB").build()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        // Room DB creation

        val thread = Thread {
            users = db.getUserDao().getAllUsers() as ArrayList<User>
        }
        thread.start()
        // Populate the user list
        users.add(admin)

        var rge = false
        var rgp = false

        btnSingIn.setOnClickListener {
            if (!email.text.toString().isEmpty() && !password.text.toString().isEmpty()) {
                val eml = email.text.toString()
                val pss = password.text.toString()

                for (user in users) {
                    if (eml.equals(user.username) && pss.equals(user.password)) {
                        if (eml.equals(user.username)) {
                            rge = true
                        }
                        if (pss.equals(user.password)) {
                            rgp = true
                        }

                        if (rge==true && rgp ==true) {
                            // Sending the intent to MainActivity
                            var intent = Intent(this, MainActivity::class.java)
                            intent.putExtra("username", "Hello, " + email.text.toString())
                            startActivity(intent)
                            email.text.clear()
                            password.text.clear()
                            Toast.makeText(this, "Welcome, $eml", Toast.LENGTH_LONG).show()
                            rge = false
                            rgp = false
                        }
                    }
                }
                if (rge == false) {
                    //Toast.makeText(this, "Email is not registered!", android.widget.Toast.LENGTH_LONG).show()
                    email.error = "Registered Email is required."
                    rge = false

                }
                if (rgp == false) {
                    //Toast.makeText(this, "Password is not correct!", android.widget.Toast.LENGTH_LONG).show()
                    password.error = "Correct Password is required."
                    rgp = false
                }
            }
            if (email.text.toString().isEmpty()) {
                email.error = "Email is required."
                rge = false
            }

            if (password.text.toString().isEmpty()) {
                password.error = "Password is required."
                rge = false
            }

        }

        btnRegister.setOnClickListener {
//            startActivity(Intent(this, RegisterActivity::class.java))
//            email.text.clear()
//            password.text.clear()

            var intent = Intent(this, RegisterActivity::class.java)
            startActivityForResult(intent,1)
            email.text.clear()
            password.text.clear()
        }
    }

    var regist = false

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                val new_user = data!!.getSerializableExtra("new_user")
                for (user in users) {
                    if (regist == false){
                        if (!user.equals(new_user)) {
                            users.add(new_user as User)
                            regist = true
                            Toast.makeText(this, "Account created successfully. \nPlease, Sign In", Toast.LENGTH_LONG).show()

                            Thread {
                                // Create the database
                              //  var db = Room.databaseBuilder(application, UserDB::class.java, "userdb").build()

                                // Insert the user in the Room DB
                                var usr = new_user
                                db.getUserDao().insertUser(usr)

                            }.start()
                            db.close()
                            //InsertUserData(new_user,application).execute()
//                            var unm = GetUserData(new_user.username,application)
//                            print("**************************** " + unm)
                        }
                    } else {
                        Toast.makeText(this, "Register unsuccessful!!! \nEmail account already exists!", Toast.LENGTH_LONG).show()
                        //regist = false
                    }
                }
            }
        }
    }


    var reg = false

    fun forgotPassword(view : View) {
        val eml = email.text.toString()
        if (eml.isEmpty()) {
            Toast.makeText(
                this,
                "Forgot password? \nPlease, enter a registered email!",
                Toast.LENGTH_LONG
            ).show()
            email.error = "Please, enter an Email."
        } else {
            for (user in users) {
                if (reg == false) {
                    if (eml.equals(user.username)) {
                        val fpass = user.password
                        // Implicit intent
                        val intent = Intent()
                        intent.action = Intent.ACTION_SEND
                        intent.type = "text/plain"
                        intent.putExtra(Intent.EXTRA_TEXT, fpass)
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivity(intent)
                        }
                        reg = true
                    }
                } else {
                    email.error = "Email is not registered."
                    Toast.makeText(
                        this,
                        "The email provided is not registered!",
                        Toast.LENGTH_LONG
                    ).show()
                    //reg = false
                }
            }
        }
    }

    // Because the da cannot be run in the main thread
    // It is required to create a helper class
//    class InsertUserData(val user : User, val application: Application) : AsyncTask<Void,Void,Void>() {
//        override fun doInBackground(vararg p0: Void?): Void? {
//            UserDB.dbCreation(application).getUserDao().insertUser(user)
//            UserDB.dbCreation(application).getUserDao().getUser("mauro@mail.com").forEach() {
//                Log.d("Searching", "UserName : ${it.username}")
//            }
//            return null
//        }
//    }

//    fun GetUserData(val usernm : String, val application: Application) : AsyncTask<Void,Void,Void>() {
//        fun doInBackground(vararg p0: Void?): Void? {
//            var uname = UserDB.dbCreation(application).getUserDao().getUser(usernm).username
//            Log.d("Searching", "username : ${uname.username}")
//            return uname.username
//        }
//    }

}