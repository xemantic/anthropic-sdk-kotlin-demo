package com.xemantic.anthropic.demo

import com.xemantic.anthropic.Anthropic
import com.xemantic.anthropic.content.Text
import com.xemantic.anthropic.content.ToolUse
import com.xemantic.anthropic.message.plusAssign
import com.xemantic.anthropic.message.Message
import com.xemantic.anthropic.schema.Description
import com.xemantic.anthropic.tool.AnthropicTool
import com.xemantic.anthropic.tool.ToolInput
import kotlinx.coroutines.runBlocking

@AnthropicTool("Calculator")
@Description("Calculates the arithmetic outcome of an operation when given the arguments a and b")
data class Calculator(
  val operation: Operation,
  val a: Double,
  val b: Double
): ToolInput() {

  @Suppress("unused") // it is used, but by Anthropic, so we skip the warning
  enum class Operation(
    val calculate: (a: Double, b: Double) -> Double
  ) {
    ADD({ a, b -> a + b }),
    SUBTRACT({ a, b -> a - b }),
    MULTIPLY({ a, b -> a * b }),
    DIVIDE({ a, b -> a / b })
  }

  init {
    use {
      operation.calculate(a, b)
    }
  }

}

fun main() = runBlocking {

  val client = Anthropic {
    tool<Calculator>()
  }

  val conversation = mutableListOf<Message>()
  conversation += Message { +"What's 15 multiplied by 7?" }

  val response1 = client.messages.create {
    messages = conversation
    allTools()
  }
  conversation += response1

  println((response1.content[0] as Text).text)
  val toolUse = response1.content[1] as ToolUse
  val result = toolUse.use() // we are doing the calculation job for Claude here

  conversation += Message { +result }

  val response2 = client.messages.create {
    messages = conversation
    allTools()
  }
  println((response2.content[0] as Text).text)
}
