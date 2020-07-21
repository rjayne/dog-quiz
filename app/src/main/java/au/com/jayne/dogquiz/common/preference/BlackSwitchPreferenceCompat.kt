package au.com.jayne.dogquiz.common.preference

import android.R
import android.content.Context
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.preference.PreferenceViewHolder
import androidx.preference.SwitchPreferenceCompat

class BlackSwitchPreferenceCompat: SwitchPreferenceCompat {
    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?) : super(context)

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        val titleView = holder.findViewById(R.id.title) as TextView
        titleView.setTextColor(context.resources.getColor(R.color.black, null))
        holder.itemView.background = context.getDrawable(au.com.jayne.dogquiz.R.drawable.settings_ripple)

        val switchView = holder.findViewById(androidx.preference.R.id.switchWidget) as SwitchCompat?
        switchView?.apply {
            trackDrawable.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY)
        }
    }
}