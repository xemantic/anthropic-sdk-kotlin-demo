package com.xemantic.anthropic.demo

import com.xemantic.anthropic.Anthropic
import com.xemantic.anthropic.content.Text
import com.xemantic.anthropic.content.ToolUse
import com.xemantic.anthropic.message.Message
import com.xemantic.anthropic.message.plusAssign
import com.xemantic.anthropic.message.StopReason
import com.xemantic.anthropic.schema.Description
import com.xemantic.anthropic.tool.AnthropicTool
import com.xemantic.anthropic.tool.ToolInput
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable

@AnthropicTool("get_coordinates")
@Description("Accepts a place as an address, then returns the latitude and longitude coordinates.")
class GetCoordinates(
  @Description("The location to look up.")
  val location: String
) : ToolInput() {

  init {
    use {
      GetCoordinatesResponse(lat = 37.7749, long = -122.4194)
    }
  }

}

@Serializable
data class GetCoordinatesResponse(val lat: Double, val long: Double)

@AnthropicTool("get_temperature_unit")
class GetTemperatureUnit(
  @Description("The country")
  val country: String
) : ToolInput() {

  init {
    use {
      "farenheit"
    }
  }

}

@AnthropicTool("get_weather")
@Description("Get the weather at a specific location")
data class GetWeather(
  @Description("The latitude of the location to check weather.")
  val lat: Double,
  @Description("The longitude of the location to check weather.")
  val long: Double,
  @Description("Unit for the output")
  val unit: String
) : ToolInput() {

  init {
    use {
      GetWeatherResponse(
        unit = "farenheit",
        temperature = 122.0
      )
    }
  }

}

@Serializable
data class GetWeatherResponse(val unit: String, val temperature: Double)

fun main() = runBlocking {
  val anthropic = Anthropic {
    tool<GetCoordinates>()
    tool<GetTemperatureUnit>()
    tool<GetWeather>()
  }

  val content = "What is the weather in San Francisco, CA?"
  println("[user]: $content")
  val conversation = mutableListOf<Message>()
  conversation += Message { +content }

  do {
    val response = anthropic.messages.create {
      messages = conversation
      allTools()
    }

    conversation += response

    print("[assistant]: ")
    response.content.forEach {
      when (it) {
        is Text -> println(it.text)
        is ToolUse -> println("$it")
        else -> {}
      }
    }

    val toolResults = response.content
      .filterIsInstance<ToolUse>()
      .map { it.use() }

    conversation += Message {
      +toolResults
    }

  } while (response.stopReason == StopReason.TOOL_USE)

}
