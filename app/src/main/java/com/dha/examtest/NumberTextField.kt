package com.dha.examtest

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun NumberTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: @Composable () -> Unit
) {
    OutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = { newValue ->
            val filtered = newValue.filter { it.isDigit() || it == '.' || it == '-' || it == ',' }

            // Validation checks
            val hasMinus = filtered.startsWith('-')
            val minusCount = filtered.count { it == '-' }
            val decimalCount = filtered.count { it == '.' }

            val isMinusValid = (minusCount == 0) || (minusCount == 1 && hasMinus)
            val isDecimalValid = decimalCount <= 1
            val isInvalidPartial = filtered == "-" || filtered == "." || filtered == "-."

            if (isMinusValid && isDecimalValid && !isInvalidPartial) {
                onValueChange(filtered) // Update parent state
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number
        ),
        label = label
    )
}