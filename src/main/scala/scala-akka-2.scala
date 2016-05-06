package bigknife.akka2 {


  /**
    * Hello world
    */

  import akka.actor.{Actor, ActorRef, ActorSystem, PoisonPill, Props}
  import akka.actor.Actor.Receive
  import akka.util.Timeout

  import scala.concurrent.duration._
  import akka.pattern._
  import scala.concurrent._

  object Main1 extends App {
    println("Akka 版本的HelloWorld")
    println("====================")

    val system = ActorSystem("bigknife")
    val textActor = system.actorOf(Props[TextActor], "text-actor")
    implicit val timeout = Timeout(5 second)
    val f:Future[Any] = textActor ? s"Hello,world"

    Await.result(f, timeout.duration)

    system.shutdown()
  }

  /**
    * Text Actor
    */
  class TextActor extends Actor {
    override def receive: Receive = {
      case msg: String => {

        val consolePrinterActor = context.actorOf(Props[ConsolePrinterActor])
        consolePrinterActor ! TextMsg(msg)

      }
      case _ => println(" TextActor unknown msg")
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
            context.stop(self)
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

}