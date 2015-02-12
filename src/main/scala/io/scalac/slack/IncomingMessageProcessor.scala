package io.scalac.slack

import akka.actor.{Actor, ActorLogging}
import io.scalac.slack.common._
import spray.json._

/**
 * Created on 08.02.15 23:36
 * Incomming message processor should parse incoming string
 * and change into proper protocol
 */
class IncomingMessageProcessor(implicit eventBus: MessageEventBus) extends Actor with ActorLogging {

  import io.scalac.slack.common.MessageJsonProtocol._

  override def receive: Receive = {

    case s: String =>
      try {
        val mType = s.parseJson.convertTo[MessageType]
        val incomingMessage: IncomingMessage = mType.messageType match {
          case "hello" => Hello
          case "pong" => Pong
          case _ =>
            UndefinedMessage(s)
        }
        eventBus.publish(incomingMessage)
      }
      catch {
        case e : JsonParser.ParsingException =>
        eventBus.publish(UndefinedMessage(s))
      }
    case ignored => //nothing special
  }
}