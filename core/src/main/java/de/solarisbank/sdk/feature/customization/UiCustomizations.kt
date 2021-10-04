package de.solarisbank.sdk.feature.customization

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.widget.*

sealed class ButtonStyle {
    object Primary : ButtonStyle()
    object Alternative : ButtonStyle()
}

sealed class ImageViewTint {
    object Primary: ImageViewTint()
    object Secondary: ImageViewTint()
}

fun Button.customize(customization: Customization, style: ButtonStyle = ButtonStyle.Primary) {
    if (!customization.enabled) {
        return
    }

    if (style != ButtonStyle.Primary) {
        return
    }

    val cornerRadii = Array(8) { customization.dimens.buttonRadius }
    val shape = RoundRectShape(cornerRadii.toFloatArray(), null, null)
    val mask = ShapeDrawable(shape)


    mask.paint.color = customization.colors.themePrimary(context)

    val highlightColor = Color.parseColor("#33000000")
    val drawable = RippleDrawable(ColorStateList.valueOf(highlightColor), mask, mask)
    background = drawable
    stateListAnimator = null
}

fun RadioButton.customize(customization: Customization) {
    if (!customization.enabled) {
        return
    }

    buttonTintList = ColorStateList.valueOf(customization.colors.themeSecondary(context))
}

fun ImageView.customize(customization: Customization, tint: ImageViewTint = ImageViewTint.Secondary) {
    if (!customization.enabled) {
        return
    }

    val color = when(tint) {
        is ImageViewTint.Primary -> customization.colors.themePrimary(context)
        is ImageViewTint.Secondary -> customization.colors.themeSecondary(context)
    }
    imageTintList = ColorStateList.valueOf(color)
}

fun CheckBox.customize(customization: Customization) {
    if (!customization.enabled) {
        return
    }

    buttonTintList = ColorStateList.valueOf(customization.colors.themeSecondary(context))
}

fun TextView.customizeLinks(customization: Customization) {
    if (!customization.enabled) {
        return
    }

    setLinkTextColor(customization.colors.themeSecondary(context))
}