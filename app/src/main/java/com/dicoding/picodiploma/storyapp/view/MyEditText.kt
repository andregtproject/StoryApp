package com.dicoding.picodiploma.storyapp.view

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.TextWatcher
import android.text.InputType
import android.util.AttributeSet
import android.util.Patterns
import com.google.android.material.textfield.TextInputEditText

class MyEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : TextInputEditText(context, attrs) {

    init {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                validateInput()
            }

            override fun afterTextChanged(s: Editable) {
            }
        })

        setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                validateInput()
            }
        }
    }

    private fun validateInput() {
        val input = text.toString()
        when (inputType) {
            InputType.TYPE_TEXT_VARIATION_PERSON_NAME or InputType.TYPE_CLASS_TEXT -> {
                validateName(input)
            }
            InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS or InputType.TYPE_CLASS_TEXT -> {
                validateEmail(input)
            }
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD,
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD -> {
                validatePassword(input)
            }
        }
    }

    private fun validateName(name: String) {
        if (name.isEmpty()) {
            setError("Nama tidak boleh kosong", null)
        } else if (name.length < 3) {
            setError("Nama tidak boleh kurang dari 3 karakter", null)
        } else {
            error = null
        }
    }

    private fun validateEmail(email: String) {
        if (email.isEmpty()) {
            setError("Email tidak boleh kosong", null)
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            setError("Format email tidak valid", null)
        } else {
            error = null
        }
    }

    private fun validatePassword(password: String) {
        if (password.isEmpty()) {
            setError("Password tidak boleh kosong", null)
        } else if (password.length < 8) {
            setError("Password tidak boleh kurang dari 8 karakter", null)
        } else {
            error = null
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        textAlignment = TEXT_ALIGNMENT_VIEW_START
    }
}