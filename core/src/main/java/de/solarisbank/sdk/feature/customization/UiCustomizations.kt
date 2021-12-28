package de.solarisbank.sdk.feature.customization

import android.content.res.ColorStateList
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.widget.*
import de.solarisbank.sdk.core.R
import de.solarisbank.sdk.core_ui.data.dto.Customization

sealed class ButtonStyle {
    object Primary : ButtonStyle()
    object Alternative : ButtonStyle()
    object SecondaryNoBackground: ButtonStyle()
}

sealed class ImageViewTint {
    object Primary: ImageViewTint()
    object Secondary: ImageViewTint()
}

fun Button.customize(customization: Customization, style: ButtonStyle = ButtonStyle.Primary) {
    if (!customization.enabled) {
        return
    }

    val backgroundColor: Int
    val textColorStateList: ColorStateList

    when(style) {
        is ButtonStyle.Alternative -> return
        is ButtonStyle.Primary -> {
            backgroundColor = customization.colors.themePrimary(context)
            textColorStateList = context.getColorStateList(R.color.button_textcolor_selector)
        }
        is ButtonStyle.SecondaryNoBackground -> {
            backgroundColor = context.getColor(R.color.ident_hub_color_surface)
            textColorStateList = ColorStateList.valueOf(customization.colors.themeSecondary(context))
        }
    }

    val cornerRadii = Array(8) { customization.dimens.buttonRadius }
    val shape = RoundRectShape(cornerRadii.toFloatArray(), null, null)
    val mask = ShapeDrawable(shape)


    mask.paint.color = backgroundColor

    val highlightColor = context.getColor(R.color.ident_hub_color_highlight)
    val drawable = RippleDrawable(ColorStateList.valueOf(highlightColor), mask, mask)
    background = drawable
    stateListAnimator = null

    setTextColor(textColorStateList)
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

fun ProgressBar.customize(customization: Customization) {
    if (!customization.enabled) {
        return
    }

    indeterminateTintList = ColorStateList.valueOf(customization.colors.themePrimary(context))
}