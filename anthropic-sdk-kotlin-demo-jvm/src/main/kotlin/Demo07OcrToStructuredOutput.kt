package com.xemantic.anthropic.demo

import com.xemantic.anthropic.Anthropic
import com.xemantic.anthropic.content.Image
import com.xemantic.anthropic.content.ToolUse
import com.xemantic.anthropic.message.*
import com.xemantic.anthropic.schema.Description
import com.xemantic.anthropic.tool.AnthropicTool
import com.xemantic.anthropic.tool.ToolInput
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable

/**
 * This demo is based on the
 * [article by Dan Nguyen](https://gist.github.com/dannguyen/faaa56cebf30ad51108a9fe4f8db36d8),
 * who showed how to extract financial disclosure reports as structured data by using OpenAI API.
 * I wanted to try out the same approach with Anthropic API, and it seems like a great test case
 * for anthropic-sdk-kotlin.
 */
@AnthropicTool("DisclosureReport")
@Description("Extract the text from this image")
data class DisclosureReport(
  val assets: List<Asset>
) : ToolInput()

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

  val anthropic = Anthropic {
    tool<DisclosureReport>()
  }

  val response = anthropic.messages.create {
    +Message {
      +Image("data/financial-disclosure-report.png")
    }
    singleTool<DisclosureReport>()
  }

  val tool = response.content.filterIsInstance<ToolUse>().first()
  val report = tool.input<DisclosureReport>()
  println(report)

}
