package com.app.blockaat.helper;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class GenericTextWatcher implements TextWatcher {

    private EditText editText;

    public GenericTextWatcher(EditText editText) {
        this.editText = editText;
    }

    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        String text = editText.getText().toString();
        if (text.isEmpty()) {
            editText.setHint(editText.getHint().toString().toLowerCase());
        } else {
            editText.setHint(editText.getHint().toString().toUpperCase());
        }
    }

    public void afterTextChanged(Editable editable) {

    }
}