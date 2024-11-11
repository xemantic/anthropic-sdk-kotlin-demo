package com.xemantic.anthropic.demo

import com.xemantic.anthropic.Anthropic
import com.xemantic.anthropic.content.ToolUse
import com.xemantic.anthropic.message.Message
import com.xemantic.anthropic.schema.Description
import com.xemantic.anthropic.tool.AnthropicTool
import com.xemantic.anthropic.tool.ToolInput
import kotlinx.coroutines.runBlocking

/**
 * IT's a minimal example which can promote `anthropic-sdk-kotlin` library.
 */

// Yay, tail recursion, enjoy the beauty of Kotlin
tailrec fun fibonacci(
  n: Int, a: Int = 0, b: Int = 1
): Int = when (n) {
  0 -> a; 1 -> b; else -> fibonacci(n - 1, b, a + b)
}

@AnthropicTool("Fibonacci")
@Description("Calculates Fibonacci number n")
data class FibonacciTool(val n: Int) : ToolInput() {
  init { use { fibonacci(n) } }
}

fun main() = runBlocking {
  val anthropic = Anthropic { tool<FibonacciTool>() }
  val response = anthropic.messages.create {
    +Message { +"What's Fibonacci number 42" }
    singleTool<FibonacciTool>()
  }
  val toolUse = response.content[0] as ToolUse
  val toolResult = toolUse.use()
  println(toolResult)
}
