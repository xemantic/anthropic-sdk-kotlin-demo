package com.xemantic.anthropic.demo

import com.xemantic.anthropic.Anthropic
import com.xemantic.anthropic.content.ToolUse
import com.xemantic.anthropic.message.Message
import com.xemantic.anthropic.message.plusAssign
import com.xemantic.anthropic.schema.Description
import com.xemantic.anthropic.tool.AnthropicTool
import com.xemantic.anthropic.tool.ToolInput
import kotlinx.coroutines.runBlocking

@AnthropicTool("get_weather")
@Description("Get the weather for a specific location")
data class WeatherTool(val location: String): ToolInput() {
  init {
    use {
      "The weather is 73f" // it should use some external service
    }
  }
}

fun main() = runBlocking {

  val client = Anthropic {
    tool<WeatherTool>()
  }

  val conversation = mutableListOf<Message>()
  conversation += Message { +"What is the weather in SF?" }

  val initialResponse = client.messages.create {
    messages = conversation
    singleTool<WeatherTool>()
  }
  println("Initial response:")
  println(initialResponse)

  conversation += initialResponse
  val tool = initialResponse.content.filterIsInstance<ToolUse>().first()
  val toolResult = tool.use()
  conversation += Message { +toolResult }

  val finalResponse = client.messages.create {
    messages = conversation
    allTools()
  }
  println("Final response:")
  println(finalResponse)
}
