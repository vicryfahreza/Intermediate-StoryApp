package com.vicryfahreza.storyapp.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.vicryfahreza.storyapp.R
import com.vicryfahreza.storyapp.databinding.ActivityLoginBinding
import com.vicryfahreza.storyapp.model.LoginViewModel
import com.vicryfahreza.storyapp.model.UserPreference
import com.vicryfahreza.storyapp.model.ViewModelFactory
import com.vicryfahreza.storyapp.response.Login
import com.vicryfahreza.storyapp.response.LoginResponse
import com.vicryfahreza.storyapp.service.ApiConfig
import com.vicryfahreza.storyapp.ui.custom.EditTextEmail
import com.vicryfahreza.storyapp.ui.custom.EditTextPassword
import com.vicryfahreza.storyapp.ui.custom.LoginButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var loginButton: LoginButton
    private lateinit var myEtEmail: EditTextEmail
    private lateinit var myEtPassword: EditTextPassword
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loginButton = binding.loginButton
        myEtEmail = binding.edEmail
        myEtPassword = binding.edPassword

        val pref = UserPreference.getInstance(dataStore)

        loginViewModel = ViewModelProvider(
            this,
            ViewModelFactory(pref)
        )[LoginViewModel::class.java]

        myEtEmail.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(!s.isNullOrEmpty()){
                    setMyButtonEnable()
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        myEtPassword.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(!s.isNullOrEmpty()){
                    setMyButtonEnable()
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        loginButton.setOnClickListener{
            login()
        }

        binding.tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        runLoginAnimation()
        setupLocalization()
    }

    private fun setupLocalization(){
        binding.apply {
            loginLocalization.setOnClickListener {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }
        }
    }

    private fun runLoginAnimation(){
        binding.apply {
            ObjectAnimator.ofFloat(ivIcon, View.TRANSLATION_X, -30f, 30f ).apply {
                duration = 8000
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.REVERSE
            }.start()
            val tvLogin = ObjectAnimator.ofFloat(tvLogin, View.ALPHA, 1f).setDuration(ALPHA_DURATION.toLong())
            val tvToRegister = ObjectAnimator.ofFloat(tvRegister, View.ALPHA, 1f).setDuration(ALPHA_DURATION.toLong())
            val edEmail = ObjectAnimator.ofFloat(edEmail, View.ALPHA, 1f).setDuration(ALPHA_DURATION.toLong())
            val edPassword = ObjectAnimator.ofFloat(edPassword, View.ALPHA, 1f).setDuration(ALPHA_DURATION.toLong())
            val btnLogin = ObjectAnimator.ofFloat(loginButton, View.ALPHA, 1f).setDuration(ALPHA_DURATION.toLong())

            val together = AnimatorSet().apply {
                playTogether(btnLogin, tvToRegister)
            }

            AnimatorSet().apply {
                playSequentially(tvLogin, edEmail, edPassword, together)
                start()
            }
        }
    }

    private fun setMyButtonEnable() {
        val email = myEtEmail.text
        val password = myEtPassword.text

        loginButton.isEnabled = (email != null && password != null) && email.toString().isNotEmpty() && password.length >= 8
    }

    private fun login() {
        showLoading(true)
        val client = ApiConfig.getApiService()
            .loginWithToken(myEtEmail.text.toString(), myEtPassword.text.toString())
        client.enqueue(object : Callback<Login> {
            override fun onResponse(call: Call<Login>, response: Response<Login>) {
                val responseBody = response.body()
                showLoading(false)
                if(responseBody != null && response.isSuccessful) {
                    if(responseBody.error){
                        Toast.makeText(this@LoginActivity, responseBody.message, Toast.LENGTH_LONG)
                            .show()
                    } else {
                        saveUserToken(responseBody.loginResult)
                        Toast.makeText(this@LoginActivity, getString(R.string.login_welcome), Toast.LENGTH_LONG)
                            .show()
                    }
                } else {
                    Toast.makeText(this@LoginActivity, response.message(), Toast.LENGTH_LONG)
                        .show()
                }
            }

            override fun onFailure(call: Call<Login>, t: Throwable) {
                showLoading(false)
                Toast.makeText(this@LoginActivity, t.message, Toast.LENGTH_LONG)
                    .show()
            }
        })
    }

     private fun saveUserToken(login: LoginResponse){
        loginViewModel.saveUser(login.token)
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun showLoading(isLoading: Boolean){
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        const val ALPHA_DURATION = 500
    }

}