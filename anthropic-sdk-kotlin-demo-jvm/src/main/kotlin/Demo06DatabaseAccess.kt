package com.xemantic.anthropic.demo

import com.xemantic.anthropic.Anthropic
import com.xemantic.anthropic.content.ToolUse
import com.xemantic.anthropic.message.Message
import com.xemantic.anthropic.schema.Description
import com.xemantic.anthropic.tool.AnthropicTool
import com.xemantic.anthropic.tool.ToolInput
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Transient
import java.sql.Connection
import java.sql.DriverManager

@AnthropicTool("query_database")
@Description("Executes SQL on the database")
data class DatabaseQueryTool(val sql: String): ToolInput() {

  @Transient
  internal lateinit var connection: Connection

  init {
    use {
      connection.prepareStatement(sql).use { statement ->
        statement.resultSet.use { resultSet ->
          resultSet.toString()
        }
      }
    }
  }

}

fun main() = runBlocking {

  val client = Anthropic {
    tool<DatabaseQueryTool> {
      connection = DriverManager.getConnection("jdbc:...")
    }
  }

  val response = client.messages.create {
    +Message { +"Select all the data from USER table" }
    tool<DatabaseQueryTool>()
  }

  val tool = response.content.filterIsInstance<ToolUse>().first()
  val toolResult = tool.use()
  println(toolResult)
}
