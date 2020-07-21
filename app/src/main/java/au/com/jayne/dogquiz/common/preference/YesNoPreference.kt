package au.com.jayne.dogquiz.common.preference

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.preference.DialogPreference
import androidx.preference.PreferenceViewHolder
import au.com.jayne.dogquiz.R

/**
 * The {@link YesNoPreference} is a preference to show a dialog with Yes and No
 * buttons.
 * <p>
 * This preference will store a boolean into the SharedPreferences.
 */
class YesNoPreference: DialogPreference {

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(
        context,
        attrs,
        defStyleAttr,
        0
    )

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, android.R.attr.yesNoPreferenceStyle)
    constructor(context: Context) : this(context, null)

    var onCompletionListener: OnCompletionListener? = null

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        val titleView = holder.findViewById(android.R.id.title) as TextView
        titleView.setTextColor(context.resources.getColor(android.R.color.black, null))
        holder.itemView.background = context.getDrawable(R.drawable.settings_ripple)
    }

    interface OnCompletionListener {
        fun onYes()
        fun onNo()
    }

}