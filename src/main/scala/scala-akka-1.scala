/**
  * Hello world
  */
package bigknife {

  import akka.actor.{Actor, ActorRef, ActorSystem, PoisonPill, Props}
  import akka.actor.Actor.Receive

  object Main extends App {
    println("Akka 版本的HelloWorld")
    println("====================")

    val system = ActorSystem("bigknife")
    val textActor = system.actorOf(Props[TextActor], "text-actor")
    val consolePrinterActor = system.actorOf(Props[ConsolePrinterActor], "console-printer-actor")

    textActor ! TextMsgPayload(consolePrinterActor, TextMsg("Hello,world"))

    system.shutdown()
  }

  /**
    * Text Actor
    */
  class TextActor extends Actor {
    override def receive: Receive = {
      case TextMsgPayload(receive, textMsg) => {
        receive ! textMsg
      }
      case _ => println("unknown msg")
    }
  }


  class ConsolePrinterActor extends Actor {

    object rcv extends Receive {
      override def isDefinedAt(x: Any): Boolean = {
        x match {
          case TextMsg(_) => true
          case _ => false
        }
      }

      override def apply(v1: Any): Unit = {
        v1 match {
          case TextMsg(msg) => {
            println(msg)
          }
        }
      }
    }

    /*
    override def receive: Receive = {
      case TextMsg(msg) => {
        println(msg)
      }
      case _ => println("unknown msg")
    }
    */
    override def receive: Receive = rcv
  }

  case class TextMsg(msg: String)
  case class TextMsgPayload(receive: ActorRef, textMsg: TextMsg)
}
