/*
 * Copyright 2015 Heiko Seeberger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.heikoseeberger.helloakkahttp

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.http.Http
import akka.http.model.{HttpResponse, StatusCodes}
import akka.http.server.Directives
import akka.pattern.ask
import akka.stream.ActorFlowMaterializer
import akka.util.Timeout

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt
import scala.io.StdIn

object HelloAkka {

  import Directives._

  def main(args: Array[String]): Unit = {

    implicit val system = ActorSystem()
    implicit val mat = ActorFlowMaterializer()

    import system.dispatcher

    val echo = system.actorOf(Props(new EchoActor))

    Http().bindAndHandle(route(echo), "127.0.0.1", 8000)
//    Http().bindAndHandle(route(echo)(mat, system.dispatcher), "127.0.0.1", 8000)

    StdIn.readLine("Hit enter to quit")
    system.shutdown()
  }

  def route(echo: ActorRef)(implicit mat: ActorFlowMaterializer, ec: ExecutionContext) = {
    path("foo") {
      get {
        complete {
          HttpResponse(StatusCodes.BadRequest)
        }
      }
    } ~
    path("echo" / Segment ) { message =>
      get {
        complete {
          implicit val timeout: Timeout = 1.second
          (echo ? message).mapTo[String]
        }
      }
    }

  }
}

class EchoActor extends Actor {
  override def receive: Receive = {
    case message => sender() ! message
  }
}