package au.com.jayne.dogquiz.espresso

import androidx.test.espresso.matcher.ViewMatchers

class ViewDisplayedInstruction(val id: Int): Instruction() {

    override fun getDescription(): String? {
        return "View Displayed with ID $id"
    }

    override fun checkCondition(): Boolean {
        return EspressoTestHelper.isViewDisplayed(ViewMatchers.withId(id))
    }
}