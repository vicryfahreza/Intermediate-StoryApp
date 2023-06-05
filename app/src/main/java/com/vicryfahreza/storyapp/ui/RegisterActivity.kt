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
import com.vicryfahreza.storyapp.R
import com.vicryfahreza.storyapp.databinding.ActivityRegisterBinding
import com.vicryfahreza.storyapp.response.RegisterResponse
import com.vicryfahreza.storyapp.service.ApiConfig
import com.vicryfahreza.storyapp.ui.custom.EditTextEmail
import com.vicryfahreza.storyapp.ui.custom.EditTextPassword
import com.vicryfahreza.storyapp.ui.custom.EditTextUsername
import com.vicryfahreza.storyapp.ui.custom.RegisterButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var myEtUsername: EditTextUsername
    private lateinit var myEtEmail: EditTextEmail
    private lateinit var myEtPassword: EditTextPassword
    private lateinit var registerButton: RegisterButton
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        myEtUsername = binding.edUsername
        myEtEmail = binding.edEmail
        myEtPassword = binding.edPassword
        registerButton = binding.registerButton


        binding.tvAccount.setOnClickListener {
            val intentLogin = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intentLogin)
            finish()
        }

        registerButton.setOnClickListener {
            register()
        }

        myEtUsername.addTextChangedListener(object: TextWatcher {
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

        runRegisterAnimation()
        setupLocalization()
    }

    private fun setupLocalization(){
        binding.apply {
            registerLocalization.setOnClickListener {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }
        }
    }

    private fun runRegisterAnimation() {
        binding.apply {
            ObjectAnimator.ofFloat(ivIcon, View.TRANSLATION_X, -30f, 30f ).apply {
                duration = IMG_DURATION.toLong()
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.REVERSE
            }.start()
            val tvRegister = ObjectAnimator.ofFloat(tvRegister, View.ALPHA, 1f).setDuration(ALPHA_DURATION.toLong())
            val tvAccount = ObjectAnimator.ofFloat(tvAccount, View.ALPHA, 1f).setDuration(ALPHA_DURATION.toLong())
            val edUsername = ObjectAnimator.ofFloat(edUsername, View.ALPHA, 1f).setDuration(ALPHA_DURATION.toLong())
            val edEmail = ObjectAnimator.ofFloat(edEmail, View.ALPHA, 1f).setDuration(ALPHA_DURATION.toLong())
            val edPassword = ObjectAnimator.ofFloat(edPassword, View.ALPHA, 1f).setDuration(ALPHA_DURATION.toLong())
            val btnRegister = ObjectAnimator.ofFloat(registerButton, View.ALPHA, 1f).setDuration(ALPHA_DURATION.toLong())

            val together = AnimatorSet().apply {
                playTogether(btnRegister, tvAccount)
            }

            AnimatorSet().apply {
                playSequentially(tvRegister, edUsername, edEmail, edPassword, together)
                start()
            }
        }
    }

    private fun setMyButtonEnable() {
        val name = myEtUsername.text
        val email = myEtEmail.text
        val password = myEtPassword.text

        registerButton.isEnabled = (name != null && email != null && password != null)
                && name.toString().isNotEmpty()
                && email.toString().isNotEmpty()
                && password.toString().isNotEmpty()
                && password.length >= 8
    }

    private fun register() {
        showLoading(true)
        val client = ApiConfig.getApiService().registerAccount(
            myEtUsername.text.toString(),
            myEtEmail.text.toString(),
            myEtPassword.text.toString()
        )
        client.enqueue(object: Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                val responseBody = response.body()
                showLoading(false)
                if(responseBody != null && response.isSuccessful) {
                    if(responseBody.error){
                        Toast.makeText(this@RegisterActivity, responseBody.message, Toast.LENGTH_LONG)
                            .show()
                    } else {
                        loginAccount()
                        Toast.makeText(this@RegisterActivity, getString(R.string.register_success), Toast.LENGTH_LONG)
                            .show()
                    }
                } else {
                    Toast.makeText(this@RegisterActivity, response.message(), Toast.LENGTH_LONG)
                        .show()
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                showLoading(false)
                Toast.makeText(this@RegisterActivity, t.message, Toast.LENGTH_LONG)
                    .show()
            }

        })
    }

    private fun loginAccount() {
        val loginIntent = Intent(this, LoginActivity::class.java)
        startActivity(loginIntent)
        finish()
    }

    private fun showLoading(isLoading: Boolean){
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        const val ALPHA_DURATION = 500
        const val IMG_DURATION = 8000
    }

}