package com.tinnovakovic.hiking.shared

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.extension.AfterTestExecutionCallback
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback
import org.junit.jupiter.api.extension.ExtensionContext


@ExperimentalCoroutinesApi
class CoroutineTestExtension(
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(TestCoroutineScheduler())
) : BeforeTestExecutionCallback, AfterTestExecutionCallback {

    val testDispatcherProvider = object : DispatcherProvider {
        override fun default() = testDispatcher
        override fun main() = testDispatcher
        override fun io() = testDispatcher
        override fun unconfined() = testDispatcher
    }

    override fun beforeTestExecution(context: ExtensionContext?) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun afterTestExecution(context: ExtensionContext?) {
        Dispatchers.resetMain()
    }
}