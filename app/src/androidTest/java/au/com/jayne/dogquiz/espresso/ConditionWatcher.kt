package au.com.jayne.dogquiz.espresso

import java.lang.Exception

class ConditionWatcher private constructor() {


    private var timeoutLimit =
        DEFAULT_TIMEOUT_LIMIT
    private var watchInterval =
        DEFAULT_INTERVAL

    fun setWatchInterval(watchInterval: Int) {
        getInstance()
            .watchInterval = watchInterval
    }

    fun setTimeoutLimit(ms: Int) {
        getInstance()
            .timeoutLimit = ms
    }

    companion object {
        val CONDITION_NOT_MET = 0
        val CONDITION_MET = 1
        val TIMEOUT = 2

        val DEFAULT_TIMEOUT_LIMIT = 1000 * 60
        val DEFAULT_INTERVAL = 250

        private var conditionWatcher: ConditionWatcher? = null

        fun getInstance(): ConditionWatcher {
            if (conditionWatcher == null) {
                conditionWatcher =
                    ConditionWatcher()
            }
            return conditionWatcher!!
        }

        @Throws(Exception::class)
        fun waitForCondition(instruction: Instruction) {
            var status =
                CONDITION_NOT_MET
            var elapsedTime = 0
            do {
                if (instruction.checkCondition()) {
                    status =
                        CONDITION_MET
                } else {
                    elapsedTime += getInstance()
                        .watchInterval
                    Thread.sleep(getInstance().watchInterval.toLong())
                }
                if (elapsedTime >= getInstance().timeoutLimit) {
                    status =
                        TIMEOUT
                    break
                }
            } while (status != CONDITION_MET)
            if (status == TIMEOUT) throw Exception(instruction.getDescription().toString() + " - took more than " + getInstance().timeoutLimit / 1000 + " seconds. Test stopped.")
        }
    }
}