package com.dicoding.picodiploma.storyapp.view

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import com.dicoding.picodiploma.storyapp.R
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

    internal fun validateInput() {
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

            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE -> {
                validateDescriptionStory(input)
            }
        }
    }

    private fun validateName(name: String) {
        if (name.isEmpty()) {
            setError(context.getString(R.string.empty_name_message), null)
        } else if (name.length < 3) {
            setError(context.getString(R.string.characters_name_message), null)
        } else {
            error = null
        }
    }

    private fun validateEmail(email: String) {
        if (email.isEmpty()) {
            setError(context.getString(R.string.empty_email_message), null)
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            setError(context.getString(R.string.email_format_message), null)
        } else {
            error = null
        }
    }

    private fun validatePassword(password: String) {
        if (password.isEmpty()) {
            setError(context.getString(R.string.empty_password_message), null)
        } else if (password.length < 8) {
            setError(context.getString(R.string.characters_password_message), null)
        } else {
            error = null
        }
    }

    private fun validateDescriptionStory(description: String) {
        if (description.isEmpty()) {
            setError(context.getString(R.string.empty_description_message), null)
        } else {
            error = null
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        textAlignment = TEXT_ALIGNMENT_VIEW_START
    }
}