package com.xemantic.anthropic.demo

import com.xemantic.anthropic.Anthropic
import com.xemantic.anthropic.message.*
import com.xemantic.anthropic.tool.AnthropicTool
import com.xemantic.anthropic.tool.UsableTool
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable

/**
 * This demo is based on the
 * [article by Dan Nguyen](https://gist.github.com/dannguyen/faaa56cebf30ad51108a9fe4f8db36d8),
 * who showed how to extract financial disclosure reports as structured data by using OpenAI API.
 * I wanted to try out the same approach with Anthropic API, and it seems like a great test case
 * for anthropic-sdk-kotlin.
 */
@AnthropicTool(
  name = "DisclosureReport",
  description = "Extract the text from this image"
)
data class DisclosureReport(
  val assets: List<Asset>
) : UsableTool {
  override suspend fun use(toolUseId: String) = ToolResult(
    toolUseId, "Data provided to client"
  )
}

@Serializable
data class Asset(
  val assetName: String,
  val owner: String,
  val location: String?,
  val assetValueLow: Int?,
  val assetValueHigh: Int?,
  val incomeType: String,
  val incomeLow: Int?,
  val incomeHigh: Int?,
  val txGt1000: Boolean
)

fun main() = runBlocking {

  val client = Anthropic {
    tool<DisclosureReport>()
  }

  val response = client.messages.create {
    +Message {
      +"Decode structured output from supplied image"
      +Image(
        path = "data/financial-disclosure-report.png",
        mediaType = Image.MediaType.IMAGE_PNG
      )
    }
    useTool<DisclosureReport>()
  }

  val tool = response.content.filterIsInstance<ToolUse>().first()
  val report = tool.input<DisclosureReport>()
  println(report)

}
