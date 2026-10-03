package com.example.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.ui.theme.MoneyFlowTheme

/**
 * Creates Material 3 TextFieldColors explicitly configured using the semantic
 * MoneyFlow theme tokens, guaranteeing optimal contrast in both Light and Dark modes.
 */
@Composable
fun moneyFlowTextFieldColors(
    containerColor: Color = MoneyFlowTheme.colors.inputBackground,
    focusedBorderColor: Color = MoneyFlowTheme.colors.inputFocusedBorder,
    unfocusedBorderColor: Color = MoneyFlowTheme.colors.inputBorder,
    textColor: Color = MoneyFlowTheme.colors.inputText,
    placeholderColor: Color = MoneyFlowTheme.colors.inputPlaceholder,
    cursorColor: Color = MoneyFlowTheme.colors.inputCursor
): TextFieldColors {
    val colors = MoneyFlowTheme.colors
    return OutlinedTextFieldDefaults.colors(
        // Text states
        focusedTextColor = textColor,
        unfocusedTextColor = textColor,
        disabledTextColor = colors.textDisabled,
        errorTextColor = colors.inputError,

        // Container states
        focusedContainerColor = containerColor,
        unfocusedContainerColor = containerColor,
        disabledContainerColor = containerColor.copy(alpha = 0.5f),
        errorContainerColor = containerColor,

        // Cursor & Text Selection
        cursorColor = cursorColor,
        errorCursorColor = colors.inputError,
        selectionColors = TextSelectionColors(
            handleColor = colors.inputSelectionHandle,
            backgroundColor = colors.inputSelectionBackground
        ),

        // Border / Indicator states
        focusedBorderColor = focusedBorderColor,
        unfocusedBorderColor = unfocusedBorderColor,
        disabledBorderColor = colors.inputBorder.copy(alpha = 0.3f),
        errorBorderColor = colors.inputError,

        // Label states
        focusedLabelColor = colors.inputFocusedLabel,
        unfocusedLabelColor = colors.inputUnfocusedLabel,
        disabledLabelColor = colors.textDisabled,
        errorLabelColor = colors.inputError,

        // Placeholder states
        focusedPlaceholderColor = placeholderColor,
        unfocusedPlaceholderColor = placeholderColor,
        disabledPlaceholderColor = colors.textDisabled,
        errorPlaceholderColor = placeholderColor,

        // Supporting / Error text states
        focusedSupportingTextColor = colors.inputSupportingText,
        unfocusedSupportingTextColor = colors.inputSupportingText,
        disabledSupportingTextColor = colors.textDisabled,
        errorSupportingTextColor = colors.inputError,

        // Leading and Trailing Icons
        focusedLeadingIconColor = colors.inputIcon,
        unfocusedLeadingIconColor = colors.inputIcon,
        disabledLeadingIconColor = colors.textDisabled,
        errorLeadingIconColor = colors.inputError,
        focusedTrailingIconColor = colors.inputIcon,
        unfocusedTrailingIconColor = colors.inputIcon,
        disabledTrailingIconColor = colors.textDisabled,
        errorTrailingIconColor = colors.inputError
    )
}

/**
 * Standard MoneyFlow Text Field component that automatically adheres to
 * the centralized MoneyFlow semantic theme across Light, Dark, and System modes.
 */
@Composable
fun MoneyFlowTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    shape: Shape = RoundedCornerShape(16.dp),
    containerColor: Color = MoneyFlowTheme.colors.inputBackground,
    focusedBorderColor: Color = MoneyFlowTheme.colors.inputFocusedBorder,
    unfocusedBorderColor: Color = MoneyFlowTheme.colors.inputBorder,
    textColor: Color = MoneyFlowTheme.colors.inputText
) {
    // Explicitly guarantee typed text color matches theme and respects error state
    val effectiveTextStyle = if (textStyle.color == Color.Unspecified) {
        textStyle.copy(color = if (isError) MoneyFlowTheme.colors.inputError else textColor)
    } else {
        textStyle
    }

    val fieldColors = moneyFlowTextFieldColors(
        containerColor = containerColor,
        focusedBorderColor = focusedBorderColor,
        unfocusedBorderColor = unfocusedBorderColor,
        textColor = textColor
    )

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = effectiveTextStyle,
        label = label,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        prefix = prefix,
        suffix = suffix,
        supportingText = supportingText,
        isError = isError,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        minLines = minLines,
        shape = shape,
        colors = fieldColors
    )
}
