package au.com.jayne.dogquiz.espresso

import android.os.Bundle


abstract class Instruction {

    private var dataContainer = Bundle()

    fun setData(dataContainer: Bundle) {
        this.dataContainer = dataContainer
    }

    fun getDataContainer(): Bundle {
        return dataContainer
    }

    abstract fun getDescription(): String?

    abstract fun checkCondition(): Boolean

}