package jhb.kotlin.kp


interface LifeCycleAware {
    fun stage1()
}

abstract class LifeCycleTemplate : LifeCycleAware {

    /**
     * Implementations can access this state
     */
    protected var stateFromStage1: String? = null

    /**
     * A template pattern implementation of the lifecycle hook.
     */
    override fun stage1() {
        startStage1()
        doCustomStage1Stuff()
        endStage1()
    }

    protected abstract fun doCustomStage1Stuff()

    private fun startStage1() {
        stateFromStage1 = "starting-stage-1"
    }

    private fun endStage1() {
        stateFromStage1 = "ended-stage-1"
    }
}

    typealias InternalStep = (me: LifeCycleComposition) -> Unit

    class LifeCycleComposition(
        private val customStage1: InternalStep,
        private val startStage1: InternalStep = { me -> me.stateFromStage1 = "starting-stage-1" },
        private val endStage1: InternalStep = { me -> me.stateFromStage1 = "ended-stage-1" }
    ) : LifeCycleAware {

        /**
         * Implementations can access this state
         */
        internal var stateFromStage1: String? = null

        /**
         * A template pattern implementation of the lifecycle hook.
         */
        override fun stage1() {
            startStage1(this)
            customStage1(this)
            endStage1(this)
        }
    }