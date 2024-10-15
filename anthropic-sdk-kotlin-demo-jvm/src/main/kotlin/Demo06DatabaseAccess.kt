package com.xemantic.anthropic.demo

import com.xemantic.anthropic.Anthropic
import com.xemantic.anthropic.message.Message
import com.xemantic.anthropic.message.ToolResult
import com.xemantic.anthropic.message.ToolUse
import com.xemantic.anthropic.tool.AnthropicTool
import com.xemantic.anthropic.tool.UsableTool
import kotlinx.coroutines.runBlocking
import java.sql.Connection
import java.sql.DriverManager

@AnthropicTool(
  name = "query_database",
  description = "Executes SQL on the database"
)
data class DatabaseQueryTool(val sql: String): UsableTool {

  internal lateinit var connection: Connection

  override suspend fun use(
    toolUseId: String
  ) = ToolResult(
    toolUseId,
    text = connection.prepareStatement(sql).use { statement ->
      statement.resultSet.use { resultSet ->
        resultSet.toString()
      }
    }
  )

}

fun main() = runBlocking {

  val client = Anthropic {
    tool<DatabaseQueryTool> {
      connection = DriverManager.getConnection("jdbc:...")
    }
  }

  val response = client.messages.create {
    +Message { +"Select all the data from USER table" }
    useTools()
  }

  val tool = response.content.filterIsInstance<ToolUse>().first()
  val toolResult = tool.use()
  println(toolResult)
}
