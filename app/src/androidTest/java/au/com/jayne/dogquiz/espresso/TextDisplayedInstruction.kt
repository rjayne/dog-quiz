package au.com.jayne.dogquiz.espresso

import androidx.test.espresso.matcher.ViewMatchers


class TextDisplayedInstruction private constructor(val textResId: Int? = null, val text: String? = null): Instruction() {

    constructor(text: String): this(null, text)

    constructor(textResId: Int): this(textResId, null)

    override fun getDescription(): String? {
        return "Text Displayed with '$text' or ID"
    }

    override fun checkCondition(): Boolean {
        textResId?.let{
            return EspressoTestHelper.isViewDisplayed(ViewMatchers.withText(it))
        }

        text?.let{
            return EspressoTestHelper.isViewDisplayed(ViewMatchers.withText(it))
        }

        return false
    }
}